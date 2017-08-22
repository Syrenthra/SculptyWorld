package sw.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.SecureObjectSocketInterface;
import net.SecureServer;
import net.ServerVulture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.DemoTestWorld;
import sw.lifeform.PC;
import sw.net.msg.SWMessage;
import sw.net.state.InWorldState;
import sw.net.state.InitialConnectionState;

public class TestSWServerConnection
{
    SWServer server;

    @Before
    public void before()
    {
        server = new SWServer(new SWServerSocket(null), 2000);
    }

    @After
    public void after()
    {
        server.shutdown();
    }
    
    @Test
    public void testAbleToProcessMultipleMessages()
    {
        SWServerConnection sc;
        sc = new MockSWServerConnection(null,null,null,null);
        SWMessage msg1 = new SWMessage("Test");
        SWMessage msg2 = new SWMessage("Test");
        SWMessage msg3 = new SWMessage("Test");
        
        sc.sendMessage(msg1);
        sc.sendMessage(msg2);
        sc.sendMessage(msg3);
        
        assertEquals(msg1,sc.removeNextMessage());
        assertEquals(msg2,sc.removeNextMessage());
        assertEquals(msg3,sc.removeNextMessage());
    }

    @Test
    public void testSendsInitialMessageAndSetToCorrectState() throws IOException, ClassNotFoundException
    {
        SWClientSocket client = new SWClientSocket(null);
        try
        {
            client.connect("127.0.0.1", 2000);
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
        SWServerConnection sc = (SWServerConnection) server.getConnections().get(0);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
        SWMessage msg = client.readObject();
        assertNull(msg);
        assertNotNull(client.m_token);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
    }

    @Test
    public void testWillProcessMultipleMessages() throws IOException, ClassNotFoundException, InterruptedException
    {
        SWClientSocket client = new SWClientSocket(null);

        client.connect("127.0.0.1", 2000);
        client.readObject();  // Have the client process the Token message.

        Thread.sleep(1000);

        SWMessage msg = new SWMessage("Msg1");
        client.writeObject(msg);

        Thread.sleep(500);

        msg = client.readObject();
        msg = new SWMessage("Msg2");
        client.writeObject(msg);

        Thread.sleep(500);

        SWServerConnection sc = (SWServerConnection) server.getConnections().get(0);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
        msg = client.readObject();
        assertEquals("Login or Register", msg.getMessage());
        assertTrue(msg instanceof SWMessage);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
    }
    

    @Test
    public void testServerSocketSendingAMessage() throws IOException, ClassNotFoundException
    {
        SWClientSocket client = new SWClientSocket(null);
        try
        {
            client.connect("127.0.0.1", 2000);
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
        SWServerConnection sc = (SWServerConnection) server.getConnections().get(0);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
        SWMessage msg = client.readObject(); // Have the client process the Token message.
        msg = new SWMessage("Hello");
        sc.sendMessage(msg);
        
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SWMessage msg2 = client.readObject();
        assertEquals("Hello",msg2.getMessage());
        
    }
    
    @Test
    public void testUnderHighMessageLoadBothDirections() throws IOException, ClassNotFoundException
    {
        SWClientSocket client = new SWClientSocket(null);
        try
        {
            client.connect("127.0.0.1", 2000);
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
        SWServerConnection sc = (SWServerConnection) server.getConnections().get(0);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
        SWMessage msg = client.readObject(); // Have the client process the Token Message.

        for (int x=0;x<1000;x++)
        {
            msg = new SWMessage("Hello"+x);
            sc.sendMessage(msg);
            client.writeObject(msg);
        }
    }
    
    @Test
    public void testTimedInformationIsTransmittedAutomticallyToThePlayer() throws IOException, InterruptedException, ClassNotFoundException
    {
        
        
        SWClientSocket client = new SWClientSocket(null);

        client.connect("127.0.0.1", 2000);
        client.readObject();  // Have the client process the Token message.

        Thread.sleep(500);
        
        SWServerConnection sc = (SWServerConnection) server.getConnections().get(0);
        
        DemoTestWorld.constructDemoWorld();
        PC player = DemoTestWorld.getPlayer1();
        
        InWorldState iws = new InWorldState(sc, 0, player);
        sc.setServerConnectionState(iws);
        
        SWMessage msg = new SWMessage("west");
        iws.executeAction(msg);
        
        iws.executeAction(msg);
        iws.executeAction(msg);
        
        Thread.sleep(10000); // Wait for creatures to appear
        
        msg = client.readObject();
        assertEquals("Forest Creature",msg.getMessage());
    }

}

class MockSWServerConnection extends SWServerConnection
{
    public MockSWServerConnection(SecureObjectSocketInterface<SWMessage> wlos, ThreadGroup threadgroup, ServerVulture vulture, SWServer app)
    {
        super(wlos, threadgroup, vulture, app);
    }

    @Override
    public void closeClient() throws IOException
    {
    }

    @Override
    public SecureObjectSocketInterface<SWMessage> getClient()
    {
        return m_client;
    }

    /**
     * Listens on the incoming stream for any messages. If a message is received
     * it checks to see if it is valid then runs getGeneralServerResponse. If
     * there is a message to send back it sends it back via the out stream. The
     * streams are then closed and the thread stops.
     * <p>
     * 
     * @see girard.ship.wl.io.msg.SWMessage
     */
    public void run()
    {

    }

    @Override
    public void addToLog(String str)
    {

    }

    @Override
    public void addToLog(Exception e)
    {
        
    }

    protected SecureServer getTheServer()
    {
        return m_theServer;
    }

    @Override
    public void notifyVulture()
    {

    }

    @Override
    public String toString()
    {
        return this.getName();
    }
}
