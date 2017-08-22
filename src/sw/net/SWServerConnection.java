package sw.net;

import java.io.IOException;
import java.util.Vector;

import sw.database.obj.SWToken;
import sw.net.msg.SWErrorMsg;
import sw.net.msg.SWMessage;
import sw.net.msg.SWTokenMsg;
import sw.net.state.InitialConnectionState;
import sw.net.state.ServerConnectionState;
import net.SecureObjectSocketInterface;
import net.SecureServerConnection;
import net.ServerVulture;

/**
 *  This class is the thread that handles all communication with a client
 *  It also notifies the Vulture when the connection is dropped.
 *  
 *  TODO: It looks like it will only handle one message and then quit.
 */
public class SWServerConnection extends SecureServerConnection
{
    static protected long m_connectionNumber = 0;

    protected ServerConnectionState m_serverState;
    
    private Vector<SWMessage> m_outgoingQueue = new Vector<SWMessage>();

    /**
     * TODO I think this is useless now.
     */
    protected long m_securityKey = (Long.valueOf("3938457194759273")).longValue();

    protected boolean m_run = true;

    /**
     * Manages the flow of information to and from the connection.
     */
    protected SecureObjectSocketInterface<SWMessage> m_client;

    // Initialize the streams and start the thread
    public SWServerConnection(SecureObjectSocketInterface<SWMessage> wlos, ThreadGroup threadgroup, ServerVulture vulture, SWServer app)
    {
        // Give the thread a group, a name, and a priority.
        super(threadgroup, "Connection-" + m_connectionNumber++, vulture, app);
        m_client = wlos;
        m_serverState = new InitialConnectionState(this);
        this.start();
    }

    /**
     * Closes the connection down to the client.
     */
    public void closeClient() throws IOException
    {
        m_run = false;
        m_client.closeSocket();
    }

    /**
     * 
     * @return The connection to the client that connected to the server for the game.
     */
    public SecureObjectSocketInterface<SWMessage> getClient()
    {
        return m_client;
    }

    /**
     * 
     * @return The present state of the connection (e.g. logining in, playing the game, etc...)
     */
    public ServerConnectionState getServerConnectionState()
    {
        return m_serverState;
    }

    /**
     * Listens on the incoming stream for any messages. If a message is received
     * it checks to see if it is valid then runs getGeneralServerResponse. If
     * there is a message to send back it sends it back via the out stream. The
     * streams are then closed and the thread stops.
     * <p>
     * 
     * @see girard.ship.wl.io.msg.SWMessage
     */
    public void run()
    {

        try
        {
            // Send the Intro Message that sends the unique token for this connection.
            // This token will be tied to this user when he or she logs in.
            SWToken token = SWToken.constructToken();
            SWTokenMsg introMsg = new SWTokenMsg(token);
            m_client.writeObject(introMsg);
            ((SWSocket) m_client).setToken(token);

            while (m_run)
            {
                SWMessage msg = m_client.readObject();
                if (msg != null)
                {
                    // Message failed security check.
                    if (msg instanceof SWErrorMsg)
                    {
                        m_theServer.addToLog(((SWErrorMsg) msg).getError());
                    }
                    else
                    {
                        m_serverState.executeAction(msg);
                        SWMessage out_msg = m_serverState.getMessage();
                        if (out_msg != null)
                            m_client.writeObject(out_msg);
                    }
                }
                if (m_outgoingQueue.size() > 0)
                {
                    m_client.writeObject(m_outgoingQueue.remove(0));
                }
            }
        }
        catch (IOException ioe)
        {
            addToLog("IOE: " + ioe + " Loc: " + this);
        }
        catch (ClassNotFoundException cnfe)
        {
            addToLog("CNFE: " + cnfe + " Loc: " + this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            addToLog("E: " + e + " Loc: " + this);
        }

        // When we're done, for whatever reason, be sure to close
        // the socket, and to notify the Vulture object. Note that
        // we have to use synchronized first to lock the vulture
        // object before we can call notify() for it.
        finally
        {
            try
            {
                this.closeClient();
            }
            catch (IOException e2)
            {
                ;
            }
            this.notifyVulture();
        }
        System.out.println("Sever Connection Closed");
    }

    /**
     * This method returns the string representation of the Connection.
     */
    public String toString()
    {
        return this.getName() + m_client.toString();
    }

    /**
     * Changes the state of the user's connection to the server.
     * @param state
     */
    public void setServerConnectionState(ServerConnectionState state)
    {
        m_serverState = state;
    }

    /**
     * Adds a message to send to the outgoing message queue.
     * @param msg
     */
    public void sendMessage(SWMessage msg)
    {
        m_outgoingQueue.add(msg);    
    }
    
    /**
     * Returns the next message to be sent by the server.
     * @return
     */
    public SWMessage getNextMessage()
    {
        if (m_outgoingQueue.size() > 0)
            return m_outgoingQueue.elementAt(0);
        return null;
    }
    
    /**
     * Removes and returns the next message to be sent by the SWServerConnection.
     * @return
     */
    public SWMessage removeNextMessage()
    {
        if (m_outgoingQueue.size() > 0)
            return m_outgoingQueue.remove(0);
        return null;
    }

}
