package com.byids.hy.smarttable.bean;

/**
 * Created by hy on 2016/7/9.
 */
public class TableModel {
    private String modelText;
    private int modelImg;
    private String room;
    private int saveRoom;

    public TableModel(String modelText, int modelImg) {
        this.modelText = modelText;
        this.modelImg = modelImg;
    }

    public TableModel(String modelText, int modelImg, String room) {
        this.modelText = modelText;
        this.modelImg = modelImg;
        this.room = room;
    }

    public TableModel(String modelText, int modelImg, String room,int saveRoom) {
        this.modelText = modelText;
        this.modelImg = modelImg;
        this.room = room;
        this.saveRoom = saveRoom;
    }

    public int getSaveRoom() {
        return saveRoom;
    }

    public void setSaveRoom(int saveRoom) {
        this.saveRoom = saveRoom;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getModelText() {
        return modelText;
    }

    public void setModelText(String modelText) {
        this.modelText = modelText;
    }

    public int getModelImg() {
        return modelImg;
    }

    public void setModelImg(int modelImg) {
        this.modelImg = modelImg;
    }
}
