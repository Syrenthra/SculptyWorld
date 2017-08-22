package sw.net;

import net.SecureObjectSocket;

import java.io.IOException;

import javax.net.ssl.SSLSocket;

import sw.database.obj.SWToken;
import sw.net.msg.SWErrorMsg;
import sw.net.msg.SWMessage;
import sw.net.msg.SWTokenMsg;

/**
 * Task: Use the SSLSocket to increase security.
 * 
 * @author cdgira
 * 
 * @param <R>
 */

public abstract class SWSocket extends SecureObjectSocket<SWMessage>
{
    /**
     * Identifies who this socket is attached to.
     */
    protected SWToken m_token = null;

    /**
     * So we don't completely block on reads to the stream.
     */
    private SWReadObjectThread m_messageReader = null;

    
    @Override
    public void connect(String host, int port) throws IOException
    {
        super.connect(host,port);
        
        if (m_messageReader == null)
            m_messageReader = new SWReadObjectThread(m_in);
        System.out.println(""+m_messageReader);
    }
    
    @Override
    public void connect(SSLSocket s) throws IOException
    {
        super.connect(s);
        
        if (m_messageReader == null)
            m_messageReader = new SWReadObjectThread(m_in);
        
        System.out.println(""+m_messageReader);
    }
    /**
     * Used to initialize a SecureTCPMessage before it is sent. Must be called
     * on any created SecureTCPMessage for the message to be processed by the
     * server. To make things easier this function is automatically called by
     * writeObject.
     * 
     * @param msg
     *            The ExptMessage to be initialized.
     */
    public void initializeMessage(SWMessage msg)
    {
        msg.setSecurityToken(m_token);
    }

    public boolean isValidMessage(SWMessage msg)
    {
        return msg.containsValidMsg(m_token);
    }

    /**
     * Sends a message across the connection.
     */
    public void writeObject(SWMessage obj) throws IOException
    {
        initializeMessage(obj);
        byte[] data = convertMessageToBytes(obj);
        m_out.writeInt(data.length);
        m_out.write(data); // Send the message.
        m_out.flush();
    }

    /**
     * Reads the next message that was sent over the connect.  Because we can't avoid
     * blocking this is done by checking the incoming queue of a thread that constantly checks
     * for new messages and reads the next message off the queue.
     */
    public SWMessage readObject() throws IOException, ClassNotFoundException
    {
        SWMessage incomingMessage = null;
        
        byte[] data = m_messageReader.getNextMessage();

        if (data != null)
        {
            
            incomingMessage = convertBytesToMessage(data);

            if (!this.isValidMessage(incomingMessage))
            {
                incomingMessage = new SWErrorMsg("Illegal Message");
            }
            else if (incomingMessage instanceof SWTokenMsg)  // TODO: This message should be handled by the internal network system?
            {
                this.setToken(((SWTokenMsg)incomingMessage).getNewToken());
                incomingMessage = null;
            }
        }

        return incomingMessage;
    }

    /**
     * Set the token for this connection.
     * @param newToken
     */
    public void setToken(SWToken newToken)
    {
        m_token = newToken;
    }
}
