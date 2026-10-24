package sp.phone.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import io.reactivex.annotations.NonNull;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.User;
import sp.phone.common.UserManagerImpl;
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.data.model.ThreadPostInfo;

import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.mvp.presenter.ArticleListPresenter;
import sp.phone.mvp.viewmodel.ArticleShareViewModel;
import sp.phone.param.ArticleListParam;
import sp.phone.param.ParamKey;
import sp.phone.rxjava.RxEvent;
import sp.phone.task.BookmarkTask;
import sp.phone.ui.adapter.ArticleListAdapter;
import sp.phone.ui.fragment.dialog.BaseDialogFragment;
import sp.phone.ui.fragment.dialog.PostCommentDialogFragment;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;
import gov.anzong.androidnga.common.util.NLog;
import sp.phone.util.StringUtils;
import sp.phone.view.RecyclerViewEx;

/*
 * MD 帖子详情每一页
 */
public class ArticleListFragment extends BaseMvpFragment<ArticleListPresenter> implements ArticleListContract.View {

    private static final String TAG = ArticleListFragment.class.getSimpleName();

    @BindView(R.id.list)
    public RecyclerViewEx mListView;

    @BindView(R.id.loading_view)
    public View mLoadingView;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private ArticleListAdapter mArticleAdapter;

    protected ArticleListParam mRequestParam;

    private OnTopicMenuItemClickListener mMenuItemClickListener = new OnTopicMenuItemClickListener() {

        private ThreadPostInfo mThreadRowInfo;

        @Override
        public void setThreadRowInfo(ThreadPostInfo threadRowInfo) {
            mThreadRowInfo = threadRowInfo;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mPresenter == null) {
                return false;
            }

            ThreadPostInfo row = mThreadRowInfo;
            if (row == null) {
                return false;
            }

            String pidStr = String.valueOf(row.pid);
            String tidStr = String.valueOf(row.tid);
            int tid = row.tid;

            switch (item.getItemId()) {
                case R.id.menu_edit:
                    if (mThreadRowInfo.isComment) {
                        showToast(R.string.cannot_eidt_comment);
                        break;
                    } else {
                        ARouter.getInstance()
                                .build(ARouterConstants.ACTIVITY_POST)
                                .withString(ParamKey.KEY_PID, pidStr)
                                .withString(ParamKey.KEY_TID, tidStr)
                                .withString("title", StringUtils.unEscapeHtml(row.subject))
                                .withString("action", "modify")
                                .withString("prefix", StringUtils.unEscapeHtml(StringUtils.removeBrTag(row.rawContent)))
                                .navigation(getActivity(), ActivityUtils.REQUEST_CODE_LOGIN);
                    }
                    break;
                case R.id.menu_post_comment:
                    mPresenter.postComment(mRequestParam, row);
                    break;
                case R.id.menu_report:
                    FunctionUtils.handleReport(row, mRequestParam.tid, getFragmentManager());
                    break;
                case R.id.menu_signature:
                    if (row.isAnonymous) {
                        ActivityUtils.showToast("这白痴匿名了,神马都看不到");
                    } else {
                        FunctionUtils.Create_Signature_Dialog(row, getActivity(),
                                mListView);
                    }
                    break;
                case R.id.menu_vote:
                    FunctionUtils.createVoteDialog(row, getActivity(), mListView, mToast);
                    break;
                case R.id.menu_ban_this_one:
                    mPresenter.banThisSB(row);
                    break;
                case R.id.menu_show_this_person_only:
                    ARouter.getInstance()
                            .build(ARouterConstants.ACTIVITY_TOPIC_CONTENT)
                            .withString("tab", "1")
                            .withInt(ParamKey.KEY_TID, tid)
                            .withInt(ParamKey.KEY_AUTHOR_ID, row.authorId)
                            .withInt("fromreplyactivity", 1)
                            .navigation();
                    break;
                case R.id.menu_support:
                    mPresenter.postSupportTask(tid, row.pid);
                    break;
                case R.id.menu_oppose:
                    mPresenter.postOpposeTask(tid, row.pid);
                    break;
                case R.id.menu_favorite:
                    BookmarkTask.execute(tidStr, pidStr);
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private View.OnClickListener mMenuTogglerListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mMenuItemClickListener.setThreadRowInfo((ThreadPostInfo) view.getTag());
            int menuId;
            if (mRequestParam.pid == 0) {
                menuId = R.menu.article_list_context_menu;
            } else {
                menuId = R.menu.article_list_context_menu_with_tid;
            }
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.inflate(menuId);
            onPrepareOptionsMenu(popupMenu.getMenu(), (ThreadPostInfo) view.getTag());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(mMenuItemClickListener);
        }

        private void onPrepareOptionsMenu(Menu menu, ThreadPostInfo row) {
            MenuItem item = menu.findItem(R.id.menu_ban_this_one);
            if (item != null) {
                item.setTitle(row.isBlocked ? R.string.cancel_ban_thisone : R.string.ban_thisone);
            }

            if (row.isComment) {
                item = menu.findItem(R.id.menu_post_comment);
                if (item != null) {
                    item.setVisible(false);
                }
                item = menu.findItem(R.id.menu_show_this_person_only);
                if (item != null) {
                    item.setVisible(false);
                }
            }

            item = menu.findItem(R.id.menu_vote);
            if (item != null && StringUtils.isEmpty(row.vote)) {
                item.setVisible(false);
            }

            item = menu.findItem(R.id.menu_edit);
            if (item != null) {
                User user = UserManagerImpl.getInstance().getActiveUser();
                if (user == null || !user.getUserId().equals(String.valueOf(row.authorId))) {
                    item.setVisible(false);
                }
            }
        }

    };

    private View.OnClickListener mSupportListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ThreadPostInfo row = (ThreadPostInfo) view.getTag();
            mPresenter.postSupportTask(row.tid, row.pid);
        }
    };

    private View.OnClickListener mOpposeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ThreadPostInfo row = (ThreadPostInfo) view.getTag();
            mPresenter.postOpposeTask(row.tid, row.pid);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        NLog.d(TAG, "onCreate");
        mRequestParam = getArguments().getParcelable(ParamKey.KEY_PARAM);
        registerRxBus();

        initData();
        super.onCreate(savedInstanceState);
    }

    private void initData() {
        ArticleShareViewModel viewModel = getActivityViewModelProvider().get(ArticleShareViewModel.class);
        viewModel.getRefreshPage().observe(this, page -> {
            if (page == mRequestParam.page) {
                loadPage();
            }
        });

        viewModel.getCachePage().observe(this, page -> {
            if (page == mRequestParam.page) {
                mPresenter.cachePage();
            }
        });
    }

    @Override
    protected void accept(@NonNull RxEvent rxEvent) {
        if (rxEvent.what == RxEvent.EVENT_ARTICLE_GO_FLOOR
                && rxEvent.arg + 1 == mRequestParam.page
                && rxEvent.obj != null) {
            mListView.scrollToPosition((Integer) rxEvent.obj);
        }
    }

    @Override
    protected ArticleListPresenter onCreatePresenter() {
        return new ArticleListPresenter(mRequestParam);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        ((BaseActivity) getActivity()).setupToolbar();
        mArticleAdapter = new ArticleListAdapter(getContext(),getActivity().getSupportFragmentManager());
        mArticleAdapter.setSupportListener(mSupportListener);
        mArticleAdapter.setOpposeListener(mOpposeListener);
        mArticleAdapter.setMenuTogglerListener(mMenuTogglerListener);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setItemViewCacheSize(20);
        mListView.setAdapter(mArticleAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        if (PhoneConfiguration.getInstance().useSolidColorBackground()) {
            mListView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        }

        TextView sayingView = (TextView) mLoadingView.findViewById(R.id.saying);
        sayingView.setText(ActivityUtils.getSaying());

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadPage() {
        mPresenter.loadPage(mRequestParam);
    }

    @Override
    public void setData(ThreadInfo data) {
        ArticleShareViewModel viewModel = getActivityViewModelProvider().get(ArticleShareViewModel.class);
        if (getActivity() != null && data != null) {
            viewModel.setReplyCount(data.totalRows);
        }
        if (data != null && data.pageInfo != null && getActivity() != null && mRequestParam.title == null) {
            getActivity().setTitle(data.pageInfo.getSubject());
        }

        mArticleAdapter.setData(data);
        mArticleAdapter.notifyDataSetChanged();

    }

    @Override
    public void startPostActivity(Intent intent) {
        if (!StringUtils.isEmpty(UserManagerImpl.getInstance().getUserName())) {// 登入了才能发
            intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
        } else {
            ActivityUtils.startLoginActivity(getActivity());
        }
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_TOPIC_POST);
    }

    @Override
    public void showPostCommentDialog(String prefix, Bundle bundle) {
        BaseDialogFragment df = new PostCommentDialogFragment();
        df.setArguments(bundle);
        df.show(getActivity().getSupportFragmentManager());
    }


    @Override
    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout.isShown()) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isShown() ? mSwipeRefreshLayout.isRefreshing() : mLoadingView.isShown();
    }

    @Override
    public void hideLoadingView() {
        mLoadingView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    interface OnTopicMenuItemClickListener extends PopupMenu.OnMenuItemClickListener {

        void setThreadRowInfo(ThreadPostInfo threadRowInfo);

    }


}
