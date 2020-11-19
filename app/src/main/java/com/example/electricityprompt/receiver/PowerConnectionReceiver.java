package com.example.electricityprompt.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import com.example.electricityprompt.R;


/**
 * @author Administrator
 */
public class PowerConnectionReceiver extends BroadcastReceiver {
    private final TextView textBatteryPct;
    private final TextView textInsertState;
    private final CardView cvCurrentBattery;
    private final TextView tvCurrentBatteryTitle;
    private final TextView tvCurrentBatteryDes;
    private int batteryPct;

    public int getBatteryPct() {
        return batteryPct;
    }

    public PowerConnectionReceiver(TextView textBatteryPct,
                                   TextView textInsertState,
                                   CardView cvCurrentBattery,
                                   TextView tvCurrentBatteryTitle,
                                   TextView tvCurrentBatteryDes) {
        this.textBatteryPct = textBatteryPct;
        this.textInsertState = textInsertState;
        this.cvCurrentBattery = cvCurrentBattery;
        this.tvCurrentBatteryTitle = tvCurrentBatteryTitle;
        this.tvCurrentBatteryDes = tvCurrentBatteryDes;
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        getBatteryPct(intent);
        // 判断电源是否插入
        if (isCharging) {
            this.textInsertState.setText(R.string.main_status_description_yes);
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_" + "preferences", Context.MODE_PRIVATE);
            String price = sharedPreferences.getString("promptCharge", "100");
            if (batteryPct >= Float.parseFloat(price)) {
                // 根据用户设置电量进行通知提示
                this.textInsertState.setText(R.string.main_status_description_complete);
                String channelId = "1";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
                builder.setContentTitle(context.getResources().getString(R.string.PowerConnectionReceiver_BatteryIsFull))
                        .setContentText(context.getResources().getString(R.string.PowerConnectionReceiver_FullBatteryPrompt))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);

                createNotificationChannel(context, channelId, builder);
            }
        } else {
            this.textInsertState.setText(context.getResources().getString(R.string.main_status_description_no));
        }
        // 判断电量是否小于或等于20
        int minimumBattery = 20;
        if (batteryPct <= minimumBattery) {
            setLowElectricity(context);
        } else {
            defaultStatus(context);
        }
    }

    /**
     * 低电量状态
     *
     * @param context context
     */
    private void setLowElectricity(Context context) {
        cvCurrentBattery.setCardBackgroundColor(context.getResources().getColor(R.color.colorLowElectricity, context.getTheme()));
        tvCurrentBatteryTitle.setTextColor(context.getResources().getColor(R.color.colorLowElectricityText, context.getTheme()));
        tvCurrentBatteryDes.setTextColor(Color.WHITE);
    }

    /**
     * 电量默认状态
     * @param context context
     */
    private void defaultStatus(Context context) {
        cvCurrentBattery.setCardBackgroundColor(Color.WHITE);
        tvCurrentBatteryTitle.setTextColor(Color.rgb(118,118,118));
        tvCurrentBatteryDes.setTextColor(Color.BLACK);
    }

    /**
     * 创建通知
     *
     * @param context            context
     * @param channelId          channelId
     * @param notificationCompat notificationCompat
     */
    private void createNotificationChannel(Context context, String channelId, NotificationCompat.Builder notificationCompat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.PowerConnectionReceiver_BatteryIsFull);
            String description = context.getString(R.string.PowerConnectionReceiver_FullBatteryPrompt);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(Integer.parseInt(channelId), notificationCompat.build());
        }
    }

    /**
     * 获取并设置电量
     *
     * @param intent intent
     */
    public void getBatteryPct(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = (int) (level * 100 / (float) scale);
        String tips = batteryPct + "%";
        this.textBatteryPct.setText(tips);
    }

}