package sw.socialNetwork;

import sw.lifeform.NPC;
import sw.quest.SocialCapitolCost;

/**
 * 
 * @author David Abrams
 * 
 * This class is used by SocialNPC to remember when it agrees to perform a favor.
 *
 */
public class Favor2
{
	private NPC requester;
	private SocialCapitolCost difficulty;
	
	/**
	 * Creates a new Favor.
	 * @param requester The SocialNPC who requested the favor
	 * @param difficulty The difficulty that the requestee decided on
	 */
	public Favor2(NPC requester, SocialCapitolCost difficulty)
	{
		this.requester = requester;
		this.difficulty = difficulty;
	}
	
	public NPC getRequester()
	{
		return requester;
	}
	
	public SocialCapitolCost getDifficulty()
	{
		return difficulty;
	}

}
