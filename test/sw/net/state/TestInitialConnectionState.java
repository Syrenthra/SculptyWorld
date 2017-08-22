package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import mock.MockSWServerConnection;

import org.junit.Test;

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

}