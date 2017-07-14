package com.gocktrip.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Video implements Serializable{

    public int id;
    public String title;
    public String city;
    public String video_url;
    public String thumbnail_url;
    public String description;
    public String longitude;
    public String latitude;
    public int user_id;
    public String category;
    public String establishment_name;
    public String creation_date;
    public String views;
    public String firstname;
    public String lastname;
    public String mail_address;
    public String avatar;
    public int canThank;
    public int thanksCount;



    public Video(JSONObject obj) throws JSONException {
        id = obj.getInt("id");
        title = obj.getString("title");
        city = obj.getString("city");
        video_url = obj.getString("video_url");
        thumbnail_url = obj.getString("thumbnail_url");
        description = obj.getString("description");
        longitude = obj.getString("longitude");
        latitude = obj.getString("latitude");
        user_id = obj.getInt("user_id");
        category = obj.getString("category");
        establishment_name = obj.getString("establishment_name");
        creation_date = obj.getString("creation_date");
        views = obj.getString("views");
        firstname = obj.getString("firstname");
        lastname = obj.getString("lastname");
        mail_address = obj.getString("mail_address");
        avatar = obj.getString("avatar");
        canThank = obj.getInt("canThank");
        thanksCount = obj.getInt("thanksCount");
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getVideo_url(){
        return video_url;
    }

    public String getThumbnail_url(){
        return thumbnail_url;
    }

    public String getDescription () {
        return description;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getLatitude(){
        return latitude;
    }

    public int getUser_id(){
        return user_id;
    }

    public String getCategory() {
        return category;
    }

    public String getEstablishment_name() {
        return establishment_name;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getViews() {
        return views;
    }

    public String getFirstname () {
        return firstname;
    }

    public String getLastname () {
        return lastname;
    }

    public String getMail_address() {
        return mail_address;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getCanThank(){
        return canThank;
    }

    public int getThanksCount() {
        return thanksCount;
    }





}
