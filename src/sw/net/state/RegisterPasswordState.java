package sw.net.state;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class RegisterPasswordState extends ServerConnectionState
{

    public RegisterPasswordState(SWServerConnection connection)
    {
        m_connection = connection;
    }

    @Override
    public void executeAction(SWMessage msg)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public SWMessage getMessage()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
