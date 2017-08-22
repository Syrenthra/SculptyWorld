package sw.net.state;

import database.Query;
import sw.database.SWRegisterNameQuery;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class RegisterNameState extends ServerConnectionState
{

    public RegisterNameState(SWServerConnection sc)
    {
        m_connection = sc;
    }

    @Override
    public void executeAction(SWMessage msg)
    {
        String name = msg.getMessage();
        if (Query.isValidNameString(name))
        {
            SWRegisterNameQuery query = new SWRegisterNameQuery(m_connection,msg);
            if (query.executeQuery())
            {
                m_connection.setServerConnectionState(new RegisterPasswordState(m_connection));
            }
            else
            {
                
            }
        }

    }

    @Override
    public SWMessage getMessage()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
