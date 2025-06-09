package com.example.uap.models;

public class PlantRequest {
    private String plant_name;
    private String description;
    private String price;

    public PlantRequest(String plant_name, String description, String price) {
        this.plant_name = plant_name;
        this.description = description;
        this.price = price;
    }
}