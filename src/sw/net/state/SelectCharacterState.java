package sw.net.state;

import java.util.Hashtable;
import java.util.Vector;

import sw.DemoWorld;
import sw.database.SWQuery;
import sw.database.req.SWCharacterListQuery;
import sw.lifeform.PC;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

/**
 * Provides the user with the list of characters they have to choose from
 * to play in the game.
 * @author cdgira
 *
 */
public class SelectCharacterState extends ServerConnectionState
{
    int m_userID;

    public SelectCharacterState(SWServerConnection sc, int id)
    {
        m_userID = id;
        m_connection = sc;
        
        /**
         * Stores the options for the player so when they choose one we don't have to recheck
         * the database.
         */
        Hashtable<Integer,String> m_options = new Hashtable<Integer,String>();
        
        StringBuffer msg = new StringBuffer("");
        SWCharacterListQuery query = new SWCharacterListQuery(m_userID);
        if (query.executeQuery() == SWQuery.SUCCESS)
        {
            Vector<String> characters = query.getCharacterList();
            for (int x = 1;x<=characters.size();x++)
            {
                m_options.put(x, characters.elementAt(x-1));
                msg.append(x +". "+characters.elementAt(x-1)+"\n");
            }
        }
        msg.append("Selection: ");
        m_message = msg.toString();
        
    }

    @Override
    public void executeAction(SWMessage msg)
    {
        String str = msg.getMessage();
        int choice = -1;
        try
        {
            choice = Integer.parseInt(str);
            
         // We need to load the character and place them in the world.
            PC character = null; // load here.
            
            if (DemoWorld.ACTIVE)
            {
                DemoWorld.constructDemoWorld();        
            }

            m_connection.setServerConnectionState(new InWorldState(m_connection,m_userID,character));
            
        } 
        catch (NumberFormatException e) 
        {
            m_message = "Invalid Choice\n"+m_message;
        }
        
        
        
    }

    @Override
    public SWMessage getMessage()
    {
        return new SWMessage(m_message);
    }

}
