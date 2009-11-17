package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class Photo {
    private long userId;
    private long photoId;
    private String thumbnailUrl;
    private String imageUrl;

    public Photo(long userId, long photoId, String thumbnailUrl, String imageUrl) {
        this.userId = userId;
        this.photoId = photoId;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
    }

    public static Photo fromJson(JSONArray photoInfo) throws JSONException {
        //todo: handle no photo set - images/question_b.gif
        String userAndPhotoString = photoInfo.getString(0);
        if (!userAndPhotoString.equalsIgnoreCase("_0")) {
            long userId = Long.parseLong(userAndPhotoString.split("_")[0]);
            long photoId = Long.parseLong(userAndPhotoString.split("_")[1]);
            String thumbnailUrl = photoInfo.getString(1);
            String imageUrl = photoInfo.getString(2);
            return new Photo(userId, photoId, thumbnailUrl, imageUrl);
        } else {
            //temporary unavailable photo
            return null;
        }
    }

    public long getUserId() {
        return userId;
    }

    public long getPhotoId() {
        return photoId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "userId=" + userId +
                ", photoId=" + photoId +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}