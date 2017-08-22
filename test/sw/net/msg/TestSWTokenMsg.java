package sw.net.msg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.database.obj.SWToken;


public class TestSWTokenMsg
{
    @Test
    public void testBasics()
    {
        SWToken token = SWToken.constructToken();
        SWTokenMsg msg = new SWTokenMsg(token);
        assertEquals("TOKEN",msg.getMessage());
        assertTrue(msg.containsValidMsg(SWToken.constructToken()));
        assertEquals(token,msg.getNewToken());
    }
}
