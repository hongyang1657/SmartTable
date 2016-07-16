package com.byids.hy.smarttable.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hy on 2016/6/24.
 */
public class HomeAttr implements Serializable{

    private static final long serializableUID = 154896645654545455L;

    private EzvizCamera ezvizCamera;


    private Music music;


    private Hiddendoor hiddendoor;


    private Outdoorwaterflow outdoorwaterflow;


    private Camera camera;


    private Lock lock;


    private Alarmclock alarmclock;


    private Cinemaroom cinemaroom;


    private Securityalarm securityalarm;


    private List<Rooms> rooms ;


    public EzvizCamera getEzvizCamera() {
        return ezvizCamera;
    }

    public void setEzvizCamera(EzvizCamera ezvizCamera) {
        this.ezvizCamera = ezvizCamera;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public List<Rooms> getRooms() {
        return rooms;
    }

    public void setRooms(List<Rooms> rooms) {
        this.rooms = rooms;
    }

    public Hiddendoor getHiddendoor() {
        return hiddendoor;
    }

    public void setHiddendoor(Hiddendoor hiddendoor) {
        this.hiddendoor = hiddendoor;
    }

    public Outdoorwaterflow getOutdoorwaterflow() {
        return outdoorwaterflow;
    }

    public void setOutdoorwaterflow(Outdoorwaterflow outdoorwaterflow) {
        this.outdoorwaterflow = outdoorwaterflow;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Alarmclock getAlarmclock() {
        return alarmclock;
    }

    public void setAlarmclock(Alarmclock alarmclock) {
        this.alarmclock = alarmclock;
    }

    public Cinemaroom getCinemaroom() {
        return cinemaroom;
    }

    public void setCinemaroom(Cinemaroom cinemaroom) {
        this.cinemaroom = cinemaroom;
    }

    public Securityalarm getSecurityalarm() {
        return securityalarm;
    }

    public void setSecurityalarm(Securityalarm securityalarm) {
        this.securityalarm = securityalarm;
    }
}
