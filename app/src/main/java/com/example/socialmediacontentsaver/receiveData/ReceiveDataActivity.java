package com.example.socialmediacontentsaver.receiveData;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;

import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.FolderDatabaseHelper;
import com.example.socialmediacontentsaver.R;

public class ReceiveDataActivity extends AppCompatActivity {
    ContentDatabaseHelper contentDatabase;
    FolderDatabaseHelper folderDatabase;
    EditText receiveTitleEditText, receiveDescriptionEditText;
    ImageView receiveThumbnailImageView;
    Button saveContentButton, addNewFolderButton;

    String sharedText = "";
    String thumbnail_path = "";
    String platform = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receive_data);

        // Initialize AppDatabaseHelper and get writable database
        AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper(this);
        SQLiteDatabase db = appDatabaseHelper.getWritableDatabase();

        // Initialize ContentDatabaseHelper with SQLiteDatabase
        contentDatabase = new ContentDatabaseHelper(db);
        folderDatabase = new FolderDatabaseHelper(db);     /////////////////////////////////////////////

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

        AddData();
        setupAddNewFolderDialog();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.receive_data_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

                        // Use insertContent() instead of insertData()
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

            // Create dialog
            AlertDialog dialog = new AlertDialog.Builder(ReceiveDataActivity.this)
                    .setView(dialogView)
                    .create();

            dialog.show();

            // Save button logic
            saveFolderButton.setOnClickListener(view -> {
                String folderTitle = folderTitleEditText.getText().toString();
                String folderDescription = folderDescriptionEditText.getText().toString();

                if (!folderTitle.isEmpty()) {
                    Toast.makeText(ReceiveDataActivity.this,
                            "Folder saved:\n" + folderTitle + "\n" + folderDescription,
                            Toast.LENGTH_LONG).show();
                } else {
                    folderTitleEditText.setError("Title cannot be empty!");
                }

                String pattern = "dd/MM/yyyy";
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
                Date currentTime = Calendar.getInstance().getTime();
                String currentTimeStringify = df.format(currentTime);

                folderDatabase.insertFolder("test", folderTitle, folderDescription, currentTimeStringify);

                dialog.dismiss();
            });

        });
    }

}
