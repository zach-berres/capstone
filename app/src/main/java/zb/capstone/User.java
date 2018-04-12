package zb.capstone;

import java.util.List;

/**
 * Created by Zach Berres on 3/29/18.
 * User class provides structure for maintaining proper data management of individual users
 */

public class User {
    public String               customname; //custom name the user sets, how they are seen by others.
    public String               userid; //custom id the user sets, unique
    public List<User>           friends; //List of this User's friends, ordered from least recently added to most recently added
    public int                  incognito; //essentially a bool: 1 = True, 0 = False. Is the user incognito(unable to be tracked)?
    //Location or set of Lat/Lng doubles

}

