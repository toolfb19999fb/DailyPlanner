package com.otisloky.dailyplanner;

import android.app.*;import android.content.*;import android.os.Build;import java.util.*;

public final class AlarmScheduler {
    private AlarmScheduler(){}
    public static void schedule(Context c, TaskStore.Task t){
        if(!t.enabled)return; AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE); Intent i=new Intent(c,AlarmReceiver.class).putExtra("task_id",t.id); PendingIntent pi=PendingIntent.getBroadcast(c,(int)(t.id%Integer.MAX_VALUE),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Calendar cal=Calendar.getInstance(); cal.set(Calendar.HOUR_OF_DAY,t.hour);cal.set(Calendar.MINUTE,t.minute);cal.set(Calendar.SECOND,0);cal.set(Calendar.MILLISECOND,0); if(cal.getTimeInMillis()<=System.currentTimeMillis())cal.add(Calendar.DAY_OF_YEAR,1);
        if(Build.VERSION.SDK_INT>=31 && am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi); else if(Build.VERSION.SDK_INT>=23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi); else am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
    }
    public static void cancel(Context c,TaskStore.Task t){ AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE); Intent i=new Intent(c,AlarmReceiver.class).putExtra("task_id",t.id); PendingIntent pi=PendingIntent.getBroadcast(c,(int)(t.id%Integer.MAX_VALUE),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE); am.cancel(pi); }
    public static void rescheduleAll(Context c){ for(TaskStore.Task t:TaskStore.load(c)) if(t.enabled)schedule(c,t); }
}
