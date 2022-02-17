package com.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beanclasses.ConnectionstatusHelper;
import com.stavigilmonitoring.CSNBannerActivity;
import com.stavigilmonitoring.R;

import java.util.ArrayList;

public class ConnectionStatusAdapterMainRecycleview extends RecyclerView.Adapter<ConnectionStatusAdapterMainRecycleview.CSNHolder> {

    private static ArrayList<ConnectionstatusHelper> searchArrayList;

    private LayoutInflater mInflater;
    Context context;
    private ArrayList<ConnectionstatusHelper> arraylist;
    String fromCSNBanner="";

    public ConnectionStatusAdapterMainRecycleview(Context ctx, ArrayList<ConnectionstatusHelper> searchResults, String fromCSNBanner) {
        this.context = ctx;
        this.arraylist = searchResults;
        this.fromCSNBanner = fromCSNBanner;
    }

    @NonNull
    @Override
    public ConnectionStatusAdapterMainRecycleview.CSNHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.connectionstatusupdatedmain, viewGroup, false);
        return new CSNHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CSNHolder holder, int position) {
        try {

            if (!(searchArrayList.get(position).gettymdiff().contains("Month") ||
                    searchArrayList.get(position).gettymdiff().contains("Min") ||
                    searchArrayList.get(position).gettymdiff().contains("hr"))) {

                //holder.card_view.setVisibility(View.VISIBLE);
                String s = searchArrayList.get(position).getinstallationId();

                if (s.equals("")) {

                    holder.installationid.setText("No Station Found..");
                    holder.servertime.setText("");
                    holder.noReason.setText("");

                } else {


                    holder.installationid.setText(searchArrayList.get(position)
                            .getinstallationId());


                    holder.servertime.setText(searchArrayList.get(position).gettymdiff());

                    String s1 = searchArrayList.get(position).getreason();
                    if (s1.equals("No Reason Found")) {
                        holder.noReason.setText("");

                    } else {
                        holder.noReason.setText(searchArrayList.get(position)
                                .getreason());
                    }
                    //	holder.starttime.setText(searchArrayList.get(position)
                    //	.getStartTime());

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class CSNHolder extends RecyclerView.ViewHolder {

        TextView installationid;
        TextView servertime;
        TextView noReason;
        CardView card_view;

        public CSNHolder(View itemView) {
            super(itemView);
            installationid = (TextView) itemView.findViewById(R.id.tvinstallationidmain);
            servertime = (TextView) itemView.findViewById(R.id.tvservertimemain);
            noReason = (TextView) itemView.findViewById(R.id.tvreasonupdated);
            card_view = (CardView) itemView.findViewById(R.id.card_view);

        }
    }
}
