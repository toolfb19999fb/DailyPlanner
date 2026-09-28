package com.otisloky.dailyplanner;
import android.content.*;import java.text.SimpleDateFormat;import java.util.*;
public class UnlockReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent in){ if(!Intent.ACTION_USER_PRESENT.equals(in.getAction()))return; Calendar now=Calendar.getInstance(); int mins=now.get(Calendar.HOUR_OF_DAY)*60+now.get(Calendar.MINUTE); String today=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(now.getTime()); List<TaskStore.Task> ts=TaskStore.load(c); for(TaskStore.Task t:ts){ int tm=t.hour*60+t.minute; if(t.enabled && tm<=mins && !today.equals(t.completedDate) && !today.equals(t.lastNotifiedDate)){ Notify.show(c,t.id,t.title,"فاتك وقت هذه المهمة أو حان وقتها. افتح التطبيق لإتمامها."); t.lastNotifiedDate=today; } } TaskStore.save(c,ts); }
}
