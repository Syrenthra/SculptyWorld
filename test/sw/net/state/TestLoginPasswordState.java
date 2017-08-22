package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import mock.MockSWServerConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.database.ConstructTestDatabase;
import sw.database.SWQuery;
import sw.database.obj.SWToken;
import sw.database.req.SWRegisterPasswordQuery;
import sw.database.req.SWRegisterUserNameQuery;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;
import database.DatabaseInfo;
import database.DatabaseManager;


public class TestLoginPasswordState
{
    SWServerConnection sc;
    
    DatabaseManager dm;
    
    SWToken myToken;
    String userName = "NameInDatabase";
    String userPassword = "MyPassword";
    
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
        LoginPasswordState ics = new LoginPasswordState(sc,userName);
        sc.setServerConnectionState(ics);
        
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo("jdbc:mysql://localhost/SculptyWorldDB", "sculpty", "world12Big");
        dm.addDB(SWQuery.DATABASE, di);
        
        // Put test name in the database.
        myToken = SWToken.constructToken();
        SWRegisterUserNameQuery query = new SWRegisterUserNameQuery(myToken,"NameInDatabase");
        query.executeQuery();
        
        SWRegisterPasswordQuery query2 = new SWRegisterPasswordQuery(myToken,userName,userPassword);
        query2.executeQuery();
    }
    
    @Test
    public void testIntialize()
    {
        LoginPasswordState state = new LoginPasswordState(sc,"Test");
        assertEquals("Test",state.getLoginName());
    }
    
    @Test
    public void testCheckMessage()
    {
        LoginPasswordState state = new LoginPasswordState(sc,"Test");
        assertEquals("Password: ",state.getMessage().getMessage());
        
    }
    
    @Test
    public void testValidLogin()
    {
        SWMessage msg = new SWMessage(userPassword);
        msg.setSecurityToken(myToken);
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof SelectCreateCharacterState);
    }
    
    @Test
    public void testInvalidLogin()
    {
        SWMessage msg = new SWMessage("badPassword");
        msg.setSecurityToken(myToken);
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof LoginNameState);
        assertEquals("--Incorrect Name or Password--\nLogin: ",sc.getServerConnectionState().getMessage().getMessage());
    }

}
