package gov.anzong.androidnga.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

public class ViewFlipperEx extends ViewFlipper {

    private ViewFlipperAdapterDataObserver mDataObserver = new ViewFlipperAdapterDataObserver();

    private RecyclerView.Adapter mAdapter;

    public ViewFlipperEx(Context context) {
        super(context);
    }

    public ViewFlipperEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
        }
        mAdapter = adapter;
        mAdapter.registerAdapterDataObserver(mDataObserver);
        updateView();
    }

    private void updateView() {
        removeAllViews();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = mAdapter.onCreateViewHolder(this, i);
            mAdapter.onBindViewHolder(viewHolder, i);
            addView(viewHolder.itemView);
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    private class ViewFlipperAdapterDataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            updateView();
        }
    }
}
