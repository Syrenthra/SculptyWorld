package sw.net.state;

import database.Query;
import sw.database.SWQuery;
import sw.database.req.SWLoginQuery;
import sw.database.req.SWRegisterPasswordQuery;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

/**
 * The state where the user is trying to login and must provide the
 * password to his or her account.
 * @author Dr. Girard
 *
 */
public class LoginPasswordState extends ServerConnectionState
{
    String m_userName;

    public LoginPasswordState(SWServerConnection sc, String loginName)
    {
        m_connection = sc;
        m_userName = loginName;
        m_message = "Password: ";
    }

    /**
     * Uses the password provided in the message to try and log in the user.
     */
    @Override
    public void executeAction(SWMessage msg)
    {
        String password = msg.getMessage();
        
     // TODO: Do want want a separate method for checking validity of passwords?
        if (Query.isValidNameString(password))
        {
            SWLoginQuery query = new SWLoginQuery(m_userName,password);
            int result = query.executeQuery();
            
            if (result == SWQuery.SUCCESS)
            {
                m_connection.setServerConnectionState(new SelectCreateCharacterState(m_connection,query.getUserID()));
            }
            else
            {
                LoginNameState state = new LoginNameState(m_connection);
                state.setMessage("--Incorrect Name or Password--\nLogin: ");
                m_connection.setServerConnectionState(state);
            }
        }
        else
        {
            m_message = "Password contains illegal characters, please enter a valid password:";
        }

    }

    /**
     * Returns the message to send to the user.
     */
    @Override
    public SWMessage getMessage()
    {
        return new SWMessage(m_message);
    }

    /**
     * Returns the login name of the person trying to connection to SculptyWOrld
     * @return
     */
    public String getLoginName()
    {
        return m_userName;
    }

}
