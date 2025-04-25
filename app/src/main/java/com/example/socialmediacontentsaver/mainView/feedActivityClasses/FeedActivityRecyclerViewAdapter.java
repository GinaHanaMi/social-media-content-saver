package com.example.socialmediacontentsaver.mainView.feedActivityClasses;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.models.ContentModel;

import java.io.File;
import java.util.ArrayList;

public class FeedActivityRecyclerViewAdapter extends RecyclerView.Adapter<FeedActivityRecyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<ContentModel> contentModels;

    public FeedActivityRecyclerViewAdapter(Context context, ArrayList<ContentModel> contentModels) {
        this.context = context;
        this.contentModels = contentModels;
    }

    @NonNull
    @Override
    public FeedActivityRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Where the layout gets inflated and it gives look to rows

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.feed_recycler_view_row, parent, false);

        return new FeedActivityRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedActivityRecyclerViewAdapter.MyViewHolder holder, int position) {
        ContentModel contentModel = contentModels.get(position);

        String thumbnailPath = contentModel.getThumbnail();

        Uri thumbnailUri;
        if (thumbnailPath.startsWith("content://") || thumbnailPath.startsWith("file://")) {
            thumbnailUri = Uri.parse(thumbnailPath);
        } else {
            thumbnailUri = Uri.fromFile(new File(thumbnailPath)); // Handles paths like /data/user/...
        }

        Glide.with(context)
                .load(thumbnailUri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.feedThumbnailRecycleViewImageViewVar);

        holder.feedTitleRecyclerTextViewVar.setText(contentModel.getTitle());
        holder.feedDescriptionRecyclerTextViewVar.setText(contentModel.getDescription());
        holder.feedSaveDateRecyclerTextViewVar.setText(contentModel.getSave_date());
        holder.feedPlatformRecyclerTextViewVar.setText(contentModel.getPlatform());
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items that are beeing displayed
        return contentModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabing the views from recycler_view_row layout file
        // kinda like oncreate

        ImageView feedThumbnailRecycleViewImageViewVar;
        TextView feedTitleRecyclerTextViewVar, feedDescriptionRecyclerTextViewVar, feedSaveDateRecyclerTextViewVar, feedPlatformRecyclerTextViewVar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            feedThumbnailRecycleViewImageViewVar = itemView.findViewById(R.id.feedThumbnailRecycleViewImageView);
            feedTitleRecyclerTextViewVar = itemView.findViewById(R.id.feedTitleRecyclerTextView);
            feedDescriptionRecyclerTextViewVar = itemView.findViewById(R.id.feedDescriptionRecyclerTextView);
            feedSaveDateRecyclerTextViewVar = itemView.findViewById(R.id.feedSaveDateRecyclerTextView);
            feedPlatformRecyclerTextViewVar = itemView.findViewById(R.id.feedPlatformRecyclerTextView);

        }
    }
}


