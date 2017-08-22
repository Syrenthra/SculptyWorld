package sw.socialNetwork;

import sw.lifeform.SocialNPC;
import sw.quest.SocialQuestDifficulty;

/**
 * 
 * @author David Abrams
 * 
 * This class is used by SocialNPC to remember when it agrees to perform a favor.
 *
 */
public class Favor
{
	private SocialNPC requester;
	private SocialQuestDifficulty difficulty;
	
	/**
	 * Creates a new Favor.
	 * @param requester The SocialNPC who requested the favor
	 * @param difficulty The difficulty that the requestee decided on
	 */
	public Favor(SocialNPC requester, SocialQuestDifficulty difficulty)
	{
		this.requester = requester;
		this.difficulty = difficulty;
	}
	
	public SocialNPC getRequester()
	{
		return requester;
	}
	
	public SocialQuestDifficulty getDifficulty()
	{
		return difficulty;
	}

}
