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

import com.beanclasses.AdvFirstPlayClipRprt;
import com.beanclasses.AdvVideoDataBean;
import com.beanclasses.MaterialDispatchedbutnotReceivedBean;
import com.stavigilmonitoring.R;

import java.util.ArrayList;
import java.util.Locale;

public class AdvDetailsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    ArrayList<AdvVideoDataBean> lststns = new ArrayList<AdvVideoDataBean>();
    private ArrayList<AdvVideoDataBean> arraylist;
    String[] parts;

    public AdvDetailsAdapter(Context parent, ArrayList<AdvVideoDataBean> results) {
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
            convertView = mInflater.inflate(R.layout.advdtl_adapter,null);
            holder = new ViewHolder();

            holder.txtnetwork = (TextView)convertView.findViewById(R.id.txtnetwork);
            holder.txtclipnum = (TextView)convertView.findViewById(R.id.txtclipnum);
            holder.txtadvdesc = (TextView)convertView.findViewById(R.id.txtadvdesc);
            holder.txtstationname = (TextView)convertView.findViewById(R.id.txtstationname);
            holder.imgplay = convertView.findViewById(R.id.imgplay);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imgplay.setTag(position);

        holder.txtnetwork.setText(lststns.get(position).getNetworkCode());
        holder.txtclipnum.setText(lststns.get(position).getAdvertisementCode());
        holder.txtadvdesc.setText(lststns.get(position).getAdvertisementDesc());
      //  holder.txtstationname.setText(lststns.get(position).getInstalationName());

        if(lststns.get(position).getClipPath_URL().contains(".wmv")){
            holder.imgplay.setImageDrawable(context.getResources().getDrawable(R.drawable.playclip_video));
        }else {
            holder.imgplay.setImageDrawable(context.getResources().getDrawable(R.drawable.playclip));
        }

        holder.imgplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    parts = lststns.get(position).getClipPath_URL().split("\\*\\*");

                    Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                    httpIntent.setData(Uri.parse(parts[0]));
                    context.startActivity(httpIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtnetwork,txtclipnum,txtadvdesc,txtstationname;
        ImageView imgplay;
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
