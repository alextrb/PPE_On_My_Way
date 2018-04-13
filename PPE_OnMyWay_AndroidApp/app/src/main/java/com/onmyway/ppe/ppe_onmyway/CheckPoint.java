package com.onmyway.ppe.ppe_onmyway;

/**
 * Created by jeremy_pc on 19/03/2018.
 */

public class CheckPoint {

    private String name;
    private String description;
    private int id;

    public CheckPoint(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
