package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Used by the JUnit tests to check for values in the database.
 * @author cdgira
 *
 */
public class CheckDatabaseValue
{
    protected Connection m_dbConn = null;

    public CheckDatabaseValue()
    {
        try
        {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
        }

        try
        {
            makeConnection();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * public Connection getDatabaseConnection() { return m_dbConn; }
     * @throws SQLException 
     */
    public void makeConnection() throws SQLException
    {
        if (m_dbConn == null)
        {
            m_dbConn = DriverManager.getConnection("jdbc:mysql://localhost/SculptyWorldDB", "sculpty", "world12Big");
        }

    }

    /**
     * 
     * @param dbQuery
     * @return
     */
    public ResultSet getDataValue(String dbQuery)
    {
        Statement stmt;
        try
        {
            stmt = m_dbConn.createStatement();

            ResultSet data = stmt.executeQuery(dbQuery);

            return data;
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

}
