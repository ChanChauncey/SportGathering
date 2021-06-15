package com.example.SportApp.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.SportApp.MainActivity;
import com.example.SportApp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectField extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_field);

        //写死的数据，用于测试
        int[] sport_image = new int[]{
                R.drawable.baseball,
                R.drawable.golf,
                R.drawable.basketball,
                R.drawable.volleyball,
                R.drawable.pingpong,
                R.drawable.tennis,
                R.drawable.swim,
                R.drawable.badminton,
                R.drawable.football}; //存储图片
        String[] sport_category = new String[] {
                "棒球场",
                "高尔夫球场",
                "篮球场",
                "排球场",
                "乒乓球室",
                "网球场",
                "游泳池",
                "羽毛球场",
                "足球场"};

        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
        for (int i = 0; i < sport_category.length; i++)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sport_image", sport_image[i]);
            map.put("sport_category", sport_category[i]);
            listitem.add(map);
        }

        //创建适配器
        // 第一个参数是上下文对象
        // 第二个是listitem
        // 第三个是指定每个列表项的布局文件
        // 第四个是指定Map对象中定义的两个键（这里通过字符串数组来指定）
        // 第五个是用于指定在布局文件中定义的id（也是用数组来指定）
        SimpleAdapter adapter = new SimpleAdapter(SelectField.this,
                listitem,
                R.layout.field_item,
                new String[]{"sport_category", "sport_image"},
                new int[]{R.id.sport_category, R.id.sport_image});

        ListView listView = findViewById(R.id.select_field_listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                String item = map.get("sport_category").toString();

                //传输数据给HomeFragment
                Intent intent = new Intent(SelectField.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("field_data", item);
                intent.putExtras(bundle);
                setResult(2,intent);
                finish();
            }
        });
    }

    /******************************返回上一页******************************/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectField.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}