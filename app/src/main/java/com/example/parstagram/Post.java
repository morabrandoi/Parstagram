package com.example.parstagram;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;


@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";

    // Getters
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    // Setters
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }
    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE,  parseFile);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

}
