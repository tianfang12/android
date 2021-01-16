package com.example.hao.app_final_test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//数据连接
public class DataHelper extends SQLiteOpenHelper {
    public DataHelper(Context context){
        super(context,"db_todo1.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table if not exists tb_activity(name varchar(30),time varchar(30),location varchar(30),priority varchar(30),done INTEGER,alarm varchar(30),primary key(name,time,location))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
