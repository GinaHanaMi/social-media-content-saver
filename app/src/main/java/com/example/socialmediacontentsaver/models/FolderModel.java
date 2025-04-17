package com.example.socialmediacontentsaver.models;

public class FolderModel {

    String id;
    String thumbnail;
    String title;
    String description;
    String created_at;

    public FolderModel(String id, String thumbnail, String title, String description, String created_at) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated_at() {
        return created_at;
    }
}
