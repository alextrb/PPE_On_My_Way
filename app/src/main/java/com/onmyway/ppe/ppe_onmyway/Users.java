package com.onmyway.ppe.ppe_onmyway;

/**
 * Created by jeremy_pc on 21/03/2018.
 */

public class Users {

    private int id;
    private String username;
    private String mail;

    public Users(int id, String username, String mail) {
        this.id = id;
        this.username = username;
        this.mail = mail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
