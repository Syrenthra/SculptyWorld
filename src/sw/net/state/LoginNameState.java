package sw.net.state;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

/**
 * The state when the user is first trying to log in to SculpltyWorld and
 * needs to provide his or her login name.
 * @author Dr. Girard
 *
 */
public class LoginNameState extends ServerConnectionState
{

    public LoginNameState(SWServerConnection sc)
    {
        m_connection = sc;
        m_message = "Login: ";
    }

    /**
     * Since we don't want users trying to find a valid account name we
     * don't let them know if they have failed till after the password state.
     */
    @Override
    public void executeAction(SWMessage msg)
    {
        String loginName = msg.getMessage();
        m_connection.setServerConnectionState(new LoginPasswordState(m_connection,loginName));

    }

    /**
     * Returns the response message to send to the user.
     */
    @Override
    public SWMessage getMessage()
    {
        return new SWMessage(m_message);
    }

}
