package com.rahul.simpleapiapp.core.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Info extends RealmObject {
    @Expose
    @SerializedName("version")
    public String version;
    @Expose
    @SerializedName("page")
    public int page;
    @Expose
    @SerializedName("results")
    public int results;
    @Expose
    @SerializedName("seed")
    public String seed;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
}
