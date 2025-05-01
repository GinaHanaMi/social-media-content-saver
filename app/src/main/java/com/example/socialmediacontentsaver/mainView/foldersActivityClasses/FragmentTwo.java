package com.example.socialmediacontentsaver.mainView.foldersActivityClasses;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.FolderContentAssociationHelper;
import com.example.socialmediacontentsaver.databaseHelpers.FolderDatabaseHelper;
import com.example.socialmediacontentsaver.mainView.SharedViewModel;
import com.example.socialmediacontentsaver.models.ContentModel;
import com.example.socialmediacontentsaver.models.FolderModel;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FragmentTwo extends Fragment implements FolderRecyclerViewInterface {
    FolderDatabaseHelper mainFolderDatabase;
    FolderContentAssociationHelper mainFolderContentAssociationDatabase;
    ContentDatabaseHelper mainContentDatabase;
    FoldersActivityRecyclerViewAdapter mainAdapter;
    ArrayList<FolderModel> mainFoldersModels = new ArrayList<>();
    SearchView foldersSearchView;

    String selectedFolderThumbnailPath = null;
    private ActivityResultLauncher<Intent> folderImagePickerLauncher;
    private ImageButton editFolderImageButton;
    private AlertDialog folderOptionsDialog;
    private ImageButton currentFolderThumbnailButton;
    String thumbnail_path = "";

    @Override
    public void onResume() {
        super.onResume();
        FoldersPopulateLayoutWithFolders();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        folderImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedFolderThumbnailPath = imageUri.toString();

                            // Load the selected image into the editContentImageButton
                            Glide.with(requireContext())
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(editFolderImageButton);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper(requireContext());
        SQLiteDatabase db = appDatabaseHelper.getWritableDatabase();
        mainFolderDatabase = new FolderDatabaseHelper(db);
        mainFolderContentAssociationDatabase = new FolderContentAssociationHelper(db);
        mainContentDatabase = new ContentDatabaseHelper(db);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FoldersPopulateLayoutWithFolders();

        Button folderAddNewFolderButton = view.findViewById(R.id.folderAddNewFolderButton);
        folderAddNewFolderButton.setOnClickListener(v -> {
            // Set current date
            String pattern = "dd/MM/yyyy";
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
            Date currentTime = Calendar.getInstance().getTime();
            String currentTimeStringify = df.format(currentTime);

            // Reset selected path before opening dialog
            selectedFolderThumbnailPath = null;

            View editDialogView = getLayoutInflater().inflate(R.layout.edit_folder_dialog, null);

            editFolderImageButton = editDialogView.findViewById(R.id.editFolderImageButton);
            EditText editFolderTitleEditText = editDialogView.findViewById(R.id.editFolderTitleEditText);
            EditText editFolderDescriptionEditText = editDialogView.findViewById(R.id.editFolderDescriptionEditText);
            Button editFolderSaveButton = editDialogView.findViewById(R.id.editFolderSaveButton);

            // Optional: set placeholder image for visual preview
            Glide.with(requireContext())
                    .load(R.drawable.ic_launcher_foreground)
                    .into(editFolderImageButton);

            AlertDialog editFolderDialog = new AlertDialog.Builder(requireContext())
                    .setView(editDialogView)
                    .create();

            editFolderDialog.show();

            editFolderImageButton.setOnClickListener(view1 -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                folderImagePickerLauncher.launch(intent);
            });

            editFolderSaveButton.setOnClickListener(view12 -> {
                String folderTitle = editFolderTitleEditText.getText().toString();

                if (folderTitle.isEmpty() || selectedFolderThumbnailPath == null || selectedFolderThumbnailPath.isEmpty()) {
                    if (folderTitle.isEmpty()) {
                        editFolderTitleEditText.setError("Title cannot be empty!");
                    }

                    if (selectedFolderThumbnailPath == null || selectedFolderThumbnailPath.isEmpty()) {
                        editFolderTitleEditText.setError("Thumbnail cannot be empty!");
                    }
                    return;
                }
                mainFolderDatabase.insertFolder(
                        selectedFolderThumbnailPath,
                        folderTitle,
                        editFolderDescriptionEditText.getText().toString(),
                        currentTimeStringify
                );

                FoldersPopulateLayoutWithFolders();
                editFolderDialog.dismiss();
            });
        });
    }

    public void FoldersPopulateLayoutWithFolders() {
        mainFoldersModels.clear();
        Cursor res = mainFolderDatabase.getAllFolders();

        RecyclerView mainRecyclerView = getView().findViewById(R.id.folderRecyclerView);
        foldersSearchView = getView().findViewById(R.id.folderSearchView);

        foldersSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                foldersFilter(newText);
                return true;
            }
        });

        while (res.moveToNext()) {
            mainFoldersModels.add(new FolderModel(res.getString(0), res.getString(1), res.getString(2), res.getString(3), res.getString(4)));
        }

        mainAdapter = new FoldersActivityRecyclerViewAdapter(requireContext(), mainFoldersModels, this);

        mainRecyclerView.setAdapter(mainAdapter);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void foldersFilter(String newText) {
        ArrayList<FolderModel> foldersFilteredList = new ArrayList<>();

        newText = newText.trim();

        if (newText.toLowerCase().startsWith("find:{") && newText.endsWith("}")) {
            String rawQuery = newText.substring(6, newText.length() - 1); // between find:{ and }
            String[] conditions = rawQuery.split(";");

            for (FolderModel singleItem : mainFoldersModels) {
                boolean matchesAll = true;

                for (String condition : conditions) {
                    String[] keyValue = condition.split(":", 2);
                    if (keyValue.length != 2) {
                        matchesAll = false;
                        break;
                    }

                    String key = keyValue[0].trim().toLowerCase();
                    String value = keyValue[1].trim().toLowerCase();

                    switch (key) {
                        case "folderid":
                            if (!singleItem.getId().toLowerCase().equals(value)) matchesAll = false;
                            break;
                        case "title":
                            if (!singleItem.getTitle().toLowerCase().contains(value)) matchesAll = false;
                            break;
                        case "description":
                            if (!singleItem.getDescription().toLowerCase().contains(value)) matchesAll = false;
                            break;
                        case "created":
                            if (!singleItem.getCreated_at().toLowerCase().contains(value)) matchesAll = false;
                            break;
                        default:
                            matchesAll = false;
                            break;
                    }

                    if (!matchesAll) break;
                }
                if (matchesAll) {
                    foldersFilteredList.add(singleItem);
                }
            }

        } else {
            for (FolderModel singleItem : mainFoldersModels) {
                if (singleItem.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                        singleItem.getDescription().toLowerCase().contains(newText.toLowerCase()) ||
                        singleItem.getCreated_at().toLowerCase().contains(newText.toLowerCase())) {
                    foldersFilteredList.add(singleItem);
                }
            }
        }

        mainAdapter.foldersFilterList(foldersFilteredList);
    }

    @Override
    public void onFolderItemClick(int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.folder_options_dialog, null);

        Button folderDialogDisplayContentsFolderButton = dialogView.findViewById(R.id.folderDialogDisplayContentsFolderButton);
        Button folderDialogEditFolderButton = dialogView.findViewById(R.id.folderDialogEditFolderButton);
        Button folderDialogDeleteFolderButton = dialogView.findViewById(R.id.folderDialogDeleteFolderButton);
        Button folderDialogDeleteFolderAndContentsButton = dialogView.findViewById(R.id.folderDialogDeleteFolderAndContentsButton);
        Button folderDialogDeleteContentsFromFolderButton = dialogView.findViewById(R.id.folderDialogDeleteContentsFromFolderButton);

        folderOptionsDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        folderOptionsDialog.show();

        folderDialogDisplayContentsFolderButton.setOnClickListener(view -> {
            int folderId = Integer.parseInt(mainFoldersModels.get(position).getId());

            // Set the search query
            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            sharedViewModel.setSearchQuery("find:{folderid:" + folderId + "}");


            // Switch to FragmentOne (tab 0)
            TabLayout tabLayout = requireActivity().findViewById(R.id.tabLayout);
            ViewPager2 viewPager2 = requireActivity().findViewById(R.id.viewPager);
            tabLayout.selectTab(tabLayout.getTabAt(0));
            viewPager2.setCurrentItem(0);

            // Dismiss both dialogs
            if (folderOptionsDialog != null) folderOptionsDialog.dismiss();
        });



        folderDialogEditFolderButton.setOnClickListener(view -> {
            View editDialogView = getLayoutInflater().inflate(R.layout.edit_folder_dialog, null);

            editFolderImageButton = editDialogView.findViewById(R.id.editFolderImageButton);
            EditText editFolderTitleEditText = editDialogView.findViewById(R.id.editFolderTitleEditText);
            EditText editFolderDescriptionEditText = editDialogView.findViewById(R.id.editFolderDescriptionEditText);
            Button editFolderSaveButton = editDialogView.findViewById(R.id.editFolderSaveButton);

            String thumbnailPath = mainFoldersModels.get(position).getThumbnail();
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
                    .into(editFolderImageButton);

            editFolderTitleEditText.setText(mainFoldersModels.get(position).getTitle());
            editFolderDescriptionEditText.setText(mainFoldersModels.get(position).getDescription());

            AlertDialog editFolderDialog = new AlertDialog.Builder(requireContext())
                    .setView(editDialogView)
                    .create();

            editFolderDialog.show();

            editFolderImageButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                folderImagePickerLauncher.launch(intent);
            });

            editFolderSaveButton.setOnClickListener(v -> {
                String newThumbnail = (selectedFolderThumbnailPath != null) ?
                        selectedFolderThumbnailPath : mainFoldersModels.get(position).getThumbnail();

                mainFolderDatabase.updateFolder(
                        Integer.parseInt(mainFoldersModels.get(position).getId()),
                        newThumbnail,
                        editFolderTitleEditText.getText().toString(),
                        editFolderDescriptionEditText.getText().toString(),
                        mainFoldersModels.get(position).getCreated_at()
                );

                FoldersPopulateLayoutWithFolders(); // refresh the list
                editFolderDialog.dismiss();
                folderOptionsDialog.dismiss();
            });
        });

        folderDialogDeleteFolderButton.setOnClickListener(view -> {
            mainFolderContentAssociationDatabase.deleteContentsInFolderAssociation(Integer.parseInt(mainFoldersModels.get(position).getId()));
            mainFolderDatabase.deleteFolder(Integer.parseInt(mainFoldersModels.get(position).getId()));
            FoldersPopulateLayoutWithFolders();
            folderOptionsDialog.dismiss();
        });

        folderDialogDeleteContentsFromFolderButton.setOnClickListener(view -> {
            int folderId = Integer.parseInt(mainFoldersModels.get(position).getId());

            // Step 1: Get all contents from the folder
            ArrayList<ContentModel> contentToDeleteFromFolder = new ArrayList<>();
            Cursor contentsCursor = mainFolderContentAssociationDatabase.getContentsInFolder(folderId);
            while (contentsCursor.moveToNext()) {
                contentToDeleteFromFolder.add(new ContentModel(
                        contentsCursor.getString(0),
                        contentsCursor.getString(1),
                        contentsCursor.getString(2),
                        contentsCursor.getString(3),
                        contentsCursor.getString(4),
                        contentsCursor.getString(5),
                        contentsCursor.getString(6)
                ));
            }
            contentsCursor.close(); // Always close cursor

            // Step 2: Remove the folder-content associations for this folder
            mainFolderContentAssociationDatabase.deleteContentsInFolderAssociation(folderId);

            // Step 3: For each content, delete it from saved_content_table if it's not associated with any other folder
            for (ContentModel singleContent : contentToDeleteFromFolder) {
                int contentId = Integer.parseInt(singleContent.getId());

                boolean isStillAssociated = mainFolderContentAssociationDatabase
                        .isContentAssociatedWithAnyFolder(contentId);

                if (!isStillAssociated) {
                    mainContentDatabase.deleteContent(contentId);
                }
            }

            // Step 4: Refresh UI and dismiss dialog
            FoldersPopulateLayoutWithFolders();
            folderOptionsDialog.dismiss();
        });

        folderDialogDeleteFolderAndContentsButton.setOnClickListener(view -> {
            int folderId = Integer.parseInt(mainFoldersModels.get(position).getId());

            // Step 1: Get all contents from the folder
            ArrayList<ContentModel> contentToDeleteFromFolder = new ArrayList<>();
            Cursor contentsCursor = mainFolderContentAssociationDatabase.getContentsInFolder(folderId);
            while (contentsCursor.moveToNext()) {
                contentToDeleteFromFolder.add(new ContentModel(
                        contentsCursor.getString(0), // ID
                        contentsCursor.getString(1), // title
                        contentsCursor.getString(2),
                        contentsCursor.getString(3),
                        contentsCursor.getString(4),
                        contentsCursor.getString(5),
                        contentsCursor.getString(6)
                ));
            }
            contentsCursor.close(); // Always close cursor

            // Step 2: Remove the folder-content associations for this folder
            mainFolderContentAssociationDatabase.deleteContentsInFolderAssociation(folderId);

            // Step 3: For each content, delete it from saved_content_table if it's not associated with any other folder
            for (ContentModel singleContent : contentToDeleteFromFolder) {
                int contentId = Integer.parseInt(singleContent.getId());

                boolean isStillAssociated = mainFolderContentAssociationDatabase
                        .isContentAssociatedWithAnyFolder(contentId);

                if (!isStillAssociated) {
                    mainContentDatabase.deleteContent(contentId);
                }
            }

            // Step 4: Delete the folder itself
            mainFolderDatabase.deleteFolder(folderId);

            // Step 5: Refresh UI and dismiss dialog
            FoldersPopulateLayoutWithFolders();
            folderOptionsDialog.dismiss();
        });
    }
}
