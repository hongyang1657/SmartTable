package com.byids.hy.smarttable.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class RoomAttr implements Serializable{

    private Panel panel;

    private Aircondition aircondition;


    private Ibeacon ibeacon;


    private Lightbelt lightbelt;


    private Light light;


    private Camera_indoor camera_indoor;


    private Sence sence;


    private Curtain curtain;


    public void setPanel(Panel panel){

        this.panel = panel;

    }

    public Panel getPanel(){

        return this.panel;

    }

    public void setAircondition(Aircondition aircondition){

        this.aircondition = aircondition;

    }

    public Aircondition getAircondition(){

        return this.aircondition;

    }

    public void setIbeacon(Ibeacon ibeacon){

        this.ibeacon = ibeacon;

    }

    public Ibeacon getIbeacon(){

        return this.ibeacon;

    }

    public void setLightbelt(Lightbelt lightbelt){

        this.lightbelt = lightbelt;

    }

    public Lightbelt getLightbelt(){

        return this.lightbelt;

    }

    public void setLight(Light light){

        this.light = light;

    }

    public Light getLight(){

        return this.light;

    }

    public void setCamera_indoor(Camera_indoor camera_indoor){

        this.camera_indoor = camera_indoor;

    }

    public Camera_indoor getCamera_indoor(){

        return this.camera_indoor;

    }

    public void setSence(Sence sence){

        this.sence = sence;

    }

    public Sence getSence(){

        return this.sence;

    }

    public void setCurtain(Curtain curtain){

        this.curtain = curtain;

    }

    public Curtain getCurtain(){

        return this.curtain;

    }

}
