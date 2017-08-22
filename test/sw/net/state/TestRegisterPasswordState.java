package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import mock.MockSWServerConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.CheckDatabaseValue;
import database.DatabaseInfo;
import database.DatabaseManager;

import sw.database.ConstructTestDatabase;
import sw.database.SWQuery;
import sw.database.UserTable;
import sw.database.obj.SWToken;
import sw.database.req.SWRegisterUserNameQuery;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;


public class TestRegisterPasswordState
{
    
    SWServerConnection sc;
    
    DatabaseManager dm;
    
    SWToken token;
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Before
    public void before() throws Exception
    {
        sc = new MockSWServerConnection(null,null,null,null);
        RegisterPasswordState ics = new RegisterPasswordState(sc,"NameInDatabase");
        sc.setServerConnectionState(ics);
        
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
        
        // Put test name in the database.
        token = SWToken.constructToken();
        SWRegisterUserNameQuery query = new SWRegisterUserNameQuery(token,"NameInDatabase");
        query.executeQuery();
    }
    
    @Test
    public void testJustEnteredState()
    {
        assertEquals("Please enter a valid password:", sc.getServerConnectionState().getMessage().getMessage());
    }
    // TODO Need to rethink how success and failure are displayed to the user.
    @Test
    public void testValidPasswordWithInvalidToken()
    {
        SWMessage msg = new SWMessage("ValidPassword");
        msg.setSecurityToken(SWToken.constructToken());
        sc.getServerConnectionState().executeAction(msg);
        fail("No decision on what should happen in this case.");
    }
    
    @Test
    public void testInValidPassword()
    {
        SWMessage msg = new SWMessage("Invalid@Password");
        msg.setSecurityToken(token);
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof RegisterPasswordState);
        assertEquals("Password contains illegal characters, please enter a valid password:",sc.getServerConnectionState().getMessage().getMessage());
    }
    
    @Test
    public void testValidPassword() throws SQLException
    {
        SWMessage msg = new SWMessage("ValidPassword");
        msg.setSecurityToken(token);
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof InitialConnectionState);
        CheckDatabaseValue cdv = new CheckDatabaseValue();
        ResultSet rs = cdv.getDataValue("SELECT * FROM "+UserTable.NAME+" WHERE "+UserTable.USER_NAME+" = 'NameInDatabase'");
        assertTrue(rs.next());
        String dbPass = rs.getString(UserTable.USER_PASSWORD);
        assertEquals("ValidPassword",dbPass);
    }

}
