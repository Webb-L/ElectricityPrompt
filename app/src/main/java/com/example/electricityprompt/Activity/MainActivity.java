package com.example.electricityprompt.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.electricityprompt.R;
import com.example.electricityprompt.Receiver.PowerConnectionReceiver;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 监听USB插入/或拔出广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        PowerConnectionReceiver receiver = new PowerConnectionReceiver(this.<TextView>findViewById(R.id.text));
        Intent batteryStatus = this.registerReceiver(receiver, intentFilter);
    }
}