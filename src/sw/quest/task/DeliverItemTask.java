package sw.quest.task;

import java.util.Hashtable;

import sw.item.Item;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.lifeform.PCEvent;
import sw.lifeform.Party;
import sw.quest.Quest;
import sw.quest.QuestState;



/**
 * A Quest in which the player must collect one or more of a certain item.
 * 
 * @author cdgira
 *
 */
public class DeliverItemTask implements QuestTask
{
    private Quest m_quest;
    protected TaskType m_type = TaskType.ITEM_TASK;
    private int m_amount;
    private Item m_questItem;
    /**
     * Who to deliver the item to, defaults to the NPC that granted the quest.
     */
    private NPC m_target;

    /**
     * How many items for the quest the player has.
     * <PC ID, Items Found>
     */
    protected Hashtable<Integer,Integer> m_itemsFound = new Hashtable<Integer,Integer>();
    
    /**
     * How many items for the quest the player has turned in.
     * <PC ID, items turned in>
     */
    protected Hashtable<Integer,Integer> m_itemsTurnedIn = new Hashtable<Integer,Integer>();
    
    /**
     * How many items have been turned in by all the players doing the quests.
     */
    protected int m_totalItemsTurnedIn = 0;

    public DeliverItemTask(Quest quest, Item item, int amount)
    {
        this(quest,quest.getGranter(),item,amount);
    }
    
    public DeliverItemTask(Quest quest, NPC target, Item item, int amount)
    {
        m_target = target;
        m_quest = quest;
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
     * 
     */
	@Override
	public void addPlayer(PC player)
	{
	    m_itemsFound.put(player.getID(),0);
        m_itemsTurnedIn.put(player.getID(), 0);
        
        if (m_quest.getCurrentState(player) == QuestState.IN_PROGRESS)
        {
            if (m_amount > m_totalItemsTurnedIn)
                m_target.addAcceptablePersonalItem(player,m_questItem);
        }
	    
	}
	
	/**
	 * Watches for things the player does that will help the player complete the quest.
	 */
	@Override
    public void processPCEvent(PCEvent event)
    {
        if (event.getType() == PCEvent.GET_ITEM)
        {
            if (event.getItem().equals(m_questItem))
            {
                // TODO: This only partially completes the quest - item still needs to be given to the NPC.
                // TODO: Will not valid for multiple item quests.
                int value = this.m_itemsFound.remove(event.getPC().getID());
                value++;
                m_itemsFound.put(event.getPC().getID(),value);
                
            }
        }
        else if (event.getType() == PCEvent.GIVE_ITEM)
        {
            if ((event.getItem().equals(m_questItem)) && (m_amount > m_totalItemsTurnedIn))
            {
                Party party = event.getPC().getParty();
                for (PC player : party.getPlayers())
                {
                    int value = this.m_itemsTurnedIn.remove(player.getID());
                    value++;
                    m_itemsTurnedIn.put(player.getID(),value);
                }
                
                m_totalItemsTurnedIn++;
                if (m_totalItemsTurnedIn == m_amount)
                {
                    for (PC player : m_quest.getPlayers())
                    {
                        m_target.removeAcceptablePersonalItem(player, m_questItem);
                        
                        // In case there is another quest that also wants that item delivered to this NPC.
                        for (Quest quest : player.getNativeQuests())
                        {
                            QuestState currentState = quest.getCurrentState(player);
                            quest.setCurrentState(player, currentState);
                        }
                        
                        for (Quest quest : player.getInheritedQuests())
                        {
                            QuestState currentState = quest.getCurrentState(player);
                            quest.setCurrentState(player, currentState);
                        }
                    }
                }
            }
        }
        else if (event.getType() == PCEvent.DROP_ITEM)
        {
            if (event.getItem().equals(m_questItem))
            {
                int value = this.m_itemsFound.remove(event.getPC().getID());
                value--;
                m_itemsFound.put(event.getPC().getID(),value);
            }
        }

    }

    /**
     * Returns how many items the specified player has on hand for the quest.
     * @param player
     * @return
     */
    public int getNumPlayerItems(PC player)
    {
        int value = 0;
        if (m_itemsFound.containsKey(player.getID()))
                value = this.m_itemsFound.get(player.getID());
        return value;
    }


    /**
     * Returns the percentage complete this task is for a specific player.
     * TODO: Need to implement.
     */
    @Override
    public int percentComplete(PC player)
    {
        int numItemsTurnedIn = 0;
        if (m_itemsTurnedIn.containsKey(player.getID()))            
            numItemsTurnedIn = m_itemsTurnedIn.get(player.getID());
        return (numItemsTurnedIn*100)/m_amount;
    }
    
    @Override
    public int overallPercentComplete()
    {
        return (m_totalItemsTurnedIn*100)/m_amount;
    }

    /**
     * Removes the player from this task.
     */
    @Override
    public void removePlayer(PC player)
    {
        m_itemsFound.remove(player);
        
    }
    
    /**
     * Returns what type of task this is.
     * @return
     */
    @Override
    public TaskType getType()
    {
        return m_type;
    }
    
    /**
     * Used to determine if a player can still hand in an item for the task.
     */
    @Override
    public void questStateUpdate(PC player)
    {
        if (m_quest.getCurrentState(player) == QuestState.INACTIVE)
        {
            m_target.removeAcceptablePersonalItem(player,m_questItem);
        }
        else if ((m_quest.getCurrentState(player) == QuestState.IN_PROGRESS) && (m_amount < m_totalItemsTurnedIn))
        {
            m_target.addAcceptablePersonalItem(player, m_questItem);
        }
    }

    /**
     * 
     * @return Who to deliver the item to.
     */
    public NPC getTarget()
    {
        return m_target;
    }

    public Quest getQuest()
    {
        return m_quest;
    }

}
