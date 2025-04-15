package com.example.socialmediacontentsaver;

public interface MetadataFetchListener {
    void onMetadataFetched(String title, String description, String thumbnailPath, String platform);
}