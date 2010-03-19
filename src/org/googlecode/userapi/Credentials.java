package org.googlecode.userapi;

import java.io.Serializable;

public class Credentials implements Serializable {

	private static final long serialVersionUID = 9058889072946466921L;

    private String login;
    private String pass;
    private String remixpass;

    public Credentials(String login, String pass, String remixpass) {
        if (login == null || pass == null) throw new IllegalArgumentException("login/pass must not be null");
        this.login = login;
        this.pass = pass;
        this.remixpass = remixpass;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public String getRemixpass() {
        return remixpass;
    }

    public void setRemixpass(String remixpass) {
        this.remixpass = remixpass;
    }
}
