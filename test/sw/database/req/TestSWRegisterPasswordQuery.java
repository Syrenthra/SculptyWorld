package sw.database.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.database.ConstructTestDatabase;
import sw.database.SWQuery;
import sw.database.UserTable;
import sw.database.obj.SWToken;
import database.DatabaseInfo;
import database.DatabaseManager;


public class TestSWRegisterPasswordQuery
{
    
    DatabaseManager dm;
    SWToken myToken;
    String userName = "NameInDatabase";
    
    @Before
    public void before() throws Exception
    {
        
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
        
        // Put test name in the database.
        myToken = SWToken.constructToken();
        SWRegisterUserNameQuery query = new SWRegisterUserNameQuery(myToken,userName);
        query.executeQuery();
    }
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Test
    public void testInitialize()
    {
        SWRegisterPasswordQuery query = new SWRegisterPasswordQuery(myToken,userName,"MyPassword");
        assertEquals(userName,query.m_userName);
        assertEquals("MyPassword",query.m_password);
    }
    
    @Test
    public void testValidToken() throws Exception
    {  
        SWRegisterPasswordQuery query = new SWRegisterPasswordQuery(myToken,userName,"MyPassword");
        
        assertEquals(SWQuery.SUCCESS,query.executeQuery());  // First attempt should succeed
        
        Connection dbConnection = dm.getDatabaseInfo(SWQuery.DATABASE).makeConnection();

        String createTable = new String("SELECT * FROM "+UserTable.NAME);
        Statement stmt = dbConnection.createStatement();
        ResultSet rs = stmt.executeQuery(createTable);
        if (rs.next())
        {
            String name = rs.getString("User_Name");
            String pwd = rs.getString("User_Password");
            assertEquals("MyPassword",pwd);
        }
        else
        {
            assertTrue(false);
        }
    }
    
    @Test
    public void testInvalidToken() throws Exception
    {
        SWToken token = SWToken.constructToken();

        SWRegisterPasswordQuery query = new SWRegisterPasswordQuery(token,userName,"MyPassword");
        
        assertEquals(SWQuery.INVALID_TOKEN,query.executeQuery()); 
    }

}
