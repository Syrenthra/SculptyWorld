package sw.quest;

import sw.item.FavorTarget;
import sw.lifeform.SocialNPC;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.SocialQuestState;

/**
 * @author David Abrams
 * 
 * Creates a quest that requires a player to perform a favor for some SocialNPC on behalf of
 * another.
 * 
 * FavorQuests are generated when neither GiftQuest or RequestFavorQuest are appropriate. FavorQuest
 * is the basic type of SocialQuest.
 * 
 * Success:
 * -intimacy increase for both SocialNPCs (amount determined by difficulty of favor)
 * 
 * Failure:
 * -intimacy penalty for both SocialNPCs
 * 
 * 
 * TODO: Currently, FavorQuest only supports one target. Add functionality to support multiple targets.
 * This would require modifying FavorQuest, QuestGenerator, and FavorTarget.
 */
public class FavorQuest extends SocialQuest
{
	protected FavorTarget objective;
	protected final int BASE_CHANGE = 10;

	/**
	 * Creates a new FavorQuest.
	 * 
	 * @param name The name of the quest
	 * @param questGiver The SocialNPC giving out the quest
	 * @param target The SocialNPC who will benefit from the favor
	 * @param objective The Item on which to perform the favor
	 * @param difficulty The difficulty of performing the favor
	 */
	public FavorQuest(String name, SocialNPC questGiver, SocialNPC target, FavorTarget objective, SocialQuestDifficulty difficulty)
	{
		super(name, "This is a FavorQuest", questGiver, target, difficulty);
		this.objective = objective;
	}

	/**
	 * This method performs the necessary actions upon successful completion of the quest. The
	 * relationship between the giver and target is strengthened depending on the difficulty of the
	 * favor.
	 */
	@Override
	public void questSuccessful()
	{
		if (currentState == SocialQuestState.IN_PROGRESS)
		{
			super.questSuccessful();

			if (questGiver.hasFriend(questTarget) && questTarget.hasFriend(questGiver))
			{
				Feelings targetFeels = questTarget.getRelationships().get(questGiver);
				Feelings giverFeels = questGiver.getRelationships().get(questTarget);

				targetFeels.changeIntimacy(calculateRelationshipChange());
				giverFeels.changeIntimacy(calculateRelationshipChange());
				targetFeels.changeSocialDebt(difficulty.getDifficulty() / 2);
				giverFeels.changeSocialDebt(-(difficulty.getDifficulty() / 2));
			}
		}
	}

	/**
	 * This method performs the necessary actions upon failure of the quest. The relationship
	 * between the giver and target is weakened based on the difficulty of the favor. The control of
	 * a SocialNPC can reduce the penalty to intimacy down to a minimum of 1/3 of the original value
	 * rounded down.
	 */
	@Override
	public void questFailed()
	{
		if (currentState == SocialQuestState.IN_PROGRESS)
		{
			super.questFailed();

			if (questGiver.hasFriend(questTarget) && questTarget.hasFriend(questGiver))
			{
				Feelings targetFeels = questTarget.getRelationships().get(questGiver);
				Feelings giverFeels = questGiver.getRelationships().get(questTarget);

				double change = calculateRelationshipChange();

				targetFeels.changeIntimacy(-(int) ((change * 1.0 / 3.0) + (2.0 / 3.0 * change * (1 - questTarget.getControl()))));
				giverFeels.changeIntimacy(-(int) ((change * 1.0 / 3.0) + (2.0 / 3.0 * change * (1 - questGiver.getControl()))));
			}
		}
	}

	/**
	 * @return The amount that this favor will change the strength of the target relationship
	 */
	private int calculateRelationshipChange()
	{
		int value = BASE_CHANGE + (difficulty.getDifficulty() / 250);
		return value;
	}

	public FavorTarget getFavorTarget()
	{
		return objective;
	}

	public void setFavorTarget(FavorTarget newTarget)
	{
		objective = newTarget;
	}
}
