package com.example.socialmediacontentsaver;

public class ContentModel {
    String id;
    String thumbnail;
    String title;
    String duration;
    String platform;
    String creator;
    String save_date;
    String link;

    public ContentModel(String id, String thumbnail, String title, String duration, String platform, String creator, String save_date, String link) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.title = title;
        this.duration = duration;
        this.platform = platform;
        this.creator = creator;
        this.save_date = save_date;
        this.link = link;
    }
}
