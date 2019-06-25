package com.teamfour.coursetabletools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountActivity extends MainActivity {

    private String TAG = "Account";
    private String token,timeout;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private boolean loading;
    private TextView account_name,account_num,account_college,account_profession;
    private String acount_Name,acount_Id,acount_College,acount_Major;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initializeToolbar();

        imageView = findViewById(R.id.account_headimage);
        Picasso.with(this).load("http://kingzr96269.cn/13.png").into(imageView);
        account_name = findViewById(R.id.account_name);
        account_num = findViewById(R.id.account_num);
        account_college = findViewById(R.id.account_college);
        account_profession = findViewById(R.id.account_profession);

//        Intent intent = new Intent(AccountActivity.this,LoginActivity.class);
//        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loading=false;
        buildProgressDialog();
        //延时获取
        try{
            Thread.sleep(500);
        }catch (Exception e) {

        }
        postQuery();
        //延时获取
        try{
            Thread.sleep(500);
        }catch (Exception e) {

        }
        account_name.setText(acount_Name);
        account_num.setText(acount_Id);
        account_college.setText(acount_College);
        account_profession.setText(acount_Major);
    }

    //发起网络请求
    public String postQuery(){
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        String t = "ZXlKMGVYQWlPaUpLVjFBaUxDSmhiR2NpT2lKa1pXWmhkV3gwSW4wOjFoS2U2TTpOMUtpYjNORUVHcTA3UnhiaGc1S2Q3SWVZRWs.ZXlKdWRXMWlaWElpT2lJeE5qZ3hNVEUxTkRFeE1qa2lmUToxaEtlNk06ZmwwRjhldEdsc2w3d1cyNDdqU0g1ZTl0M3ZZ.571b306e50fb28b3d2d680e4dc26d45b";
        RequestBody requestBody = new FormBody.Builder().add("token",t).build();
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://haokanghao123.xicp.io:58144/myblog/get_message/").post(requestBody).build();
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
            for (int a = 0; a < len; a++) {
                JSONObject info = jsonObject.getJSONObject(a);
                acount_Name = info.getString("name");
                acount_Id = info.getString("id");
                acount_College = info.getString("college");
                acount_Major = info.getString("major");
            }
            loading = true;
            if (loading) {
                cancelProgressDialog();
            }
        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }
    }

    /**
     * 加载框
     */
    public void buildProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage("正在获取个人信息...");
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
