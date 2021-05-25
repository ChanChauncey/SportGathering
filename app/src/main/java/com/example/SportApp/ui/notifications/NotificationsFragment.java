package com.example.SportApp.ui.notifications;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.SportApp.ChangePassword;
import com.example.SportApp.Login;
import com.example.SportApp.MainActivity;
import com.example.SportApp.MyDatabaseHelper;
import com.example.SportApp.R;
import com.example.SportApp.Register;

public class NotificationsFragment extends Fragment {

    private MyDatabaseHelper databaseHelper;
    private int current_user_id; //当前用户

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        return root;
    }

    //获取当前用户
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        current_user_id = ((MainActivity) context).getCurrentUser();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //数据库
        databaseHelper = new MyDatabaseHelper(getContext(),"SportDatabase.db",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //部件
        Button modify_name_button = getActivity().findViewById(R.id.modify_name_button);
        Button modify_password_button = getActivity().findViewById(R.id.modify_password_button);
        Button delete_user_button = getActivity().findViewById(R.id.delete_user_button);
        Button exit_button = getActivity().findViewById(R.id.exit_button);
        TextView show_name = getActivity().findViewById(R.id.user_name);

        //显示用户名
        Cursor getName = db.rawQuery("SELECT user_name FROM user WHERE _id = '" + current_user_id + "';", null);
        if (getName.moveToNext()){
            String current_user = getName.getString(getName.getColumnIndex("user_name"));

            show_name.setText(current_user);
        }

        //修改用户名
        modify_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getContext());
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(getContext());
                inputDialog.setTitle("请输入新用户名").setView(editText);
                inputDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(editText.getText().toString())){
                                    Toast.makeText(getContext(), "用户名不能为空",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.execSQL("UPDATE user SET user_name = '" + editText.getText().toString() + "' WHERE _id = '" + current_user_id + "';");
                                    show_name.setText(editText.getText().toString());
                                    Toast.makeText(getContext(),"修改成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                inputDialog.show();
            }
        });

        //修改密码
        modify_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChangePassword = new Intent(getActivity(), ChangePassword.class);
                toChangePassword.putExtra("current_user_id",current_user_id);
                startActivity(toChangePassword);
//                //使用对话框修改
//                final EditText editText = new EditText(getContext());
//                AlertDialog.Builder inputDialog =
//                        new AlertDialog.Builder(getContext());
//                inputDialog.setTitle("请输入新密码").setView(editText);
//                inputDialog.setPositiveButton("确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (TextUtils.isEmpty(editText.getText().toString())){
//                                    Toast.makeText(getContext(), "密码不能为空",Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    db.execSQL("UPDATE user SET password = '" + editText.getText().toString() + "' WHERE _id = '" + current_user_id + "';");
//                                    Toast.makeText(getContext(),"修改成功,请返回登录", Toast.LENGTH_SHORT).show();
//                                    Intent toLogin = new Intent(getActivity(), Login.class);
//                                    startActivity(toLogin);
//                                }
//                            }
//                        });
//                inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                inputDialog.show();
            }
        });

        //注销账号
        delete_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("警告：确定注销账号吗？");
                dialog.setMessage("注销后将无法恢复！");
                dialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("UPDATE field SET reserved = '未被预约' WHERE user_id = '" + current_user_id + "';");
                                db.execSQL("UPDATE field SET user_id = null WHERE user_id = '" + current_user_id + "';");
                                db.execSQL("DELETE FROM user WHERE _id = '" + current_user_id + "';");
                                Toast.makeText(getContext(),"注销成功", Toast.LENGTH_SHORT).show();
                                Intent toLogin = new Intent(getActivity(), Login.class);
                                startActivity(toLogin);
                                getActivity().finish();
                            }
                        });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("确定退出登录？");
                dialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"退出成功", Toast.LENGTH_SHORT).show();
                                Intent toLogin = new Intent(getActivity(), Login.class);
                                startActivity(toLogin);
                                getActivity().finish();
                            }
                        });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });

    }
}