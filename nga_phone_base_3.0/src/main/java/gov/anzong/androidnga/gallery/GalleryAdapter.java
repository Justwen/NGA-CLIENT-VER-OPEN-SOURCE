package gov.anzong.androidnga.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * 浏览
 * Created by elrond on 2017/10/13.
 */

public class GalleryAdapter extends PagerAdapter {

    private Context mContext;

    private String[] mGalleryUrls;

    public GalleryAdapter(Context context, String[] galleryUrls) {
        mContext = context;
        mGalleryUrls = galleryUrls;
    }

    @Override
    public int getCount() {
        return mGalleryUrls.length;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setMaximumScale(10.0f);
        String url = mGalleryUrls[position];
        Glide.with(mContext).load(url).listener(mRequestListener).apply(RequestOptions.fitCenterTransform()).into(photoView);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private RequestListener<Drawable> mRequestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
            callbackActivity();
            return false;
        }

    };

    private void callbackActivity() {
        ((ImageZoomActivity) mContext).hideLoading();
    }
}
