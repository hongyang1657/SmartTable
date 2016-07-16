package com.byids.hy.smarttable.bean;

/**
 * Created by hy on 2016/6/24.
 */
public class SwitchItem {
    private int iconImageId;
    private String itemName;

    public SwitchItem(int iconImageId) {
        this.iconImageId = iconImageId;
    }

    public SwitchItem(String itemName) {
        this.itemName = itemName;
    }

    public SwitchItem(String itemName,int iconImageId) {
        this.iconImageId = iconImageId;
        this.itemName = itemName;
    }

    public int getIconImageId() {
        return iconImageId;
    }

    public void setIconImageId(int iconImageId) {
        this.iconImageId = iconImageId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
