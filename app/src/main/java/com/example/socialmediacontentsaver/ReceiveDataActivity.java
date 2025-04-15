package com.example.socialmediacontentsaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReceiveDataActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    TextView receivingTxtTextView, receiveTitleTextView, receivePlatformTextView, receiveSaveDateTextView, receiveThumbnailPathTextView;
    ImageView receiveThumbnailImageView;
    Button saveContentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receive_data);
        myDb = new DatabaseHelper(this);

        receivingTxtTextView = findViewById(R.id.receivingTxt);
        receiveTitleTextView = findViewById(R.id.receiveTitle);
        receivePlatformTextView = findViewById(R.id.receivePlatform);
        receiveSaveDateTextView = findViewById(R.id.receiveSaveDate);
        receiveThumbnailImageView = findViewById(R.id.receiveThumbnail);
        receiveThumbnailPathTextView = findViewById(R.id.receiveThumbnailPath);

        saveContentButton = findViewById(R.id.saveContent);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        AddData();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    MetadataFetcher.fetchMetadata(this ,sharedText, receiveTitleTextView, receivePlatformTextView, receiveSaveDateTextView, receivingTxtTextView, receiveThumbnailImageView, receiveThumbnailPathTextView);
                }
            }
        }

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
                        boolean isInserted = myDb.insertData(
                                receiveThumbnailPathTextView.getText().toString(),
                                receiveTitleTextView.getText().toString(),
                                receivePlatformTextView.getText().toString(),
                                receiveSaveDateTextView.getText().toString(),
                                receivingTxtTextView.getText().toString()
                        );
                        if(isInserted == true) {
                            Toast.makeText(ReceiveDataActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        }

                    }
                }
        );
    }

}
