package net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.junit.Before;
import org.junit.Test;


public class TestSecureObjectSokcet
{
    
    @Test
    public void testSetup()
    {
        MockSecureObjectSocket client = new MockSecureObjectSocket();
        assertNull(client.m_in);
        assertNull(client.m_out);
        assertNull(client.m_socket);
        assertEquals(SecureObjectSocket.CLIENT, client.m_socketType);
    }

    @Test
    public void testConnection() throws InterruptedException
    {
        MockSecureObjectSocket client1 = new MockSecureObjectSocket();
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
            assertTrue("Connection Failed",false);
        }        
        assertNotNull(client1.m_in);
        assertNotNull(client1.m_out);
        assertNotNull(client1.m_socket);
        c.flag = false;
        
        Thread.sleep(500); // Give the connection time to close down before futher tests.
    }
    
    @Test
    public void testConvertingMsgToBytes() throws IOException, ClassNotFoundException
    {
        String msg = new String("Test");
        MockSecureObjectSocket client1 = new MockSecureObjectSocket();
        String result = client1.convertBytesToMessage(client1.convertMessageToBytes(msg));
        assertEquals(msg,result);
        
    }

}

class Connector extends Thread
{
    SSLServerSocket sock;

    boolean flag = true;

    public Connector()
    {
        try
        {
            sock = (SSLServerSocket)SSLServerSocketFactory.getDefault().createServerSocket(2000);
            sock.setEnabledCipherSuites(new String[] { "SSL_DH_anon_WITH_RC4_128_MD5", "SSL_DH_anon_WITH_DES_CBC_SHA", "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA" });

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
        DataInputStream in;

        DataOutputStream out;

        try
        {
            s = (SSLSocket)sock.accept();
           

            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            out.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        while (flag)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
    

}

class MockSecureObjectSocket extends SecureObjectSocket<String>
{
    
    public MockSecureObjectSocket()
    {
        m_socketType = CLIENT;
    }

    @Override
    public SecureObjectSocketInterface<String> createInstance()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writeObject(String obj) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String readObject() throws IOException, ClassNotFoundException
    {
        // TODO Auto-generated method stub
        return null;
    }


}
