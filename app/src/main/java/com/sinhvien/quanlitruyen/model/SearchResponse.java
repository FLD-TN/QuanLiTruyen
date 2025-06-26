package com.sinhvien.quanlitruyen.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {

    @SerializedName("items")
    public List<Result> items;

    public static class Result {
        @SerializedName("title")
        public String title;

        @SerializedName("link")
        public String url;

        @SerializedName("snippet")
        public String description;
    }
}
