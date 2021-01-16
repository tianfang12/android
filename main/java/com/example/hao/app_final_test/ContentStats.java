package com.example.hao.app_final_test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ContentStats extends Fragment {//继承fragment
    private Context acontext;
    private DataOperator data_operator;
    private TextView name,stars,eggs;
    private ListView things;
    private fruitAdapter adapter1;//定义适配器

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_stats_main, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        this.acontext=context;//获取上下文
        data_operator =new DataOperator(acontext);
        data_operator.getStats();
        super.onAttach(context);
    }


    @Override
    public void onStart() {
        //获取数据库中数据
        super.onStart();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        things=getView().findViewById(R.id.things);
        stars=getView().findViewById(R.id.stars);
        eggs=getView().findViewById(R.id.eggs);
        name=getView().findViewById(R.id.name);
        //寻找主键
        adapter1=new fruitAdapter();
        things.setAdapter(adapter1);//数据适配器连接到listview
        adapter1.notifyDataSetChanged();//动态更新listviwe
        super.onActivityCreated(savedInstanceState);//调用父类，储存状态

        SharedPreferences sp_data=acontext.getSharedPreferences("data",Context.MODE_PRIVATE);
        int count= sp_data.getInt("done",0);
        int not_count=sp_data.getInt("not_done",0); //从SharedPreferences类中获取星星（已打卡事件）和臭鸡蛋（未打卡事件）的数量
        stars.setText("星星数："+count);//在textview中显示读出的星星数
        eggs.setText("臭鸡蛋："+not_count);//在textview中显示读出的臭鸡蛋数
        if(count-not_count>=5){name.setText("勤劳小蜜蜂");}
        if(count-not_count>=0&&count-not_count<5){name.setText("冲鸭少年");}
        if(count-not_count<0){name.setText("懒癌晚期患者");}
        //显示评价称号通过count-not_count的值判断



    }







    class fruitAdapter extends BaseAdapter {//新建一个适配器类
        private TextView t1,t2;

        @Override
        public int getCount() {
            return data_operator.list_thing.size();
        }

        @Override
        public Object getItem(int position) {
            return data_operator.list_thing.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(acontext, R.layout.layout_stats_item, null);
            t1=convertView.findViewById(R.id.textView3);//寻找主键
            t1.setText(data_operator.list_thing.get(position).name);//调用数据库获取事件名称
            t2=convertView.findViewById(R.id.textView4);//寻找主键

            if(data_operator.list_thing.get(position).done==1)
                t2.setText("已打卡");               //事件的done字段值为1时在textview中显示已打卡
            if(data_operator.list_thing.get(position).done==0)
                t2.setText("未打卡");               //事件的done字段值为0时在textview中显示未打卡


            return convertView;


        }
    }


}