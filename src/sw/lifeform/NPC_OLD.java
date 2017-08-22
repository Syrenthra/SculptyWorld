package sw.lifeform;

import java.util.Enumeration;
import java.util.Hashtable;

import sw.environment.Room;
import sw.environment.RoomObserver;
import sw.environment.RoomUpdateType;
import sw.item.HandLocation;
import sw.item.Item;
import sw.quest.Quest;
import sw.quest.task.KillCreatureTask;
import sw.quest.task.QuestTask;
import sw.quest.task.TaskType;

/**
 * TODO: Think about making abstract and having all NPCs be SocialNPCs.
 * @author cdgira
 *
 */
public class NPC_OLD extends Lifeform implements RoomObserver
{
 

    /**
     * The quest this NPC is presently offering to players to complete.
     * TODO: Allow for NPCs to have more than one quest.
     * TODO: Should we have a list of tasks this NPC wants to create quests for?
     */
    protected Quest m_quest;

    protected boolean m_questActive = false;



    /**
     * Constructs the creature.
     * @param name
     * @param desc
     * @param life
     * @param damage
     * @param armor
     * @param speed
     */
    public NPC_OLD(int id, String name, String desc, int life, int damage, int armor, int speed)
    {
        
       
    }

 
 

    /**
     * Assigns a quest to this NPC.
     * @param quest
     */
    public void addAssignableQuest(Quest quest)
    {
        m_quest = quest;
    }

    /**
     * Gets the quest assigned to this NPC.
     * @return
     */
    public Quest getQuest()
    {
        return m_quest;
    }

    /**
     * Assigns the specified quest to the player.
     * @param player
     * @param i
     */
    public void assignQuest(PC player, int quest)
    {
        if (m_questActive)
        {
            player.addQuest(m_quest);
            m_quest.addPlayer(player);
        }

    }

 

    /**
     * Returns whether the NPC's quest is active or not.
     * @return
     */
    public boolean isQuestActive()
    {
        return m_questActive;
    }

    @Override
    public void updateTime(String name, int time)
    {
        // TODO Auto-generated method stub

    }

 

}
