package com.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.request.target.ViewTarget;
import com.squareup.picasso.Picasso;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.VideoPlayerActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class VideoListAdapter extends RecyclerView.Adapter {
    ArrayList<String> Filename;
    ArrayList<String> Filepath;

    Context context;
    MyViewHolder vh;
    public VideoListAdapter(Context context, ArrayList<String> Filename, ArrayList<String> Filepath
                            ) {
        this.context = context;
        this.Filename = Filename;
        this.Filepath = Filepath;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(com.stavigilmonitoring.R.layout.rowlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
         vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        vh.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle dataBundle = new Bundle();
                dataBundle.putString("FileName", Filename.get(position));
                dataBundle.putString("VideoPath", Filepath.get(position));
                dataBundle.putString("ImagePath", Filepath.get(position).replace(".mp4", ".jpg"));

                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtras(dataBundle);
                context.startActivity(intent);
            }
        });

        vh.name.setText(Filename.get(position));
        try {
           String imageurl = Filepath.get(position);
           imageurl =  imageurl.replace(".mp4",".jpg");
        Picasso.with(context).load(imageurl).placeholder(com.stavigilmonitoring.R.drawable.sta_logo)
                .error(R.drawable.playclip_video)
                .into(vh.image,new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess() {                    }
                    @Override
                    public void onError() {                    }
                });
        }  catch (Exception e){
            e.printStackTrace();
        }
    }


   /* @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // set the data in items
        holder.name.setText(Filename.get(position));
       // holder.image.setImageResource(Filepath.get(position));
        // implement setOnClickListener event on item view.
        *//*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open another activity on item click
                Intent intent = new Intent(context, SecondActivity.class);
                intent.putExtra("image", personImages.get(position)); // put image data in Intent
                context.startActivity(intent); // start Intent
            }
        });*//*
    }*/
    @Override
    public int getItemCount() {
        return Filename.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name;
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(com.stavigilmonitoring.R.id.name);
            image = (ImageView) itemView.findViewById(com.stavigilmonitoring.R.id.image);
        }


    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}