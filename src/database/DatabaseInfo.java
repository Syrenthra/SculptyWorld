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
    protected String m_databaseName = null;

    protected String m_loginName = null;

    protected String m_password = null;

    protected DataSource m_dataSource = null;

    protected Connection m_dbConn = null;

    public DatabaseInfo(String name, String login, String pass)
        {
        m_databaseName = name;
        m_loginName = login;
        m_password = pass;
        }

    public DatabaseInfo(DataSource dataSource)
        {
        m_dataSource = dataSource;
        }

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
     * public Connection getDatabaseConnection() { return m_dbConn; }
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
    
    public void closeConnection() throws SQLException
        {
        if (this.isConnected())
            {
            m_dbConn.close();
            }
        }

    }
