package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Alarmclock implements Serializable {

    private int active;


    public void setActive(int active){

        this.active = active;

    }

    public int getActive(){

        return this.active;

    }

}
