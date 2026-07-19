package sp.phone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.ui.adapter.ActionBarUserListAdapter;

/**
 * Created by Justwen on 2018/1/15.
 */

public class ActionBarUserListSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    private UserManager mUserManager;

    public ActionBarUserListSpinner(Context context) {
        this(context, null);
    }

    public ActionBarUserListSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mUserManager = UserManagerImpl.getInstance();
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != mUserManager.getActiveUserIndex()) {
                    mUserManager.setActiveUser(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setAdapter(new ActionBarUserListAdapter(getContext()));

    }

    @Override
    protected void onFinishInflate() {
        if (UserManagerImpl.getInstance().hasValidUser()) {
            setSelection(mUserManager.getActiveUserIndex());
        }
        super.onFinishInflate();
    }
}
