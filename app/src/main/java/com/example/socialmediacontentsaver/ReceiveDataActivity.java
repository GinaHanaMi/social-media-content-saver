package com.example.socialmediacontentsaver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReceiveDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receive_data);

        TextView receivingTxtTextView = (TextView)findViewById(R.id.receivingTxt);
        TextView receiveTitleTextView = (TextView)findViewById(R.id.receiveTitle);
        TextView receivePlatformTextView = (TextView)findViewById(R.id.receivePlatform);
        TextView receiveSaveDateTextView = (TextView)findViewById(R.id.receiveSaveDate);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
//                    // Inside the sharedText block:
//                    receivingTxtTextView.setText(sharedText);

                    // Call the new fetcher method
                    MetadataFetcher.fetchMetadata(sharedText, receiveTitleTextView, receivePlatformTextView, receiveSaveDateTextView, receivingTxtTextView);
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
