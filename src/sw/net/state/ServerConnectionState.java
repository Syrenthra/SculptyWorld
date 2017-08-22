package sw.net.state;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public abstract class ServerConnectionState
{
    protected SWServerConnection m_connection;
    
    public abstract void executeAction(SWMessage msg);
    public abstract SWMessage getMessage();

}
