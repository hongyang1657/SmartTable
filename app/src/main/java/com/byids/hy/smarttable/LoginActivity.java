package com.byids.hy.smarttable;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.byids.hy.smarttable.bean.Aircondition;
import com.byids.hy.smarttable.bean.Alarmclock;
import com.byids.hy.smarttable.bean.Camera;
import com.byids.hy.smarttable.bean.Camera_indoor;
import com.byids.hy.smarttable.bean.Cinemaroom;
import com.byids.hy.smarttable.bean.Curtain;
import com.byids.hy.smarttable.bean.EzvizCamera;
import com.byids.hy.smarttable.bean.Hiddendoor;
import com.byids.hy.smarttable.bean.HomeAttr;
import com.byids.hy.smarttable.bean.Ibeacon;
import com.byids.hy.smarttable.bean.Light;
import com.byids.hy.smarttable.bean.Lightbelt;
import com.byids.hy.smarttable.bean.Lock;
import com.byids.hy.smarttable.bean.Music;
import com.byids.hy.smarttable.bean.Outdoorwaterflow;
import com.byids.hy.smarttable.bean.Panel;
import com.byids.hy.smarttable.bean.RoomAttr;
import com.byids.hy.smarttable.bean.Rooms;
import com.byids.hy.smarttable.bean.Securityalarm;
import com.byids.hy.smarttable.bean.Sence;
import com.byids.hy.smarttable.utils.AES;
import com.byids.hy.smarttable.utils.ByteUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

  /*
  *  登录界面
  */

public class LoginActivity extends Activity implements SurfaceHolder.Callback{

    String TAG = "result";
    //登录UI
    private ViewPager viewPager;
    private View v1;
    private View v2;
    private ArrayList<View> vlist = new ArrayList<>();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer player;
    private EditText username;
    private EditText password;
    private String uname;
    private String pwd;
    public ImageView iv_login;
    //
    private RequestQueue requestQueue;
    HomeAttr homeAttrBean;
    Rooms rs;
    List<Rooms> roomsList = new ArrayList<Rooms>();
    RoomAttr ra;

    //---------------------------udp----------------------------
    public static final int DEFAULT_PORT = 57816;//端口号
    public static final String LOG_TAG = "WifiBroadcastActivity";
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private static final int MAX_DATA_PACKET_LENGTH = 100;
    String udpCheck;
    String ip;    //接收到的ip地址
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        initView();//初始化view

    }

    private void initView(){
        //初始化surface 播放背景视频
        surfaceView = (SurfaceView) findViewById(R.id.id_sf);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        viewPager = (ViewPager) findViewById(R.id.id_vp);
        v1 = getLayoutInflater().inflate(R.layout.item_login1,null);
        vlist.add(v1);
        v2 = getLayoutInflater().inflate(R.layout.item_login2,null);
        vlist.add(v2);
        //第二页的控件
        iv_login = (ImageView) v2.findViewById(R.id.iv_login);
        username=(EditText)v2.findViewById(R.id.et_username);
        password=(EditText)v2.findViewById(R.id.et_password);
        viewPager.setAdapter(adapter);
    }

    PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return vlist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(vlist.get(position));
            return vlist.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    };

    //surfaceHolder回调
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        player = MediaPlayer.create(this,R.raw.loginmovie);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        player.setLooping(true);
        try {
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player.isPlaying()){
            player.stop();
        }
        player.release();
    }

    //第一页点击事件
    public void tologin(View view){
        switch (view.getId()){
            case R.id.btn_goon:
                viewPager.setCurrentItem(1);           //切换到第二页

                break;
        }
    }

    //测试  包装发送udp
    public void test(String hid){

        String udpJson="{\"command\":\"find\",\"data\":{\"hid\":\""+hid+"\",\"loginName\":\"byids\"}}";
        Log.i(TAG, "test: ------------------"+udpJson);
        byte[] enByte = AES.encrpt(udpJson);//加密
        if (enByte == null)
            return;
        byte[] lengthByte = ByteUtils.intToByteBigEndian(enByte.length);
        byte[] headByte = new byte[8];
        for (int i = 0;i<headByte.length;i++) {
            headByte[i] = 0x50;
        }
        byte[] tailByte = new byte[4];
        tailByte[0] = 0x0d;
        tailByte[1] = 0x0a;
        tailByte[2] = 0x0d;
        tailByte[3] = 0x0a;

        byte[] sendByte = ByteUtils.byteJoin(headByte,lengthByte,enByte,tailByte);
        AES.byteStringLog(sendByte);
        //发送udp广播
        new BroadCastUdp(sendByte).start();
    }

    //发送UDP
    public class BroadCastUdp extends Thread {
        DatagramPacket dataPacket = null;
        DatagramPacket receiveData= null;
        private byte[] dataByte;
        private DatagramSocket udpSocket;
        public BroadCastUdp(byte[] sendByte) {
            this.dataByte = sendByte;
        }
        public void run() {
            try {
                //udpSocket = new DatagramSocket(DEFAULT_PORT);
                if (udpSocket==null){
                    udpSocket = new DatagramSocket(null);
                    udpSocket.setReuseAddress(true);
                    udpSocket.bind(new InetSocketAddress(DEFAULT_PORT));
                }
                dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
                receiveData= new DatagramPacket(buffer,MAX_DATA_PACKET_LENGTH);
                if (this.dataByte == null){
                    return;
                }
                byte[] data = this.dataByte;
                dataPacket.setData(data);
                dataPacket.setLength(data.length);
                dataPacket.setPort(DEFAULT_PORT);

                InetAddress broadcastAddr;
                broadcastAddr = InetAddress.getByName("255.255.255.255");
                dataPacket.setAddress(broadcastAddr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            // while( start ){
            try {
                udpSocket.send(dataPacket);
                sleep(10);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            try {
                udpSocket.receive(receiveData);
                udpSocket.receive(receiveData);
            } catch (Exception e) {
// TODO Auto-generated catch block
                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            if (null!=receiveData){

                if( 0!=receiveData.getLength() ) {
                    String codeString = new String( buffer, 0, receiveData.getLength() );

                    Log.i("result", "接收到数据为codeString: "+codeString);
                    Log.i("result", "接收到数据为: "+codeString.substring(2,4));
                    udpCheck=codeString.substring(2,4);
                    Log.i("result", "接收到数据为: "+udpCheck);
                    Log.i("result","recivedataIP地址为："+receiveData.getAddress().toString().substring(1));//此为IP地址
                    Log.i("result","recivedata_sock地址为："+receiveData.getAddress());//此为IP加端口号

                    /*
                    7.4    连接udp，
                     */
                    ip = receiveData.getAddress().toString().substring(1);   //ip地址
                }
            }else{
                try {
                    udpSocket.send(dataPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            udpSocket.close();
        }

    }

    //第二页点击事件
    public void login(View v){
        switch (v.getId()){
            case R.id.iv_login://登录按钮
                uname = username.getText().toString().trim();
                pwd = password.getText().toString().trim();
                Toast.makeText(this, uname + "," + pwd, Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(uname)|| TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    postAndInitData();
                    Log.i(TAG, "login: 能点击");
                }
        }
    }

    private void postAndInitData(){
        String url="http://115.29.97.189:2737/api/login";
        Map<String,String> map=new HashMap<String, String>();
        map.put("uname", uname);
        map.put("pwd", pwd);
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse:--- "+response.toString());
                //Toast.makeText(LoginActivity.this, "返回的json："+response.toString(), Toast.LENGTH_SHORT).show();
                doJsonParse(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: --"+error.getMessage());
                Toast.makeText(LoginActivity.this, "错误信息:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    //解析服务器返回的json数据
    private void doJsonParse(String jsonData){
        try {
            JSONObject obj1 = new JSONObject(jsonData);
            Iterator iterator = obj1.keys();
            String hid = (String) iterator.next();   //获取动态json的key值（hid）
            test(hid);        //测试udp
            //...sleep one second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "doJsonParse: --------------------"+hid);
            JSONObject obj2 = obj1.getJSONObject(hid);
            String address = obj2.getString("address");//客户地址
            String comment = obj2.getString("comment");//注释
            String owner = obj2.getString("owner");//所有人
            JSONObject profile = obj2.getJSONObject("profile");
            JSONObject homeAttr = profile.getJSONObject("homeAttr");
            String reboot_cmd = homeAttr.getString("reboot_cmd");//重启指令

            //-------------------------- home 信息-----------------------------
            //萤石
            JSONObject ezviz = homeAttr.getJSONObject("ezviz");
            int ezvizActive = ezviz.getInt("active");
            EzvizCamera EzCBean = new EzvizCamera();
            EzCBean.setActive(ezvizActive);
            //门锁
            JSONObject lock = homeAttr.getJSONObject("lock");
            int lockActive = lock.getInt("active");
            String lock_protocol = lock.getString("protocol");
            Lock lockBean = new Lock();
            lockBean.setActive(lockActive);
            lockBean.setProtocol(lock_protocol);
            //影藏门
            JSONObject hiddendoor = homeAttr.getJSONObject("hiddendoor");
            int hiddendoorActive = hiddendoor.getInt("active");
            String hiddendoor_protocol = hiddendoor.getString("protocol");
            String hiddendoor_uuid = hiddendoor.getString("ibeacon_uuid");
            String hiddendoor_major = hiddendoor.getString("ibeacon_major");
            String hiddendoor_minor = hiddendoor.getString("ibeacon_minor");
            String hiddendoor_pwd = hiddendoor.getString("pwd");
            Hiddendoor hiddendoorBean = new Hiddendoor();
            hiddendoorBean.setActive(hiddendoorActive);
            hiddendoorBean.setProtocol(hiddendoor_protocol);
            hiddendoorBean.setIbeacon_uuid(hiddendoor_uuid);
            hiddendoorBean.setIbeacon_major(hiddendoor_major);
            hiddendoorBean.setIbeacon_minor(hiddendoor_minor);
            hiddendoorBean.setPwd(hiddendoor_pwd);
            //摄像机
            JSONObject camera = homeAttr.getJSONObject("camera");
            int cameraActive = camera.getInt("active");
            String camera_ip = camera.getString("pri_ip");
            String camera_domain = camera.getString("pub_domain");
            String camera_uname = camera.getString("uname");
            String camera_pwd = camera.getString("pwd");
            Camera cameraBean = new Camera();
            cameraBean.setActive(cameraActive);
            cameraBean.setPri_ip(camera_ip);
            cameraBean.setPub_domain(camera_domain);
            cameraBean.setUname(camera_uname);
            cameraBean.setPwd(camera_pwd);
            //安防
            JSONObject securityalarm = homeAttr.getJSONObject("securityalarm");
            String securityalarm_protocol = securityalarm.getString("protocol");
            int securityalarm_active = securityalarm.getInt("active");
            Securityalarm SABean = new Securityalarm();
            SABean.setActive(securityalarm_active);
            SABean.setProtocol(securityalarm_protocol);
            //闹钟
            JSONObject alarmclock = homeAttr.getJSONObject("alarmclock");
            int alarmclock_active = alarmclock.getInt("active");
            Alarmclock ACBean = new Alarmclock();
            ACBean.setActive(alarmclock_active);
            //户外喷泉
            JSONObject outdoorwaterflow = homeAttr.getJSONObject("outdoorwaterflow");
            String outdoorwaterflow_protocol = outdoorwaterflow.getString("protocol");
            int outdoorwaterflow_active = outdoorwaterflow.getInt("active");
            Outdoorwaterflow OWBean = new Outdoorwaterflow();
            OWBean.setActive(outdoorwaterflow_active);
            OWBean.setProtocol(outdoorwaterflow_protocol);
            //影音室
            JSONObject cinemaroom = homeAttr.getJSONObject("cinemaroom");
            int cinemaroom_active = cinemaroom.getInt("active");
            Cinemaroom CRBean = new Cinemaroom();
            CRBean.setActive(cinemaroom_active);
            //音乐
            JSONObject music = homeAttr.getJSONObject("music");
            String music_protocol = music.getString("protocol");
            int music_active = music.getInt("active");
            Music musicBean = new Music();
            musicBean.setActive(music_active);
            musicBean.setProtocol(music_protocol);

            //添加home数据
            homeAttrBean = new HomeAttr();
            homeAttrBean.setEzvizCamera(EzCBean);
            homeAttrBean.setLock(lockBean);
            homeAttrBean.setHiddendoor(hiddendoorBean);
            homeAttrBean.setCamera(cameraBean);
            homeAttrBean.setSecurityalarm(SABean);
            homeAttrBean.setAlarmclock(ACBean);
            homeAttrBean.setOutdoorwaterflow(OWBean);
            homeAttrBean.setMusic(musicBean);
            homeAttrBean.setCinemaroom(CRBean);

            //--------------------房间信息------------------------
            JSONArray rooms = homeAttr.getJSONArray("rooms");
            String roomAttr = profile.getString("homeAttr");      //房间json数据
            doParseRooms(roomAttr);//解析房间数据

            int roomsNum = roomsList.size();//房间数量
            String[] roomNameList = new String[roomsNum];//房间名字数组
            for (int i=0;i<roomsNum;i++){
                JSONObject roomsObj = rooms.getJSONObject(i);
                String roomName = roomsObj.getString("roomName");
                roomNameList[i] = roomName;
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("roomNameList",roomNameList);
            intent.putExtra("roomAttr",roomAttr);
            intent.putExtra("hid",hid);
            intent.putExtra("uname",uname);
            intent.putExtra("pwd",pwd);
            if (udpCheck.equals("ip")) {
                intent.putExtra("ip",ip);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("homeAttr",homeAttrBean);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();        //结束此activity，下一个activity返回时，直接退出
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //解析房间数据
    private void doParseRooms(String mRoomAttr){
        //roomsList = new ArrayList<Rooms>();




        try {
            JSONObject object = new JSONObject(mRoomAttr);
            JSONArray rooms = object.getJSONArray("rooms");
            for (int i=0;i<rooms.length();i++){
                rs = new Rooms();
                ra = new RoomAttr();

                Panel panelBean = new Panel();
                Ibeacon ibeaconBean = new Ibeacon();
                Camera_indoor camera_indoorBean = new Camera_indoor();
                Aircondition airconditionBean = new Aircondition();
                Curtain curtainBean = new Curtain();
                Lightbelt lightbeltBean = new Lightbelt();
                Light lightBean = new Light();
                Sence senceBean = new Sence();

                JSONObject roomObj = rooms.getJSONObject(i);
                String roomDBName = roomObj.getString("roomDBName");//房间拼音名
                String roomName = roomObj.getString("roomName");
                JSONObject roomAttr = roomObj.getJSONObject("roomAttr");

                //floorheater
                JSONObject floorheater = roomAttr.getJSONObject("floorheater");
                String floorheater_protocol = floorheater.getString("protocol");
                int floorheater_active = floorheater.getInt("active");

                //panel
                JSONObject panel = roomAttr.getJSONObject("panel");
                if (roomDBName=="keting"||roomDBName=="shufang"||roomDBName=="woshi"){
                    String panel1 = panel.getString("panel1");
                    String panel2 = panel.getString("panel2");
                    String panel3 = panel.getString("panel3");
                    String panel4 = panel.getString("panel4");
                    panelBean.setPanel1(panel1);
                    panelBean.setPanel2(panel2);
                    panelBean.setPanel3(panel3);
                    panelBean.setPanel4(panel4);
                }
                String panel_protocol = panel.getString("protocol");
                int panel_active = panel.getInt("active");
                panelBean.setProtocol(panel_protocol);
                panelBean.setActive(panel_active);
                ra.setPanel(panelBean);

                //ibeacon
                JSONObject ibeacon = roomAttr.getJSONObject("ibeacon");
                if (roomDBName=="keting"||roomDBName=="shufang"||roomDBName=="woshi"){
                    String minor = ibeacon.getString("minor");
                    String major = ibeacon.getString("major");
                    String uuid = ibeacon.getString("uuid");
                    ibeaconBean.setMajor(major);
                    ibeaconBean.setMinor(minor);
                    ibeaconBean.setUuid(uuid);
                }
                int ibeacon_active = ibeacon.getInt("active");
                ibeaconBean.setActive(ibeacon_active);
                ra.setIbeacon(ibeaconBean);

                //camera_indoor
                JSONObject camera_indoor = roomAttr.getJSONObject("camera_indoor");
                String channel_id = camera_indoor.getString("channel_id");
                int camera_indoor_active = camera_indoor.getInt("active");
                camera_indoorBean.setActive(camera_indoor_active);
                camera_indoorBean.setChannel_id(channel_id);
                ra.setCamera_indoor(camera_indoorBean);

                //aircondition
                JSONObject aircondition = roomAttr.getJSONObject("aircondition");
                String aircondition_protocol = aircondition.getString("protocol");
                int aircondition_active = aircondition.getInt("active");
                airconditionBean.setActive(aircondition_active);
                airconditionBean.setProtocol(aircondition_protocol);
                ra.setAircondition(airconditionBean);

                //curtain
                JSONObject curtain = roomAttr.getJSONObject("curtain");
                String right = curtain.getString("right");
                String left = curtain.getString("left");
                String curtain_protocol = curtain.getString("protocol");
                int curtain_active = curtain.getInt("active");
                curtainBean.setProtocol(curtain_protocol);
                curtainBean.setActive(curtain_active);
                curtainBean.setLeft(left);
                curtainBean.setRight(right);
                ra.setCurtain(curtainBean);

                //lightbelt
                JSONObject lightbelt = roomAttr.getJSONObject("lightbelt");
                String lightbelt_protocol = lightbelt.getString("protocol");
                int lightbelt_active = lightbelt.getInt("active");
                lightbeltBean.setActive(lightbelt_active);
                lightbeltBean.setProtocol(lightbelt_protocol);
                ra.setLightbelt(lightbeltBean);

                //light
                JSONObject light = roomAttr.getJSONObject("light");
                String light_protocol = light.getString("protocol");
                int light_active = light.getInt("active");
                lightBean.setProtocol(light_protocol);
                lightBean.setActive(light_active);
                ra.setLight(lightBean);

                //sence
                JSONObject sence = roomAttr.getJSONObject("sence");
                int sence_active = sence.getInt("active");
                senceBean.setActive(sence_active);
                ra.setSence(senceBean);


                rs.setRoomDBName(roomDBName);
                rs.setRoomName(roomName);
                rs.setRoomAttr(ra);

                roomsList.add(rs);//房间信息加入数组
            }
            homeAttrBean.setRooms(roomsList);

        } catch (JSONException e) {
            Log.i(TAG, "doParseRooms: "+e);
            e.printStackTrace();
        }
    }

}
