package com.byids.hy.smarttable;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.smarttable.adapter.MyBaseAdapter;
import com.byids.hy.smarttable.bean.TableModel;
import com.github.glomadrian.dashedcircularprogress.DashedCircularProgress;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity {
    private String TAG = "result";
    private int flag = 0;

    private ListView listView;
    private MyBaseAdapter adapter;
    private RadioGroup radioGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private TableModel tableModel;
    private List<TableModel> list;
    private List<TableModel> beforeList;
    private String[] keTingModel = {"会客模式","影音模式","用餐模式","离开模式","     空调    "};
    private int[] ketTingImg = {R.mipmap.meet_friend_icon,R.mipmap.medium_icon,R.mipmap.eating_icon,R.mipmap.leave_icon,R.mipmap.aircondition_icon};
    private String[] woShiModel = {"工作模式","安眠模式","音乐模式","阅读模式","离开模式"};
    private String[] erTongFangModel = {"学习模式","午睡模式","娱乐模式","影音模式","离开模式"};
    private final String KETING = "会客模式";
    private final String WOSHI = "工作模式";
    private final String ERTONGFANG = "学习模式";
    private PopupWindow popupWindow;

    LinearLayout linearData1;
    LinearLayout linearData2;
    LinearLayout linearlist;
    ImageView ivLogo;

    private TextView tvTime;
    private TextView tvWeek;
    private TextView tvDate;
    private TextView tvWaterTemp;//显示水温
    private TextView tvScreen;//屏幕

    private Button restartButton;
    private DashedCircularProgress dashedCircularProgress;
    private ImageView androidImage;
    int waterTemp = 0;//水温

    //空调dialog控件
    private ImageView ivClose;
    private CheckBox tvAirSwitch;
    private RadioGroup rgAir;
    private RadioButton tvAirCold;
    private RadioButton tvAirAuto;
    private RadioButton tvAirHumi;
    private RadioButton tvAirHot;
    //加湿器dialog
    private ImageView ivHumClose;
    private CheckBox tvHumSwitch;
    private CheckBox tvHumAuto;
    //净化器dialog控件
    private ImageView ivCleanerClose;
    private CheckBox cbCleanerSwitch;
    private RadioGroup rgCleaner;
    private RadioButton rbLizi;
    private RadioButton rbAuto;
    private RadioButton rbSleep;
    private RadioButton rbTimer;
    private Dialog dialog1;
    private Dialog dialog2;
    private Dialog dialog3;

    private Thread aThread;
    private Thread bThread;

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
                    Log.i(TAG, "handleMessage: ------"+waterTemp);
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
                    Log.i(TAG, "handleMessage: ------"+waterTemp);
                    tvWaterTemp.setText(waterTemp+"℃");
                    tvWaterTemp.postInvalidate();
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

        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);

        setContentView(R.layout.activity_table);
        initData();
        initView();
        getCurrentTime();
        // 通过WindowManager获取
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Log.i(TAG, "onCreate: ---------heigth"+dm.heightPixels);
        Log.i(TAG, "onCreate: ---------width"+dm.widthPixels);
        Log.i(TAG, "onCreate: ---------屏幕密度"+density);
        Log.i(TAG, "onCreate: ----------Dpi"+densityDpi);

    }

    private void initData() {
        list = new ArrayList<TableModel>();
        tableModel = new TableModel("会客模式",R.mipmap.meet_friend_icon);
        list.add(tableModel);
        tableModel = new TableModel("影音模式",R.mipmap.medium_icon);
        list.add(tableModel);
        tableModel = new TableModel("用餐模式",R.mipmap.eating_icon);
        list.add(tableModel);
        tableModel = new TableModel("离开模式",R.mipmap.leave_icon);
        list.add(tableModel);
        tableModel = new TableModel("     空调    ",R.mipmap.aircondition_icon);
        list.add(tableModel);
    }

    private void initView() {
         linearData1 = (LinearLayout) findViewById(R.id.linear_air_group);
         linearData2 = (LinearLayout) findViewById(R.id.linear_data);
         linearlist = (LinearLayout) findViewById(R.id.linear_list);
         ivLogo = (ImageView) findViewById(R.id.logo);

        tvScreen = (TextView) findViewById(R.id.tv_screen);
        tvScreen.setOnClickListener(screenSwitch);
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
        dashedCircularProgress = (DashedCircularProgress) findViewById(R.id.size);

        restartButton = (Button) findViewById(R.id.restart);
        dashedCircularProgress.setInterpolator(new AccelerateInterpolator());
        //dashedCircularProgress.setProgressColor(R.color.colorPrimary);
        dashedCircularProgress.setValue(0);//设置进度条初始加到的百分度   0-10

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag==0){
                    animateUp();
                    changeWaterTempUp();
                }else if (flag==1){

                    animateDown();
                    changeWaterTempDown();
                }
            }
        });
        initDialog();
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
        aThread = new Thread(){
            @Override
            public void run() {
                while (true) {
                    super.run();
                    if (waterTemp < 80) {           //设定水温
                        waterTemp++;
                    }
                    Message message = new Message();
                    message.what = 2;
                    message.obj = waterTemp;
                    handler1.sendMessage(message);

                    try {
                        Thread.sleep(75);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        aThread.start();
    }
    //水温数值下降
    private void changeWaterTempDown(){
        bThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    super.run();
                    if (waterTemp > 0) {           //设定水温
                        Log.i(TAG, "run: ----909009------"+waterTemp);
                        waterTemp = waterTemp-1;
                    }
                    Message message = new Message();
                    message.what = 2;
                    message.obj = waterTemp;
                    handler2.sendMessage(message);

                    try {
                        Thread.sleep(75);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        bThread.start();
    }



    private void animateUp() {
        flag = 1;
        //dashedCircularProgress.reset();
        dashedCircularProgress.setValue(8);
        //dashedCircularProgress.setProgressColor(R.color.colorPrimary);//设置进度条颜色
    }
    private void animateDown(){
        flag = 0;
        dashedCircularProgress.setValue(0);
    }


    //模式点击  listview item click
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //intentToDialog();   //跳转二级界面
            switch (list.get(0).getModelText()){
                case KETING:
                    adapter.changeSelected(position);
                    intentToAirDialog();
                    break;
                case WOSHI:
                    adapter.changeSelected(position);
                    intentToHumiDialog();
                    break;
                case ERTONGFANG:
                    adapter.changeSelected(position);
                    intentToCleanerDialog();
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
                        refreshData(keTingModel,ketTingImg,1);
                        rb1.setTextColor(MainActivity.this.getResources().getColor(R.color.colorText));
                    rb2.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    rb3.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    break;
                case R.id.rb_woshi:
                        refreshData(woShiModel,ketTingImg,2);
                        rb2.setTextColor(MainActivity.this.getResources().getColor(R.color.colorText));
                    rb1.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    rb3.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    break;
                case R.id.rb_ertongfang:
                        refreshData(erTongFangModel,ketTingImg,3);
                        rb3.setTextColor(MainActivity.this.getResources().getColor(R.color.colorText));
                    rb1.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    rb2.setTextColor(MainActivity.this.getResources().getColor(R.color.room_color));
                    break;
            }
        }
    };

    //点击切换房间
    private void refreshData(String[] model,int[] img,int room){
        //切换房间时 淡入淡出动画
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        listView.startAnimation(animationSet);

        beforeList = new ArrayList<TableModel>();
        for (int i=0;i<model.length;i++){
            tableModel = new TableModel(model[i],img[i],room);
            beforeList.add(tableModel);
        }
        list.clear();
        list.addAll(beforeList);
        adapter.changeRooms();
        adapter.notifyDataSetChanged();
    }

    /*
    *
    * ---------------------------------二级界面-----------------------------------------
    *
    * */

    //-----------------------空调dialog初始化------------------------
    private void intentToAirDialog(){
        dialog1.show();
    }

    //-----------------------加湿器dialog初始化------------------------
    private void intentToHumiDialog(){
        dialog2.show();
    }

    //---------------------------净化器dialog初始化------------------------------
    private void intentToCleanerDialog(){
        dialog3.show();
    }

    private void initDialog(){
        dialog1 = new Dialog(this,R.style.CustomDialog);
        View view1 = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout,null);
        ivClose = (ImageView) view1.findViewById(R.id.iv_close);
        tvAirSwitch = (CheckBox) view1.findViewById(R.id.iv_aircondition_turn);
        rgAir = (RadioGroup) view1.findViewById(R.id.rg_air);
        tvAirCold = (RadioButton) view1.findViewById(R.id.iv_aircondition_cold);
        tvAirAuto = (RadioButton) view1.findViewById(R.id.iv_aircondition_auto);
        tvAirHumi = (RadioButton) view1.findViewById(R.id.iv_aircondition_humi);
        tvAirHot = (RadioButton) view1.findViewById(R.id.iv_aircondition_hot);
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
        params.height = 400;
        Window mWindow = dialog1.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(params);


        dialog2 = new Dialog(this,R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.humidifier_dialog_layout,null);
        ivHumClose = (ImageView) view.findViewById(R.id.iv_hum_close);
        tvHumSwitch = (CheckBox) view.findViewById(R.id.tv_hum_switch);
        tvHumAuto = (CheckBox) view.findViewById(R.id.tv_hum_auto);
        tvHumSwitch.setOnCheckedChangeListener(humSwitchCheckedListener);
        tvHumAuto.setOnCheckedChangeListener(humAutoCheckedListener);
        ivHumClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.hide();
            }
        });
        dialog2.setContentView(view);
        dialog2.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params2 = dialog2.getWindow().getAttributes();
        params.width = 1200;
        params.height = 400;
        Window mWindow2 = dialog2.getWindow();
        mWindow2.setGravity(Gravity.BOTTOM);
        mWindow2.setAttributes(params2);


        dialog3 = new Dialog(this,R.style.CustomDialog);
        View view2 = LayoutInflater.from(this).inflate(R.layout.cleaner_dialog_layout,null);

        ivCleanerClose = (ImageView) view2.findViewById(R.id.iv_cleaner_close);
        cbCleanerSwitch = (CheckBox) view2.findViewById(R.id.cb_switch);
        rgCleaner = (RadioGroup) view2.findViewById(R.id.rg_cleaner);
        rgCleaner.setOnCheckedChangeListener(cleanerCheckListener);
        ivCleanerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.hide();
            }
        });
        dialog3.setContentView(view2);
        dialog3.setCanceledOnTouchOutside(true);//点击外部，弹框消失
        WindowManager.LayoutParams params3 = dialog3.getWindow().getAttributes();
        params.width = 1200;
        params.height = 400;
        Window mWindow3 = dialog3.getWindow();
        mWindow3.setGravity(Gravity.BOTTOM);
        mWindow3.setAttributes(params3);
    }



    //---------------------------------各种控制-----------------------------
    //空调控制
    RadioGroup.OnCheckedChangeListener airChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.iv_aircondition_cold:
                    Toast.makeText(MainActivity.this, "1111", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_aircondition_auto:
                    Toast.makeText(MainActivity.this, "1121", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_aircondition_humi:
                    Toast.makeText(MainActivity.this, "1131", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_aircondition_hot:
                    Toast.makeText(MainActivity.this, "1141", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    //加湿器控制  开关
    CompoundButton.OnCheckedChangeListener humSwitchCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    };

    //加湿器  自动
    CompoundButton.OnCheckedChangeListener humAutoCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    };

    //净化器控制
    RadioGroup.OnCheckedChangeListener cleanerCheckListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_lizi:
                    Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rb_auto:
                    Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rb_sleep:
                    Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rb_timer:
                    Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    /*
    *
    * --------------------------------------------------------------------------
    *
    * */
    //设置Activity背景透明度，明暗度
    private void setScreenStyle(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        lp.dimAmount = 1.0f;
        getWindow().setAttributes(lp);
    }

    //listview上下的按钮点击事件
    public void upORDown(View v){
        switch (v.getId()){
            case R.id.iv_up:
                listView.setSmoothScrollbarEnabled(true);
                listView.smoothScrollToPositionFromTop(0,2,1000);
                break;
            case R.id.iv_down:
                listView.setSmoothScrollbarEnabled(true);
                listView.smoothScrollToPositionFromTop(9,2,1000);
                break;
        }
    }

    int screenFlag = -1;
    View.OnClickListener screenSwitch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (screenFlag==0){
                ivLogo.setVisibility(View.INVISIBLE);
                radioGroup.setVisibility(View.INVISIBLE);
                dashedCircularProgress.setVisibility(View.INVISIBLE);
                linearData1.setVisibility(View.INVISIBLE);
                linearData2.setVisibility(View.INVISIBLE);
                linearlist.setVisibility(View.INVISIBLE);
                screenFlag = 1;
            }else if (screenFlag==1){
                ivLogo.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.VISIBLE);
                dashedCircularProgress.setVisibility(View.VISIBLE);
                linearData1.setVisibility(View.VISIBLE);
                linearData2.setVisibility(View.VISIBLE);
                linearlist.setVisibility(View.VISIBLE);
                screenFlag = 0;
            }
        }
    };
}
