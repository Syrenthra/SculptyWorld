package sw.database.req;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sw.database.SWQuery;
import sw.database.UserTable;
import sw.database.obj.SWToken;

/**
 * Attempts to complete registration of an account for a new user.
 * 
 * TODO: How to track users that only get halfway through the registration process and loose connection.
 * @author cdgira
 *
 */
public class SWRegisterPasswordQuery extends SWQuery
{
    /**
     * The login name the user used for the account.
     */
    String m_userName;
    /**
     * The actual password the user wants to use for the account.
     */
    String m_password;
    /**
     * Need this as it was the temporary password that was created for this account.
     */
    SWToken m_token;

    public SWRegisterPasswordQuery(SWToken token, String userName, String pwd)
    {
        m_token = token;
        m_userName = userName;
        m_password = pwd;
    }

    public int executeQuery()
    {
        try
        {
            synchronized (m_databaseInfo)
            {   
                // First check to see if already in the table.
                String selectStr = new String("SELECT * FROM "+UserTable.NAME+" WHERE "+UserTable.USER_NAME+" = '"+m_userName+"'");
                Statement stmt = m_databaseConnection.createStatement();
                ResultSet data = stmt.executeQuery(selectStr);
                if (data.next()) 
                {
                    // We can set the password of the new user.
                    String token = data.getString(UserTable.USER_PASSWORD);
                    if (token.equals(m_token.toString()))
                    {
                        StringBuffer insertStm = new StringBuffer("UPDATE "+UserTable.NAME+" SET "+UserTable.USER_PASSWORD+" = ? WHERE "+UserTable.USER_NAME+" = ?");
                        PreparedStatement pStmt = m_databaseConnection.prepareStatement(insertStm.toString());
                        pStmt.setString(1, m_password);
                        pStmt.setString(2, m_userName);

                        pStmt.execute();
                        if (pStmt.getUpdateCount() > 0)
                            return SUCCESS;
                        return FAILED;
                    }
                    else
                    {
                        return INVALID_TOKEN;
                    }
                    
                    
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

}
