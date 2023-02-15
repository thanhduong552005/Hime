package com.example.khkt_2023.models;

public class Suggestion {
    private String title;
    private String content;
    private String button;

    public Suggestion(String title, String content, String button) {
        this.title = title;
        this.content = content;
        this.button = button;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getButton() {
        return button;
    }
}
