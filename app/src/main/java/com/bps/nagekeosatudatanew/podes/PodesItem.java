package com.bps.nagekeosatudatanew.podes;

public class PodesItem {
    private String id;
    private String nama;
    private String url;

    public PodesItem(String id, String nama, String url){
        this.id = id;
        this.nama = nama;
        this.url = url;    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getUrl() {
        return url;
    }
}
