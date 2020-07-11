package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {


    public static final String KEY_POST = "post";
    public static final String KEY_USER = "user";

    // Getters

    public Post getPostObj() {
        return (Post) getParseObject(KEY_POST);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    // Setters
    public void setPostObj(ParseObject post) {
        put(KEY_POST, post);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }


}
