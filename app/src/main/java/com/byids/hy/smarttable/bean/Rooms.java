package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Rooms implements Serializable {
    private RoomAttr roomAttr;


    private String roomName;


    private String roomDBName;


    public RoomAttr getRoomAttr() {
        return roomAttr;
    }

    public void setRoomAttr(RoomAttr roomAttr) {
        this.roomAttr = roomAttr;
    }

    public void setRoomName(String roomName){

        this.roomName = roomName;

    }

    public String getRoomName(){

        return this.roomName;

    }

    public void setRoomDBName(String roomDBName){

        this.roomDBName = roomDBName;

    }

    public String getRoomDBName(){

        return this.roomDBName;

    }
}
