package org.googlecode.userapi;

/**
 * Created by Ildar Karimov
 * Date: Sep 9, 2009
 */
public class Captcha {
    String captcha_sid;
    String url = "http://userapi.com/data?act=captcha&csid=" + captcha_sid;
    //&fcsid={captcha_sid}&fccode={код, введенный пользователем}
}
