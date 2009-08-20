package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

public class User {
    private long userId;
    private String userName;
    private String userPhotoUrl;
    private boolean online = false;

    public User(JSONArray userInfo) throws JSONException {
        userId = userInfo.getLong(0);
        userName = userInfo.getString(1);
        userPhotoUrl = userInfo.getString(2);
        if (userInfo.length() == 4)
            online = userInfo.getInt(3) == 1;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPhotoUrl='" + userPhotoUrl + '\'' +
                ", online=" + online +
                '}';
    }
}
