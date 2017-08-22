package sw.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.DatabaseInfo;
import database.DatabaseManager;

import sw.database.obj.SWToken;
import sw.net.msg.SWMessage;

/**
 * Test
 * @author cdgira
 *
 */
public class TestSWQuery
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
    public void testGlobals()
    {   
        assertEquals("SculptyWorldDB",SWQuery.DATABASE);
    }
    
    @Test
    public void testIDGeneratorUserTable() throws Exception
    {   
        Connection dbConnection = dm.getDatabaseInfo(SWQuery.DATABASE).makeConnection();
        // This test should problem move to test the db constructor.
        String createTable = new String("SELECT * FROM "+NumberingTable.NAME +" WHERE "+NumberingTable.TABLE_NAME+"='"+UserTable.NAME+"'");
        Statement stmt = dbConnection.createStatement();
        ResultSet rs = stmt.executeQuery(createTable);
        if (rs.next())
        {
            String name = rs.getString(NumberingTable.TABLE_NAME);
            int num = rs.getInt(NumberingTable.NEXT_NUMBER);
            assertEquals(UserTable.NAME,name);
            assertEquals(0,num);
        }
        else
        {
            assertTrue(false);
        }
        
        MockSWQuery query = new MockSWQuery();
        int value = query.getNextIDNumber(UserTable.NAME);
        
        assertEquals(1,value);
        
        value = query.getNextIDNumber(UserTable.NAME);
        
        assertEquals(2,value);
    }
}

class MockSWQuery extends SWQuery
{

    public MockSWQuery()
    {
        super();
        System.out.println(""+this.m_databaseConnection);
    }
    
}
