package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Create a class to organize the information need to form a connection to a
 * database. Have the class able to use both the DriverManager method of making
 * a connection and the newer DataSource method.
 * 
 * @author cdgira
 * 
 */
public class DatabaseInfo
    {
    /**
     * Used to store the information to connect to a database.
     */
    protected String m_databaseName = null;

    protected String m_loginName = null;

    protected String m_password = null;
    
    /**
     * Alternative way to store the connect information to the database.
     */
    protected DataSource m_dataSource = null;

    /**
     * The actual connection to the database.
     */
    protected Connection m_dbConn = null;

    
    /**
     * Used to create a DatabaseInfo instance that connects using
     * the name, login, and password information stored as Strings.
     * @param name
     * @param login
     * @param pass
     */
    public DatabaseInfo(String name, String login, String pass)
        {
        m_databaseName = name;
        m_loginName = login;
        m_password = pass;
        }

    /**
     * Used to create a DatabaseInfo instance for connecting to 
     * a database that uses a DataSource to store all the information.
     * @param dataSource
     */
    public DatabaseInfo(DataSource dataSource)
        {
        m_dataSource = dataSource;
        }

    /**
     * Returns true if connected to the database, false
     * otherwise.
     * @return
     */
    public boolean isConnected()
        {
        boolean connected = false;

        if (m_dbConn != null)
            {
            connected = true;
            }
        return connected;
        }

    /**
     * If there is no connection to the database it will create one.
     * If there is a connection already established it will return
     * that connection.
     */
    public Connection makeConnection() throws Exception
        {
        if (this.isConnected())
            {
            if (m_dbConn.isClosed())
                {
                if (m_dataSource != null)
                    {
                    m_dbConn = m_dataSource.getConnection();
                    }
                else if (m_loginName != null)
                    {
                    m_dbConn = DriverManager.getConnection(m_databaseName, m_loginName, m_password);
                    }
                else
                    {
                    m_dbConn = DriverManager.getConnection(m_databaseName);
                    }
                }
            }
        else
            {
            if (m_dataSource != null)
                {
                m_dbConn = m_dataSource.getConnection();
                }
            else if (m_loginName != null)
                {
                m_dbConn = DriverManager.getConnection(m_databaseName, m_loginName, m_password);
                }
            else
                {
                m_dbConn = DriverManager.getConnection(m_databaseName);
                }
            }
        return m_dbConn;
        }
    
    /**
     * Closes the connection to the database.
     * @throws SQLException
     */
    public void closeConnection() throws SQLException
        {
        if (this.isConnected())
            {
            m_dbConn.close();
            }
        }

    }
