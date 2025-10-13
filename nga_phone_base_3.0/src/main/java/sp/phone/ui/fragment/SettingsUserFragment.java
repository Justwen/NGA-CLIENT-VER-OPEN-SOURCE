package sp.phone.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.ui.adapter.UserListAdapter;
import sp.phone.util.ActivityUtils;
import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2017/12/17.
 */

public class SettingsUserFragment extends BaseFragment implements View.OnClickListener {

    private UserListAdapter mListAdapter;

    private RecyclerViewEx mListView;

    private UserManager mUserManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.setting_title_user);
        setHasOptionsMenu(true);
        mUserManager = UserManagerImpl.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_user, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        mListAdapter = new UserListAdapter(getContext(),mUserManager.getUserList());
        mListAdapter.setOnClickListener(this);

        mListView = view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setAdapter(mListAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mUserManager.removeUser(viewHolder.getAdapterPosition());
                mListAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                for (int i = 0; i < mListAdapter.getItemCount(); i++) {
                    mListAdapter.notifyItemChanged(i);
                }
            }
        });
        //将recycleView和ItemTouchHelper绑定
        touchHelper.attachToRecyclerView(mListView);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_user_option_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_user) {
            ActivityUtils.startLoginActivity(getContext());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        int position = (int) v.getTag();

        if (v instanceof Checkable) {
            if (((Checkable) v).isChecked()) {
                setActiveUser(position);
            } else {
                ((Checkable) v).setChecked(true);
            }
        } else {
            showUserProfile(position);
        }

    }

    private void setActiveUser(int position) {
        mUserManager.setActiveUser(position);
        for (int i = 0; i < mListAdapter.getItemCount(); i++) {
            if (i != position) {
                mListAdapter.notifyItemChanged(i);
            }
        }
    }

    private void showUserProfile(int position) {
        String userName = mUserManager.getUserList().get(position).getNickName();
        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().profileActivityClass);
        intent.putExtra("mode", "username");
        intent.putExtra("username", userName);
        startActivity(intent);
    }
}
