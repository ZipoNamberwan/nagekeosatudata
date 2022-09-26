package com.bps.nagekeosatudata.sectoral;

public class DataSektoral {
    private String id;
    private String name;
    private int image;

    public DataSektoral(String id, String name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
