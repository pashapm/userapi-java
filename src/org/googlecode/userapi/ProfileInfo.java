package org.googlecode.userapi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Calendar;

public class ProfileInfo {
    private long id;
    private String firstname;
    private String surname;
    private String maidenName;
    private Status status;
    private String photo;
    private int sex;
    private Date birthday;
    private String phone;

    public ProfileInfo(JSONObject profileJson) throws JSONException {
        id = profileJson.getLong("id");
        firstname = profileJson.getString("fn");
        surname = profileJson.getString("ln");
        maidenName = profileJson.getString("mn");
        JSONObject element = profileJson.getJSONObject("actv");
        status = new Status(JSONHelper.objectToArray(element));
        photo = profileJson.getString("bp");
        sex = profileJson.getInt("sx");
        int bd = profileJson.getInt("bd");
        int bm = profileJson.getInt("bm");
        int by = profileJson.getInt("by");
        if (bd != 0 && bm != 0 && by != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(by, bm - 1, bd, 0, 0, 0);
            birthday = calendar.getTime();
        }
        phone = profileJson.getString("mo");
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
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                '}';
    }
}
