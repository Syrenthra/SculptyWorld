package sw.database.req;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.database.ConstructTestDatabase;
import sw.database.SWQuery;
import sw.database.obj.SWToken;
import database.DatabaseInfo;
import database.DatabaseManager;

/**
 * For this test to work correctly SWRegisterUserNameQuery and SWRegisterPasswordQuery
 * need to be functioning correctly.
 * 
 * @author Dr. Girard
 *
 */
public class TestSWLoginQuery
{

    DatabaseManager dm;
    SWToken myToken;
    String userName = "NameInDatabase";
    String userPassword = "MyPassword";
    
    @Before
    public void before() throws Exception
    {
        
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
        
        // Put test name and password in the database.
        myToken = SWToken.constructToken();
        SWRegisterUserNameQuery query = new SWRegisterUserNameQuery(myToken,userName);
        query.executeQuery();
        
        SWRegisterPasswordQuery query2 = new SWRegisterPasswordQuery(myToken,userName,userPassword);
        query2.executeQuery();
    }
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Test
    public void testConstruction()
    {
        SWLoginQuery query = new SWLoginQuery(userName,userPassword);
        assertEquals(userName,query.m_userName);
        assertEquals(userPassword,query.m_password);
        assertEquals(-1,query.getUserID());
    }
    
    @Test
    public void testValidLoginRequest()
    {
        SWLoginQuery query = new SWLoginQuery(userName,userPassword);
        assertEquals(SWQuery.SUCCESS,query.executeQuery());
        assertEquals(1,query.getUserID());
    }

    
    @Test
    public void testInValidLoginNameLoginRequest()
    {
        SWLoginQuery query = new SWLoginQuery("wrongName",userPassword);
        assertEquals(SWQuery.FAILED,query.executeQuery());
        assertEquals(-1,query.getUserID());
    }
    
    @Test
    public void testInValidPasswordLoginRequest()
    {
        SWLoginQuery query = new SWLoginQuery(userName,"wrongPassword");
        assertEquals(SWQuery.FAILED,query.executeQuery());
        assertEquals(-1,query.getUserID());
    }
}
