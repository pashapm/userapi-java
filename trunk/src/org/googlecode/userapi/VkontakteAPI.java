package org.googlecode.userapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class VkontakteAPI {
    public long id;

    private AbstractHttpClient httpClient;
    //    private static final int SITE_ID = 4128;
    private static final int SITE_ID = 2;
    private static final String CAPTCHA_REQUIRED = "{\"ok\":-2}";
    private static final String SESSION_EXPIRED = "{\"ok\":-1}";
    private CaptchaHandler captchaHandler;
    private Credentials credentials;

    public void setCaptchaHandler(CaptchaHandler captchaHandler) {
        this.captchaHandler = captchaHandler;
    }

    public enum friendsTypes {
        friends, friends_mutual, friends_online, friends_new
    }

    public enum photosTypes {
        photos, photos_with, photos_new
    }

    public enum privateMessagesTypes {
        message, inbox, outbox
    }

    public AbstractHttpClient getHttpClient() {
        return httpClient;
    }

    public VkontakteAPI() {
        HttpParams params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, params);
        httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpClientHelper.setAcceptAllCookies(httpClient);
        HttpClientHelper.addGzipCompression(httpClient);
    }

    public boolean login(Credentials credentials) throws IOException {
        this.credentials = credentials;
        if (credentials.getSession() != null) {
            boolean b = loginWithSid();
            System.out.println("login with sid success? " + b);
            return b;
        } else if (credentials.getRemixpass() != null) {
            boolean b = loginWithRemix();
            System.out.println("login with remix success? " + b);
            return b;
        } else return loginWithPass();
    }

    private boolean loginWithSid() throws IOException {
        String url = "http://userapi.com/data?act=" + "history" + "&sid=" + credentials.getSession();
        return !getTextFromUrl(url).equals(SESSION_EXPIRED);
    }

    public boolean loginWithPass() throws IOException {
        String urlString = "http://login.userapi.com/auth?login=force&site=" + SITE_ID + "&email=" + credentials.getLogin() + "&pass=" + credentials.getLogin();
        HttpGet get = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(get);
        String location = response.getFirstHeader("Location").getValue();
        String sid = location.substring(location.indexOf("0;sid=") + "0;sid=".length());
        List<Cookie> cookies = httpClient.getCookieStore().getCookies();
        String remixpassword = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("remixpassword")) remixpassword = cookie.getValue();
            if (cookie.getName().equalsIgnoreCase("remixmid")) id = Long.parseLong(cookie.getValue());
        }
        credentials.setSession(sid);
        credentials.setRemixpass(remixpassword);
        return remixpassword != null;
    }

    public boolean loginWithRemix() throws IOException {
        String urlString = "http://login.userapi.com/auth?login=auto&site=" + SITE_ID;
        HttpGet get = new HttpGet(urlString);
        get.addHeader("Cookie", "remixpassword=" + credentials.getRemixpass());
        HttpResponse response = httpClient.execute(get);
        String location = response.getFirstHeader("Location").getValue();
        String sid = location.substring(location.indexOf("0;sid=") + "0;sid=".length());
        credentials.setSession(sid);
        return false;
    }

    public void logout() throws IOException {
        String urlString = "http://login.userapi.com/auth?login=logout&site=" + SITE_ID + "&sid=" + credentials.getSession();
        HttpGet get = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(get);
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
    public List<User> getFriends(long id, int from, int to, friendsTypes type) throws IOException, JSONException {
        List<User> friends = new LinkedList<User>();
        String url = "http://userapi.com/data?act=" + type.name() + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + credentials.getSession();
        String jsonText = getTextFromUrl(url);
        JSONArray fr;
        if (type == friendsTypes.friends_new) {
            JSONObject object = new JSONObject(jsonText);
            fr = object.getJSONArray("d");
        } else {
            fr = new JSONArray(jsonText);
        }
        for (int i = 0; i < fr.length(); i++) {
            JSONArray userInfo = (JSONArray) fr.get(i);
            friends.add(new User(userInfo, this));
        }
        return friends;
    }

    public List<User> getFriends(long userId) throws IOException, JSONException {
        int current = 0;
        int fetchSize = 1024;
        List<User> friends = new LinkedList<User>();
        while (friends.addAll(getFriends(userId, current, current + fetchSize, friendsTypes.friends))) {
            current += fetchSize;
        }
        //todo: friendsHiddenExcetion
        return friends;
    }

    public List<User> getMyFriends() throws IOException, JSONException {
        int current = 0;
        int fetchSize = 1024;
        List<User> friends = new LinkedList<User>();
        List<User> tmp;
        do {
            tmp = getFriends(id, current, current + fetchSize, friendsTypes.friends);
            friends.addAll(tmp);
            current += fetchSize;
        } while (tmp.size() == fetchSize);
        return friends;
    }

    public List<User> getMyNewFriends() throws IOException, JSONException {
        int current = 0;
        int fetchSize = 1024;
        List<User> friends = new LinkedList<User>();
        while (friends.addAll(getFriends(-1, current, current + fetchSize, friendsTypes.friends_new))) {
            current += fetchSize;
        }
        return friends;
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
        URL url = new URL("http://userapi.com/data?act=" + type.name() + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + credentials.getSession());
        String jsonText = getTextFromUrl(url);
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
        URL url = new URL("http://userapi.com/data?act=" + type + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + credentials.getSession());
        String jsonText = getTextFromUrl(url);
        JSONObject messagesJson = new JSONObject(jsonText);
//        Long count = messagesJson.getLong("n");
        Long history = messagesJson.getLong("h");
//        System.out.println("count:" + count);
        System.out.println("history:" + history);
        JSONArray messagesArray = messagesJson.getJSONArray("d");
        for (int i = 0; i < messagesArray.length(); i++) {
            JSONArray messageJson;
            if (type == privateMessagesTypes.message) {
                messageJson = (JSONArray) messagesArray.get(i);
            } else {
                JSONObject element = messagesArray.getJSONObject(i);
                messageJson = JSONHelper.objectToArray(element);
            }
            messages.add(new Message(messageJson, this));
        }
        //todo: total count
        return messages;
    }

    public List<Message> getInbox(int from, int to) throws IOException, JSONException {
        return getPrivateMessages(id, from, to, privateMessagesTypes.inbox);
    }

    public List<Message> getOutbox(int from, int to) throws IOException, JSONException {
        return getPrivateMessages(id, from, to, privateMessagesTypes.outbox);
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
        URL url = new URL("http://userapi.com/data?act=" + "wall" + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + credentials.getSession());
        String jsonText = getTextFromUrl(url);
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

    /**
     * This class send message to user
     *
     * @param sendingMessage - incapsulated request parameters
     * @return error code
     * @throws IOException
     */
    public String sendMessageToUser(Message sendingMessage)
            throws IOException {
        if ((sendingMessage == null) || (sendingMessage.getText() == null)) {
            throw new DataException("Null message to send");
        }
        URL url = new URL("http://userapi.com/data?act=add_message" +
                "&id=" + sendingMessage.getReceiverId() +
                "&ts=" + sendingMessage.getDate().getTime() +
                "&message=" + URLEncoder.encode(sendingMessage.getText(), "UTF-8") + "&sid=" + credentials.getSession());

        return getTextFromUrl(url);
    }

    /**
     * Returns new messages, new friends and new photos counters as ChagesHistory
     * Unlimited calls(captcha not required)
     *
     * @return new messages, new friends and new photos counters as ChagesHistory
     * @throws java.io.IOException    in case of connection problems
     * @throws org.json.JSONException
     */
    public ChangesHistory getChangesHistory() throws IOException, JSONException {
        String url = "http://userapi.com/data?act=" + "history" + "&sid=" + credentials.getSession();
        String jsonText = getTextFromUrl(url);
        JSONObject messagesJson = new JSONObject(jsonText);
        long messagesCount = messagesJson.has("nm") ? messagesJson.getLong("nm") : 0;
        long friendsCount = messagesJson.has("nf") ? messagesJson.getLong("nf") : 0;
        long photosCount = messagesJson.has("nph") ? messagesJson.getLong("nph") : 0;
        return new ChangesHistory(messagesCount, friendsCount, photosCount);
    }

    private List<Status> getStatusHistory(long id, int from, int to, long ts) throws IOException, JSONException {
        List<Status> statuses = new LinkedList<Status>();
        URL url = new URL("http://userapi.com/data?act=" + "activity" + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + credentials.getSession() + (ts == 0 ? "" : ("&ts=" + ts)));
        String jsonText = getTextFromUrl(url);
        JSONObject messagesJson = new JSONObject(jsonText);
        JSONArray messagesArray = messagesJson.getJSONArray("d");
        for (int i = 0; i < messagesArray.length(); i++) {
            JSONArray messageJson = (JSONArray) messagesArray.get(i);
            statuses.add(new Status(messageJson));
        }
        return statuses;
    }

    public List<Status> getStatusHistory(long id) throws IOException, JSONException {
        String url = "http://userapi.com/data?act=" + "activity" + "&from=" + 0 + "&to=" + 0 + "&id=" + id + "&sid=" + credentials.getSession();
        JSONObject messagesJson = getJsonFromUrl(url);
        int count = messagesJson.getInt("n");
        return getStatusHistory(id, 0, count, 0);
    }

    public List<Status> getMyStatusHistory() throws IOException, JSONException {
        String url = "http://userapi.com/data?act=" + "activity" + "&from=" + 0 + "&to=" + 0 + "&id=" + id + "&sid=" + credentials.getSession();
        JSONObject messagesJson = getJsonFromUrl(url);
        int count = messagesJson.getInt("n");
        return getStatusHistory(id, 0, count, 0);
    }

    public ProfileInfo getProfile(long id) throws IOException, JSONException {
        String url = "http://userapi.com/data?act=" + "profile" + "&id=" + id + "&sid=" + credentials.getSession();
        JSONObject jsonText = getJsonFromUrl(url);
        return new ProfileInfo(jsonText);
    }

    public ProfileInfo getMyProfile() throws IOException, JSONException {
        return getProfile(id);
    }


    protected byte[] getFileFromUrl(String url) throws IOException {
        if (url == null) return new byte[]{};
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
        if (result.equals(SESSION_EXPIRED)) {
            System.out.println("session expired!");
            credentials.setSession(null);
            login(credentials);
            return doWothCaptcha(url);
        } else if (result.equals(CAPTCHA_REQUIRED)) {
            System.out.println("captcha required!");
            return doWothCaptcha(url);
        }
        return result;
    }

    private String doWothCaptcha(String url) throws IOException {
        String captcha_sid = String.valueOf(Math.abs(new Random().nextLong()));
        String captcha_url = "http://userapi.com/data?act=captcha&csid=" + captcha_sid;
        String captcha_code = captchaHandler.handleCaptcha(captcha_url);
        return getTextFromUrl(url + "&fcsid=" + captcha_sid + "&fccode=" + captcha_code);
    }

    private String getTextFromUrl(URL url) throws IOException {
        return getTextFromUrl(url.toString());
    }

    private JSONObject getJsonFromUrl(String url) throws IOException, JSONException {
        return new JSONObject(getTextFromUrl(url));
    }
}
