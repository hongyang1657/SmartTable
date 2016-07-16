package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Camera_indoor implements Serializable {

    private int active;


    private String channel_id;


    public void setActive(int active){

        this.active = active;

    }

    public int getActive(){

        return this.active;

    }

    public void setChannel_id(String channel_id){

        this.channel_id = channel_id;

    }

    public String getChannel_id(){

        return this.channel_id;

    }

}
