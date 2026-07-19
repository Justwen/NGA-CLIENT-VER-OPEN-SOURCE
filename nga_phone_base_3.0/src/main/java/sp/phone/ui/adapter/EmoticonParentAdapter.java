package sp.phone.ui.adapter;

import static gov.anzong.androidnga.common.util.EmoticonUtils.EMOTICON_LABEL;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import gov.anzong.androidnga.common.util.EmoticonUtils;

/**
 * Created by Justwen on 2018/6/8.
 */
public class EmoticonParentAdapter extends PagerAdapter {

    private Context mContext;

    private int mHeight;

    private static final int COLUMN_COUNT = 4;

    public EmoticonParentAdapter(Context context, int height) {
        mContext = context;
        mHeight = height;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, COLUMN_COUNT));
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        EmoticonChildAdapter adapter = new EmoticonChildAdapter(mContext, mHeight);

        adapter.setData(EMOTICON_LABEL[position][0], getEmotionList(EmoticonUtils.EMOTICON_URL[position], position, 1),
                getEmotionList(EmoticonUtils.EMOTICON_URL[position], position, 0)
        );

        recyclerView.setAdapter(adapter);

        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EMOTICON_LABEL[position][1];
    }

    private String[] getEmotionList(String[][] list, int emotionPosion, int emotionType) {
        String[] result = new String[list.length];
        if (emotionType == 0) {
            for (int i = 0; i < list.length; i++) {
                result[i] = "[s:" + EmoticonUtils.EMOTICON_LABEL[emotionPosion][0] + ":" + list[i][emotionType] + "]";
            }
        } else {
            for (int i = 0; i < list.length; i++) {
                result[i] = list[i][emotionType];
            }
        }

        return result;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return EMOTICON_LABEL.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
