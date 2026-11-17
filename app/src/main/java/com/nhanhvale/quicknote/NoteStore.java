package com.nhanhvale.quicknote;
import android.content.Context;
import android.content.SharedPreferences;
final class NoteStore {
    static final int SLOTS = 3;
    static final class Note {
        final String text; final boolean done;
        Note(String text, boolean done) { this.text = text; this.done = done; }
        boolean empty() { return text.isEmpty(); }
    }
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("quick_notes",Context.MODE_PRIVATE); }
    static Note get(Context c,int i) {
        SharedPreferences p=prefs(c);
        return new Note(p.getString("text_"+i,""),p.getBoolean("done_"+i,false));
    }
    static void put(Context c,int i,String text,boolean done) {
        if(i<0 || i>=SLOTS) return;
        String value=text.trim();
        prefs(c).edit().putString("text_"+i,value).putBoolean("done_"+i,!value.isEmpty()&&done).apply();
    }
    static void toggle(Context c,int i) {
        if(i<0 || i>=SLOTS) return;
        Note n=get(c,i); if(!n.empty()) put(c,i,n.text,!n.done);
    }
    static int firstEmpty(Context c) { for(int i=0;i<SLOTS;i++) if(get(c,i).empty()) return i; return -1; }
    static int firstDone(Context c) { for(int i=0;i<SLOTS;i++) if(get(c,i).done) return i; return -1; }
}
