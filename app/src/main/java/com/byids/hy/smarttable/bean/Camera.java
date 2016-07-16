package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Camera implements Serializable {

    private String pub_domain;


    private int active;


    private String pri_ip;


    private String pwd;


    private String uname;


    public void setPub_domain(String pub_domain){

        this.pub_domain = pub_domain;

    }

    public String getPub_domain(){

        return this.pub_domain;

    }

    public void setActive(int active){

        this.active = active;

    }

    public int getActive(){

        return this.active;

    }

    public void setPri_ip(String pri_ip){

        this.pri_ip = pri_ip;

    }

    public String getPri_ip(){

        return this.pri_ip;

    }

    public void setPwd(String pwd){

        this.pwd = pwd;

    }

    public String getPwd(){

        return this.pwd;

    }

    public void setUname(String uname){

        this.uname = uname;

    }

    public String getUname(){

        return this.uname;

    }

}
