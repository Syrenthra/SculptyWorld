package sw.quest.task;

import java.util.Hashtable;

import sw.lifeform.Creature;
import sw.lifeform.PC;
import sw.lifeform.PCEvent;
import sw.lifeform.Party;
import sw.quest.Quest;

public class KillCreatureTask implements QuestTask
{
    private Quest m_quest;

    private TaskType m_type = TaskType.CREATURE_TASK;

    private Creature m_creature;

    private int m_amount;

    private Hashtable<PC, Integer> m_kills = new Hashtable<PC, Integer>();

    public KillCreatureTask(Quest quest, Creature creature, int amount)
    {
        m_quest = quest;
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
    public int getCreaturesKilled(PC dude)
    {
        if (m_kills.containsKey(dude))
            return m_kills.get(dude);
        else
            return -1;
    }

    /**
     * 
     */
    @Override
    public void addPlayer(PC dude)
    {
        m_kills.put(dude, 0);
    }

    @Override
    public void removePlayer(PC dude)
    {
        m_kills.remove(dude);
    }

    /**
     * Watches for things the player does that will help the player complete the quest.
     */
    @Override
    public void processPCEvent(PCEvent event)
    {
        if (event.getType() == PCEvent.KILLED_CREATURE)
        {
            PC player = event.getPC();
            Party party = player.getParty();

            if (event.getCreature().equals(m_creature))
            {
                for (PC partyPlayer : party.getPlayers())
                {
                    if (m_kills.containsKey(partyPlayer))
                    {
                        int kills = m_kills.get(partyPlayer);
                        if (kills < m_amount)
                        {
                            m_kills.put(partyPlayer, ++kills);
                        }
                    }
                }
            }
        }

    }

    @Override
    public int percentComplete(PC player)
    {
        int kills = m_kills.get(player);
        return (kills * 100) / m_amount;
    }

    @Override
    public TaskType getType()
    {
        return m_type;
    }

    @Override
    public int overallPercentComplete()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void questStateUpdate(PC player)
    {
        // TODO Auto-generated method stub

    }

}
