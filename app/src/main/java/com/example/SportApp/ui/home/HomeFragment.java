package com.example.SportApp.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.SportApp.MainActivity;
import com.example.SportApp.MyDatabaseHelper;
import com.example.SportApp.R;
import com.example.SportApp.SelectDate;
import com.example.SportApp.SelectField;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private MyDatabaseHelper databaseHelper;
    private String filed_date; //选择的日期
    private String field_name; //选择的场地
    private int current_user_id; //当前用户
    Cursor search ; //SQL语句

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
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

        //跳转到选择日期
        TextView date_text = getActivity().findViewById(R.id.date_text);
        date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //需要返回数据，特征码为1
                startActivityForResult(new Intent(getActivity(), SelectDate.class),1);
            }
        });

        //跳转到选择场地
        TextView field_text = getActivity().findViewById(R.id.field_text);
        field_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //需要返回数据，特征码为1
                startActivityForResult(new Intent(getActivity(), SelectField.class),2);
            }
        });

        //数据库
        databaseHelper = new MyDatabaseHelper(getContext(),"SportDatabase.db",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //展示数据
        ListView field_listview = getActivity().findViewById(R.id.field_listview);
        Cursor cursor = db.rawQuery("select * from field;", null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.reservation_item,
                cursor,
                //必须有_id字段
                new String[]{"_id", "field_name", "date", "time", "reserved"},
                new int[]{R.id.field_id, R.id.field_name, R.id.date, R.id.time, R.id.reserved}
        );
        field_listview.setAdapter(adapter);
        field_listview.setEmptyView(getActivity().findViewById(R.id.no_field));

        //筛选事件
        Button search_button = getActivity().findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox reserved_checkbox = getActivity().findViewById(R.id.reserved_checkbox);
                

                //未被预定
                if(reserved_checkbox.isChecked()){
                    search = db.rawQuery("select * from field where date = '" + filed_date +
                            "' and field_name = '" + field_name +
                            "' and reserved = '未被预约';", null);
                }

                //条件为空则显示全部
                else if (field_name == null && filed_date == null){
                    search = db.rawQuery("select * from field;", null);
                }

                else{
                    search = db.rawQuery("select * from field where date = '" + filed_date + "' and field_name = '" + field_name + "';", null);
                }

                //适配器
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        getContext(),
                        R.layout.reservation_item,
                        search,
                        //必须有_id字段
                        new String[]{"_id", "field_name", "date", "time", "reserved"},
                        new int[]{R.id.field_id, R.id.field_name, R.id.date, R.id.time, R.id.reserved}
                );

                field_listview.setAdapter(adapter);
            }
        });

        //预约事件
        field_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取选择的场地ID和预定情况
                Cursor c = (Cursor) field_listview.getItemAtPosition(position);
                int field_id = c.getInt(c.getColumnIndex("_id"));
                String reserved = c.getString(c.getColumnIndex("reserved"));

                if (reserved.equals("未被预约")){
                    //对话框
                    AlertDialog.Builder reserve = new AlertDialog.Builder(getContext());
                    reserve.setTitle("是否确认预约");
                    reserve.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.execSQL("UPDATE field SET user_id = '" + current_user_id + "' WHERE _id = " + field_id + ";");
                            db.execSQL("UPDATE field SET reserved = '已被预约' WHERE _id = " + field_id + ";");
                            Toast.makeText(getContext(), "预约成功", Toast.LENGTH_SHORT).show();

                            //刷新listview
                            SimpleCursorAdapter ad = (SimpleCursorAdapter) field_listview.getAdapter();
                            if (field_name == null && filed_date == null){
                                cursor.requery();
                            }
                            else {
                                search.requery();
                            }
                            ad.notifyDataSetChanged();
                        }
                    });
                    reserve.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    reserve.show();
                }

                else {
                    Toast.makeText(getContext(), "该场地已被预约", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //用于接收数据
        Bundle bundle = data.getExtras();

        //根据requestCode接收返回的数据
        switch (requestCode){
            case 1:
                String date_data = bundle.getString("date_data");
                TextView date_text = getActivity().findViewById(R.id.date_text);
                date_text.setText(date_data);
                filed_date = date_data;
                break;
            case 2:
                String field_data = bundle.getString("field_data");
                TextView field_text = getActivity().findViewById(R.id.field_text);
                field_text.setText(field_data);
                field_name = field_data;
                break;
            default:
        }
    }

}