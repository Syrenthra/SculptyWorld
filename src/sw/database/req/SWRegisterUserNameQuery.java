package sw.database.req;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sw.database.SWQuery;
import sw.database.UserTable;
import sw.database.obj.SWToken;

/**
 * This attempts to register a new user name to the SculptyWorld database.
 * @author cdgira
 *
 */
public class SWRegisterUserNameQuery extends SWQuery
{
    /**
     * The name the user wants to login with.
     */
    String m_name;
    /**
     * Used as a temporary passoword.
     */
    SWToken m_token;

    public SWRegisterUserNameQuery(SWToken token, String name)
    {
        m_token = token;        
        m_name = name;
    }

    /**
     * Will attempt to add this new user to the database.  If it succeeds it
     * returns true.
     */
    public int executeQuery()
    {
        try
        {
            synchronized (m_databaseInfo)
            {   
                // First check to see if already in the table.
                String selectStr = new String("SELECT * FROM "+UserTable.NAME+" WHERE "+UserTable.USER_NAME+" = '"+m_name+"'");
                Statement stmt = m_databaseConnection.createStatement();
                ResultSet data = stmt.executeQuery(selectStr);
                if (data.next()) 
                {
                 // That person is already in the Database.
                    return FAILED;
                }
                else
                {
                    // We can add the new user to the database.
                    int userNum = getNextIDNumber(UserTable.NAME);
                    if (userNum == -1)
                        return FAILED;
                    
                    StringBuffer insertStm = new StringBuffer("INSERT INTO "+UserTable.NAME+" ("+UserTable.USER_ID+" , "+UserTable.USER_NAME+" , "+UserTable.USER_PASSWORD);
                    insertStm.append(") VALUES ( ?, ?, ?)");
                    PreparedStatement pStmt = m_databaseConnection.prepareStatement(insertStm.toString());
                    pStmt.setInt(1, userNum);
                    pStmt.setString(2, m_name);
                    pStmt.setString(3,   m_token.toString()); // Use the token as a temp password.

                    pStmt.execute();
                    if (pStmt.getUpdateCount() > 0)
                        return SUCCESS;
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
