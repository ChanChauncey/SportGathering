package com.example.SportApp.Dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.SportApp.MainActivity;
import com.example.SportApp.MyDatabaseHelper;
import com.example.SportApp.R;

public class DashboardFragment extends Fragment {

    private MyDatabaseHelper databaseHelper;
    private int current_user_id; //当前用户

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
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

        //展示数据
        ListView my_reservation = getActivity().findViewById(R.id.my_reservation);
        Cursor cursor = db.rawQuery("select * from field where user_id = '" + current_user_id + "' ;", null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.my_item,
                cursor,
                //必须有_id字段
                new String[]{"_id", "field_name", "date", "time", "reserved"},
                new int[]{R.id.field_id, R.id.field_name, R.id.date, R.id.time, R.id.reserved}
        );
        my_reservation.setAdapter(adapter);
        my_reservation.setEmptyView(getActivity().findViewById(R.id.no_reservation));

        //取消预约
        my_reservation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取选择的场地ID
                Cursor c = (Cursor) my_reservation.getItemAtPosition(position);
                int field_id = c.getInt(c.getColumnIndex("_id"));

                //对话框
                AlertDialog.Builder cancel = new AlertDialog.Builder(getContext());
                cancel.setTitle("是否取消预约");
                cancel.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.execSQL("UPDATE field SET user_id = null WHERE _id = " + field_id + ";");
                        db.execSQL("UPDATE field SET reserved = '未被预约' WHERE _id = " + field_id + ";");
                        Toast.makeText(getContext(), "取消成功", Toast.LENGTH_SHORT).show();

                        //刷新listview
                        SimpleCursorAdapter ad = (SimpleCursorAdapter) my_reservation.getAdapter();
                        cursor.requery();
                        ad.notifyDataSetChanged();
                    }
                });
                cancel.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                cancel.show();
            }
        });
    }
}