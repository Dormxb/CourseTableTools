package com.teamfour.coursetabletools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by teamfour on 19/5/9.
 */
public class LoginActivity extends AppCompatActivity {

    EditText user,passwd;
    Button button;
    String username,password;
    String token,timeout;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        user = findViewById(R.id.login_user);
        passwd = findViewById(R.id.login_password);
        button = findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = user.getText().toString();
                password = passwd.getText().toString();
//                测试内容
                int usernameLen = username.length();
                int passwordLen = password.length();
                if(usernameLen == 0){
                    Toast.makeText(getApplicationContext(),"用户名、密码未输入！",Toast.LENGTH_LONG).show();
                }else if(passwordLen == 0){
                    Toast.makeText(getApplicationContext(),"密码未输入！",Toast.LENGTH_LONG).show();
                }else{
                    //延时获取
                    try{
                        Thread.sleep(5000);
                    }catch (Exception e) {

                    }
                    postLogin(username,password);
                    Intent intent = new Intent(LoginActivity.this,CourseActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //通过okhttp3访问数据库
    //网络请求服务器数据，返回登录用户的token，用于获取课表及个人信息
    public String postLogin(String username,String password){
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.通过new FormBody()调用build方法,创建一个RequestBody,可以用add添加键值对
        RequestBody requestBody = new FormBody.Builder().add("username",username).add("password",password).build();
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://haokanghao123.xicp.io:58144/myblog/login/").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                timeout = e.getMessage();
                if(timeout.equals("timeout")){
                    Log.e("qwe","网络请求超时");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                token = response.body().string();
                if(token.equals("error") )
                {
                    Log.e("yui","错误");
                }
                else{
                    Log.e("yui","成功");
                }

            }
        });
        return token;
    }
}
