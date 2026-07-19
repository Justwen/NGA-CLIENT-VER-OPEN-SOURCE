package gov.anzong.androidnga.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.justwen.androidnga.cloud.CloudServerManager;

import java.io.File;
import java.util.Arrays;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.base.util.ShareUtils;
import sp.phone.http.OnSimpleHttpCallBack;

/**
 * 显示图片
 * Created by Elrond on 2015/11/18.
 */
public class ImageZoomActivity extends BaseActivity {

    public static final String KEY_GALLERY_URLS = "keyGalleryUrl";

    public static final String KEY_GALLERY_RECT = "keyGalleryRect";

    public static final String KEY_GALLERY_CUR_URL = "keyGalleryCurUrl";

    private String[] mGalleryUrls;

    private int mPageIndex;

    private TextView mTxtView;

    private ProgressBar mProgressBar;

    private ViewPager mViewPager;

    private SaveImageTask mSaveImageTask;

    private SaveImageTask.DownloadResult[] mDownloadResults;

    private String mCurrentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        receiveIntent();
        initBottomView();
        initGallery();
        initActionBar();

    }

    private void initActionBar() {
        setupToolbar();
    }

    private void initGallery() {
        mViewPager = (ViewPager) findViewById(R.id.gallery);
        GalleryAdapter adapter = new GalleryAdapter(this, mGalleryUrls);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mPageIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPageIndex = position;
                setTitle((position + 1) + " / " + mGalleryUrls.length);
                //  mTxtView.setText(String.valueOf(position + 1) + " / " + String.valueOf(mGalleryUrls.length));
            }
        });
    }

    private void receiveIntent() {
        Intent intent = getIntent();
        mGalleryUrls = intent.getStringArrayExtra(KEY_GALLERY_URLS);
        mCurrentUrl = intent.getStringExtra(KEY_GALLERY_CUR_URL);
        if (mGalleryUrls == null ) {
            mGalleryUrls = new String[1];
            mGalleryUrls[0] = mCurrentUrl;
        }
        mPageIndex = Arrays.asList(mGalleryUrls).indexOf(mCurrentUrl);
        if (mPageIndex < 0) {
            mPageIndex = 0;
        }
        mDownloadResults = new SaveImageTask.DownloadResult[mGalleryUrls.length];
    }

    private void initBottomView() {
//        mTxtView = (TextView) findViewById(R.id.reader_image_desc);
//        mTxtView.setMovementMethod(new ScrollingMovementMethod());11
//        mTxtView.setText(String.valueOf(mPageIndex + 1) + " / " + String.valueOf(mGalleryUrls.length));
//        ImageView download = (ImageView) findViewById(R.id.reader_image_download);
//        download.setOnClickListener(v -> saveBitmap(mGalleryUrls[mPageIndex >= 0 ? mPageIndex : 0]));
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);
        setTitle((mPageIndex + 1) + " / " + mGalleryUrls.length);
    }

    private void saveBitmap(OnSimpleHttpCallBack<SaveImageTask.DownloadResult> callBack, String... urls) {
        if (mSaveImageTask == null) {
            mSaveImageTask = new SaveImageTask();
        }
        mSaveImageTask.execute(callBack, urls);
    }

    private void saveBitmap(String... urls) {
        saveBitmap(data -> {
            for (int i = 0; i < mGalleryUrls.length; i++) {
                if (mGalleryUrls[i].equals(data.url)) {
                    mDownloadResults[i] = data;
                    break;
                }
            }
        }, urls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_zoom, menu);
        return true;
    }

    private void share(File file) {
        ShareUtils.INSTANCE.shareImage(this, file);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (mDownloadResults.length == 0) {
                    CloudServerManager.putCrashData(this, "mCurrentUrl", mCurrentUrl);
                }
                if (mDownloadResults[mPageIndex] != null) {
                    share(mDownloadResults[mPageIndex].file);
                } else {
                    saveBitmap(data -> {
                        for (int i = 0; i < mGalleryUrls.length; i++) {
                            if (mGalleryUrls[i].equals(data.url)) {
                                mDownloadResults[i] = data;
                                break;
                            }
                        }
                        share(data.file);
                    }, mGalleryUrls[mPageIndex]);
                }
                break;
            case R.id.menu_download_all:
                showDownloadAllDialog();
                break;
            case R.id.menu_download:
                saveBitmap(mGalleryUrls[mPageIndex]);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showDownloadAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否要下载全部图片 ？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveBitmap(mGalleryUrls);

                    }
                }).setNegativeButton(android.R.string.cancel, null).create().show();
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }
}
