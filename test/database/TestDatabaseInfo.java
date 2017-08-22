package database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class TestDatabaseInfo
{

    @Before
    public void before()
    {
        try
        {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        }
        catch (SQLException sqle)
        {
            System.out.println(sqle);
        }
    }

    @Test
    public void testBasics()
    {
        DatabaseInfo di = new DatabaseInfo("DB", "id", "pass");
        assertEquals("DB", di.m_databaseName);
        assertEquals("id", di.m_loginName);
        assertEquals("pass", di.m_password);
        assertFalse(di.isConnected());
        try
        {
            di.makeConnection();
            assertFalse(true);
        }
        catch (Exception e)
        {
            // This should fail.
        }
    }

    @Test
    public void testForSWDatabase()
    {
        DatabaseInfo di = new DatabaseInfo("jdbc:mysql://localhost/SculptyWorldDB", "sculpty", "world12Big");
        try
        {
            di.makeConnection();
        }
        catch (Exception e)
        {
            assertFalse(true);
        }

        assertTrue(di.isConnected());
    }

}
