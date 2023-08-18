package com.android.mydiary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "Diary.db";

    public DBHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table user(_id text primary key, password text, name text not null, profile text)");
        db.execSQL("create table diary(_id integer primary key autoincrement, weather text, pictures text, text text, address text, date text, youtube text, user_id text, foreign key(user_id) references user(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("create table if not exists user(_id text primary key, password text, name text not null, profile text)");

        db.execSQL("drop table if exists diary");
        db.execSQL("create table if not exists diary(_id integer primary key autoincrement, weather text, pictures text, text text, address text, date text, youtube text, user_id text, foreign key(user_id) references user(_id))");
    }
    public void onResetUser(SQLiteDatabase db){
        db.execSQL("drop table if exists user");
        db.execSQL("create table if not exists user(_id text primary key, password text, name text not null, profile text)");
    }
    public void onResetDiary(SQLiteDatabase db){
        db.execSQL("drop table if exists diary");
        db.execSQL("create table if not exists diary(_id integer primary key autoincrement, weather text, pictures text, text text, address text, date text, youtube text, user_id text, foreign key(user_id) references user(_id))");
    }


    public user getUser(String _id){
        user user = new user(_id);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from user where _id = '"+_id+"'", null);
        if(c.getCount() != 0){
            while (c.moveToNext()) {

                int _idIndex = c.getColumnIndex("_id");
                int passwordIndex = c.getColumnIndex("password");
                int nameIndex = c.getColumnIndex("name");
                int profileIndex = c.getColumnIndex("profile");


                String _ID = c.getString(_idIndex);
                String password = c.getString(passwordIndex);
                String name = c.getString(nameIndex);

                String profile = c.getString(profileIndex);

                user.setName(name);
                user.setPassword(password);
                user.setProfile(profile);
            }
        }
        c.close();

        return user;
    }

    @SuppressLint("Range")
    public String getPassword(String _id){
        String password = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select password from user where _id = '"+_id+"'", null);

        if(c.getCount()!=0) {
            c.moveToNext();
            password = c.getString(0);
        }
        c.close();


        return password;
    }

    public ArrayList<String> getIDList(){
        ArrayList<String> IDList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select _id from user", null);
        if(c.getCount()!=0){
            while(c.moveToNext()){
                int IDIndex = c.getColumnIndex("_id");
                String ID = c.getString(IDIndex);
                IDList.add(ID);
            }
        }
        c.close();
        return IDList;
    }


    public void CreateUser(String _id, String password, String name, String profile ){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into user (_id, password, name, profile) values ('" + _id + "','" +  password + "','" + name + "','"+ profile +"');");
    }
    public void DeleteUser(String _id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from user where _id = '"+_id+"'");
    }

    public void UpdateUser(String _id, String name, String password, String profile){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update user set name = '"+name+"', password = '"+password+"', profile = '"+profile+"' where _id = '"+_id+"'");
    }
    public void UpdateUserName(String _id, String name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update user set name = '"+name+"' where _id = '"+_id+"'");
    }
    public void UpdateUserPassword(String _id, String password){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update user set password = '"+password+"' where _id = '"+_id+"'");
    }
    public void UpdateUserProfile(String profile, String _id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update user set profile = '"+profile+"' where _id = '"+_id+"'");
    }

    public void UpdateUserInformation(String _id, String name, String password){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update user set name = '"+name+"', password = '"+password+"' where _id = '"+_id+"'");
    }




    //////////////////다이어리 관련.

    public long InsertDiary(mData mData, String user_id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        JSONArray jsonArray = new JSONArray(mData.getPictures());

        values.put("weather", mData.getWeather());
//        values.put("picture", mData.getPicture());
        values.put("pictures", jsonArray.toString());
        values.put("text", mData.getText());
        values.put("user_id", user_id);

        //추가
        values.put("address", mData.getAddress());
        values.put("youtube", mData.getYoutube());
        values.put("date", mData.getDate());
        return db.insert("diary", null, values);
    }


    public void DeleteDiary(long _id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from diary where _id = '"+_id+"'");
    }

    public void UpdateDiary(long _id, mData mData){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        JSONArray jsonArray = new JSONArray(mData.getPictures());

        values.put("weather", mData.getWeather());
        values.put("pictures", jsonArray.toString());
        values.put("text", mData.getText());
        //추가
        values.put("address", mData.getAddress());
        values.put("youtube", mData.getYoutube());
        values.put("date", mData.getDate());
        db.update("diary", values, "_id ="+_id,null);
    }



    public ArrayList<mData> getDiary(String user_id) {
        ArrayList<mData> mDataArrayList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from diary where user_id = '"+user_id+"' order by _id desc", null);
        if(cursor.getCount()!= 0){
            while(cursor.moveToNext()){
                int _idIndex = cursor.getColumnIndex("_id");
                int weatherIndex = cursor.getColumnIndex("weather");
                int picturesIndex = cursor.getColumnIndex("pictures");
                int textIndex = cursor.getColumnIndex("text");
                int addressIndex = cursor.getColumnIndex("address");
                int youtubeIndex = cursor.getColumnIndex("youtube");
                int dateIndex = cursor.getColumnIndex("date");

                long _id = cursor.getLong(_idIndex);
                String weather = cursor.getString(weatherIndex);
                String text = cursor.getString(textIndex);
                String address = cursor.getString(addressIndex);
                String youtube = cursor.getString(youtubeIndex);
                String date = cursor.getString(dateIndex);

                try {
                    JSONArray jsonArray = new JSONArray(cursor.getString(picturesIndex));
                    ArrayList<String> pictures = new ArrayList<>();
                    for(int i = 0; i < jsonArray.length(); i++){
                        String picture = "";
                        picture = jsonArray.getString(i);
                        if(picture!=null){
                            pictures.add(picture);
                        }
                    }
                    mData mdata = new mData(pictures, weather, text, _id);
                    mdata.setAddress(address);
                    mdata.setYoutube(youtube);
                    mdata.setDate(date);
                    mDataArrayList.add(mdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        cursor.close();
        return mDataArrayList;
    }

    public void DeleteUserDiary(String user_id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from diary where user_id = '"+user_id+"'");
    }

    
}
