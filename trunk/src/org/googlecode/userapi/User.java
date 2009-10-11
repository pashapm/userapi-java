package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class User {
	private long userId;
    private String userName;
    private String userPhotoUrl;
    private String userPhotoUrlSmall;
    private boolean male;
    private boolean online = false;
    private VkontakteAPI api;

    public User(JSONArray userInfo, VkontakteAPI api) throws JSONException {
        this.api = api;
        userId = userInfo.getLong(0);
        int length = userInfo.length();
        if (length >= 3) {
            userName = userInfo.getString(1);
            userPhotoUrl = userInfo.getString(2);
            if (userPhotoUrl.equals("0")) userPhotoUrl = null;
        }
        if (length == 4)
            online = userInfo.getInt(3) == 1;
        if (length == 6) {
            userPhotoUrlSmall = userPhotoUrl == null ? null : userPhotoUrl.substring(0, userPhotoUrl.lastIndexOf("/") + 1) + userInfo.getString(3) + ".jpg";
            male = userInfo.getInt(4) == 2;
            online = userInfo.getInt(5) == 1;
        }
    }

    public User() {
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

    public String getUserPhotoUrlSmall() {
        return userPhotoUrlSmall;
    }

    public byte[] getUserPhoto() throws IOException {
        return api.getFileFromUrl(userPhotoUrl);
    }

    public byte[] getUserPhotoSmall() throws IOException {
        return api.getFileFromUrl(userPhotoUrlSmall);
    }

    public boolean isMale() {
        return male;
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
                ", userPhotoUrlSmall='" + userPhotoUrlSmall + '\'' +
                ", male=" + male +
                ", online=" + online +
                '}';
    }
}
