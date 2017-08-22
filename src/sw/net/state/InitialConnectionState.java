package sw.net.state;

import sw.DemoWorld;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class InitialConnectionState extends ServerConnectionState
{

    public InitialConnectionState(SWServerConnection sc)
    {
        m_connection = sc;
    }

    @Override
    public void executeAction(SWMessage msg)
    {
        if (msg.getMessage().equals("Register"))
            m_connection.setServerConnectionState(new RegisterNameState(m_connection));
        else if (msg.getMessage().equals("Login"))
            m_connection.setServerConnectionState(new LoginNameState(m_connection));
        else if (msg.getMessage().equals("Demo"))
        {
            DemoWorld.constructDemoWorld();
            m_connection.setServerConnectionState(new InWorldState(m_connection));
        }
    }

    @Override
    public SWMessage getMessage()
    {
        SWMessage msg = new SWMessage("Login or Register");
        return msg;
    }

}
