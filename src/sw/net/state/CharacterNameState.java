package sw.net.state;

import sw.database.SWQuery;
import sw.database.req.SWCharacterNameQuery;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

/**
 * Allows a player to create a new character by first choosing a name for the character.
 * For now this is all that is required to create a character.
 * 
 * TODO: Expand the character creation system.
 * @author Dr. Girard
 *
 */
public class CharacterNameState extends ServerConnectionState
{

    /**
     * The unique ID of the user that is creating this character.
     */
    int m_userID;

    public CharacterNameState(SWServerConnection sc, int id)
    {
        m_userID = id;
        m_connection = sc;
        m_message = "Enter a name for your character: ";
    }

    @Override
    public void executeAction(SWMessage msg)
    {
        String charName = msg.getMessage();
        
        SWCharacterNameQuery query = new SWCharacterNameQuery(m_userID, charName);
        int result = query.executeQuery();
        
        if (result == SWQuery.SUCCESS)
        {
            SelectCreateCharacterState state = new SelectCreateCharacterState(m_connection,m_userID);
            m_connection.setServerConnectionState(state);
        }
        else if (result == SWQuery.DATA_ALREADY_EXISTS)
        {
            m_message = "Character name already taken, please enter another name: ";
        }
        else
        {
            m_message = "Invalid character name, please enter a valid character name: ";
        }

    }

    @Override
    public SWMessage getMessage()
    {
        return new SWMessage(m_message);
    }

}
