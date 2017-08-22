package sw.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.DatabaseManager;

import sw.database.SWQuery;
import sw.net.msg.SWMessage;


public class TestSWServer
{
    SWServer sw;
    
    @Before
    public void before()
    {
        sw = new SWServer(new SWServerSocket(null), 2000);
    }
    
    @After
    public void after()
    {
        sw.shutdown();
    }
    
    @Test
    public void testIntialize()
    {  
        // Need to check that the database manager is connected to the right database.
        assertNotNull(DatabaseManager.getInstance().getDBAddress(SWQuery.DATABASE));
        assertEquals("SWServer",sw.getName());
    }
    
    @Test
    public void testConnectingToServer() throws IOException, ClassNotFoundException
    {
        SWClientSocket client = new SWClientSocket(null);
        try
        {
            client.connect("127.0.0.1", 2000);
            client.readObject();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue(false);
        }
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(1,sw.getConnections().size());
    }


}
