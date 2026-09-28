package com.otisloky.dailyplanner;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public final class TaskStore {
    private static final String PREF = "daily_planner";
    private static final String KEY = "tasks";
    private TaskStore() {}

    public static class Task {
        public long id; public String title; public int hour, minute; public boolean enabled; public String completedDate; public String lastNotifiedDate;
        public Task(long id, String title, int hour, int minute) { this.id=id; this.title=title; this.hour=hour; this.minute=minute; this.enabled=true; this.completedDate=""; this.lastNotifiedDate=""; }
    }

    public static List<Task> load(Context c) {
        List<Task> out = new ArrayList<>();
        String raw = c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "[]");
        try { JSONArray a = new JSONArray(raw); for (int i=0;i<a.length();i++) { JSONObject o=a.getJSONObject(i); Task t=new Task(o.getLong("id"),o.getString("title"),o.getInt("hour"),o.getInt("minute")); t.enabled=o.optBoolean("enabled",true); t.completedDate=o.optString("completedDate",""); t.lastNotifiedDate=o.optString("lastNotifiedDate",""); out.add(t); } } catch(Exception ignored) {}
        return out;
    }
    public static void save(Context c, List<Task> tasks) {
        JSONArray a=new JSONArray(); try { for(Task t:tasks){ JSONObject o=new JSONObject(); o.put("id",t.id);o.put("title",t.title);o.put("hour",t.hour);o.put("minute",t.minute);o.put("enabled",t.enabled);o.put("completedDate",t.completedDate);o.put("lastNotifiedDate",t.lastNotifiedDate);a.put(o); } } catch(Exception ignored) {}
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY,a.toString()).apply();
    }
}
