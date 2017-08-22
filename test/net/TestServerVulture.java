package net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class TestServerVulture
{
    
    @Test
    public void testInitialization()
    {
        MockServer server = new MockServer("Test", "Group", 2000);
        assertNotNull(server.m_vulture);
        server.shutdown();
        assertTrue(server.m_socket.isClosed());
    }
    
    @Test
    public void testCleansUpDeadThreads() throws InterruptedException
    {
        System.out.println("This test takes about 30 seconds.");
        MockServer server = new MockServer("Test", "Group", 2000);
        assertNotNull(server.m_vulture);
        MockServerConnectionForVulture connection = new MockServerConnectionForVulture(null,"Test",server.m_vulture,server);
        server.m_connections.addElement(connection);
        assertTrue(connection.isAlive());
        
        // Vulture should not remove alive thread.
        Thread.sleep(15000);
        assertEquals(1,server.m_connections.size());
        
     // Cause thread to end.
        connection.flag = false; 
        Thread.sleep(200);
        assertTrue(!connection.isAlive());
        
        // Vulture should clean up dead thread
        Thread.sleep(11000);
        assertEquals(0,server.m_connections.size());
        
        server.shutdown();
        
    }

}

class MockServerConnectionForVulture extends SecureServerConnection
{
    public boolean flag = true;

    public MockServerConnectionForVulture(ThreadGroup threadgroup, String threadname, ServerVulture vulture, SecureServer app)
    {
        super(threadgroup, threadname, vulture, app);
        this.start();
    }

    @Override
    public void run()
    {
        while(flag)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

    @Override
    public void closeClient() throws IOException
    {
        // TODO Auto-generated method stub
        
    }
    
}
