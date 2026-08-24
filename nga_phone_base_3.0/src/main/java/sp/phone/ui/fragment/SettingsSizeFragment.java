package sp.phone.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhouyou.view.seekbar.SignSeekBar;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.widget.SeekBarEx;
import sp.phone.common.Constants;
import sp.phone.common.PhoneConfiguration;

public class SettingsSizeFragment extends BaseFragment implements  SignSeekBar.OnProgressChangedListener {

    private PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    private WebView mWebView;

    private int mArticleLineHeight;
    private int mArticleHorizontalMargin;
    private int mArticleLetterSpacing;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_size, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.setting_title_size);
        super.onResume();
    }

    private void initView(View rootView) {
        mArticleLineHeight = mConfiguration.getArticleLineHeight();
        mArticleHorizontalMargin = mConfiguration.getArticleHorizontalMargin();
        mArticleLetterSpacing = mConfiguration.getArticleLetterSpacing();
        initFontSizeView(rootView);
        initAvatarSizeView(rootView);
        initWebFontSizeView(rootView);
        initArticleLineHeightView(rootView);
        initArticleHorizontalMarginView(rootView);
        initArticleLetterSpacingView(rootView);
        initEmotionSizeView(rootView);

    }

    private void initFontSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_topic_title);
        seekBar.getConfigBuilder()
                .max(Constants.TOPIC_TITLE_SIZE_MAX)
                .min(Constants.TOPIC_TITLE_SIZE_MIN)
                .progress(mConfiguration.getTopicTitleSize())
                .sectionCount(Constants.TOPIC_TITLE_SIZE_MAX - Constants.TOPIC_TITLE_SIZE_MIN)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    private void initWebFontSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_web_size);
        int max = 100;
        int min = 1;
        int size = mConfiguration.getWebViewTextZoom();
        seekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(size)
                .sectionCount(max - min)
                .build();
        seekBar.setOnProgressChangedListener(this);

        mWebView = rootView.findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/html/adjust_size.html?lineHeight=" + mArticleLineHeight
                + "&horizontalMargin=" + mArticleHorizontalMargin
                + "&letterSpacing=" + mArticleLetterSpacing);
    }

    private void initAvatarSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_avatar);
        seekBar.getConfigBuilder()
                .max(Constants.AVATAR_SIZE_MAX)
                .min(Constants.AVATAR_SIZE_MIN)
                .progress(mConfiguration.getAvatarSize())
                .sectionCount(Constants.AVATAR_SIZE_MAX - Constants.AVATAR_SIZE_MIN)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    private void initArticleLineHeightView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_article_line_height);
        seekBar.getConfigBuilder()
                .max(Constants.ARTICLE_LINE_HEIGHT_MAX)
                .min(Constants.ARTICLE_LINE_HEIGHT_MIN)
                .progress(mConfiguration.getArticleLineHeight())
                .sectionCount(Constants.ARTICLE_LINE_HEIGHT_MAX - Constants.ARTICLE_LINE_HEIGHT_MIN)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    private void initArticleHorizontalMarginView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_article_horizontal_margin);
        seekBar.getConfigBuilder()
                .max(Constants.ARTICLE_HORIZONTAL_MARGIN_MAX)
                .min(Constants.ARTICLE_HORIZONTAL_MARGIN_MIN)
                .progress(mConfiguration.getArticleHorizontalMargin())
                .sectionCount(Constants.ARTICLE_HORIZONTAL_MARGIN_MAX - Constants.ARTICLE_HORIZONTAL_MARGIN_MIN)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    private void initArticleLetterSpacingView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_article_letter_spacing);
        seekBar.getConfigBuilder()
                .max(Constants.ARTICLE_LETTER_SPACING_MAX + Constants.ARTICLE_LETTER_SPACING_PROGRESS_OFFSET)
                .min(Constants.ARTICLE_LETTER_SPACING_MIN + Constants.ARTICLE_LETTER_SPACING_PROGRESS_OFFSET)
                .progress(mArticleLetterSpacing + Constants.ARTICLE_LETTER_SPACING_PROGRESS_OFFSET)
                .sectionCount(Constants.ARTICLE_LETTER_SPACING_MAX - Constants.ARTICLE_LETTER_SPACING_MIN)
                .build();
        seekBar.setValueFormatListener(value -> String.valueOf(Math.round(value)
                - Constants.ARTICLE_LETTER_SPACING_PROGRESS_OFFSET));
        seekBar.setOnProgressChangedListener(this);
    }

    private void refreshArticlePreview() {
        mWebView.loadUrl("javascript:updateArticlePreview(" + mArticleLineHeight + ","
                + mArticleHorizontalMargin + "," + mArticleLetterSpacing + ")");
    }

    private void initEmotionSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_emoticon);
        int max = Constants.EMOTICON_SIZE_MAX;
        int min = Constants.EMOTICON_SIZE_MIN;
        seekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(mConfiguration.getEmoticonSize())
                .sectionCount(max - min)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    @Override
    public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
        switch (signSeekBar.getId()) {
            case R.id.seek_web_size:
                mWebView.getSettings().setTextZoom(progress);
                break;
            case R.id.seek_article_line_height:
                mArticleLineHeight = progress;
                refreshArticlePreview();
                break;
            case R.id.seek_article_horizontal_margin:
                mArticleHorizontalMargin = progress;
                refreshArticlePreview();
                break;
            case R.id.seek_article_letter_spacing:
                mArticleLetterSpacing = progress - Constants.ARTICLE_LETTER_SPACING_PROGRESS_OFFSET;
                refreshArticlePreview();
                break;
            default:
                break;
        }
    }

    @Override
    public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
        switch (signSeekBar.getId()) {
            case R.id.seek_topic_title:
                mConfiguration.setTopicTitleSize(progress);
                break;
            case R.id.seek_avatar:
                mConfiguration.setAvatarSize(progress);
                break;
            case R.id.seek_emoticon:
                mConfiguration.setEmoticonSize(progress);
                break;
            case R.id.seek_web_size:
                mConfiguration.setWebViewTextZoom(progress);
                break;
            case R.id.seek_article_line_height:
                mConfiguration.setArticleLineHeight(progress);
                break;
            case R.id.seek_article_horizontal_margin:
                mConfiguration.setArticleHorizontalMargin(progress);
                break;
            case R.id.seek_article_letter_spacing:
                mConfiguration.setArticleLetterSpacing(progress - Constants.ARTICLE_LETTER_SPACING_PROGRESS_OFFSET);
                break;
            default:
                break;
        }
    }

    @Override
    public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {


    }
}
