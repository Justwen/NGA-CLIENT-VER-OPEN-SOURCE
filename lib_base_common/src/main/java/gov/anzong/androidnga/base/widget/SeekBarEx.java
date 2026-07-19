package gov.anzong.androidnga.base.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.zhouyou.view.seekbar.SignSeekBar;

import java.lang.reflect.Field;

import gov.anzong.androidnga.common.R;

public class SeekBarEx extends SignSeekBar {

    public SeekBarEx(Context context) {
        this(context, null);
    }

    public SeekBarEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, com.zhouyou.view.seekbar.R.styleable.SignSeekBar, defStyleAttr, 0);
        int thumbRadiusOnDragging = a.getDimensionPixelSize(com.zhouyou.view.seekbar.R.styleable.SignSeekBar_ssb_thumb_radius_on_dragging, 0);
        if (thumbRadiusOnDragging != 0) {
            try {
                Field field = SignSeekBar.class.getDeclaredField("mThumbRadiusOnDragging");
                field.setAccessible(true);
                field.set(this, thumbRadiusOnDragging);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        a.recycle();

    }


    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {

        setOnProgressChangedListener(new OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                listener.onProgressChanged(null, progress, false);
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                listener.onStopTrackingTouch(null);
            }
        });
    }
}
