package sp.phone.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;

import java.io.IOException;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.activity.LauncherSubActivity;
import gov.anzong.androidnga.activity.SettingsActivity;
import gov.anzong.androidnga.activity.compose.TemplateComposeActivity;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.ui.fragment.BasePreferenceFragment;
import sp.phone.common.UserManagerImpl;
import sp.phone.theme.ThemeManager;
import sp.phone.ui.fragment.dialog.AlertDialogFragment;

public class SettingsFragment extends BasePreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        mapping(getPreferenceScreen());
        configPreference();
    }

    private void configPreference() {
        findPreference(PreferenceKey.NIGHT_MODE).setEnabled(!ThemeManager.getInstance().isNightModeFollowSystem());
        findPreference(PreferenceKey.MATERIAL_THEME).setEnabled(!ThemeManager.getInstance().isNightMode());

        findPreference(PreferenceKey.KEY_CLEAR_CACHE).setOnPreferenceClickListener(preference -> {
            showClearCacheDialog();
            return true;
        });

    }

    private void showClearCacheDialog() {
        AlertDialogFragment dialogFragment = AlertDialogFragment.create("确认要清除缓存吗？");
        dialogFragment.setPositiveClickListener((dialog, which) -> clearCache());
        dialogFragment.show(((BaseActivity)getActivity()).getSupportFragmentManager(),"clear_cache");
    }

    private void clearCache() {
        ThreadUtils.postOnSubThread(() -> {
            // 清除glide缓存
            Glide.get(ContextUtils.getContext()).clearDiskCache();
            // 清除avatar数据
            UserManagerImpl.getInstance().clearAvatarUrl();
            // 清除之前的使用过的awp缓存数据
            try {
                FileUtils.deleteDirectory(ContextUtils.getContext().getDir("awp", Context.MODE_PRIVATE));
                FileUtils.deleteDirectory(ContextUtils.getContext().getDir("sogou_webview", Context.MODE_PRIVATE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ToastUtils.success("缓存清除成功");
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.menu_setting);
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference instanceof ListPreference) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        String key = preference.getKey();
        switch (key) {
            case PreferenceKey.NIGHT_MODE:
                SettingsActivity.sRecreated = true;
                break;
            case PreferenceKey.KEY_NIGHT_MODE_FOLLOW_SYSTEM:
                findPreference(PreferenceKey.NIGHT_MODE).setEnabled(Boolean.FALSE.equals(newValue));
                SettingsActivity.sRecreated = true;
                break;
            case PreferenceKey.MATERIAL_THEME:
                SettingsActivity.sRecreated = true;
                ThreadUtils.postOnMainThreadDelay(() -> {
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                }, 200);
                break;
            default:
                break;

        }
        return true;
    }

    private void setFullScreen(boolean fullScreen) {
        int flag;
        if (fullScreen) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        Intent intent;
        if (preference.getFragment() != null && (preference.getFragment().contains("compose") || preference.getKey().contains("compose"))) {
            intent = new Intent(getActivity(), TemplateComposeActivity.class);
            intent.putExtra("fragment", preference.getFragment());
            startActivity(intent);
            return true;
        } else if (preference.getFragment() != null) {
            intent = new Intent(getActivity(), LauncherSubActivity.class);
            intent.putExtra("fragment", preference.getFragment());
            startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

}
