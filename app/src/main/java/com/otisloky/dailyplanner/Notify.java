package com.otisloky.dailyplanner;

import android.app.*;import android.content.*;import android.os.Build;

public final class Notify {
    public static final String CHANNEL_ID="tasks";
    private Notify(){}
    public static void createChannel(Context c){ if(Build.VERSION.SDK_INT>=26){ NotificationChannel ch=new NotificationChannel(CHANNEL_ID,"تنبيهات المهام",NotificationManager.IMPORTANCE_HIGH); ch.setDescription("تنبيهات البرنامج اليومي"); ch.enableVibration(true); ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch); } }
    public static void show(Context c,long id,String title,String reason){
        createChannel(c); Intent i=new Intent(c,MainActivity.class); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP); PendingIntent pi=PendingIntent.getActivity(c,(int)(id%Integer.MAX_VALUE),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,CHANNEL_ID):new Notification.Builder(c); b.setSmallIcon(com.otisloky.dailyplanner.R.drawable.ic_launcher).setContentTitle("المهمة الآن").setContentText(title).setStyle(new Notification.BigTextStyle().bigText(reason+"\n"+title)).setContentIntent(pi).setAutoCancel(true).setPriority(Notification.PRIORITY_HIGH);
        ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).notify((int)(id%Integer.MAX_VALUE),b.build());
    }
}
