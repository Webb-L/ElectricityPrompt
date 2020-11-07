package com.example.electricityprompt;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author Administrator
 */
public class PowerConnectionReceiver extends BroadcastReceiver {
    private final TextView textView;

    public PowerConnectionReceiver(TextView textView) {
        this.textView = textView;
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging) {
            Toast.makeText(context, "已插入电源！", Toast.LENGTH_SHORT).show();
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;
            String tips = "当前电量：" + batteryPct + "%";
            this.textView.setText(tips);
        } else {
            this.textView.setText("未插入电源！");
        }
    }
}