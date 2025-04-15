package com.example.socialmediacontentsaver.receiveData;

public interface MetadataFetchListener {
    void onMetadataFetched(String title, String description, String thumbnailPath, String platform);
}