package com.onmyway.ppe.ppe_onmyway;

/**
 * Created by jeremy_pc on 21/03/2018.
 */

public class Way {

    private int id;
    private String nameway;
    private int noteway;
    private int iduser;

    public Way(int id, String nameway, int noteway, int iduser) {
        this.id = id;
        this.nameway = nameway;
        this.noteway = noteway;
        this.iduser = iduser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameway() {
        return nameway;
    }

    public void setNameway(String nameway) {
        this.nameway = nameway;
    }

    public int getNoteway() {
        return noteway;
    }

    public void setNoteway(int noteway) {
        this.noteway = noteway;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }
}
