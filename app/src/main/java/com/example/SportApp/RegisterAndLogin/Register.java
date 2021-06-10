package com.example.SportApp.RegisterAndLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SportApp.MyDatabaseHelper;
import com.example.SportApp.R;

public class Register extends AppCompatActivity {

    private CustomVideoView videoView; //视频背景

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //加载视频数据
        initView();

        //部件
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        TextView to_login_button = findViewById(R.id.to_login_button);
        Button register_button = findViewById(R.id.register_button);
        TextView check_name_button = findViewById(R.id.check_name_button);

        //数据库
        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(Register.this,"SportDatabase.db",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //已有帐号，前往登录
        to_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(Register.this, Login.class);
                startActivity(toLogin);
                finish();
            }
        });

        //检查重名
        check_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户输入的用户名及密码
                String inputUsername = editTextUsername.getText().toString().trim();
                String inputPassword = editTextPassword.getText().toString().trim();

                //检查重名
                Cursor getName = db.rawQuery("SELECT user_name FROM user WHERE user_name = '" + inputUsername + "';", null);
                if (getName.moveToNext()){
                    String current_user = getName.getString(getName.getColumnIndex("user_name"));
                    editTextUsername.setText(null);
                    Toast.makeText(Register.this,current_user + "已存在，请更换用户名",Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(inputUsername)){
                    Toast.makeText(Register.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Register.this,"用户名可用",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //注册事件
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户输入的用户名及密码
                String inputUsername = editTextUsername.getText().toString().trim();
                String inputPassword = editTextPassword.getText().toString().trim();

                //检查重名
                Cursor getName = db.rawQuery("SELECT user_name FROM user WHERE user_name = '" + inputUsername + "';", null);
                if (getName.moveToNext()){
                    String current_user = getName.getString(getName.getColumnIndex("user_name"));
                    editTextUsername.setText(null);
                    Toast.makeText(Register.this,current_user + "已存在，请更换用户名",Toast.LENGTH_SHORT).show();
                }

                //检查空值
                else if (TextUtils.isEmpty(inputUsername) || TextUtils.isEmpty(inputPassword)){
                    Toast.makeText(Register.this, "用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                }

                else{
                    db.execSQL("insert into user(_id, user_name, password)values(NULL,'" + inputUsername +"','" + inputPassword + "');" +
                            "select last_insert_rowid() newid;");
                    Toast.makeText(Register.this,"注册成功！请登录",Toast.LENGTH_SHORT).show();
                    Intent toLogin = new Intent(Register.this, Login.class);
                    startActivity(toLogin);
                    finish();
                }

            }
        });

        //失去焦点时收起软键盘
        RelativeLayout layout = findViewById(R.id.register_layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) Register.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Register.this.getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });
    }

    /******************************视频背景******************************/
    private void initView() {
        //加载视频资源控件
        videoView = findViewById(R.id.videoView);
        //设置播放加载路径
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background2));
        //播放
        videoView.start();
        //循环播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    //返回重启加载
    @Override
    protected void onRestart() {
        initView();
        super.onRestart();
    }

    //防止锁屏或者切出的时候持续播放
    @Override
    protected void onStop() {
        videoView.stopPlayback();
        super.onStop();
    }
}