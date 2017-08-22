package sw.database.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.PreparedStatement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.database.PCTable;
import sw.database.ConstructTestDatabase;
import sw.database.SWQuery;
import sw.database.UserTable;
import sw.database.obj.SWToken;
import database.DatabaseInfo;
import database.DatabaseManager;


public class TestSWCharacterNameQuery
{

    DatabaseManager dm;
    SWToken myToken;
    String userName = "NameInDatabase";
    String userPassword = "MyPassword";
    String charBob = "Bob";
    
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
    }
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Test
    public void testConstructor()
    {
        SWCharacterNameQuery query = new SWCharacterNameQuery(1,"Hello'");
        assertEquals(1,query.m_userID);
        assertEquals("Hello'",query.m_characterName);
    }
    
    @Test
    public void testBadCharacterName()
    {
        SWCharacterNameQuery query = new SWCharacterNameQuery(1,"Hello'");
        assertEquals(SWQuery.INVALID_NAME,query.executeQuery());
    }
    
    @Test
    public void testCharacterNameAlreadyInDatabase()
    {
        SWCharacterNameQuery query = new SWCharacterNameQuery(1,charBob);
        assertEquals(SWQuery.DATA_ALREADY_EXISTS,query.executeQuery());
    }
    
    @Test
    public void testValidCharacterName()
    {
        SWCharacterNameQuery query = new SWCharacterNameQuery(1,"Fred");
        assertEquals(SWQuery.SUCCESS,query.executeQuery());
        assertFalse("Need to update XML so can test data is there",true);
    }
}
