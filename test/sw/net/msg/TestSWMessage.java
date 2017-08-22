package sw.net.msg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.database.obj.SWToken;


public class TestSWMessage
{
    
    @Test
    public void testUsingSecurityKey()
    {
        SWMessage msg = new SWMessage("Test");
        assertTrue(msg.containsValidMsg(null));
        SWToken token = SWToken.constructToken();
        SWToken token2 = new SWToken(token.toString().toCharArray());
        msg.setToken(token);
        assertTrue(msg.containsValidMsg(token2));
        assertFalse(msg.containsValidMsg(SWToken.constructToken()));
    }

    
    @Test
    public void testBasics()
    {
        SWMessage msg = new SWMessage("Hello");
        assertEquals("Hello",msg.getMessage());
    }
}
