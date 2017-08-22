package sw.net.state;

import database.Query;
import sw.database.SWQuery;
import sw.database.req.SWRegisterPasswordQuery;
import sw.database.req.SWRegisterUserNameQuery;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class RegisterPasswordState extends ServerConnectionState
{
    String m_userName;

    public RegisterPasswordState(SWServerConnection connection, String userName)
    {
        m_connection = connection;
        m_userName = userName;
        m_message = "Please enter a valid password:";
    }

    /**
     * If the player responds what action do we take on the server side.
     */
    @Override
    public void executeAction(SWMessage msg)
    {
        String password = msg.getMessage();

        // TODO: Do want want a seperate method for checking validity of passwords?
        if (Query.isValidNameString(password))
        {
            SWRegisterPasswordQuery query = new SWRegisterPasswordQuery(msg.getSecurityToken(),m_userName,password);
            int result = query.executeQuery();
            
            if (result == SWQuery.SUCCESS)
            {
                m_connection.setServerConnectionState(new InitialConnectionState(m_connection));
            }
            else if (result == SWQuery.INVALID_TOKEN)
            {
                // TODO: Do something here.
            }
        }
        else
        {
            m_message = "Password contains illegal characters, please enter a valid password:";
        }

    }

    /**
     * Returns the text message being sent to the player.
     */
    @Override
    public SWMessage getMessage()
    {
        return new SWMessage(m_message);
    }

    /**
     * Returns the name of the User that we are setting up a password for.
     * @return
     */
    public String getUserName()
    {
        return m_userName;
    }

}
