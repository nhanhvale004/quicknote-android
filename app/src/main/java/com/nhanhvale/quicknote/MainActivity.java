package com.nhanhvale.quicknote;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.view.Gravity;
import android.widget.*;
public final class MainActivity extends Activity {
    private LinearLayout root;
    private static final int FG=Color.parseColor("#F2F6F2"), MUTED=Color.parseColor("#AABBB0"), ACCENT=Color.parseColor("#BCE5B2");
    private int dp(float x){return Math.round(x*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle state){super.onCreate(state);}
    @Override protected void onResume(){super.onResume();render();}
    private TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
    private void edit(int slot){startActivity(new Intent(this,EditActivity.class).putExtra("slot",slot));}
    private void render(){
        ScrollView scroll=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(22),dp(24),dp(22),dp(24));scroll.addView(root);setContentView(scroll);
        if(android.os.Build.VERSION.SDK_INT>=35){scroll.setOnApplyWindowInsetsListener((v,w)->{android.graphics.Insets in=w.getInsets(android.view.WindowInsets.Type.systemBars()|android.view.WindowInsets.Type.displayCutout());v.setPadding(in.left,in.top,in.right,in.bottom);return w;});scroll.requestApplyInsets();}
        TextView name=text("Ghi chú nhanh",30,FG);name.setTypeface(null,Typeface.BOLD);root.addView(name);
        TextView sub=text("3 việc cần làm · Widget 2×2",14,MUTED);root.addView(sub);sub.setPadding(0,dp(5),0,dp(24));
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(16),dp(12),dp(16),dp(16));card.setBackgroundResource(R.drawable.widget_bg);root.addView(card,new LinearLayout.LayoutParams(-1,-2));
        int n=0,total=0;
        for(int i=0;i<NoteStore.SLOTS;i++){
            final int slot=i;NoteStore.Note note=NoteStore.get(this,i);if(!note.empty())total++;if(note.done)n++;
            LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);card.addView(row,new LinearLayout.LayoutParams(-1,dp(60)));
            ImageButton check=new ImageButton(this);check.setBackgroundColor(Color.TRANSPARENT);check.setImageResource(note.done?R.drawable.ic_done:R.drawable.ic_check);check.setContentDescription(note.done?"Bỏ hoàn thành":"Hoàn thành");row.addView(check,new LinearLayout.LayoutParams(dp(48),dp(48)));check.setOnClickListener(v->{if(note.empty())edit(slot);else{NoteStore.toggle(this,slot);QuickWidget.refresh(this);render();}});
            TextView title=text(note.empty()?"Thêm ghi chú":note.text,17,note.done||note.empty()?MUTED:FG);title.setMaxLines(2);title.setGravity(Gravity.CENTER_VERTICAL);if(note.done)title.setPaintFlags(title.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);row.addView(title,new LinearLayout.LayoutParams(0,-1,1));title.setOnClickListener(v->edit(slot));
            TextView arrow=text("›",23,MUTED);arrow.setGravity(Gravity.CENTER);row.addView(arrow,new LinearLayout.LayoutParams(dp(32),-1));arrow.setOnClickListener(v->edit(slot));
        }
        TextView status=text(n+"/"+total+" hoàn thành",13,MUTED);status.setPadding(0,dp(10),0,dp(8));card.addView(status);
        ProgressBar progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setProgressDrawable(getDrawable(R.drawable.progress_bg));progress.setMax(Math.max(1,total));progress.setProgress(n);card.addView(progress,new LinearLayout.LayoutParams(-1,dp(5)));
        Button add=new Button(this);add.setText("+  Thêm ghi chú");add.setTextColor(Color.parseColor("#203C28"));add.setBackgroundResource(R.drawable.plus_bg);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));p.topMargin=dp(18);root.addView(add,p);add.setOnClickListener(v->edit(-1));
        Button pin=new Button(this);pin.setText("Thêm widget vào màn hình chính");pin.setTextColor(ACCENT);LinearLayout.LayoutParams pb=new LinearLayout.LayoutParams(-1,dp(52));pb.topMargin=dp(10);root.addView(pin,pb);pin.setOnClickListener(v->{AppWidgetManager m=AppWidgetManager.getInstance(this);if(m.isRequestPinAppWidgetSupported()){boolean requested=m.requestPinAppWidget(new ComponentName(this,QuickWidget.class),null,null);if(requested)return;}Toast.makeText(this,"Nhấn giữ màn hình chính → Tiện ích → Ghi chú nhanh",Toast.LENGTH_LONG).show();});
        TextView hint=text("Bạn cũng có thể nhấn giữ màn hình chính, chọn Tiện ích và tìm Ghi chú nhanh. Dữ liệu chỉ lưu trên thiết bị.",14,MUTED);hint.setPadding(0,dp(18),0,0);root.addView(hint);
    }
}
