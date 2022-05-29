package com.gachon.ccpp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DeadlinePopupActivity extends Activity {

    TextView titlebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_deadline_popup);

        titlebar = (TextView) findViewById(R.id.titlebar);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        titlebar.setText(date);
    }


    public void mOnClose(View view) {

        finish();
    }
}