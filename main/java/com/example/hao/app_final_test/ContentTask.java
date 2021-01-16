package com.example.hao.app_final_test;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ContentTask extends Fragment implements View.OnClickListener {		//继承fragment

    private ListView listView;
    private Context mcontext;
    private Button btn_add,btn_clear;
    private FruitAdapter adapter;
    private int position;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private EditText edit_name,edit_place;
    TextView tv_date;
    private RadioGroup radio_level;
    private String date_string="",time_string="";

    private Boolean flag;
    private DatePickerDialog.OnDateSetListener listener;
    private TimePickerDialog.OnTimeSetListener timeListener;
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
        this.mcontext=context;//获取上下文
        data_operator =new DataOperator(mcontext);
        super.onAttach(context);
    }

    //创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate( R.layout.layout_task_main, container, false );
        return view; //要加载的layout文件
    }


    @Override
    public void onStart() {
        //获取数据库中数据
        data_operator.get_data();
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        listView=getView().findViewById(R.id.ListView_thing);
        adapter=new FruitAdapter(ContentTask.this);
        listView.setAdapter(adapter);

        btn_add=getView().findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        btn_clear=getView().findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
        super.onActivityCreated(savedInstanceState);
    }



    //按钮监听器
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        Button btn_set_time;
        final View view;

        //时间组件的监听器
        listener=new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //获取日期
                date_string="";
                date_string=date_string+year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
            }};
        timeListener=new TimePickerDialog.OnTimeSetListener(){
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // 获取time
                time_string="";
                time_string=time_string+hourOfDay+":"+minute;

                //转换格式，首先转换成date，再转回string，为了美观所以写的
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                DateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm");

                //判断一下dateString

                Calendar now = Calendar.getInstance();
                if(date_string.isEmpty()) date_string=now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+"-"+now.get(Calendar.DAY_OF_MONTH);
                String time=date_string+" "+time_string;
                try {
                    Date date=fmt.parse(time);
                    //设置到tv_date上显示
                    tv_date.setText(formatter.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                    tv_date.setText(time);
                }

            }};
        //按钮选择
        switch (v.getId()){
            case R.id.btn_add:
                //弹出对话框
                builder=new AlertDialog.Builder(mcontext);
                inflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
                //对话框的布局
                view = inflater.inflate(R.layout.layout_task_dial, null);
                builder.setView(view);
                //找到布局上的按键
                edit_name=view.findViewById(R.id.edit_name);
                tv_date=view.findViewById(R.id.tv_date);
                Date date=new Date();
                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String now_str=dateFormat.format(date);
                tv_date.setText(now_str);
                edit_place=view.findViewById(R.id.edit_place);
                radio_level=view.findViewById(R.id.radio_level);

                //时间按钮监听事件及寻找按钮
                btn_set_time=view.findViewById(R.id.btn_set_time);
                btn_set_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(new Date());//设置时间
                        //最后显示time，在栈里
                        new TimePickerDialog(
                                getContext(),
                                timeListener,
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        ).show();
                        //第一个显示选择date，
                        new DatePickerDialog(getContext(),
                                listener,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        ).show();

                    }
                });

                //监听对话框的确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获取选择的group按钮
                        RadioButton radio_choose=view.findViewById(radio_level.getCheckedRadioButtonId());
                        //添加事件
                        flag= data_operator.add(edit_name.getText().toString(),tv_date.getText().toString(),edit_place.getText().toString(),radio_choose.getText().toString());
                        //提醒有重复
                        if (!flag)
                            Toast.makeText(mcontext,"已经拥有了这条记录，添加失败",Toast.LENGTH_LONG).show();
                        //更新显示
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();//显示对话框
                break;
            case R.id.btn_clear:
                //对话框
                builder=new AlertDialog.Builder(mcontext);
                builder.setTitle("所有的事件删除！！！");
                builder.setNegativeButton("取消", null);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data_operator.clear();//调用数据类的方法处理数据
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                break;
            case R.id.btn_change:
                position=(int)v.getTag();//获取需要修改数据在list的位置
                builder=new AlertDialog.Builder(mcontext); //建立对话框
                builder.setTitle(data_operator.list_thing.get(position).name+"事件进行修改");
                inflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.layout_task_dial, null);
                builder.setView(view);
                //寻找组件，并设置内容
                edit_name=view.findViewById(R.id.edit_name);
                edit_name.setText(data_operator.list_thing.get(position).name);
                tv_date=view.findViewById(R.id.tv_date);
                tv_date.setText(data_operator.list_thing.get(position).time);
                edit_place=view.findViewById(R.id.edit_place);
                edit_place.setText(data_operator.list_thing.get(position).location);
                radio_level=view.findViewById(R.id.radio_level);
                if (data_operator.list_thing.get(position).priority.equals("高级"))
                    radio_level.check(R.id.radio_button_high);
                else
                    radio_level.check(R.id.radio_button_low);

                //时间组件
                btn_set_time=view.findViewById(R.id.btn_set_time);
                btn_set_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(new Date());
                        new TimePickerDialog(
                                getContext(),
                                timeListener,
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        ).show();

                        new DatePickerDialog(getContext(),
                                listener,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        ).show();
                    }
                });
                //设置确定处理事件
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton radio_choose=view.findViewById(radio_level.getCheckedRadioButtonId());
                        //调用数据类写的set方法
                        flag= data_operator.set(position,edit_name.getText().toString(),tv_date.getText().toString(),edit_place.getText().toString(),radio_choose.getText().toString());

                        if (!flag)
                            Toast.makeText(mcontext,"修改这条记录，请检查是否有重复的",Toast.LENGTH_LONG).show();

                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
                break;
            case R.id.btn_delete:
                position=(int)v.getTag();//获取数据在list的position
                builder=new AlertDialog.Builder(mcontext);
                builder.setTitle(data_operator.list_thing.get(position).name+"事件删除");
                builder.setNegativeButton("取消", null);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //数据类的delete方法
                        data_operator.delete(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                break;
        }
    }

    //数据适配器，用于listview的显示，这里已经优化了listview的显示（当太多的item时，这里固定在界面只显示一部分的item）
    public class FruitAdapter extends BaseAdapter {
        private final View.OnClickListener listener;

        //监听listview中item的button组件
        public FruitAdapter(View.OnClickListener listener) {
            this.listener = listener;
        }

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
        public View getView(int position, View itemView, ViewGroup parent) {
            ViewHolder holder;
            //初次进入listview时
            if (itemView == null) {
                itemView = LayoutInflater.from(mcontext).inflate(R.layout.layout_task_item, parent, false);
                holder = new ViewHolder();//定义一个ViewHolder类
                holder.tv_name = itemView.findViewById(R.id.textView_name);
                holder.tv_place = itemView.findViewById(R.id.textView_place);
                holder.tv_time = itemView.findViewById(R.id.textView_time);
                holder.tv_level = itemView.findViewById(R.id.textView_level);
                holder.btn_change = itemView.findViewById(R.id.btn_change);
                holder.btn_delete = itemView.findViewById(R.id.btn_delete);
                itemView.setTag(holder);//传给下次进入使用
            } else{
                //获取上次的holder
                holder=(ViewHolder)itemView.getTag();
            }

                //设置组件的内容
                holder.tv_name.setText(data_operator.list_thing.get(position).name);
                holder.tv_place.setText(data_operator.list_thing.get(position).location);
                holder.tv_time.setText(data_operator.list_thing.get(position).time);
                holder.tv_level.setText(data_operator.list_thing.get(position).priority);
                if (holder.tv_level.getText().toString().equals("高级")){
                    holder.tv_level.setTextColor(Color.parseColor("#ffcc0000"));
                }
                else {
                    holder.tv_level.setTextColor(Color.GRAY);
                }


                //设置监听，并传出数据被点击的位置
                holder.btn_change.setOnClickListener(listener);
                holder.btn_change.setTag(position);
                holder.btn_delete.setOnClickListener(listener);
                holder.btn_delete.setTag(position);
            return itemView;
        }
        //自定义的viewHolder类
        class ViewHolder{
            TextView tv_name,tv_place,tv_time,tv_level;
            Button btn_change,btn_delete;
        }
    }

}