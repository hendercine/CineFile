package com.example.android.cinefile.objects;

import org.parceler.Parcel;

@Parcel
public class Trailer {

    public String name, key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTrailerName() {
        return name;
    }

    public void setTrailerName(String name) {
        this.name = name;
    }

}
