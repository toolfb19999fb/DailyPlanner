package com.otisloky.dailyplanner;

import android.content.*;import java.text.SimpleDateFormat;import java.util.*;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent in){ long id=in.getLongExtra("task_id",-1); List<TaskStore.Task> ts=TaskStore.load(c); String today=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
        for(TaskStore.Task t:ts) if(t.id==id && t.enabled){ if(!today.equals(t.completedDate)){ Notify.show(c,t.id,t.title,"حان الآن وقت المهمة"); t.lastNotifiedDate=today; TaskStore.save(c,ts); } AlarmScheduler.schedule(c,t); break; }
    }
}
