package com.example.socialmediacontentsaver.models;

public class ContentModel {
    String id;
    String thumbnail;
    String title;
    String description;
    String platform;
    String save_date;
    String link;

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

    public String getPlatform() {
        return platform;
    }

    public String getSave_date() {
        return save_date;
    }

    public String getLink() {
        return link;
    }

    public ContentModel(String id, String thumbnail, String title, String description, String platform, String save_date, String link) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.platform = platform;
        this.save_date = save_date;
        this.link = link;
    }
}
