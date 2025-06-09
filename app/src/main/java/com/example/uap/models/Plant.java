package com.example.uap.models;

public class Plant {
    private int id;
    private String plant_name;
    private String description;
    private String price;

    public int getId() { return id; }
    public String getPlant_name() { return plant_name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }

    public Plant(String plant_name, String description, String price) {
        this.plant_name = plant_name;
        this.description = description;
        this.price = price;
    }
}

