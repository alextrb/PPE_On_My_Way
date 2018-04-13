package com.onmyway.ppe.ppe_onmyway;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy_pc on 21/03/2018.
 */

public class Way {

    private int id;
    private String nameway;
    private int noteway;
    private int iduser;

    private List<LatLng> listCoord;

    private List<LatLng> listCheck;

    public Way(int id, String nameway, int noteway, int iduser) {
        this.id = id;
        this.nameway = nameway;
        this.noteway = noteway;
        this.iduser = iduser;
        listCoord = new ArrayList<>();
        listCheck = new ArrayList<>();
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

    public List<LatLng> getListCoord() {
        return listCoord;
    }

    public void setListCoord(List<LatLng> listCoord) {
        this.listCoord = listCoord;
    }

    public void addToList(LatLng e){
        listCoord.add(e);
    }

    public void addToListCheck(LatLng e){
        listCheck.add(e);
    }

    public List<LatLng> getListCheck() {
        return listCheck;
    }

    public void setListCheck(List<LatLng> listCheck) {
        this.listCheck = listCheck;
    }
}
