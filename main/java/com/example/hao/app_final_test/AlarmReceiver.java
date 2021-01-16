package com.example.hao.app_final_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //从intent中获取事件名称
        String thingName = intent.getStringExtra("thingName");
        //发送Toast通知
        Toast.makeText(context, "记得给"+thingName+"打卡哦", Toast.LENGTH_LONG).show();
        //获取默认提醒闹铃声，并播放
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context,notification);
        ringtone.play();
    }
}
