package com.karithrastarson.monitor.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.karithrastarson.monitor.entity.AmazingNode;
import com.karithrastarson.monitor.exception.NodeNotFoundException;
import com.karithrastarson.monitor.repository.NodeRepository;
import com.karithrastarson.monitor.repository.RootNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class TreeService {

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    RootNodeRepository rootNodeRepository;

    LoadingCache<Long, AmazingNode> treeCache;

    AmazingNode root;
    private long rootId;

    /**
     * Method to establish the tree cache
     */
    @PostConstruct
    private void initCache() {
        treeCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(new CacheLoader<Long, AmazingNode>() {
                           @Override
                           public AmazingNode load(Long id) throws Exception {
                               AmazingNode node = getNodeByFromRepository(id);
                               if (node == null) {
                                   throw new NodeNotFoundException(id);
                               }
                               return node;
                           }
                       }
                );
    }

    /**
     * Method to create or fetch the root element of the tree
     * A default root id is provided in application.properties
     */
    @PostConstruct
    private void establishRoot() {
        Iterable<AmazingNode> all = rootNodeRepository.findAll();
        if (!all.iterator().hasNext()) {
            root = new AmazingNode(null);
            rootId = root.getId();
            rootNodeRepository.save(root);
            updateCache(root);
        } else {
            root = all.iterator().next();
            rootId = root.getId();
            updateCache(root);
        }
    }

    /**
     * Method to fetch the root and all of its children
     * @return the root node
     */
    public AmazingNode getTree() {
        try {
            return getNodeById(rootId);
        } catch (NodeNotFoundException e) {
            //Should not happen
        }
        return null;
    }

    /**
     * Method to create a new node in the tree
     *
     * @param parentId The id of the parent node
     * @return The newly created node
     * @throws NodeNotFoundException when the parent node is not found
     */
    public AmazingNode createNode(long parentId) throws NodeNotFoundException {
        try {
            AmazingNode parent = treeCache.get(parentId);
            if (parent == null) {
                throw new NodeNotFoundException(parentId);
            }
            AmazingNode newNode = new AmazingNode(parentId);
            nodeRepository.save(newNode);
            updateCache(newNode);
            return newNode;
        } catch (ExecutionException e) {
            throw new NodeNotFoundException(parentId);
        }
    }

    /**
     * A method to move a node to a new parent
     *
     * @param nodeId   The id of the node to be moved
     * @param newParentId The id of the new parent
     * @return the node after being moved
     * @throws NodeNotFoundException when parent or node are not found
     */
    public AmazingNode moveNode(Long nodeId, Long newParentId) throws NodeNotFoundException {
        AmazingNode node = getNodeById(nodeId);
        AmazingNode parentNode = getNodeById(newParentId);
        if (node == null) {
            throw new NodeNotFoundException(nodeId);
        }
        if (parentNode == null) {
            throw new NodeNotFoundException(newParentId);
        }
        node.setParentId(newParentId);
        nodeRepository.save(node);
        updateCache(node);
        return node;
    }

    /**
     * A method to fetch a node by ID. First we look in the cache,
     * but then we fetch it from repository
     *
     * @param id The id of the node to fetch
     * @return The node, if found
     * @throws NodeNotFoundException when no node is found with that id
     */
    public AmazingNode getNodeById(Long id) throws NodeNotFoundException {
        AmazingNode node;
        try {
            node = treeCache.get(id);
            if (node == null) {
                throw new NodeNotFoundException(id);
            }
        } catch (ExecutionException e) {
            throw new NodeNotFoundException(id);
        }
        return node;
    }

    /**
     * Fetch a node from the repository
     *
     * @param id The ID of the node
     * @return The node if present. Otherwise null.
     */
    private AmazingNode getNodeByFromRepository(Long id) {
        Optional<AmazingNode> nodeOptional = nodeRepository.findById(id);
        return nodeOptional.orElse(null);
    }

    /**
     * Update the tree cache information about a given node
     *
     * @param newNode The node to be updated in the cache
     */
    private void updateCache(AmazingNode newNode) {
        AmazingNode cached = null;
        try {
            cached = treeCache.get(newNode.getId());
        } catch (ExecutionException e) {
            //Nothing to do
        }
        if (cached != null) {
            treeCache.invalidate(newNode.getId());
        }
        treeCache.put(newNode.getId(), newNode);
    }
}
