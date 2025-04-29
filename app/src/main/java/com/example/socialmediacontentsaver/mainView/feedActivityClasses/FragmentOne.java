package com.example.socialmediacontentsaver.mainView.feedActivityClasses;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.models.ContentModel;

import java.io.File;
import java.util.ArrayList;

public class FragmentOne extends Fragment implements FeedRecyclerViewInterface {
    ContentDatabaseHelper contentDatabase;
    FeedActivityRecyclerViewAdapter adapter;
    ArrayList<ContentModel> contentModels = new ArrayList<>();
    SearchView feedSearchView;
    String selectedContentThumbnailPath = null;
    private ActivityResultLauncher<Intent> contentImagePickerLauncher;
    private ImageButton editContentImageButton;

    @Override
    public void onResume() {
        super.onResume();
        FeedPopulateLayoutWithContent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedContentThumbnailPath = imageUri.toString();

                            // Load the selected image into the editContentImageButton
                            Glide.with(requireContext())
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(editContentImageButton);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper(requireContext());
        SQLiteDatabase db = appDatabaseHelper.getWritableDatabase();
        contentDatabase = new ContentDatabaseHelper(db);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FeedPopulateLayoutWithContent();
    }

    public void FeedPopulateLayoutWithContent() {
        contentModels.clear();
        Cursor res = contentDatabase.getAllContent();

        RecyclerView recyclerView = getView().findViewById(R.id.feedRecyclerView);
        feedSearchView = getView().findViewById(R.id.feedSearchView);

        feedSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                feedFilter(newText);
                return true;
            }
        });

        while (res.moveToNext()) {
            contentModels.add(new ContentModel(
                    res.getString(0), res.getString(1), res.getString(2),
                    res.getString(3), res.getString(4), res.getString(5), res.getString(6)));
        }

        adapter = new FeedActivityRecyclerViewAdapter(requireContext(), contentModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void feedFilter(String newText) {
        ArrayList<ContentModel> feedFilteredList = new ArrayList<>();
        for (ContentModel singleItem : contentModels) {
            if (singleItem.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getDescription().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getPlatform().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getSave_date().toLowerCase().contains(newText.toLowerCase())) {
                feedFilteredList.add(singleItem);
            }
        }
        adapter.feedFilterList(feedFilteredList);
    }

    @Override
    public void onFeedItemClick(int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.feed_content_dialog, null);

        Button feedDialogOpenContentButton = dialogView.findViewById(R.id.feedDialogOpenContentButton);
        Button feedDialogEditContentButton = dialogView.findViewById(R.id.feedDialogEditContentButton);
        Button feedDialogShareContentButton = dialogView.findViewById(R.id.feedDialogShareContentButton);
        Button feedDialogDeleteContentButton = dialogView.findViewById(R.id.feedDialogDeleteContentButton);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        dialog.show();

        feedDialogOpenContentButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentModels.get(position).getLink()));
            startActivity(browserIntent);
            dialog.dismiss();
        });

        feedDialogEditContentButton.setOnClickListener(view -> {
            View editDialogView = getLayoutInflater().inflate(R.layout.edit_content_dialog, null);

            editContentImageButton = editDialogView.findViewById(R.id.editContentImageButton);
            EditText editContentTitleEditText = editDialogView.findViewById(R.id.editContentTitleEditText);
            EditText editContentDescriptionEditText = editDialogView.findViewById(R.id.editContentDescriptionEditText);
            Button editContentSaveButton = editDialogView.findViewById(R.id.editContentSaveButton);

            String thumbnailPath = contentModels.get(position).getThumbnail();
            Uri thumbnailUri;

            if (thumbnailPath.startsWith("content://") || thumbnailPath.startsWith("file://")) {
                thumbnailUri = Uri.parse(thumbnailPath);
            } else {
                thumbnailUri = Uri.fromFile(new File(thumbnailPath));
            }

            Glide.with(requireContext())
                    .load(thumbnailUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(editContentImageButton);

            editContentTitleEditText.setText(contentModels.get(position).getTitle());
            editContentDescriptionEditText.setText(contentModels.get(position).getDescription());

            AlertDialog editDialog = new AlertDialog.Builder(requireContext())
                    .setView(editDialogView)
                    .create();

            editDialog.show();

            editContentImageButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                contentImagePickerLauncher.launch(intent);
            });


            editContentSaveButton.setOnClickListener(v -> {
                String newThumbnail = (selectedContentThumbnailPath != null) ?
                        selectedContentThumbnailPath : contentModels.get(position).getThumbnail();

                contentDatabase.updateContent(
                        Integer.parseInt(contentModels.get(position).getId()),
                        newThumbnail,
                        editContentTitleEditText.getText().toString(),
                        editContentDescriptionEditText.getText().toString(),
                        contentModels.get(position).getPlatform(),
                        contentModels.get(position).getSave_date(),
                        contentModels.get(position).getLink()
                );

                FeedPopulateLayoutWithContent(); // refresh the list
                editDialog.dismiss();
                dialog.dismiss();
            });
        });

        feedDialogShareContentButton.setOnClickListener(view -> {
            Intent sendContentURLIntent = new Intent();
            sendContentURLIntent.setAction(Intent.ACTION_SEND);
            sendContentURLIntent.putExtra(Intent.EXTRA_TEXT, contentModels.get(position).getLink());
            sendContentURLIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendContentURLIntent, null);
            startActivity(shareIntent);
            dialog.dismiss();
        });

        feedDialogDeleteContentButton.setOnClickListener(view -> {
            contentDatabase.deleteContent(Integer.parseInt(contentModels.get(position).getId()));
            FeedPopulateLayoutWithContent();
            dialog.dismiss();
        });
    }
}
