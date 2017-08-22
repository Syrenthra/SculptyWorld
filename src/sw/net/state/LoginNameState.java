package sw.net.state;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class LoginNameState extends ServerConnectionState
{

    public LoginNameState(SWServerConnection sc)
    {
        m_connection = sc;
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
