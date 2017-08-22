package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import database.CheckDatabaseValue;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;


public class TestRegisterPasswordState
{
    
    SWServerConnection sc;
    
    @Before
    public void before()
    {
        sc = new MockSWServerConnection(null,null,null,null);
        RegisterPasswordState ics = new RegisterPasswordState(sc);
        sc.setServerConnectionState(ics);
    }
    
    @Test
    public void testJustEnteredState()
    {
        assertEquals("Please enter a password:", sc.getServerConnectionState().getMessage().getMessage());
    }
    
    @Test
    public void testValidPassword()
    {
        SWMessage msg = new SWMessage("Invalid@Password");
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof RegisterPasswordState);
        assertEquals("<Invalid Password>Please enter a password:",sc.getServerConnectionState().getMessage().getMessage());
    }
    
    @Test
    public void testInValidPassword() throws SQLException
    {
        SWMessage msg = new SWMessage("ValidPassword");
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
        CheckDatabaseValue cdv = new CheckDatabaseValue();
        ResultSet rs = cdv.getDataValue("SELECT * FROM USERS_TABLE WHERE USER_NAME = 'NameNotInDatabase'");
        assertTrue(rs.next());
        assertTrue("Need to test that correct values saved",false);
    }

}
