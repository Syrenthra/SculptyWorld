package sw.quest;

import java.util.Iterator;
import java.util.Vector;

import sw.lifeform.PC;
import sw.quest.reward.FavorReward;

/**
 * 
 * @author David Abrams and Dr. Girard
 *
 * This is what the player must accomplish in order to complete the favor.  Examples of favors are 
 * killing a creature, delivering an item, or taking a message to someone.  Once the favor is completed
 * then the quest is done.
 */
public class Favor
{
	/**
	 * This is the list of FavorQuests that are using this FavorTarget.
	 */
	protected Vector<FavorReward> quests;
	
	/**
	 * Allows the player to perform the requested favor.
	 * 
	 * @param player The player trying to use this FavorTarget to perform a favor
	 */
	public void performFavorAction(PC player)
	{
		Iterator<FavorReward> itr = quests.iterator();
		FavorReward current;
		while(itr.hasNext())
		{
			current = itr.next();
			if(player.hasQuest(current.))
			{
				current.questSuccessful();
			}
		}
	}
	
	/**
	 * Adds a FavorQuest to the list that are using this FavorTarget
	 * @param quest The FavorQuest to add
	 */
	public void addQuest(FavorReward quest)
	{
		if(!quests.contains(quest))
		{
			quests.add(quest);
		}
	}
	
	/**
	 * Removes the specified quest from the list
	 * @param quest
	 */
	public void removeQuest(FavorReward quest)
	{
		if(quests.contains(quest))
		{
			quests.remove(quest);
		}
	}
	
	/**
	 * Tells us whether this FavorTarget is being used by a certain FavorQuest
	 * @param quest
	 * @return
	 */
	public boolean hasQuest(FavorReward quest)
	{
		return quests.contains(quest);
	}
}
