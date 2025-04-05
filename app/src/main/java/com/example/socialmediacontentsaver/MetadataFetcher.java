package com.example.socialmediacontentsaver;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetadataFetcher {

    public static void fetchMetadata(String url, TextView textView) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String result;
            try {
                Document doc = Jsoup.connect(url).get();

                // yt image thumbnail, site_name platform, title title,
                // thumb, title, platf, savedate link
                //twitter, yt, instagram, tiktok, pinterest // fb, linkedin, reddit

                String title = doc.select("meta[property=og:title]").attr("content");
                String platform = doc.select("meta[property=og:site_name]").attr("content");

                if (title == null || title.isEmpty()) {
                    title = doc.title();
                }

                if (platform == null || platform.isEmpty()) {
                    platform = doc.title();
                }

                result = "Title: " + title;


            } catch (Exception e) {
                e.printStackTrace();
                result = "Failed to fetch metadata.";
            }

            String finalResult = result;
            handler.post(() -> {
                // Update UI on the main thread
                textView.append("\n\nMetadata:\n" + finalResult);
            });
        });
    }
}
