package webb.batteryreminder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import webb.batteryreminder.R;
import webb.batteryreminder.receiver.PowerConnectionReceiver;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Class.forName;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity {

    private PowerConnectionReceiver receiver;
    private LineChart chart;
    private List<Entry> entries;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        boolean isEnabled = notification.areNotificationsEnabled();

        if (!isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
                Intent intent = new Intent()
                        .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package",
                                getApplicationContext().getPackageName(), null));
                startActivity(intent);
                Toast.makeText(this, "请开启通知权限！", Toast.LENGTH_SHORT).show();
            }
        }
        // 监听USB插入/或拔出广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        receiver = new PowerConnectionReceiver(this.<TextView>findViewById(R.id.batteryPct),
                this.<TextView>findViewById(R.id.status_description),
                this.<CardView>findViewById(R.id.cv_current_battery),
                this.<TextView>findViewById(R.id.tv_currentBatteryTitle),
                this.<TextView>findViewById(R.id.batteryPct));
        this.registerReceiver(receiver, intentFilter);
        chart = findViewById(R.id.chart);
        TextView capacity = findViewById(R.id.capacity);
        initChart();
        capacity.setText(getBatteryCapacity(this));
        new ChartDataUpdateThread().start();
    }

    /**
     * 获取电量
     *
     * @param context context
     * @return 电容量
     */
    public static String getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String generalCategories = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = forName(generalCategories)
                    .getConstructor(Context.class)
                    .newInstance(context);
            batteryCapacity = (double) forName(generalCategories)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(batteryCapacity);
    }

    /**
     * 初始化图表
     */
    private void initChart() {
        entries = new ArrayList<>();
        entries.add(new Entry(0, receiver.getBatteryPct()));
        LineDataSet dataSet = new LineDataSet(entries, "%/m");
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setColor(getResources().getColor(R.color.colorAccent, getTheme()));
        dataSet.setCircleColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
        dataSet.setValueTextSize(10f);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getXAxis().setAxisMaximum(120);
        chart.getXAxis().setValueFormatter(new DefaultAxisValueFormatter(1));
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMaximum(100);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.animateX(1000, Easing.Linear);
        chart.animateY(1000, Easing.Linear);
        chart.invalidate();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * 创建菜单
     *
     * @param menu menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 引入菜单
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * 给菜单创建点击事件
     *
     * @param item menu
     * @return book
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ChartDataUpdateThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                entries.add(new Entry(++index, receiver.getBatteryPct()));
                chart.notifyDataSetChanged();
            }
        }
    }

}