package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Ibeacon implements Serializable {

    private int active;


    private String minor;


    private String major;


    private String uuid;


    public void setActive(int active){

        this.active = active;

    }

    public int getActive(){

        return this.active;

    }

    public void setMinor(String minor){

        this.minor = minor;

    }

    public String getMinor(){

        return this.minor;

    }

    public void setMajor(String major){

        this.major = major;

    }

    public String getMajor(){

        return this.major;

    }

    public void setUuid(String uuid){

        this.uuid = uuid;

    }

    public String getUuid(){

        return this.uuid;

    }
}
