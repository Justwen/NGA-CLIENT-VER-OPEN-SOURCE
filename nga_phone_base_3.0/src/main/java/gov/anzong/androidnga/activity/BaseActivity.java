package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.justwen.androidnga.cloud.CloudServerManager;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.common.NotificationController;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;
import gov.anzong.androidnga.common.util.NLog;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected PhoneConfiguration mConfig;

    private boolean mToolbarEnabled;

    private boolean mComposeEnabled;

    private int mNaviBarHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mConfig = PhoneConfiguration.getInstance();
        updateThemeUi();
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().initializeWebTheme(this);

        try {
            if (ThemeManager.getInstance().isNightMode() && !mComposeEnabled) {
                getWindow().setNavigationBarColor(ContextUtils.getColor(R.color.background_color));
            }
        } catch (Exception e) {
            NLog.e("set navigation bar color exception occur: " + e);
        }
        enableEdge2Edge();
    }

    public void setComposeEnabled(boolean composeEnabled) {
        mComposeEnabled = composeEnabled;
    }

    private void enableEdge2Edge() {
        View contentView = findViewById(android.R.id.content);
        if (mToolbarEnabled && !mComposeEnabled && contentView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(contentView, new OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                    if (getWindow().getDecorView().findViewById(R.id.status_bar) == null) {
                        Insets stateBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                        ViewGroup parent = (ViewGroup) contentView.getParent();
                        View statusView = new View(contentView.getContext());
                        statusView.setId(R.id.status_bar);
                        statusView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, stateBars.top));
                        statusView.setBackgroundColor(ThemeManager.getInstance().getPrimaryColor(contentView.getContext()));
                        parent.addView(statusView, 0);

                        Insets navaBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                        mNaviBarHeight = navaBars.bottom;
                        contentView.setPadding(0, 0, 0, navaBars.bottom);
                    }
                    return insets;
                }
            });
        }
    }

    // Android15上开启EdgeToEdge后adjustResize会失效，这里临时做下兼容
    protected void compatActivityAdjustResize(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return;
        }
        View content = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        final Rect r = new Rect();
        content.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            content.getWindowVisibleDisplayFrame(r);
            int screenHeight = content.getRootView().getHeight();
            int keyboardHeight = screenHeight - r.bottom - mNaviBarHeight;
            if (keyboardHeight > screenHeight / 4) { // 键盘高度超过屏幕1/4
                content.setPadding(0, 0, 0, keyboardHeight);
            } else {
                content.setPadding(0, 0, 0, 0);
            }
        });
    }

    protected void setToolbarEnabled(boolean enabled) {
        mToolbarEnabled = enabled;
    }

    public void setupToolbar(Toolbar toolbar) {
        if (toolbar != null && getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    public void setupToolbar() {
        setupToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void setupActionBar() {
        if (mToolbarEnabled) {
            setupToolbar();
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    protected void updateThemeUi() {
        ThemeManager tm = ThemeManager.getInstance();
        setTheme(tm.getTheme(mToolbarEnabled));
    }

    @Deprecated
    public void setupActionBar(Toolbar toolbar) {
        if (toolbar != null && getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return super.dispatchKeyEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        view.setFitsSystemWindows(!mToolbarEnabled);
    }

    @Override
    protected void onResume() {
        checkUpgrade();
        NotificationController.getInstance().checkNotificationDelay();
        super.onResume();
    }

    private void checkUpgrade() {
        if (PreferenceUtils.getData(PreferenceKey.KEY_CHECK_UPGRADE_STATE, true)) {
            long time = PreferenceUtils.getData(PreferenceKey.KEY_CHECK_UPGRADE_TIME, 0L);
            if (System.currentTimeMillis() - time > 1000 * 60 * 60 * 24) {
                CloudServerManager.checkUpgrade();
                PreferenceUtils.putData(PreferenceKey.KEY_CHECK_UPGRADE_TIME, System.currentTimeMillis());
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        try {
            super.startActivityForResult(intent, requestCode, options);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
