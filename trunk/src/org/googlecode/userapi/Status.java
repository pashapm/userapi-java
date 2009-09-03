package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class Status {
    private long statusId;
    private long userId;
    private int reserved;//0, reserved (rather user's gender?)
    private String userName;
    private Date date;
    private String text;

    public Status(JSONArray statusJson) throws JSONException {
        statusId = Long.parseLong(statusJson.getString(0).split("_")[1]);
        userId = statusJson.getLong(1);
//        reserved = statusJson.getInt(2);
        userName = statusJson.getString(3);
        date = new Date(statusJson.getLong(4)*1000);
        text = statusJson.getString(5);
    }

    public Status(long statusId, long userId, int reserved, String userName, Date date, String text) {
        this.statusId = statusId;
        this.userId = userId;
        this.reserved = reserved;
        this.userName = userName;
        this.date = date;
        this.text = text;
    }

    public long getStatusId() {
        return statusId;
    }

    public long getUserId() {
        return userId;
    }

    public int getReserved() {
        return reserved;
    }

    public String getUserName() {
        return userName;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Status{" +
                "statusId=" + statusId +
                ", userId=" + userId +
                ", reserved=" + reserved +
                ", userName='" + userName + '\'' +
                ", date=" + date +
                ", text='" + text + '\'' +
                '}';
    }
}
