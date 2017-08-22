package sw.net;

import java.io.IOException;
import java.sql.SQLException;

import javax.net.ssl.SSLSocket;

import database.DatabaseInfo;
import database.DatabaseManager;

import sw.database.SWQuery;
import sw.net.msg.SWMessage;
import net.SecureObjectSocketInterface;
import net.SecureServer;
import net.SecureServerConnection;

/**
 * The base server class for JAVA applications dealing with Web-Lab.
 * <p>
 * 
 * TODO: Have it keep a list of those actively logged in.  If someone tries to log in twice don't let them.
 * 
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class SWServer extends SecureServer
{
    /**
     * Used in the run method for stopping it gracefully.
     * 
     */
    protected boolean m_stayAliveFlag = true;

    /**
     * Based ObjectSocket class that all ObjectSocket instances will be cloned
     * from.  We stick with the interface for testing reasons.
     */
    protected SecureObjectSocketInterface<SWMessage> m_os;


    /**
     * The constructor for the SWServer.  The SSL socket manager is normally a SWServerSocket.
     * However, we can create and use a MockSWServerSocket for testing by having the type passed
     * in as a SecureObjectInterface.
     * 
     * NOTE: You need to still initialize the list of databases that the server can talk to.
     * 
     * @param port
     *           The port that this sever is listening on.
     */
    public SWServer(SecureObjectSocketInterface<SWMessage> os, int port)
    {
        // Create our server thread with a name.
        super("SWServer", "SWServer_Thread_Group", port);
        
        // Setup the Database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        DatabaseInfo dbInfo = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dbManager.addDB(SWQuery.DATABASE, dbInfo);

        m_os = os;

        this.start();
    }

    /**
     * The body of the server thread. Loop forever, listening for and accepting
     * connections from clients. For each connection, create a Connection object
     * to handle communication through the new Socket. When we create a new
     * connection, add it to the Vector of connections, and display it in the
     * List. Note that we use synchronized to lock the Vector of connections.
     * The Vulture class does the same, so the vulture won't be removing dead
     * connections while we're adding fresh ones.
     */
    public void run()
    {
        try
        {
            while (m_stayAliveFlag)
            {
                SSLSocket clientSocket = (SSLSocket) this.getSocket().accept();
                SecureObjectSocketInterface<SWMessage> os = m_os.createInstance();
                os.connect(clientSocket);
                SWServerConnection c = new SWServerConnection(os, this.getVultureThreadGroup(), this.getVulture(), this);

                // prevent simultaneous access.
                synchronized (this.getConnections())
                {
                    this.getConnections().addElement(c);
                    this.addToLog(new String("OPENING: " + c.toString()));
                }
            }
        }
        catch (IOException e)
        {
            fail(e, "Exception while listening for connections");
        }

    }

    public void setFlag(boolean value)
    {
        m_stayAliveFlag = value;
    }

    /**
     * Used to stop the WLServer.
     * 
     */
    public void shutdown()
    {
        m_stayAliveFlag = false;

        for (SecureServerConnection connection : getConnections())
        {
            try
            {
                connection.closeClient();
            }
            catch (IOException e)
            {
                m_log.addMessageToLog(e);
            }
        }

        try
        {
            DatabaseManager.getInstance().shutdown();
        }
        catch (SQLException e1)
        {
            m_log.addMessageToLog(e1);
        }

        m_log.shutdown();

        try
        {
            m_socket.close();
        }
        catch (Exception e)
        {
        }
    }

}
