package com.example.hao.app_final_test;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private android.support.v4.app.FragmentTransaction transaction;
    private android.support.v4.app.FragmentManager fragmentManager;

    //一个私有的事件选择监听器
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.task:
                    //利用transation传递替换掉content为我们的页面，一定要commit
                    transaction.replace(R.id.content, new ContentTask());
                    transaction.commit();
                    return true;
                case R.id.check:
                    transaction.replace(R.id.content, new ContentCheck());
                    transaction.commit();
                    return true;
                case R.id.alarm:
                    transaction.replace(R.id.content, new ContentAlarm());
                    transaction.commit();
                    return true;
                case R.id.count:
                    transaction.replace(R.id.content, new ContentStats());
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    private void setDefaultFragment() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new ContentTask());
        transaction.commit();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置默认首页
        setDefaultFragment();
        //底部拉入NAVIGATION，并关闭滑动效果，和建立事件选择监听器
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        main_BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
