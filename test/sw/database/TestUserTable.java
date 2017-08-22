package sw.database;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestUserTable
{
    
    @Test
    public void testGlobals()
    {
        assertEquals("USER_TABLE",UserTable.NAME);
        assertEquals("USER_ID",UserTable.USER_ID);
        assertEquals("USER_NAME",UserTable.USER_NAME);
        assertEquals("USER_PASSWORD",UserTable.USER_PASSWORD);
    }

}
