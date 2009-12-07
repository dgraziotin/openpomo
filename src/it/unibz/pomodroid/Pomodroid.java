package it.unibz.pomodroid;

import it.unibz.pomodroid.persistency.User;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Pomodroid extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Start");
        User user = new User(null,null,null,this);
        user = user.retrieve();
        tv.setText(user.getTracUrl());
        setContentView(tv);
        //setContentView(R.layout.main);
    }
}