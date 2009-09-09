package org.googlecode.userapi;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Ildar Karimov
 * Date: Sep 9, 2009
 */
public class CaptchaHandlerImpl implements CaptchaHandler {
    public String handleCaptcha(final String url) {
        System.out.println("please enter captcha: " + url);
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(reader);
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
