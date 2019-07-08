package com.example.ankitjha.buddyfinder;

import java.util.ArrayList;

public class Users {

    int uid;
    String uname;
    String username;
    String password;
    Double lat;
    Double lng;
    ArrayList<Integer> groupList;
    int locSharePer;

    public Users() {

    }

    public Users(int uid, String uname, String username, String password, Double lat, Double lng, ArrayList<Integer> groupList, int locSharePer) {
        this.uid = uid;
        this.uname = uname;
        this.username = username;
        this.password = password;
        this.lat = lat;
        this.lng = lng;
        this.groupList = groupList;
        this.locSharePer = locSharePer;
    }

    public int getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public ArrayList<Integer> getGroupList() {
        return groupList;
    }

    public int getLocSharePer() {
        return locSharePer;
    }
}
