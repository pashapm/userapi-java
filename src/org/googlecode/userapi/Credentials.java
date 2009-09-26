package org.googlecode.userapi;

public class Credentials {
    private String login;
    private String pass;
    private String remixpass;
    private String session;

    public Credentials(String login, String pass, String remixpass, String session) {
        //if (login==null || pass==null) throw new IllegalArgumentException("login/pass must not be null");
    	//why? only Sid?
        //sid login might possibly fail after some time and we'll need to re-ask for pass
    	
        this.login = login;
        this.pass = pass;
        this.remixpass = remixpass;
        this.session = session;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRemixpass() {
        return remixpass;
    }

    public void setRemixpass(String remixpass) {
        this.remixpass = remixpass;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
