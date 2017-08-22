package net;

// This class waits to be notified that a thread is dying (exiting)
// and then cleans up the list of threads and the graphical list.
public class ServerVulture extends Thread
    {
    protected SecureServer m_server;

    public ServerVulture(SecureServer s) 
        {
        super(s.m_vultureThreadGroup, "Connection Vulture");

        m_server = s;
        this.start();
        }

    // This is the method that waits for notification of exiting threads
    // and cleans up the lists.  It is a synchronized method, so it
    // acquires a lock on the `this' object before running.  This is 
    // necessary so that it can call wait() on this.  Even if the 
    // the Connection objects never call notify(), this method wakes up
    // every five seconds and checks all the connections, just in case.
    // Note also that all access to the Vector of connections and to
    // the GUI List component are within a synchronized block as well.
    // This prevents the Server class from adding a new connection while
    // we're removing an old one.
    public synchronized void run() 
        {
        for(;;) 
            {
            try { this.wait(10000); } catch (InterruptedException e) { ; }
            // prevent simultaneous access
            synchronized(m_server.m_connections) 
                {
                // loop through the connections
                for(int i = 0; i < m_server.m_connections.size(); i++) 
                    {
                    SecureServerConnection c;
                    c = (SecureServerConnection)m_server.m_connections.elementAt(i);
                    // if the connection thread isn't alive anymore, 
                    // remove it from the Vector and List.
                    if (!c.isAlive()) 
                        {
                        m_server.m_connections.removeElementAt(i);
                        // m_server.getConnectionList().remove(i);
                        m_server.addToLog(new String("CLOSING: "+c.toString()));
// System.err.println("CLOSING: "+c.toString());
                        i--;
                        }
                    }
                }
            }
        }
    } 
