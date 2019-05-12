package com.scheduleme.timetable;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ExamAlarmReceiver extends BroadcastReceiver {

    public ExamAlarmReceiver(){super();}
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri ringtoneUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSound(ringtoneUri)
                .setContentTitle("Exam schedule...")
                .setContentText("Tomorrow is your exam. All the best")
                .setSmallIcon(R.mipmap.ic_launcher_round);
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10, builder.build());
    }
}
