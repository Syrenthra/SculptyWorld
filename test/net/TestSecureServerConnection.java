package net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import mock.MockServer;
import mock.MockServerConnection;

import org.junit.After;
import org.junit.Test;


public class TestSecureServerConnection
{
    /**
     * Remove any log files we created while testing.
     */
    @After
    public void after()
    {
        File f = new File("Log_Test.log");
        if (f.exists())
        {
            f.delete();
        }
    }
    
    @Test
    public void testBasics()
    {
        MockServer server = new MockServer("Test","Group",2000);
        MockServerConnection connection = new MockServerConnection(null,"Test",server.m_vulture,server);
        assertEquals(server,connection.getTheServer());
        assertEquals(server.m_vulture,connection.m_vulture);
        server.shutdown();
    }
    
    @Test
    public void testLogWorks()
    {
        MockServer server = new MockServer("Test","Group",2000);
        server.addToLog("Test Message");
        server.m_log.flushLog();
        File f = new File("Log_Test.log");
        assertTrue(f.exists());
        server.shutdown();
    }

}


