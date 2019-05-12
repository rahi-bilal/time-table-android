package com.scheduleme.timetable;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ScheduleAlarmReceiver extends BroadcastReceiver {

    public ScheduleAlarmReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
         Bundle bundle= intent.getBundleExtra("bundle");
         String[] data= bundle.getStringArray("notificationData");

         Uri ringtoneUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Toast.makeText(context, data[0] ,Toast.LENGTH_SHORT).show();
         //Initializing inbox style
        NotificationCompat.BigTextStyle style= new NotificationCompat.BigTextStyle();
        style.bigText("From\t\t\t"+data[0]+"\n"+
                        "Subject\t"+data[1]+"\n"+
                        "Room\t\t"+data[2]+"\n"+
                        "Teacher\t"+data[3])
                .setBigContentTitle("Get Ready for class")
                .setSummaryText("Class time\t"+data[0]);


         NotificationCompat.Builder builder=(NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSound(ringtoneUri)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Class time")
                .setContentText("Slide down for details")
                 .setStyle(style);
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
