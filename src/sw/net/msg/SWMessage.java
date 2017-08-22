package sw.net.msg;

import java.io.Serializable;

import sw.database.obj.SWToken;
import sw.net.SWServerConnection;

/**
 * The the base message type for all messages sent in ExNet III.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class SWMessage implements Serializable
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256438122995792178L;

    /**
     * Identifies who this message is attached to.
     */
    private SWToken m_token = null;

    private String m_message = null;

    public SWMessage(String msg)
    {
        m_message = msg;
    }

    /**
     * Sets who this message is attached to.
     * @param token
     */
    public void setToken(SWToken token)
    {
        m_token = token;
    }

    /**
     * Returns the text information included in the message.
     * @return
     */
    public String getMessage()
    {
        return m_message;
    }

    /**
     * Used to make sure that the message is from a legitimate source.
     * 
     * @param value
     *            Value used in determining if the source of the message is
     *            legitimate.
     * @return Returns true if a legitimate, false otherwise.
     */
    public boolean containsValidMsg(SWToken token)
    {
        if (m_token == null)
            return true;
        else if (token.toString().equals(m_token.toString()))
            return true;
        else
            return false;
    }
}
