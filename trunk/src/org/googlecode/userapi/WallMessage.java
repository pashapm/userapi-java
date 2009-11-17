package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class WallMessage {
    //types TEXT by default
    public static final int TEXT = 0;
    public static final int FOTO = 1;
    public static final int GRAFFITY = 2;
    public static final int VIDEO = 3;
    public static final int AUDIO = 4;

    private long id;
    private Date date;
    private String text;
    private User sender;
    private User receiver;
    private int type = TEXT;
    private boolean read;
    private String contentUrl;
    private String previewUrl;

    public WallMessage(JSONArray jsonArray, VkontakteAPI api) throws JSONException {
        id = jsonArray.getLong(0);
        date = new Date(1000 * jsonArray.getLong(1));
        JSONArray textJsonArray = jsonArray.getJSONArray(2);
        text = textJsonArray.getString(0);
        if (textJsonArray.length() > 1) {
            type = textJsonArray.getInt(1);
            if (type == FOTO || type == GRAFFITY || type == VIDEO) {
                previewUrl = textJsonArray.getString(3);
            }
            if (type == FOTO || type == GRAFFITY || type == AUDIO || type == VIDEO) {
                contentUrl = textJsonArray.getString(4);
            }
        }
        sender = new User(jsonArray.getJSONArray(3), api);
        receiver = new User(jsonArray.getJSONArray(4), api);
        if (jsonArray.length() == 6)
            read = jsonArray.getInt(5) == 1;
    }


    public long getId() {
        return id;
    }

    public WallMessage(long id, Date date, String text, User sender, User receiver, int type, boolean read, String contentUrl, String previewUrl) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.read = read;
        this.contentUrl = contentUrl;
        this.previewUrl = previewUrl;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
