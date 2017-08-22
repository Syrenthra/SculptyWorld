package database;

import java.sql.Connection;

/**
 * Base class for all classes that need to access the database.
 * <p>
 * Last Modified: 4-30-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
*/

public class Query
{
    /**
     * The name of the database the WLQuery is accessing.
     *
     */
    protected String m_queryDB;

    /**
     * The actual connection to the database.
     *
     */
    protected Connection m_databaseConnection;

    /**
     * The information needed to form a connection with the database.
     *
     */
    protected DatabaseInfo m_databaseInfo;

    protected DatabaseManager m_manager;

    /**
     * The constructor for Query.
     * 
     * @param name The name of the database to use, this should match the name being used
     *             by the DatabaseManager in its database lookup Hashtable.
     */
    public Query(String name)
    {
        m_queryDB = name;
        m_manager = DatabaseManager.getInstance();

        m_databaseInfo = m_manager.getDatabaseInfo(m_queryDB);
        createConnection();
    }

    /**
     * Closes a connection to the database.  This is not normally called, as keeping
     * the connection open is more efficient.
     *
     */
    public void closeConnection()
    {
        try
        {
            m_databaseConnection.close();
        }
        catch (Exception e)
        {
            m_manager.addToLog(e.getMessage());
        }
    }

    /**
     * Attempts to form a connection to the database.  The database is determined by 
     * m_queryDB.  Normally the connection is already established, however if it isn't
     * it tries to re-establish a connection to the database in question.
     *
     * @return Returns true if successful, false otherwise.
     * @see SWQuery.ship.wl.sql.MKQuery#m_queryDB
     */
    public boolean createConnection()
    {
        int counter = 0;

        while (counter < 4)
        {
            try
            {
                m_databaseConnection = m_databaseInfo.makeConnection();

                return true;
            }
            catch (Exception e)
            {
                m_manager.addToLog(e);
                counter++;
            }
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public String getQueryDB()
    {
        return m_queryDB;
    }

    /**
     * Use to make sure there are no special characters that might cause
     * problems with the SQL query or compromise the database.
     * 
     * TODO: This still needs tweaking.
     * 
     * @param input
     * @return
     */
    public static boolean isValidDBString(String input)
    {
        boolean isValid = true;

        for (int x = 0; x < input.length(); x++)
        {
            char letter = input.charAt(x);

            // Less than 0-9
            if ((letter < 48) && (letter != 32))
            {
                isValid = false;
                break;
            }
            // Between 0-9 and @-Z
            else if ((letter > 57) && (letter < 64))
            {
                isValid = false;
                break;
            }
            // Between a-z and A-Z
            else if ((letter > 90) && (letter < 97))
            {
                isValid = false;
                break;
            }
            // Greater than A-Z
            else if (letter > 122)
            {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    /**
     * Use to make sure there are no special characters that might cause
     * problems with the SQL query or compromise the database.
     * 
     * TODO: This still needs tweaking.
     * 
     * @param input
     * @return
     */
    public static boolean isValidNameString(String input)
    {
        boolean isValid = true;

        for (int x = 0; x < input.length(); x++)
        {
            char letter = input.charAt(x);

            // Less Than A-Z
            if (letter < 65)
            {
                isValid = false;
                break;
            }
            // Between a-z and A-Z
            else if ((letter > 90) && (letter < 97))
            {
                isValid = false;
                break;
            }
            // Greater than A-Z
            else if (letter > 122)
            {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    /**
     * Use to make sure there are no special characters that might cause
     * problems with the SQL query or compromise the database.
     * 
     * TODO: This still needs tweaking.
     * 
     * @param input
     * @return
     */
    public static boolean isValidEmailString(String input)
    {
        boolean isValid = true;

        for (int x = 0; x < input.length(); x++)
        {
            char letter = input.charAt(x);

            // Less than 0-9
            if ((letter < 48) && (letter != 46))
            {
                isValid = false;
                break;
            }
            // Between 0-9 and A-Z
            else if ((letter > 57) && (letter < 64))
            {
                isValid = false;
                break;
            }
            // Between a-z and A-Z
            else if ((letter > 90) && (letter < 97) && (letter != 95))
            {
                isValid = false;
                break;
            }
            // Greater than A-Z
            else if (letter > 122)
            {
                isValid = false;
                break;
            }
        }

        return isValid;
    }
}
