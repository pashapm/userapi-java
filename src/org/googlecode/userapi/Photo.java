package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class Photo {
    private long userId;
    private long photoId;
    private String thumbnailUrl;
    private String imageUrl;
    private VkontakteAPI api;

    public Photo(JSONArray photoInfo, VkontakteAPI api) throws JSONException {
        this.api = api;
        //todo: handle ["_0","images\/m_null.gif","images\/x_null.gif"] - server wit photo temporary unavailable
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

    public byte[] getThumbnail() throws IOException {
        return api.getFileFromUrl(thumbnailUrl);
    }

    public byte[] getImage() throws IOException {
        return api.getFileFromUrl(imageUrl);
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
