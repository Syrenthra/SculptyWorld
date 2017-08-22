package sw.quest;

import java.util.Hashtable;
import java.util.Vector;

import sw.lifeform.Party;
import sw.lifeform.Player;


/**
 * Base class for all quests in the game.
 * @author cdgira
 *
 */
public abstract class Quest
{
    protected String m_name;
    protected String m_description;
    protected int m_maxReward;
    protected Hashtable<Integer,Boolean> m_completed = new Hashtable<Integer,Boolean>();
    /**
     * Keeps track of which players have this quest.
     */
    protected Vector<Player> m_players = new Vector<Player>();

    public Quest(String name, String desc)
    {
        m_name = name;
        m_description = desc;
    }

    /**
     * 
     * @return The name of the quest.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * 
     * @return A description of the quest.
     */
    public String getDescription()
    {
        return m_description;
    }

    /**
     * Adds a player to the quest.
     * @param dude
     */
    public void addPlayer(Player dude)
    {
        m_players.add(dude);
    }

    /**
     * Returns the number of players assigned to the quest.
     * @return
     */
    public int getNumPlayers()
    {
        return m_players.size();
    }

    /**
     * 
     * @param dude
     * @return  True if dude has the quest, false otherwise.
     */
    public boolean hasPlayer(Player dude)
    {
        return m_players.contains(dude);
    }

    /**
     * Removes the player from the quest.
     * @param dude
     */
    public void removePlayer(Player dude)
    {
        m_players.remove(dude);    
        dude.removeQuest(this);
    }

    /**
     * 
     * @return The max reward for the quest.
     */
    public int getMaxReward()
    {
        return m_maxReward;
    }

    /**
     * Sets the max reward a player can get for this quest.
     * @param reward
     */
    public void setMaxReward(int reward)
    {
        m_maxReward = reward; 
    }

    /**
     * Sets whether the quest is completed or not.
     * @param state
     */
    public void setCompleted(Player dude, boolean state)
    {
        if (dude.getParty() == null)
        {
            if (m_completed.contains(dude.getID()))
                m_completed.remove(dude.getID());
            m_completed.put(dude.getID(), state);
        }
        else
        {
            Party party = dude.getParty();
            for (Player player : party.getPlayers())
            {
                if (m_completed.contains(player.getID()))
                    m_completed.remove(player.getID());
                m_completed.put(player.getID(), state);
            }
        }
    }

    /**
     * Allows a player to turn in the quest.
     * @param player
     */
    public abstract void turnInQuest(Player player);
    
    /**
     * Calculates the reward for completing the quest.
     */
    public abstract int calculateReward(Player player);
    
    /**
     * Returns whether this quest is completed or not for that player.
     * @param dude 
     * @return
     */
    public boolean getCompleted(Player dude)
    {
        if (m_completed.containsKey(dude.getID()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
