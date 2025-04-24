package com.example.socialmediacontentsaver.mainView.feedActivityClasses;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.models.ContentModel;
import com.example.socialmediacontentsaver.models.FolderModel;
import com.example.socialmediacontentsaver.receiveData.FolderRecyclerViewAdapter;

import java.util.ArrayList;

public class feedActivityRecyclerViewAdapter extends RecyclerView.Adapter<feedActivityRecyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<ContentModel> contentModels;

    public feedActivityRecyclerViewAdapter(Context context, ArrayList<ContentModel> contentModels) {
        this.context = context;
        this.contentModels = contentModels;
    }

    @NonNull
    @Override
    public feedActivityRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Where the layout gets inflated and it gives look to rows

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.content_recycler_view_row, parent, false);

        return new feedActivityRecyclerViewAdapter.MyViewHolder(view);
    }

    // DO ZMIANY
    @Override
    public void onBindViewHolder(@NonNull FolderRecyclerViewAdapter.MyViewHolder holder, int position) {
        // Assign values based on position of the RecyclerView
        ContentModel contentModel = contentModels.get(position);



        // Load image using Glide
        Glide.with(context)
                .load(Uri.parse(folderModel.getThumbnail()))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.thumbnailRecycleViewTextViewVar);

        holder.titleRecyclerTextViewVar.setText(folderModel.getTitle());
        holder.descriptionRecyclerTextViewVar.setText(folderModel.getDescription());
        holder.createAtRecyclerTextViewVar.setText(folderModel.getCreated_at());

    }
    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items that are beeing displayed
        return contentModels.size();
    }

    //DO ZMIANY
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabing the views from recycler_view_row layout file
        // kinda like oncreate

        ImageView thumbnailRecycleViewTextViewVar;
        TextView titleRecyclerTextViewVar, descriptionRecyclerTextViewVar, createAtRecyclerTextViewVar;
        CheckBox selectFolderRecycleViewVar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnailRecycleViewTextViewVar = itemView.findViewById(R.id.thumbnailRecycleViewTextView);
            titleRecyclerTextViewVar = itemView.findViewById(R.id.titleRecyclerTextView);
            descriptionRecyclerTextViewVar = itemView.findViewById(R.id.descriptionRecyclerTextView);
            createAtRecyclerTextViewVar = itemView.findViewById(R.id.createAtRecyclerTextView);
            selectFolderRecycleViewVar = itemView.findViewById(R.id.selectFolderRecycleView);

        }
    }
}


