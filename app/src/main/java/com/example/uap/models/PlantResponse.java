package com.example.uap.models;

import java.util.List;

public class PlantResponse {
    private String message;
    private List<Plant> data;

    public String getMessage() { return message; }
    public List<Plant> getData() { return data; }
}