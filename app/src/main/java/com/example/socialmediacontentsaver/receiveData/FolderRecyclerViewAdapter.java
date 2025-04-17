package com.example.socialmediacontentsaver.receiveData;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.MyViewHolder> {

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
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items that are beeing displayed
        return 0;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabing the views from recycler_view_row layout file
        // kinda like oncreate


//        ImageView mediaThumbnail;
//        TextView



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }

}
