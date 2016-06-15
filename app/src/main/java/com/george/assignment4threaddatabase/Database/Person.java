package com.george.assignment4threaddatabase.Database;

/**
 * Created by George on 2016-02-10.
 */
public class Person {


    private int id;
    private String Name;
    private String Picture;
    private String Description;

    public Person(){}

    public Person(String Name, String Picture, String Description) {
        super();
        this.Name = Name;
        this.Picture = Picture;
        this.Description = Description;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPicture() {
        return Picture;
    }
    public void setPicture(String Picture) {
        this.Picture = Picture;
    }

    public String getDescription(){ return Description; }
    public void setDescription(String Description){ this.Description = Description; }

    @Override
    public String toString() {
        return "Person [id=" + id + ", Name=" + Name + ", Picture=" + Picture +
                ", Description=" + Description  + "]";
    }


}
