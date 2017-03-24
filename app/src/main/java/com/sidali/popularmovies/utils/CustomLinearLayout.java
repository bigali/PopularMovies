package com.sidali.popularmovies.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

/**
 * Created by shallak on 24/03/2017.
 */

public class CustomLinearLayout extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayout(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
