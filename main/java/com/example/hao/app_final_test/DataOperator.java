package com.example.hao.app_final_test;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DataOperator {
    public List<thing> list_thing=new ArrayList<thing>();
    private DataHelper dataHelper;
    private SQLiteDatabase db;
    private Context mcontext;

    public DataOperator(){}
    //数据库连接需要activity的context，所以写构造方法中，再用这个类的时候就连接context了。
    public DataOperator(Context context){
        mcontext=context;
        dataHelper =new DataHelper(context);
    }


    //从数据库读出数据到list_thing中
    public void get_data(){
        db= dataHelper.getReadableDatabase();
        Cursor cursor=db.query("tb_activity",null,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            add(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("time")),cursor.getString(cursor.getColumnIndex("location")),cursor.getString(cursor.getColumnIndex("priority")),cursor.getInt(cursor.getColumnIndex("done")),cursor.getString(cursor.getColumnIndex("alarm")));
        }
        cursor.close();
        db.close();
    }


    //根据name，location,time判断能否添加事件
    public Boolean add_able(thing o){
        Boolean flag=true;
        for(int i=0;i<list_thing.size();i++){
            if(list_thing.get(i).name.equals(o.name))
                if (list_thing.get(i).time.equals(o.time))
                    if (list_thing.get(i).location.equals(o.location))
                            flag=false;
        }
        return flag;
    }

    //list_thing添加数据
    public void add(String name,String time,String place,String level,int done,String alarm){
        thing new_thing=new thing();
        new_thing.time=time;
        new_thing.name=name;
        new_thing.priority=level;
        new_thing.location=place;
        new_thing.alarm=alarm;
        new_thing.done=done;
        list_thing.add(new_thing);
        Collections.sort(list_thing);//数据排序
    }

    //获取闹铃数据
    public void getAlarmData(){
        //设置筛选记录的条件
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = df.format(new Date(System.currentTimeMillis()));
        String selection = "priority = ? AND time >= ?";
        String[] selectionArgs ={ "高级", currentTime};

        //从数据库中获取记录
        db= dataHelper.getReadableDatabase();
        Cursor cursor=db.query("tb_activity",null, selection,selectionArgs,null,null,null,null);
        while(cursor.moveToNext()){
            add(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("time")),cursor.getString(cursor.getColumnIndex("location")),cursor.getString(cursor.getColumnIndex("priority")),cursor.getInt(cursor.getColumnIndex("done")),cursor.getString(cursor.getColumnIndex("alarm")));
        }
        cursor.close();
        db.close();
    }

    //设置闹钟时间
    public void setAlarmTime(String alarm,String name, String time){
        //设置更新记录的条件
        String selection = "name = ? AND time = ?";
        String[] selectionArgs ={name, time};
        ContentValues cv = new ContentValues();
        cv.put("alarm",alarm);

        //更新数据库中的记录
        db= dataHelper.getWritableDatabase();
        db.update("tb_activity", cv,selection,selectionArgs);
        db.close();
    }

    public void getStats(){
        int done_count=0,not_count=0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today =df.format(date);
        String selection = "time like ? ";
        String[] selectionArgs ={today+"%"};
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = df.format(new Date(System.currentTimeMillis()-300000));

        db= dataHelper.getReadableDatabase();
        Cursor cursor=db.query("tb_activity",null, selection,selectionArgs,null,null,null,null);
        while(cursor.moveToNext()){
            add(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("time")),cursor.getString(cursor.getColumnIndex("location")),cursor.getString(cursor.getColumnIndex("priority")),cursor.getInt(cursor.getColumnIndex("done")),cursor.getString(cursor.getColumnIndex("alarm")));
            if (cursor.getInt(cursor.getColumnIndex("done"))==1)
                done_count=done_count+1;
            else {

                if (cursor.getString(cursor.getColumnIndex("time")).compareTo(currentTime)<0)
                    not_count=not_count+1;
            }
        }
        cursor.close();
        db.close();
        SharedPreferences sp_data=mcontext.getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp_data.edit();
        editor.remove("done");
        editor.remove("not_done");
        editor.commit();
        editor.putInt("not_done",not_count);
        editor.putInt("done",done_count);
        editor.commit();
    }



    //删除一个事件，需要list和数据库一起删除
    public void delete(int index){
        db= dataHelper.getWritableDatabase();
        db.delete("tb_activity","name=? and time=? and location=?",new String[]{list_thing.get(index).name,list_thing.get(index).time,list_thing.get(index).location});
        db.close();
        list_thing.remove(index);
    }

    //清空事件，list和数据库一起清空
    public void clear(){
        db= dataHelper.getWritableDatabase();
        db.delete("tb_activity",null,null);
        db.close();
        list_thing.clear();
    }

    //新加一个thing类，添加数据，再判断能否添加，再输入数据库，再加到list_thing
    public Boolean add(String name,String time,String location,String priority){
            Boolean flag=true;
            thing new_thing=new thing();
            if (time.isEmpty())
                time="default";
            new_thing.time=time;
            //判断用户输入是否为空，空则置为默认值
            if (name.isEmpty())
                name="default";
            new_thing.name=name;
            if (priority.isEmpty())
                priority="低级";
            new_thing.priority=priority;
            if (location.isEmpty())
                location="default";
            new_thing.location=location;
            new_thing.alarm="无闹铃提醒";
            new_thing.done=0;

            if (add_able(new_thing)) {
                db= dataHelper.getWritableDatabase();
                ContentValues values =new ContentValues();
                values.put("name",new_thing.name);
                values.put("time",new_thing.time);
                values.put("location",new_thing.location);
                values.put("priority",new_thing.priority);
                values.put("done",new_thing.done);
                values.put("alarm",new_thing.alarm);

                long tes=db.insert("tb_activity",null,values);
                if (tes!=-1)
                    list_thing.add(new_thing); //怕插入失败，数据库与list不同步
                Collections.sort(list_thing);
                values.clear();
                db.close();
            }
            else{
                flag=false;  //返回添加失败，有相同事件
            }
            return flag;
    }

    //修改事件，首先利用list原来的数据先在数据库中删除原有事件，再添加到数据库，list再更新
    public Boolean set(int index,String name,String time,String location,String priority){
        thing new_thing=new thing();
        Boolean flag=true;

        new_thing.time=time;
        if (name.isEmpty())  name="default1";
        new_thing.name=name;
        if (priority.isEmpty())  priority="低级";
        new_thing.priority=priority;
        if (location.isEmpty()) location="default1";
        new_thing.location=location;
        new_thing.alarm=list_thing.get(index).alarm;
        new_thing.done=list_thing.get(index).done;
        //首先判断能否添加
        if (add_able(new_thing)) {
            db= dataHelper.getWritableDatabase();
            db.delete("tb_activity","name=? and time=? and location=?",new String[]{list_thing.get(index).name,list_thing.get(index).time,list_thing.get(index).location});

            ContentValues values =new ContentValues();
            values.put("name",new_thing.name);
            values.put("time",new_thing.time);
            values.put("location",new_thing.location);
            values.put("priority",new_thing.priority);
            values.put("done",new_thing.done);
            values.put("alarm",new_thing.alarm);
            //写入修改
            list_thing.set(index, new_thing);
            db.insert("tb_activity",null,values);

            values.clear();
            db.close();
            Collections.sort(list_thing);//排序一下
        }else {
            flag=false;
        }
        return flag;
    }


}

//数据类，里面有事件的属性，同时重写了一个排序方法（先根据时间排序，然后再是根据中文的发音abc排序）
class thing implements Comparable<thing>{
    String name,location,time,priority,alarm;
    int done;

    //数据的排序方式
    @Override
    public int compareTo(@NonNull thing o) {
        int diff=0;//this.xx-o.xx
        //diff为比较方式带来的结果
        //比较时间
        if(!this.time.equals(o.time)){
            diff=this.time.compareTo(o.time);
            if(diff>0){
                return 1;
            }else if (diff<0){
                return -1;
            }
        }else{
            //先比较优先级
            Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
            diff=cmp.compare(this.priority,o.priority);
            if (diff>0)
                return -1;
            else if (diff<0)
                return 1;
            else {
                //再按照名字排序
                diff = cmp.compare(this.name, o.name);
                if (diff < 0) {
                    return -1;
                } else if (diff >= 0) {
                    return 1;
                }
            }
        }
        return diff;
    }
}