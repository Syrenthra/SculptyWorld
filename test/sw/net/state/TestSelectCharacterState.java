package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.PreparedStatement;

import mock.MockSWServerConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.database.PCTable;
import sw.database.ConstructTestDatabase;
import sw.database.SWQuery;
import sw.database.UserTable;
import sw.database.obj.SWToken;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

import database.DatabaseInfo;
import database.DatabaseManager;


public class TestSelectCharacterState
{
    
    SWServerConnection sc;
    
    DatabaseManager dm;
    
    SWToken myToken;
    String userName = "NameInDatabase";
    String userPassword = "MyPassword";
    String charBob = "Bob";
    String charFred = "Fred";
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Before
    public void before() throws Exception
    {
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
        
        // Put test name in the database.
        String addUser = "INSERT INTO "+UserTable.NAME+" ("+UserTable.USER_ID+","+UserTable.USER_NAME+","+UserTable.USER_PASSWORD+") VALUES (?,?,?)";
        PreparedStatement pStmt = di.makeConnection().prepareStatement(addUser.toString());
        pStmt.setInt(1, 1);
        pStmt.setString(2, userName);
        pStmt.setString(3, userPassword);
        
        pStmt.execute();
        
        //Put Bob into the database.
        String addCharacter = "INSERT INTO "+PCTable.NAME+" ("+PCTable.PC_ID+","+PCTable.PC_NAME+","+PCTable.PC_DATA+") VALUES (?,?,?)";
        pStmt = di.makeConnection().prepareStatement(addCharacter.toString());
        pStmt.setInt(1, 1);
        pStmt.setString(2, charBob);
        String bob = "<Character> <name>Bob</name> <desc>This is Bob.</desc>  </Character>";
        pStmt.setString(3, bob);
        pStmt.execute();
        
      //Put Fred into the database.
        addCharacter = "INSERT INTO "+PCTable.NAME+" ("+PCTable.PC_ID+","+PCTable.PC_NAME+","+PCTable.PC_DATA+") VALUES (?,?,?)";
        pStmt = di.makeConnection().prepareStatement(addCharacter.toString());
        pStmt.setInt(1, 1);
        pStmt.setString(2, charFred);
        String fred = "<Character> <name>Fred</name> <desc>This is Fred.</desc>  </Character>";
        pStmt.setString(3, fred);
        pStmt.execute();
        
        sc = new MockSWServerConnection(null,null,null,null);
        SelectCharacterState ics = new SelectCharacterState(sc,1);
        sc.setServerConnectionState(ics);
        

    }
    
    @Test
    public void testInitialize()
    {
        SelectCharacterState ics = new SelectCharacterState(sc,1);
        assertEquals(1,ics.m_userID);
    }
    
    @Test
    public void testGetMessage()
    {
        assertEquals("1. Bob\n2. Fred\nSelection: ",sc.getServerConnectionState().getMessage().getMessage());
    }
    
    @Test
    public void testSelectedCharacter()
    {
        fail("Load system for player not done yet");
        SWMessage msg = new SWMessage("1");
        sc.getServerConnectionState().executeAction(msg);
        assertTrue(sc.getServerConnectionState() instanceof InWorldState);
        InWorldState myState = (InWorldState)sc.getServerConnectionState();
        assertEquals(1,myState.getUserID());
        assertEquals("Bob",myState.getCharacter().getName());
    }

}
