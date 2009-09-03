package org.googlecode.userapi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ProfileInfo {
    private long id;
    private String firstname;
    private String surname;
    private String maidenName;
    private Status status;
    private String photo;
//    private int sex;
//    private Date birthday;

    public ProfileInfo(JSONObject profileJson) throws JSONException {
        id = profileJson.getLong("id");
        firstname = profileJson.getString("fn");
        surname = profileJson.getString("ln");
        maidenName = profileJson.getString("mn");
        JSONObject element = profileJson.getJSONObject("actv");
        status = new Status(JSONHelper.objectToArray(element));
        photo = profileJson.getString("bp");
    }

    @Override
    public String toString() {
        return "ProfileInfo{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", maidenName='" + maidenName + '\'' +
                ", status=" + status +
                ", photo='" + photo + '\'' +
                '}';
    }
}
