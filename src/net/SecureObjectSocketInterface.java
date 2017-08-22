package net;

import java.io.IOException;

import javax.net.ssl.SSLSocket;

import sw.net.msg.SWTokenMsg;


public interface SecureObjectSocketInterface <R>
    {
    public static final int SERVER = 0;
    public static final int CLIENT = 1;
    
    /**
     * Used to create an instance of the spefic ObjectSocket
     */
    public SecureObjectSocketInterface<R> createInstance();
    
    /**
     * Used to create the connection specified by the host and port.
     * @param host
     * @param port
     * @throws IOException
     */
    public void connect(String host, int port) throws IOException;
    
    /**
     * Used to create the connection specified by the Socket.
     * @param host
     * @param port
     * @throws IOException
     */
    public void connect(SSLSocket s) throws IOException;
    
    /**
     * Used to send an Object through the connection.
     * @param obj
     * @throws IOException
     */
    public void writeObject(R obj) throws IOException;
    
    /**
     * Used to read an Object from the connection.
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public R readObject() throws IOException,ClassNotFoundException;
    
    /**
     * Used to close the connection.
     * @throws IOException
     */
    public void closeSocket() throws IOException;
    }
