package com.liangyt.entity;

import java.io.Serializable;

/**
 * 描述：
 * 创建时间 2017-12-13 13:05
 * 作者 tony
 */
public class User implements Serializable {
    private Long id;
    private String name;

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                '}';
    }
}
