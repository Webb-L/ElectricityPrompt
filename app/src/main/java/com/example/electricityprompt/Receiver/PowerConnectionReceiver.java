package com.example.electricityprompt.Receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;
import com.example.electricityprompt.R;


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
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;
            String tips = context.getResources().getString(R.string.PowerConnectionReceiver_CurrentBattery) + "：" + batteryPct + "%";
            this.textView.setText(tips);
            if (batteryPct == 100) {
                // 根据用户设置电量进行通知提示
                this.textView.setText(R.string.PowerConnectionReceiver_BatteryIsFull);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification builder = new NotificationCompat.Builder(context, "1")
                        .setContentTitle(context.getResources().getString(R.string.PowerConnectionReceiver_BatteryIsFull))
                        .setContentText(context.getResources().getString(R.string.PowerConnectionReceiver_FullBatteryPrompt))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .build();
                manager.notify(1, builder);
            }
        } else {
            this.textView.setText(context.getResources().getString(R.string.PowerConnectionReceiver_NotPluggedIn));
        }
    }
}