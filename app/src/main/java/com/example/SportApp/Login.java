package com.example.SportApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private CustomVideoView videoView; //视频背景
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //加载视频数据
        initView();

        //部件
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        CheckBox remember_checkbox = findViewById(R.id.remember_checkbox);
        Button login_button = findViewById(R.id.login_button);
        TextView to_register_button = findViewById(R.id.to_register_button);
//        View login_layout = findViewById(R.id.login_layout);
//        login_layout.getBackground().setAlpha(100);


        //没有账号，前往注册
        to_register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegister = new Intent(Login.this, Register.class);
                startActivity(toRegister);
                finish();
            }
        });

        //使用SharedPreferences实现记住密码
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Login.this);
        final SharedPreferences.Editor editor = sp.edit();
        //配置sp
        boolean isRemember = sp.getBoolean("remember password",false);
        if (isRemember){
            String Username = sp.getString("username","");
            String password = sp.getString("password","");
            editTextUsername.setText(Username);
            editTextPassword.setText(password);
            remember_checkbox.setChecked(true);
        }

        //登陆事件
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户输入的用户名密码
                String inputUsername = editTextUsername.getText().toString().trim();
                String inputPassword = editTextPassword.getText().toString().trim();

                //数据库
                MyDatabaseHelper databaseHelper = new MyDatabaseHelper(Login.this,"SportDatabase.db",null,1);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                Cursor getPassword = db.query("user", new String[]{"password"}, "user_name=?", new String[]{inputUsername}, null, null, null);
                Cursor getID = db.query("user", new String[]{"_id"}, "user_name=?", new String[]{inputUsername}, null, null, null);
                //Cursor getID = db.rawQuery("SELECT _id FROM user WHERE user_name = '" + inputUsername + "';", null);
                if (getPassword.moveToNext()){
                    String psw_query = getPassword.getString(getPassword.getColumnIndex("password"));
                    //密码匹配
                    if(inputPassword.equals(psw_query)){
                        //记住密码
                        if (remember_checkbox.isChecked()){
                            editor.putBoolean("remember password",true);
                            editor.putString("username",inputUsername);
                            editor.putString("password",inputPassword);
                        }
                        else {
                            editor.clear();
                        }
                        editor.commit();

                        //跳转到主页，传输登陆用户的ID
                        Intent toMainActivity = new Intent(Login.this,MainActivity.class);

                        if (getID.moveToNext()){
                            int id_query = getID.getInt(getID.getColumnIndex("_id"));
                            toMainActivity.putExtra("user_id",id_query);
                            startActivity(toMainActivity);
                            Toast.makeText(Login.this,"欢迎您，" + inputUsername, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    //密码错误
                    else {
                        //记住账号
                        if (remember_checkbox.isChecked()){
                            editor.putBoolean("remember password",true);
                            editor.putString("username",inputUsername);
                            editor.putString("password","");
                        }
                        else {
                            editor.clear();
                        }
                        editor.commit();
                        Toast.makeText(Login.this,"登陆失败，密码错误",Toast.LENGTH_SHORT).show();
                        editTextPassword.setText(null);
                    }
                }

                //未注册
                else{
                    Toast.makeText(Login.this, "账号不存在，请先注册", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //失去焦点时收起软键盘
        RelativeLayout layout = findViewById(R.id.login_layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) Login.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Login.this.getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });
    }

    /******************************视频背景******************************/
    private void initView() {
        //加载视频资源控件
        videoView = (CustomVideoView) findViewById(R.id.videoView);
        //设置播放加载路径
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background));
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