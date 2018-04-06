package zb.capstone;

import java.util.ArrayList;

/**
 * Created by Zach Berres on 3/29/18.
 * User class provides structure for maintaining proper data management of individual users
 */

public class User {
    public String               name; //custom name the user sets
    public String               id; //The name derived from the Google AccountManager API, or possibily email address
    public ArrayList<User>      friend; //List of this User's friends, ordered from least recently added to most recently added
    public int                  incognito; //essentially a bool: 1 = True, 0 = False. Is the user incognito(unable to be tracked)?


}
