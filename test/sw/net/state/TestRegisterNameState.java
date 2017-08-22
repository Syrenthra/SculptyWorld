package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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


public class TestRegisterNameState
{
    SWServerConnection sc;
    
    DatabaseManager dm;

    
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
        RegisterNameState ics = new RegisterNameState(sc);
        sc.setServerConnectionState(ics);
        
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
        
        // Put test name in the database.
        SWToken token = SWToken.constructToken();
        SWRegisterUserNameQuery query = new SWRegisterUserNameQuery(token,"NameInDatabase");
        query.executeQuery();
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
        SWToken token = SWToken.constructToken();
        msg.setSecurityToken(token);
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof RegisterNameState);
        assertEquals("That user name has been taken, please choose another: ",sc.getServerConnectionState().getMessage().getMessage());
    }
    
    @Test
    public void testNameNotInDatabase() throws SQLException
    {
        SWMessage msg = new SWMessage("NameNotInDatabase");
        SWToken token = SWToken.constructToken();
        msg.setSecurityToken(token);
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof RegisterPasswordState);
        RegisterPasswordState rps = (RegisterPasswordState)sc.getServerConnectionState();
        assertEquals("NameNotInDatabase",rps.getUserName());
        CheckDatabaseValue cdv = new CheckDatabaseValue();
        ResultSet rs = cdv.getDataValue("SELECT * FROM "+UserTable.NAME+" WHERE "+UserTable.USER_NAME+" = 'NameNotInDatabase'");
        assertTrue(rs.next());
    }

}
