package org.googlecode.userapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HTTP;
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
    private AbstractHttpClient httpClient;
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


        HttpParams params = new BasicHttpParams();
// Increase max total connection to 200
//        ConnManagerParams.setMaxTotalConnections(params, 200);
// Increase default max connection per route to 20
//        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
// Increase max connections for localhost:80 to 50
//        HttpHost localhost = new HttpHost("locahost", 80);
//        connPerRoute.setMaxForRoute(new HttpRoute(localhost), 50);
//        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(
                new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, params);
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
    public List<User> getFriends(long id, int from, int to, friendsTypes type) throws IOException, JSONException {
        List<User> friends = new LinkedList<User>();
        URL url = new URL("http://userapi.com/data?act=" + type.name() + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid);
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

    public List<User> getFriends() throws IOException, JSONException {
        int current = 0;
        int fetchSize = 1024;
        List<User> friends = new LinkedList<User>();
        while (friends.addAll(getFriends(id, current, current + fetchSize, friendsTypes.friends))) {
            current += fetchSize;
        }
        return friends;
    }

    public List<User> getNewFriends() throws IOException, JSONException {
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

    /**
     * Returns new messages, new friends and new photos counters as ChagesHistory
     *
     * @return new messages, new friends and new photos counters as ChagesHistory
     * @throws java.io.IOException    in case of connection problems
     * @throws org.json.JSONException
     */
    public ChangesHistory getChangesHistory() throws IOException, JSONException {
        URL url = new URL("http://userapi.com/data?act=" + "history" + "&sid=" + sid);
        String jsonText = getTextFromUrl(url);
        System.out.println(jsonText);
        JSONObject messagesJson = new JSONObject(jsonText);
        long messagesCount = messagesJson.has("nm") ? messagesJson.getLong("nm") : 0;
        long friendsCount = messagesJson.has("nf") ? messagesJson.getLong("nf") : 0;
        long photosCount = messagesJson.has("nph") ? messagesJson.getLong("nph") : 0;
        return new ChangesHistory(messagesCount, friendsCount, photosCount);
    }

    private List<Status> getStatusHistory(long id, int from, int to, long ts) throws IOException, JSONException {
        List<Status> statuses = new LinkedList<Status>();
        URL url = new URL("http://userapi.com/data?act=" + "activity" + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid + (ts == 0 ? "" : ("&ts=" + ts)));
        String jsonText = getTextFromUrl(url);
        System.out.println(jsonText);
        JSONObject messagesJson = new JSONObject(jsonText);
        JSONArray messagesArray = messagesJson.getJSONArray("d");
        for (int i = 0; i < messagesArray.length(); i++) {
            JSONArray messageJson = (JSONArray) messagesArray.get(i);
            statuses.add(new Status(messageJson));
        }
        return statuses;
    }

    public List<Status> getStatusHistory(long id) throws IOException, JSONException {
        String url = "http://userapi.com/data?act=" + "activity" + "&from=" + 0 + "&to=" + 0 + "&id=" + id + "&sid=" + sid;
        JSONObject messagesJson = getJsonFromUrl(url);
        int count = messagesJson.getInt("n");
        return getStatusHistory(id, 0, count, 0);
    }

    public List<Status> getStatusHistory() throws IOException, JSONException {
        String url = "http://userapi.com/data?act=" + "activity" + "&from=" + 0 + "&to=" + 0 + "&id=" + id + "&sid=" + sid;
        JSONObject messagesJson = getJsonFromUrl(url);
        int count = messagesJson.getInt("n");
        return getStatusHistory(id, 0, count, 0);
    }

//    public ProfileInfo getProfile(long id) throws IOException, JSONException {
//        URL url = new URL("http://userapi.com/data?act=" + "profile" + "&id=" + id + "&sid=" + sid);
//        String jsonText = getTextFromUrl(url);
//        System.out.println(jsonText);
//        return new ProfileInfo();
//    }


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
        return result;
    }

    private String getTextFromUrl(URL url) throws IOException {
        return getTextFromUrl(url.toString());
    }

    private JSONObject getJsonFromUrl(String url) throws IOException, JSONException {
        return new JSONObject(getTextFromUrl(url));
    }

}
