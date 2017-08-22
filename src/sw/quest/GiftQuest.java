package sw.quest;

import sw.item.Item;
import sw.lifeform.SocialNPC;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.SocialNetworkDecayRates;
import sw.socialNetwork.SocialQuestState;
import sw.socialNetwork.simulation.EventTypes;

/**
 * @author David Abrams
 * 
 * Creates a SocialQuest that requires a player to deliver a gift to a certain SocialNPC on the
 * behalf of another.
 * 
 * GiftQuests are created when a SocialNPC wants to: -form a new relationship -significantly improve
 * the value of an existing relationship -repay another SocialNPC for a gift
 * 
 * Success: -increase intimacy for both SocialNPCs (amount depends on value of gift) -bonus intimacy
 * if the gift is one that the receiving SocialNPCs has a preference for -receiver is socially
 * indebted to the giver (amount depends on value of gift)
 * 
 * Failure: -intimacy penalty for both SocialNPCs
 */
public class GiftQuest extends SocialQuest
{
	protected Item gift;
	protected SocialNetworkDecayRates decayRate;
	private final int BASE_CHANGE = 15;
	private final int BONUS = 15;

	/**
	 * Creates a new GiftQuest
	 * 
	 * @param name The name of the quest
	 * @param questGiver The SocialNPC handing out the quest
	 * @param target The SocialNPC to whom the gift should be delivered
	 * @param gift The Item to be delivered to the target
	 * @param difficulty The difficulty of the quest
	 */
	public GiftQuest(String name, SocialNPC questGiver, SocialNPC target, Item gift, SocialQuestDifficulty difficulty, SocialNetworkDecayRates rate)
	{
		super(name, "This is a GiftQuest.", questGiver, target, difficulty);
		this.gift = gift;
		this.decayRate = rate;
	}

	/**
	 * The target received the gift, so its relationship with the giver improves and it becomes
	 * indebted to the giver. If the two SocialNPCs are not friends already, a new relationship is
	 * formed between them.
	 */
	@Override
	public void questSuccessful()
	{
		if (currentState == SocialQuestState.IN_PROGRESS)
		{
			super.questSuccessful();
			
			questGiver.newEvent(questTarget, EventTypes.FRIENDSHIP_CREATED);

			Feelings giverFeels = null;
			Feelings targetFeels = null;

			if (!(questTarget.hasFriend(questGiver) && questGiver.hasFriend(questTarget)))
			{
				//part of the friendship doesn't exist
				if (questTarget.hasFriend(questGiver) && !questGiver.hasFriend(questTarget))
				{
					//only target is already friends with giver
					targetFeels = questTarget.getRelationships().get(questGiver);
					giverFeels = new Feelings(decayRate);
					questGiver.addFriend(questTarget, giverFeels);
				} else if (!questTarget.hasFriend(questGiver) && questGiver.hasFriend(questTarget))
				{
					//only giver is already friends with target
					giverFeels = questGiver.getRelationships().get(questTarget);
					targetFeels = new Feelings(decayRate);
					questTarget.addFriend(questGiver, targetFeels);
				} else
				{
					//friendship doesn't exist currently, so create a new one
					giverFeels = new Feelings(decayRate);
					targetFeels = new Feelings(decayRate);

					questGiver.addFriend(questTarget, giverFeels);
					questTarget.addFriend(questGiver, targetFeels);
				}
			} else if (questTarget.hasFriend(questGiver) && questGiver.hasFriend(questTarget))
			{
				//friendship exists already
				giverFeels = questGiver.getRelationships().get(questTarget);
				targetFeels = questTarget.getRelationships().get(questGiver);
			}

			//increase intimacy of relationship.
			giverFeels.changeIntimacy(BASE_CHANGE);
			targetFeels.changeIntimacy(BASE_CHANGE);
			//set trust to 3
			giverFeels.setTrust(3);
			targetFeels.setTrust(3);

			//bonus if gift is something that target likes.
			if (questTarget.getFavoriteItems().contains(gift))
			{
				giverFeels.changeIntimacy(BONUS);
				targetFeels.changeIntimacy(BONUS);
			}

			//receiver of gift becomes indebted to the giver based on value of gift
			targetFeels.changeSocialDebt(getGiftValue());
			giverFeels.changeSocialDebt(-getGiftValue());
		}
	}

	/**
	 * The target and giver both receive an intimacy penalty when the quest is failed. If they are
	 * not friends, then no relationship is formed.
	 * 
	 * The amount of intimacy lost is decreased by the control of the SocialNPC losing it. Control
	 * can reduce the intimacy penalty down to a minimum of 5.
	 */
	@Override
	public void questFailed()
	{
		if (currentState == SocialQuestState.IN_PROGRESS)
		{
			super.questFailed();

			if (questGiver.getFriends().contains(questTarget) && questTarget.getFriends().contains(questGiver))
			{
				Feelings giverFeels = questGiver.getRelationships().get(questTarget);
				Feelings targetFeels = questTarget.getRelationships().get(questGiver);

				giverFeels.changeIntimacy(-(int) (((BASE_CHANGE - 5) * (1 - questGiver.getControl())) + 5));
				targetFeels.changeIntimacy(-(int) (((BASE_CHANGE - 5) * (1 - questTarget.getControl())) + 5));
			}
		}
	}

	/**
	 * This method allows the gift Item to be changed after the GiftQuest is created.
	 */
	public void setGift(Item newGift)
	{
		this.gift = newGift;
	}

	/**
	 * @return the Item that this GiftQuest requires a Player to retrieve
	 */
	public Item getGift()
	{
		return gift;
	}

	/**
	 * The difficulty of the quest and the density of item together determine how valuable the gift
	 * is. The more dense the gift, the more valuable it is.
	 * 
	 * @return The value of the gift in social capital
	 */
	public int getGiftValue()
	{
		return (difficulty.getDifficulty() / 2) + (gift.getWeight() / gift.getSize());
	}
}
