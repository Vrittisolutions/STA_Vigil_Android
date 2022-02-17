package com.stavigilmonitoring;

import android.view.View;

public interface ItemClickListener {
    void onItemClick(View view, int position);
    void onItemClick(String fname, String fpath, int position);
}
