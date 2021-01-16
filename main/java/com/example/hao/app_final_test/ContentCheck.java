package com.example.hao.app_final_test;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContentCheck extends Fragment implements View.OnClickListener{
    ListView listView;
    DataOperator data;
    Context mcontext;
    String nowtime;
    DataHelper helper;
    MyBaseAdapter adapter;
    int done;
    int pos;
    //List<Integer> done=new ArrayList<Integer>();
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_check, container, false);
        return view;
    }

    @Override
    public void onStart() {
        data=new DataOperator(mcontext);
        data.getStats();  //获取数据库中数据
        super.onStart();
//        Date date=new Date();
//        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        nowtime=dateFormat.format(date);
//        Toast.makeText(mcontext,"当前时间："+nowtime,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        listView=getView().findViewById(R.id.todaylistview);
        adapter=new MyBaseAdapter(ContentCheck.this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        this.mcontext=context;//获取上下文
        data=new DataOperator(mcontext);
        data.get_data();
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {

    }
    class MyBaseAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{

        View.OnClickListener listener;
        public MyBaseAdapter(View.OnClickListener listener) {
            this.listener=listener;
        }

        @Override
        public int getCount() {
            return data.list_thing.size();
        }

        @Override
        public Object getItem(int position) {
            //return data.get_data(mcontext).list_thing.get(position);
            return data.list_thing.get(position);
            //return data.list_thing.get(position);
           // return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            pos=position;
            done=data.list_thing.get(position).done;
            convertView=LayoutInflater.from(mcontext).inflate(R.layout.layout_check_item,parent,false);
            TextView textView=(TextView)convertView.findViewById(R.id.item_tv);
            CheckBox checkBox=(CheckBox)convertView.findViewById(R.id.item_clock);
            if (data.list_thing.get(position).priority.equals("高级"))
                convertView.setBackgroundColor(Color.rgb(250, 219, 216 ));
            else
                convertView.setBackgroundColor(Color.WHITE);
            checkBox.setTag(position);
            if (done==1){
                checkBox.setChecked(true);
            }
            textView.setText("事件名称："+data.list_thing.get(position).name+"\n"+
                    "开始时间："+ data.list_thing.get(position).time+"\n"+
                    "活动地点："+data.list_thing.get(position).location+"\n"+
                    "优先程度："+data.list_thing.get(position).priority+"\n");
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date=new Date();
            try {
                date=format.parse(data.list_thing.get(position).time);
            } catch (ParseException e) {
            }
            checkBox.setClickable(false);
            if (new Date().getTime()-300000<=date.getTime()&&new Date().getTime()+300000>=date.getTime()){
                //设置为活动开始时间的前后5分钟
                checkBox.setClickable(true);
            }
            checkBox.setOnCheckedChangeListener(this);
            //将修改的done写入对应的list_thing.get(position).done位置
            return convertView;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //打卡
                //Toast.makeText(mcontext, ""+getTag(),Toast.LENGTH_LONG).show();
                data.list_thing.get(Integer.parseInt(String.valueOf(buttonView.getTag()))).done=1;
                helper=new DataHelper(mcontext);
                SQLiteDatabase db=helper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("done",1);
                //int number=db.update("tb_activity",values,"name=?",new String[]{data.list_thing.get(i).name});
                int number=db.update("tb_activity",values,"name=? and time=? and location=?",new String[]{
                        data.list_thing.get(Integer.parseInt(String.valueOf(buttonView.getTag()))).name,
                        data.list_thing.get(Integer.parseInt(String.valueOf(buttonView.getTag()))).time,
                        data.list_thing.get(Integer.parseInt(String.valueOf(buttonView.getTag()))).location
                });

                //Toast.makeText(mcontext, ""+getTag(),Toast.LENGTH_LONG).show();
                data.list_thing.get(Integer.parseInt(String.valueOf(buttonView.getTag()))).done=1;
            }else {
                Toast.makeText(mcontext,"had clocked",Toast.LENGTH_LONG).show();
                buttonView.setChecked(true);
            }

        }

    }
}
