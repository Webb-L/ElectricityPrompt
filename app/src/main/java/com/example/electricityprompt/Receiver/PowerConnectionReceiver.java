package com.example.electricityprompt.Receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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


            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_" + "preferences", Context.MODE_PRIVATE);
            String price = sharedPreferences.getString("promptCharge", "100");
            if (batteryPct >= Float.parseFloat(price)) {
                // 根据用户设置电量进行通知提示
                this.textView.setText(R.string.PowerConnectionReceiver_BatteryIsFull);
                String channelId = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationChannel channel = new NotificationChannel("1", "tips", NotificationManager.IMPORTANCE_HIGH);
                    assert manager != null;
                    manager.createNotificationChannel(channel);
                    channelId = "100";
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
                builder.setContentTitle(context.getResources().getString(R.string.PowerConnectionReceiver_BatteryIsFull))
                        .setContentText(context.getResources().getString(R.string.PowerConnectionReceiver_FullBatteryPrompt))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(1, builder.build());
            }
        } else {
            this.textView.setText(context.getResources().getString(R.string.PowerConnectionReceiver_NotPluggedIn));
        }
    }
}