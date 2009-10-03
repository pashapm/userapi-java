package org.googlecode.userapi;

import java.util.Map;

/**
 * Created by Ildar Karimov
 * Date: Oct 3, 2009
 */
public class UrlBuilder {
    public static final String urlBase = "http://userapi.com/";

    public static String makeUrl(String action) {
        String url = urlBase + "data?act=" + action;
        return url;
    }

    public static String makeUrl(String action, long id) {
        String url = urlBase + "data?act=" + action + "&id=" + id;
        return url;
    }

    public static String makeUrl(String action, long id, int from, int to) {
        String url = urlBase + "data?act=" + action + "&from=" + from + "&to=" + to + "&id=" + id;
        return url;
    }

    public static String makeUrl(String action, Map<String, String> params) {
        StringBuilder url = new StringBuilder(urlBase + "data?act=" + action);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append("&" + entry.getKey() + "=" + entry.getValue());
        }
        return url.toString();
    }
}
