package com.android.mydiary;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class user {
    private String _id;
    private String password;
    private String name;
    private String profile;

    public user(String _id, String password, String name, String profile) {
        this._id = _id;
        this.password = password;
        this.name = name;
        this.profile = profile;
    }
    public user(String _id){
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
