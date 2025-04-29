package com.example.socialmediacontentsaver.receiveData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.FolderContentAssociationHelper;
import com.example.socialmediacontentsaver.databaseHelpers.FolderDatabaseHelper;
import com.example.socialmediacontentsaver.models.FolderModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReceiveDataActivity extends AppCompatActivity {
    ContentDatabaseHelper contentDatabase;
    FolderDatabaseHelper folderDatabase;
    FolderContentAssociationHelper folderContentAssociationDatabase;
    EditText receiveTitleEditText, receiveDescriptionEditText;
    ImageView receiveThumbnailImageView;
    Button saveContentButton, addNewFolderButton;
    FolderRecyclerViewAdapter adapter;

    String sharedText = "";
    String thumbnail_path = "";
    String platform = "";

    // New fields for image selection
    private ActivityResultLauncher<Intent> folderImagePickerLauncher;
    private ImageButton currentFolderThumbnailButton;
    private String selectedFolderThumbnailPath = null;

    ArrayList<FolderModel> folderModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receive_data);

        // Initialize AppDatabaseHelper and get writable database
        AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper(this);
        SQLiteDatabase db = appDatabaseHelper.getWritableDatabase();

        // Initialize ContentDatabaseHelper and FolderDatabaseHelper
        contentDatabase = new ContentDatabaseHelper(db);
        folderDatabase = new FolderDatabaseHelper(db);
        folderContentAssociationDatabase = new FolderContentAssociationHelper(db);

        receiveTitleEditText = findViewById(R.id.receiveTitle);
        receiveDescriptionEditText = findViewById(R.id.receiveDescription);
        receiveThumbnailImageView = findViewById(R.id.receiveThumbnail);
        saveContentButton = findViewById(R.id.saveContent);
        addNewFolderButton = findViewById(R.id.addNewFolder);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    MetadataFetcher.fetchMetadata(this, sharedText, new MetadataFetchListener() {
                        @Override
                        public void onMetadataFetched(String title, String description, String savedPath, String fetchedPlatform) {
                            receiveTitleEditText.setText(title);
                            receiveDescriptionEditText.setText(description);
                            if (savedPath != null) {
                                Uri imageUri = Uri.fromFile(new java.io.File(savedPath));
                                receiveThumbnailImageView.setImageURI(imageUri);
                                thumbnail_path = imageUri.toString();
                            }
                            thumbnail_path = savedPath;
                            platform = fetchedPlatform;

                            PopulateLayoutWithFolders();
                        }
                    });
                }
            }
        }

        // Initialize image picker launcher
        initFolderImagePickerLauncher();

        AddData();
        setupAddNewFolderDialog();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.receive_data_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initFolderImagePickerLauncher() {
        folderImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        if (imageUri != null && currentFolderThumbnailButton != null) {
                            currentFolderThumbnailButton.setImageURI(imageUri);
                            selectedFolderThumbnailPath = imageUri.toString();
                        }
                    }
                }
        );
    }

    public void AddData() {
        saveContentButton.setOnClickListener(new View.OnClickListener() {
            boolean isSaving = false;

            @Override
            public void onClick(View v) {
                if (isSaving) {
                    return;
                }
                isSaving = true;

                String pattern = "dd/MM/yyyy";
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
                Date currentTime = Calendar.getInstance().getTime();
                String currentTimeStringify = df.format(currentTime);

                long contentId = contentDatabase.insertContent(
                        thumbnail_path,
                        receiveTitleEditText.getText().toString(),
                        receiveDescriptionEditText.getText().toString(),
                        platform,
                        currentTimeStringify,
                        sharedText
                );

                if (contentId > 0) {
                    ArrayList<FolderModel> selectedFolders = adapter.getSelectedFolders();

                    for (FolderModel folder : selectedFolders) {
                        long associationId = folderContentAssociationDatabase.addContentToFolder(
                                Integer.parseInt(folder.getId()),
                                (int) contentId
                        );
                    }
                    finishAffinity();
                }
                isSaving = false;
            }
        });
    }

    public void setupAddNewFolderDialog() {
        addNewFolderButton.setOnClickListener(v -> {
            selectedFolderThumbnailPath = null;
            View dialogView = getLayoutInflater().inflate(R.layout.add_folder_dialog, null);

            EditText folderTitleEditText = dialogView.findViewById(R.id.dialogFolderTitle);
            EditText folderDescriptionEditText = dialogView.findViewById(R.id.dialogFolderDescription);
            Button saveFolderButton = dialogView.findViewById(R.id.dialogSaveFolder);
            ImageButton folderThumbnailButton = dialogView.findViewById(R.id.imageButton);

            AlertDialog dialog = new AlertDialog.Builder(ReceiveDataActivity.this)
                    .setView(dialogView)
                    .create();

            dialog.show();

            saveFolderButton.setOnClickListener(view -> {
                String folderTitle = folderTitleEditText.getText().toString();
                String folderDescription = folderDescriptionEditText.getText().toString();

                if (!folderTitle.isEmpty()) {
                    String pattern = "dd/MM/yyyy";
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
                    Date currentTime = Calendar.getInstance().getTime();
                    String currentTimeStringify = df.format(currentTime);
                    String thumbnailToSave = selectedFolderThumbnailPath != null ? selectedFolderThumbnailPath : thumbnail_path;

                    folderDatabase.insertFolder(thumbnailToSave, folderTitle, folderDescription, currentTimeStringify);

                } else {
                    folderTitleEditText.setError("Title cannot be empty!");
                    return;
                }
                PopulateLayoutWithFolders();
                dialog.dismiss();
            });

            folderThumbnailButton.setOnClickListener(view -> {
                currentFolderThumbnailButton = folderThumbnailButton;

                // Intent to pick image from local storage
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);  // Hint to only show local files
                folderImagePickerLauncher.launch(intent);
            });
        });
    }

    public void PopulateLayoutWithFolders () {
        folderModels.clear();
        Cursor res = folderDatabase.getAllFolders();

        RecyclerView recyclerView = findViewById(R.id.foldersRecyclerViewReceiveData);

        while (res.moveToNext()) {
            folderModels.add(new FolderModel(res.getString(0), res.getString(1), res.getString(2), res.getString(3), res.getString(4)));
        }

        adapter = new FolderRecyclerViewAdapter(this, folderModels);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
