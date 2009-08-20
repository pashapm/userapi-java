package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

public class Photo {
    private long userId;
    private long photoId;
    private String thumbnailUrl;
    private String imageUrl;

    public Photo(JSONArray photoInfo) throws JSONException {
        userId = Long.parseLong(photoInfo.getString(0).split("_")[0]);
        photoId = Long.parseLong(photoInfo.getString(0).split("_")[1]);
        thumbnailUrl = photoInfo.getString(1);
        imageUrl = photoInfo.getString(2);
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
