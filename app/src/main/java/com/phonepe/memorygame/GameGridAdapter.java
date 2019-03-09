package com.phonepe.memorygame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GameGridAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mPictureGrid;
    private View.OnClickListener mGridClickListener;

    public GameGridAdapter(Context context, int[] pictureGrid, View.OnClickListener gridClickListener) {
        this.mContext = context;
        this.mPictureGrid = pictureGrid;
        this.mGridClickListener = gridClickListener;
    }

    @Override
    public int getCount() {
        return mPictureGrid.length;
    }

    @Override
    public Object getItem(int i) {
        return mPictureGrid[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (view == null) {
            view = inflater.inflate(R.layout.memory_card, viewGroup, false);
        }
        view.setTag(R.id.tag_picture_res, mPictureGrid[i]);
        view.setTag(R.id.tag_view_index, i);
        view.setOnClickListener(mGridClickListener);
        return view;
    }
}
