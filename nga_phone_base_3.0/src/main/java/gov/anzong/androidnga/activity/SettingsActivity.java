package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ThemeUtils;
import sp.phone.ui.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    public static boolean sRecreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupFragment();
        setupActionBar();
        if (sRecreated) {
            getWindow().setWindowAnimations(android.R.style.Animation_Toast);
            sRecreated = false;
            ThemeUtils.init(this);
            setResult(Activity.RESULT_OK);
            findViewById(android.R.id.content).post(() -> startAnimation(findViewById(android.R.id.content)));
        }
    }
    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment settingsFragment = fm.findFragmentByTag(SettingsFragment.class.getSimpleName());
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            fm.beginTransaction().replace(R.id.container, settingsFragment, SettingsFragment.class.getSimpleName()).commit();
        }
    }

    private void startAnimation(View contentView) {
        int cx = contentView.getWidth() / 2;
        int cy = contentView.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius).start();
    }

    @Override
    protected void onDestroy() {
        sRecreated = false;
        super.onDestroy();
    }
}
