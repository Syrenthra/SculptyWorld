package sw.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.net.msg.SWMessage;
import sw.net.msg.SWTokenMsg;
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

}
