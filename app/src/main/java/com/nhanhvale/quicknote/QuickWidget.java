package com.nhanhvale.quicknote;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.os.Bundle;
import android.widget.RemoteViews;
public final class QuickWidget extends AppWidgetProvider {
    private static final String TOGGLE="com.nhanhvale.quicknote.TOGGLE";
    private static final int[] CHECK={R.id.check0,R.id.check1,R.id.check2};
    private static final int[] TASK={R.id.task0,R.id.task1,R.id.task2};
    private static final int FLAGS=PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE;
    @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids) { for(int id:ids) render(c,m,id); }
    @Override public void onAppWidgetOptionsChanged(Context c,AppWidgetManager m,int id,Bundle options) { render(c,m,id); }
    @Override public void onReceive(Context c,Intent intent) {
        if(TOGGLE.equals(intent.getAction())) { NoteStore.toggle(c,intent.getIntExtra("slot",-1)); refresh(c); return; }
        super.onReceive(c,intent);
    }
    static void refresh(Context c) {
        AppWidgetManager m=AppWidgetManager.getInstance(c);
        int[] ids=m.getAppWidgetIds(new ComponentName(c,QuickWidget.class));
        for(int id:ids) render(c,m,id);
    }
    private static PendingIntent edit(Context c,int slot) {
        Intent i=new Intent(c,EditActivity.class).putExtra("slot",slot).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(c,100+slot,i,FLAGS);
    }
    private static PendingIntent toggle(Context c,int slot) {
        Intent i=new Intent(c,QuickWidget.class).setAction(TOGGLE).putExtra("slot",slot);
        return PendingIntent.getBroadcast(c,200+slot,i,FLAGS);
    }
    private static void render(Context c,AppWidgetManager m,int id) {
        Bundle b=m.getAppWidgetOptions(id);
        int h=b.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,120);
        int w=b.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,120);
        RemoteViews v=new RemoteViews(c.getPackageName(),h>=150&&w>=150?R.layout.widget_normal:R.layout.widget_compact);
        v.setOnClickPendingIntent(R.id.add,edit(c,-1));
        v.setOnClickPendingIntent(R.id.title,PendingIntent.getActivity(c,300,new Intent(c,MainActivity.class),FLAGS));
        int completed=0,total=0;
        for(int i=0;i<NoteStore.SLOTS;i++) {
            NoteStore.Note n=NoteStore.get(c,i);
            if(!n.empty()) total++;
            if(n.done) completed++;
            SpannableString shown=new SpannableString(n.empty()?"Thêm ghi chú":n.text);
            if(n.done) shown.setSpan(new StrikethroughSpan(),0,shown.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            v.setTextViewText(TASK[i],shown);
            v.setTextColor(TASK[i],Color.parseColor(n.empty()?"#82958B":n.done?"#A2B0A7":"#F2F6F2"));
            v.setImageViewResource(CHECK[i],n.done?R.drawable.ic_done:R.drawable.ic_check);
            v.setContentDescription(CHECK[i],n.empty()?"Thêm ghi chú":(n.done?"Bỏ hoàn thành ":"Hoàn thành ")+n.text);
            v.setOnClickPendingIntent(CHECK[i],n.empty()?edit(c,i):toggle(c,i));
            v.setOnClickPendingIntent(TASK[i],edit(c,i));
        }
        v.setTextViewText(R.id.count,completed+"/"+total);
        v.setProgressBar(R.id.progress,Math.max(1,total),completed,false);
        m.updateAppWidget(id,v);
    }
}
