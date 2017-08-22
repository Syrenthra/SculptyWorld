package database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.LogThread;


public class TestDatabaseManager
{

    @Test
    public void testBasics()
    {
        DatabaseManager dm = DatabaseManager.getInstance();
        assertEquals("Log_DatabaseManager",dm.m_log.getName());
    }
    
    @Test
    public void testActivateJDBCDriver()
    {
        DatabaseManager dm = DatabaseManager.getInstance();
        assertTrue(dm.activateJDBC());
    }
}
