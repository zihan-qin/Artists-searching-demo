package com.example.artsy.data;

public class SearchResult {

    private final String name;
    private final String img;
    private final String id;

    public SearchResult(String name, String img, String id) {
        this.name = name;
        this.img = img;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getImg() {
        return this.img;
    }

    public String getId() {
        return this.id;
    }
}
