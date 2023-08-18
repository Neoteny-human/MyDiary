package com.android.mydiary;

import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;

public class mData {
    ArrayList<String> pictures;
    String weather;
    String Text;
    long _id;

    String address;
    String date;
    String youtube;

    public mData(ArrayList<String> pictures, String weather, String text, long _id) {
        this.pictures = pictures;
        this.weather = weather;
        Text = text;
        this._id = _id;
    }

    public mData(ArrayList<String> pictures, String weather, String text, long _id, String address, String date, String youtube) {
        this.pictures = pictures;
        this.weather = weather;
        Text = text;
        this._id = _id;
        this.address = address;
        this.youtube = youtube;
        this.date = date;
    }


    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }
}

