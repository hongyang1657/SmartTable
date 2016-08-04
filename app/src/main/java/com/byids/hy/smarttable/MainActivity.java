package com.byids.hy.smarttable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byids.hy.smarttable.adapter.MyBaseAdapter;
import com.byids.hy.smarttable.bean.TableModel;
import com.byids.hy.smarttable.utils.AES;
import com.byids.hy.smarttable.utils.ByteUtils;
import com.byids.hy.smarttable.utils.CommandJsonUtils;
import com.byids.hy.smarttable.utils.Encrypt;
import com.byids.hy.smarttable.utils.VibratorUtil;
import com.byids.hy.smarttable.view.MySeekbar;
import com.midea.msmartsdk.openapi.MSmartListListener;
import com.midea.msmartsdk.openapi.MSmartListener;
import com.midea.msmartsdk.openapi.MSmartSDK;
import com.midea.msmartsdk.openapi.MSmartTransportDataListener;
import com.midea.msmartsdk.openapi.device.MSmartDeviceManager;
import com.midea.msmartsdk.openapi.plugin.MSmartPluginManager;
import com.midea.msmartsdk.openapi.transport.MSmartTransportManager;
import com.midea.msmartsdk.openapi.user.MSmartUserManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends Activity {
    private static final String KTAirId ="70368744793037";//客厅空调设备id
    private static final String ETFAirId = "211106232844181";//儿童房空调设备id
    private static final String WSAirId = "70368744778246";//卧室空调设备id
    private static final String WSDeHumId ="140737488376919";//卧室除湿器设备id
    private static final String KTCleanerId ="70368744773719";//客厅净化器id
    private static final String ETFCleanerId = "211106232833371";//儿童房净化器id
    private static final String KTFanId = "211106232834363";//客厅风扇id
    private static final String HumiId = "211106232834440";//加湿器id
    private static final String WSCleanerId = "140737488379700";//卧室净化器id
    private static final String AirBoxId = "70368744776313";

    private String TAG = "result";
    private int flag = 0;
    private int screenFlag = 0;//判断屏幕是否开启

    //udp通信appleTV

    private ListView listView;
    private MyBaseAdapter adapter;
    private RadioGroup radioGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private TableModel tableModel;
    private List<TableModel> list;
    private List<TableModel> beforeList;
    //房间模式和单品
    private String[] keTingModel = {"会客模式","影院模式","用餐模式","离开模式","     空调    ","  净化器  ","智能风扇","智能路由","智能插座","  空气球  ","     灯光    ","     窗帘    "};
    private int[] ketTingImg = {R.mipmap.meet_friend_icon,R.mipmap.medium_icon,R.mipmap.eating_icon,R.mipmap.leave_icon,R.mipmap.aircondition_s,
            R.mipmap.airball_s,R.mipmap.cleaner_s,R.mipmap.fan_s,R.mipmap.smart_tplink_s,R.mipmap.smartjack_s,R.mipmap.deng_icon,R.mipmap.chuanglian_icon};
    private String[] woShiModel = {"起床模式","梳妆模式","睡眠模式","离开模式","     空调    ","  除湿器  ","  加湿器  ","  净化器  ","  睡眠环  ","  空气球  ","  路由器  ","     灯光    ","     窗帘    "};
    private int[] woShiImg = {R.mipmap.get_up_s,R.mipmap.shuzhuang_s,R.mipmap.sleep_s,R.mipmap.leave_icon,R.mipmap.aircondition_s,R.mipmap.dehum_s,R.mipmap.hum_s,R.mipmap.cleaner_s,
            R.mipmap.hand_cycle_s,R.mipmap.airball_s,R.mipmap.smart_tplink_s,R.mipmap.deng_icon,R.mipmap.chuanglian_icon};
    private String[] erTongFangModel = {"学习模式","游戏模式","睡眠模式","离开模式","     空调    ","  净化器  ","智能路由","儿童手环","  空气球  ","     灯光    "};
    private int[] erTongFangImg = {R.mipmap.study_s,R.mipmap.game_s,R.mipmap.sleep_s,R.mipmap.leave_icon,R.mipmap.aircondition_s,
            R.mipmap.cleaner_s,R.mipmap.smart_tplink_s,R.mipmap.hand_cycle_s,R.mipmap.airball_s,R.mipmap.deng_icon};
    private final String KETING = "会客模式";
    private final String WOSHI = "起床模式";
    private final String ERTONGFANG = "学习模式";

    //各种布局view
    LinearLayout linearData1;
    LinearLayout linearData2;
    LinearLayout linearlist;
    ImageView ivLogo;
    RelativeLayout rlCycle;
    RelativeLayout rlMainBack;
    ImageView ivCycleTemp;



    private TextView tvTime;
    private TextView tvWeek;
    private TextView tvDate;
    private TextView tvWaterTemp;//显示水温
    private TextView tvScreen;//屏幕

    int waterTemp = 0;//水温

    private ImageView ivClose;
    //------------------空调dialog控件------------------
    private CheckBox tvAirSwitch;//客厅空调开关
    private CheckBox WsAirSwitch;
    private CheckBox EtfAirSwitch;
    private TextView tvAirTemp;//客厅空调温度
    private TextView tvAirTempWs;
    private TextView tvAirTempEtf;
    private RadioGroup rgAir;//客厅模式rg
    private RadioGroup rgAirWs;
    private RadioGroup rgAirEtf;
    private SeekBar SbAirTempKt;//客厅空调温度
    private SeekBar SbAirTempWs;//卧室空调温度
    private SeekBar SbAirTempEtf;//儿童房空调温度
    private RadioButton tvAirColdKt;
    private RadioButton tvAirAutoKt;
    private RadioButton tvAirHumiKt;
    private RadioButton tvAirHotKt;
    private RadioButton tvAirColdWs;
    private RadioButton tvAirAutoWs;            //各个房间的空调模式
    private RadioButton tvAirHumiWs;
    private RadioButton tvAirHotWs;
    private RadioButton tvAirColdEtf;
    private RadioButton tvAirAutoEtf;
    private RadioButton tvAirHumiEtf;
    private RadioButton tvAirHotEtf;
    //------------------加湿器dialog--------------------
    private SeekBar SbHumiSpeed;
    private TextView tvHumiValue;
    private ImageView ivHumClose;
    private CheckBox tvHumSwitch;
    private CheckBox tvHumAuto;
    //------------------------客厅  净化器dialog控件---------------------
    private SeekBar SbCleanerSpeed;
    private TextView tvCleanerSpeedValue;
    private ImageView ivCleanerClose;
    private CheckBox cbCleanerSwitch;
    private CheckBox cbCleanerAuto;//客厅净化器开关
    private RadioGroup rgCleaner;
    //------------------------儿童房  净化器dialog控件---------------------
    private SeekBar SbCleanerSpeedEtf;
    private TextView tvCleanerSpeedValueEtf;
    private ImageView ivCleanerCloseEtf;
    private CheckBox cbCleanerSwitchEtf;
    private CheckBox cbCleanerAutoEtf;//客厅净化器开关
    private RadioGroup rgCleanerEtf;
    //------------------------卧室  净化器dialog控件---------------------
    private SeekBar SbCleanerSpeedWs;
    private TextView tvCleanerSpeedValueWs;
    private ImageView ivCleanerCloseWs;
    private CheckBox cbCleanerSwitchWs;
    private CheckBox cbCleanerAutoWs;//客厅净化器开关
    private RadioGroup rgCleanerWs;

    //-------------------------风扇dialog--------------------------------
    private SeekBar SbFanSpeed;
    private TextView tvFanSpeed;//显示风的档位
    private ImageView ivFanClose;
    private CheckBox cbFanSwitch;
    private RadioGroup rgFan;
    private RadioButton rbNormal;
    private RadioButton rbMute;
    private RadioButton rbComfort;
    private RadioButton rbSleep;
    //-----------------------------除湿器dialog--------------------------------
    private SeekBar SbDehumiSpeed;
    private TextView tvDehumiSpeed;
    private ImageView ivDehumiClose;
    private CheckBox cbDehumiSwitch;
    private RadioGroup rgDehumi;
    private RadioButton rbDehumiAuto;
    private RadioButton rbDehumichushi;
    private RadioButton rbDehumiganyi;
    private RadioButton rbDehumilianxu;
    //-------------------------------灯光dialog----------------------------------
    private SeekBar SbLight,SbLight2,SbLight3;
    private ImageView ivLightClose,ivLightClose2,ivLightClose3;
    private CheckBox cbLightSwitch,cbLightSwitch2,cbLightSwitch3;
    private TextView tvLightValue,tvLightValue2,tvLightValue3;
    //-------------------------------窗帘dialog----------------------------------
    private ImageView ivCurtainClose,ivCurtainClose2;
    private CheckBox rbCurtain,rbCurtain2;
    private CheckBox rbSecond,rbSecond2;
    private CheckBox rbAll,rbAll2;
    private CheckBox rbStop,rbStop2;
    //-------------------------------影院dialog----------------------------------
    private TextView tvVideoClose;
    private ImageView ivVideo1;
    private ImageView ivVideo2;

    private Dialog dialog1;//客厅空调
    private Dialog dialog11;
    private Dialog dialog111;
    private Dialog dialog2;//卧室除湿器
    private Dialog dialog22;//卧室加湿器
    private Dialog dialogWsCleaner;//卧室净化器
    private Dialog dialog222;//灯光
    private Dialog dialog2222;//卧室灯光
    private Dialog dialog22222;//儿童房灯光
    private Dialog dialog3;//客厅空气净化器
    private Dialog dialog33;//客厅风扇
    private Dialog dialog333;//儿童房净化器
    private Dialog dialog4,dialog44;//窗帘
    private Dialog dialogVideo;//影院模式
    private Dialog dialogModelHuiKe;//各种模式
    private Dialog dialogModelEating;
    private Dialog dialogModelStudy;
    private Dialog dialogModelGame;
    private Dialog dialogModelSleep;
    private Dialog dialogModelGetup;
    private Dialog dialogModelShuzhuang;
    private Dialog dialogModelLeave;
    private Dialog dialogLuYou;//四种单品
    private Dialog dialogYunChaZuo;
    private Dialog dialogKongQiBall;
    private Dialog dialogSleepCycle;


    /*private Thread aThread;
    private Thread bThread;*/

    //隐藏seekbar
    private MySeekbar AirSeekbar;//客厅空调隐藏seekbar
    private MySeekbar AirSeekbarWs;
    private MySeekbar AirSeekbarEtf;
    private MySeekbar HumiSeekbar;//加湿器
    private MySeekbar HumiSeekbarWs;
    private MySeekbar HumiSeekbarEtf;
    private MySeekbar DehumiSeekbar;//除湿器
    private MySeekbar FanSeekbar;//电扇
    private MySeekbar LightSeekbar,LightSeekbar2,LightSeekbar3;//灯光
    private MySeekbar FanSeekbarEtf;
    private MySeekbar CleanerSeekbar;//净化器
    private MySeekbar CleanerSeekbarWs;
    private MySeekbar CleanerSeekbarEtf;

    //----------------socket---------------------
    public static final int DEFAULT_PORT = 57816;
    private TcpLongSocket tcplongSocket;

    //---------------------------udp----------------------------
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private static final int MAX_DATA_PACKET_LENGTH = 100;
    public static final String LOG_TAG = "WifiBroadcastActivity";
    String udpCheck;
    String ip;    //接收到的ip地址

    //--------------------------美的sdk------------------------
    MSmartUserManager mUserManager;//登录
    MSmartDeviceManager mSmartDeviceManager;
    MSmartPluginManager mPluginManager;//插件
    MSmartTransportManager mSmartTransportManager;//传输

    //--------------------------------环形杯垫-------------------------------------
    private ImageView ivCycle;
    private ImageView ivZhiZhen;
    private ImageView ivCycleShow;

    //---------------------------天气板块----------------------------------
    private GridLayout gridLayout1,gridLayout2;
    private ViewPager vpWeather;
    private View view1,view2,view3;
    private List<View> weatherList;
    private String[] BeiJing = {"27℃","北京 阴","阴","湿度:40%","PM2.5 : 82","无持续风向 微风","六月二十九","保湿保润","不宜开窗","易感冒","轻毛毯","早睡早起","易脱毛","T恤"};
    private String[] ShangHai = {"30℃","上海 多云","多云","湿度:70%","PM2.5 : 28","东南风3级","六月二十九","除湿","宜开窗","易感冒","轻毛毯","早睡早起","易脱毛","T恤"};
    private String[] DiBai = {"41℃","迪拜 多云","阴","湿度:38%","PM2.5 : 170","西北风3级","六月二十九","保湿保润","不宜开窗","干燥","防中暑","早睡早起","易脱毛","T恤"};
    private RadioGroup rgWeather;
    private RadioButton rbW1;
    private RadioButton rbW2;
    private RadioButton rbW3;
    //private RadioButton rbW4;
    //-------------------------------新闻板块------------------------------------
    private ViewPager vpNews;
    private View view11,view22,view33,view44;
    private List<View> newsList;
    //-------------------------------新闻板块------------------------------------
    private ViewPager vpMenu;
    private View view111,view222,view333,view444;
    private List<View> menuList;

    //--------------------------progressDialog---------------------------
    private ProgressDialog pd;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int[] b = (int[]) msg.obj;
                    int hour = b[3];
                    int min = b[4];
                    String h;
                    String m;
                    if (hour<10){
                        h = "0"+hour;
                    }else {
                        h = ""+hour;
                    }
                    if (min<10){
                        m = "0"+min;
                    }else {
                        m = ""+min;
                    }
                    tvTime.setText(h + ":" + m);
                    tvDate.setText(b[0] + "." + b[1] + "." + b[2]);
                    if (Calendar.MONDAY == b[5]) {
                        tvWeek.setText("星期一");
                    } else if (Calendar.TUESDAY == b[5]) {
                        tvWeek.setText("星期二");
                    } else if (Calendar.WEDNESDAY == b[5]) {
                        tvWeek.setText("星期三");
                    } else if (Calendar.THURSDAY == b[5]) {
                        tvWeek.setText("星期四");
                    } else if (Calendar.FRIDAY == b[5]) {
                        tvWeek.setText("星期五");
                    } else if (Calendar.SATURDAY == b[5]) {
                        tvWeek.setText("星期六");
                    } else if (Calendar.SUNDAY == b[5]) {
                        tvWeek.setText("星期日");
                    }
                    // 使用postInvalidate可以直接在线程中更新界面
                    tvTime.postInvalidate();
                    tvDate.postInvalidate();
                    tvWeek.postInvalidate();
                    break;
            }
        }
    };

    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    int waterTemp = (int) msg.obj;
                    tvWaterTemp.setText(waterTemp+"℃");
                    tvWaterTemp.postInvalidate();
                    break;
            }
        }
    };
    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 3:
                    int waterTemp = (int) msg.obj;
                    tvWaterTemp.setText(waterTemp+"℃");
                    tvWaterTemp.postInvalidate();
                    break;
            }
        }
    };

    private Handler handler4 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 4:
                    String[] b = (String[]) msg.obj;
                    double b2 = Double.parseDouble(b[2]);
                    int bb = (int) b2;
                    tvTemp.setText(b[0]+"℃");
                    tvPm25.setText("PM2.5："+b[1]+"μg/m³");
                    tvHumCur.setText("湿   度："+bb+"%");
                    tvTemp.postInvalidate();
                    tvPm25.postInvalidate();
                    tvHumCur.postInvalidate();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag=WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window=MainActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        //隐藏虚拟按键
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);

        setContentView(R.layout.activity_table);
        //test("55f6797364c0ce976beb0110");          //udp

        initData();
        initView();

        connectTCP();       //连接Tcp
        getCurrentTime();
        mUserManager = MSmartSDK.getInstance().getUserManager();
        mSmartDeviceManager = MSmartSDK.getInstance().getDeviceManager();//设备
        mSmartTransportManager = MSmartSDK.getInstance().getTransportManager();
        mPluginManager = MSmartSDK.getInstance().getPluginManager();//插件

        mideaSDKLogin();//登录
        //回调，登录成功or失败
        mSmartTransportManager.registerDataResponseListener(new MSmartTransportDataListener() {
            @Override
            public void onSuccess(Map<String, Object> map) {
                Log.i(TAG, "onSuccess: 传输成功"+map.get("deviceId"));
            }

            @Override
            public void onFailure(Map<String, Object> map) {
                Log.i(TAG, "onFailure: 传输失败"+map.get("errorCode")+"错误信息"+map.get("errorMsg"));
            }
        });

        mSmartDeviceManager.startScanDeviceInWifi(new MSmartListListener() {
            @Override
            public void onComplete(List<Map<String, Object>> list) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });


    }

    //登录美的
    private void mideaSDKLogin(){
        mUserManager.loginWithAccount("15308630310", "tvxq12345", new MSmartListener() {
            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: 登录成功");
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: 登录错误"+i+"string"+s);
            }
        });
    }
    //美的传输
    private void mideaSDKTransport(String deviceId,String command){
        command.trim();
        String Command = command.replace(" ","");
        String Cmd = Command.replace(" ","");
        byte[] a = hexStringToByte(Cmd);
        int MessageId = mSmartTransportManager.sendDataMessage(deviceId,(short)0,a,true);
        Log.i(TAG, "mideaSDKTransport: "+MessageId);
    }

    /**
     * 把16进制字符串转换成字节数组
     * @param
     * @return byte[]
     */

    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    private void initData() {
        list = new ArrayList<TableModel>();
        tableModel = new TableModel("会客模式",R.mipmap.meet_friend_icon,"keting");
        list.add(tableModel);
        tableModel = new TableModel("影院模式",R.mipmap.medium_icon,"keting");
        list.add(tableModel);
        tableModel = new TableModel("用餐模式",R.mipmap.eating_icon,"keting");
        list.add(tableModel);
        tableModel = new TableModel("离开模式",R.mipmap.leave_icon,"keting");
        list.add(tableModel);
        tableModel = new TableModel("     空调    ",R.mipmap.aircondition_s,"keting");
        list.add(tableModel);
        tableModel = new TableModel("  净化器  ",R.mipmap.cleaner_s,"keting");
        list.add(tableModel);
        tableModel = new TableModel("智能风扇",R.mipmap.fan_s,"keting");
        list.add(tableModel);
        tableModel = new TableModel("智能路由",R.mipmap.smart_tplink_s,"keting");
        list.add(tableModel);
        tableModel = new TableModel("智能插座",R.mipmap.smartjack_s,"keting");
        list.add(tableModel);
        tableModel = new TableModel("  空气球  ",R.mipmap.airball_s,"keting");
        list.add(tableModel);
        tableModel = new TableModel("     灯光    ",R.mipmap.deng_icon,"keting");
        list.add(tableModel);
        tableModel = new TableModel("     窗帘    ",R.mipmap.chuanglian_icon,"keting");
        list.add(tableModel);
    }

    int flag1 = 1;
    private void initView() {

        ivCycleShow = (ImageView) findViewById(R.id.iv_click_cycle_show);
        ivCycleShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag1 == 1){
                    AnimationSet animationSet = new AnimationSet(true);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
                    alphaAnimation.setDuration(2000);

                    animationSet.addAnimation(alphaAnimation);
                    ivZhiZhen.startAnimation(animationSet);
                    ivCycleTemp.startAnimation(animationSet);
                    tvWaterTemp.startAnimation(animationSet);
                    ivZhiZhen.setVisibility(View.VISIBLE);
                    ivCycleTemp.setVisibility(View.VISIBLE);
                    tvWaterTemp.setVisibility(View.VISIBLE);


                    flag1 = -1;
                }else if (flag1 == -1){
                    AnimationSet animationSet = new AnimationSet(true);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
                    alphaAnimation.setDuration(2000);
                    animationSet.addAnimation(alphaAnimation);
                    ivZhiZhen.startAnimation(animationSet);
                    ivCycleTemp.startAnimation(animationSet);
                    tvWaterTemp.startAnimation(animationSet);
                    ivZhiZhen.setVisibility(View.INVISIBLE);
                    ivCycleTemp.setVisibility(View.INVISIBLE);
                    tvWaterTemp.setVisibility(View.INVISIBLE);
                    flag1 = 1;
                }

            }
        });
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvHumCur = (TextView) findViewById(R.id.tv_humidity);
        tvPm25 = (TextView) findViewById(R.id.tv_pm);

        ivZhiZhen = (ImageView) findViewById(R.id.iv_zhizhen_temp);
        ivCycleTemp = (ImageView) findViewById(R.id.iv_cycle_temp);
        pd = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);   //progressDialog
        pd.setMessage("loding...");

        ivClick = (ImageView) findViewById(R.id.iv_click_cycle);
        ivClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cycleFlag == 0) {
                    ivCycleTemp.startAnimation(cAnimation);
                    cAnimation.start();
                    changeWaterTempUp();
                    cycleFlag = 1;
                } else if (cycleFlag == 1) {
                    ivCycleTemp.startAnimation(cAnimation1);
                    cAnimation1.start();
                    changeWaterTempDown();
                    cycleFlag = 0;
                }
            }
        });
        gridLayout1 = (GridLayout) findViewById(R.id.grid_close);
        gridLayout2 = (GridLayout) findViewById(R.id.grid_weather);
        linearData1 = (LinearLayout) findViewById(R.id.linear_air_group);
        linearData2 = (LinearLayout) findViewById(R.id.linear_data);
        linearlist = (LinearLayout) findViewById(R.id.linear_list);
        ivLogo = (ImageView) findViewById(R.id.logo);
        rlCycle = (RelativeLayout) findViewById(R.id.relate_cycle);
        rlMainBack = (RelativeLayout) findViewById(R.id.relate_main);//背景图片
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWeek = (TextView) findViewById(R.id.tv_week);
        tvDate = (TextView) findViewById(R.id.tv_date);
        radioGroup = (RadioGroup) findViewById(R.id.rg_rooms);
        rb1 = (RadioButton) findViewById(R.id.rb_keting);
        rb2 = (RadioButton) findViewById(R.id.rb_woshi);
        rb3 = (RadioButton) findViewById(R.id.rb_ertongfang);
        radioGroup.setOnCheckedChangeListener(rgListener);
        tvWaterTemp = (TextView) findViewById(R.id.tv_water_number);
        listView = (ListView) findViewById(R.id.lv_model);
        adapter = new MyBaseAdapter(this,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);

        ivCycle = (ImageView) findViewById(R.id.iv_cycle_temp);//环形杯垫

        initCycleAnimation();
        initCycleShowAnimation();

        initDialog();//初始化dialog
        initNewsView();//三个模块
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(10000);
                    doJson();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true){
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    doJson();
                }
            }
        }.start();

    }

    //请求空气球数据
    private TextView tvTemp;
    private TextView tvPm25;
    private TextView tvHumCur;
    private String AirUrl = "http://air.midea.com/v1/open2base/appliance/getStatus?accessId=20c92c94f549fece73ee20539e378ff4";
    private String jsonPost = "{\"applianceId\": "+AirBoxId+"}";
    String AirJson = "";
    private void doJson(){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"),jsonPost);
        Request request = new Request.Builder().url(AirUrl).post(requestBody).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                AirJson = response.body().string();
                Log.i(TAG, "doJson: ------------------------------------"+AirJson);
                StringBuffer stringBuffer = new StringBuffer(AirJson);

                String airJson = stringBuffer.insert(25,"\"").toString();
                Log.i(TAG, "doJson: ----------"+airJson);
                JSONObject jsonObject = new JSONObject(airJson);
                JSONObject object = jsonObject.getJSONObject("result");
                String temp = object.getString("t1");
                String pm25 = object.getString("pm25");
                String humCur = object.getString("humCur");
                String[] a = {temp,pm25,humCur};
                Message message = new Message();
                message.what = 4;
                message.obj = a;
                handler4.sendMessage(message);

                //
            }else {
                Log.i(TAG, "doJson: ------------shibai----------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //--------------------------------发送udp  通信appleTV-----------------------------------
    DatagramSocket ds = null;//监听端口
    DatagramPacket dp = null;
    final String strPlay1 = "{\"state\": \"play\",\"videoName\": \"美的空气管家.mp4\"}";//播放1
    final String strPlay2 = "{\"state\": \"play\",\"videoName\": \"美的智能制造.mov\"}";//播放2
    final String strPause = "{\"state\": \"pause\",\"videoName\": \"美的智能制造.mov\"}";//停止
    private int exitThread = 0;     //默认为0 ，当值改变时退出线程，停止通信
    public void sendToAppleTV(final String str){

        new Thread(){
            @Override
            public void run() {
                super.run();
                while (exitThread==1){
                    try {
                        ds = new DatagramSocket();
                        //将要发送的数据封装到数据包中
                        Log.i(TAG, "run: -------5------"+str);
                        //使用DatagramPacket将数据封装到该对象中
                        byte[] buf = str.getBytes();
                        dp = new DatagramPacket(buf, buf.length,InetAddress.getByName("255.255.255.255"),57816);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    try {
                        for (int i=0;i<3;i++){
                            ds.send(dp);
                            Thread.sleep(50);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] backbuf = new byte[1024];
                    DatagramPacket backPacket = new DatagramPacket(backbuf, backbuf.length);
                    try {
                        ds.receive(backPacket);  //接收返回数据
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String backMsg = new String(backbuf, 0, backPacket.getLength());
                    Log.i(TAG, "run: -------------tv返回的数据---"+backMsg);
                    ds.close();
                    exitThread = -1;
                }
            }
        }.start();
        //第二个线程
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (exitThread==2){
                    try {
                        ds = new DatagramSocket();
                        //将要发送的数据封装到数据包中
                        Log.i(TAG, "run: -------55------"+str);
                        //使用DatagramPacket将数据封装到该对象中
                        byte[] buf = str.getBytes();
                        dp = new DatagramPacket(buf, buf.length,InetAddress.getByName("255.255.255.255"),57816);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    try {
                        for (int i=0;i<3;i++){
                            ds.send(dp);
                            Thread.sleep(50);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ds.close();
                    exitThread = -1;
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (exitThread==0){
                    try {
                        ds = new DatagramSocket();
                        //将要发送的数据封装到数据包中
                        Log.i(TAG, "run: -------55------"+str);
                        //使用DatagramPacket将数据封装到该对象中
                        byte[] buf = str.getBytes();
                        dp = new DatagramPacket(buf, buf.length,InetAddress.getByName("255.255.255.255"),57816);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    try {
                        for (int i=0;i<3;i++){
                            ds.send(dp);
                            Thread.sleep(50);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ds.close();
                    exitThread = -1;
                }
            }
        }.start();
    }
    //------------------------------接收Apple UDP---------------------------------------
    DatagramSocket dsRecive = null;
    DatagramPacket dpRecive = null;
    public void reciveAppleTV(){
        try {
            dsRecive = new DatagramSocket(57816);  //监听10000端口
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //定义数据包，用于存储数据
        byte[] buf = new byte[1024];
        dpRecive = new DatagramPacket(buf,buf.length);

        try {
            dsRecive.receive(dpRecive);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ip = dpRecive.getAddress().getHostAddress();   //数据提取
        String data = new String(dpRecive.getData(),0,dpRecive.getLength());
        int port = dpRecive.getPort();
        System.out.println(data+"."+port+".."+ip);
        Log.i(TAG, "reciveAppleTV: ---------------"+data+"-----"+port+"-----"+"ip");
        dsRecive.close();
    }



    LayoutInflater inflater;
    //三个模块 ------------------天气 新闻 菜谱----------------------
    TextView tvIcon1;
    TextView tvIcon2;
    TextView tvIcon3;
    TextView tvIcon4;
    TextView tvIcon5;
    TextView tvIcon6;
    TextView tvIcon7;
    TextView tvIcon8;
    private void initNewsView(){
        tvIcon1 = (TextView) findViewById(R.id.tv_icon1);
        tvIcon2 = (TextView) findViewById(R.id.tv_icon2);
        tvIcon3 = (TextView) findViewById(R.id.tv_icon3);
        tvIcon4 = (TextView) findViewById(R.id.tv_icon4);
        tvIcon5 = (TextView) findViewById(R.id.tv_icon5);
        tvIcon6 = (TextView) findViewById(R.id.tv_icon6);
        tvIcon7 = (TextView) findViewById(R.id.tv_icon7);
        tvIcon8 = (TextView) findViewById(R.id.tv_icon8);

        rgWeather = (RadioGroup) findViewById(R.id.rg_weather);
        vpWeather = (ViewPager) findViewById(R.id.viewpager_weather);
        vpNews = (ViewPager) findViewById(R.id.viewpager_news);
        vpMenu = (ViewPager) findViewById(R.id.viewpager_menu);
        //天气页面切换
        rbW1 = (RadioButton) findViewById(R.id.rb_1);
        rbW2 = (RadioButton) findViewById(R.id.rb_2);
        rbW3 = (RadioButton) findViewById(R.id.rb_3);
        //rbW4 = (RadioButton) view.findViewById(R.id.rb_4);
        vpWeather.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position%3){
                    case 0:
                        rbW1.setChecked(true);
                        tvIcon1.setText(BeiJing[6]);
                        tvIcon2.setText(BeiJing[7]);
                        tvIcon3.setText(BeiJing[8]);
                        tvIcon4.setText(BeiJing[9]);
                        tvIcon5.setText(BeiJing[10]);
                        tvIcon6.setText(BeiJing[11]);
                        tvIcon7.setText(BeiJing[12]);
                        tvIcon8.setText(BeiJing[13]);
                        vpWeather.setBackgroundResource(R.drawable.beijing);   //切换图片
                        break;
                    case 1:
                        rbW2.setChecked(true);
                        tvIcon1.setText(ShangHai[6]);
                        tvIcon2.setText(ShangHai[7]);
                        tvIcon3.setText(ShangHai[8]);
                        tvIcon4.setText(ShangHai[9]);
                        tvIcon5.setText(ShangHai[10]);
                        tvIcon6.setText(ShangHai[11]);
                        tvIcon7.setText(ShangHai[12]);
                        tvIcon8.setText(ShangHai[13]);
                        vpWeather.setBackgroundResource(R.drawable.shanghai);
                        break;
                    case 2:
                        rbW3.setChecked(true);
                        tvIcon1.setText(DiBai[6]);
                        tvIcon2.setText(DiBai[7]);
                        tvIcon3.setText(DiBai[8]);
                        tvIcon4.setText(DiBai[9]);
                        tvIcon5.setText(DiBai[10]);
                        tvIcon6.setText(DiBai[11]);
                        tvIcon7.setText(DiBai[12]);
                        tvIcon8.setText(DiBai[13]);
                        vpWeather.setBackgroundResource(R.drawable.dibai);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        weatherList = new ArrayList<View>();
        newsList = new ArrayList<View>();
        menuList = new ArrayList<View>();

        inflater = getLayoutInflater();
        for (int i=0;i<99;i++){
            changeView(view1,weatherList,R.layout.weather_beijing_layout,1,1);
            changeView(view2,weatherList,R.layout.weather_beijing_layout,2,1);
            changeView(view3,weatherList,R.layout.weather_beijing_layout,3,1);
        }
        changeView(view11,newsList,R.layout.news_layout,4,2);
        changeView(view22,newsList,R.layout.news_layout,5,2);
        changeView(view33,newsList,R.layout.news_layout,6,2);
        changeView(view44,newsList,R.layout.add_layout,101,10);//添加新闻页面
        changeView(view111,menuList,R.layout.menu_layout,7,3);
        changeView(view222,menuList,R.layout.menu_layout,8,3);
        changeView(view333,menuList,R.layout.menu_layout,9,3);
        changeView(view444,menuList,R.layout.add_layout,102,10);//添加菜谱页面

        PagerAdapter pagerAdapter1 = new PagerAdapter() {
            @Override
            public int getCount() {
                return weatherList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //super.destroyItem(container, position, object);
                container.removeView(weatherList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //return super.instantiateItem(container, position);
                container.addView(weatherList.get(position));
                return weatherList.get(position);
            }
        };
        PagerAdapter pagerAdapter2 = new PagerAdapter() {
            @Override
            public int getCount() {
                return newsList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //super.destroyItem(container, position, object);
                container.removeView(newsList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //return super.instantiateItem(container, position);
                container.addView(newsList.get(position));
                return newsList.get(position);
            }
        };
        PagerAdapter pagerAdapter3 = new PagerAdapter() {
            @Override
            public int getCount() {
                return menuList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //super.destroyItem(container, position, object);
                container.removeView(menuList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //return super.instantiateItem(container, position);
                container.addView(menuList.get(position));
                return menuList.get(position);
            }
        };
        vpWeather.setAdapter(pagerAdapter1);
        vpNews.setAdapter(pagerAdapter2);
        vpMenu.setAdapter(pagerAdapter3);
    }

    //----------------------------------------------------------------

    private void changeView(View view,List<View> mList,int Rwenjian,int index,int index2){                  //index   456 新闻  11,12add     789 菜谱         index2    2新闻  3菜谱

        view = inflater.inflate(Rwenjian,null);
        if (index2==1){               //天气
            TextView tvWeatherTemp = (TextView) view.findViewById(R.id.tv_weather_temp);
            TextView tvWeatherCity = (TextView) view.findViewById(R.id.tv_weather_city);
            //TextView tvWeatherTianQi = (TextView) view.findViewById(R.id.tv_weather_tianqi);
            TextView tvWeatherHumi = (TextView) view.findViewById(R.id.tv1);
            TextView tvWeatherPM = (TextView) view.findViewById(R.id.tv2);
            TextView tvWeatherWind = (TextView) view.findViewById(R.id.tv3);

            if (index == 1){
                tvWeatherTemp.setText(BeiJing[0]);
                tvWeatherCity.setText(BeiJing[1]);
                //tvWeatherTianQi.setText(BeiJing[2]);
                tvWeatherHumi.setText(BeiJing[3]);
                tvWeatherPM.setText(BeiJing[4]);
                tvWeatherWind.setText(BeiJing[5]);

            }
            if (index == 2){
                tvWeatherTemp.setText(ShangHai[0]);
                tvWeatherCity.setText(ShangHai[1]);
                //tvWeatherTianQi.setText(ShangHai[2]);
                tvWeatherHumi.setText(ShangHai[3]);
                tvWeatherPM.setText(ShangHai[4]);
                tvWeatherWind.setText(ShangHai[5]);

            }
            if (index == 3){
                tvWeatherTemp.setText(DiBai[0]);
                tvWeatherCity.setText(DiBai[1]);
                //tvWeatherTianQi.setText(DiBai[2]);
                tvWeatherHumi.setText(DiBai[3]);
                tvWeatherPM.setText(DiBai[4]);
                tvWeatherWind.setText(DiBai[5]);

            }
        }else if (index2==2){                //新闻
            ImageView ivNewsImg = (ImageView) view.findViewById(R.id.iv_news_img);
            TextView tvNewsTitle = (TextView) view.findViewById(R.id.tv_news_title);
            TextView tvNewsContent = (TextView) view.findViewById(R.id.tv_news_content);
            if (index==5){
                ivNewsImg.setImageResource(R.drawable.jixie);
                tvNewsTitle.setText("美的收购东芝家电事业部");
                tvNewsContent.setText("         中国佛山和日本东京 - 2016年3月30日 - 今天，美的集团股" +
                        "份有限公司 (深圳证券交易所代码: 000333, “美的”) 和东芝株式会社 (东京证券交" +
                        "易所代码: 6502, “东芝”)联合宣布，双方已签署确定性协议（“协议”），将东芝家电" +
                        "业务80.1%的股权转让给美的，并加强战略合作伙伴关系。两个备受尊敬的企业将通过利用" +
                        "彼此互补的能力和资源，携手共同探索发展机遇。");
            }else if (index==6){
                ivNewsImg.setImageResource(R.drawable.xiaomi);
                tvNewsTitle.setText("美的邀约收购德国库卡");
                tvNewsContent.setText("         2016年5月18日 – 全球领先的消费家电及暖通空调系统工业集团美" +
                        "的，宣布透过旗下子公司MECCA International (BVI) Limited有意通过自愿要约，以每股115欧" +
                        "元价格，收购全球领先的智能自动化解决方案的供应商德国库卡的所有股份。该交易证实了美" +
                        "的此前有意增加德国库卡持股比例的意向。目前美的间接拥有库卡13.5%的股权。鉴于美的有意将" +
                        "在库卡的持股比例增加至30%以上，公司必须按照监管机构规则，就库卡所有已发行的股本提出收购要约。");
            }
        }else if (index2==3){          //菜谱
            ImageView ivMenuImg = (ImageView) view.findViewById(R.id.iv_menu_img);
            TextView tvMenuTitle = (TextView) view.findViewById(R.id.tv_menu_title);
            TextView tvMenuBt1 = (TextView) view.findViewById(R.id.bt_menu1);
            TextView tvMenuBt2 = (TextView) view.findViewById(R.id.bt_menu2);
            TextView tvMenuContent = (TextView) view.findViewById(R.id.tv_menu_content);
            TextView tvMenuTips1 = (TextView) view.findViewById(R.id.tv_menu_tips1);
            TextView tvMenuTips2 = (TextView) view.findViewById(R.id.tv_menu_tips2);
            if (index==8){   //香辣虾
                ivMenuImg.setImageResource(R.drawable.longxia);
                tvMenuTitle.setText("香辣虾");
                tvMenuBt1.setText("川菜");
                tvMenuBt2.setText("炒");
                tvMenuContent.setText("         这是妈妈做的香辣虾，不得不说，现在妈妈做的菜中充满了感" +
                        "情。还记得周星弛电影《食神》中那碗黯然消魂饭，因为溶进了感情，最后黯然消魂饭在" +
                        "比赛中获胜，赢得冠军。 那碗饭其实就是叉烧饭。叉烧用碳火现烤，红红的浆汁浇在上面发出吱吱的声音；");
                tvMenuTips1.setText("主料:海虾");
                tvMenuTips2.setText("辅料：鲜虾1斤，土豆3个，洋葱1个，水芹菜2根，花椒，干辣椒，糖");
            }else if (index==9){  //烤猪
                ivMenuImg.setImageResource(R.drawable.longxia);
                tvMenuTitle.setText("烤乳猪");
                tvMenuBt1.setText("粤菜");
                tvMenuBt2.setText("烤");
                tvMenuContent.setText("         烤乳猪是广州最著名的特色菜，并且是“满汉全席”中的主打菜肴之一。[1] " +
                        " 早在西周时此菜已被列为“八珍”之一，那时称为“炮豚”。烤乳猪也是许多年来广东人祭祖的祭品之" +
                        "一，是家家都少不了的应节之物，用乳猪祭完先人后，亲戚们再聚餐食用。");
                tvMenuTips1.setText("主料：带皮净乳猪");
                tvMenuTips2.setText("辅料：生菜 适量、大葱丝50克，薄饼500克，柱侯酱1/2瓶，海鲜酱1/2瓶，白芝麻酱、花生酱");
            }
        }else if (index2==10){
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_add);
            if (index==101){
                imageView.setImageResource(R.drawable.more_news);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.setTitle("获取更多新闻");
                        pd.show();

                    }
                });
            }else if (index==102){
                imageView.setImageResource(R.drawable.more_menu);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.setTitle("获取更多菜谱");
                        pd.show();

                    }
                });
            }
        }
            mList.add(view);
    }

    //设置时间
    private void getCurrentTime(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                while (!Thread.currentThread().isInterrupted()) {
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);//0-11 代表1-12月
                    int mon = month + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int min = c.get(Calendar.MINUTE);
                    int week = c.get(Calendar.DAY_OF_WEEK);
                    int[] a = {year,mon,day,hour,min,week};
                    Message message = new Message();
                    message.what = 1;
                    message.obj = a;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }.start();
    }

    //水温数值上升
    private void changeWaterTempUp(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                ivClick.setClickable(false);
                while (waterTemp<30) {
                    if (waterTemp < 33) {           //设定水温
                        waterTemp++;
                        ivClick.setClickable(false);
                    }
                    ivClick.setClickable(true);
                    Message message = new Message();
                    message.what = 2;
                    message.obj = waterTemp;
                    handler1.sendMessage(message);

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ivClick.setClickable(true);
            }
        }.start();
    }
    //水温数值下降
    private void changeWaterTempDown(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                ivClick.setClickable(false);
                while (waterTemp>15) {
                    super.run();
                    if (waterTemp > 13) {           //设定水温
                        waterTemp = waterTemp-1;
                        ivClick.setClickable(false);
                    }
                    ivClick.setClickable(true);
                    Message message = new Message();
                    message.what = 3;
                    message.obj = waterTemp;
                    handler2.sendMessage(message);

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ivClick.setClickable(true);
            }
        }.start();
    }


    //模式点击  listview item click
    private int saveRoomKT = 0;
    private int saveRoomWS = 0;
    private int saveRoomETF = 0;        //保存点击的模式
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (list.get(0).getModelText()){
                case KETING:
                    switch (position){
                        case 0:
                            adapter.changeSelected(position+10,11);
                            saveRoomKT = position+10;
                            intentToModelDialog(dialogModelHuiKe);
                            controlModel("会客","keting");   //byids模式
                            mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 2A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 11 44 F0");//客厅空调自动26度
                            mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 28 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 07 E9 0E");//客厅净化器，手动风速40
                            mideaSDKTransport(KTFanId,"aa 1d fa 00 00 00 00 00 00 02 00 00 00 00 82 00 00 00 80 00 00 00 00 00 ff 00 00 00 00 e6");//客厅风扇：正常风
                            break;
                        case 1:
                            adapter.changeSelected(position+10,12);
                            saveRoomKT = position+10;
                            controlModel("影院","keting");   //byids模式
                            intentToVideoDialog(dialogVideo);
                            mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 44 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 05 1C 0A");//客厅空调20度
                            mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 1E 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 06 78 8A");//客厅净化器，手动风速30
                            mideaSDKTransport(KTFanId,"aa 1d fa 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 ff 00 00 00 00 67");//客厅风扇：关
                            break;
                        case 2:
                            adapter.changeSelected(position+10,13);
                            saveRoomKT = position+10;
                            controlModel("用餐","keting");   //byids模式
                            intentToModelDialog(dialogModelEating);
                            mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 47 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 08 93 8D");//客厅空调23度
                            mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 11 65 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 01 5E 70");//客厅净化器，自动风速
                            mideaSDKTransport(KTFanId,"aa 1d fa 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 ff 00 00 00 00 67");//客厅风扇：关
                            break;
                        case 3:
                            adapter.changeSelected(position+10,14);
                            saveRoomKT = position+10;
                            controlModel("离开","keting");   //byids模式
                            intentToModelDialog(dialogModelLeave);
                            break;
                        case 4:
                            intentToAirDialog(dialog1);            //控制空调开关
                            break;
                        case 5:
                            intentToCleanerDialog(dialog3);   //客厅净化器
                            break;
                        case 6:
                            intentToCleanerDialog(dialog33);//客厅风扇
                            break;
                        case 7:
                            intentToDanPinDialog(dialogLuYou);
                            break;
                        case 8:
                            intentToDanPinDialog(dialogYunChaZuo);
                            break;
                        case 9:
                            intentToDanPinDialog(dialogKongQiBall);
                            break;
                        case 10:
                            intentToLightDialog(dialog222);      //灯光dialog
                            break;
                        case 11:
                            intentToCurtainDialog(dialog4);//客厅窗帘
                            break;
                    }
                    break;
                case WOSHI:
                    switch (position){
                        case 0:
                            adapter.changeSelected(position+20,21);
                            saveRoomWS = position+20;
                            intentToModelDialog(dialogModelGetup);
                            controlModel("起床","woshi");   //byids模式
                            mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 42 4A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 14 6E A4");//卧室空调 关
                            mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 42 00 65 7F 7F 00 32 00 00 62 FF 80 00 00 00 00 00 00 00 03 B3 29");//卧室加湿器，关
                            mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 42 01 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0F C6 CB");//除湿机，关
                            break;
                        case 1:
                            adapter.changeSelected(position+20,22);
                            saveRoomWS = position+20;
                            controlModel("梳妆","woshi");   //byids模式
                            intentToModelDialog(dialogModelShuzhuang);
                            mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 46 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 07 0B 17");//卧室空调 制冷22度
                            mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 42 00 65 7F 7F 00 32 00 00 62 FF 80 00 00 00 00 00 00 00 03 B3 29");//卧室加湿器，关
                            mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 42 01 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0F C6 CB");//除湿机，关
                            break;
                        case 2:
                            adapter.changeSelected(position+20,23);
                            saveRoomWS = position+20;
                            controlModel("睡眠","woshi");   //byids模式
                            intentToModelDialog(dialogModelSleep);
                            mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 49 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0A 55 C7");//卧室空调 制冷25度
                            mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 43 00 1E 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 29 85 78");//卧室加湿器，风速30
                            mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 43 01 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 01 8F 06");//除湿机，设定除湿风速10
                            break;
                        case 3:
                            adapter.changeSelected(position+20,24);
                            saveRoomWS = position+20;
                            controlModel("离开","woshi");   //byids模式
                            intentToModelDialog(dialogModelLeave);
                            break;
                        case 4:
                            intentToAirDialog(dialog11);            //控制空调
                            break;
                        case 5:
                            intentToHumiDialog(dialog2);     //除湿器
                            break;
                        case 6:
                            intentToHumiDialog(dialog22);//控制加湿器
                            break;
                        case 7:
                            intentToCleanerDialog(dialogWsCleaner);
                            break;
                        case 8:
                            intentToDanPinDialog(dialogSleepCycle);
                            break;
                        case 9:
                            intentToDanPinDialog(dialogKongQiBall);
                            break;
                        case 10:
                            intentToDanPinDialog(dialogLuYou);
                            break;
                        case 11:
                            intentToLightDialog(dialog2222);      //灯光dialog
                            break;
                        case 12:
                            intentToCurtainDialog(dialog44);//卧室窗帘
                            break;
                    }
                    break;
                case ERTONGFANG:
                    switch (position){
                        case 0:
                            adapter.changeSelected(position+30,31);
                            saveRoomETF = position+30;
                            controlModel("学习","ertongfang");   //byids模式
                            intentToModelDialog(dialogModelStudy);
                            mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 2A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 11 44 F0");//儿童房空调 自动
                            mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 28 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 07 E9 0E");//儿童房净化器，手动40
                            break;
                        case 1:
                            adapter.changeSelected(position+30,32);
                            saveRoomETF = position+30;
                            controlModel("游戏","ertongfang");   //byids模式
                            intentToModelDialog(dialogModelGame);
                            mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 48 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 09 6E B0");//儿童房空调 24度
                            mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 42 20 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0E 0B 0D");//儿童房净化器，关
                            break;
                        case 2:
                            adapter.changeSelected(position+30,33);
                            saveRoomETF = position+30;
                            controlModel("睡眠","ertongfang");   //byids模式
                            intentToModelDialog(dialogModelSleep);
                            mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 2A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 11 44 F0");//儿童房空调：自动：26度
                            mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 04 23 F5");//儿童房净化器，手动10
                            break;
                        case 3:
                            adapter.changeSelected(position+30,34);
                            saveRoomETF = position+30;
                            controlModel("离开","ertongfang");   //byids模式
                            intentToModelDialog(dialogModelLeave);
                            break;
                        case 4:
                            intentToAirDialog(dialog111);            //控制空调
                            break;
                        case 5:
                            intentToCleanerDialog(dialog333);//净化器
                            break;
                        case 6:
                            intentToDanPinDialog(dialogLuYou);
                            break;
                        case 7:
                            intentToDanPinDialog(dialogSleepCycle);
                            break;
                        case 8:
                            intentToDanPinDialog(dialogKongQiBall);
                            break;
                        case 9:
                            intentToLightDialog(dialog22222);      //灯光dialog
                            break;
                    }
                    break;
            }
        }
    };

    //房间点击 RadioGroup
    RadioGroup.OnCheckedChangeListener rgListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_keting:
                    refreshData(keTingModel,ketTingImg,"keting",saveRoomKT);
                    Log.i(TAG, "onCheckedChanged: -----------"+"客厅");
                    rlMainBack.setBackgroundResource(R.drawable.main_back);
                    rb1.setTextColor(MainActivity.this.getResources().getColor(R.color.colorText));
                    rb2.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    rb3.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    break;
                case R.id.rb_woshi:
                    refreshData(woShiModel,woShiImg,"woshi",saveRoomWS);
                    Log.i(TAG, "onCheckedChanged: -----------"+"卧室");
                    rlMainBack.setBackgroundResource(R.drawable.woshi_back);
                    rb2.setTextColor(MainActivity.this.getResources().getColor(R.color.colorText));
                    rb1.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    rb3.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    break;
                case R.id.rb_ertongfang:
                    refreshData(erTongFangModel,erTongFangImg,"ertongfang",saveRoomETF);
                    Log.i(TAG, "onCheckedChanged: -----------"+"儿童房");
                    rlMainBack.setBackgroundResource(R.drawable.ertongfang_back);
                    rb3.setTextColor(MainActivity.this.getResources().getColor(R.color.colorText));
                    rb1.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    rb2.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    break;
            }
        }
    };

    //点击切换房间
    private void refreshData(String[] model,int[] img,String room,int savaRoomss){
        //切换房间时 淡入淡出动画
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        listView.setSelection(0);
        listView.startAnimation(animationSet);

        beforeList = new ArrayList<TableModel>();
        for (int i=0;i<model.length;i++){
            tableModel = new TableModel(model[i],img[i],room,savaRoomss);
            beforeList.add(tableModel);
        }
        list.clear();
        list.addAll(beforeList);
        //adapter.changeRooms();

        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
    }

    /*
    *
    * ---------------------------------二级界面-----------------------------------------
    *
    * */

    //-----------------------空调dialog初始化------------------------
    private void intentToAirDialog(Dialog dia1){
        dia1.show();
    }

    //-----------------------加湿器dialog初始化------------------------
    private void intentToHumiDialog(Dialog dia2){
        dia2.show();
    }

    //---------------------------净化器dialog初始化------------------------------
    private void intentToCleanerDialog(Dialog dia3){
        dia3.show();
    }
    //---------------------------灯光dialog初始化------------------------------
    private void intentToLightDialog(Dialog dia4){
        dia4.show();
    }
    //---------------------------窗帘dialog初始化------------------------------
    private void intentToCurtainDialog(Dialog dia5){
        dia5.show();
    }
    //-------------------------------影院模式dialog--------------------------------------------
    private void intentToVideoDialog(Dialog dia6){
        dia6.show();
    }
    //-------------------------------各种模式dialog--------------------------------------------
    private void intentToModelDialog(final Dialog dia7){
        dia7.show();
        /*AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1,1);
        alphaAnimation.setDuration(2000);
        animationSet.addAnimation(alphaAnimation);*/
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dia7.dismiss();
            }
        }.start();

    }
    //-------------------------------单品dialog--------------------------------------------
    private void intentToDanPinDialog(Dialog dia8){
        dia8.show();
    }


    //初始化dialog
    private void initDialog(){
        //----------------------------客厅空调---------------------------
        dialog1 = new Dialog(this,R.style.CustomDialog);
        View view1 = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout,null);
        tvAirTemp = (TextView) view1.findViewById(R.id.tv_airtemp_value);
        AirSeekbar = (MySeekbar) view1.findViewById(R.id.my_seekbar_air);
        SbAirTempKt = (SeekBar) view1.findViewById(R.id.sb_aircondition_temp);
        ivClose = (ImageView) view1.findViewById(R.id.iv_close);
        tvAirSwitch = (CheckBox) view1.findViewById(R.id.iv_aircondition_turn);//空调开关
        tvAirSwitch.setOnCheckedChangeListener(AirSwitchListener);
        rgAir = (RadioGroup) view1.findViewById(R.id.rg_air);
        tvAirColdKt = (RadioButton) view1.findViewById(R.id.iv_aircondition_cold);
        tvAirAutoKt = (RadioButton) view1.findViewById(R.id.iv_aircondition_auto);
        tvAirHumiKt = (RadioButton) view1.findViewById(R.id.iv_aircondition_humi);
        tvAirHotKt = (RadioButton) view1.findViewById(R.id.iv_aircondition_hot);
        SbAirTempKt.setOnSeekBarChangeListener(airTempListenerKt);
        rgAir.setOnCheckedChangeListener(airChangeListener);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.hide();
            }
        });
        dialog1.setContentView(view1);
        dialog1.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
        params.width = 1200;
        params.height = 400;//改成400
        Window mWindow = dialog1.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);
        //------------------------------------卧室空调-----------------------------------------
        dialog11 = new Dialog(this,R.style.CustomDialog);
        View view11 = LayoutInflater.from(this).inflate(R.layout.ws_custom_dialog_layout,null);
        AirSeekbarWs = (MySeekbar) view11.findViewById(R.id.my_seekbar_air_ws);
        tvAirTempWs = (TextView) view11.findViewById(R.id.tv_airtemp_value_ws);
        SbAirTempWs = (SeekBar) view11.findViewById(R.id.sb_aircondition_temp_ws);
        ivClose = (ImageView) view11.findViewById(R.id.iv_close);
        WsAirSwitch = (CheckBox) view11.findViewById(R.id.iv_aircondition_turn_ws);//空调开关
        WsAirSwitch.setOnCheckedChangeListener(WsAirSwitchListener);
        rgAirWs = (RadioGroup) view11.findViewById(R.id.rg_air_ws);
        tvAirColdWs = (RadioButton) view11.findViewById(R.id.iv_aircondition_cold_ws);
        tvAirAutoWs = (RadioButton) view11.findViewById(R.id.iv_aircondition_auto_ws);
        tvAirHumiWs = (RadioButton) view11.findViewById(R.id.iv_aircondition_humi_ws);
        tvAirHotWs = (RadioButton) view11.findViewById(R.id.iv_aircondition_hot_ws);
        SbAirTempWs.setOnSeekBarChangeListener(airTempListenerWs);//卧室空调温度
        rgAirWs.setOnCheckedChangeListener(airChangeListenerWs);//卧室选择模式
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog11.hide();
            }
        });
        dialog11.setContentView(view11);
        dialog11.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params11 = dialog11.getWindow().getAttributes();
        params11.width = 1200;
        params11.height = 400;//改成400
        Window mWindow11 = dialog11.getWindow();
        mWindow11.setGravity(Gravity.BOTTOM);
        mWindow11.setAttributes(params11);
        //------------------------------------儿童房-----------------------------------------
        dialog111 = new Dialog(this,R.style.CustomDialog);
        View view111 = LayoutInflater.from(this).inflate(R.layout.etf_custom_dialog_layout,null);
        tvAirTempEtf = (TextView) view111.findViewById(R.id.tv_airtemp_value_rtf);
        AirSeekbarEtf = (MySeekbar) view111.findViewById(R.id.my_seekbar_air_rtf);
        SbAirTempEtf = (SeekBar) view111.findViewById(R.id.sb_aircondition_temp_rtf);
        ivClose = (ImageView) view111.findViewById(R.id.iv_close_rtf);
        tvAirSwitch = (CheckBox) view111.findViewById(R.id.iv_aircondition_turn_rtf);//空调开关
        tvAirSwitch.setOnCheckedChangeListener(EtfAirSwitchListener);
        rgAirEtf = (RadioGroup) view111.findViewById(R.id.rg_air_rtf);
        tvAirColdEtf = (RadioButton) view111.findViewById(R.id.iv_aircondition_cold_rtf);
        tvAirAutoEtf = (RadioButton) view111.findViewById(R.id.iv_aircondition_auto_rtf);
        tvAirHumiEtf = (RadioButton) view111.findViewById(R.id.iv_aircondition_humi_rtf);
        tvAirHotEtf = (RadioButton) view111.findViewById(R.id.iv_aircondition_hot_rtf);
        SbAirTempEtf.setOnSeekBarChangeListener(airTempListenerEtf);
        rgAirEtf.setOnCheckedChangeListener(airChangeListenerEtf);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog111.hide();
            }
        });
        dialog111.setContentView(view111);
        dialog111.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params111 = dialog111.getWindow().getAttributes();
        params111.width = 1200;
        params111.height = 400;//改成400
        Window mWindow111 = dialog111.getWindow();
        mWindow111.setGravity(Gravity.BOTTOM);
        mWindow111.setAttributes(params111);



        //-------------------------------2卧室除湿器--------------------------------
        dialog2 = new Dialog(this,R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dehumi_dialog_layout,null);
        SbDehumiSpeed = (SeekBar) view.findViewById(R.id.sb_dehumi_value);
        DehumiSeekbar = (MySeekbar) view.findViewById(R.id.my_seekbar_dehumi);
        tvDehumiSpeed = (TextView) view.findViewById(R.id.tv_dehumi_value);
        ivDehumiClose = (ImageView) view.findViewById(R.id.iv_dehumi_close);
        cbDehumiSwitch = (CheckBox) view.findViewById(R.id.iv_dehumi_switch);
        rgDehumi = (RadioGroup) view.findViewById(R.id.rg_dehumi);
        rbDehumiAuto = (RadioButton) view.findViewById(R.id.iv_dehumi_auto);
        rbDehumichushi = (RadioButton) view.findViewById(R.id.iv_dehumi_chushi);
        rbDehumiganyi = (RadioButton) view.findViewById(R.id.iv_dehumi_declose);
        rbDehumilianxu = (RadioButton) view.findViewById(R.id.iv_dehumi_lianxu);
        SbDehumiSpeed.setOnSeekBarChangeListener(dehumiSpeedListener);
        cbDehumiSwitch.setOnCheckedChangeListener(dehumiSwitchCheckedListener);
        rgDehumi.setOnCheckedChangeListener(dehumiModeListener);
        ivDehumiClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.hide();
            }
        });
        dialog2.setContentView(view);
        dialog2.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params2 = dialog2.getWindow().getAttributes();
        params2.width = 1200;
        params2.height = 400;
        Window mWindow2 = dialog2.getWindow();
        mWindow2.setGravity(Gravity.BOTTOM);
        mWindow2.setAttributes(params2);
        //-------------------------------22卧室加湿器---------------------------------
        dialog22 = new Dialog(this,R.style.CustomDialog);
        View view22 = LayoutInflater.from(this).inflate(R.layout.humidifier_dialog_layout,null);
        SbHumiSpeed = (SeekBar) view22.findViewById(R.id.sb_humiditier);
        ivHumClose = (ImageView) view22.findViewById(R.id.iv_hum_close);
        tvHumSwitch = (CheckBox) view22.findViewById(R.id.tv_hum_switch);
        tvHumAuto = (CheckBox) view22.findViewById(R.id.tv_hum_auto);
        HumiSeekbarWs = (MySeekbar) view22.findViewById(R.id.my_seekbar_humi);
        tvHumiValue = (TextView) view22.findViewById(R.id.tv_fun_speed);
        SbHumiSpeed.setOnSeekBarChangeListener(humiSpeedListener);//加湿器风速调节 3档
        tvHumSwitch.setOnCheckedChangeListener(humSwitchCheckedListener);
        tvHumAuto.setOnCheckedChangeListener(humAutoCheckedListener);
        ivHumClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog22.hide();
            }
        });
        dialog22.setContentView(view22);
        dialog22.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params22 = dialog22.getWindow().getAttributes();
        params22.width = 1200;
        params22.height = 400;
        Window mWindow22 = dialog22.getWindow();
        mWindow22.setGravity(Gravity.BOTTOM);
        mWindow22.setAttributes(params22);
        //-------------------------------222客厅灯光---------------------------------
        dialog222 = new Dialog(this,R.style.CustomDialog);
        View view222 = LayoutInflater.from(this).inflate(R.layout.light_dialog_layout,null);
        SbLight = (SeekBar) view222.findViewById(R.id.sb_light);
        LightSeekbar = (MySeekbar) view222.findViewById(R.id.my_seekbar_light);
        ivLightClose = (ImageView) view222.findViewById(R.id.iv_light_close);
        cbLightSwitch = (CheckBox) view222.findViewById(R.id.tv_light_switch);
        tvLightValue = (TextView) view222.findViewById(R.id.tv_light_value);
        SbLight.setOnSeekBarChangeListener(lightValueListener);
        cbLightSwitch.setOnCheckedChangeListener(lightSwitchCheckedListener);
        ivLightClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog222.hide();
            }
        });
        dialog222.setContentView(view222);
        dialog222.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params222 = dialog222.getWindow().getAttributes();
        params222.width = 1200;
        params222.height = 400;
        Window mWindow222 = dialog222.getWindow();
        mWindow222.setGravity(Gravity.BOTTOM);
        mWindow222.setAttributes(params222);
        //-------------------------------2222卧室灯光---------------------------------
        dialog2222 = new Dialog(this,R.style.CustomDialog);
        View view2222 = LayoutInflater.from(this).inflate(R.layout.light_dialog_layout,null);
        SbLight2 = (SeekBar) view2222.findViewById(R.id.sb_light);
        LightSeekbar2 = (MySeekbar) view2222.findViewById(R.id.my_seekbar_light);
        ivLightClose2 = (ImageView) view2222.findViewById(R.id.iv_light_close);
        cbLightSwitch2 = (CheckBox) view2222.findViewById(R.id.tv_light_switch);
        tvLightValue2 = (TextView) view2222.findViewById(R.id.tv_light_value);
        SbLight2.setOnSeekBarChangeListener(lightValueListener2);
        cbLightSwitch2.setOnCheckedChangeListener(lightSwitchCheckedListener2);
        ivLightClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2222.hide();
            }
        });
        dialog2222.setContentView(view2222);
        dialog2222.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params2222 = dialog2222.getWindow().getAttributes();
        params2222.width = 1200;
        params2222.height = 400;
        Window mWindow2222 = dialog2222.getWindow();
        mWindow2222.setGravity(Gravity.BOTTOM);
        mWindow2222.setAttributes(params2222);
        //-------------------------------22222儿童房灯光---------------------------------
        dialog22222 = new Dialog(this,R.style.CustomDialog);
        View view22222 = LayoutInflater.from(this).inflate(R.layout.light_dialog_layout,null);
        SbLight3 = (SeekBar) view22222.findViewById(R.id.sb_light);
        LightSeekbar3 = (MySeekbar) view22222.findViewById(R.id.my_seekbar_light);
        ivLightClose3 = (ImageView) view22222.findViewById(R.id.iv_light_close);
        cbLightSwitch3 = (CheckBox) view22222.findViewById(R.id.tv_light_switch);
        tvLightValue3 = (TextView) view22222.findViewById(R.id.tv_light_value);
        SbLight3.setOnSeekBarChangeListener(lightValueListener3);
        cbLightSwitch3.setOnCheckedChangeListener(lightSwitchCheckedListener3);
        ivLightClose3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog22222.hide();
            }
        });
        dialog22222.setContentView(view22222);
        dialog22222.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params22222 = dialog22222.getWindow().getAttributes();
        params22222.width = 1200;
        params22222.height = 400;
        Window mWindow22222 = dialog22222.getWindow();
        mWindow22222.setGravity(Gravity.BOTTOM);
        mWindow22222.setAttributes(params22222);


        //----------------------------------卧室净化器------------------------------------
        dialogWsCleaner = new Dialog(this,R.style.CustomDialog);
        View viewWsC = LayoutInflater.from(this).inflate(R.layout.ws_cleaner_dialog_layout,null);
        CleanerSeekbarWs = (MySeekbar) viewWsC.findViewById(R.id.my_seekbar_cleaner_ws);
        SbCleanerSpeedWs = (SeekBar) viewWsC.findViewById(R.id.sb_cleaner_ws);
        ivCleanerCloseWs = (ImageView) viewWsC.findViewById(R.id.iv_cleaner_close_ws);
        cbCleanerSwitchWs = (CheckBox) viewWsC.findViewById(R.id.tv_cleaner_switch_ws);
        cbCleanerAutoWs = (CheckBox) viewWsC.findViewById(R.id.tv_cleaner_auto_ws);
        tvCleanerSpeedValueWs = (TextView) viewWsC.findViewById(R.id.tv_cleaner_speed_ws);
        cbCleanerAutoWs.setClickable(false);//一开始不能点击
        cbCleanerAutoWs.setOnCheckedChangeListener(WsCleanerAutoCheckListener);
        cbCleanerSwitchWs.setOnCheckedChangeListener(WsCleanerSwitchCheckListener);
        SbCleanerSpeedWs.setOnSeekBarChangeListener(cleanerSpeedListenerWs);//净化器风速
        ivCleanerCloseWs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWsCleaner.hide();
            }
        });
        dialogWsCleaner.setContentView(viewWsC);
        dialogWsCleaner.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsws = dialogWsCleaner.getWindow().getAttributes();
        paramsws.width = 1200;
        paramsws.height = 400;
        Window mWindow3ws = dialogWsCleaner.getWindow();
        mWindow3ws.setGravity(Gravity.BOTTOM);
        mWindow3ws.setAttributes(paramsws);

        //----------------------------------3客厅净化器------------------------------------
        dialog3 = new Dialog(this,R.style.CustomDialog);
        View view2 = LayoutInflater.from(this).inflate(R.layout.kt_cleaner_dialog_layout,null);
        CleanerSeekbar = (MySeekbar) view2.findViewById(R.id.my_seekbar_cleaner_kt);
        SbCleanerSpeed = (SeekBar) view2.findViewById(R.id.sb_cleaner_kt);
        ivCleanerClose = (ImageView) view2.findViewById(R.id.iv_cleaner_close_kt);
        cbCleanerSwitch = (CheckBox) view2.findViewById(R.id.tv_cleaner_switch_kt);
        cbCleanerAuto = (CheckBox) view2.findViewById(R.id.tv_cleaner_auto_kt);
        tvCleanerSpeedValue = (TextView) view2.findViewById(R.id.tv_cleaner_speed_kt);
        cbCleanerAuto.setClickable(false);//一开始不能点击
        cbCleanerAuto.setOnCheckedChangeListener(KtCleanerAutoCheckListener);
        cbCleanerSwitch.setOnCheckedChangeListener(KtCleanerSwitchCheckListener);
        SbCleanerSpeed.setOnSeekBarChangeListener(cleanerSpeedListenerKt);//净化器风速
        ivCleanerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.hide();
            }
        });
        dialog3.setContentView(view2);
        dialog3.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params3 = dialog3.getWindow().getAttributes();
        params3.width = 1200;
        params3.height = 400;
        Window mWindow3 = dialog3.getWindow();
        mWindow3.setGravity(Gravity.BOTTOM);
        mWindow3.setAttributes(params3);

        //----------------------------------33客厅风扇------------------------------------
        dialog33 = new Dialog(this,R.style.CustomDialog);
        View view33 = LayoutInflater.from(this).inflate(R.layout.fan_dialog_layout,null);
        SbFanSpeed = (SeekBar) view33.findViewById(R.id.sb_fan);
        FanSeekbar = (MySeekbar) view33.findViewById(R.id.my_seekbar_fan);
        ivFanClose = (ImageView) view33.findViewById(R.id.iv_fan_close);
        tvFanSpeed = (TextView) view33.findViewById(R.id.tv_fan_value);
        cbFanSwitch = (CheckBox) view33.findViewById(R.id.cb_fan_switch);
        rgFan = (RadioGroup) view33.findViewById(R.id.rg_fan);
        rbNormal = (RadioButton) view33.findViewById(R.id.rb_normal);
        rbMute = (RadioButton) view33.findViewById(R.id.rb_mute);
        rbComfort = (RadioButton) view33.findViewById(R.id.rb_comfort);
        rbSleep = (RadioButton) view33.findViewById(R.id.rb_sleep_fan);
        SbFanSpeed.setOnSeekBarChangeListener(FanSbChangeListener);
        cbFanSwitch.setOnCheckedChangeListener(funSwitchListener);
        rgFan.setOnCheckedChangeListener(funCheckListener);
        ivFanClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog33.hide();
            }
        });
        dialog33.setContentView(view33);
        dialog33.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params33 = dialog33.getWindow().getAttributes();
        params33.width = 1200;
        params33.height = 400;
        Window mWindow33 = dialog33.getWindow();
        mWindow33.setGravity(Gravity.BOTTOM);
        mWindow33.setAttributes(params33);

        //----------------------------------333儿童房净化器------------------------------------
        dialog333 = new Dialog(this,R.style.CustomDialog);
        View view333 = LayoutInflater.from(this).inflate(R.layout.etf_cleaner_dialog_layout,null);
        SbCleanerSpeedEtf = (SeekBar) view333.findViewById(R.id.sb_cleaner_etf);
        CleanerSeekbarEtf = (MySeekbar) view333.findViewById(R.id.my_seekbar_cleaner_etf);
        tvCleanerSpeedValueEtf = (TextView) view333.findViewById(R.id.tv_cleaner_speed_etf);
        ivCleanerCloseEtf = (ImageView) view333.findViewById(R.id.iv_cleaner_close_etf);
        cbCleanerSwitchEtf = (CheckBox) view333.findViewById(R.id.tv_cleaner_switch_etf);
        cbCleanerAutoEtf = (CheckBox) view333.findViewById(R.id.tv_cleaner_auto_etf);
        cbCleanerSwitchEtf.setOnCheckedChangeListener(CleanerSwitchListenerEtf);
        cbCleanerAutoEtf.setOnCheckedChangeListener(CleanerAutoListenerEtf);
        SbCleanerSpeedEtf.setOnSeekBarChangeListener(cleanerSpeedListenerEtf);
        ivCleanerCloseEtf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog333.hide();
            }
        });
        dialog333.setContentView(view333);
        dialog333.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params333 = dialog333.getWindow().getAttributes();
        params333.width = 1200;
        params333.height = 400;
        Window mWindow333 = dialog333.getWindow();
        mWindow333.setGravity(Gravity.BOTTOM);
        mWindow333.setAttributes(params333);

        //----------------------------------4客厅窗帘  无纱帘------------------------------------
        dialog4 = new Dialog(this,R.style.CustomDialog);
        View view4 = LayoutInflater.from(this).inflate(R.layout.curtain_dialog_layout,null);
        ivCurtainClose = (ImageView) view4.findViewById(R.id.iv_curtain_close);
        rbCurtain = (CheckBox) view4.findViewById(R.id.rb_curtain_main);
        rbSecond = (CheckBox) view4.findViewById(R.id.rb_curtain_second);
        rbAll = (CheckBox) view4.findViewById(R.id.rb_curtain_open);
        rbStop = (CheckBox) view4.findViewById(R.id.rb_curtain_stop);
        rbSecond.setVisibility(View.GONE);
        rbCurtain.setOnCheckedChangeListener(rbCurtainListener);
        rbAll.setOnCheckedChangeListener(rbAllListener);
        rbStop.setOnCheckedChangeListener(rbStopListener);
        ivCurtainClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog4.hide();
            }
        });
        dialog4.setContentView(view4);
        dialog4.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params4 = dialog4.getWindow().getAttributes();
        params4.width = 1200;
        params4.height = 400;
        Window mWindow4 = dialog4.getWindow();
        mWindow4.setGravity(Gravity.BOTTOM);
        mWindow4.setAttributes(params4);
        //----------------------------------44卧室窗帘------------------------------------
        dialog44 = new Dialog(this,R.style.CustomDialog);
        View view44 = LayoutInflater.from(this).inflate(R.layout.curtain_dialog_layout,null);
        ivCurtainClose2 = (ImageView) view44.findViewById(R.id.iv_curtain_close);
        rbCurtain2 = (CheckBox) view44.findViewById(R.id.rb_curtain_main);
        rbSecond2 = (CheckBox) view44.findViewById(R.id.rb_curtain_second);
        rbAll2 = (CheckBox) view44.findViewById(R.id.rb_curtain_open);
        rbStop2 = (CheckBox) view44.findViewById(R.id.rb_curtain_stop);
        rbCurtain2.setOnCheckedChangeListener(rbCurtain2Listener);
        rbSecond2.setOnCheckedChangeListener(rbSecond2Listener);
        rbAll2.setOnCheckedChangeListener(rbAll2Listener);
        rbStop2.setOnCheckedChangeListener(rbStop2Listener);
        ivCurtainClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog44.hide();
            }
        });
        dialog44.setContentView(view44);
        dialog44.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params44 = dialog44.getWindow().getAttributes();
        params44.width = 1200;
        params44.height = 400;
        Window mWindow44 = dialog44.getWindow();
        mWindow44.setGravity(Gravity.BOTTOM);
        mWindow44.setAttributes(params44);

        //----------------------------------44影院dialog------------------------------------
        dialogVideo = new Dialog(this,R.style.CustomDialog);
        View viewVideo = LayoutInflater.from(this).inflate(R.layout.video_dialog_layout,null);
        tvVideoClose = (TextView) viewVideo.findViewById(R.id.iv_video_close);//退出影院
        ivVideo1 = (ImageView) viewVideo.findViewById(R.id.iv_video1);
        ivVideo2 = (ImageView) viewVideo.findViewById(R.id.iv_video2);
        ivVideo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVideo1.setBackgroundResource(R.drawable.kuang_on);
                ivVideo2.setBackgroundResource(R.drawable.kuang_off);
                exitThread = 1;
                sendToAppleTV(strPlay1);        //向appleTV发送udp通信  播放1
            }
        });
        ivVideo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVideo1.setBackgroundResource(R.drawable.kuang_off);
                ivVideo2.setBackgroundResource(R.drawable.kuang_on);
                exitThread = 2;
                sendToAppleTV(strPlay2);       //向appleTV发送udp通信  播放2
            }
        });
        tvVideoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitThread = 0;
                sendToAppleTV(strPause);     //退出影院模式，停止播放
                ivVideo1.setBackgroundResource(R.drawable.kuang_off);
                ivVideo2.setBackgroundResource(R.drawable.kuang_off);
                dialogVideo.hide();
            }
        });
        dialogVideo.setContentView(viewVideo);
        dialogVideo.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsV = dialogVideo.getWindow().getAttributes();
        paramsV.width = 1200;
        paramsV.height = 400;
        Window mWindowV = dialogVideo.getWindow();
        mWindowV.setGravity(Gravity.BOTTOM);
        mWindowV.setAttributes(paramsV);

        //----------------------------------会客1dialog------------------------------------
        dialogModelHuiKe = new Dialog(this,R.style.CustomDialog);
        View viewHuiKe = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelHuike = (ImageView) viewHuiKe.findViewById(R.id.iv_model);
        dialogModelHuiKe.setContentView(viewHuiKe);
        dialogModelHuiKe.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsM = dialogModelHuiKe.getWindow().getAttributes();
        paramsM.width = 540;
        paramsM.height = 400;
        Window mWindowM = dialogModelHuiKe.getWindow();
        mWindowM.setGravity(Gravity.CENTER);
        mWindowM.setAttributes(paramsM);
        //----------------------------------用餐2dialog------------------------------------
        dialogModelEating = new Dialog(this,R.style.CustomDialog);
        View viewE = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelE = (ImageView) viewE.findViewById(R.id.iv_model);
        ivModelE.setImageResource(R.drawable.eating_model);
        dialogModelEating.setContentView(viewE);
        dialogModelEating.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsE = dialogModelEating.getWindow().getAttributes();
        paramsE.width = 540;
        paramsE.height = 400;
        Window mWindowE = dialogModelEating.getWindow();
        mWindowE.setGravity(Gravity.CENTER);
        mWindowE.setAttributes(paramsE);
        //----------------------------------起床3dialog------------------------------------
        dialogModelGetup = new Dialog(this,R.style.CustomDialog);
        View viewG = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelG = (ImageView) viewG.findViewById(R.id.iv_model);
        ivModelG.setImageResource(R.drawable.getup_model);
        dialogModelGetup.setContentView(viewG);
        dialogModelGetup.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsG = dialogModelGetup.getWindow().getAttributes();
        paramsG.width = 540;
        paramsG.height = 400;
        Window mWindowG = dialogModelGetup.getWindow();
        mWindowG.setGravity(Gravity.CENTER);
        mWindowG.setAttributes(paramsG);
        //----------------------------------学习4dialog------------------------------------
        dialogModelStudy = new Dialog(this,R.style.CustomDialog);
        View viewS = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelS = (ImageView) viewS.findViewById(R.id.iv_model);
        ivModelS.setImageResource(R.drawable.study_model);
        dialogModelStudy.setContentView(viewS);
        dialogModelStudy.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsS = dialogModelStudy.getWindow().getAttributes();
        paramsS.width = 540;
        paramsS.height = 400;
        Window mWindowS = dialogModelStudy.getWindow();
        mWindowS.setGravity(Gravity.CENTER);
        mWindowS.setAttributes(paramsS);
        //----------------------------------游戏5dialog------------------------------------
        dialogModelGame = new Dialog(this,R.style.CustomDialog);
        View viewGA = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelGA = (ImageView) viewGA.findViewById(R.id.iv_model);
        ivModelGA.setImageResource(R.drawable.game_model);
        dialogModelGame.setContentView(viewGA);
        dialogModelGame.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsGA = dialogModelGame.getWindow().getAttributes();
        paramsGA.width = 540;
        paramsGA.height = 400;
        Window mWindowGA = dialogModelGame.getWindow();
        mWindowGA.setGravity(Gravity.CENTER);
        mWindowGA.setAttributes(paramsGA);
        //----------------------------------睡眠6dialog------------------------------------
        dialogModelSleep = new Dialog(this,R.style.CustomDialog);
        View viewSL = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelSL = (ImageView) viewSL.findViewById(R.id.iv_model);
        ivModelSL.setImageResource(R.drawable.sleep_model);
        dialogModelSleep.setContentView(viewSL);
        dialogModelSleep.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsSL = dialogModelSleep.getWindow().getAttributes();
        paramsSL.width = 540;
        paramsSL.height = 400;
        Window mWindowSL = dialogModelSleep.getWindow();
        mWindowSL.setGravity(Gravity.CENTER);
        mWindowSL.setAttributes(paramsSL);
        //----------------------------------梳妆7dialog------------------------------------
        dialogModelShuzhuang = new Dialog(this,R.style.CustomDialog);
        View viewSZ = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelSZ = (ImageView) viewSZ.findViewById(R.id.iv_model);
        ivModelSZ.setImageResource(R.drawable.shuzhuang_model);
        dialogModelShuzhuang.setContentView(viewSZ);
        dialogModelShuzhuang.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsSZ = dialogModelShuzhuang.getWindow().getAttributes();
        paramsSZ.width = 540;
        paramsSZ.height = 400;
        Window mWindowSZ = dialogModelShuzhuang.getWindow();
        mWindowSZ.setGravity(Gravity.CENTER);
        mWindowSZ.setAttributes(paramsSZ);
        //----------------------------------离开8dialog------------------------------------
        dialogModelLeave = new Dialog(this,R.style.CustomDialog);
        View viewL = LayoutInflater.from(this).inflate(R.layout.model_dialog_layout,null);
        ImageView ivModelL = (ImageView) viewL.findViewById(R.id.iv_model);
        ivModelL.setImageResource(R.drawable.leave_model);
        dialogModelLeave.setContentView(viewL);
        dialogModelLeave.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsL = dialogModelLeave.getWindow().getAttributes();
        paramsL.width = 540;
        paramsL.height = 400;
        Window mWindowL = dialogModelLeave.getWindow();
        mWindowL.setGravity(Gravity.CENTER);
        mWindowL.setAttributes(paramsL);
        //----------------------------------路由9dialog------------------------------------
        dialogLuYou = new Dialog(this,R.style.CustomDialog);
        View viewLy = LayoutInflater.from(this).inflate(R.layout.smart4_dialog_layout,null);
        TextView tvTitle = (TextView) viewLy.findViewById(R.id.tv_danping_title);
        TextView tvContent = (TextView) viewLy.findViewById(R.id.tv_danping_content);
        tvTitle.setText("智能路由器");
        tvContent.setText("美的air＋智能center，一键联网所有美的智能家电。别墅级信号，一机满足全屋接入；安全网络分享功能，使用更安全。");
        dialogLuYou.setContentView(viewLy);
        dialogLuYou.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsLy = dialogLuYou.getWindow().getAttributes();
        paramsLy.width = 600;
        paramsLy.height = 600;
        Window mWindowLy = dialogLuYou.getWindow();
        mWindowLy.setGravity(Gravity.CENTER);
        mWindowLy.setAttributes(paramsLy);
        //----------------------------------云插座10dialog------------------------------------
        dialogYunChaZuo = new Dialog(this,R.style.CustomDialog);
        View viewYCZ = LayoutInflater.from(this).inflate(R.layout.smart4_dialog_layout,null);
        TextView tvTitleYCZ = (TextView) viewYCZ.findViewById(R.id.tv_danping_title);
        TextView tvContentYCZ = (TextView) viewYCZ.findViewById(R.id.tv_danping_content);
        tvTitleYCZ.setText("云插座");
        tvContentYCZ.setText("美的Air＋云插座，专为普通非智能空调设计。一次设置，空调立变智能。操作简单,只需四步便可激活传统空调的智慧潜能，真正实现智能舒适的美好生活。");
        dialogYunChaZuo.setContentView(viewYCZ);
        dialogYunChaZuo.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsYCZ = dialogYunChaZuo.getWindow().getAttributes();
        paramsYCZ.width = 600;
        paramsYCZ.height = 600;
        Window mWindowYCZ = dialogYunChaZuo.getWindow();
        mWindowYCZ.setGravity(Gravity.CENTER);
        mWindowYCZ.setAttributes(paramsYCZ);
        //----------------------------------11空气balldialog------------------------------------
        dialogKongQiBall = new Dialog(this,R.style.CustomDialog);
        View viewKQ = LayoutInflater.from(this).inflate(R.layout.smart4_dialog_layout,null);
        TextView tvTitleKQ = (TextView) viewKQ.findViewById(R.id.tv_danping_title);
        TextView tvContentKQ = (TextView) viewKQ.findViewById(R.id.tv_danping_content);
        tvTitleKQ.setText("空气Ball");
        tvContentKQ.setText("美的air＋空气Ball，依托全新智能科技打造，便捷的操作，人性化设计，轻松对室内空气精准感知；air＋空气Ball与美的i＋智能空调搭配，为你和家人营造一个健康舒适的空气环境。");
        dialogKongQiBall.setContentView(viewKQ);
        dialogKongQiBall.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsKQ = dialogKongQiBall.getWindow().getAttributes();
        paramsKQ.width = 600;
        paramsKQ.height = 600;
        Window mWindowKQ = dialogKongQiBall.getWindow();
        mWindowKQ.setGravity(Gravity.CENTER);
        mWindowKQ.setAttributes(paramsKQ);
        //----------------------------------睡眠环12dialog------------------------------------
        dialogSleepCycle = new Dialog(this,R.style.CustomDialog);
        View viewSC = LayoutInflater.from(this).inflate(R.layout.smart4_dialog_layout,null);
        TextView tvTitleSC = (TextView) viewSC.findViewById(R.id.tv_danping_title);
        TextView tvContentSC = (TextView) viewSC.findViewById(R.id.tv_danping_content);
        tvTitleSC.setText("睡眠环");
        tvContentSC.setText("全新开发的美的睡眠环，拥有多项国级家专利， OLED 显示屏，通过轻触按键，可便捷控制空调模式及温度，通过美的睡眠环与美的空调的联动可让用户感受到最佳的空气环境，打开美的空气管家app，可查询当天步数及昨晚睡眠情况。");
        dialogSleepCycle.setContentView(viewSC);
        dialogSleepCycle.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams paramsSC = dialogSleepCycle.getWindow().getAttributes();
        paramsSC.width = 600;
        paramsSC.height = 600;
        Window mWindowSC = dialogSleepCycle.getWindow();
        mWindowSC.setGravity(Gravity.CENTER);
        mWindowSC.setAttributes(paramsSC);
    }


    //---------------------------------各种控制-----------------------------
    //空调控制  ----------------------------客厅------------------------------  开关
    CompoundButton.OnCheckedChangeListener AirSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                tvAirAutoKt.setClickable(true);
                tvAirHotKt.setClickable(true);
                tvAirColdKt.setClickable(true);
                tvAirHumiKt.setClickable(true);
                tvAirAutoKt.setChecked(true);
                mideaSDKTransport(KTAirId,"AA20AC0000000000010240432AE67F7F003F0000001002000000000A00001144F0");//控制空调 开 自动 26度
            }else if (!isChecked){
                tvAirTemp.setText("");
                tvAirTemp.postInvalidate();//在线程中更新
                tvAirAutoKt.setChecked(false);
                tvAirHotKt.setChecked(false);
                tvAirColdKt.setChecked(false);
                tvAirHumiKt.setChecked(false);
                tvAirAutoKt.setClickable(false);
                tvAirHotKt.setClickable(false);
                tvAirColdKt.setClickable(false);
                tvAirHumiKt.setClickable(false);
                AirSeekbar.setProgress(0);//关闭开关，
                AirSeekbar.setVisibility(View.VISIBLE);
                SbAirTempKt.setVisibility(View.GONE);//拖动条不可控
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 42 4A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 14 6E A4");//控制空调关
            }
        }
    };
    //空调控制  ------------------------卧室-------------------------开关
    CompoundButton.OnCheckedChangeListener WsAirSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                tvAirAutoWs.setClickable(true);
                tvAirAutoWs.setChecked(true);
                tvAirTempWs.setText("26℃");
                tvAirHotWs.setClickable(true);
                tvAirColdWs.setClickable(true);
                tvAirHumiWs.setClickable(true);
                mideaSDKTransport(WSAirId,"AA20AC0000000000010240432AE67F7F003F0000001002000000000A00001144F0");//控制空调 开 自动 26度
            }else if (!isChecked){
                tvAirTempWs.setText("");
                tvAirAutoWs.setChecked(false);
                tvAirHotWs.setChecked(false);
                tvAirColdWs.setChecked(false);
                tvAirHumiWs.setChecked(false);
                tvAirAutoWs.setClickable(false);
                tvAirHotWs.setClickable(false);
                tvAirColdWs.setClickable(false);
                tvAirHumiWs.setClickable(false);
                SbAirTempWs.setProgress(0);//关闭开关，
                mideaSDKTransport(WSAirId,"AA20AC0000000000010240424AE67F7F003F0000001002000000000A0000146EA4");//控制空调关
            }
        }
    };
    //空调控制  ----------------------------儿童房------------------------------  开关
    CompoundButton.OnCheckedChangeListener EtfAirSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                tvAirAutoEtf.setChecked(true);
                tvAirTempEtf.setText("26℃");
                tvAirAutoEtf.setClickable(true);
                tvAirHotEtf.setClickable(true);
                tvAirColdEtf.setClickable(true);
                tvAirHumiEtf.setClickable(true);
                mideaSDKTransport(ETFAirId,"AA20AC0000000000010240432AE67F7F003F0000001002000000000A00001144F0");//控制空调 开 自动 26度
            }else if (!isChecked){
                tvAirTempEtf.setText("");
                tvAirAutoEtf.setChecked(false);
                tvAirHotEtf.setChecked(false);
                tvAirColdEtf.setChecked(false);
                tvAirHumiEtf.setChecked(false);
                tvAirAutoEtf.setClickable(false);
                tvAirHotEtf.setClickable(false);
                tvAirColdEtf.setClickable(false);
                tvAirHumiEtf.setClickable(false);
                AirSeekbarEtf.setVisibility(View.VISIBLE);
                SbAirTempEtf.setVisibility(View.GONE);
                AirSeekbarEtf.setProgress(0);
                SbAirTempEtf.setProgress(0);//关闭开关，
                mideaSDKTransport(ETFAirId,"AA20AC0000000000010240424AE67F7F003F0000001002000000000A0000146EA4");//控制空调关
            }
        }
    };

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------

    //空调  模式控制  ----------------------------------客厅----------------------------------
    RadioGroup.OnCheckedChangeListener airChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.iv_aircondition_cold:
                    AirSeekbar.setVisibility(View.GONE);
                    SbAirTempKt.setVisibility(View.VISIBLE);
                    SbAirTempKt.setProgress(0);
                    tvAirTemp.setText("17℃");
                    mideaSDKTransport(KTAirId,"AA20AC00000000000102404341E67F7F003F0000001002000000000A0000020923");//控制空调 制冷 17度
                    break;
                case R.id.iv_aircondition_auto:
                    AirSeekbar.setVisibility(View.VISIBLE);
                    SbAirTempKt.setVisibility(View.GONE);//拖动条不可控
                    tvAirTemp.setText("26℃");
                    AirSeekbar.setProgress(67);
                    mideaSDKTransport(KTAirId,"AA20AC0000000000010240432AE67F7F003F0000001002000000000A00001144F0");//控制空调 开 自动 26度
                    break;
                case R.id.iv_aircondition_humi:
                    AirSeekbar.setVisibility(View.VISIBLE);
                    SbAirTempKt.setVisibility(View.GONE);//拖动条不可控
                    AirSeekbar.setProgress(67);
                    tvAirTemp.setText("26℃");
                    mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 6A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 13 6C 86");//控制空调 抽湿
                    break;
                case R.id.iv_aircondition_hot:
                    AirSeekbar.setVisibility(View.VISIBLE);
                    SbAirTempKt.setVisibility(View.GONE);//拖动条不可控
                    AirSeekbar.setProgress(100);
                    tvAirTemp.setText("30℃");
                    mideaSDKTransport(KTAirId,"AA20AC0000000000010240438EE67F7F003F0000001002000000000A000012923D");//控制空调 制热
                    break;
            }
        }
    };
    //空调  模式控制  ----------------------------------卧室-----------------------------------
    RadioGroup.OnCheckedChangeListener airChangeListenerWs = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.iv_aircondition_cold_ws:
                    AirSeekbarWs.setVisibility(View.GONE);
                    SbAirTempWs.setVisibility(View.VISIBLE);
                    SbAirTempWs.setProgress(0);
                    tvAirTempWs.setText("17℃");
                    mideaSDKTransport(WSAirId,"AA20AC00000000000102404341E67F7F003F0000001002000000000A0000020923");//控制空调 制冷 17度
                    break;
                case R.id.iv_aircondition_auto_ws:
                    AirSeekbarWs.setVisibility(View.VISIBLE);
                    SbAirTempWs.setVisibility(View.GONE);//拖动条不可控
                    tvAirTempWs.setText("26℃");
                    AirSeekbarWs.setProgress(67);
                    mideaSDKTransport(WSAirId,"AA20AC0000000000010240432AE67F7F003F0000001002000000000A00001144F0");//控制空调 开 自动 26度
                    break;
                case R.id.iv_aircondition_humi_ws:
                    AirSeekbarWs.setVisibility(View.VISIBLE);
                    SbAirTempWs.setVisibility(View.GONE);//拖动条不可控
                    AirSeekbarWs.setProgress(67);
                    tvAirTempWs.setText("26℃");
                    mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 6A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 13 6C 86");//控制空调 抽湿
                    break;
                case R.id.iv_aircondition_hot_ws:
                    AirSeekbarWs.setVisibility(View.VISIBLE);
                    SbAirTempWs.setVisibility(View.GONE);//拖动条不可控
                    AirSeekbarWs.setProgress(100);
                    tvAirTempWs.setText("30℃");
                    mideaSDKTransport(WSAirId,"AA20AC0000000000010240438EE67F7F003F0000001002000000000A000012923D");//控制空调 制热
                    break;
            }
        }
    };
    //空调  模式控制  ----------------------------------儿童房-----------------------------------
    RadioGroup.OnCheckedChangeListener airChangeListenerEtf = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.iv_aircondition_cold_rtf:
                    AirSeekbarEtf.setVisibility(View.GONE);
                    SbAirTempEtf.setVisibility(View.VISIBLE);
                    SbAirTempEtf.setProgress(0);
                    tvAirTempEtf.setText("17℃");
                    mideaSDKTransport(ETFAirId,"AA20AC00000000000102404341E67F7F003F0000001002000000000A0000020923");//控制空调 制冷 17度
                    break;
                case R.id.iv_aircondition_auto_rtf:
                    AirSeekbarEtf.setVisibility(View.VISIBLE);
                    SbAirTempEtf.setVisibility(View.GONE);//拖动条不可控
                    tvAirTempEtf.setText("26℃");
                    AirSeekbarEtf.setProgress(67);
                    mideaSDKTransport(ETFAirId,"AA20AC0000000000010240432AE67F7F003F0000001002000000000A00001144F0");//控制空调 开 自动 26度
                    break;
                case R.id.iv_aircondition_humi_rtf:
                    AirSeekbarEtf.setVisibility(View.VISIBLE);
                    SbAirTempEtf.setVisibility(View.GONE);//拖动条不可控
                    tvAirTempEtf.setText("26℃");
                    AirSeekbarEtf.setProgress(67);
                    mideaSDKTransport(ETFAirId,"AA20AC0000000000010240436AE67F7F003F0000001002000000000A0000136C86");//控制空调 抽湿
                    break;
                case R.id.iv_aircondition_hot_rtf:
                    AirSeekbarEtf.setVisibility(View.VISIBLE);
                    SbAirTempEtf.setVisibility(View.GONE);//拖动条不可控
                    AirSeekbarEtf.setProgress(100);
                    tvAirTempEtf.setText("30℃");
                    mideaSDKTransport(ETFAirId,"AA20AC0000000000010240438EE67F7F003F0000001002000000000A000012923D");//控制空调 制热
                    break;
            }
        }
    };

    //加湿器控制  开关
    CompoundButton.OnCheckedChangeListener humSwitchCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                tvHumAuto.setChecked(true);
                tvHumAuto.setClickable(true);
                mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 43 00 66 7F 7F 00 32 00 00 62 FF 80 00 00 00 00 00 00 00 01 62 7A");
            }else if (!isChecked){
                tvHumAuto.setChecked(false);
                tvHumiValue.setText("");
                HumiSeekbarWs.setVisibility(View.VISIBLE);
                SbHumiSpeed.setVisibility(View.GONE);//拖动条不可控
                SbHumiSpeed.setProgress(0);
                mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 42 00 65 7F 7F 00 32 00 00 62 FF 80 00 00 00 00 00 00 00 03 B3 29");
            }
        }
    };
    //加湿器  自动
    CompoundButton.OnCheckedChangeListener humAutoCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                HumiSeekbarWs.setVisibility(View.VISIBLE);
                SbHumiSpeed.setVisibility(View.GONE);//拖动条不可控
                tvHumiValue.setText("自动");
            }else if (!isChecked){
                HumiSeekbarWs.setVisibility(View.GONE);
                SbHumiSpeed.setVisibility(View.VISIBLE);
                tvHumiValue.setText("1档");
                SbHumiSpeed.setProgress(0);
            }
        }
    };

    //------------------------------------客厅  净化器  开关-----------------------------------------
    CompoundButton.OnCheckedChangeListener KtCleanerSwitchCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                cbCleanerAuto.setChecked(true);
                cbCleanerAuto.setClickable(true);
                CleanerSeekbar.setVisibility(View.VISIBLE);
                tvCleanerSpeedValue.setText("自动");
                SbCleanerSpeed.setVisibility(View.GONE);//拖动条不可控

            }else if (!isChecked){
                cbCleanerAuto.setChecked(false);
                cbCleanerAuto.setClickable(false);
                tvCleanerSpeedValue.setText("");
                SbCleanerSpeed.setProgress(0);
                SbCleanerSpeed.setVisibility(View.GONE);
                CleanerSeekbar.setVisibility(View.VISIBLE);
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 42 20 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0E 0B 0D");
            }
        }
    };
    CompoundButton.OnCheckedChangeListener KtCleanerAutoCheckListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                CleanerSeekbar.setVisibility(View.VISIBLE);
                SbCleanerSpeed.setVisibility(View.GONE);//拖动条不可控
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 11 65 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 01 5E 70");
            }else if (!isChecked){
                CleanerSeekbar.setVisibility(View.GONE);
                SbCleanerSpeed.setVisibility(View.VISIBLE);
                tvCleanerSpeedValue.setText("");
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 04 23 F5");
            }
        }
    };

    //------------------------------------ws  净化器  开关-----------------------------------------
    CompoundButton.OnCheckedChangeListener WsCleanerSwitchCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                cbCleanerAutoWs.setChecked(true);
                cbCleanerAutoWs.setClickable(true);
                CleanerSeekbarWs.setVisibility(View.VISIBLE);
                tvCleanerSpeedValueWs.setText("自动");
                SbCleanerSpeedWs.setVisibility(View.GONE);//拖动条不可控
            }else if (!isChecked){
                cbCleanerAutoWs.setChecked(false);
                cbCleanerAutoWs.setClickable(false);
                tvCleanerSpeedValueWs.setText("");
                SbCleanerSpeedWs.setProgress(0);
                SbCleanerSpeedWs.setVisibility(View.GONE);
                CleanerSeekbarWs.setVisibility(View.VISIBLE);
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 42 20 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0E 0B 0D");
            }
        }
    };
    CompoundButton.OnCheckedChangeListener WsCleanerAutoCheckListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                CleanerSeekbarWs.setVisibility(View.VISIBLE);
                SbCleanerSpeedWs.setVisibility(View.GONE);//拖动条不可控
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 11 65 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 01 5E 70");
            }else if (!isChecked){
                CleanerSeekbarWs.setVisibility(View.GONE);
                SbCleanerSpeedWs.setVisibility(View.VISIBLE);
                tvCleanerSpeedValueWs.setText("");
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 04 23 F5");
            }
        }
    };


    //----------------------------------------风扇模式--------------------------------------------
    RadioGroup.OnCheckedChangeListener funCheckListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_normal:
                    FanSeekbar.setVisibility(View.GONE);
                    SbFanSpeed.setVisibility(View.VISIBLE);//拖动条可控
                    SbFanSpeed.setProgress(0);
                    tvFanSpeed.setText("1档");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 01 00 00 80 00 00 00 00 00 FF 00 00 00 00 E7");
                    break;
                case R.id.rb_mute:
                    FanSeekbar.setVisibility(View.VISIBLE);
                    SbFanSpeed.setVisibility(View.GONE);//拖动条不可控
                    tvFanSpeed.setText("静音风");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 8A 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 DE");
                    break;
                case R.id.rb_comfort:
                    FanSeekbar.setVisibility(View.VISIBLE);
                    SbFanSpeed.setVisibility(View.GONE);
                    tvFanSpeed.setText("舒适风");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 84 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 E4");
                    break;
                case R.id.rb_sleep_fan:
                    FanSeekbar.setVisibility(View.VISIBLE);
                    SbFanSpeed.setVisibility(View.GONE);//拖动条不可控
                    SbFanSpeed.setProgress(0);
                    tvFanSpeed.setText("睡眠风");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                    mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 86 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 E2");
                    break;
            }
        }
    };
    //-----------------------------------风扇开关-----------------------------
    CompoundButton.OnCheckedChangeListener funSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                rbNormal.setClickable(true);
                tvFanSpeed.setText("1档");
                rbMute.setClickable(true);
                rbNormal.setChecked(true);
                rbComfort.setClickable(true);
                rbSleep.setClickable(true);
                FanSeekbar.setVisibility(View.GONE);
                SbFanSpeed.setVisibility(View.VISIBLE);

            }else if (!isChecked){
                tvFanSpeed.setText("");
                rbNormal.setChecked(false);
                rbNormal.setClickable(false);
                rbMute.setChecked(false);
                rbComfort.setChecked(false);
                rbSleep.setChecked(false);
                rbMute.setClickable(false);
                rbComfort.setClickable(false);
                rbSleep.setClickable(false);
                SbFanSpeed.setProgress(0);//关闭开关，
                FanSeekbar.setVisibility(View.VISIBLE);
                SbFanSpeed.setVisibility(View.GONE);
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 68");
            }
        }
    };

    //----------------------------------------儿童房净化器开关---------------------------------------------
    CompoundButton.OnCheckedChangeListener CleanerSwitchListenerEtf = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                cbCleanerAutoEtf.setChecked(true);
                cbCleanerAutoEtf.setClickable(true);
                tvCleanerSpeedValueEtf.setText("自动");
                CleanerSeekbarEtf.setVisibility(View.VISIBLE);
                SbCleanerSpeedEtf.setVisibility(View.GONE);//拖动条不可控

            }else if (!isChecked){
                cbCleanerAutoEtf.setChecked(false);
                tvCleanerSpeedValueEtf.setText("");
                SbCleanerSpeedEtf.setProgress(0);
                SbCleanerSpeedEtf.setVisibility(View.GONE);
                CleanerSeekbarEtf.setVisibility(View.VISIBLE);
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 42 20 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0E 0B 0D");
            }
        }
    };
    CompoundButton.OnCheckedChangeListener CleanerAutoListenerEtf = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                CleanerSeekbarEtf.setVisibility(View.VISIBLE);
                SbCleanerSpeedEtf.setVisibility(View.GONE);//拖动条不可控
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 11 65 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 01 5E 70");
            }else if (!isChecked){
                CleanerSeekbarEtf.setVisibility(View.GONE);
                SbCleanerSpeedEtf.setVisibility(View.VISIBLE);
                tvCleanerSpeedValueEtf.setText("");
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 04 23 F5");
            }
        }
    };
    //-----------------------------------除湿器开关-----------------------------------------
    CompoundButton.OnCheckedChangeListener dehumiSwitchCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                rbDehumiAuto.setClickable(true);
                rbDehumiAuto.setChecked(true);
                tvDehumiSpeed.setText("自动");
                rbDehumichushi.setClickable(true);
                rbDehumiganyi.setClickable(true);
                rbDehumilianxu.setClickable(true);

            }else if (!isChecked){
                tvDehumiSpeed.setText("");
                rbDehumiAuto.setChecked(false);
                rbDehumiAuto.setClickable(false);
                rbDehumichushi.setChecked(false);
                rbDehumiganyi.setChecked(false);
                rbDehumilianxu.setChecked(false);
                rbDehumichushi.setClickable(false);
                rbDehumiganyi.setClickable(false);
                rbDehumilianxu.setClickable(false);
                SbDehumiSpeed.setProgress(0);//关闭开关，
                DehumiSeekbar.setVisibility(View.VISIBLE);
                SbDehumiSpeed.setVisibility(View.GONE);
                mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 42 01 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0F C6 CB");
            }
        }
    };
    //-----------------------------------除湿器模式-----------------------------------------
    RadioGroup.OnCheckedChangeListener dehumiModeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.iv_dehumi_auto:
                    DehumiSeekbar.setVisibility(View.VISIBLE);
                    SbDehumiSpeed.setVisibility(View.GONE);//拖动条不可控
                    SbDehumiSpeed.setProgress(0);
                    tvDehumiSpeed.setText("自动");
                    mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 43 03 65 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0E 44 E7");
                    break;
                case R.id.iv_dehumi_chushi:
                    DehumiSeekbar.setVisibility(View.GONE);
                    SbDehumiSpeed.setVisibility(View.VISIBLE);
                    tvDehumiSpeed.setText("除湿");
                    mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 43 01 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 01 8F 06");
                    break;
                case R.id.iv_dehumi_declose:
                    DehumiSeekbar.setVisibility(View.VISIBLE);
                    SbDehumiSpeed.setVisibility(View.GONE);
                    tvDehumiSpeed.setText("干衣");
                    mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 43 04 01 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0D 27 68");
                    break;
                case R.id.iv_dehumi_lianxu:
                    DehumiSeekbar.setVisibility(View.VISIBLE);
                    SbDehumiSpeed.setVisibility(View.GONE);//拖动条不可控
                    SbDehumiSpeed.setProgress(0);
                    tvDehumiSpeed.setText("连续");
                    mideaSDKTransport(WSDeHumId,"AA 20 A1 00 00 00 00 00 02 02 48 43 02 64 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0C 43 EC");
                    break;
            }
        }
    };


    //------------------------------空调温度调节滑块------------------------------
    SeekBar.OnSeekBarChangeListener airTempListenerKt = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=7){
                tvAirTemp.setText("17℃");
            }else if (progress<=14&&progress>7){
                tvAirTemp.setText("18℃");
            }else if (progress<=21&&progress>14){
                tvAirTemp.setText("19℃");
            }else if (progress<=28&&progress>21){
                tvAirTemp.setText("20℃");
            }else if (progress<=35&&progress>28){
                tvAirTemp.setText("21℃");
            }else if (progress<=42&&progress>35){
                tvAirTemp.setText("22℃");
            }else if (progress<=49&&progress>42){
                tvAirTemp.setText("23℃");
            }else if (progress<=56&&progress>49){
                tvAirTemp.setText("24℃");
            }else if (progress<=63&&progress>56){
                tvAirTemp.setText("25℃");
            }else if (progress<=70&&progress>63){
                tvAirTemp.setText("26℃");
            }else if (progress<=77&&progress>70){
                tvAirTemp.setText("27℃");
            }else if (progress<=84&&progress>77){
                tvAirTemp.setText("28℃");
            }else if (progress<=92&&progress>84){
                tvAirTemp.setText("29℃");
            }else if (progress>92){
                tvAirTemp.setText("30℃");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=7){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 41 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 02 09 23");//控制空调 制冷 17度
            }else if (progress<=14&&progress>7){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 42 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 03 25 05");//控制空调  18
            }else if (progress<=21&&progress>14){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 43 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 04 7F A9");//控制空调  19
            }else if (progress<=28&&progress>21){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 44 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 05 1C 0A");//控制空调  20
            }else if (progress<=35&&progress>28){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 45 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 06 27 FD");//控制空调  21
            }else if (progress<=42&&progress>35){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 46 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 07 0B 17");//控制空调  22
            }else if (progress<=49&&progress>42){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 47 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 08 93 8D");//控制空调  23
            }else if (progress<=56&&progress>49){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 48 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 09 6E B0");//控制空调  24
            }else if (progress<=63&&progress>56){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 49 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0A 55 C7");//控制空调  25
            }else if (progress<=70&&progress>63){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0B 79 A1");//控制空调  26
            }else if (progress<=77&&progress>70){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4B E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0C 23 F5");//控制空调  27
            }else if (progress<=84&&progress>77){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4C E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0E A2 73");//控制空调  28
            }else if (progress<=92&&progress>84){
                mideaSDKTransport(KTAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4D E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0F 25 EE");//控制空调  29
            }else if (progress>92){
                mideaSDKTransport(KTAirId,"AA20AC0000000000010240434EE67F7F003F0000001002000000000A0000108B86");//控制空调  30
            }
        }
    };
    SeekBar.OnSeekBarChangeListener airTempListenerWs = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=7){
                tvAirTempWs.setText("17℃");
            }else if (progress<=14&&progress>7){
                tvAirTempWs.setText("18℃");
            }else if (progress<=21&&progress>14){
                tvAirTempWs.setText("19℃");
            }else if (progress<=28&&progress>21){
                tvAirTempWs.setText("20℃");
            }else if (progress<=35&&progress>28){
                tvAirTempWs.setText("21℃");
            }else if (progress<=42&&progress>35){
                tvAirTempWs.setText("22℃");
            }else if (progress<=49&&progress>42){
                tvAirTempWs.setText("23℃");
            }else if (progress<=56&&progress>49){
                tvAirTempWs.setText("24℃");
            }else if (progress<=63&&progress>56){
                tvAirTempWs.setText("25℃");
            }else if (progress<=70&&progress>63){
                tvAirTempWs.setText("26℃");
            }else if (progress<=77&&progress>70){
                tvAirTempWs.setText("27℃");
            }else if (progress<=84&&progress>77){
                tvAirTempWs.setText("28℃");
            }else if (progress<=92&&progress>84){
                tvAirTempWs.setText("29℃");
            }else if (progress>92){
                tvAirTempWs.setText("30℃");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=7){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 41 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 02 09 23");//控制空调 制冷 17度
            }else if (progress<=14&&progress>7){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 42 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 03 25 05");//控制空调  18
            }else if (progress<=21&&progress>14){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 43 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 04 7F A9");//控制空调  19
            }else if (progress<=28&&progress>21){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 44 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 05 1C 0A");//控制空调  20
            }else if (progress<=35&&progress>28){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 45 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 06 27 FD");//控制空调  21
            }else if (progress<=42&&progress>35){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 46 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 07 0B 17");//控制空调  22
            }else if (progress<=49&&progress>42){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 47 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 08 93 8D");//控制空调  23
            }else if (progress<=56&&progress>49){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 48 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 09 6E B0");//控制空调  24
            }else if (progress<=63&&progress>56){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 49 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0A 55 C7");//控制空调  25
            }else if (progress<=70&&progress>63){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0B 79 A1");//控制空调  26
            }else if (progress<=77&&progress>70){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4B E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0C 23 F5");//控制空调  27
            }else if (progress<=84&&progress>77){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4C E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0E A2 73");//控制空调  28
            }else if (progress<=92&&progress>84){
                mideaSDKTransport(WSAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4D E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0F 25 EE");//控制空调  29
            }else if (progress>92){
                mideaSDKTransport(WSAirId,"AA20AC0000000000010240434EE67F7F003F0000001002000000000A0000108B86");//控制空调  30
            }
        }
    };
    SeekBar.OnSeekBarChangeListener airTempListenerEtf = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=7){
                tvAirTempEtf.setText("17℃");
            }else if (progress<=14&&progress>7){
                tvAirTempEtf.setText("18℃");
            }else if (progress<=21&&progress>14){
                tvAirTempEtf.setText("19℃");
            }else if (progress<=28&&progress>21){
                tvAirTempEtf.setText("20℃");
            }else if (progress<=35&&progress>28){
                tvAirTempEtf.setText("21℃");
            }else if (progress<=42&&progress>35){
                tvAirTempEtf.setText("22℃");
            }else if (progress<=49&&progress>42){
                tvAirTempEtf.setText("23℃");
            }else if (progress<=56&&progress>49){
                tvAirTempEtf.setText("24℃");
            }else if (progress<=63&&progress>56){
                tvAirTempEtf.setText("25℃");
            }else if (progress<=70&&progress>63){
                tvAirTempEtf.setText("26℃");
            }else if (progress<=77&&progress>70){
                tvAirTempEtf.setText("27℃");
            }else if (progress<=84&&progress>77){
                tvAirTempEtf.setText("28℃");
            }else if (progress<=92&&progress>84){
                tvAirTempEtf.setText("29℃");
            }else if (progress>92){
                tvAirTempEtf.setText("30℃");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=7){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 41 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 02 09 23");//控制空调 制冷 17度
            }else if (progress<=14&&progress>7){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 42 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 03 25 05");//控制空调  18
            }else if (progress<=21&&progress>14){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 43 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 04 7F A9");//控制空调  19
            }else if (progress<=28&&progress>21){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 44 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 05 1C 0A");//控制空调  20
            }else if (progress<=35&&progress>28){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 45 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 06 27 FD");//控制空调  21
            }else if (progress<=42&&progress>35){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 46 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 07 0B 17");//控制空调  22
            }else if (progress<=49&&progress>42){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 47 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 08 93 8D");//控制空调  23
            }else if (progress<=56&&progress>49){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 48 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 09 6E B0");//控制空调  24
            }else if (progress<=63&&progress>56){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 49 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0A 55 C7");//控制空调  25
            }else if (progress<=70&&progress>63){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0B 79 A1");//控制空调  26
            }else if (progress<=77&&progress>70){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4B E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0C 23 F5");//控制空调  27
            }else if (progress<=84&&progress>77){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4C E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0E A2 73");//控制空调  28
            }else if (progress<=92&&progress>84){
                mideaSDKTransport(ETFAirId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4D E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0F 25 EE");//控制空调  29
            }else if (progress>92){
                mideaSDKTransport(ETFAirId,"AA20AC0000000000010240434EE67F7F003F0000001002000000000A0000108B86");//控制空调  30
            }
        }
    };
    //-------------------------加湿器风速调节滑块------------------------------
    SeekBar.OnSeekBarChangeListener humiSpeedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=33){
                tvHumiValue.setText("1档");
            }else if (progress<=66&&progress>33){
                tvHumiValue.setText("2档");
            }else if (progress>66){
                tvHumiValue.setText("3档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress1 = seekBar.getProgress();
            if (progress1<34){
                mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 43 00 1E 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 29 85 78");
            }else if (progress1<67&&progress1>=34){
                mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 43 00 32 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 2B 7F 68");
            }else if (progress1>=67){
                mideaSDKTransport(HumiId,"AA 20 FD 00 00 00 00 00 02 02 48 43 00 46 7F 7F 00 32 00 00 61 FF 80 00 00 00 00 00 00 00 2D 68 69");
            }
        }
    };
    //----------------------客厅 净化器风速调节滑块------------------------
    SeekBar.OnSeekBarChangeListener cleanerSpeedListenerKt = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=10){
                tvCleanerSpeedValue.setText("1档");
            }else if (progress<=20&&progress>10){
                tvCleanerSpeedValue.setText("2档");
            }else if (progress<=30&&progress>20){
                tvCleanerSpeedValue.setText("3档");
            }else if (progress<=40&&progress>30){
                tvCleanerSpeedValue.setText("4档");
            }else if (progress<=50&&progress>40){
                tvCleanerSpeedValue.setText("5档");
            }else if (progress<=60&&progress>50){
                tvCleanerSpeedValue.setText("6档");
            }else if (progress<=70&&progress>60){
                tvCleanerSpeedValue.setText("7档");
            }else if (progress<=80&&progress>70){
                tvCleanerSpeedValue.setText("8档");
            }else if (progress<=90&&progress>80){
                tvCleanerSpeedValue.setText("9档");
            }else if (progress>90){
                tvCleanerSpeedValue.setText("10档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=10){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 04 23 F5");
            }else if (progress<=20&&progress>10){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 14 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 05 65 A8");
            }else if (progress<=30&&progress>20){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 1E 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 06 78 8A");
            }else if (progress<=40&&progress>30){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 28 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 07 E9 0E");
            }else if (progress<=50&&progress>40){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 32 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 08 21 CB");
            }else if (progress<=60&&progress>50){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 3C 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 09 11 D0");
            }else if (progress<=70&&progress>60){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 46 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0A 57 7F");
            }else if (progress<=80&&progress>70){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 50 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0B 2A A1");
            }else if (progress<=90&&progress>80){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 5A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0C 56 6A");
            }else if (progress>90){
                mideaSDKTransport(KTCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 64 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0D FC B9");
            }
        }
    };

    //----------------------卧室 净化器风速调节滑块------------------------
    SeekBar.OnSeekBarChangeListener cleanerSpeedListenerWs = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=10){
                tvCleanerSpeedValueWs.setText("1档");
            }else if (progress<=20&&progress>10){
                tvCleanerSpeedValueWs.setText("2档");
            }else if (progress<=30&&progress>20){
                tvCleanerSpeedValueWs.setText("3档");
            }else if (progress<=40&&progress>30){
                tvCleanerSpeedValueWs.setText("4档");
            }else if (progress<=50&&progress>40){
                tvCleanerSpeedValueWs.setText("5档");
            }else if (progress<=60&&progress>50){
                tvCleanerSpeedValueWs.setText("6档");
            }else if (progress<=70&&progress>60){
                tvCleanerSpeedValueWs.setText("7档");
            }else if (progress<=80&&progress>70){
                tvCleanerSpeedValueWs.setText("8档");
            }else if (progress<=90&&progress>80){
                tvCleanerSpeedValueWs.setText("9档");
            }else if (progress>90){
                tvCleanerSpeedValueWs.setText("10档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=10){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 04 23 F5");
            }else if (progress<=20&&progress>10){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 14 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 05 65 A8");
            }else if (progress<=30&&progress>20){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 1E 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 06 78 8A");
            }else if (progress<=40&&progress>30){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 28 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 07 E9 0E");
            }else if (progress<=50&&progress>40){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 32 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 08 21 CB");
            }else if (progress<=60&&progress>50){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 3C 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 09 11 D0");
            }else if (progress<=70&&progress>60){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 46 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0A 57 7F");
            }else if (progress<=80&&progress>70){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 50 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0B 2A A1");
            }else if (progress<=90&&progress>80){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 5A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0C 56 6A");
            }else if (progress>90){
                mideaSDKTransport(WSCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 64 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0D FC B9");
            }
        }
    };

    //----------------------儿童房 净化器风速调节滑块------------------------
    SeekBar.OnSeekBarChangeListener cleanerSpeedListenerEtf = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=10){
                tvCleanerSpeedValueEtf.setText("1档");
            }else if (progress<=20&&progress>10){
                tvCleanerSpeedValueEtf.setText("2档");
            }else if (progress<=30&&progress>20){
                tvCleanerSpeedValueEtf.setText("3档");
            }else if (progress<=40&&progress>30){
                tvCleanerSpeedValueEtf.setText("4档");
            }else if (progress<=50&&progress>40){
                tvCleanerSpeedValueEtf.setText("5档");
            }else if (progress<=60&&progress>50){
                tvCleanerSpeedValueEtf.setText("6档");
            }else if (progress<=70&&progress>60){
                tvCleanerSpeedValueEtf.setText("7档");
            }else if (progress<=80&&progress>70){
                tvCleanerSpeedValueEtf.setText("8档");
            }else if (progress<=90&&progress>80){
                tvCleanerSpeedValueEtf.setText("9档");
            }else if (progress>90){
                tvCleanerSpeedValueEtf.setText("10档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=10){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 0A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 04 23 F5");
            }else if (progress<=20&&progress>10){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 14 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 05 65 A8");
            }else if (progress<=30&&progress>20){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 1E 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 06 78 8A");
            }else if (progress<=40&&progress>30){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 28 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 07 E9 0E");
            }else if (progress<=50&&progress>40){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 32 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 08 21 CB");
            }else if (progress<=60&&progress>50){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 3C 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 09 11 D0");
            }else if (progress<=70&&progress>60){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 46 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0A 57 7F");
            }else if (progress<=80&&progress>70){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 50 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0B 2A A1");
            }else if (progress<=90&&progress>80){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 5A 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0C 56 6A");
            }else if (progress>90){
                mideaSDKTransport(ETFCleanerId,"AA 20 FC 00 00 00 00 00 02 02 48 43 20 64 7F 7F 00 32 00 00 60 FF 80 00 00 00 00 00 00 00 0D FC B9");
            }
        }
    };

    //----------------------------------电扇风速调节----------------------------------------
    SeekBar.OnSeekBarChangeListener FanSbChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=8){
                tvFanSpeed.setText("1档");
            }else if (progress<=16&&progress>8){
                tvFanSpeed.setText("2档");
            }else if (progress<=24&&progress>16){
                tvFanSpeed.setText("3档");
            }else if (progress<=32&&progress>24){
                tvFanSpeed.setText("4档");
            }else if (progress<=40&&progress>32){
                tvFanSpeed.setText("5档");
            }else if (progress<=48&&progress>40){
                tvFanSpeed.setText("6档");
            }else if (progress<=56&&progress>48){
                tvFanSpeed.setText("7档");
            }else if (progress<=64&&progress>56){
                tvFanSpeed.setText("8档");
            }else if (progress<=72&&progress>64){
                tvFanSpeed.setText("9档");
            }else if (progress<=80&&progress>72){
                tvFanSpeed.setText("10档");
            }else if (progress<=88&&progress>80){
                tvFanSpeed.setText("11档");
            }else if (progress<=100&&progress>88){
                tvFanSpeed.setText("12档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=8){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 01 00 00 80 00 00 00 00 00 FF 00 00 00 00 E7");
            }else if (progress<=16&&progress>8){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 02 00 00 80 00 00 00 00 00 FF 00 00 00 00 E6");
            }else if (progress<=24&&progress>16){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 03 00 00 80 00 00 00 00 00 FF 00 00 00 00 E5");
            }else if (progress<=32&&progress>24){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 04 00 00 80 00 00 00 00 00 FF 00 00 00 00 E4");
            }else if (progress<=40&&progress>32){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 05 00 00 80 00 00 00 00 00 FF 00 00 00 00 E3");
            }else if (progress<=48&&progress>40){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 06 00 00 80 00 00 00 00 00 FF 00 00 00 00 E2");
            }else if (progress<=56&&progress>48){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 07 00 00 80 00 00 00 00 00 FF 00 00 00 00 E1");
            }else if (progress<=64&&progress>56){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 08 00 00 80 00 00 00 00 00 FF 00 00 00 00 E0");
            }else if (progress<=72&&progress>64){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 09 00 00 80 00 00 00 00 00 FF 00 00 00 00 DF");
            }else if (progress<=80&&progress>72){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 0A 00 00 80 00 00 00 00 00 FF 00 00 00 00 DE");
            }else if (progress<=88&&progress>80){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 0B 00 00 80 00 00 00 00 00 FF 00 00 00 00 DD");
            }else if (progress<=100&&progress>88){
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 01 00 00 00 80 00 00 00 00 00 FF 00 00 00 00 67");
                mideaSDKTransport(KTFanId,"AA 1D FA 00 00 00 00 00 00 02 00 00 00 00 80 0C 00 00 80 00 00 00 00 00 FF 00 00 00 00 DC");
            }
        }
    };

    //-------------------------------除湿器强度调节---------------------------------------------
    SeekBar.OnSeekBarChangeListener dehumiSpeedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=10){
                tvDehumiSpeed.setText("1档");
            }else if (progress<=20&&progress>10){
                tvDehumiSpeed.setText("2档");
            }else if (progress<=30&&progress>20){
                tvDehumiSpeed.setText("3档");
            }else if (progress<=40&&progress>30){
                tvDehumiSpeed.setText("4档");
            }else if (progress<=50&&progress>40){
                tvDehumiSpeed.setText("5档");
            }else if (progress<=60&&progress>50){
                tvDehumiSpeed.setText("6档");
            }else if (progress<=70&&progress>60){
                tvDehumiSpeed.setText("7档");
            }else if (progress<=80&&progress>70){
                tvDehumiSpeed.setText("8档");
            }else if (progress<=90&&progress>80){
                tvDehumiSpeed.setText("9档");
            }else if (progress>90){
                tvDehumiSpeed.setText("10档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=10){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 41 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 02 09 23");
            }else if (progress<=20&&progress>10){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 42 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 03 25 05");
            }else if (progress<=30&&progress>20){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 43 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 04 7F A9");
            }else if (progress<=40&&progress>30){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 44 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 05 1C 0A");
            }else if (progress<=50&&progress>40){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 45 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 06 27 FD");
            }else if (progress<=60&&progress>50){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 46 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 07 0B 17");
            }else if (progress<=70&&progress>60){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 47 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 08 93 8D");
            }else if (progress<=80&&progress>70){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 48 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 09 6E B0");
            }else if (progress<=90&&progress>80){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 49 E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0A 55 C7");
            }else if (progress>90){
                mideaSDKTransport(WSDeHumId,"AA 20 AC 00 00 00 00 00 01 02 40 43 4A E6 7F 7F 00 3F 00 00 00 10 02 00 00 00 00 0A 00 00 0B 79 A1");
            }
        }
    };

    //-------------------------------------灯光控制客厅---------------------------------------------------
    CompoundButton.OnCheckedChangeListener lightSwitchCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                LightSeekbar.setVisibility(View.GONE);
                SbLight.setVisibility(View.VISIBLE);//拖动条可控
                tvLightValue.setText("10档");
                SbLight.setProgress(100);
                controlLight("keting","100");
            }else if (!isChecked){
                LightSeekbar.setVisibility(View.VISIBLE);
                SbLight.setVisibility(View.GONE);
                tvLightValue.setText(" ");
                SbLight.setProgress(0);
                controlLight("keting","0");
            }
        }
    };

    SeekBar.OnSeekBarChangeListener lightValueListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=10){
                tvLightValue.setText("1档");
            }else if (progress<=20&&progress>10){
                tvLightValue.setText("2档");
            }else if (progress<=30&&progress>20){
                tvLightValue.setText("3档");
            }else if (progress<=40&&progress>30){
                tvLightValue.setText("4档");
            }else if (progress<=50&&progress>40){
                tvLightValue.setText("5档");
            }else if (progress<=60&&progress>50){
                tvLightValue.setText("6档");
            }else if (progress<=70&&progress>60){
                tvLightValue.setText("7档");
            }else if (progress<=80&&progress>70){
                tvLightValue.setText("8档");
            }else if (progress<=90&&progress>80){
                tvLightValue.setText("9档");
            }else if (progress>90){
                tvLightValue.setText("10档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=10){
                controlLight("keting","10");
            }else if (progress<=20&&progress>10){
                controlLight("keting","20");
            }else if (progress<=30&&progress>20){
                controlLight("keting","30");
            }else if (progress<=40&&progress>30){
                controlLight("keting","40");
            }else if (progress<=50&&progress>40){
                controlLight("keting","50");
            }else if (progress<=60&&progress>50){
                controlLight("keting","60");
            }else if (progress<=70&&progress>60){
                controlLight("keting","70");
            }else if (progress<=80&&progress>70){
                controlLight("keting","80");
            }else if (progress<=90&&progress>80){
                controlLight("keting","90");
            }else if (progress>90){
                controlLight("keting","100");
            }
        }
    };
    //-------------------------------------灯光控制卧室---------------------------------------------------
    CompoundButton.OnCheckedChangeListener lightSwitchCheckedListener2 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                LightSeekbar2.setVisibility(View.GONE);
                SbLight2.setVisibility(View.VISIBLE);//拖动条可控
                tvLightValue2.setText("10档");
                SbLight.setProgress(100);
                controlLight("woshi","100");
            }else if (!isChecked){
                LightSeekbar2.setVisibility(View.VISIBLE);
                SbLight2.setVisibility(View.GONE);
                tvLightValue2.setText(" ");
                SbLight.setProgress(0);
                controlLight("woshi","0");
            }
        }
    };

    SeekBar.OnSeekBarChangeListener lightValueListener2 = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=10){
                tvLightValue2.setText("1档");
            }else if (progress<=20&&progress>10){
                tvLightValue2.setText("2档");
            }else if (progress<=30&&progress>20){
                tvLightValue2.setText("3档");
            }else if (progress<=40&&progress>30){
                tvLightValue2.setText("4档");
            }else if (progress<=50&&progress>40){
                tvLightValue2.setText("5档");
            }else if (progress<=60&&progress>50){
                tvLightValue2.setText("6档");
            }else if (progress<=70&&progress>60){
                tvLightValue2.setText("7档");
            }else if (progress<=80&&progress>70){
                tvLightValue2.setText("8档");
            }else if (progress<=90&&progress>80){
                tvLightValue2.setText("9档");
            }else if (progress>90){
                tvLightValue2.setText("10档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=10){
                controlLight("woshi","10");
            }else if (progress<=20&&progress>10){
                controlLight("woshi","20");
            }else if (progress<=30&&progress>20){
                controlLight("woshi","30");
            }else if (progress<=40&&progress>30){
                controlLight("woshi","40");
            }else if (progress<=50&&progress>40){
                controlLight("woshi","50");
            }else if (progress<=60&&progress>50){
                controlLight("woshi","60");
            }else if (progress<=70&&progress>60){
                controlLight("woshi","70");
            }else if (progress<=80&&progress>70){
                controlLight("woshi","80");
            }else if (progress<=90&&progress>80){
                controlLight("woshi","90");
            }else if (progress>90){
                controlLight("woshi","100");
            }
        }
    };
    //-------------------------------------灯光控制儿童房---------------------------------------------------
    CompoundButton.OnCheckedChangeListener lightSwitchCheckedListener3 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                LightSeekbar3.setVisibility(View.GONE);
                SbLight3.setVisibility(View.VISIBLE);//拖动条可控
                tvLightValue3.setText("10档");
                SbLight.setProgress(100);
                controlLight("ertongfang","100");
            }else if (!isChecked){
                LightSeekbar3.setVisibility(View.VISIBLE);
                SbLight3.setVisibility(View.GONE);
                tvLightValue3.setText(" ");
                SbLight.setProgress(0);
                controlLight("ertongfang","0");
            }
        }
    };

    SeekBar.OnSeekBarChangeListener lightValueListener3 = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress<=10){
                tvLightValue3.setText("1档");
            }else if (progress<=20&&progress>10){
                tvLightValue3.setText("2档");
            }else if (progress<=30&&progress>20){
                tvLightValue3.setText("3档");
            }else if (progress<=40&&progress>30){
                tvLightValue3.setText("4档");
            }else if (progress<=50&&progress>40){
                tvLightValue3.setText("5档");
            }else if (progress<=60&&progress>50){
                tvLightValue3.setText("6档");
            }else if (progress<=70&&progress>60){
                tvLightValue3.setText("7档");
            }else if (progress<=80&&progress>70){
                tvLightValue3.setText("8档");
            }else if (progress<=90&&progress>80){
                tvLightValue3.setText("9档");
            }else if (progress>90){
                tvLightValue3.setText("10档");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress<=10){
                controlLight("ertongfang","10");
            }else if (progress<=20&&progress>10){
                controlLight("ertongfang","20");
            }else if (progress<=30&&progress>20){
                controlLight("ertongfang","30");
            }else if (progress<=40&&progress>30){
                controlLight("ertongfang","40");
            }else if (progress<=50&&progress>40){
                controlLight("ertongfang","50");
            }else if (progress<=60&&progress>50){
                controlLight("ertongfang","60");
            }else if (progress<=70&&progress>60){
                controlLight("ertongfang","70");
            }else if (progress<=80&&progress>70){
                controlLight("ertongfang","80");
            }else if (progress<=90&&progress>80){
                controlLight("ertongfang","90");
            }else if (progress>90){
                controlLight("ertongfang","100");
            }
        }
    };

    //--------------------------------控制 客厅 窗帘------------------------------------------
    CompoundButton.OnCheckedChangeListener rbCurtainListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //开窗帘
                rbStop.setChecked(false);
                rbAll.setChecked(false);
                controlCurtain("all_both_open","keting");//窗帘开
            }else if (!isChecked){
                //关窗帘
                controlCurtain("all_both_close","keting");//窗帘关
            }
        }
    };

    CompoundButton.OnCheckedChangeListener rbAllListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //开窗帘
                rbStop.setChecked(false);
                rbCurtain.setChecked(false);
                controlCurtain("all_both_open","keting");//窗帘开
            }else if (!isChecked){
                //关窗帘
                controlCurtain("all_both_close","keting");//窗帘关
            }
        }
    };
    CompoundButton.OnCheckedChangeListener rbStopListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                // 停止
                rbCurtain.setChecked(false);
                rbAll.setChecked(false);
                controlCurtain("all_both_pause","keting");//窗帘暂停
            }
        }
    };

    //--------------------------------控制 卧室 窗帘------------------------------------------
    CompoundButton.OnCheckedChangeListener rbCurtain2Listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //开窗帘
                rbStop2.setChecked(false);
                rbAll2.setChecked(false);
                rbSecond2.setChecked(false);
                controlCurtain("all_right_open","woshi");//窗帘开
            }else if (!isChecked){
                //关窗帘
                controlCurtain("all_right_close","woshi");//窗帘关
            }
        }
    };
    CompoundButton.OnCheckedChangeListener rbSecond2Listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //开纱帘
                rbStop2.setChecked(false);
                rbAll2.setChecked(false);
                rbCurtain2.setChecked(false);
                controlCurtain("all_left_open","woshi");//纱帘开
            }else if (!isChecked){
                //关纱帘
                controlCurtain("all_left_close","woshi");//纱帘关
            }
        }
    };

    CompoundButton.OnCheckedChangeListener rbAll2Listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //开窗帘
                rbStop2.setChecked(false);
                rbCurtain2.setChecked(false);
                rbSecond2.setChecked(false);
                controlCurtain("all_both_open","woshi");//全开
            }else if (!isChecked){
                //关窗帘
                controlCurtain("all_both_close","woshi");//全关
            }
        }
    };
    CompoundButton.OnCheckedChangeListener rbStop2Listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                // 停止
                rbCurtain2.setChecked(false);
                rbSecond2.setChecked(false);
                rbAll2.setChecked(false);
                controlCurtain("all_both_pause","woshi");//暂停
            }
        }
    };

    /*
   *
   * --------------------------------------------------------------------------
   *
   * */


    //listview上下的按钮点击事件
    public void upORDown(View v){
        switch (v.getId()){
            case R.id.iv_up:
                listView.setSmoothScrollbarEnabled(true);
                listView.smoothScrollToPositionFromTop(0,2,1000);
                break;
            case R.id.iv_down:
                listView.setSmoothScrollbarEnabled(true);
                listView.smoothScrollToPositionFromTop(20,2,1000);
                break;
        }
    }


    private void screenAnimationOn(){
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        ivCycleShow.startAnimation(animationSet);
        radioGroup.startAnimation(animationSet);
        linearData1.startAnimation(animationSet);
        linearData2.startAnimation(animationSet);
        linearlist.startAnimation(animationSet);
        vpWeather.startAnimation(animationSet);
        vpMenu.startAnimation(animationSet);
        vpNews.startAnimation(animationSet);
        rgWeather.startAnimation(animationSet);
        rlCycle.startAnimation(animationSet);
        gridLayout1.startAnimation(animationSet);
        gridLayout2.startAnimation(animationSet);
    }
    private void screenAnimationOff(){
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        ivCycleShow.startAnimation(animationSet);
        radioGroup.startAnimation(animationSet);
        linearData1.startAnimation(animationSet);
        linearData2.startAnimation(animationSet);
        linearlist.startAnimation(animationSet);
        vpWeather.startAnimation(animationSet);
        vpMenu.startAnimation(animationSet);
        vpNews.startAnimation(animationSet);
        rgWeather.startAnimation(animationSet);
        rlCycle.startAnimation(animationSet);
        gridLayout1.startAnimation(animationSet);
        gridLayout2.startAnimation(animationSet);
    }

    //----------------------------环形杯垫点击-----------------------------------
    private ImageView ivClick;
    /*public void toCycle(View v){
        switch (v.getId()){
            case R.id.iv_click_cycle:
                if (ivCycle.isShown()==false){
                    ivCycle.startAnimation(animationSet1);
                    animationSet1.start();
                    ivCycle.setVisibility(View.VISIBLE);
                    tvWaterTemp.setVisibility(View.VISIBLE);
                    ivZhiZhen.setVisibility(View.VISIBLE);
                }else if (ivCycle.isShown()==true){
                    if (cycleFlag==0){
                    ivCycleTemp.startAnimation(cAnimation);
                    cAnimation.start();
                    changeWaterTempUp();
                    cycleFlag = 1;
                }else if (cycleFlag==1){
                    ivCycleTemp.startAnimation(cAnimation1);
                    cAnimation1.start();
                    changeWaterTempDown();
                    cycleFlag = 0;
                }
                }

                break;
        }
    }*/

    //------------------------------------环形杯垫显示动画------------------------------------
    AlphaAnimation alphaAnimation1;
    AlphaAnimation alphaAnimation2;
    AlphaAnimation alphaAnimation3;
    AnimationSet animationSet1;
    private void initCycleShowAnimation(){
        animationSet1 = new AnimationSet(true);
        alphaAnimation1 = new AlphaAnimation(0,1);
        alphaAnimation1.setDuration(2000);
        alphaAnimation2 = new AlphaAnimation(1,0);
        alphaAnimation2.setStartOffset(2000);
        alphaAnimation2.setDuration(2000);
        alphaAnimation3 = new AlphaAnimation(0,1);
        alphaAnimation3.setStartOffset(4000);
        alphaAnimation3.setDuration(2000);
        animationSet1.addAnimation(alphaAnimation1);
        animationSet1.addAnimation(alphaAnimation2);
        //animationSet1.addAnimation(alphaAnimation3);
    }

    //--------------------------------------环形杯垫旋转动画---------------------------------------------
    RotateAnimation cAnimation;
    RotateAnimation cAnimation1;
    int cycleFlag = 0;
    private void initCycleAnimation(){
        cAnimation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        cAnimation.setDuration(2000);
        cAnimation.setRepeatCount(3);
        cAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        //cAnimation.setStartOffset(500);//执行前的等待时间

        cAnimation1 = new RotateAnimation(0f,-360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        cAnimation1.setDuration(2000);
        cAnimation1.setRepeatCount(3);
        cAnimation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        //cAnimation1.setStartOffset(500);//执行前的等待时间
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
                udpSocket = new DatagramSocket(DEFAULT_PORT);
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

                Log.e(LOG_TAG, e.toString());
                udpSocket.close();
            }
            if (null!=receiveData){

                if( 0!=receiveData.getLength() ) {
                    String codeString = new String( buffer, 0, receiveData.getLength() );

                    Log.i("result", "接收到数据为codeString: "+codeString);
                    Log.i("result", "接收到数据为: "+codeString.substring(2,4));
                    udpCheck = codeString.substring(2,4);
                    Log.i("result", "接收到数据为: "+udpCheck);
                    Log.i("result","recivedataIP地址为："+receiveData.getAddress().toString().substring(1));//此为IP地址
                    Log.i("result","recivedata_sock地址为："+receiveData.getAddress());//此为IP加端口号

                    /*
                    7.4    连接udp，
                     */
                    //ip = receiveData.getAddress().toString().substring(1);   //ip地址
                    ip = "192.168.10.24";
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



    public void connectTCP(){
        Log.i(TAG, "connectTCP: "+ip);
        tcplongSocket=new TcpLongSocket(new ConnectTcp());
        tcplongSocket.startConnect("192.168.10.24",DEFAULT_PORT);     //ip  写死了
    }

    public class ConnectTcp implements TCPLongSocketCallback {


        @Override
        public void connected() {
            Log.i(TAG, "connected: ---------123------------");
            Log.i("result", String.valueOf(tcplongSocket.getConnectStatus()));
            VibratorUtil.Vibrate(MainActivity.this,300);
            JSONObject checkCommandData=new JSONObject();
            try {
                checkCommandData.put("kong","keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String  checkJson = CommandJsonUtils.getCommandJson(1,checkCommandData,"1","2","3", String.valueOf(System.currentTimeMillis()));
            Log.i("result","check"+checkJson);
            tcplongSocket.writeDate(Encrypt.encrypt(checkJson));

        }

        @Override
        public void receive(byte[] buffer) {
            Log.i("收到数据","--------");
            if("Hello client"==buffer.toString()){
                Log.i("心跳", "心跳"+String.valueOf(tcplongSocket.getConnectStatus()));
            }
        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
        }
    }

    //点击休眠按钮，屏幕的其他控件都隐藏
    public void logoClick(View v){
        switch (v.getId()){
            case R.id.logo:
                if (screenFlag==0){
                    screenAnimationOff();
                    ivCycleShow.setVisibility(View.INVISIBLE);
                    radioGroup.setVisibility(View.INVISIBLE);
                    linearData1.setVisibility(View.INVISIBLE);
                    linearData2.setVisibility(View.INVISIBLE);
                    linearlist.setVisibility(View.INVISIBLE);
                    vpWeather.setVisibility(View.INVISIBLE);
                    vpMenu.setVisibility(View.INVISIBLE);
                    vpNews.setVisibility(View.INVISIBLE);
                    rgWeather.setVisibility(View.INVISIBLE);
                    rlCycle.setVisibility(View.INVISIBLE);
                    gridLayout1.setVisibility(View.INVISIBLE);
                    gridLayout2.setVisibility(View.INVISIBLE);
                    //midea标志透明度改变
                    AnimationSet animationSet1 = new AnimationSet(true);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1,0.5f);
                    alphaAnimation.setDuration(1000);
                    animationSet1.addAnimation(alphaAnimation);
                    ivLogo.startAnimation(animationSet1);
                    ivLogo.setAlpha(0.5f);
                    screenFlag = 1;
                }else if (screenFlag==1){
                    ivCycleShow.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);
                    linearData1.setVisibility(View.VISIBLE);
                    linearData2.setVisibility(View.VISIBLE);
                    linearlist.setVisibility(View.VISIBLE);
                    vpWeather.setVisibility(View.VISIBLE);
                    vpMenu.setVisibility(View.VISIBLE);
                    vpNews.setVisibility(View.VISIBLE);
                    rgWeather.setVisibility(View.VISIBLE);
                    rlCycle.setVisibility(View.VISIBLE);
                    gridLayout1.setVisibility(View.VISIBLE);
                    gridLayout2.setVisibility(View.VISIBLE);
                    //midea标志透明度改变
                    AnimationSet animationSet2 = new AnimationSet(true);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f,1);
                    alphaAnimation.setDuration(1000);
                    animationSet2.addAnimation(alphaAnimation);
                    ivLogo.startAnimation(animationSet2);
                    ivLogo.setAlpha(1.0f);
                    screenAnimationOn();
                    screenFlag = 0;
                }
                break;
        }
    }

    //-----------------------------------------------控制灯，窗帘-----------------------------------------
    //测试控制灯
    private void controlLight(String houseDBName,String lightValue){

            JSONObject lightOnCommandData=new JSONObject();
            JSONObject controlData=new JSONObject();
            try {
                lightOnCommandData.put("controlProtocol","hdl");
                lightOnCommandData.put("machineName","light");
                lightOnCommandData.put("controlData",controlData);
                controlData.put("lightValue",lightValue);
                controlData.put("isServerAUTO","0");
                lightOnCommandData.put("controlSence","all");
                lightOnCommandData.put("houseDBName",houseDBName);
                String lightJson = CommandJsonUtils.getCommandJson(0,lightOnCommandData,"1","2","3", String.valueOf(System.currentTimeMillis()));
                Log.i(TAG, "control: -----------------"+lightJson);
                tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    //控制窗帘
    // -----------------------------------
    private void controlCurtain(String controlSence,String houseDBName){

            JSONObject lightOnCommandData=new JSONObject();
            JSONObject controlData=new JSONObject();
            try {
                lightOnCommandData.put("controlProtocol","hdl");
                lightOnCommandData.put("machineName","curtain");
                lightOnCommandData.put("controlData",controlData);
                controlData.put("","");
                controlData.put("","");
                lightOnCommandData.put("controlSence",controlSence);
                lightOnCommandData.put("houseDBName",houseDBName);
                String lightJson = CommandJsonUtils.getCommandJson(0,lightOnCommandData,"1","2","3", String.valueOf(System.currentTimeMillis()));
                Log.i(TAG, "control:窗帘 -----------------"+lightJson);
                tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
    //-------------------------------------各种模式----------------------------------------
    private void controlModel(String controlSence,String houseDBName){

        JSONObject lightOnCommandData=new JSONObject();
        JSONObject controlData=new JSONObject();
        try {
            lightOnCommandData.put("controlProtocol","midea");
            lightOnCommandData.put("machineName","model");
            lightOnCommandData.put("controlData",controlData);
            controlData.put("","");
            controlData.put("","");
            lightOnCommandData.put("controlSence",controlSence);
            lightOnCommandData.put("houseDBName",houseDBName);
            String lightJson = CommandJsonUtils.getCommandJson(0,lightOnCommandData,"1","2","3", String.valueOf(System.currentTimeMillis()));
            Log.i(TAG, "control:拜爱模式 -----------------"+lightJson);
            tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
