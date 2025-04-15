package com.example.socialmediacontentsaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class ReceiveDataActivity extends AppCompatActivity {
    ContentDatabaseHelper myDb;
    EditText receiveTitleEditText, receiveDescriptionEditText;
    ImageView receiveThumbnailImageView;
    Button saveContentButton;

    String sharedText = "";
    String thumbnail_path = "";
    String platform = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receive_data);

        myDb = new ContentDatabaseHelper(this);

        receiveTitleEditText = findViewById(R.id.receiveTitle);
        receiveDescriptionEditText = findViewById(R.id.receiveDescription);
        receiveThumbnailImageView = findViewById(R.id.receiveThumbnail);
        saveContentButton = findViewById(R.id.saveContent);

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

                        boolean isInserted = myDb.insertData(
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
}
