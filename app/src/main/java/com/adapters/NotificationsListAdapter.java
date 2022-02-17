package com.adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beanclasses.Notifications;
import com.beanclasses.StateList;
import com.stavigilmonitoring.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationsListAdapter extends BaseAdapter {
    private ArrayList<Notifications> list;
    private Context parent;
    private LayoutInflater mInflater;
    private ArrayList<Notifications> notifyList;

    public NotificationsListAdapter(Context parent,
                                 ArrayList<Notifications> notificationslist) {
        this.parent = parent;
        this.list = notificationslist;
        notifyList = new ArrayList<Notifications>();
        notifyList.addAll(notificationslist);
        mInflater = LayoutInflater.from(parent);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.notificationslist, null);
            holder = new ViewHolder();

            holder.layout_sl = convertView.findViewById(R.id.layout_sl);
            holder.layout_sl.setVisibility(View.GONE);
            holder.layout_pconoff = convertView.findViewById(R.id.layout_pconoff);
            holder.layout_pconoff.setVisibility(View.GONE);
            holder.layout_advfplay = convertView.findViewById(R.id.layout_advfplay);
            holder.layout_advfplay.setVisibility(View.GONE);
            holder.layout_busann = convertView.findViewById(R.id.layout_busann);
            holder.layout_busann.setVisibility(View.GONE);
            holder.layout_advnotrun = convertView.findViewById(R.id.layout_advnotrun);
            holder.layout_advnotrun.setVisibility(View.GONE);
            holder.layout_tvstatus = convertView.findViewById(R.id.layout_tvstatus);
            holder.layout_tvstatus.setVisibility(View.GONE);

            holder.txtdt_sl = (TextView)convertView.findViewById(R.id.txtdt_sl);
            holder.txttime_sl = (TextView)convertView.findViewById(R.id.txttime_sl);
            holder.txtpctgval_sl = (TextView)convertView.findViewById(R.id.txtpctgval_sl);

            holder.txtdt_pc = (TextView)convertView.findViewById(R.id.txtdt_pc);
            holder.txttime_pc = (TextView)convertView.findViewById(R.id.txttime_pc);
            holder.txttimerestart_pc = (TextView)convertView.findViewById(R.id.txttimerestart_pc);
            holder.txtduration_pc = (TextView)convertView.findViewById(R.id.txtduration_pc);

            holder.txtdt_fplay = (TextView)convertView.findViewById(R.id.txtdt_fplay);
            holder.txttime_fplay = (TextView)convertView.findViewById(R.id.txttime_fplay);
            holder.txtschtime = (TextView)convertView.findViewById(R.id.txtschtime);
            holder.txtplaytime_fplay = (TextView)convertView.findViewById(R.id.txtplaytime_fplay);

            holder.txtdt_busann = (TextView)convertView.findViewById(R.id.txtdt_busann);
            holder.txt_shift1 = (TextView)convertView.findViewById(R.id.txt_shift1);
            holder.txt_shift2 = (TextView)convertView.findViewById(R.id.txt_shift2);

            holder.txtdt_notrun = (TextView)convertView.findViewById(R.id.txtdt_notrun);
            holder.txttime_notrun = (TextView)convertView.findViewById(R.id.txttime_notrun);
            holder.txtdur_notrun = (TextView)convertView.findViewById(R.id.txtdur_notrun);
            holder.txtofftime_notrun = (TextView)convertView.findViewById(R.id.txtofftime_notrun);

            holder.txtdt_tvstat = (TextView)convertView.findViewById(R.id.txtdt_tvstat);
            holder.txttime_tvstat = (TextView)convertView.findViewById(R.id.txttime_tvstat);

            holder.imgTV = (ImageView)convertView.findViewById(R.id.imgTV);
            holder.imgTV1 = (ImageView)convertView.findViewById(R.id.imgTV1);
            holder.imgTV2 = (ImageView)convertView.findViewById(R.id.imgTV2);
            holder.imgTV3 = (ImageView)convertView.findViewById(R.id.imgTV3);
            holder.imgTV4 = (ImageView)convertView.findViewById(R.id.imgTV4);
            holder.imgTV5 = (ImageView)convertView.findViewById(R.id.imgTV5);
            holder.imgTV6 = (ImageView)convertView.findViewById(R.id.imgTV6);
            holder.imgTV7 = (ImageView)convertView.findViewById(R.id.imgTV7);

            holder.txtnotifytype = (TextView)convertView.findViewById(R.id.txtnotifytype);
            holder.txtinstid = (TextView)convertView.findViewById(R.id.txtinstid);
            holder.txtnotifytext = (TextView)convertView.findViewById(R.id.txtnotifytext);
            holder.txtnotifydata = (TextView)convertView.findViewById(R.id.txtnotifydata);
            holder.txtdatetime = (TextView)convertView.findViewById(R.id.txtdatetime);
            holder.imgsymbol = (ImageView)convertView.findViewById(R.id.imgsymbol);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String notType = list.get(position).getNotificationtype();

        String DateTime = list.get(position).getDateTime();

        if(list.get(position).getNotificationtype().equalsIgnoreCase("ADVFPLAY")){
            holder.txtnotifytype.setText("Advertisement First Play Time");
            holder.imgsymbol.setImageResource(R.drawable.advschd);

            holder.layout_sl.setVisibility(View.GONE);
            holder.layout_pconoff.setVisibility(View.GONE);
            holder.layout_advfplay.setVisibility(View.VISIBLE);
            holder.layout_busann.setVisibility(View.GONE);
            holder.layout_advnotrun.setVisibility(View.GONE);
            holder.layout_tvstatus.setVisibility(View.GONE);

        }else if(list.get(position).getNotificationtype().equalsIgnoreCase("BUSANN")) {
            holder.txtnotifytype.setText("Daily Bus Announcement");
            holder.imgsymbol.setImageResource(R.drawable.busrep);

            holder.layout_sl.setVisibility(View.GONE);
            holder.layout_pconoff.setVisibility(View.GONE);
            holder.layout_advfplay.setVisibility(View.GONE);
            holder.layout_busann.setVisibility(View.VISIBLE);
            holder.layout_advnotrun.setVisibility(View.GONE);
            holder.layout_tvstatus.setVisibility(View.GONE);

            try{
                String[] date_time = list.get(position).getDateTime().split(" ");
                holder.txtdt_busann.setText(date_time[0]);

                String[] val = list.get(position).getVal_SL_BUSANN_TV().split(",");
                holder.txt_shift1.setText(val[0]);
                holder.txt_shift2.setText(val[1]);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(list.get(position).getNotificationtype().equalsIgnoreCase("ADVNOTRUN")){
            holder.txtnotifytype.setText("Advertisement Not Running");
            holder.imgsymbol.setImageResource(R.drawable.audnotrun);

            holder.layout_sl.setVisibility(View.GONE);
            holder.layout_pconoff.setVisibility(View.GONE);
            holder.layout_advfplay.setVisibility(View.GONE);
            holder.layout_busann.setVisibility(View.GONE);
            holder.layout_advnotrun.setVisibility(View.VISIBLE);
            holder.layout_tvstatus.setVisibility(View.GONE);

            /*get date time - duration = getofftime*/
            String[] date_time = list.get(position).getDateTime().split(" ");
            holder.txtdt_notrun.setText(date_time[0] +" "+ date_time[1] +" "+ date_time[2]);
         //   holder.txtdt_notrun.setText(date_time[0]);
            holder.txttime_notrun.setText(date_time[1] +" "+date_time[2]);

            long duration = Long.parseLong(list.get(position).getVal_SL_BUSANN_TV());

            holder.txtdur_notrun.setText(list.get(position).getVal_SL_BUSANN_TV() + " min.");

            long millisec_for1min = 60000;
            long millisec_tosubtract = millisec_for1min * duration;

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            formatter.setLenient(false);

           // String oldTime = "05.01.2011, 12:45";
            String oldTime = list.get(position).getDateTime();
            Date oldDate = null;
            long oldMillis = 0;
            try {
                oldDate = formatter.parse(oldTime);
                oldMillis = oldDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long prevdatemilli = oldMillis - millisec_tosubtract;
            Log.e("prevdate : ",String.valueOf(prevdatemilli));

            String prevDATE = formatter.format(new Date(prevdatemilli));
            Log.e("prevDATE : ",prevDATE);

            holder.txtofftime_notrun.setText(prevDATE);
            
        }else if(list.get(position).getNotificationtype().equalsIgnoreCase("TVSTAT")){
            holder.txtnotifytype.setText("TV Status");
            holder.imgsymbol.setImageResource(R.drawable.tvstatus);

            holder.layout_sl.setVisibility(View.GONE);
            holder.layout_pconoff.setVisibility(View.GONE);
            holder.layout_advfplay.setVisibility(View.GONE);
            holder.layout_busann.setVisibility(View.GONE);
            holder.layout_advnotrun.setVisibility(View.GONE);
            holder.layout_tvstatus.setVisibility(View.VISIBLE);

            String[] date_time = list.get(position).getDateTime().split(" ");
            holder.txtdt_tvstat.setText(date_time[0]);
            holder.txttime_tvstat.setText(date_time[1] +" "+date_time[2]);

            String TotalTV = list.get(position).getVal_SL_BUSANN_TV();
            int i=0;
            for (char d : TotalTV.toCharArray()) {
                switch (i) {
                    case 0:
                        if(d=='1')
                        {
                            holder.imgTV.setVisibility(View.VISIBLE);
                            //holder.imgTV.setBackground(R.drawable.tvon);
                            holder.imgTV.setImageResource(R.drawable.tvon);
                        }
                        else if(d=='0'){
                            holder.imgTV.setImageResource(R.drawable.tvoff);
                            holder.imgTV.setVisibility(View.VISIBLE);
                        }

                        break;

                    case 1:
                        if(d=='1')
                        {
                            holder.imgTV1.setVisibility(View.VISIBLE);
                            holder.imgTV1.setImageResource(R.drawable.tvon);
                        } else
                            holder.imgTV1.setImageResource(R.drawable.tvoff);
                        holder.imgTV1.setVisibility(View.VISIBLE);
                        break;

                    case 2:
                        if(d=='1')
                        {
                            holder.imgTV2.setVisibility(View.VISIBLE);
                            holder.imgTV2.setImageResource(R.drawable.tvon);
                        }
                        else
                            holder.imgTV2.setImageResource(R.drawable.tvoff);
                        holder.imgTV2.setVisibility(View.VISIBLE);
                        break;

                    case 3:
                        if(d=='1')
                        {
                            holder.imgTV3.setVisibility(View.VISIBLE);
                            holder.imgTV3.setImageResource(R.drawable.tvon);
                        }
                        else
                            holder.imgTV3.setImageResource(R.drawable.tvoff);
                        holder.imgTV3.setVisibility(View.VISIBLE);
                        break;

                    case 4:
                        if(d=='1')
                        {
                            holder.imgTV4.setVisibility(View.VISIBLE);
                            holder.imgTV4.setImageResource(R.drawable.tvon);
                        }
                        else
                            holder.imgTV4.setImageResource(R.drawable.tvoff);
                        holder.imgTV4.setVisibility(View.VISIBLE);
                        break;

                    case 5:
                        if(d=='1')
                        {
                            holder.imgTV5.setVisibility(View.VISIBLE);
                            holder.imgTV5.setImageResource(R.drawable.tvon);
                        }
                        else
                            holder.imgTV5.setImageResource(R.drawable.tvoff);
                        holder.imgTV5.setVisibility(View.VISIBLE);
                        break;

                    case 6:
                        if(d=='1')
                        {
                            holder.imgTV6.setVisibility(View.VISIBLE);
                            holder.imgTV6.setImageResource(R.drawable.tvon);
                        }else if(d=='0'){
                            holder.imgTV6.setImageResource(R.drawable.tvoff);
                            holder.imgTV6.setVisibility(View.VISIBLE);
                        }else {
                            holder.imgTV6.setVisibility(View.GONE);
                        }
                        break;

                    case 7:
                        if(d=='1')
                        {
                            holder.imgTV7.setVisibility(View.VISIBLE);
                            holder.imgTV7.setImageResource(R.drawable.tvon);
                        }else if(d=='0'){
                            holder.imgTV7.setImageResource(R.drawable.tvoff);
                            holder.imgTV7.setVisibility(View.VISIBLE);
                        }else {
                            holder.imgTV7.setVisibility(View.GONE);
                        }
                        break;
                }
                i++;
            }

        }else if(list.get(position).getNotificationtype().equalsIgnoreCase("PCONOFF")){
            holder.txtnotifytype.setText("PC On - Off");
            holder.imgsymbol.setImageResource(R.drawable.pconoff);
            holder.layout_sl.setVisibility(View.GONE);
            holder.layout_pconoff.setVisibility(View.VISIBLE);
            holder.layout_advfplay.setVisibility(View.GONE);
            holder.layout_busann.setVisibility(View.GONE);
            holder.layout_advnotrun.setVisibility(View.GONE);
            holder.layout_tvstatus.setVisibility(View.GONE);

            String[] date_time = list.get(position).getDateTime().split(" ");
            holder.txtdt_pc.setText(date_time[0]);
            holder.txttime_pc.setText(date_time[1] +" "+date_time[2]);
            holder.txttimerestart_pc.setText(date_time[1]+" "+date_time[2]);

        }else if(list.get(position).getNotificationtype().equalsIgnoreCase("SL")){
            holder.txtnotifytype.setText("Sound Level");
            holder.imgsymbol.setImageResource(R.drawable.soundlevel_new);

            holder.layout_sl.setVisibility(View.VISIBLE);
            holder.layout_pconoff.setVisibility(View.GONE);
            holder.layout_advfplay.setVisibility(View.GONE);
            holder.layout_busann.setVisibility(View.GONE);
            holder.layout_advnotrun.setVisibility(View.GONE);
            holder.layout_tvstatus.setVisibility(View.GONE);

            String[] date_time = list.get(position).getDateTime().split(" ");
            holder.txtdt_sl.setText(date_time[0]);
            holder.txttime_sl.setText(date_time[1] +" "+date_time[2]);

            holder.txtpctgval_sl.setText(list.get(position).getVal_SL_BUSANN_TV());
        }

        holder.txtnotifytext.setText(list.get(position).getNotificationText());
        holder.txtnotifydata.setText(list.get(position).getNotificationData());
        holder.txtinstid.setText(list.get(position).getInstallationID());
        holder.txtdatetime.setText(list.get(position).getDateTime());

        return convertView;
    }

    class ViewHolder{
        LinearLayout layout_sl, layout_pconoff, layout_advfplay, layout_busann, layout_advnotrun, layout_tvstatus;
        TextView txtnotifytype, txtinstid, txtnotifytext,txtnotifydata, txtdatetime;
        ImageView imgsymbol;

        TextView txtdt_sl, txttime_sl, txtpctgval_sl;
        TextView txtdt_pc,txttime_pc, txttimerestart_pc,  txtduration_pc;
        TextView txtdt_fplay, txttime_fplay,txtschtime, txtplaytime_fplay;
        TextView txtdt_busann, txt_shift1, txt_shift2;
        TextView txtdt_notrun, txttime_notrun, txtdur_notrun,txtofftime_notrun;
        TextView txtdt_tvstat,txttime_tvstat;
        ImageView imgTV,imgTV1, imgTV2, imgTV3, imgTV4, imgTV5, imgTV6, imgTV7;

    }
}