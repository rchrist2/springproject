package myproject.models;

public class LoggedUser {

    private static LoggedUser instance;
    private Tblusers loggedInUser;

    public LoggedUser(Tblusers loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public static LoggedUser getInstance(){

        return instance;
    }

    public String getUserName(){
        return loggedInUser.getUsername();
    }

    public void logoutEmployee(){
        loggedInUser = null;
    }
}
