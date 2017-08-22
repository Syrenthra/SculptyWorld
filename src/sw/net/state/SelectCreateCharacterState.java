package sw.net.state;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class SelectCreateCharacterState extends ServerConnectionState
{
    int m_userID;

    public SelectCreateCharacterState(SWServerConnection connection, int id)
    {
        m_connection = connection;
        m_userID = id;
        m_message = "Select Character (select) or Create new Character (create)?";
    }

    @Override
    public void executeAction(SWMessage msg)
    {
        if (msg.getMessage().equals("select"))
        {
            SelectCharacterState cls = new SelectCharacterState(m_connection,m_userID);
            m_connection.setServerConnectionState(cls);
        }
        else if (msg.getMessage().equals("create"))
        {
            CharacterNameState cns = new CharacterNameState(m_connection,m_userID);
            m_connection.setServerConnectionState(cns);
        }
        
    }

    @Override
    public SWMessage getMessage()
    {
        return new SWMessage(m_message);
    }

}
