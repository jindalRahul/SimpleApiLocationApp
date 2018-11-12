package com.rahul.simpleapiapp.core.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rahul.simpleapiapp.core.models.Info;
import com.rahul.simpleapiapp.core.models.Results;

import java.util.List;

public class RandomUserResponse {
    @Expose
    @SerializedName("info")
    public Info info;
    @Expose
    @SerializedName("results")
    public List<Results> results;
}
