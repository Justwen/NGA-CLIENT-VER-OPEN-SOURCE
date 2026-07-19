package gov.anzong.androidnga.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.ui.fragment.BasePreferenceFragment;
import sp.phone.ui.fragment.BaseFragment;

public class LauncherSubActivity extends BaseActivity {

    private BaseFragment mBaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_template);
        setupToolbar();
        String fragmentStr = intent.getStringExtra("fragment");
        if (fragmentStr != null) {
            commitFragment(fragmentStr);
        }
    }

    private void commitFragment(String fragmentStr) {
        try {
            Object fragment = Class.forName(fragmentStr).newInstance();
            if (fragment instanceof BaseFragment) {
                mBaseFragment = (BaseFragment) fragment;
                Bundle bundle = getIntent().getExtras();
                mBaseFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, mBaseFragment).commit();
            } else if (fragment instanceof BasePreferenceFragment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, (BasePreferenceFragment)fragment).commit();
            } else {
                Bundle bundle = getIntent().getExtras();
                ((Fragment) fragment).setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.main_content, (Fragment) fragment).commit();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBaseFragment == null || !mBaseFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
