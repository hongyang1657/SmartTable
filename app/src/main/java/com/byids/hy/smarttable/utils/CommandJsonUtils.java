package com.byids.hy.smarttable.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by byids on 16/6/14.
 */
public class CommandJsonUtils {
    public static String  getCommandJson(int CommandType ,JSONObject commandData,String hid, String loginName,String password,String CommandTime){
        String resultJson="";
        JSONObject commandJson=new JSONObject();
        JSONObject commanduser=new JSONObject();

//        JSONObject commandTime=new JSONObject();
//        JSONObject commandType=new JSONObject();


        try {
            //commandJson
            commandJson.put("CommandType",CommandType);
            commandJson.put("CommandData",commandData);
            commandJson.put("CommandUser",commanduser);
            commandJson.put("CommandTime",CommandTime);

            //commandData


            //commandUser
            commanduser.put("hid",hid);
            commanduser.put("loginName",loginName);
            commanduser.put("password",password);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("getCommandJson", commandJson.toString());
        return commandJson.toString();
    }

}
