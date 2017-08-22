package sw.net.state;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public abstract class ServerConnectionState
{
    protected String m_message;
    /**
     * TODO: There is nothing that makes me have to connect to this...should there be?
     */
    protected SWServerConnection m_connection;
    
    public abstract void executeAction(SWMessage msg);
    public abstract SWMessage getMessage();
    
    public void setMessage(String message)
    {
        m_message = message;
    }

}
