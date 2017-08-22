package sw.database.req;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sw.database.SWQuery;
import sw.database.UserTable;

/**
 * Tries to log the user into ScupltyWorld.  If the user name and password
 * are correct then it returns success. 
 * 
 * TODO: How to keep a user from logging in more than once.
 * TODO: We could set which token is valid for the user, but then every action by the user would require an extra db query to confirm the token.
 * TODO: If we did use the SWToken in this fashion we could update SWQuery to contain a check, every message would need the user's login name though.
 * @author Dr. Girard
 *
 */
public class SWLoginQuery extends SWQuery
{
    int m_userID = -1;
    String m_userName;
    String m_password;

    public SWLoginQuery(String userName, String userPassword)
    {
        m_userName = userName;
        m_password = userPassword;
    }

    /**
     * Tries to select a row from the UserTable in the database that has the user name
     * and password provided.  If it doesn't then the login attempt fails.
     */
    public int executeQuery()
    {
        try
        {
            synchronized (m_databaseInfo)
            {   
                // First check to see if already in the table.
                String selectStr = new String("SELECT * FROM "+UserTable.NAME+" WHERE "+UserTable.USER_NAME+" = '"+m_userName+"' AND "+UserTable.USER_PASSWORD+" = '"+m_password+"'");
                Statement stmt = m_databaseConnection.createStatement();
                ResultSet data = stmt.executeQuery(selectStr);
                if (data.next()) 
                {
                    m_userID = data.getInt(UserTable.USER_ID);
                    return SUCCESS;
                }
                else
                {
                    return FAILED;
                } 
            }
        }
        catch (SQLException e)
        {
            m_manager.addToLog(e);
            closeConnection();
        }
        return FAILED;
    }

    public int getUserID()
    {
        return m_userID;
    }

}
