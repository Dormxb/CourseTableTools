package com.teamfour.coursetabletools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;
import com.teamfour.coursetabletools.UI.AbsGridAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CourseActivity extends MainActivity {

    private static String TAG = "Course";
    private Spinner spinner;
    private GridView detailCource;
    private String[][] contents= new String [6][7];
    private AbsGridAdapter Adapter;
    private List<String> dataList;
    private ArrayAdapter<String> spinnerAdapter;
    private String token,timeout;
    private ProgressDialog progressDialog;
    private boolean loading;

    //获取当前学期的第几周
    LocalDate start = LocalDate.of(2019,2,25);
    WeekFields week = WeekFields.of(Locale.getDefault());
    int startWeek = start.get(week.weekOfWeekBasedYear());
    LocalDate today = LocalDate.now();
    WeekFields weekFields = WeekFields.of(Locale.getDefault());
    int todayWeek = today.get(weekFields.weekOfWeekBasedYear());
    int weeks = todayWeek-startWeek+1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initializeToolbar();

        spinner = findViewById(R.id.switchWeek);
        loadspinner();
        detailCource = findViewById(R.id.courceDetail);

        fillStringArray();
        postQuery();

    }

    @Override
    protected void onStart() {
        super.onStart();
        loading = false;
        buildProgressDialog();
        //延时获取
        try{
            Thread.sleep(1000);
        }catch (Exception e) {

        }
        loadcourrse();
    }

    //加载空课程信息
    public void fillStringArray() {
        for(int i=0;i<6;i++)
            for(int j=0;j<7;j++)
                contents[i][j] = "";
    }

    //发起网络请求
    public String postQuery(){
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        String t = "ZXlKMGVYQWlPaUpLVjFBaUxDSmhiR2NpT2lKa1pXWmhkV3gwSW4wOjFoS2U2TTpOMUtpYjNORUVHcTA3UnhiaGc1S2Q3SWVZRWs.ZXlKdWRXMWlaWElpT2lJeE5qZ3hNVEUxTkRFeE1qa2lmUToxaEtlNk06ZmwwRjhldEdsc2w3d1cyNDdqU0g1ZTl0M3ZZ.571b306e50fb28b3d2d680e4dc26d45b";
        RequestBody requestBody = new FormBody.Builder().add("token",t).build();
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://haokanghao123.xicp.io:58144/myblog/query").post(requestBody).build();
        //Log.e(TAG,request.toString());
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                try{
                    timeout = e.getMessage();
                    if(timeout.equals("timeout")){
                        Log.e(TAG,"网络请求超时");
                    }
                }catch (Exception ex){
                    showErrorDialog(ex);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                token = response.body().string();
                Log.e(TAG,token);
                parseJsonObject(token);
            }
        });
        return token;
    }

    //处理json数据
    private void parseJsonObject(String Data){
        try{
            // 解析接收的JSON字符串
            JSONArray jsonObject = new JSONArray(Data);
            // 接收JSON对象里的数组
            // 获取数组长度
            int len = jsonObject.length();
            // 通过循环取出数组里的值
            for(int i=0;i<6;i++)
                for(int j=0;j<7;j++){
                    for (int a = 0; a < len; a++) {
                        JSONObject info = jsonObject.getJSONObject(a);
//                        String Course_num = info.getString("KCH");
                        String Course_name = info.getString("KCM");
//                        String Course_teacher = info.getString("teacher");
//                        String Course_week = info.getString("ZC");
                        String Course_weeks = info.getString("time");
                        String Course_location = info.getString("addres");
                        int Course_weekdetial = info.getInt("xq");
                        String [] Course_time = info.getString("xj").split("-");
                        //1 2 3 4 5 6
                        String []aa =Course_weeks.replace("[","").replace("]","").split(",");
                        if((j+1==Course_weekdetial) && i+1== (Integer.parseInt(Course_time[1]))/2) {
                            for(String c :aa ) {
                                if(c.equals(7+"")) {
                                    contents[i][j]=Course_name+"\n"+Course_location;
                                    break;
                                }
                            }
                        }
                    }
                }
            loading = true;
            if (loading) {
                cancelProgressDialog();
            }
        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }
    }

    //创建课程信息
    public void loadcourrse(){
        Adapter = new AbsGridAdapter(this);
        Adapter.setContent(contents, 6, 7);
        detailCource.setAdapter(Adapter);
    }

    //创建Spinner数据
    public void loadspinner(){

        //第几周
        dataList = new ArrayList<>();
        for(int i = 1; i < 21; i++) {
            dataList.add("第" + i + "周");
        }
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dataList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weeks = position + 1;

                Toast.makeText(getApplicationContext(),"选择了" + dataList.get(position) + "",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        progressDialog.setMessage("正在获取课程信息...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * 取消加载框
     */
    public void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }

    private void showErrorDialog(Exception e) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        normalDialog.setTitle("没有网络");
        normalDialog.setMessage("获取资源失败\n"+e.getMessage());
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
}
