package com.nhanhvale.quicknote;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
public final class EditActivity extends Activity {
    private int slot;
    private boolean replacing;
    private EditText input;
    private int dp(float x){return Math.round(x*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        slot=getIntent().getIntExtra("slot",-1);
        if(state!=null){slot=state.getInt("slot"); replacing=state.getBoolean("replacing"); if(slot>=0){showEditor(state.getString("draft"));return;}}
        if(slot>=0&&slot<NoteStore.SLOTS){showEditor(null);return;}
        slot=NoteStore.firstEmpty(this);
        if(slot>=0){showEditor(null);return;}
        int done=NoteStore.firstDone(this);
        if(done>=0) {
            new AlertDialog.Builder(this).setTitle("Widget đã đủ 3 nhiệm vụ")
                .setMessage("Thay nhiệm vụ đã hoàn thành: "+NoteStore.get(this,done).text+"?")
                .setPositiveButton("Thay thế",(d,w)->{slot=done; replacing=true;showEditor(null);})
                .setNegativeButton("Hủy",(d,w)->finish()).setOnCancelListener(d->finish()).show();
        } else {
            String[] labels=new String[NoteStore.SLOTS];
            for(int i=0;i<labels.length;i++) labels[i]=NoteStore.get(this,i).text;
            new AlertDialog.Builder(this).setTitle("Đã đủ 3 nhiệm vụ — chọn để sửa")
                .setItems(labels,(d,w)->{slot=w;showEditor(null);}).setNegativeButton("Hủy",(d,w)->finish()).setOnCancelListener(d->finish()).show();
        }
    }
    private void showEditor(String draft){
        if(slot<0||slot>=NoteStore.SLOTS){finish();return;}
        NoteStore.Note existing=NoteStore.get(this,slot);
        boolean adding=existing.empty()||replacing;
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(20),dp(20),dp(20),dp(12));
        TextView title=new TextView(this);title.setText(adding?"Ghi chú mới":"Sửa ghi chú");title.setTextSize(20);title.setTextColor(Color.WHITE);root.addView(title);
        input=new EditText(this);input.setHint("Nhập việc cần làm…");input.setTextColor(Color.WHITE);input.setHintTextColor(Color.parseColor("#A2B0A7"));input.setTextSize(16);input.setMaxLines(3);input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});input.setText(draft!=null?draft:adding?"":existing.text);input.setSelection(input.length());root.addView(input,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.END);root.addView(bar);
        if(!adding){ Button del=new Button(this);del.setText("Xóa");bar.addView(del);del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Xóa ghi chú?").setNegativeButton("Hủy",null).setPositiveButton("Xóa",(d,w)->{NoteStore.put(this,slot,"",false);QuickWidget.refresh(this);finish();}).show());}
        Button cancel=new Button(this);cancel.setText("Hủy");bar.addView(cancel);cancel.setOnClickListener(v->finish());
        Button save=new Button(this);save.setText("Lưu");save.setTextColor(Color.parseColor("#BCE5B2"));bar.addView(save);save.setOnClickListener(v->{String text=input.getText().toString().trim();if(text.isEmpty()){input.setError("Nhập nội dung");return;}NoteStore.put(this,slot,text,adding?false:existing.done);QuickWidget.refresh(this);finish();});
        setContentView(root); input.requestFocus();getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
    @Override protected void onSaveInstanceState(Bundle b){super.onSaveInstanceState(b); b.putInt("slot",slot);b.putBoolean("replacing",replacing);if(input!=null)b.putString("draft",input.getText().toString());}
}
