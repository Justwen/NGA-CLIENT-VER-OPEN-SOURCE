package gov.anzong.androidnga.activity;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.FragmentTabHost;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.ui.fragment.SearchHistoryBoardFragment;
import sp.phone.ui.fragment.SearchHistoryTopicFragment;
import sp.phone.ui.fragment.SearchHistoryUserFragment;

@Route(path = ARouterConstants.ACTIVITY_SEARCH)
public class SearchActivity extends BaseActivity {

    public static final String SEARCH_MODE_USER = "1";

    public static final String SEARCH_MODE_TOPIC = "2";

    public static final String SEARCH_MODE_BOARD = "3";

    private String mCurrentMode;

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activty);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.real_content);
        Bundle bundle = new Bundle();
        bundle.putString("mode", SEARCH_MODE_TOPIC);
        bundle.putInt("fid", getIntent().getIntExtra("fid", 0));
        tabHost.addTab(tabHost.newTabSpec(SEARCH_MODE_TOPIC).setIndicator("主题"), SearchHistoryTopicFragment.class, bundle);

        bundle = new Bundle();
        bundle.putString("mode", SEARCH_MODE_BOARD);
        tabHost.addTab(tabHost.newTabSpec(SEARCH_MODE_BOARD).setIndicator("板块"), SearchHistoryBoardFragment.class, bundle);

        bundle = new Bundle();
        bundle.putString("mode", SEARCH_MODE_USER);
        tabHost.addTab(tabHost.newTabSpec(SEARCH_MODE_USER).setIndicator("用户"), SearchHistoryUserFragment.class, bundle);
        tabHost.setOnTabChangedListener(this::updateTabChanged);

    }

    private void updateTabChanged(String mode) {
        mCurrentMode = mode;
        if (mEditText == null) {
            return;
        }
        if (mCurrentMode.equals(SEARCH_MODE_USER)) {
            mEditText.setHint(R.string.profile_search_dialog_hint);
        } else {
            mEditText.setHint(R.string.search_dialog_hint);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.expandActionView();
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                SearchActivity.this.finish();
                return true;
            }
        });
        SearchView searchView = (SearchView) item.getActionView();
        mEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        mEditText.setCursorVisible(true);
        if (Build.VERSION.SDK_INT >= 29) {
            mEditText.setTextCursorDrawable(R.drawable.text_cursor_drawable);
        }
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            query(v.getText());
            return true;
        });
        updateTabChanged(SEARCH_MODE_TOPIC);
        return true;
    }

    private void query(CharSequence query) {
        if (query == null) {
            query = "";
        }
        SearchHistoryBoardFragment fragment = (SearchHistoryBoardFragment) getSupportFragmentManager().findFragmentByTag(mCurrentMode);
        if (fragment != null) {
            fragment.query(query.toString().trim().replaceAll("\\n", ""));
        }
    }

}
