package com.karithrastarson.monitor.controller;

import com.karithrastarson.monitor.entity.AmazingNode;
import com.karithrastarson.monitor.exception.NodeNotFoundException;
import com.karithrastarson.monitor.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/tree")
public class TreeController {

    @Autowired
    TreeService treeService;

    /**
     * Endpoint to fetch the entire tree
     *
     * @return The root of the tree and all of its children
     */
    @GetMapping(path = "")
    public @ResponseBody
    ResponseEntity<AmazingNode> getTree() {
        AmazingNode node = treeService.getTree();
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    /**
     * Endpoint to fetch a specific node and of all its children
     *
     * @param nodeId The ID of the node to be returned
     * @return The node and all of its children with properties
     */
    @GetMapping(path = "/{id}")
    public @ResponseBody
    ResponseEntity<AmazingNode> getNode(@PathVariable(value = "id") Long nodeId) {
        try {
            AmazingNode node = treeService.getNodeById(nodeId);
            return new ResponseEntity<>(node, HttpStatus.OK);

        } catch (NodeNotFoundException e) {
            return new ResponseEntity(errorResponse(e.getNodeId()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to update a node in the tree with a new parent ID
     *
     * @param nodeId  The ID of the node to be changed
     * @param newNode The node with the changed property
     * @return The node after the update
     */
    @PutMapping(path = "/{id}/:move")
    public @ResponseBody
    ResponseEntity<AmazingNode> moveNode(
            @PathVariable(value = "id") Long nodeId,
            @RequestBody AmazingNode newNode) {
        try {
            AmazingNode movedNode = treeService.moveNode(nodeId, newNode.getParentId());
            return new ResponseEntity<>(movedNode, HttpStatus.OK);

        } catch (NodeNotFoundException e) {
            return new ResponseEntity(errorResponse(e.getNodeId()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to add a random node to the tree
     * <p>
     * The entire tree is then returned
     *
     * @return The tree after the addition
     */
    @PostMapping(path = "/{parentId}")
    public @ResponseBody
    ResponseEntity<AmazingNode> createNode(@PathVariable(value = "parentId") Long parentId) {
        try {
            AmazingNode newNode = treeService.createNode(parentId);
            return new ResponseEntity<>(newNode, HttpStatus.CREATED);
        } catch (NodeNotFoundException e) {
            return new ResponseEntity(errorResponse(parentId), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Helper function to create the response for node not found
     *
     * @param nodeId
     * @return
     */
    private String errorResponse(long nodeId) {
        return "Node with id \"" + nodeId + "\" not found";
    }
}
