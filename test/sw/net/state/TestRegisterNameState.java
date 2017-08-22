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


public class TestRegisterNameState
{
    SWServerConnection sc;
    
    @Before
    public void before()
    {
        sc = new MockSWServerConnection(null,null,null,null);
        RegisterNameState ics = new RegisterNameState(sc);
        sc.setServerConnectionState(ics);
    }
    
    @Test
    public void testJustEnteredState()
    {
        assertEquals("Please enter a user name:", sc.getServerConnectionState().getMessage().getMessage());
    }
    
    @Test
    public void testNameInDatabase()
    {
        SWMessage msg = new SWMessage("NameInDatabase");
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof RegisterNameState);
    }
    
    @Test
    public void testNameNotInDatabase() throws SQLException
    {
        SWMessage msg = new SWMessage("NameNotInDatabase");
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof RegisterPasswordState);
        CheckDatabaseValue cdv = new CheckDatabaseValue();
        ResultSet rs = cdv.getDataValue("SELECT * FROM USERS_TABLE WHERE USER_NAME = 'NameNotInDatabase'");
        assertTrue(rs.next());
    }

}
