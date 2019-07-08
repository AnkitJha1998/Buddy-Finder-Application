package com.example.ankitjha.buddyfinder;

import java.util.ArrayList;

public class Groups {

    int gid;
    String gname;
    ArrayList<Integer> friendList;
    int admin_user;
    Double destLat,destLong;
    ArrayList<Double> pathLats;
    ArrayList<Double> pathLongs;
    /*String dateCreated;
    String timeCreated;
*/
    public int getGid() {
        return gid;
    }

    public String getGname() {
        return gname;
    }

    public ArrayList<Integer> getFriendList() {
        return friendList;
    }

    public int getAdmin_user() {
        return admin_user;
    }

    public Double getDestLat() {
        return destLat;
    }

    public Double getDestLong() {
        return destLong;
    }

    /*public String getDateCreated() {
        return dateCreated;
    }

    public String getTimeCreated() {
        return timeCreated;
    }*/

    public ArrayList<Double> getPathLats() {
        return pathLats;
    }

    public ArrayList<Double> getPathLongs() {
        return pathLongs;
    }
}
