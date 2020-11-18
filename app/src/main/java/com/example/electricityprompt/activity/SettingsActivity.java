package com.example.electricityprompt.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.example.electricityprompt.R;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;

/**
 * @author Administrator
 */
public class SettingsActivity extends AppCompatActivity {

    private static CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        coordinatorLayout = findViewById(R.id.settingCoordinatorLayout);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final int MAX_LENGTH = 100;
        private final int MIN_LENGTH = 0;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            EditTextPreference promptCharge = findPreference("promptCharge");
            assert promptCharge != null;

            promptCharge.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // 判断用户是否有输入
                    if (newValue == null || TextUtils.isEmpty((CharSequence) newValue)) {
                        Snackbar.make(coordinatorLayout, R.string.settings_notEmpty, Snackbar.LENGTH_SHORT).show();
                        return false;
                    }

                    // 判断用户输入的内容范围
                    int size = Integer.parseInt((String) newValue);
                    if (size < MIN_LENGTH || size > MAX_LENGTH) {
                        Snackbar.make(coordinatorLayout, R.string.settings_electricityRange, Snackbar.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                }
            });

            promptCharge.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull @NotNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setFilters(new InputFilter[]{
                            new InputFilter.LengthFilter(3),
                    });
                }
            });
        }
    }
}