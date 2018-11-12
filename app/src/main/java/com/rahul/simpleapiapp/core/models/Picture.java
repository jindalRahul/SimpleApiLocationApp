package com.rahul.simpleapiapp.core.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Picture extends RealmObject {
    @Expose
    @SerializedName("thumbnail")
    public String thumbnail;
    @Expose
    @SerializedName("medium")
    public String medium;
    @Expose
    @SerializedName("large")
    public String large;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }
}
