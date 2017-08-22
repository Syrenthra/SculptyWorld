package sw.item;

import java.util.Iterator;
import java.util.Vector;

import sw.lifeform.Player;
import sw.quest.FavorQuest;

/**
 * 
 * @author David Abrams
 *
 * This abstract class must be extended by any classes that will be valid targets of FavorQuests.
 */
public abstract class FavorTarget
{
	/**
	 * This is the list of FavorQuests that are using this FavorTarget.
	 */
	protected Vector<FavorQuest> quests;
	
	/**
	 * Allows the player to perform the requested favor.
	 * 
	 * @param player The player trying to use this FavorTarget to perform a favor
	 */
	public void performFavorAction(Player player)
	{
		Iterator<FavorQuest> itr = quests.iterator();
		FavorQuest current;
		while(itr.hasNext())
		{
			current = itr.next();
			if(player.hasQuest(current))
			{
				current.questSuccessful();
			}
		}
	}
	
	/**
	 * Adds a FavorQuest to the list that are using this FavorTarget
	 * @param quest The FavorQuest to add
	 */
	public void addQuest(FavorQuest quest)
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
	public void removeQuest(FavorQuest quest)
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
	public boolean hasQuest(FavorQuest quest)
	{
		return quests.contains(quest);
	}
}
