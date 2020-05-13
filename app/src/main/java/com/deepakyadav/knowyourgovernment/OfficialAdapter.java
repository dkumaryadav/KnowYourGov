package com.deepakyadav.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "OfficialAdapter";
    private List<Official> officialList;
    private MainActivity mainActivity;

    OfficialAdapter(List<Official> officialList, MainActivity ma){
        this.officialList = officialList;
        mainActivity = ma;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        itemView.setOnClickListener(mainActivity);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Official official = officialList.get(position);
        holder.designation.setText( official.getOffice() );

        if( !official.getOfficialParty().equalsIgnoreCase("No data provided") &&
                !official.getOfficialParty().equalsIgnoreCase("Unknown"))
            holder.name.setText( official.getOfficialName() + "(" + official.getOfficialParty() + ")" );
        else
            holder.name.setText(official.getOfficialName());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
