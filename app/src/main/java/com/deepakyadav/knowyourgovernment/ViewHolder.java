package com.deepakyadav.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView designation;
    public TextView name;

    ViewHolder(View view) {
        super(view);
        designation = view.findViewById(R.id.officeDesignation);
        name = view.findViewById(R.id.officialName);
    }
}
