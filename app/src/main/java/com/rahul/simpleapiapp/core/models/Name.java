package com.rahul.simpleapiapp.core.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Name extends RealmObject {
    @Expose
    @SerializedName("last")
    public String last;
    @Expose
    @SerializedName("first")
    public String first;
    @Expose
    @SerializedName("title")
    public String title;

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
