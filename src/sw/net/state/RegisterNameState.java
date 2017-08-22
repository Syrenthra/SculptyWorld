package sw.net.state;

import database.Query;
import sw.database.SWQuery;
import sw.database.req.SWRegisterUserNameQuery;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class RegisterNameState extends ServerConnectionState
{

    public RegisterNameState(SWServerConnection sc)
    {
        m_connection = sc;
        m_message = "Please enter a user name:";
    }

    @Override
    public void executeAction(SWMessage msg)
    {
        String name = msg.getMessage();
        if (Query.isValidNameString(name))
        {
            SWRegisterUserNameQuery query = new SWRegisterUserNameQuery(msg.getSecurityToken(),name);
            if (query.executeQuery() == SWQuery.SUCCESS)
            {
                m_connection.setServerConnectionState(new RegisterPasswordState(m_connection,name));
            }
            else
            {
                m_message = "That user name has been taken, please choose another: ";
            }
        }

    }

    @Override
    public SWMessage getMessage()
    {
        SWMessage msg = new SWMessage(m_message);
        return msg;
    }

}
