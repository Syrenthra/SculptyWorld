package sw.quest;

import java.util.Hashtable;

import sw.lifeform.Creature;
import sw.lifeform.Player;


public class CreatureQuest extends Quest
{
    private Creature m_creature;

    private int m_amount;

    private Hashtable<Player, Integer> m_kills = new Hashtable<Player, Integer>();

    public CreatureQuest(String name, String desc, Creature creature, int amount)
    {
        super(name, desc);
        m_creature = creature;
        m_amount = amount;
    }

    /**
     * 
     * @return The creature the player is supposed to hunt down and kill.
     */
    public Creature getCreature()
    {
        return m_creature;
    }

    /**
     * 
     * @return How many of that type of creature the player needs to kill to
     * complete the quest.
     */
    public int getAmount()
    {
        return m_amount;
    }

    /**
     * 
     * @param dude 
     * @return How many of the creatures the player has killed so far.
     */
    public int getCreaturesKilled(Player dude)
    {
        return m_kills.get(dude);
    }

    /**
     * Updates the number of creatures killed thus far by one.
     * @param dude 
     */
    public void updateGoal(Player dude)
    {
        if (!getCompleted(dude))
        {
            if (this.hasPlayer(dude))
            {
                int kills = m_kills.get(dude);
                if (kills < m_amount)
                {
                    m_kills.remove(dude);
                    m_kills.put(dude, ++kills);
                    if (m_kills.get(dude) == m_amount)
                        m_completed.put(dude.getID(), true);
                }
            }
        }
    }

    /**
     * 
     */
    @Override
    public void addPlayer(Player dude)
    {
        super.addPlayer(dude);
        m_kills.put(dude, 0);
    }

    @Override
    public void removePlayer(Player dude)
    {
        super.removePlayer(dude);
        m_kills.remove(dude);
    }

    /**
     TODO: This method should perform whatever actions occur on completion of the quest,
     * not simply return the reward value. 
     */
    @Override
    public void turnInQuest(Player dude)
    {

    }

    /**
     * Allows a player to turn in a quest.  Takes the player off the quest
     * and returns the reward for the player.  If the player does not have the 
     * quest it returns the value -1.
     * @param dude
     * @return Reward amount 
     */
	@Override
	public int calculateReward(Player dude)
	{
        if (this.hasPlayer(dude))
        {
            if (this.getCompleted(dude))
            {
                removePlayer(dude);
                return m_maxReward;
            }
            else
            {
                double kills = (double) m_kills.get(dude);
                double percent = kills / m_amount;
                removePlayer(dude);
                return (int) (percent * m_maxReward);
            }
        }
        // Player doesn't have the quest.
        return -1;
	}

}
