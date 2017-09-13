package sw.quest;

import java.util.ArrayList;

import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.quest.reward.QuestReward;
import sw.quest.reward.SocialReward;
import sw.socialNetwork.simulation.EventTypes;
import sw.time.TimeObserver;

/**
 * @author David Abrams and Dr. Girard
 * 
 * This is the parent of the different types of social quests. It contains all of the things common
 * between the social quests. A SocialQuest is initialized into a state that is not time limited. If
 * an aspect of the quest should be time limited, then the appropriate time limit (hold/complete)
 * should be added via the corresponding setter.
 * 
 * QuestSuccessful()/QuestFailed() handles the change in relationships. These changes occur at the
 * time that the quest ends. TurnInQuest() should be called when a Player actually hands the quest
 * back in for a reward.
 */
public class TimedQuest extends Quest implements TimeObserver
{
    /**
     * How long in minutes the NPC will make this quest available for players to complete.  -1 means the quest does not expire.
     * If no one accepts (and completes) the quest in time, then the quest fails.
     */
    protected int timeToHoldRemaining = -1;

    /**
     * How long in minutes the player has to complete the quest. A value of
     * -1 means the quest does not have a time limit on completion.
     */
    protected int timeToCompleteRemaining = -1;
    
    /**
     * Used when a player adds then removes the quest to reset the time to complete
     * timer.  Is set to what ever value is passed into setTimeToCompleteRemaining();
     */
    protected int m_maxTimeToComplete = -1;

    /**
     * Creates a new SocialQuest. Time limits are handled via setters.
     * 
     * The clock this quest will listen to is not added here to make testing easier as well
     * as to decouple it from any specific timer.
     * 
     * @param name The name of this quest
     * @param desc A description of this quest
     * @param questGiver The SocialNPC handing out the quest
     * @param target The SocialNPC who is the target of the quest
     * @param difficulty The difficulty of the quest
     */
    public TimedQuest(String name, String desc, NPC questGiver)
    {
        super(name, desc, questGiver);

        timeToHoldRemaining = -1;
        timeToCompleteRemaining = -1;
    }

    /**
     * This method performs whatever actions occur immediately upon failure of the TimedQuest.
     */
    public void questFailed()
    {
    	//super.questFailed();
        m_questState = QuestState.FAILED;
        //ArrayList<NPC> targets=new ArrayList<NPC>();
        for (QuestReward reward : m_rewards)
        {
        	//Adds all of the targets of the quest to a list, so that events can be created for each
        	/**SocialReward rewardTarget= (SocialReward) reward;
        	if(!targets.contains(rewardTarget.getTarget()))
        	{
        		targets.add(rewardTarget.getTarget());
        	}*/
        	reward.failedQuest();
            
        }
        /**for(NPC target:targets)
        {
        	m_granter.newEvent(target, EventTypes.QUEST_FAILED);
        }*/
        while (m_players.size() > 0)
            this.removePlayer(m_players.elementAt(0));
        m_granter.removeQuest(this);
    }
    
    /**
     * This method performs whatever actions occur immediately upon failure of the TimedQuest.
     */
    public void questSucessful(PC player)
    {
//        m_questState = QuestState.COMPLETED;
//        ArrayList<NPC> targets=new ArrayList<NPC>();
//        for (QuestReward reward : m_rewards)
//        {
//        	//Adds all of the targets of the quest to a list, so that events can be created for each
//        	SocialReward rewardTarget= (SocialReward) reward;
//        	if(!targets.contains(rewardTarget.getTarget()))
//        	{
//        		targets.add(rewardTarget.getTarget());
//        	}
        	//super.questSuccessful(player);;
//            
//        }
//        for(NPC target:targets)
//        {
//        	m_granter.newEvent(target, EventTypes.QUEST_SUCCESSFUL);
//        }
    }

    /**
     * This method performs the actions that occur when a Player turns in a finished SocialQuest.
     * Descendants of SocialQuest must call this version of turnInQuest() first thing in their own
     * turnInQuest() methods.
     * 
     * @return Whether the quest was turned in successfully or not.
     */
    @Override
    public boolean turnInQuest(PC player)
    {
        return super.turnInQuest(player);
    }

    /**
     * TODO: This should be listening to a minute timer.
     * Controls what happens as time passes.
     * 
     * timeToCompleteRemaining should only decrease when the quest has been accepted and when a time
     * limit has been placed on completing the quest.  When all players with this quest
     * as their native quest drop it then the timer is reset and the timeToHoldRemaining timer is started
     * again.  All players with the inherited quest should have it dropped.
     * 
     * timeToHoldRemaining should only decrease while the quest is not yet accepted and when a time
     * limit has been placed on how long the quest can be held.  When this quest has been picked up by a player
     * this timer is paused. 
     * 
     * @param name The name of the time that provided the update.
     * @param time What time the timer has.
     */
    @Override
    public void updateTime(String name, int time)
    {
        if (m_players.size() > 0) //if the quest has been accepted by at least one player...
        {
            if (timeToCompleteRemaining > 0)
            {
                timeToCompleteRemaining--;
            }

            /**
             * The time out condition for the quest goes inside this conditional for two reasons:
             * 1 - the quest should time out as soon as the timer hits 0
             * 2 - questFailed() should only be called ONCE when the quest expires, not every
             * 		time the timer ticks on an expired quest
             */
            if (timeToCompleteRemaining == 0)
            {
                questFailed();
            }

        }
        else
        {
            if (timeToHoldRemaining > 0)
            {
                timeToHoldRemaining--;
            }
            if (timeToHoldRemaining == 0)
            {
                questFailed();
            }
        }
    }
    
    /**
     * If we remove all the players we need to reset the time to complete timer.
     */
    @Override
    public void removePlayer(PC player)
    {
        super.removePlayer(player);
        if ((m_players.size() == 0) && (m_questState != QuestState.COMPLETED) && (m_questState != QuestState.FAILED))
        {
            timeToCompleteRemaining = m_maxTimeToComplete;
        }
    }

    public int getTimeToHoldRemaining()
    {
        return timeToHoldRemaining;
    }

    public void setTimeToHoldRemaining(int time)
    {
        this.timeToHoldRemaining = time;
    }

    public int getTimeToCompleteRemaining()
    {
        return timeToCompleteRemaining;
    }

    public void setTimeToCompleteRemaining(int time)
    {
        this.timeToCompleteRemaining = time;
        m_maxTimeToComplete = time;
    }

}
