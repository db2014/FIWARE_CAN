package com.example.okis.fiwareproba.moj_mvc;

public  class OsnoniModel {
    private String type;
    private String name;
    private Object value;

    public OsnoniModel(String naziv, String tip,Object vrednost){
        this.type=tip;
        this.name=naziv;
        this.value=vrednost;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
