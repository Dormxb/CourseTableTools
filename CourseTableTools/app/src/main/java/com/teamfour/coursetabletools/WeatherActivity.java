package com.teamfour.coursetabletools;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.teamfour.coursetabletools.Weather.CityList;
import com.teamfour.coursetabletools.Weather.RcvClickAdapter;
import com.teamfour.coursetabletools.Weather.WeatherDetailActivity;
import com.zaaach.citypicker.CityPickerActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;

public class WeatherActivity extends MainActivity implements RcvClickAdapter.OnItemClickListener{

    private static final int REQUEST_CODE_PICK_CITY = 0;
    private static final int LOCATION_CODE = 1;
    private LocationManager lm;//【位置管理】
    private List<CityList> mList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;//获取编辑器
    private Set<String> stored_city = new HashSet<String>();
    RcvClickAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initializeToolbar();

        Window window = getWindow();
        //After LOLLIPOP not translucent status bar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //Then call setStatusBarColor.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        sharedPreferences = getSharedPreferences("stored_city", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        HeConfig.init("HE1803061521401123", "0e6229b55b7f48c8a7b1a7a17585c2ab");
        HeConfig.switchToFreeServerNode();
        quanxian();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(WeatherActivity.this, CityPickerActivity.class),
                        REQUEST_CODE_PICK_CITY);
            }
        });

        initData();
        initView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //重写onActivityResult方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_CITY && resultCode == RESULT_OK) {
            if (data != null) {
                String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                if (!stored_city.contains(city)) {
                    stored_city.add(city);
                    initView();
                    CityList cityList = new CityList();
                    cityList.setCity(city);
                    mList.add(cityList);
                }
                //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
                Intent intent = new Intent(WeatherActivity.this, WeatherDetailActivity.class);

                //用Bundle携带数据
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putString("city", city);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        }
    }

    public void quanxian() {
        lm = (LocationManager) WeatherActivity.this.getSystemService(WeatherActivity.this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("BRG", "没有权限");
                // 没有权限，申请权限。
                // 申请授权。
                ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);

            } else {
            }
        } else {
            Log.e("BRG", "系统检测到未开启GPS定位服务");
            Toast.makeText(getApplicationContext(), "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。

                } else {
                    // 权限被用户拒绝了。
                    Toast.makeText(getApplicationContext(), "定位权限被禁止，相关地图功能无法使用！", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initData() {

        Set res = new HashSet();
        stored_city = sharedPreferences.getStringSet("stored_city", res);
        for (String str : stored_city) {
            System.out.println(str);
            CityList cityList = new CityList();
            cityList.setCity(str);
            mList.add(cityList);
        }
        if (mList.size() == 0) {
            startActivityForResult(new Intent(WeatherActivity.this, CityPickerActivity.class),
                    REQUEST_CODE_PICK_CITY);
        }
    }

    private void initView() {
        adapter = new RcvClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.cityRV);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(CityList content) {
        Toast.makeText(this, content.getCity() + "已被移除", Toast.LENGTH_SHORT).show();
        mList.remove(mList.indexOf(content));
        stored_city.remove(content.getCity());
        if (mList.size() == 0) {
            startActivityForResult(new Intent(WeatherActivity.this, CityPickerActivity.class),
                    REQUEST_CODE_PICK_CITY);
        }
        initView();

    }

    @Override
    protected void onStop() {
        editor.clear();
        editor.putStringSet("stored_city", stored_city);
        editor.commit();

        super.onStop();
    }

    private static String getLogcatInfo() {
        String strLogcatInfo = "";
        Process process;
        try {
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-d");

            commandLine.add("*:E"); // 过滤所有的错误信息

            ArrayList<String> clearLog = new ArrayList<String>();  //设置命令  logcat -c 清除日志
            clearLog.add("logcat");
            clearLog.add("-c");

            process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
                strLogcatInfo = strLogcatInfo + line + "\n";
            }

            bufferedReader.close();
        } catch (Exception ex) {
            strLogcatInfo = strLogcatInfo + ex + "\n";
        }
        return strLogcatInfo;
    }
}
