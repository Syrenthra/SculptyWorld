package sw.quest;

import java.util.Hashtable;
import java.util.Vector;

import sw.item.Item;
import sw.lifeform.Player;



/**
 * A Quest in which the player must collect one or more of a certain item.
 * 
 * @author cdgira
 *
 */
public class ItemQuest extends Quest
{
    private int m_amount;
    private Item m_questItem;
    /**
     * Used to track which goals each player that has this quest has
     * acheived.
     */
    private Hashtable<Player,Vector<Goal>> m_goalsReached = new Hashtable<Player,Vector<Goal>>();
    /**
     * The different goals that can be done to complete this quest.  A player
     * may not have to complete all the goals to complete this quest.
     */
    private Vector<Goal> m_goals = new Vector<Goal>();

    public ItemQuest(String name, String desc, Item item, int amount)
    {
        super(name,desc);
        m_questItem = item;
        m_amount = amount;
    }

    /**
     * Returns the item the player or players are supposed to gather in regards to this
     * quest.
     * @return
     */
    public Item getItem()
    {
        return m_questItem;
    }

    /**
     * Returns how many of the item the player or players are supposed to gather
     * in regards to this quest.
     * @return
     */
    public int getAmount()
    {
        return m_amount;
    }

    /**
     * Adds a goal to the quest that helps measure how much progress the player
     * has made in completing the quest.
     * @param node
     */
    public void setGoalNode(Goal node)
    {
        m_goals.add(node);
        
    }

    /**
     * Returns the goal node at that index.
     * @param index
     * @return
     */
    public Goal getGoalNode(int index)
    {
        Goal goal = null;
        
        if (index < m_goals.size())
            goal = m_goals.get(0);
        return goal;
    }

    /**
     * Removes the goal node at that index.
     * @param index
     */
    public boolean removeGoalNode(Goal goal)
    {
        if (m_goals.contains(goal))
        {
            m_goals.remove(goal);
            return true;
        }

        return false;
        
    }
    
    /**
     * Adds a goal to the players list of completed goals for this quest.
     * @param player
     * @param goal
     */
    public void visitGoal(Player player, Goal goal)
    {
        if (m_goalsReached.containsKey(player))
        {
            Vector<Goal> goals = m_goalsReached.get(player);
            if (!goals.contains(goal))
            {
                goals.addElement(goal);
            }
        }
        else
        {
            Vector<Goal> goals = new Vector<Goal>();
            goals.addElement(goal);
            m_goalsReached.put(player, goals);
        }
    }

    /**
     * Has this player completed the goal.
     * @param player
     * @param index
     * @return
     */
    public boolean goalVisited(Player player, Goal goal)
    {
        if (m_goalsReached.containsKey(player))
        {
            return m_goalsReached.get(player).contains(goal);
        }
        return false;
    }

    /**
     * TODO: This method should perform whatever actions occur on completion of the quest,
     * not simply return the reward value. 
     */
    @Override
    public void turnInQuest(Player player)
    {

    }

    
    /**
     * Allows a player to turn in the quest.
     * @param player
     * @return The monetary reward for turning in the quest. 
     */
	@Override
	public int calculateReward(Player player)
	{
        if (hasPlayer(player))
        {
            if (this.getCompleted(player))
            {
                this.removePlayer(player);
                return m_maxReward;
            }
            else
            {
                int reward = 0;
                if (m_goalsReached.containsKey(player))
                {
                    int goalsReached = m_goalsReached.get(player).size();
                    reward = (goalsReached*m_maxReward)/(m_goals.size()*2);
                }
                this.removePlayer(player);
                
                return reward;
            }
        }
        return 0;
	}

}
