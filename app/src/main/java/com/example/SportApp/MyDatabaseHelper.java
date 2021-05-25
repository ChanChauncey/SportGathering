package com.example.SportApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建用户表
        db.execSQL("create table user(_id integer primary key autoincrement, user_name text, password text)");
        //创建场地表
        db.execSQL("create table field(_id integer primary key autoincrement, field_name text, date text, time text, reserved text, user_id integer)");
        //插入场地数据
        db.execSQL("insert into field values(1, '棒球场', '7月1日', '14:00-15:00', '未被预约', null)");
        db.execSQL("insert into field values(2, '棒球场', '7月1日', '15:00-16:00', '未被预约', null)");
        db.execSQL("insert into field values(3, '棒球场', '7月1日', '16:00-17:00', '已被预约', null)");
        db.execSQL("insert into field values(4, '棒球场', '7月1日', '17:00-18:00', '已被预约', null)");
        db.execSQL("insert into field values(5, '高尔夫球场', '7月1日', '14:00-15:00', '未被预约', null)");
        db.execSQL("insert into field values(6, '高尔夫球场', '7月1日', '15:00-16:00', '未被预约', null)");
        db.execSQL("insert into field values(7, '高尔夫球场', '7月1日', '16:00-17:00', '已被预约', null)");
        db.execSQL("insert into field values(8, '高尔夫球场', '7月1日', '17:00-18:00', '已被预约', null)");
        db.execSQL("insert into field values(9, '篮球场', '7月1日', '14:00-15:00', '未被预约', null)");
        db.execSQL("insert into field values(10, '篮球场', '7月1日', '15:00-16:00', '未被预约', null)");
        db.execSQL("insert into field values(11, '篮球场', '7月1日', '16:00-17:00', '已被预约', null)");
        db.execSQL("insert into field values(12, '篮球场', '7月1日', '17:00-18:00', '已被预约', null)");
        db.execSQL("insert into field values(13, '排球场', '7月1日', '14:00-15:00', '未被预约', null)");
        db.execSQL("insert into field values(14, '排球场', '7月1日', '15:00-16:00', '未被预约', null)");
        db.execSQL("insert into field values(15, '排球场', '7月1日', '16:00-17:00', '已被预约', null)");
        db.execSQL("insert into field values(16, '排球场', '7月1日', '17:00-18:00', '已被预约', null)");
        //插入用户数据
        db.execSQL("insert into user values(1, 'Chauncey', '123456')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
