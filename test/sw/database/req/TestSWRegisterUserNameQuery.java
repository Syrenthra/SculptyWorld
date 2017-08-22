package sw.database.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.DatabaseInfo;
import database.DatabaseManager;

import sw.database.ConstructTestDatabase;
import sw.database.SWQuery;
import sw.database.UserTable;
import sw.database.obj.SWToken;
import sw.net.msg.SWMessage;


public class TestSWRegisterUserNameQuery
{
 
    DatabaseManager dm;
    @Before
    public void before() throws Exception
    {
        
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
    }
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Test
    public void testNameInAndNotInTheDatabase() throws Exception
    {  
        SWToken token = SWToken.constructToken();
        SWRegisterUserNameQuery query = new SWRegisterUserNameQuery(token,"Bob");
        
        assertEquals(SWQuery.SUCCESS,query.executeQuery());  // First attempt should succeed
        assertEquals(SWQuery.FAILED,query.executeQuery());  // Trying to put the same name in twice should fail.
        
        Connection dbConnection = dm.getDatabaseInfo(SWQuery.DATABASE).makeConnection();

        String createTable = new String("SELECT * FROM "+UserTable.NAME);
        Statement stmt = dbConnection.createStatement();
        ResultSet rs = stmt.executeQuery(createTable);
        if (rs.next())
        {
            String name = rs.getString("User_Name");
            String pwd = rs.getString("User_Password");
            assertEquals("Bob",name);
            assertEquals(token.toString(),pwd);
        }
        else
        {
            assertTrue(false);
        }
    }
}
