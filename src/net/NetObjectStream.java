//
// General class for 
//

package net;

import java.io.IOException;

/**
 * Designed for single use communication.  Not setup as a thread, so
 * won't run in the background.  If you want to constantly listen for
 * and send messages then you should use MessageListener.
 * 
 * @author CDGira
 *
 * @param <M>
 */
public class NetObjectStream<M>
    {
/**
 * Used to manage the Object streams and the Socket.
 */
    SecureObjectSocketInterface<M> m_os;
/**
 * Did the NetObjectStream get setup successfully?
 */
    protected boolean m_flag = true;

    public NetObjectStream(String host, int port, SecureObjectSocketInterface<M> os)
        {
        m_os = os;
        try
            {
            m_os.connect(host,port);
            }
        catch (IOException e)
            {
            e.printStackTrace();
            m_flag = false;
            }
        }

    public boolean getFlag()
        {
        return m_flag;
        }
    public M getNextMessage() throws IOException, ClassNotFoundException
        {
        M obj = null;

        try
            {
            obj = (M)m_os.readObject();
            }
        catch (ClassCastException cce)
            {
            cce.printStackTrace();
            }
        
        return obj;
        }

    public void retry(String host, int port)
        {
        try
            {
            m_os.connect(host,port);
            }
        catch (IOException e)
            {
            e.printStackTrace();
            }
        }

    public void sendMessage(M msg) throws IOException
        {
        m_os.writeObject(msg);
        }

    /**
     *  Go and close down the port connections.
     */
    public void close()
        {
        try
            {
            m_os.closeSocket();
            }
        catch (IOException e)
            {
            e.printStackTrace();
            }
        }
    }
