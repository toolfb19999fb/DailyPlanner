package com.otisloky.dailyplanner;

import android.Manifest;import android.app.*;import android.content.*;import android.content.pm.PackageManager;import android.graphics.Color;import android.graphics.drawable.GradientDrawable;import android.net.Uri;import android.os.*;import android.provider.Settings;import android.view.*;import android.widget.*;import java.text.SimpleDateFormat;import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list; List<TaskStore.Task> tasks; final int BLUE=Color.rgb(25,118,210); final int BG=Color.rgb(16,19,24); final int CARD=Color.rgb(27,32,40); final int WHITE=Color.WHITE; final int MUTED=Color.rgb(170,178,191);
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(BG); Notify.createChannel(this); requestPermissionsIfNeeded(); build();}
    void requestPermissionsIfNeeded(){ if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},20); if(Build.VERSION.SDK_INT>=31){ try{AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE); if(!am.canScheduleExactAlarms()){ Intent i=new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:"+getPackageName())); startActivity(i);} }catch(Exception ignored){} } }
    TextView tv(String s,float sp){ TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(WHITE);t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);t.setTypeface(null,1);return t; }
    GradientDrawable bg(int color,float r){ GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(r);return g; }
    void build(){
        ScrollView sv=new ScrollView(this); sv.setBackgroundColor(BG); LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(24,20,24,28);sv.addView(root); setContentView(sv);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL); head.setOrientation(LinearLayout.HORIZONTAL);
        Button add=new Button(this);add.setText("+");add.setTextSize(25);add.setTextColor(WHITE);add.setBackground(bg(BLUE,50));add.setOnClickListener(v->addTask()); head.addView(add,new LinearLayout.LayoutParams(58,58));
        TextView title=tv("برنامجي اليومي",26);title.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,70,1);tp.setMargins(18,0,0,0);head.addView(title,tp); root.addView(head);
        TextView sub=tv("المهام تتكرر كل يوم تلقائياً",14);sub.setTextColor(MUTED);sub.setGravity(Gravity.RIGHT);root.addView(sub,new LinearLayout.LayoutParams(-1,42));
        list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);root.addView(list);
        refresh();
    }
    void refresh(){ tasks=TaskStore.load(this); Collections.sort(tasks,(a,b)->{int x=a.hour*60+a.minute,y=b.hour*60+b.minute;return Integer.compare(x,y);}); list.removeAllViews(); if(tasks.isEmpty()){TextView e=tv("مازال ما ضفت حتى مهمة.\nضغط على + باش تبدا.",17);e.setTextColor(MUTED);e.setGravity(Gravity.CENTER);list.addView(e,new LinearLayout.LayoutParams(-1,180));return;} String today=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date()); for(TaskStore.Task t:tasks) addRow(t,today); }
    void addRow(TaskStore.Task t,String today){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(12,6,8,6);row.setBackground(bg(CARD,28)); LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,78);rp.setMargins(0,0,0,12);list.addView(row,rp);
        CheckBox cb=new CheckBox(this);cb.setButtonTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[][]{new int[]{49,196,141},new int[]{170,178,191}}));cb.setChecked(today.equals(t.completedDate)); cb.setOnClickListener(v->{t.completedDate=cb.isChecked()?today:"";TaskStore.save(this,tasks);});row.addView(cb,new LinearLayout.LayoutParams(52,60));
        TextView name=tv(t.title,17);name.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);name.setTextColor(WHITE); LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(0,60,1);row.addView(name,np);
        TextView time=tv(String.format(Locale.US,"%02d:%02d",t.hour,t.minute),20);time.setTextColor(Color.rgb(80,170,255));time.setGravity(Gravity.CENTER);row.addView(time,new LinearLayout.LayoutParams(80,60));
        ImageButton del=new ImageButton(this);del.setImageResource(android.R.drawable.ic_menu_delete);del.setColorFilter(Color.LTGRAY);del.setBackgroundColor(Color.TRANSPARENT);del.setOnClickListener(v->{AlarmScheduler.cancel(this,t);tasks.remove(t);TaskStore.save(this,tasks);refresh();});row.addView(del,new LinearLayout.LayoutParams(48,60));
    }
    void addTask(){
        final EditText input=new EditText(this);input.setHint("مثلاً: دراسة الألمانية");input.setTextColor(WHITE);input.setHintTextColor(MUTED);input.setSingleLine(true);input.setTextDirection(View.TEXT_DIRECTION_RTL);
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(45,5,45,0);box.addView(input,new LinearLayout.LayoutParams(-1,60));
        new AlertDialog.Builder(this).setTitle("مهمة جديدة").setView(box).setPositiveButton("اختيار الوقت",(d,w)->{ if(input.getText().toString().trim().isEmpty())return; TimePickerDialog tp=new TimePickerDialog(this,(v,h,m)->saveTask(input.getText().toString().trim(),h,m),Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),true);tp.setTitle("وقت المهمة");tp.show(); }).setNegativeButton("إلغاء",null).show();
    }
    void saveTask(String title,int h,int m){TaskStore.Task t=new TaskStore.Task(System.currentTimeMillis(),title,h,m);tasks.add(t);TaskStore.save(this,tasks);AlarmScheduler.schedule(this,t);refresh();}
    @Override protected void onResume(){super.onResume(); if(list!=null)refresh();}
}
