package com.example.socialmediacontentsaver.receiveData;

import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.socialmediacontentsaver.R;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            Bitmap originalBitmap = BitmapFactory.decodeStream(input);

            int targetWidth = 160;
            int targetHeight = (int) ((double) originalBitmap.getHeight() / originalBitmap.getWidth() * targetWidth);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

            File directory = context.getFilesDir();
            String filename = "thumb_" + System.currentTimeMillis() + ".png";
            File imageFile = new File(directory, filename);

            FileOutputStream out = new FileOutputStream(imageFile);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Main method to fetch metadata
    public static void fetchMetadata(Context context, String sharedText, MetadataFetchListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String resultTitle;
            String resultDescription;
            String resultImageUrl;
            String platform;

            // Determine platform and fetch metadata accordingly
            if (sharedText.contains("youtube.com")) {
                platform = "YouTube";

                try {
                    Document doc = Jsoup.connect(sharedText).get();
                    // https://chatgpt.com/c/681381dc-ba50-800c-862a-1004833d0661
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

                return;
            } else if (sharedText.contains("linkedin.com")) {
                platform = "LinkedIn";

                try {
                    Document doc = Jsoup.connect(sharedText).get();
                    // https://chatgpt.com/c/681381dc-ba50-800c-862a-1004833d0661
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

                return;
            } else if (sharedText.contains("instagram.com")) {
                platform = "Instagram";

                return;
            } else if (sharedText.contains("twitter.com") || sharedText.contains("x.com")) {
                platform = "Twitter";

                try {
                    // Normalize x.com to twitter.com
                    String normalizedUrl = sharedText.replace("x.com", "twitter.com");

                    // Build oEmbed endpoint
                    String oEmbedUrl = "https://publish.twitter.com/oembed?url=" + normalizedUrl;

                    // Fetch oEmbed JSON
                    URL url = new URL(oEmbedUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();

                    // Parse JSON
                    JSONObject json = new JSONObject(responseBuilder.toString());

                    // Get author_name as title
                    final String finalResultTitle = json.optString("author_name", "Unknown");

                    // Extract tweet HTML and decode
                    String rawHtml = json.optString("html", "");
                    String decodedText = Jsoup.parse(rawHtml).text();  // Converts HTML to clean Unicode text
                    final String finalResultDescription = decodedText;

                    // Use placeholder image from drawable
                    final String finalSavedPath = "android.resource://" + context.getPackageName() + "/" + R.drawable.ic_launcher_background;

                    final String finalPlatform = platform;

                    handler.post(() -> {
                        listener.onMetadataFetched(finalResultTitle, finalResultDescription, finalSavedPath, finalPlatform);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    final String fallbackTitle = "Failed to fetch tweet";
                    final String fallbackDescription = "Error loading Twitter content";
                    final String fallbackPath = null;
                    final String fallbackPlatform = "Twitter";

                    handler.post(() -> {
                        listener.onMetadataFetched(fallbackTitle, fallbackDescription, fallbackPath, fallbackPlatform);
                    });
                }

                return;
            } else if (sharedText.contains("tiktok.com")) {
                platform = "TikTok";

                resultDescription = "";  // Always empty as requested

                try {
                    // Build oEmbed URL
                    String oEmbedUrl = "https://www.tiktok.com/oembed?url=" + sharedText;

                    // Fetch oEmbed JSON
                    URL url = new URL(oEmbedUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();

                    // Parse JSON
                    JSONObject json = new JSONObject(responseBuilder.toString());

                    resultTitle = json.optString("title", "");
                    if (resultTitle.isEmpty()) {
                        resultTitle = json.optString("author_name", "TikTok Creator");
                    }

                    resultImageUrl = json.optString("thumbnail_url", null);

                } catch (Exception e) {
                    e.printStackTrace();
                    resultTitle = "Failed to fetch TikTok";
                    resultImageUrl = null;
                }

                // Save thumbnail locally
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

                return;
            }
            else if (sharedText.contains("reddit.com")) {
                platform = "Reddit";

                return;
            } else if (sharedText.contains("facebook.com")) {
                platform = "Facebook";

                return;
            } else if (sharedText.contains("pinterest.com")) {
                platform = "Pinterest";

                return;
            } else {
                platform = "Unknown";
                resultTitle = "Unknown platform";
                resultDescription = "Cannot determine platform";
                resultImageUrl = null;

                String finalResultTitle1 = resultTitle;
                String finalResultDescription1 = resultDescription;
                String finalPlatform1 = platform;
                handler.post(() -> {
                    listener.onMetadataFetched(finalResultTitle1, finalResultDescription1, null, finalPlatform1);
                });
                return;
            }
        });
    }
}
