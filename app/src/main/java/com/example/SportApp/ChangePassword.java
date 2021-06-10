package com.example.SportApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.SportApp.RegisterAndLogin.Login;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
//        if (NavUtils.getParentActivityName(ChangePassword.this) != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }

        //获取当前用户ID
        Intent intent = getIntent();
        int current_user_id = intent.getIntExtra("current_user_id",0);

        //部件
        EditText edit_original_password = findViewById(R.id.edit_original_password);
        EditText edit_new_password = findViewById(R.id.edit_new_password);
        EditText edit_new_password_again = findViewById(R.id.edit_new_password_again);
        Button submit_button = findViewById(R.id.summit_button);

        //数据库
        MyDatabaseHelper databaseHelper;
        databaseHelper = new MyDatabaseHelper(ChangePassword.this,"SportDatabase.db",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputOriginalPassword = edit_original_password.getText().toString();
                String inputNewPassword = edit_new_password.getText().toString();
                String inputNewPasswordAgain = edit_new_password_again.getText().toString();

                //检查原密码是否正确
                Cursor getPassword = db.rawQuery("SELECT password FROM user WHERE _id = " + current_user_id + ";", null);
                if (getPassword.moveToNext()){
                    String psw_query = getPassword.getString(getPassword.getColumnIndex("password"));
                    //判断密码是否匹配
                    if (inputOriginalPassword.equals(psw_query)) {
                        //判断两次输入的新密码是否相同
                        if (inputNewPassword.equals(inputNewPasswordAgain)){
                            //判断新密码是否为空
                            if (TextUtils.isEmpty(inputNewPassword)) {
                                Toast.makeText(ChangePassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                            } else {
                                db.execSQL("UPDATE user SET password = '" + inputNewPassword + "' WHERE _id = '" + current_user_id + "';");
                                Toast.makeText(ChangePassword.this, "修改成功,请返回登录", Toast.LENGTH_SHORT).show();
                                Intent toLogin = new Intent(ChangePassword.this, Login.class);
                                startActivity(toLogin);
                                MainActivity a = new MainActivity();
                                a.mainActivity.finish();
                                finish();
                            }
                        } else {
                            Toast.makeText(ChangePassword.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        edit_original_password.setText("");
                        Toast.makeText(ChangePassword.this, "原密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePassword.this, "修改失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //失去焦点时收起软键盘
        LinearLayout layout = findViewById(R.id.changePassword_layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) ChangePassword.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ChangePassword.this.getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });

    }
}