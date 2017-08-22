package net;

/**
 * This is the base class for for a Server system that uses a Vulture to manage
 * the closing of connections no longer in use.  It is an abstract class that lacks
 * a functioning run method.  All error messages are written to a log file managed
 * by the log thread for the server.  Connections are managed by a SecureServerConnection
 * class, also abstract.
 * 
 * TODO: Look at eventually replacing the network system with: http://www.onjava.com/2004/11/03/ssl-nio.html
 * 
 * @author Dudley Girard
 * 
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public abstract class SecureServer extends Thread
    {
    protected int m_port;

    protected SSLServerSocket m_socket;

    /**
     * This is the ThreadGroup for the ServerVulture.
     */
    protected ThreadGroup m_vultureThreadGroup;

    protected Vector<SecureServerConnection> m_connections;

    protected ServerVulture m_vulture;

    protected LogThread m_log;

    // Exit with an error message, when an exception occurs.
    public static void fail(Exception e, String msg)
        {
        System.err.println(msg + ": " + e);
        }

    /**
     * Create a ServerSocket to listen for connections on a specified port and
     * starts the thread. Also creates an instance of LogThread to store any
     * messages generated, such as errors.
     */
    public SecureServer(String sName, String tgName, int port)
        {
        // Create our server thread with a name.
        super(sName);
        setPriority(6);
        m_port = port;
        try
            {
            m_socket = (SSLServerSocket)SSLServerSocketFactory.getDefault().createServerSocket(m_port);
            m_socket.setEnabledCipherSuites(new String[] { 
                    "SSL_DH_anon_WITH_RC4_128_MD5", 
                    "SSL_DH_anon_WITH_DES_CBC_SHA", 
                    "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", 
                    "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", 
                    "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA" });
            }
        catch (IOException e)
            {
            e.printStackTrace();
            fail(e, "Exception creating server socket");
            }

        System.out.println("Thread Group: "+this.getThreadGroup());
        m_vultureThreadGroup = new ThreadGroup(tgName);

        m_log = new LogThread("Log_"+sName);
        m_log.start();

        // Initialize a vector to store our connections in
        m_connections = new Vector<SecureServerConnection>();

        // Create a Vulture thread to wait for other threads to die.
        // It starts itself automatically.
        m_vulture = new ServerVulture(this);
        }

    public synchronized void addToLog(String str)
        {
        m_log.addMessageToLog(str);
        }
    
    public void addToLog(Exception e)
        {
        m_log.addMessageToLog(e);
        }

    public Vector<SecureServerConnection> getConnections()
        {
        return m_connections;
        }

    public ServerSocket getSocket()
        {
        return m_socket;
        }

    public ThreadGroup getVultureThreadGroup()
        {
        return m_vultureThreadGroup;
        }

    public ServerVulture getVulture()
        {
        return m_vulture;
        }

    public abstract void run();

    public void shutdown()
        {
        m_log.shutdown();
        try
            {
            m_socket.close();
            }
        catch (IOException ioe)
            {
            }
        }

    
    }
