package sw.database.obj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import database.InvalidDBStringException;


public class TestSWUser
{
    @Test
    public void testBasics() throws InvalidDBStringException
    {
        SWUser user = new SWUser();
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getUserLoginName());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        user = new SWUser("First", "Last", "UserName", "Password", "Email@somewhere.com");
        assertEquals("First",user.getFirstName());
        assertEquals("Last",user.getLastName());
        assertEquals("USERNAME",user.getUserLoginName());
        assertEquals("Password",user.getPassword());
        assertEquals("Email@somewhere.com",user.getEmail());
    }
    

}
