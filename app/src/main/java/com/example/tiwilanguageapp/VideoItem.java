package com.example.tiwilanguageapp;

public class VideoItem {
    private final String videoId;
    private final String title;
    private final String channel;

    public VideoItem(String videoId, String title, String channel) {
        this.videoId = videoId;
        this.title = title;
        this.channel = channel;
    }

    public String getVideoId() { return videoId; }
    public String getTitle()   { return title; }
    public String getChannel() { return channel; }

    public String getThumbnailUrl() {
        // Standard YouTube thumbnail URL
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    public String getWebUrl() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }
}
