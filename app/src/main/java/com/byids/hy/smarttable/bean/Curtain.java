package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Curtain implements Serializable {

    private int active;


    private String right;


    private String protocol;


    private String left;


    public void setActive(int active){

        this.active = active;

    }

    public int getActive(){

        return this.active;

    }

    public void setRight(String right){

        this.right = right;

    }

    public String getRight(){

        return this.right;

    }

    public void setProtocol(String protocol){

        this.protocol = protocol;

    }

    public String getProtocol(){

        return this.protocol;

    }

    public void setLeft(String left){

        this.left = left;

    }

    public String getLeft(){

        return this.left;

    }
}
