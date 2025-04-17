package com.example.socialmediacontentsaver.receiveData;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.models.FolderModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<FolderModel> folderModels;

    public FolderRecyclerViewAdapter(Context context, ArrayList<FolderModel> folderModels) {
        this.context = context;
        this.folderModels = folderModels;
    }

    @NonNull
    @Override
    public FolderRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Where the layout gets inflated and it gives look to rows
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderRecyclerViewAdapter.MyViewHolder holder, int position) {
        // assigning values to each rows as they come back to screen
        // based on position of the recycler view

        holder.thumbnailRecycleViewTextViewVar.setImageResource();
        holder.titleRecyclerTextViewVar.setText(folderModels.get(position).getTitle());
        holder.descriptionRecyclerTextViewVar.setText(folderModels.get(position).getDescription());
        holder.createAtRecyclerTextViewVar.setText(folderModels.get(position).getCreated_at());

    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items that are beeing displayed
        return folderModels.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabing the views from recycler_view_row layout file
        // kinda like oncreate

        ImageView thumbnailRecycleViewTextViewVar;
        TextView titleRecyclerTextViewVar, descriptionRecyclerTextViewVar, createAtRecyclerTextViewVar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnailRecycleViewTextViewVar = itemView.findViewById(R.id.thumbnailRecycleViewTextView);
            titleRecyclerTextViewVar = itemView.findViewById(R.id.titleRecyclerTextView);
            descriptionRecyclerTextViewVar = itemView.findViewById(R.id.descriptionRecyclerTextView);
            createAtRecyclerTextViewVar = itemView.findViewById(R.id.createAtRecyclerTextView);

        }
    }

}
