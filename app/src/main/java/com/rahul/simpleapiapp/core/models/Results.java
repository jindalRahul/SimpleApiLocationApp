package com.rahul.simpleapiapp.core.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Results  extends RealmObject {
    @Expose
    @SerializedName("picture")
    public Picture picture;


    @Expose
    @SerializedName("phone")
    public String phone;

    @Expose
    @SerializedName("dob")
    public Dob dob;
    @Expose
    @SerializedName("email")
    public String email;
    @Expose
    @SerializedName("name")
    public Name name;

    @PrimaryKey
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Dob getDob() {
        return dob;
    }

    public void setDob(Dob dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }
}
