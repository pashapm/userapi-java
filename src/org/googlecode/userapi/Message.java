package org.googlecode.userapi;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class Message {
    private long id;
    private Date date;
    String text;
    User sender;
    User receiver;
    boolean read;

    public Message(JSONArray messageInfo, VkontakteAPI api) throws JSONException {
        id = messageInfo.getLong(0);
        date = new Date(1000 * messageInfo.getLong(1));
        JSONArray textJsonArray = messageInfo.getJSONArray(2);
        text = textJsonArray.getString(0);
        if (textJsonArray.length() > 1) {
//            System.out.println(textJsonArray);
            //todo: handle
        }
        sender = new User(messageInfo.getJSONArray(3), api);
        receiver = new User(messageInfo.getJSONArray(4), api);
        if (messageInfo.length() == 6)
            read = messageInfo.getInt(5) == 1;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", read=" + read +
                '}';
    }
}
