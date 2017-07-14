package com.gocktrip.models;

import android.content.Context;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class User implements Serializable{
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Uri avatarUri;
    private String password;

    public User() {
        id = 0;
        firstName = new String();
        lastName = new String();
        password = new String();
        email = new String();
        avatarUri = null;

    }

    public User(JSONObject obj) {

        try {
            id = obj.getInt("id");
            firstName = obj.getString("firstname");
            lastName = obj.getString("lastname");
            email = obj.getString("mail_address");
//            avatar = obj.getString("avatar");
            password = obj.getString("password");

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Uri getImageUri() {
        return avatarUri;
    }

    public void setImageUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }

    public void saveOnDisk(Context context) {

        FileOutputStream fos;
        try {
            fos = context.openFileOutput("currentUser", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User loadFromDisk(Context context) {

        FileInputStream fis;
        try {
            fis = context.openFileInput("currentUser");
            ObjectInputStream is = new ObjectInputStream(fis);
            User user = (User)is.readObject();
            is.close();
            return user;
        }
        catch (Exception e) {
            return null;
        }
    }

    public void deleteFromDisk(Context context) {

        context.deleteFile("currentUser");
    }

}
