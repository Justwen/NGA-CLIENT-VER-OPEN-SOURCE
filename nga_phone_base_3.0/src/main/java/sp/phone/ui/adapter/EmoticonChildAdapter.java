package sp.phone.ui.adapter;

import static gov.anzong.androidnga.common.util.EmoticonUtils.EMOTICON_LABEL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;

import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ImageUtils;

/**
 * Created by Justwen on 2018/6/8.
 */
public class EmoticonChildAdapter extends RecyclerView.Adapter<EmoticonChildAdapter.EmoticonViewHolder> {

    private Context mContext;

    private String[] mImageUrls;

    private String[] mEmotionCodes;
    private String mCategoryName;

    private int mHeight;

    private boolean isNightMode;

    private View.OnClickListener mEmoticonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_INSERT_EMOTICON, v.getTag()));
        }
    };

    public EmoticonChildAdapter(Context context, int height) {
        mContext = context;
        mHeight = height;
        isNightMode = ThemeManager.getInstance().isNightMode();
    }

    public void setData(String categoryName, String[] urls, String[] codes) {
        mImageUrls = urls;
        mEmotionCodes = codes;
        mCategoryName = categoryName;
    }

    @Override
    public EmoticonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView emoticonView = new ImageView(mContext);
        int padding = 32;
        emoticonView.setPadding(padding, padding, padding, padding);
        emoticonView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight / 3));
        emoticonView.setBackground(mContext.getDrawable(android.R.drawable.list_selector_background));
        emoticonView.setOnClickListener(mEmoticonClickListener);
        return new EmoticonViewHolder(emoticonView);
    }

    @Override
    public void onBindViewHolder(EmoticonViewHolder holder, int position) {
        ImageUtils.recycleImageView(holder.mEmoticonItem);
        try (InputStream is = mContext.getAssets().open(getFileName(position))) {
            Bitmap bm = BitmapFactory.decodeStream(is);
            bm = ImageUtils.zoomImageByHeight(bm, 130);
            // 只有三个组的表情在夜间模式需要背景
            if (isNightMode) {
                switch (mCategoryName) {
                    case "ac":
                    case "a2":
                    case "dt":
                        bm = addWhiteBackground(bm);
                }
            }
            holder.mEmoticonItem.setImageBitmap(bm);
            holder.mEmoticonItem.setTag(mEmotionCodes[position] +
                    "-" + mCategoryName + "/" + mImageUrls[position]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(int position) {
        return mCategoryName + "/" + FilenameUtils.getName(mImageUrls[position]);
    }

    @Override
    public int getItemCount() {
        return mImageUrls == null ? 0 : mImageUrls.length;
    }

    private Bitmap addWhiteBackground(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        Bitmap result = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawRect(0, 0, bm.getWidth(), bm.getHeight(), paint);
        canvas.drawBitmap(bm, 0, 0, paint);
        return result;
    }

    static class EmoticonViewHolder extends RecyclerView.ViewHolder {

        ImageView mEmoticonItem;

        public EmoticonViewHolder(View itemView) {
            super(itemView);
            mEmoticonItem = (ImageView) itemView;
        }
    }
}
