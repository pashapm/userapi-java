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

public class VkontakteAPI {
    String login;
    String sid;
    public String id;

    private String remixpassword;
    private DefaultHttpClient httpClient;

    public enum friendsTypes {
        friends, friends_mutual, friends_online, friends_new
    }

    public enum photosTypes {
        photos, photos_with, photos_new
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


    public static void main(String[] args) throws IOException, JSONException {
        VkontakteAPI api = new VkontakteAPI();
        if (api.login("email", "pass")) {
            List<Photo> photos = api.getPhotos(api.id, 0, 20, photosTypes.photos_new);
            List<User> friends = api.getFriends(api.id, 0, 20, friendsTypes.friends_new);
            for (User user : friends) {
                System.out.println(user);
            }
            for (Photo photo : photos) {
                System.out.println(photo);
            }
        } else {
            System.out.println("login failed!");
        }
    }

    public boolean login(String email, String pass) throws IOException {
        String urlString = "http://login.userapi.com/auth?login=force&site=2&email=" + email + "&pass=" + pass;
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
     * Returns the list of the user's friends
     *
     * @param id   user id
     * @param type - type of friends to return
     * @return the last element in this list
     * @throws java.io.IOException in case of connection problems
     */
    public List<User> getFriends(String id, int from, int to, friendsTypes type) throws IOException, JSONException {
        List<User> friends = new LinkedList<User>();
        URL url = new URL("http://userapi.com/data?act=" + type.name() + "&from=" + from + "&to=" + to + "&id=" + id + "&sid=" + sid);
        String jsonText = getTextFromUrl(url);
        JSONArray fr;
        if (type == friendsTypes.friends) {
            fr = new JSONArray(jsonText);
        } else {
            fr = new JSONObject(jsonText).getJSONArray("d");
        }
        for (int i = 0; i < fr.length(); i++) {
            JSONArray userInfo = (JSONArray) fr.get(i);
            friends.add(new User(userInfo));
        }
        return friends;
    }

    public List<Photo> getPhotos(String id, int from, int to, photosTypes type) throws IOException, JSONException {
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
