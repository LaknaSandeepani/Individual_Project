package com.lk.individual_project;

public class Fish {
    private String name;
    private String countryName;
    private String imageUrl;

    public Fish(String name, String countryName, String imageUrl) {
        this.name = name;
        this.countryName = countryName;
        this.imageUrl = imageUrl;
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

}

