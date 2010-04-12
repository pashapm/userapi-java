package org.googlecode.userapi;

/**
 * Created by Ildar Karimov
 * Date: Sep 9, 2009
 */
public class Captcha {
	
    public static String wrapCaptcha(String url) {
    	if (Captcha.captcha_sid != null && Captcha.captcha_decoded != null) {
    		url += "&fcsid=" + captcha_sid + "&fccode=" + captcha_decoded;
    		Captcha.captcha_sid = null;
    		Captcha.captcha_decoded = null;
    	}
    	return url;
    }
	
    static String captcha_sid;
    static String captcha_decoded;
}
