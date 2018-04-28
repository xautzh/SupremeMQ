package com.xaut.server;

import java.io.Serializable;

public class StudentTest implements Serializable {
    private String name = "lily";
    private String sex = "男";
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StudentTest{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", id=" + id +
                '}';
    }
}
