package com.example.socialmediacontentsaver.receiveData;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.models.FolderModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.bumptech.glide.Glide;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<FolderModel> folderModels;
    HashMap<String, Boolean> selectedItems = new HashMap<>();

    public FolderRecyclerViewAdapter(Context context, ArrayList<FolderModel> folderModels) {
        this.context = context;
        this.folderModels = folderModels;
    }

    @NonNull
    @Override
    public FolderRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Where the layout gets inflated and it gives look to rows

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.receive_data_folder_recycler_view_row, parent, false);

        return new FolderRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderRecyclerViewAdapter.MyViewHolder holder, int position) {
        // Assign values based on position of the RecyclerView
        FolderModel folderModel = folderModels.get(position);

        // Load image using Glide
        String thumbnailPath = folderModel.getThumbnail();
        Uri thumbnailUri;

        if (thumbnailPath.startsWith("content://") || thumbnailPath.startsWith("file://")) {
            thumbnailUri = Uri.parse(thumbnailPath);
        } else {
            thumbnailUri = Uri.fromFile(new File(thumbnailPath));
        }

        Glide.with(context)
                .load(thumbnailUri)
                .placeholder(R.drawable.ic_launcher_background)
                .override(160, 90)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.thumbnailRecycleViewTextViewVar);

        holder.titleRecyclerTextViewVar.setText(folderModel.getTitle());
        holder.descriptionRecyclerTextViewVar.setText(folderModel.getDescription());
        holder.createAtRecyclerTextViewVar.setText(folderModel.getCreated_at());

        // Get ID
        String folderId = folderModel.getId();

        // Bind checkbox state safely
        holder.selectFolderRecycleViewVar.setOnCheckedChangeListener(null); // avoid recycled listeners
        holder.selectFolderRecycleViewVar.setChecked(selectedItems.containsKey(folderId) && selectedItems.get(folderId));

        // Set listener to update selection map
        holder.selectFolderRecycleViewVar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.put(folderId, true);
            } else {
                selectedItems.remove(folderId);
            }
        });
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items that are beeing displayed
        return folderModels.size();
    }

    public ArrayList<FolderModel> getSelectedFolders() {
        ArrayList<FolderModel> selectedFolders = new ArrayList<>();
        for (FolderModel folder : folderModels) {
            if (selectedItems.containsKey(folder.getId())) {
                selectedFolders.add(folder);
            }
        }
        return selectedFolders;
    }



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
