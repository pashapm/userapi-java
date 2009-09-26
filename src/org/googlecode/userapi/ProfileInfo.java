package org.googlecode.userapi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Calendar;

public class ProfileInfo {
    protected long id;
    protected String firstname;
    protected String surname;
    protected String maidenName;
    protected Status status;
    protected String photo;
    protected int sex;
    protected Date birthday;
    protected String phone;

    public ProfileInfo(ProfileInfo p) {
    	id = p.getId();
    	firstname = p.getFirstname();
    	surname = p.getSurname();
    	maidenName = p.getMaidenName();
    	status = p.getStatus();
    	photo = p.getPhoto();
    	sex = p.getSex();
    	birthday = p.getBirthday();
    	phone = p.getPhone();
    }
    
    public ProfileInfo() {
    	
    }
    
    public ProfileInfo(JSONObject profileJson) throws JSONException {
        id = profileJson.getLong("id");
        firstname = profileJson.getString("fn");
        surname = profileJson.getString("ln");
        maidenName = profileJson.getString("mn");
        JSONObject element = profileJson.getJSONObject("actv");
        status = Status.fromJson(JSONHelper.objectToArray(element));
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
        if (profileJson.has("mo"))
            phone = profileJson.getString("mo");
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public Status getStatus() {
        return status;
    }

    public String getPhoto() {
        return photo;
    }

    public int getSex() {
        return sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getPhone() {
        return phone;
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
