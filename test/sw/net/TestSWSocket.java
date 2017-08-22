package sw.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import net.SecureObjectSocketInterface;

import org.junit.Test;

import sw.database.obj.SWToken;
import sw.net.SWSocket;
import sw.net.msg.SWMessage;

public class TestSWSocket
{

    @Test
    public void testSendMessage() throws InterruptedException
    {
        MockSWClientSocket client1 = new MockSWClientSocket();
        Connector c = new Connector();
        c.start();
        try
        {
            client1.connect("127.0.0.1", 2000);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue("Connection Failed", false);
        }

        SWMessage msg = new SWMessage("Test");
        try
        {
            client1.writeObject(msg);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue("Message Send Failed", false);
        }
        Thread.sleep(500);
        SWMessage rec = c.getMessage();
        while (rec == null)
        {
            rec = c.getMessage();
           // System.out.println("No Message Yet..."+rec);
        }
        assertEquals(msg.getMessage(), rec.getMessage());

        c.flag = false;
    }

    /**
     * Remember all messages with null tokens are valid.
     */
    @Test
    public void testInitializeMessage()
    {
        SWToken token = SWToken.constructToken();
        SWMessage msg = new SWMessage("Test");
        msg.setSecurityToken(SWToken.constructToken());
        
        MockSWClientSocket client1 = new MockSWClientSocket();
        client1.setToken(token);
        assertFalse(client1.isValidMessage(msg));
        client1.initializeMessage(msg);
        assertTrue(client1.isValidMessage(msg));
    }

}

class Connector extends Thread
{
    SSLServerSocket sock;

    SWMessage msg = null;

    boolean flag = true;

    public Connector()
    {

        try
        {
            sock = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(2000);

            String[] suites = sock.getSupportedCipherSuites();
            System.out.println("Support cipher suites are:");
            for (int i = 0; i < suites.length; i++)
            {
                System.out.println(suites[i]);
            }
            //sock.setEnabledCipherSuites(suites);
            sock.setEnabledCipherSuites(new String[] { 
                    "SSL_DH_anon_WITH_RC4_128_MD5", 
                    "SSL_DH_anon_WITH_DES_CBC_SHA", 
                    "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", 
                    "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", 
                    "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA" });

            System.out.println("Support protocols are:");
            String[] protocols = sock.getSupportedProtocols();
            for (int i = 0; i < protocols.length; i++)
            {
                System.out.println(protocols[i]);
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        SSLSocket s = null;
        MockSWServerSocket ss = new MockSWServerSocket();

        try
        {
            s = (SSLSocket) sock.accept();

            ss.connect(s);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
            try
            {
                while (msg == null)
                {
                    msg = ss.readObject();
                    Thread.sleep(250);
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        try
        {
            s.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try
        {
            sock.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public SWMessage getMessage()
    {
        return msg;
    }

}

class MockSWServerSocket extends SWSocket
{

    public MockSWServerSocket()
    {
        m_token = null;
        m_socketType = SERVER;
    }

    @Override
    public SecureObjectSocketInterface<SWMessage> createInstance()
    {
        // TODO Auto-generated method stub
        return null;
    }

}

class MockSWClientSocket extends SWSocket
{

    public MockSWClientSocket()
    {
        m_token = null;
        m_socketType = CLIENT;
    }

    @Override
    public SecureObjectSocketInterface<SWMessage> createInstance()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
