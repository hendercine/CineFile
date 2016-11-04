package com.example.android.cinefile.objects;

import org.parceler.Parcel;

@Parcel
public class Review {

    public String author, content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
