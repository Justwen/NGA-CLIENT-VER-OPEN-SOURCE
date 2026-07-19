package sp.phone.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.base.util.ToastUtils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.util.ActivityUtils;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    protected BaseActivity mActivity;

    protected Toast mToast;

    protected PhoneConfiguration mConfig = PhoneConfiguration.getInstance();

    private int mTitleId;

    private CharSequence mTitleStr;

    private ViewModelProvider mActivityViewModelProvider;

    @Deprecated
    public void showToast(int res) {
        String str = getString(res);
        showToast(str);
    }

    @Deprecated
    public void showToast(String res) {
        ToastUtils.info(res);
    }

    public void finish() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    protected void setupToolbar(Toolbar toolbar) {
        if (mActivity != null) {
            mActivity.setupToolbar(toolbar);
        }
    }

    protected ActionBar getSupportActionBar() {
        return mActivity != null ? mActivity.getSupportActionBar() : null;
    }

    public void setTitle(String title) {
        mTitleStr = title;
        mTitleId = 0;
    }

    public void setTitle(int resId) {
        mTitleId = resId;
        mTitleStr = null;
    }

    public void setResult(int resultCode) {
        if (mActivity != null) {
            mActivity.setResult(resultCode);
        }
    }

    @Override
    public void onResume() {
        if (mActivity != null) {
            if (mTitleStr != null) {
                mActivity.setTitle(mTitleStr);
            } else if (mTitleId > 0) {
                mActivity.setTitle(mTitleId);
            }
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFitsSystemWindows(false);
    }

    protected FragmentManager getSupportFragmentManager() {
        return getChildFragmentManager();
    }

    @Override
    public void onAttach(Context context) {
        mActivity = (BaseActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history:
                ActivityUtils.startHistoryTopicActivity(getContext());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ViewModelProvider getActivityViewModelProvider() {
        if (mActivityViewModelProvider == null) {
            mActivityViewModelProvider = new ViewModelProvider(mActivity);
        }
        return mActivityViewModelProvider;
    }

}
