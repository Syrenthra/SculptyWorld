package net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import mock.MockServer;

import org.junit.After;
import org.junit.Test;


public class TestSecureServer
{
    /**
     * Remove any log files we created while testing.
     */
    @After
    public void after()
    {
        File f = new File("Test.log");
        if (f.exists())
        {
            f.delete();
        }
    }
    
    @Test
    public void testBasics()
    {
        MockServer server = new MockServer("Test","Group",2000);
        assertNotNull(server.m_socket);
        assertEquals(2000,server.m_socket.getLocalPort());
        assertEquals(0,server.m_connections.size());
        assertNotNull(server.m_log);
        server.shutdown();
        assertTrue(server.m_socket.isClosed());
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


