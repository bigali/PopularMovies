package com.sidali.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sidali.popularmovies.R;
import com.sidali.popularmovies.model.Review;
import com.sidali.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shallak on 20/03/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>{

    private List<Review> reviews=new ArrayList<>();
    private Context context;

    public ReviewsAdapter(Context context, List<Review> reviews){
        this.reviews=reviews;
        this.context = context;

    }

    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.MyViewHolder holder, final int position) {
        holder.tvAuthor.setText(reviews.get(position).getAuthor());
        holder.reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = reviews.get(position).getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAuthor;
        public ConstraintLayout reviewLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            reviewLayout =(ConstraintLayout) itemView.findViewById(R.id.review_layout);
        }
    }
}
