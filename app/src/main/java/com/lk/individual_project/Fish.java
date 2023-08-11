package com.lk.individual_project;

public class Fish {
    private String name;
    private String countryName;
    private String imageUrl;

    private String description;


    public Fish(String name) {
        this.name = name;
        this.countryName = countryName;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Fish() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

