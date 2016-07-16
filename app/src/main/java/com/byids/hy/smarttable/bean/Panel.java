package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Panel implements Serializable{

    private String panel2;


    private int active;


    private String panel3;


    private String protocol;


    private String panel1;


    private String panel4;


    public void setPanel2(String panel2){

        this.panel2 = panel2;

    }

    public String getPanel2(){

        return this.panel2;

    }

    public void setActive(int active){

        this.active = active;

    }

    public int getActive(){

        return this.active;

    }

    public void setPanel3(String panel3){

        this.panel3 = panel3;

    }

    public String getPanel3(){

        return this.panel3;

    }

    public void setProtocol(String protocol){

        this.protocol = protocol;

    }

    public String getProtocol(){

        return this.protocol;

    }

    public void setPanel1(String panel1){

        this.panel1 = panel1;

    }

    public String getPanel1(){

        return this.panel1;

    }

    public void setPanel4(String panel4){

        this.panel4 = panel4;

    }

    public String getPanel4(){

        return this.panel4;

    }
}
