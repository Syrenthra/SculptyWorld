package database;

import net.LogThread;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * To start and stop the database:
 *  sudo /usr/local/mysql/support-files/mysql.server stop
 *   sudo /usr/local/mysql/support-files/mysql.server start
 * @author cdgira
 *
 */
public class DatabaseManager
{
    private static DatabaseManager dbOverlord = null;

    /**
     * Is a lookup table of all the databases. Each database is assigned a name
     * which acts as its key value in the Hashtable.
     * 
     */
    static protected Hashtable<String, DatabaseInfo> m_dbConnections = new Hashtable<String, DatabaseInfo>();

    protected LogThread m_log;

    private DatabaseManager()
    {
        activateJDBC();

        m_log = new LogThread("Log_DatabaseManager");
        m_log.start();
    }

    public static DatabaseManager getInstance()
    {
        if (dbOverlord == null)
            dbOverlord = new DatabaseManager();

        return dbOverlord;
    }

    /**
     * This is the recommended way to activate the JDBC drivers, but is
     * only setup to work with one specific driver.  Setup to work with
     * a MySQL JDBC driver.
     * 
     * @return Returns true if it successfully sets up the driver.
     */
    protected boolean activateJDBC()
    {
        try
        {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        }
        catch (SQLException sqle)
        {
            m_log.addMessageToLog(sqle);
        }

        return true;

    }

    /**
     * Adds a new database connection to the manager.  Only adds the info to the manager if
     * a connection is created successfully.
     * @param key The ID to lookup the connection to the database.
     * @param db The info for the database to create a connection to.
     * @return Returns true if a connection is made.
     */
    public boolean addDB(String key, DatabaseInfo db)
    {
        try
        {
            db.makeConnection();
            m_dbConnections.put(key, db);
            return true;
        }
        catch (Exception e)
        {
            m_log.addMessageToLog(e);
        }
        return false;
    }

    /**
     * Gets the address information for a given database from m_dbConnections.
     * 
     * @param name
     *            The name of the database you want connection information for.
     * @return The Hashtable containing the needed information, or null if no
     *         information for that database is found.
     */
    public DatabaseInfo getDBAddress(String name)
    {
        DatabaseInfo db = null;

        if (m_dbConnections.containsKey(name))
        {
            db = m_dbConnections.get(name);
        }

        return db;
    }

    public void updateDatabaseList(Hashtable<String, DatabaseInfo> databases)
    {
        Enumeration<String> enum1 = databases.keys();
        while (enum1.hasMoreElements())
        {
            String str = (String) enum1.nextElement();

            if (!m_dbConnections.containsKey(str))
            {
                DatabaseInfo con = databases.get(str);

                m_dbConnections.put(str, con);
            }
        }
    }

    public void shutdown() throws SQLException
    {
        Enumeration<DatabaseInfo> enum1 = m_dbConnections.elements();
        while (enum1.hasMoreElements())
        {
            DatabaseInfo dbInfo = enum1.nextElement();
            dbInfo.closeConnection();
        }

    }

}
