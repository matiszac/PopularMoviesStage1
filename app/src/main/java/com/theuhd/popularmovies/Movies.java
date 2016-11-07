package com.theuhd.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zachary on 11/5/2016.
 */

public class Movies implements Parcelable {



    private int id;
    private String title;
    private String release;
    private String plot;
    private double average;
    private String poster;

    public Movies () {

    }

    public Movies(int id, String title, String release, String plot, double average, String poster) {
        this.id = id;
        this.title = title;
        this.release = release;
        this.plot = plot;
        this.average = average;
        this.poster = poster;
    }

    // Parcel constructor
    private Movies(Parcel in) {
        id = in.readInt();
        title = in.readString();
        release = in.readString();
        plot = in.readString();
        average = in.readDouble();
        poster = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(release);
        dest.writeString(plot);
        dest.writeDouble(average);
        dest.writeString(poster);
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {

        @Override
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    @Override
    public String toString() {
        return String.format(
                "%d, %s, %s, %s, %.2f, %s ",
                id, title, release, plot, average, poster);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease() {
        return release;
    }

    public String getPlot() {
        return plot;
    }

    public double getAverage() {
        return average;
    }

    public String getPoster() {
        return poster;
    }
}
