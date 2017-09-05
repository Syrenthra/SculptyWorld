package sw.quest;

import java.util.Hashtable;
import java.util.Vector;

import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.lifeform.NPC;
import sw.lifeform.PCEvent;
import sw.lifeform.PCObserver;
import sw.lifeform.PC;
import sw.quest.reward.QuestReward;
import sw.quest.reward.SocialReward;
import sw.quest.task.QuestTask;
import sw.quest.task.TaskType;
import sw.socialNetwork.simulation.EventTypes;


/**
 * Base class for all quests in the game.
 * 
 * TODO: Add the ability where quest tasks have to be completed in a specific order?
 * @author cdgira
 *
 */
public class Quest implements  PCObserver
{
    protected String m_name;
    protected String m_description;
    
    /**
     * For the reward we will have it always be an item, multiple items are always put into a container.
     */
    protected Vector<QuestReward> m_rewards = new Vector<QuestReward>();

    /**
     * This is the NPC that granted the quest.  This may get changed later, but
     * the SocialRewards require that the granter be an NPC with a SocialNetwork.
     */
    protected NPC m_granter;
    
    /**
     * What things the player has to do in order to fully complete the quest.
     */
    protected Vector<QuestTask> m_tasks = new Vector<QuestTask>();
    
    /**
     * Current state of the Quest.
     */
    protected Hashtable<Integer,QuestState> m_currentState = new Hashtable<Integer,QuestState>();
    
    /**
     * Keeps track of which players have this quest.
     */
    protected Vector<PC> m_players = new Vector<PC>();
    
    /**
     * What is the overall status of this quest.
     */
    protected QuestState m_questState;

    public Quest(String name, String desc, NPC granter)
    {
        m_name = name;
        m_description = desc;
        m_granter = granter;
        m_questState = QuestState.INACTIVE;
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
     * Returns the NPC that granted this quest to the player.
     * @return
     */
    public NPC getGranter()
    {
        return m_granter;
    }
    
    /**
     * Adds a player to the quest.  Should not be allowed to add players if the quest has been completed or failed.
     * Will change the quest to IN-PROGRESS if in an INACTIVE state.
     * @param dude
     */
    public boolean addPlayer(PC dude)
    {
        boolean result = false;
        
        if ((m_questState != QuestState.COMPLETED) && (m_questState != QuestState.FAILED))
        {
            m_questState = QuestState.IN_PROGRESS;
            m_players.add(dude);
            m_currentState.put(dude.getID(), QuestState.IN_PROGRESS);
            for (QuestTask t : m_tasks)
            {
                t.addPlayer(dude);
            }
            result = true;
        }
        
        
        return result;
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
    public boolean hasPlayer(PC dude)
    {
        return m_players.contains(dude);
    }

    /**
     * Removes the player from the quest.
     * When the last player with the quest as native is removed
     * then all players should be removed from the quest.
     * @param dude
     */
    public void removePlayer(PC dude)
    {
        m_players.remove(dude); 
        m_currentState.remove(dude.getID());
        dude.removeNativeQuest(this);
        dude.removeInheritedQuest(this);
        for (QuestTask t : m_tasks)
        {
            t.removePlayer(dude);
        }
        
        boolean stillActive = false;
        for (PC  player : m_players)
        {
            if (player.getNativeQuests().contains(this))
            {
                stillActive = true;
                break;
            }
        }
        
        if (!stillActive)
        {
            while (m_players.size() > 0)
            {
                PC player = m_players.remove(0);
                m_currentState.remove(player.getID());
                player.removeNativeQuest(this);
                player.removeInheritedQuest(this);
                for (QuestTask t : m_tasks)
                {
                    t.removePlayer(player);
                }
            }
        }
        
        if ((m_players.size() == 0) && (m_questState != QuestState.COMPLETED) && (m_questState != QuestState.FAILED))
            m_questState = QuestState.INACTIVE;
    }
    
    /**
     * @return Whether the player gets and Item as a reward for completeing the quest..
     */
    public boolean hasRewardItem()
    {
        boolean hasItem = false;

        for (QuestReward reward : m_rewards)
        {
            if (reward.hasItemReward())
            {
                hasItem = true;
                break;
            }
        }
        return hasItem;
    }
    

    /**
     * @return The reward the player earned for completing the quest.
     */
    public Item getItemReward()
    {
        Vector<Item> rewardItems = new Vector<Item>();
        int requiredCapacity = 0;
        for (QuestReward reward : m_rewards)
        {
            Item item = reward.getItemReward();
            if (item != null)
            {
                rewardItems.add(item);
                requiredCapacity += item.getSize();
            }
            
        }
        
        if (rewardItems.size() == 0)
            return null;
        if (rewardItems.size() == 1)
            return rewardItems.elementAt(0);
        else // Need to create a container to hold all the rewards.
        {
            ItemContainer questBag = new ItemContainer("Quest Bag", "Holds items awarded to the character in a quest.", 1, 1, requiredCapacity);
            for (Item item : rewardItems)
            {
                questBag.store(item);
            }
            return questBag;
        }
    }

    /**
     * Returns what percent the quest was completed by a specific player.
     * @return
     */
    public int getPercentComplete(PC player)
    {
        int total = 0;
        for (QuestTask task : m_tasks)
        {
            total += task.percentComplete(player);
        }
        
        if (m_tasks.size() == 0)
            return 100;
        else
            return total/m_tasks.size();
    }
    
    /**
     * Returns the overall completion percentage of all the tasks
     * being done by various players.
     * @return
     */
    public int getOverallCompletionPercent()
    {
        int total = 0;
        for (QuestTask task : m_tasks)
        {
            total += task.overallPercentComplete();
        }
        
        if (m_tasks.size() == 0)
            return 100;
        else
            return total/m_tasks.size();
    }

    /**
     * Adds a reward a player can get for this quest.
     * @param reward
     */
    public void addReward(QuestReward reward)
    {
        m_rewards.add(reward);
    }

     /**
     * Allows a player to turn in the quest.
     * 
     * * Any items earned based on the level of completetion of the quest are handed to the player that
     * turns in the quest.  It is up to this player to hand out the items to the other questers.
     * 
     * The XP for the quest is handed out to all players that have at least part of the quest completed.
     * Each player gets percentComplete*XP.
     * 
     * The Gold is split among the players based on how much of the quest they completed.
     * If all players completed the quest to the same level the gold is split evenly among them.
     * Otherwise it is based on the percent each completed the quest.
     * Sum the percents, divide the gold by the total and then multiple that value by each person's
     * percent to determine how much gold they get.
     * @param player
     * @return True if successfully turned in the quest, false otherwise.
     */
    public boolean turnInQuest(PC player)
    {
    	//We do't have a direct way to fail a quest, while there is a failure in QuestReward
        boolean canCompleteQuest = true;
        if (this.getOverallCompletionPercent() == 100)
        {
            
            if (hasRewardItem())
            {          
                if (player.getContentsInHand(HandLocation.RIGHT) == null)
                {
                    Item rewardItem = getItemReward();
                    player.holdInHand(rewardItem, HandLocation.RIGHT);
                }
                else if (player.getContentsInHand(HandLocation.LEFT) == null)
                {
                    Item rewardItem = getItemReward();
                    player.holdInHand(rewardItem, HandLocation.LEFT);
                }
                else
                {
                    // Can't hand over the reward.
                    canCompleteQuest = false;
                }
            }
            else
            {
             // Some rewards (e.g. the Social Rewards) still do something here even if no item reward.
                getItemReward();  
            }
            if (canCompleteQuest)
            {
                int goldReward = getGoldReward();
                int xpReward = getXPReward();
                int totalPercent = 0;
                for (PC quester : m_players)
                {
                    totalPercent += getPercentComplete(quester);
                }
                double goldPerPercent = (1.0 * goldReward / totalPercent);
                for (PC quester : m_players)
                {
                    int percentComplete = getPercentComplete(quester);
                    quester.updateXP((percentComplete * xpReward) / 100);
                    quester.updateGold((int) (percentComplete * goldPerPercent));
                    // All done so remove from quest player quest list.
                    quester.removeInheritedQuest(this);
                    quester.removeNativeQuest(this);
                }
                //m_questState = QuestState.COMPLETED;
                questSuccessful();
                while (m_players.size() > 0)
                {
                    removePlayer(m_players.elementAt(0));
                }
                m_granter.removeQuest(this);
            }
        }
        else
        {
            // Can't turn in quest as its not done yet.
            canCompleteQuest = false;
        }
        
        return canCompleteQuest;
    }
    
    /**
     * Returns the total XP reward for the quest.
     * @return
     */
    private int getXPReward()
    {
        int total = 0;
        for (QuestReward reward : m_rewards)
        {
            total = total + reward.getXPReward();
        }
        return total;
    }

    /**
     * Returns the total Gold reward for the quest.
     * @return
     */
    private int getGoldReward()
    {
        int total = 0;
        for (QuestReward reward : m_rewards)
        {
            total = total + reward.getGoldReward();
        }
        return total;
    }

    /**
     * Watches for things the player does that will help the player complete the quest.
     */
    @Override
    public void pcUpdate(PCEvent event)
    {
        if (m_currentState.get(event.getPC().getID()) != QuestState.INACTIVE)
        {
            for (QuestTask t : m_tasks)
            {
                t.processPCEvent(event);
            }
        }
    }

    /**
     * Adds a quest to the task.
     * @param task
     */
    public void addTask(QuestTask task)
    {
        m_tasks.add(task);
        
    }

    /**
     * Removes a task from a quest.
     * @param task
     */
    public void removeTask(QuestTask task)
    {
        m_tasks.remove(task);
        
    }

    /**
     * Returns true if the quest contains a task of this type.
     * @param type
     * @return
     */
    public boolean containsTask(TaskType type)
    {
        for (QuestTask task : m_tasks)
        {
            if (task.getType() == type)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the list of tasks attached to this quest.
     * @return
     */
    public Vector<QuestTask> getTasks()
    {
        return m_tasks;
    }

    /**
     * Returns the rewards for this quest.
     * @return
     */
    public Vector<QuestReward> getRewards()
    {
        return m_rewards;
    }

    /**
     * Returns the current state of this quest for the specific player.
     * @return
     */
    public QuestState getCurrentState(PC player)
    {
        return m_currentState.get(player.getID());
    }

    /**
     * Updates the current state of the quest for this player.  Two primary states are In-PROGRESS
     * and INACTIVE.
     * @param player
     * @param state
     */
    public void setCurrentState(PC player, QuestState state)
    {
        m_currentState.put(player.getID(), state);
        for (QuestTask task : m_tasks)
        {
            task.questStateUpdate(player);
        }
    }

    /**
     * Returns the list of players attached to this quest.  Very helpful
     * to tasks and rewards that are attached to this quest.
     * @return
     */
    public Vector<PC> getPlayers()
    {
        return m_players;
    }

    /**
     * Returns the overall state of this quest.  If unassigned to any players
     * it should be INACTIVE.  If assigned to players then should be IN_PROGRESS.
     * If it has been completed then it should be COMPLETED.  If it is a timed 
     * quest and time ran out then it should be FAILED.
     * @return
     */
    public QuestState getCurrentState()
    {
        return m_questState;
    }

    /**
     * Changes the current overall state of the quest.
     * @param state
     */
    public void setCurrentState(QuestState state)
    {
        m_questState = state;
        
    }
    
    /**
	 * This method performs whatever actions occur immediately upon successful completion of the
	 * SocialQuest.
	 */
	public void questSuccessful()
	{
		if(m_questState == QuestState.IN_PROGRESS)
		{
			setCurrentState(QuestState.COMPLETED);
			getItemReward();
			SocialReward reward= (SocialReward) m_rewards.get(0);
			m_granter.newEvent(reward.getTarget(), EventTypes.QUEST_SUCCESSFUL);
		}
	}

	/**
	 * This method performs whatever actions occur immediately upon failure of the SocialQuest.
	 */
	public void questFailed()
	{
		if(m_questState == QuestState.IN_PROGRESS)
		{
			setCurrentState(QuestState.FAILED);
			SocialReward reward= (SocialReward) m_rewards.get(0);
			reward.failedQuest();
			m_granter.newEvent(reward.getTarget(), EventTypes.QUEST_FAILED);
		}
	}


}
