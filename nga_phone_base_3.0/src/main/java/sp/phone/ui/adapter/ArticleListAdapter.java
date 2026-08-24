package sp.phone.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import com.alibaba.android.arouter.launcher.ARouter;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.DeviceUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.http.bean.Attachment;
import sp.phone.http.bean.ThreadData;
import sp.phone.http.bean.ThreadRowInfo;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.rxjava.RxUtils;
import sp.phone.theme.ThemeManager;
import sp.phone.ui.fragment.dialog.AvatarDialogFragment;
import sp.phone.ui.fragment.dialog.BaseDialogFragment;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.HtmlUtils;
import sp.phone.util.ImageUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.webview.LocalWebView;

/**
 * 帖子详情列表Adapter
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

    private static final String DEVICE_TYPE_IOS = "ios";

    private static final String DEVICE_TYPE_ANDROID = "android";

    private static final String DEVICE_TYPE_WP = "wp";

    private static final int VIEW_TYPE_WEB_VIEW = 0;

    private static final int VIEW_TYPE_NATIVE_VIEW = 1;

    private static final Pattern FLASH_VIDEO_PATTERN = Pattern.compile(
            "\\[flash(?:=video)?\\]([^\\[]+)\\[/flash\\]", Pattern.CASE_INSENSITIVE);

    private static final Pattern BILIBILI_VIDEO_PATTERN = Pattern.compile(
            "/video/(BV[0-9a-z]+|av[0-9]+)", Pattern.CASE_INSENSITIVE);

    private Context mContext;

    private FragmentManager mFragmentManager;

    private ThreadData mData;

    private LayoutInflater mLayoutInflater;

    private ThemeManager mThemeManager = ThemeManager.getInstance();

    private LocalWebView[] mLocalWebViews = new LocalWebView[20];

    private String mTopicOwner;

    private ExoPlayer mPlayingPlayer;
    private Dialog mVideoDialog;
    private WebView mVideoWebView;

    private View.OnClickListener mOnClientClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ThreadRowInfo row = (ThreadRowInfo) v.getTag();
            String fromClient = row.getFromClient();
            String clientModel = row.getFromClientModel();
            String deviceInfo;
            if (!StringUtils.isEmpty(clientModel)) {
                String clientAppCode;
                if (!fromClient.contains(" ")) {
                    clientAppCode = fromClient;
                } else {
                    clientAppCode = fromClient.substring(0,
                            fromClient.indexOf(' '));
                }
                switch (clientAppCode) {
                    case "1":
                        if (fromClient.length() <= 2) {
                            deviceInfo = "发送自Life Style苹果客户端 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自Life Style苹果客户端 机型及系统:"
                                    + fromClient.substring(2);
                        }
                        break;
                    case "7":
                        if (fromClient.length() <= 2) {
                            deviceInfo = "发送自NGA苹果官方客户端 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自NGA苹果官方客户端 机型及系统:"
                                    + fromClient.substring(2);
                        }
                        break;
                    case "8":
                        if (fromClient.length() <= 2) {
                            deviceInfo = "发送自NGA安卓客户端 机型及系统:未知";
                        } else {
                            String fromData = fromClient.substring(2);
                            if (fromData.startsWith("[")
                                    && fromData.contains("](Android")) {
                                deviceInfo = "发送自NGA安卓开源版客户端 机型及系统:"
                                        + fromData.substring(1).replace(
                                        "](Android", "(Android");
                            } else {
                                deviceInfo = "发送自NGA安卓官方客户端 机型及系统:" + fromData;
                            }
                        }
                        break;
                    case "9":
                        if (fromClient.length() <= 2) {
                            deviceInfo = "发送自NGA Windows Phone官方客户端 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自NGA Windows Phone官方客户端 机型及系统:"
                                    + fromClient.substring(2);
                        }
                        break;
                    case "100":
                        if (fromClient.length() <= 4) {
                            deviceInfo = "发送自安卓浏览器 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自安卓浏览器 机型及系统:"
                                    + fromClient.substring(4);
                        }
                        break;
                    case "101":
                        if (fromClient.length() <= 4) {
                            deviceInfo = "发送自苹果浏览器 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自苹果浏览器 机型及系统:"
                                    + fromClient.substring(4);
                        }
                        break;
                    case "102":
                        if (fromClient.length() <= 4) {
                            deviceInfo = "发送自Blackberry浏览器 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自Blackberry浏览器 机型及系统:"
                                    + fromClient.substring(4);
                        }
                        break;
                    case "103":
                        if (fromClient.length() <= 4) {
                            deviceInfo = "发送自Windows Phone客户端 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自Windows Phone客户端 机型及系统:"
                                    + fromClient.substring(4);
                        }
                        break;
                    default:
                        if (!fromClient.contains(" ")) {
                            deviceInfo = "发送自未知浏览器 机型及系统:未知";
                        } else {
                            if (fromClient.length() == (fromClient.indexOf(' ') + 1)) {
                                deviceInfo = "发送自未知浏览器 机型及系统:未知";
                            } else {
                                deviceInfo = "发送自未知浏览器 机型及系统:"
                                        + fromClient.substring(fromClient
                                        .indexOf(' ') + 1);
                            }
                        }
                        break;
                }
                ActivityUtils.showToast(deviceInfo);
            }
        }
    };

    private View.OnClickListener mOnReplyClickListener = new View.OnClickListener() {

        private Intent getReplyIntent(ThreadRowInfo row) {
            Intent intent = new Intent();
            StringBuilder postPrefix = new StringBuilder();
            String mention = null;

            final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
            final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
            String content = row.getContent();
            final String name = row.getAuthor();
            final String uid = String.valueOf(row.getAuthorid());
            int page = (row.getLou() + 20) / 20;// 以楼数计算page
            content = content.replaceAll(quote_regex, "");
            content = content.replaceAll(replay_regex, "");
            final String postTime = row.getPostdate();
            final String tidStr = String.valueOf(row.getTid());
            content = FunctionUtils.checkContent(content);
            content = StringUtils.unEscapeHtml(content);
            if (row.getPid() != 0 || row.getLou() == 0) {
                mention = name;
                postPrefix.append("[quote][pid=");
                postPrefix.append(row.getPid());
                postPrefix.append(',');
                postPrefix.append(tidStr);
                postPrefix.append(",");
                if (page > 0)
                    postPrefix.append(page);
                postPrefix.append("]");// Topic
                postPrefix.append("Reply");
                if (row.getISANONYMOUS()) {// 是匿名的人
                    postPrefix.append("[/pid] [b]Post by [uid=");
                    postPrefix.append("-1");
                    postPrefix.append("]");
                    postPrefix.append(name);
                    postPrefix.append("[/uid][color=gray](");
                    postPrefix.append(row.getLou());
                    postPrefix.append("楼)[/color] (");
                } else {
                    postPrefix.append("[/pid] [b]Post by [uid=");
                    postPrefix.append(uid);
                    postPrefix.append("]");
                    postPrefix.append(name);
                    postPrefix.append("[/uid] (");
                }
                postPrefix.append(postTime);
                postPrefix.append("):[/b]\n");
                postPrefix.append(content);
                postPrefix.append("[/quote]\n");
            }
            if (!StringUtils.isEmpty(mention))
                intent.putExtra("mention", mention);
            intent.putExtra("prefix",
                    StringUtils.removeBrTag(postPrefix.toString()));
            intent.putExtra("tid", tidStr);
            intent.putExtra("action", "reply");

            if (UserManagerImpl.getInstance().hasValidUser()) {// 登入了才能发
                intent.setClass(
                        ContextUtils.getContext(),
                        PhoneConfiguration.getInstance().postActivityClass);
            } else {
                ActivityUtils.startLoginActivity(mContext);
            }
            return intent;
        }

        @Override
        public void onClick(View view) {

            ThreadRowInfo row = (ThreadRowInfo) view.getTag();

            Observable.create((ObservableOnSubscribe<Intent>) emitter -> {
                emitter.onNext(getReplyIntent(row));
                emitter.onComplete();

            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<Intent>() {
                @Override
                public void onNext(@io.reactivex.annotations.NonNull Intent intent) {
                    try {
                        view.setEnabled(true);
                        ((Activity) view.getContext()).startActivityForResult(intent, ActivityUtils.REQUEST_CODE_TOPIC_POST);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.onNext(intent);
                }
            });
        }
    };

    private View.OnClickListener mOnProfileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ThreadRowInfo row = (ThreadRowInfo) view.getTag();

            if (row.getISANONYMOUS()) {
                ActivityUtils.showToast("这白痴匿名了,神马都看不到");
            } else if (row.getAuthor() != null){
                ARouter.getInstance()
                        .build(ARouterConstants.ACTIVITY_PROFILE)
                        .withString("mode", "uid")
                        .withString("uid", String.valueOf(row.getAuthorid()))
                        .navigation();
            }
        }
    };

    private View.OnClickListener mOnAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ThreadRowInfo row = (ThreadRowInfo) view.getTag();
            if (row.getISANONYMOUS()) {
                ActivityUtils.showToast("这白痴匿名了,神马都看不到");
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("name", row.getAuthor());
                bundle.putString("url", FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar()));
                BaseDialogFragment.show(mFragmentManager, bundle, AvatarDialogFragment.class);
                //FunctionUtils.Create_Avatar_Dialog(row, view.getContext(), null);
            }
        }
    };

    private View.OnClickListener mSupportListener;
    private View.OnClickListener mOpposeListener;
    private View.OnClickListener mMenuTogglerListener;

    private boolean mWifiConnected;

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_nickName)
        TextView nickNameTV;

        LocalWebView contentTV;

        @BindView(R.id.wv_container)
        FrameLayout contentContainer;

        @BindView(R.id.video_container)
        LinearLayout videoContainer;

        @BindView(R.id.tv_floor)
        TextView floorTv;

        @BindView(R.id.tv_post_time)
        TextView postTimeTv;

        @BindView(R.id.iv_support)
        ImageView supportBtn;

        @BindView(R.id.iv_oppose)
        ImageView opposeBtn;

        @BindView(R.id.iv_reply)
        ImageView replyBtn;

        @BindView(R.id.iv_avatar)
        ImageView avatarIv;

        @BindView(R.id.iv_client)
        ImageView clientIv;

        @BindView(R.id.tv_score)
        TextView scoreTv;

        @BindView(R.id.iv_more)
        ImageView menuIv;

        @BindView(R.id.fl_avatar)
        FrameLayout avatarPanel;

        @BindView(R.id.tv_detail)
        TextView detailTv;

        @BindView(R.id.tv_content)
        TextView contentTextView;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArticleListAdapter(Context context, FragmentManager fm) {
        mContext = context;
        mFragmentManager = fm;
        if (HtmlUtils.hide == null) {
            HtmlUtils.initStaticStrings(mContext);
        }
        mLayoutInflater = LayoutInflater.from(mContext);
        mWifiConnected = DeviceUtils.isWifiConnected(context);
    }

    public void setTopicOwner(String topicOwner) {
        mTopicOwner = topicOwner;
    }

    public void setData(ThreadData data) {
        mData = data;
    }

    public void setSupportListener(View.OnClickListener listener) {
        mSupportListener = listener;
    }

    public void setOpposeListener(View.OnClickListener listener) {
        mOpposeListener = listener;
    }

    public void setMenuTogglerListener(View.OnClickListener menuTogglerListener) {
        mMenuTogglerListener = menuTogglerListener;
    }

    @Override
    public int getItemViewType(int position) {
        ThreadRowInfo row = mData.getRowList().get(position);
        return TextUtils.isEmpty(row.getFormattedHtmlData()) ? VIEW_TYPE_NATIVE_VIEW : VIEW_TYPE_WEB_VIEW;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.fragment_article_list_item, parent, false);
        ArticleViewHolder viewHolder = new ArticleViewHolder(view);
        ViewGroup.LayoutParams lp = viewHolder.avatarIv.getLayoutParams();
        lp.width = lp.height = PhoneConfiguration.getInstance().getAvatarSize();
        if (viewType == VIEW_TYPE_WEB_VIEW) {
            viewHolder.contentTextView.setVisibility(View.GONE);
            // viewHolder.contentTV.setVisibility(View.VISIBLE);
        } else {
            viewHolder.contentTextView.setVisibility(View.VISIBLE);
            //  viewHolder.contentTV.setVisibility(View.GONE);
        }
        RxUtils.clicks(viewHolder.nickNameTV, mOnProfileClickListener);
        RxUtils.clicks(viewHolder.supportBtn, mSupportListener);
        RxUtils.clicks(viewHolder.opposeBtn, mOpposeListener);
        RxUtils.clicks(viewHolder.replyBtn, mOnReplyClickListener);
        RxUtils.clicks(viewHolder.clientIv, mOnClientClickListener);
        RxUtils.clicks(viewHolder.menuIv, mMenuTogglerListener);
        RxUtils.clicks(viewHolder.avatarPanel, mOnAvatarClickListener);
        viewHolder.contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, PhoneConfiguration.getInstance().getTopicContentSize());
        // viewHolder.contentTV.setTextSize(PhoneConfiguration.getInstance().getTopicContentSize());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticleViewHolder holder, final int position) {

        final ThreadRowInfo row = mData.getRowList().get(position);

        if (row == null) {
            return;
        }

        if (!PhoneConfiguration.getInstance().useSolidColorBackground()) {
            holder.itemView.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor(position));
        }

        holder.supportBtn.setTag(row);
        holder.opposeBtn.setTag(row);
        holder.replyBtn.setTag(row);
        holder.nickNameTV.setTag(row);
        holder.menuIv.setTag(row);
        holder.avatarPanel.setTag(row);

        onBindAvatarView(holder.avatarIv, row);
        onBindDeviceType(holder.clientIv, row);
        onBindContentView(holder, row, position);
        onBindVideoAttachments(holder.videoContainer, row);

        int fgColor = mThemeManager.getAccentColor(mContext);
        FunctionUtils.handleNickName(row, fgColor, holder.nickNameTV, mTopicOwner, mContext);

        holder.floorTv.setText(MessageFormat.format("[{0} 楼]", String.valueOf(row.getLou())));
        holder.postTimeTv.setText(row.getPostdate());
        holder.scoreTv.setText(MessageFormat.format("{0}", row.getScore()));

        holder.detailTv.setText(String.format("级别：%s   威望：%s   发帖：%s", row.getMemberGroup(), row.getReputation(), row.getPostCount()));

    }

    private LocalWebView createLocalWebView() {
        LocalWebView localWebView = new LocalWebView(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                PhoneConfiguration.getInstance().getArticleHorizontalMargin(),
                mContext.getResources().getDisplayMetrics());
        lp.setMarginStart(horizontalMargin);
        lp.setMarginEnd(horizontalMargin);
        localWebView.setLayoutParams(lp);
        return localWebView;
    }

    private void onBindContentView(ArticleViewHolder holder, ThreadRowInfo row, int position) {
        String html = row.getFormattedHtmlData();
        if (html != null) {
            if (mLocalWebViews != null) {
                LocalWebView localWebView = mLocalWebViews[position];
                if (localWebView == null) {
                    localWebView = createLocalWebView();
                    mLocalWebViews[position] = localWebView;
                }
                if (localWebView != holder.contentTV) {
                    holder.contentContainer.removeView(holder.contentTV);
                    if (localWebView.getParent() != null) {
                        ((ViewGroup) localWebView.getParent()).removeView(localWebView);
                    }
                    holder.contentTV = localWebView;
                    holder.contentContainer.addView(localWebView);
                }
            } else if (holder.contentTV == null) {
                holder.contentTV = createLocalWebView();
                holder.contentContainer.addView(holder.contentTV);
            }
            holder.contentTV.getWebViewClientEx().setImgUrls(row.getImageUrls());
            holder.contentTV.loadDataWithBaseURL(null, applyArticleTextStyle(html), "text/html", "utf-8", null);
        } else {
            holder.contentTextView.setLetterSpacing(PhoneConfiguration.getInstance().getArticleLetterSpacing() / 100f);
            holder.contentTextView.setText(row.getContent());
        }
    }

    private void onBindVideoAttachments(LinearLayout container, ThreadRowInfo row) {
        container.removeAllViews();

        Set<String> videoUrls = new LinkedHashSet<>();
        if (row.getAttachs() != null) {
            for (Attachment attachment : row.getAttachs().values()) {
                if (attachment != null && isVideoAttachment(attachment)) {
                    videoUrls.add(getAttachmentUrl(row, attachment));
                }
            }
        }

        if (!TextUtils.isEmpty(row.getContent())) {
            Matcher matcher = FLASH_VIDEO_PATTERN.matcher(row.getContent());
            while (matcher.find()) {
                String url = normalizeFlashVideoUrl(row, matcher.group(1));
                if (!TextUtils.isEmpty(url)) {
                    videoUrls.add(url);
                }
            }
        }

        for (String url : videoUrls) {
            addVideoCard(container, url);
        }
        container.setVisibility(videoUrls.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private boolean isVideoAttachment(Attachment attachment) {
        String extension = attachment.getExt();
        if (!TextUtils.isEmpty(extension) && "mp4".equalsIgnoreCase(extension)) {
            return true;
        }
        String url = attachment.getAttachurl();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return isDirectVideoUrl(url);
    }

    private String normalizeFlashVideoUrl(ThreadRowInfo row, String value) {
        String url = value == null ? null : value.trim().replace("&amp;", "&");
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return isDirectVideoUrl(url) || isSupportedWebVideoUrl(url) ? url : null;
        }
        if (!isDirectVideoUrl(url) || TextUtils.isEmpty(row.attachmentHost)) {
            return null;
        }
        if (url.startsWith("./")) {
            url = url.substring(2);
        } else if (url.startsWith("/")) {
            url = url.substring(1);
        }
        return "http://" + row.attachmentHost + "/attachments/" + url;
    }

    private boolean isDirectVideoUrl(String url) {
        String path = Uri.parse(url).getPath();
        return path != null && path.toLowerCase(Locale.US).endsWith(".mp4");
    }

    private boolean isSupportedWebVideoUrl(String url) {
        String host = Uri.parse(url).getHost();
        if (host == null) {
            return false;
        }
        host = host.toLowerCase(Locale.US);
        return host.equals("weibo.com") || host.endsWith(".weibo.com")
                || host.equals("weibo.cn") || host.endsWith(".weibo.cn")
                || host.equals("bilibili.com") || host.endsWith(".bilibili.com")
                || host.equals("b23.tv") || host.endsWith(".b23.tv");
    }

    private String getWebVideoPlayerUrl(String url) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (host == null || !(host.equalsIgnoreCase("bilibili.com")
                || host.toLowerCase(Locale.US).endsWith(".bilibili.com"))) {
            return url;
        }
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return url;
        }
        Matcher matcher = BILIBILI_VIDEO_PATTERN.matcher(path);
        if (!matcher.find()) {
            return url;
        }
        String videoId = matcher.group(1);
        if (videoId.regionMatches(true, 0, "BV", 0, 2)) {
            return "https://player.bilibili.com/player.html?bvid=" + videoId + "&autoplay=1";
        }
        return "https://player.bilibili.com/player.html?aid=" + videoId.substring(2) + "&autoplay=1";
    }

    private String getAttachmentUrl(ThreadRowInfo row, Attachment attachment) {
        return "http://" + row.attachmentHost + "/attachments/" + attachment.getAttachurl();
    }

    private void addVideoCard(LinearLayout container, final String url) {
        TextView playButton = new TextView(mContext);
        playButton.setText("▶ 播放视频");
        playButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        int padding = getVideoCardPadding();
        playButton.setPadding(padding, padding, padding, padding);
        playButton.setBackgroundResource(android.R.drawable.btn_default);
        playButton.setOnClickListener(v -> playFullscreenVideo(url));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = padding / 2;
        container.addView(playButton, params);

        TextView openInBrowser = new TextView(mContext);
        openInBrowser.setText("浏览器打开");
        openInBrowser.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        openInBrowser.setPadding(0, padding / 2, 0, padding / 2);
        openInBrowser.setOnClickListener(v -> openVideoInBrowser(url));
        container.addView(openInBrowser);
    }

    private int getVideoCardPadding() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                mContext.getResources().getDisplayMetrics());
    }

    private void playFullscreenVideo(final String url) {
        if (isSupportedWebVideoUrl(url)) {
            playFullscreenWebVideo(url);
            return;
        }
        releaseVideo();
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        PlayerView playerView = new PlayerView(mContext);
        playerView.setUseController(true);
        playerView.setControllerAutoShow(true);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        FrameLayout dialogContent = new FrameLayout(mContext);
        dialogContent.addView(playerView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageButton closeButton = new ImageButton(mContext);
        closeButton.setContentDescription("关闭视频");
        closeButton.setBackgroundColor(Color.TRANSPARENT);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        int closeButtonSize = getVideoCardPadding() * 4;
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(closeButtonSize, closeButtonSize,
                Gravity.TOP | Gravity.START);
        dialogContent.addView(closeButton, closeParams);
        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(dialogContent);

        final ExoPlayer player = new ExoPlayer.Builder(mContext).build();
        playerView.setPlayer(player);
        mPlayingPlayer = player;
        mVideoDialog = dialog;
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                if (player == mPlayingPlayer) {
                    ActivityUtils.showToast("视频播放失败");
                    dialog.dismiss();
                }
            }
        });
        dialog.setOnDismissListener(d -> {
            if (mVideoDialog == dialog) {
                mVideoDialog = null;
                mPlayingPlayer = null;
                player.release();
            }
        });
        dialog.setOnKeyListener((d, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                dialog.dismiss();
                return true;
            }
            return false;
        });
        dialog.show();
        player.setMediaItem(MediaItem.fromUri(Uri.parse(url)));
        player.prepare();
        player.play();
    }

    private void playFullscreenWebVideo(final String url) {
        releaseVideo();
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        WebView webView = new WebView(mContext);
        webView.setBackgroundColor(Color.BLACK);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        FrameLayout dialogContent = new FrameLayout(mContext);
        dialogContent.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageButton closeButton = new ImageButton(mContext);
        closeButton.setContentDescription("关闭视频");
        closeButton.setBackgroundColor(Color.TRANSPARENT);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        int closeButtonSize = getVideoCardPadding() * 4;
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(closeButtonSize, closeButtonSize,
                Gravity.TOP | Gravity.START);
        dialogContent.addView(closeButton, closeParams);
        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(dialogContent);

        mVideoDialog = dialog;
        mVideoWebView = webView;
        dialog.setOnDismissListener(d -> {
            if (mVideoDialog == dialog) {
                mVideoDialog = null;
                mVideoWebView = null;
                webView.stopLoading();
                webView.loadUrl("about:blank");
                webView.destroy();
            }
        });
        dialog.setOnKeyListener((d, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                dialog.dismiss();
                return true;
            }
            return false;
        });
        dialog.show();
        webView.loadUrl(getWebVideoPlayerUrl(url));
    }

    private void openVideoInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(intent);
    }

    public void releaseVideo() {
        Dialog dialog = mVideoDialog;
        ExoPlayer player = mPlayingPlayer;
        WebView webView = mVideoWebView;
        mVideoDialog = null;
        mPlayingPlayer = null;
        mVideoWebView = null;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (player != null) {
            player.release();
        }
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.destroy();
        }
    }

    public void pauseVideo() {
        if (mPlayingPlayer != null) {
            mPlayingPlayer.pause();
        }
        if (mVideoWebView != null) {
            mVideoWebView.onPause();
        }
    }

    private String applyArticleTextStyle(String html) {
        PhoneConfiguration configuration = PhoneConfiguration.getInstance();
        String style = "<style>body{line-height:" + configuration.getArticleLineHeight()
                + "% !important;letter-spacing:" + (configuration.getArticleLetterSpacing() / 100f)
                + "em !important;}</style>";
        int headStart = html.toLowerCase(Locale.US).indexOf("<head");
        if (headStart < 0) {
            return style + html;
        }
        int headEnd = html.indexOf('>', headStart);
        if (headEnd < 0) {
            return style + html;
        }
        return html.substring(0, headEnd + 1) + style + html.substring(headEnd + 1);
    }

    private void onBindDeviceType(ImageView clientBtn, ThreadRowInfo row) {
        String deviceType = row.getFromClientModel();

        if (TextUtils.isEmpty(deviceType)) {
            clientBtn.setVisibility(View.GONE);
        } else {
            switch (deviceType) {
                case DEVICE_TYPE_IOS:
                    clientBtn.setImageResource(R.drawable.ic_apple_12dp);
                    break;
                case DEVICE_TYPE_WP:
                    clientBtn.setImageResource(R.drawable.ic_windows_12dp);
                    break;
                case DEVICE_TYPE_ANDROID:
                    clientBtn.setImageResource(R.drawable.ic_android_12dp);
                    break;
                default:
                    clientBtn.setImageResource(R.drawable.ic_smartphone_12dp);
                    break;
            }
            clientBtn.setTag(row);
            clientBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.getRowNum();
    }

    private void onBindAvatarView(ImageView avatarIv, ThreadRowInfo row) {
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar());
        final boolean downImg = PhoneConfiguration.getInstance().isAvatarLoadEnabled(mWifiConnected);

        ImageUtils.loadRoundCornerAvatar(avatarIv, avatarUrl, !downImg);
    }

}
