package gov.anzong.androidnga.base.widget;

import android.app.Activity;
import android.graphics.Color;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import net.steamcrafted.loadtoast.LoadToast;

import java.lang.reflect.Field;
import java.util.Objects;

import gov.anzong.androidnga.base.util.ThemeUtils;

public class ProgressBarEx {

    private LoadToast mLoadToast;

    public ProgressBarEx(Activity activity) {
        setupWithActivity(activity);
    }

    public ProgressBarEx(Fragment fragment) {
        setupWithFragment(fragment);
    }

    public ProgressBarEx(ViewGroup parentView) {
        setupWithViewGroup(parentView);
    }

    private void setupWithFragment(Fragment fragment) {
        mLoadToast = new LoadToast(fragment.requireActivity());
        mLoadToast.setBackgroundColor(ThemeUtils.getAccentColor());
        mLoadToast.setTextColor(Color.WHITE);
        mLoadToast.setProgressColor(Color.WHITE);

        try {
            ViewGroup parentView = (ViewGroup) fragment.getView().getParent();
            Field field = LoadToast.class.getDeclaredField("mParentView");
            field.setAccessible(true);
            field.set(mLoadToast, parentView);
            mLoadToast.setTranslationY(parentView.getTop() + Objects.requireNonNull(parentView).getHeight() / 2);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
            setupWithActivity(fragment.getActivity());
        }
    }

    private void setupWithActivity(Activity activity) {
        mLoadToast = new LoadToast(activity);
        mLoadToast.setBackgroundColor(ThemeUtils.getAccentColor());
        mLoadToast.setTextColor(Color.WHITE);
        mLoadToast.setProgressColor(Color.WHITE);
        mLoadToast.setTranslationY(activity.getResources().getDisplayMetrics().heightPixels / 2);
    }

    private void setupWithViewGroup(ViewGroup parentView) {
        mLoadToast = new LoadToast(parentView.getContext());
        mLoadToast.setBackgroundColor(ThemeUtils.getAccentColor());
        mLoadToast.setTextColor(Color.WHITE);
        mLoadToast.setProgressColor(Color.WHITE);

        try {
            Field field = LoadToast.class.getDeclaredField("mParentView");
            field.setAccessible(true);
            field.set(mLoadToast, parentView);
            mLoadToast.setTranslationY(parentView.getTop() + Objects.requireNonNull(parentView).getHeight() / 2);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
            setupWithActivity((Activity) parentView.getContext());
        }
    }

    public void show(String text) {
        mLoadToast.setText(text);
        mLoadToast.show();
    }

    public void success() {
        mLoadToast.success();
    }

    public void error() {
        mLoadToast.error();
    }

    public void hide() {
        mLoadToast.hide();
    }
}
