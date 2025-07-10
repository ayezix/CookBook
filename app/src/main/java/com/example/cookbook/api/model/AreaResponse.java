package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AreaResponse {
    @SerializedName("meals")
    private List<Area> areas;

    public List<Area> getAreas() {
        return areas;
    }

    public static class Area implements java.io.Serializable {
        @SerializedName("strArea")
        private String name;

        public String getName() { return name; }
    }
} 