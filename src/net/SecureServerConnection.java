package net;

import java.io.IOException;

/**
 * This class is taken from an example in the book Java in a Nutshell
 *  by David Flanagan. Copyright (c) 1996 O'Reilly & Associates.
 * 
 * @author David Flanagan
 */


/**
 * This class is the thread that handles all communication with a client It also
 * notifies the Vulture when the connection is dropped.
 */
public abstract class SecureServerConnection extends Thread
    { 
    protected ServerVulture m_vulture;

    protected SecureServer m_theServer;

    /**
     * 
     */
    public SecureServerConnection(ThreadGroup threadgroup, String threadname, ServerVulture vulture, SecureServer app)
        {
        // Give the thread a group, a name, and a priority.
        super(threadgroup, threadname);
        this.setPriority(5);
 
        m_vulture = vulture;
        m_theServer = app;
        }

    public void addToLog(String str)
        {
        m_theServer.addToLog(str);
        }

    public void addToLog(Exception e)
        {
        m_theServer.addToLog(e);
        }
    
    public abstract void closeClient() throws IOException;
    
    protected SecureServer getTheServer()
        {
        return m_theServer;
        }

    public void notifyVulture()
        {
        synchronized (m_vulture)
            {
            m_vulture.notify();
            }
        }

    /**
     * To be implemented to all the connection to function.
     */
    public abstract void run();

    /**
     * This method returns the string representation of the Connection.
     */
    public String toString()
        {
        return this.getName();
        }
    }
