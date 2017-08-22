package sw.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sw.database.obj.SWToken;


public class TestSWClientSocket
{
    
    @Test
    public void testConstructor()
    {
        SWToken token = SWToken.constructToken();
        SWClientSocket client = new SWClientSocket(token);
        assertEquals(token,client.m_token);
        assertEquals(SWSocket.CLIENT,client.getSocketType());
        
    }

}
