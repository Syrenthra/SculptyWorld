package database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import sw.database.SWQuery;

public class TestDatabaseInfo
{

    @Before
    public void before()
    {
        DatabaseManager.getInstance().activateJDBC();
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
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        try
        {
            di.makeConnection();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertFalse(true);
        }

        assertTrue(di.isConnected());
    }

}
