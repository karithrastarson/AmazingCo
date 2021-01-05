package com.karithrastarson.monitor.repository;

import com.karithrastarson.monitor.entity.AmazingNode;
import org.springframework.data.repository.CrudRepository;

public interface RootNodeRepository extends CrudRepository<AmazingNode, Long> {
}
