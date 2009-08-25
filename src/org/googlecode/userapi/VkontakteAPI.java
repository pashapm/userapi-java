package org.googlecode.userapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

public class VkontakteAPI {
    String login;
    String sid;
    public long id;

    private String remixpassword;
    private DefaultHttpClient httpClient;
    private static final int SITE_ID = 2;

    public enum friendsTypes {
        friends, friends_mutual, friends_online, friends_new
    }

    public enum photosTypes {
        photos, photos_with, photos_new
    }

    public enum privateMessagesTypes {
        message, inbox, outbox
    }

    public VkontakteAPI() {
        sid = null;
        remixpassword = null;
        CookieSpecFactory acceptAllFactory = new CookieSpecFactory() {
            public CookieSpec newInstance(HttpParams params) {
                return new BrowserCompatSpec() {
                    public void validate(Cookie cookie, CookieOrigin origin)
                            throws MalformedCookieException {
                        //all cookies are accepted
                    }
                };
            }
        };

        httpClient = new DefaultHttpClient();
        httpClient.getCookieSpecs().register("accept_all", acceptAllFactory);
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "accept_all");
        httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, true);
    }

    public boolean login(String email, String pass) throws IOException {
        String urlString = "http://login.userapi.com/auth?login=force&site=" + SITE_ID + "&email=" + email + "&pass=" + pass;
        HttpGet get = new HttpGet(urlString);
        HttpContext context = new BasicHttpContext();
        HttpResponse response = httpClient.execute(get, context);

        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            httpEntity.consumeContent();
        }

        HttpUriRequest finalRequest = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);


        sid = finalRequest.getURI().getFragment().substring("0;sid=".length());
        System.out.println(sid);
        List<Cookie> cookies = httpClient.getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("remixpassword")) remixpassword = cookie.getValue();
            if (cookie.getName().equalsIgnoreCase("remixmid")) id = Long.parseLong(cookie.getValue());
        }
        return remixpassword != null;
    }

    public boolean login(String remixpasswordCookie) throws IOException {
        URL url = new URL("http://login.userapi.com/auth?​login=auto&site=2");
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Cookie", "remixpassword=" + remixpasswordCookie);
        connection.getContent();
        sid = connection.getURL().getRef().substring("0;sid=".length());
        return !sid.equalsIgnoreCase("-1");
    }

    public void logout() throws IOException {
        URL url = new URL("http://login.userapi.com/auth?login=logout&site=2&sid=" + sid);
        URLConnection connection = url.openConnection();
        connection.getContent();
    }

    /**
     * Returns friend list for a user
     *
     * @param id   user ID
     * @param from no. of first entry required (zero-based)
     * @param to   no. of last entry required (plus one)
     * @param type - type of friends to return
     * @return friend list for a user
     * @throws java.io.IOException    in case of connection problems
     * @throws org.json.JSONException
     */
    public ListWithTotal<User> getFriends(long id, int from, int to, friendsTypes type) throws IOException, JSONException {
        List<User> friends = new LinkedList<User>();
        URL url = new URL("http://userapi.com/data?act=" + type.name() + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid);
        String jsonText = getTextFromUrl(url);
        System.out.println(jsonText);
        JSONArray fr;
        long count = -1;
        if (type == friendsTypes.friends_new) {
            JSONObject object = new JSONObject(jsonText);
            count = object.getLong("n");
            fr = object.getJSONArray("d");
        } else {
            fr = new JSONArray(jsonText);
        }
        for (int i = 0; i < fr.length(); i++) {
            JSONArray userInfo = (JSONArray) fr.get(i);
            friends.add(new User(userInfo, this));
        }
        return new ListWithTotal<User>(friends, count);
    }

    /**
     * Returns photo list for a user
     *
     * @param id   user id
     * @param from first entry no.
     * @param to   last entry no.
     * @param type - type of photos to return
     * @return the last element in this list
     * @throws java.io.IOException    in case of connection problems
     * @throws org.json.JSONException
     */
    public List<Photo> getPhotos(long id, int from, int to, photosTypes type) throws IOException, JSONException {
        List<Photo> photos = new LinkedList<Photo>();
        URL url = new URL("http://userapi.com/data?act=" + type.name() + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid);
        String jsonText = getTextFromUrl(url);
        System.out.println(jsonText);
        JSONArray photosJson;
        if (type == photosTypes.photos) {
            photosJson = new JSONArray(jsonText);
        } else {
            photosJson = new JSONObject(jsonText).getJSONArray("d");
        }
        for (int i = 0; i < photosJson.length(); i++) {
            JSONArray photoInfo = (JSONArray) photosJson.get(i);
            photos.add(new Photo(photoInfo, this));
        }
        return photos;
    }

    public List<Message> getPrivateMessages(long id, int from, int to, privateMessagesTypes type) throws IOException, JSONException {
        List<Message> messages = new LinkedList<Message>();
        URL url = new URL("http://userapi.com/data?act=" + type + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid);
        String jsonText = getTextFromUrl(url);
        JSONObject messagesJson = new JSONObject(jsonText);
        Long count = messagesJson.getLong("n");
        Long history = messagesJson.getLong("h");
        JSONArray messagesArray = messagesJson.getJSONArray("d");
        for (int i = 0; i < messagesArray.length(); i++) {
            JSONArray messageJson;
            if (type == privateMessagesTypes.message) {
                messageJson = (JSONArray) messagesArray.get(i);
            } else {
                JSONObject element = messagesArray.getJSONObject(i);
                Object[] objects = {element.getLong("0"), element.getLong("1"), element.getJSONArray("2"), element.getJSONArray("3"), element.getJSONArray("4"), element.getInt("5")};
                messageJson = new JSONArray(Arrays.asList(objects));
            }
            messages.add(new Message(messageJson, this));
        }
        //todo: total count
        return messages;
    }

    /**
     * Returns wall messages list for a user
     *
     * @param id   user id
     * @param from first entry no.
     * @param to   last entry no.
     * @return the last element in this list
     * @throws java.io.IOException    in case of connection problems
     * @throws org.json.JSONException
     */
    public List<Message> getWallMessages(long id, int from, int to) throws IOException, JSONException {
        List<Message> messages = new LinkedList<Message>();
        URL url = new URL("http://userapi.com/data?act=" + "wall" + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid);
        String jsonText = getTextFromUrl(url);
        System.out.println(jsonText);
        JSONObject messagesJson = new JSONObject(jsonText);
        Long count = messagesJson.getLong("n");
        Long history = messagesJson.getLong("h");
        JSONArray messagesArray = messagesJson.getJSONArray("d");
        for (int i = 0; i < messagesArray.length(); i++) {
            JSONArray messageJson = (JSONArray) messagesArray.get(i);
            messages.add(new Message(messageJson, this));
        }
        return messages;
    }

//    public List<Message> getStatusMessages(long id, int from, int to) throws IOException, JSONException {
//        List<Message> messages = new LinkedList<Message>();
//        URL url = new URL("http://userapi.com/data?" + "activity" + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid);
//        String jsonText = getTextFromUrl(url);
//        System.out.println(jsonText);
//        todo: total count
//        return messages;
//    }


//    public ProfileInfo getProfile(long id) throws IOException, JSONException {
//        URL url = new URL("http://userapi.com/data?act=" + "profile" + "&id=" + id + "&sid=" + sid);
//        String jsonText = getTextFromUrl(url);
//        System.out.println(jsonText);
//        return new ProfileInfo();
//    }


    protected byte[] getFileFromUrl(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();
        byte[] result = null;
        if (httpEntity != null) {
            result = EntityUtils.toByteArray(httpEntity);
            httpEntity.consumeContent();
        }
        return result;
    }

    private String getTextFromUrl(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();
        String result = null;
        if (httpEntity != null) {
            result = EntityUtils.toString(httpEntity);
            httpEntity.consumeContent();
        }
        return result;
    }

    private String getTextFromUrl(URL url) throws IOException {
        return getTextFromUrl(url.toString());
    }

}
