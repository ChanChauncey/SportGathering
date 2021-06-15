package com.example.SportApp.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import com.example.SportApp.MainActivity;
import com.example.SportApp.R;

public class SelectDate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date_data = (month+1) + "月" + dayOfMonth + "日";

                //传输数据给HomeFragment
                Intent intent = new Intent(SelectDate.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("date_data", date_data);
                intent.putExtras(bundle);
                setResult(1,intent);
                finish();
            }
        });
    }

    /******************************返回上一页******************************/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectDate.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}