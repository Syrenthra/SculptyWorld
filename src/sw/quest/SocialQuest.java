package sw.quest;

import sw.lifeform.Player;
import sw.lifeform.SocialNPC;
import sw.socialNetwork.SocialQuestState;
import sw.socialNetwork.simulation.EventTypes;
import sw.time.TimeObserver;

/**
 * @author David Abrams
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
public abstract class SocialQuest extends Quest implements TimeObserver
{
	protected SocialNPC questGiver;
	protected SocialNPC questTarget;
	protected int timeToHoldRemaining; //-1 means the quest does not expire
	protected int timeToCompleteRemaining; //-1 means the quest does not have a time limit on completion
	protected SocialQuestState currentState;
	protected SocialQuestDifficulty difficulty;

	/**
	 * Creates a new SocialQuest. Time limits are handled via setters.
	 * 
	 * @param name The name of this quest
	 * @param desc A description of this quest
	 * @param questGiver The SocialNPC handing out the quest
	 * @param target The SocialNPC who is the target of the quest
	 * @param difficulty The difficulty of the quest
	 */
	public SocialQuest(String name, String desc, SocialNPC questGiver, SocialNPC target, SocialQuestDifficulty difficulty)
	{
		super(name, desc);
		this.questGiver = questGiver;
		this.questTarget = target;
		timeToHoldRemaining = -1;
		timeToCompleteRemaining = -1;
		currentState = SocialQuestState.IN_PROGRESS;
		this.difficulty = difficulty;
	}

	/**
	 * This method performs whatever actions occur immediately upon successful completion of the
	 * SocialQuest.
	 */
	public void questSuccessful()
	{
		if(currentState == SocialQuestState.IN_PROGRESS)
		{
			setCurrentState(SocialQuestState.SUCCESS);
			questGiver.newEvent(questTarget, EventTypes.QUEST_SUCCESSFUL);
		}
	}

	/**
	 * This method performs whatever actions occur immediately upon failure of the SocialQuest.
	 */
	public void questFailed()
	{
		if(currentState == SocialQuestState.IN_PROGRESS)
		{
			setCurrentState(SocialQuestState.FAILURE);
			questGiver.newEvent(questTarget, EventTypes.QUEST_FAILED);
		}
	}

	/**
	 * This method performs the actions that occur when a Player turns in a finished SocialQuest.
	 * Descendants of SocialQuest must call this version of turnInQuest() first thing in their own
	 * turnInQuest() methods.
	 * 
	 * TODO: Player should get a reward when the quest is turned in. Currently, nothing exists in
	 * Player to support this.
	 */
	@Override
	public void turnInQuest(Player player)
	{

		if ((currentState == SocialQuestState.SUCCESS || currentState == SocialQuestState.FAILURE) &&
			 m_players.contains(player))
		{
			questGiver.turnInQuest(this);
		} else
		{
			//state == NOT_COMPLETE, so don't do anything
		}
	}

	/**
	 * Controls what happens as time passes.
	 * 
	 * timeToCompleteRemaining should only decrease when the quest has been accepted and when a time
	 * limit has been placed on completing the quest.
	 * 
	 * timeToHoldRemaining should only decrease while the quest is not yet accepted and when a time
	 * limit has been placed on how long the quest can be held.
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
			if(timeToCompleteRemaining == 0)
			{
				//failed to complete the quest in the required amount of time
				questFailed();
			}
			
		} else
		{
			if (timeToHoldRemaining > 0)
			{
				timeToHoldRemaining--;
			}
		}
	}

	public SocialNPC getQuestTarget()
	{
		return questTarget;
	}

	public SocialNPC getQuestGiver()
	{
		return questGiver;
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
	}

	public SocialQuestState getCurrentState()
	{
		return currentState;
	}

	public void setCurrentState(SocialQuestState newState)
	{
		this.currentState = newState;
	}

	public SocialQuestDifficulty getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(SocialQuestDifficulty newDifficulty)
	{
		difficulty = newDifficulty;
	}

	@Override
	public int calculateReward(Player player)
	{
		return difficulty.getDifficulty() * 2;
	}

}
