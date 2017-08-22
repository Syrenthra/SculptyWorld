package sw.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sw.database.obj.SWToken;


public class TestSWServerSocket
{
    
    @Test
    public void testConstructor()
    {
        SWToken token = SWToken.constructToken();
        SWServerSocket client = new SWServerSocket(token);
        assertEquals(token,client.m_token);
        assertEquals(SWSocket.SERVER,client.getSocketType());
    }

}
