package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.SecureObjectSocketInterface;
import net.SecureServer;
import net.ServerVulture;

import org.junit.Test;

import sw.net.SWServer;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class TestInitialConnectionState
{

    @Test
    public void testJustConnected()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InitialConnectionState ics = new InitialConnectionState(sc);

        assertEquals("Login or Register", ics.getMessage().getMessage());
    }
    
    @Test
    public void testGotRegisterMessage()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InitialConnectionState ics = new InitialConnectionState(sc);
        SWMessage msg = new SWMessage("Register");
        ics.executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof RegisterNameState);
    }
    
    @Test
    public void testGotLoginMessage()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InitialConnectionState ics = new InitialConnectionState(sc);
        SWMessage msg = new SWMessage("Login");
        ics.executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof LoginNameState);
    }
    
    @Test
    public void testOtherMessages()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InitialConnectionState ics = new InitialConnectionState(sc);
        SWMessage msg = new SWMessage("Blah");
        ics.executeAction(msg);
        assertEquals("Login or Register", ics.getMessage().getMessage());
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
    }
    
    @Test
    public void testGotDemoMessage()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InitialConnectionState ics = new InitialConnectionState(sc);
        SWMessage msg = new SWMessage("Demo");
        ics.executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof InWorldState);
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
