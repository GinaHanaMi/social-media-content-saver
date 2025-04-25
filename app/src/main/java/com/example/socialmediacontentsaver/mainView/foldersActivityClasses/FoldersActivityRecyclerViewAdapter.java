package com.example.socialmediacontentsaver.mainView.foldersActivityClasses;

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
import com.example.socialmediacontentsaver.models.FolderModel;

import java.io.File;
import java.util.ArrayList;

public class FoldersActivityRecyclerViewAdapter extends RecyclerView.Adapter<FoldersActivityRecyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<FolderModel> mainFolderModels;

    public FoldersActivityRecyclerViewAdapter(Context context, ArrayList<FolderModel> mainFolderModels) {
        this.context = context;
        this.mainFolderModels = mainFolderModels;
    }

    @NonNull
    @Override
    public FoldersActivityRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Where the layout gets inflated and it gives look to rows

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.folder_recycler_view_row, parent, false);

        return new FoldersActivityRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersActivityRecyclerViewAdapter.MyViewHolder holder, int position) {
        FolderModel mainFolderModel = mainFolderModels.get(position);

        String thumbnailPath = mainFolderModel.getThumbnail();

        Uri thumbnailUri;
        if (thumbnailPath.startsWith("content://") || thumbnailPath.startsWith("file://")) {
            thumbnailUri = Uri.parse(thumbnailPath);
        } else {
            thumbnailUri = Uri.fromFile(new File(thumbnailPath));
        }

        Glide.with(context)
                .load(thumbnailUri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.folderThumbnailRecycleViewImageViewVar);

        holder.folderTitleRecyclerTextViewVar.setText(mainFolderModel.getTitle());
        holder.folderDescriptionRecyclerTextViewVar.setText(mainFolderModel.getDescription());
        holder.folderCreatedAtRecyclerTextViewVar.setText(mainFolderModel.getCreated_at());
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items that are beeing displayed
        return mainFolderModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabing the views from recycler_view_row layout file
        // kinda like oncreate
        ImageView folderThumbnailRecycleViewImageViewVar;
        TextView folderTitleRecyclerTextViewVar, folderDescriptionRecyclerTextViewVar, folderCreatedAtRecyclerTextViewVar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            folderThumbnailRecycleViewImageViewVar = itemView.findViewById(R.id.folderThumbnailRecycleViewImageView);
            folderTitleRecyclerTextViewVar = itemView.findViewById(R.id.folderTitleRecyclerTextView);
            folderDescriptionRecyclerTextViewVar = itemView.findViewById(R.id.folderDescriptionRecyclerTextView);
            folderCreatedAtRecyclerTextViewVar = itemView.findViewById(R.id.folderCreatedAtRecyclerTextView);
        }
    }
}
