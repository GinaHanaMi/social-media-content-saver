package com.example.socialmediacontentsaver;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Calendar;
import java.util.Date;

public class MetadataFetcher {

    // Call this method with image URL and context
    public static String downloadAndSaveImage(Context context, String imageUrl) {
        try {
            // Open connection to image
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();

            // Decode image into bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            // Save bitmap to internal storage
            File directory = context.getFilesDir();
            String filename = "thumb_" + System.currentTimeMillis() + ".png";
            File imageFile = new File(directory, filename);

            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return imageFile.getAbsolutePath(); // 👈 This is what you save
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or return "error"
        }
    }

    public static void fetchMetadata(Context context, String sharedText, TextView receiveTitleTextView, TextView receivePlatformTextView, TextView receiveSaveDateTextView, TextView receivingTxtTextView, ImageView receiveThumbnailImageView) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String resultTitle;
            String resultPlatform;
            String resultImageUrl;

            try {
                Document doc = Jsoup.connect(sharedText).get();

                // yt image thumbnail, site_name platform, title title,
                // thumb, title, platf, savedate link
                //twitter, yt, instagram, tiktok, pinterest // fb, linkedin, reddit

                String title = doc.select("meta[property=og:title]").attr("content");
                String platform = doc.select("meta[property=og:site_name]").attr("content");
                String imageUrl = doc.select("meta[property=og:image]").attr("content");

                if (title == null || title.isEmpty()) {
                    title = doc.title();
                }

                if (platform == null || platform.isEmpty()) {
                    platform = doc.title();
                }

                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = doc.title();
                }

                resultTitle = title;
                resultPlatform = platform;
                resultImageUrl = imageUrl;



            } catch (Exception e) {
                e.printStackTrace();
                resultTitle = "Failed to fetch title";
                resultPlatform = "Failed to fetch platform";
                resultImageUrl = "Failed to fetch thumbnail"; // Give the path to no image found!
            }

            String finalResultTitle = resultTitle;
            String finalResultPlatform = resultPlatform;
            String finalResultImageUrl = resultImageUrl;
            String savedPath = MetadataFetcher.downloadAndSaveImage(context, finalResultImageUrl);

            String pattern = "dd/MM/yyyy HH:mm:ss";
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
            Date currentTime = Calendar.getInstance().getTime();
            String currentTimeStringify = df.format(currentTime);


            handler.post(() -> {
                // Update UI on the main thread
                receiveTitleTextView.append(finalResultTitle);
                receivePlatformTextView.append(finalResultPlatform);
                receiveSaveDateTextView.append(currentTimeStringify);
                receivingTxtTextView.append(sharedText);
                receiveThumbnailImageView.setImageBitmap(BitmapFactory.decodeFile(savedPath));
            });
        });
    }
}
