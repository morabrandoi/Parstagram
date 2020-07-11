package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_CONTENTS = "contents";
    public static final String KEY_POST = "post";
    public static final String KEY_USER = "user";

    // Getters
    public String getContents(){
        return getString(KEY_CONTENTS);
    }
    public Post getPostObj() {
        return (Post) getParseObject(KEY_POST);
    }
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    // Setters
    public void setContents(String contents){
        put(KEY_CONTENTS, contents);
    }
    public void setPostObj(ParseObject post){
        put(KEY_POST,  post);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
