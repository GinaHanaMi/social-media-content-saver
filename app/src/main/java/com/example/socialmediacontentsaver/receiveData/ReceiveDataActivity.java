package com.example.socialmediacontentsaver.receiveData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.FolderDatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReceiveDataActivity extends AppCompatActivity {
    ContentDatabaseHelper contentDatabase;
    FolderDatabaseHelper folderDatabase;
    EditText receiveTitleEditText, receiveDescriptionEditText;
    ImageView receiveThumbnailImageView;
    Button saveContentButton, addNewFolderButton;

    String sharedText = "";
    String thumbnail_path = "";
    String platform = "";

    // New fields for image selection
    private ActivityResultLauncher<Intent> folderImagePickerLauncher;
    private ImageButton currentFolderThumbnailButton;
    private String selectedFolderThumbnailPath = null;

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
                                receiveThumbnailImageView.setImageBitmap(BitmapFactory.decodeFile(savedPath));
                            }
                            thumbnail_path = savedPath;
                            platform = fetchedPlatform;
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
        saveContentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pattern = "dd/MM/yyyy";
                        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
                        Date currentTime = Calendar.getInstance().getTime();
                        String currentTimeStringify = df.format(currentTime);

                        boolean isInserted = contentDatabase.insertContent(
                                thumbnail_path,
                                receiveTitleEditText.getText().toString(),
                                receiveDescriptionEditText.getText().toString(),
                                platform,
                                currentTimeStringify,
                                sharedText
                        );

                        if (isInserted) {
                            Toast.makeText(ReceiveDataActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        }
                    }
                }
        );
    }

    public void setupAddNewFolderDialog() {
        addNewFolderButton.setOnClickListener(v -> {
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

                    String thumbnailToSave = selectedFolderThumbnailPath != null ? selectedFolderThumbnailPath : "none";

                    folderDatabase.insertFolder(thumbnailToSave, folderTitle, folderDescription, currentTimeStringify);

                    Toast.makeText(ReceiveDataActivity.this,
                            "Folder saved:\n" + folderTitle + "\n" + folderDescription,
                            Toast.LENGTH_LONG).show();
                } else {
                    folderTitleEditText.setError("Title cannot be empty!");
                    return;
                }

                dialog.dismiss();
            });

            folderThumbnailButton.setOnClickListener(view -> {
                currentFolderThumbnailButton = folderThumbnailButton;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                folderImagePickerLauncher.launch(intent);
            });
        });
    }
}
