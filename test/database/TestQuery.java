package database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class TestQuery
{
    
    @Test
    public void testIsValidDBString()
    {
        String validCharacters = new String("@059amzALZ ");
        String invalidCharacters = new String("!:'[{(/");
        
        for (int x=0;x<validCharacters.length();x++)
        {   
            assertTrue("x: "+x,Query.isValidDBString(validCharacters.charAt(x)+""));
        }
        for (int x=0;x<invalidCharacters.length();x++)
        {
            assertFalse("x: "+invalidCharacters.charAt(x),Query.isValidDBString(invalidCharacters.charAt(x)+""));
        }
        assertFalse(Query.isValidDBString("\""));
        
    }
    
    @Test
    public void testIsValidName()
    {
        String validName = new String("amzALZ");
        String invalidName = new String("059!/:'\"@[{( ");
        
        for (int x=0;x<validName.length();x++)
        {
            assertTrue("x: "+x,Query.isValidNameString(validName.charAt(x)+""));
        }
        for (int x=0;x<invalidName.length();x++)
        {
            assertFalse("x: "+x,Query.isValidNameString(invalidName.charAt(x)+""));
        }
    }
    
    @Test
    public void testIsValidEmailString()
    {
        String validEmailCharacters = new String("@059amzALZ");
        String invalidEmailCharacters = new String("!:'[{(/ ");
        
        for (int x=0;x<validEmailCharacters.length();x++)
        {   
            assertTrue("x: "+x,Query.isValidEmailString(validEmailCharacters.charAt(x)+""));
        }
        for (int x=0;x<invalidEmailCharacters.length();x++)
        {
            assertFalse("x: "+invalidEmailCharacters.charAt(x),Query.isValidEmailString(invalidEmailCharacters.charAt(x)+""));
        }
        assertFalse(Query.isValidEmailString("\""));
    }

}
