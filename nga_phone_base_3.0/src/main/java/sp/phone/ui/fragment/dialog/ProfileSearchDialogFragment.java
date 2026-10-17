package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.justwen.androidnga.base.activity.ARouterConstants;

import java.util.Objects;

import gov.anzong.androidnga.R;
import sp.phone.common.UserManagerImpl;
import sp.phone.util.StringUtils;

public class ProfileSearchDialogFragment extends BaseDialogFragment {

    private RadioGroup mSearchRadio;

    private RadioButton mSearchName;

    private RadioButton mSearchId;

    private EditText mEditText;

    private void getViews(View view) {
        mSearchRadio = (RadioGroup) view.findViewById(R.id.radioGroup);
        mSearchName = (RadioButton) mSearchRadio.findViewById(R.id.profilesearch_name);
        mSearchId = (RadioButton) view.findViewById(R.id.profilesearch_id);
        mEditText = (EditText) view.findViewById(R.id.search_data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile_search, null);
        getViews(view);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    getDialog().dismiss();
                    handleSearch();
                    return true;
                }
                return false;
            }
        });
        builder.setView(view)
                .setTitle(R.string.profile_search_hint)
                .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleSearch();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private void handleSearch() {
        final String inputString = mEditText.getText().toString().trim().replaceAll("\\n", "");
        if (mSearchRadio.getCheckedRadioButtonId() == mSearchName.getId()) {//用户名
            searchName(inputString);
        } else {
            searchId(inputString);
        }
    }

    private void searchName(String inputString) {
        if (TextUtils.isEmpty(inputString)) {
            inputString = UserManagerImpl.getInstance().getUserName();
            if (TextUtils.isEmpty(inputString)) {
                return;
            }
        }
        startProfileActivity("", inputString);
    }

    private void startProfileActivity(String uid, String userName) {
        ARouter.getInstance().build(ARouterConstants.ACTIVITY_PROFILE)
                .withString("uid", uid).withString("username", userName).navigation();
    }

    private void searchId(String inputString) {
        if (!StringUtils.isEmpty(inputString)) {
            startProfileActivity(inputString, "");
        } else {
            String userName = UserManagerImpl.getInstance().getUserName();
            if (!Objects.equals(userName, "")) {
                startProfileActivity("", userName);
            }
        }
    }


}
