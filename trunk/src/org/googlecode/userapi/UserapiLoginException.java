package org.googlecode.userapi;

/**
 * Created by Ildar Karimov
 * Date: Nov 18, 2009
 */
public class UserapiLoginException extends Exception {
    private static final String LOGIN_INCORRECT = "-1";
    private static final String CAPTCHA_INCORRECT = "-2";
    private static final String LOGIN_INCORRECT_CAPTCHA_REQUIRED = "-3";
    private static final String LOGIN_INCORRECT_CAPTCHA_NOT_REQUIRED = "-4";

    private ErrorType type;

    public UserapiLoginException(ErrorType type) {
        this.type = type;
    }

    public static UserapiLoginException fromSid(String sid) {
        if (sid.equals(UserapiLoginException.LOGIN_INCORRECT))
            return new UserapiLoginException(ErrorType.LOGIN_INCORRECT);
        if (sid.equals(UserapiLoginException.CAPTCHA_INCORRECT))
            return new UserapiLoginException(ErrorType.CAPTCHA_INCORRECT);
        if (sid.equals(UserapiLoginException.LOGIN_INCORRECT_CAPTCHA_REQUIRED))
            return new UserapiLoginException(ErrorType.LOGIN_INCORRECT_CAPTCHA_REQUIRED);
        if (sid.equals(UserapiLoginException.LOGIN_INCORRECT_CAPTCHA_NOT_REQUIRED))
            return new UserapiLoginException(ErrorType.LOGIN_INCORRECT_CAPTCHA_NOT_REQUIRED);
        return null;
    }

    public ErrorType getType() {
        return type;
    }

    public static enum ErrorType {
        LOGIN_INCORRECT,
        CAPTCHA_INCORRECT,
        LOGIN_INCORRECT_CAPTCHA_REQUIRED,
        LOGIN_INCORRECT_CAPTCHA_NOT_REQUIRED
    }

}
