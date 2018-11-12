package com.rahul.simpleapiapp.core.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Dob extends RealmObject {
    @Expose
    @SerializedName("age")
    public int age;
    @Expose
    @SerializedName("date")
    public String date;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
