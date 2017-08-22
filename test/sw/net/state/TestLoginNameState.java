package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import mock.MockSWServerConnection;

import org.junit.Before;
import org.junit.Test;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;


public class TestLoginNameState
{
   SWServerConnection sc;
    
   // TODO: Need to add a test name / password to the database.
    @Before
    public void before()
    {
        sc = new MockSWServerConnection(null,null,null,null);
        LoginNameState ics = new LoginNameState(sc);
        sc.setServerConnectionState(ics);
    }
    
    @Test
    public void testJustEnteredState()
    {
        assertEquals("Login: ", sc.getServerConnectionState().getMessage().getMessage());
    }
    
    @Test
    public void testNameInDatabase()
    {
        SWMessage msg = new SWMessage("NameInDatabase");
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof LoginPasswordState);
        LoginPasswordState state = (LoginPasswordState)sc.getServerConnectionState();
        assertEquals("NameInDatabase",state.getLoginName());
    }
    
    @Test
    public void testNameNotInDatabase() throws SQLException
    {
        SWMessage msg = new SWMessage("NameNotInDatabase");
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof LoginPasswordState);
        LoginPasswordState state = (LoginPasswordState)sc.getServerConnectionState();
        assertEquals("NameNotInDatabase",state.getLoginName());
    }

}
