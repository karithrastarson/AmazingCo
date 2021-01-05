package com.karithrastarson.monitor.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AmazingNode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Long parentId;

    public AmazingNode() {
        //Default constructor created for Spring
    }

    public AmazingNode(Long parentId) {
        this.parentId = parentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
