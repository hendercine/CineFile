package com.example.android.cinefile.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hendercine on 10/23/16.
 */

public class Trailer implements Parcelable {

    private String mTrailerName, mTrailerKey;

    private Trailer(Parcel in) {
        mTrailerName = in.readString();
        mTrailerKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTrailerName);
        out.writeString(mTrailerKey);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) { return new Trailer(in); }

        public Trailer[] newArray(int size) { return new Trailer[size]; }
    };

    public String getmTrailerKey() {
        return mTrailerKey;
    }

    public String getmTrailerName() {
        return mTrailerName;
    }

}
