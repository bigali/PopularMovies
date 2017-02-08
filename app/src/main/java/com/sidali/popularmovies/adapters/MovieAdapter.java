package com.sidali.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sidali.popularmovies.MovieDetailActivity;
import com.sidali.popularmovies.R;
import com.sidali.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.onClick;

/**
 * Created by shallak on 05/02/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    private List<Movie> movies=new ArrayList<>();
    private Context context;

    public MovieAdapter(Context context,List<Movie> movies){
        this.movies = movies;
        this.context = context;
    }

    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MyViewHolder holder, final int position) {
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+movies.get(position).getPosterPath()).into(holder.img);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),MovieDetailActivity.class);
                intent.putExtra("id",movies.get(position).getId());
                v.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private LinearLayout linearLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }



    }
}
