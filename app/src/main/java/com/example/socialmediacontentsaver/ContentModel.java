package com.example.socialmediacontentsaver;

public class ContentModel {
    String id;
    String thumbnail;
    String title;
    String description;
    String platform;
    String save_date;
    String link;

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
