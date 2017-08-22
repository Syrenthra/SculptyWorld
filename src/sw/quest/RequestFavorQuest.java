package sw.quest;

import sw.item.FavorTarget;
import sw.lifeform.SocialNPC;

/**
 * @author David Abrams
 * 
 * A quest where one SocialNPC asks another SocialNPC to perform a favor on behalf of the first. The
 * second SocialNPC creates this quest and issues it to players. During completion of the quest,
 * nothing is different from a FavorQuest. Upon completion of a RequestFavorQuest, the trust counter
 * in the relationship of the SocialNPCs involved is incremented. After the counter hits 3, the
 * trust between the two SocialNPCs starts to increase. Likewise, after 3 consecutive failures, the
 * trust between the SocialNPCs will begin to decrease.
 * 
 * Success: -intimacy increase for both SocialNPCs (amount determined by difficulty of favor)
 * -completion of 3 consecutive RequestFavorQuest allows trust to increase
 * 
 * Failure: -intimacy penalty for both SocialNPCs -after 3 consecutive failures, trust begins to
 * decrease
 */
public class RequestFavorQuest extends FavorQuest
{
	/**
	 * Creates a new RequestFavorQuest.
	 * 
	 * @param name The name of this quest
	 * @param questGiver This is the SocialNPC that agrees to perform the favor
	 * @param target This is the SocialNPC that asks for the favor
	 * @param objective The Item for this favor to be performed on
	 * @param difficulty The difficulty of performing this favor
	 */
	public RequestFavorQuest(String name, SocialNPC questGiver, SocialNPC target, FavorTarget objective, SocialQuestDifficulty difficulty)
	{
		super(name, questGiver, target, objective, difficulty);
		this.m_description = "This is a RequestFavorQuest";
	}

	/**
	 * When a quest is successfully completed, the SocialNPCs involved will receive the normal
	 * increase in the strength of their relationship. Additionally, they will begin to trust each
	 * other more.
	 */
	@Override
	public void questSuccessful()
	{
		super.questSuccessful();

		if (questGiver.hasFriend(questTarget) && questTarget.hasFriend(questGiver))
		{
			questGiver.getRelationships().get(questTarget).trustIncrement();
			questTarget.getRelationships().get(questGiver).trustIncrement();
		}
	}

	/**
	 * When a quest is failed, the SocialNPCs involved will receive the normal penalty to the
	 * strength of their relationship. Additionally, their trust will begin to decay.
	 */
	@Override
	public void questFailed()
	{
		if (questGiver.hasFriend(questTarget) && questTarget.hasFriend(questGiver))
		{
			questGiver.getRelationships().get(questTarget).trustDecrement();
			questTarget.getRelationships().get(questGiver).trustDecrement();
		}
	}

}
