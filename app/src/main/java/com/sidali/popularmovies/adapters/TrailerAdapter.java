package com.sidali.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sidali.popularmovies.R;
import com.sidali.popularmovies.model.Movie;
import com.sidali.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.name;

/**
 * Created by shallak on 20/03/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder>{

    private List<Trailer> trailers=new ArrayList<>();
    private Context context;

    public TrailerAdapter(Context context, List<Trailer> trailers){
        this.trailers=trailers;
        this.context = context;

    }

    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.MyViewHolder holder, final int position) {

        Picasso.with(context).load("https://img.youtube.com/vi/"+trailers.get(position).getKey()+"/0.jpg").into(holder.ivThumbnail);
        holder.trailerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // your video id
                // for instance a video link: http://www.youtube.com/watch?v=Q9hF0j9G8_M
                String videoId = trailers.get(position).getKey();;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                intent.putExtra("VIDEO_ID", videoId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public RelativeLayout trailerLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.img_thumnail);
            trailerLayout =(RelativeLayout) itemView.findViewById(R.id.trailers_layout);
        }
    }
}
