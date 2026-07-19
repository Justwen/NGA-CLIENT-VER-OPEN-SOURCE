package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.compose.board.ForumBoardViewModel;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.ui.fragment.TopicListSimpleFragment;
import sp.phone.param.ParamKey;
import sp.phone.param.TopicListParam;
import sp.phone.ui.fragment.TopicFavoriteFragment;
import sp.phone.ui.fragment.TopicListFragment;
import sp.phone.util.ActivityUtils;
import sp.phone.util.StringUtils;

/**
 * 帖子列表
 */
@Route(path = ARouterConstants.ACTIVITY_TOPIC_LIST)
public class TopicListActivity extends BaseActivity {

    private static String TAG = TopicListActivity.class.getSimpleName();

    private TopicListParam mRequestParam;

    private TopicListParam getRequestParam() {

        Bundle bundle = getIntent().getExtras();
        String url = getIntent().getDataString();
        TopicListParam requestParam = null;
        if (url != null) {
            requestParam = new TopicListParam();
            requestParam.authorId = StringUtils.getUrlParameter(url, "authorid");
            requestParam.searchPost = StringUtils.getUrlParameter(url, "searchpost");
            requestParam.favor = StringUtils.getUrlParameter(url, "favor");
            requestParam.key = StringUtils.getStringBetween(url, 0, "key=", "&").result;
            requestParam.author = StringUtils.getStringBetween(url, 0, "author=", "&").result;
            requestParam.fidGroup = StringUtils.getStringBetween(url, 0, "fidgroup=", "&").result;
            requestParam.content = StringUtils.getUrlParameter(url, "content");
            requestParam.fid = StringUtils.getUrlParameter(url, "fid");
            requestParam.stid = StringUtils.getUrlParameter(url, "stid");
        } else if (bundle != null) {
            requestParam = bundle.getParcelable(ParamKey.KEY_PARAM);
            if (requestParam == null) {
                requestParam = new TopicListParam();
                requestParam.fid = bundle.getInt(ParamKey.KEY_FID, 0);
                requestParam.authorId = bundle.getInt(ParamKey.KEY_AUTHOR_ID, 0);
                requestParam.content = bundle.getInt(ParamKey.KEY_CONTENT, 0);
                requestParam.searchPost = bundle.getInt(ParamKey.KEY_SEARCH_POST, 0);
                requestParam.favor = bundle.getInt(ParamKey.KEY_FAVOR, 0);
                requestParam.key = bundle.getString(ParamKey.KEY_KEY);
                requestParam.author = bundle.getString(ParamKey.KEY_AUTHOR);
                requestParam.fidGroup = bundle.getString(ParamKey.KEY_FID_GROUP);
                requestParam.title = bundle.getString(ParamKey.KEY_TITLE);
                requestParam.recommend = bundle.getInt(ParamKey.KEY_RECOMMEND, 0);
                requestParam.twentyfour = bundle.getInt(ParamKey.KEY_TWENTYFOUR, 0);
                requestParam.stid = bundle.getInt(ParamKey.KEY_STID, 0);
                requestParam.boardHead = bundle.getString(ParamKey.BOARD_HEAD, null);
            }
        }

        if (requestParam != null && TextUtils.isEmpty(requestParam.title)) {
            requestParam.title = ForumBoardViewModel.INSTANCE.getBoardName(requestParam.fid,requestParam.stid);
        }
        return requestParam;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        mRequestParam = getRequestParam();
        super.onCreate(savedInstanceState);
        if (mRequestParam != null) {
            setupFragment();
        } else {
            finish();
        }
    }

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(android.R.id.content) == null) {
            Fragment fragment;
            if (mRequestParam.favor != 0) {
                fragment = new TopicFavoriteFragment();
            } else if (isBoardTopicList()) {
                fragment = new TopicListFragment();
            } else {
                fragment = new TopicListSimpleFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(ParamKey.KEY_PARAM, mRequestParam);
            fragment.setArguments(bundle);
            fm.beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }

    private boolean isBoardTopicList() {
        return mRequestParam.recommend == 0
                && mRequestParam.twentyfour == 0
                && mRequestParam.key == null
                && mRequestParam.favor == 0
                && mRequestParam.authorId == 0
                && mRequestParam.author == null
                && mRequestParam.searchPost == 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_favorite:
                ActivityUtils.startFavoriteTopicActivity(this);
                break;
            case R.id.menu_recommend:
                showRecommendTopicList();
                break;
            case R.id.menu_twenty_four:
                showTwentyFourList();
                break;
            case R.id.menu_search:
                ARouter.getInstance()
                        .build(ARouterConstants.ACTIVITY_SEARCH)
                        .withInt("fid", mRequestParam.fid)
                        .navigation(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showRecommendTopicList() {
        TopicListParam param = (TopicListParam) mRequestParam.clone();
        param.recommend = 1;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ParamKey.KEY_PARAM, param);
        intent.putExtras(bundle);
        ActivityUtils.startRecommendTopicActivity(this, intent);
    }

    private void showTwentyFourList() {
        TopicListParam param = (TopicListParam) mRequestParam.clone();
        param.twentyfour = 1;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ParamKey.KEY_PARAM, param);
        intent.putExtras(bundle);
        ActivityUtils.startTwentyFourActivity(this, intent);
    }

}
