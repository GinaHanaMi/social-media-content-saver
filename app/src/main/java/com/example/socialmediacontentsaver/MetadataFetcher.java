package com.example.socialmediacontentsaver;

import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetadataFetcher {

    public static String downloadAndSaveImage(Context context, String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);

            File directory = context.getFilesDir();
            String filename = "thumb_" + System.currentTimeMillis() + ".png";
            File imageFile = new File(directory, filename);

            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void fetchMetadata(Context context, String sharedText, MetadataFetchListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String resultTitle;
            String resultDescription;
            String resultImageUrl;
            String platform;

            try {
                Document doc = Jsoup.connect(sharedText).get();

                resultTitle = doc.select("meta[property=og:title]").attr("content");
                resultDescription = doc.select("meta[property=og:description]").attr("content");
                platform = doc.select("meta[property=og:site_name]").attr("content");
                resultImageUrl = doc.select("meta[property=og:image]").attr("content");

                if (resultTitle == null || resultTitle.isEmpty()) {
                    resultTitle = doc.title();
                }

                if (resultDescription == null || resultDescription.isEmpty()) {
                    resultDescription = doc.title();
                }

                if (resultImageUrl == null || resultImageUrl.isEmpty()) {
                    resultImageUrl = null;
                }

                if (platform == null || platform.isEmpty()) {
                    platform = "Unknown";
                }

            } catch (Exception e) {
                e.printStackTrace();
                resultTitle = "Failed to fetch title";
                resultDescription = "Failed to fetch description";
                platform = "Unknown";
                resultImageUrl = null;
            }

            String savedPath = null;
            if (resultImageUrl != null) {
                savedPath = downloadAndSaveImage(context, resultImageUrl);
            }

            String finalResultTitle = resultTitle;
            String finalResultDescription = resultDescription;
            String finalPlatform = platform;
            String finalSavedPath = savedPath;

            handler.post(() -> {
                listener.onMetadataFetched(finalResultTitle, finalResultDescription, finalSavedPath, finalPlatform);
            });
        });
    }
}
