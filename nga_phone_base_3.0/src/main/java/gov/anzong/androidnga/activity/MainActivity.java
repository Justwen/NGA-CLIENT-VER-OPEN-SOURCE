package gov.anzong.androidnga.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.compose.drawer.NavigationDrawerFragment;
import gov.anzong.androidnga.base.util.PermissionUtils;
import gov.anzong.androidnga.base.util.ThemeUtils;
import sp.phone.theme.ThemeManager;
import sp.phone.ui.fragment.dialog.VersionUpgradeDialogFragment;
import sp.phone.util.ActivityUtils;

public class MainActivity extends BaseActivity {

    private boolean mIsNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        setComposeEnabled(true);
        EdgeToEdge.enable((ComponentActivity) this);
        super.onCreate(savedInstanceState);
        ThemeUtils.init(this);
        checkPermission();
        checkNewVersion();
        initView();
        mIsNightMode = ThemeManager.getInstance().isNightMode();
        setTitle(R.string.start_title);
        fixMultiMainActivityIssue();
    }

    private void fixMultiMainActivityIssue() {
        if (!isTaskRoot()) {
            finish();
        }
    }

    private void checkPermission() {
        try {
            PermissionUtils.request(this, null, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } catch (Exception e) {
            // ignore
        }

    }

    private void checkNewVersion() {
        Application app = getApplication();
        if (app instanceof NgaClientApp) {
            if (NgaClientApp.isNewVersion()) {
                new VersionUpgradeDialogFragment().show(getSupportFragmentManager(), null);
            }
        }
    }

    @Override
    protected void onResume() {
        if (mIsNightMode != ThemeManager.getInstance().isNightMode()) {
            finish();
            startActivity(getIntent());
        }
        super.onResume();
    }

    private void initView() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(NavigationDrawerFragment.class.getSimpleName());
        if (fragment == null) {
            fm.beginTransaction().replace(android.R.id.content, new NavigationDrawerFragment(), NavigationDrawerFragment.class.getSimpleName()).commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.REQUEST_CODE_SETTING && resultCode == Activity.RESULT_OK) {
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
