package com.example.hao.app_final_test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class ContentAlarm extends Fragment implements View.OnClickListener {

    private ListView listView;
    Context mcontext;
    private myAdapter adapter;
    private int position;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private EditText edit_alarm;
    private Switch switch_alarm;
    private DataOperator data_operator;

    //调用方法顺序：onAttach(),onCreate(),onCreateView(),onActivityCreated()
    //onStart()
    //onResume()
    //onPause()
    //onStop()
    //onDestroyView(),onDestroy(),onDetach()
    //每次调用fregment时候，都会调用到onResume所有的方法，
    // 同时切换页面的时候，fregment会默认destory掉，不是像activity一样放入栈。
    // 如果当前的页面切换不变，则会调用onCreateView()到onResume方法

     @Override
        public void onAttach(Context context) {
            this.mcontext=context;
            data_operator =new DataOperator(mcontext);
            super.onAttach(context);
        }

    //创建视图
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         //加载对应的layout文件
         View view = inflater.inflate(R.layout.layout_alarm_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
         //填充listView数据，并添加监听
        listView = (ListView) getView().findViewById(R.id.ListView_thing);
        adapter=new myAdapter(ContentAlarm.this);
        listView.setAdapter((ListAdapter) adapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        //获取闹铃数据
        data_operator.getAlarmData();
        super.onStart();
    }

    //监听点击事件
    @Override
    public void onClick(View v) {
        final View view;
        switch (v.getId()){
            //监听设置按钮的点击事件
            case R.id.btn_edit:
                //加载对话框界面
                position=(int)v.getTag();
                builder=new AlertDialog.Builder(mcontext);
                builder.setTitle("设置"+ data_operator.list_thing.get(position).name+"的闹铃");
                inflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.layout_alarm_dial, null);
                builder.setView(view);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit_alarm =view.findViewById(R.id.edit_alarm);
                        switch_alarm=view.findViewById(R.id.switch_alarm);
                        thing thing= data_operator.list_thing.get(position);
                        //如果设置闹铃开关为开
                        if(switch_alarm.isChecked()){
                            //获取当前时间，事件时间与设置的时间差，计算并获得设置闹铃的时间
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            long currentTime =System.currentTimeMillis();
                            long activityTime = 0;
                            long diffTime, alarmTime;
                            String diff = edit_alarm.getText().toString();
                            try {
                                activityTime = df.parse(thing.time).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (diff.length() == 0){
                                diffTime = 5*60*1000;
                            }
                            else {
                                diffTime = Long.parseLong(diff)*60*1000;
                            }
                            alarmTime= activityTime-diffTime;
                            //如果设置的闹铃时间还没过，设置闹铃
                            if (currentTime<alarmTime){
                                //更新界面上的闹铃时间和数据库中的对应的闹铃时间
                                Date alarmDate=new Date(alarmTime);
                                thing.alarm = df.format(alarmDate);
                                data_operator.setAlarmTime(df.format(alarmDate),thing.name, thing.time);

                                //设置闹铃
                                AlarmManager am = (AlarmManager)mcontext.getSystemService(Context.ALARM_SERVICE);
                                //使用显式意图来发送显式广播
                                Intent intent = new Intent(mcontext,AlarmReceiver.class);
                                intent.setAction(thing.name+thing.time);
                                intent.putExtra("thingName",thing.name);
                                PendingIntent sender = PendingIntent.getBroadcast(mcontext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                //对于版本在4.4以上的系统，调用setExact()方法，确保闹铃时间精确
                                assert am != null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    am.setExact(AlarmManager.RTC_WAKEUP,alarmTime , sender);
                                }
                                else {
                                    am.set(AlarmManager.RTC_WAKEUP,alarmTime , sender);
                                }
                            }
                            //设置的闹铃时间已过，发送Toast通知提醒用户
                            else {
                                Toast.makeText(mcontext,"设置的闹铃时间已过",Toast.LENGTH_SHORT).show();
                            }
                            }

                        //如果设置闹铃开关为关
                        else {
                            //更新界面上的闹铃时间和数据库中的对应的闹铃时间
                            thing.alarm ="无闹铃提醒";
                            data_operator.setAlarmTime("无闹铃提醒",thing.name, thing.time);
                            //取消对应的闹铃
                            AlarmManager am = (AlarmManager)mcontext.getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                            intent.setAction(thing.name+thing.time);
                            PendingIntent sender = PendingIntent.getBroadcast(mcontext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            assert am != null;
                            am.cancel(sender);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                break;
        }
    }

    //数据适配器类，用于为ListView提供数据
    public class myAdapter extends BaseAdapter {
        private final View.OnClickListener listener;

        public myAdapter(View.OnClickListener listener) {
            this.listener = listener;

        }

        @Override
        public int getCount() {
            return data_operator.list_thing.size();
        }

        @Override
        public Object getItem(int position) {
            return data_operator.list_thing.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            itemView = View.inflate(mcontext, R.layout.layout_alarm_item, null);
            TextView tv_name= itemView.findViewById(R.id.textView_name);
            TextView tv_place = itemView.findViewById(R.id.textView_place);
            TextView tv_time = itemView.findViewById(R.id.textView_time);
            TextView tv_alarm = itemView.findViewById(R.id.textView_alarm);
            TextView btn_edit=itemView.findViewById(R.id.btn_edit);

            //设置各组件的内容
            tv_name.setText(data_operator.list_thing.get(position).name);
            tv_place.setText(data_operator.list_thing.get(position).location);
            tv_time.setText(data_operator.list_thing.get(position).time);
            tv_alarm.setText(data_operator.list_thing.get(position).alarm);
            btn_edit.setOnClickListener(listener);
            btn_edit.setTag(position);
            return itemView;
        }
    }
}