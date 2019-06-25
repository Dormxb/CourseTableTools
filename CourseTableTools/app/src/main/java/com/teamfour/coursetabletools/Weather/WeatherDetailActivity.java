package com.teamfour.coursetabletools.Weather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teamfour.coursetabletools.R;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class WeatherDetailActivity extends AppCompatActivity {

    private String city = "上海";
    private ProgressDialog progressDialog;
    private boolean Z1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String name = bundle.getString("city");
        Log.i("获取到的city值为",name);
        city=name;

        Z1 = false;
        buildProgressDialog();

        //初始化控件
        final TextView Today =  findViewById(R.id.Today);
        final TextView Tmp = findViewById(R.id.Tmp);
        final TextView Feel =  findViewById(R.id.Feel);
        final TextView weatherTxt = findViewById(R.id.weatherTxt);
        final TextView visNum = findViewById(R.id.visNum);
        final TextView HumNum = findViewById(R.id.HumNum);
        final TextView UpdateTime = findViewById(R.id.UpdateTime);
        final TextView windir = findViewById(R.id.windir);
        final TextView windspd = findViewById(R.id.windspd);
        final TextView windsc = findViewById(R.id.windsc);
        final ImageView imageView = findViewById(R.id.weatherImg);
        final TextView Pcpn = findViewById(R.id.Pcpn);
        final TextView Press = findViewById(R.id.Press);
        final TextView getCloud = findViewById(R.id.getCloud);

        /**
         * 实况天气
         * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
         * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
         *
         * @param context  上下文
         * @param location 地址详解
         * @param lang     多语言，默认为简体中文
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
         */
        HeWeather.getWeatherNow(WeatherDetailActivity.this, city, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable e) {
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Now dataObject) {
                //下面开始放数据
                String a = dataObject.getNow().getCond_txt();
                int b = Integer.parseInt(dataObject.getNow().getTmp());
                if (a.equals("多云")) {
                    imageView.setImageResource(R.mipmap.icons8_partly_cloudy_day);
                } else if (a.equals("晴")) {
                    imageView.setImageResource(R.mipmap.icons8_sun);
                } else if (a.equals("阴")) {
                    imageView.setImageResource(R.mipmap.icons8_cloud);
                } else if (a.contains("雨")) {
                    imageView.setImageResource(R.mipmap.icons8_rainy_weather);
                } else if (a.contains("雾")) {
                    imageView.setImageResource(R.mipmap.icons8_fog_day);
                } else if (a.contains("雪")) {
                    imageView.setImageResource(R.mipmap.icons8_snow_storm);
                } else if (a.contains("雷")) {
                    imageView.setImageResource(R.mipmap.icons8_storm);
                } else if (a.contains("冰雹")) {
                    imageView.setImageResource(R.mipmap.icons8_hail);
                } else {
                    imageView.setImageResource(R.drawable.ic_warning_black_24dp);
                }

                if (a.equals("多云") && (b >= 18 && b <= 30)) {
                    Feel.setText("较舒适");
                } else if (a.equals("阴")) {
                    Feel.setText("比较闷");
                } else if (a.contains("雨")) {
                    Feel.setText("较潮湿");
                } else if (a.contains("雾")) {
                    Feel.setText("较潮湿");
                } else if (a.contains("雪")) {
                    Feel.setText("较寒冷");
                } else if (a.contains("雷")) {
                    Feel.setText("较潮湿");
                } else if (a.contains("冰雹")) {
                    Feel.setText("较寒冷");
                } else if (b < 10) {
                    Feel.setText("较寒冷");
                } else if (b > 30) {
                    Feel.setText("较炎热");
                } else {
                    Feel.setText("较舒适");
                }

                Today.setText(dataObject.getUpdate().getLoc().substring(0, 10));
                Tmp.setText(dataObject.getNow().getTmp() + "°C");
                weatherTxt.setText(dataObject.getNow().getCond_txt());
                visNum.setText(dataObject.getNow().getVis() + "Km");
                HumNum.setText(dataObject.getNow().getHum());
                UpdateTime.setText(dataObject.getUpdate().getLoc());
                windir.setText(dataObject.getNow().getWind_dir());
                windspd.setText(dataObject.getNow().getWind_spd() + " Km/h");
                windsc.setText(dataObject.getNow().getWind_sc() + " 级");
                Pcpn.setText(dataObject.getNow().getPcpn() + " mm");
                Press.setText(dataObject.getNow().getPres() + " Pa");
                getCloud.setText(dataObject.getNow().getCloud());

                Z1 = true;

                if (Z1) {
                    cancelProgressDialog();
                }

                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(dataObject.getStatus())) {
                    //此时返回数据
                    NowBase now = dataObject.getNow();
                } else {
                    //在此查看返回数据失败的原因
                    String status = dataObject.getStatus();
                    Code code = Code.toEnum(status);
                }
            }
        });

    }

    /**
     * 加载框
     */
    public void buildProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage("正在获取天气资源...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * @Description: TODO 取消加载框
     */
    public void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }

    private void showErrorDialog(Throwable e) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        normalDialog.setTitle("没有网络");
        normalDialog.setMessage("获取资源失败\n"+e);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        finish();
                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
