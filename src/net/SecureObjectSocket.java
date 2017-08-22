package net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public abstract class SecureObjectSocket<R> implements SecureObjectSocketInterface<R>
{
    protected SSLSocket m_socket;

    protected DataInputStream m_in;

    protected DataOutputStream m_out;

    protected int m_socketType = CLIENT;

    public void closeSocket() throws IOException
    {
        m_socket.close();
    }

    public void connect(String host, int port) throws IOException
    {
        SSLSocket s = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
        connect(s);
    }

    public void connect(SSLSocket s) throws IOException
    {
        if (m_socketType == CLIENT)
        {
            m_socket = (SSLSocket) s;
            m_socket.setEnabledCipherSuites(new String[] {
                    "SSL_DH_anon_WITH_RC4_128_MD5",
                    "SSL_DH_anon_WITH_DES_CBC_SHA",
                    "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",
                    "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5",
                    "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA"});

            m_out = new DataOutputStream(m_socket.getOutputStream());
            m_out.flush();

            m_in = new DataInputStream(m_socket.getInputStream());
        }
        if (m_socketType == SERVER)
        {
            m_socket = (SSLSocket) s;

            m_in = new DataInputStream(m_socket.getInputStream());
            m_out = new DataOutputStream(m_socket.getOutputStream());
            m_out.flush();
        }

    }
    
    public InetAddress getInetAddress()
    {
        return m_socket.getInetAddress();
    }
    
    public int getSocketType()
    {
        return m_socketType;
    }


    protected byte[] convertMessageToBytes(R msg) throws IOException
    {
        SimpleByteArrayOutputStream bytesOut = new SimpleByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
        objectOut.writeObject(msg);
        objectOut.flush();
        objectOut.close();
        bytesOut.flush();
        bytesOut.close();

        byte[] buf = bytesOut.toByteArray();

        return buf;
    }

    protected R convertBytesToMessage(byte[] buf) throws IOException, ClassNotFoundException
    {
        R message = null;
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
        ObjectInputStream objectIn = new ObjectInputStream(bytesIn);
        message = (R) objectIn.readObject();

        objectIn.close();
        bytesIn.close();
        return message;
    }
    
    /**
     * Tell me who this SSLSocket is connected to.
     */
    @Override
    public String toString()
    {
        return " connected to: " + m_socket.getInetAddress().getHostName() + ":" + m_socket.getPort();
    }
}
