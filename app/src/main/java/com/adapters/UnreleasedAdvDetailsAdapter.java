package com.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanclasses.AdvVideoDataBean;
import com.stavigilmonitoring.R;

import java.util.ArrayList;
import java.util.Locale;

public class UnreleasedAdvDetailsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Context context;
    ArrayList<AdvVideoDataBean> lststns = new ArrayList<AdvVideoDataBean>();
    private ArrayList<AdvVideoDataBean> arraylist;
    String[] parts;

    public UnreleasedAdvDetailsAdapter(Context parent, ArrayList<AdvVideoDataBean> results) {
        this.context = parent;
        mInflater = LayoutInflater.from(parent);
        lststns = results;
        arraylist = new ArrayList<AdvVideoDataBean>();
        arraylist.addAll(results);
    }

    @Override
    public int getCount() {
        return lststns.size();
    }

    @Override
    public Object getItem(int position) {
        return lststns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.unreleased_advdtl_adapter,null);
            holder = new ViewHolder();

            holder.txtnetwork = (TextView)convertView.findViewById(R.id.txtnetwork);
            holder.txtsono = (TextView)convertView.findViewById(R.id.txtsono);
            holder.txtadvcode = (TextView)convertView.findViewById(R.id.txtadvcode);
            holder.txtapprdate = (TextView)convertView.findViewById(R.id.txtapprdate);
            holder.txtsopreldate = (TextView)convertView.findViewById(R.id.txtsopreldate);
            holder.txtadvdesc = (TextView)convertView.findViewById(R.id.txtadvdesc);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        String a_date ="", a_time ="", sop_date ="", sop_time ="";

        try{
            String ApprDate[] = lststns.get(position).getApproveDate().split(" ");
            a_date = ApprDate[0]+" "+ApprDate[1]+" "+ApprDate[2];
            String atime[] = ApprDate[3].split(":");
            a_time = atime[0]+":"+atime[1];

        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            String SOPDate[] = lststns.get(position).getSOPReleaseDate().split(" ");
            sop_date = SOPDate[0]+" "+SOPDate[1]+" "+SOPDate[2];
            String soptime[] = SOPDate[3].split(":");
            sop_time = soptime[0]+":"+soptime[1];

        }catch (Exception e){
            e.printStackTrace();
        }

        holder.txtnetwork.setText(lststns.get(position).getNetworkCode());
        holder.txtadvcode.setText(lststns.get(position).getAdvertisementCode());
        holder.txtadvdesc.setText(lststns.get(position).getAdvertisementDesc());
        holder.txtsono.setText(lststns.get(position).getSoNumber());
        holder.txtapprdate.setText(a_date+" "+a_time);
        holder.txtsopreldate.setText(sop_date+" "+sop_time);

        /*if(lststns.get(position).getClipPath_URL().contains(".wmv")){
            holder.imgplay.setImageDrawable(context.getResources().getDrawable(R.drawable.playclip_video));
        }else {
            holder.imgplay.setImageDrawable(context.getResources().getDrawable(R.drawable.playclip));
        }*/

        return convertView;
    }

    static class ViewHolder {
        TextView txtnetwork,txtadvdesc,txtsono,txtadvcode,txtapprdate,txtsopreldate;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        lststns.clear();
        if (charText.length() == 0) {
            lststns.addAll(arraylist);
        } else {
            for (AdvVideoDataBean wp : arraylist) {
                if (wp.getNetworkCode().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getAdvertisementCode().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getAdvertisementDesc().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getNetworkCode().toLowerCase(Locale.getDefault()).contains(charText)) {
                    lststns.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
