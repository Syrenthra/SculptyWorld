package sw.quest.reward;

import static org.junit.Assert.*;

import mock.MockItem;

import org.junit.Test;

import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.quest.reward.GiftReward;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.SocialNetworkDecayRates;

/**
 * @author David Abrams and Dr. Girard
 * 
 * The purpose of this set of tests is to make sure that GiftQuest behaves properly.
 */
public class TestGiftReward
{
	/**
	 * This test makes sure that a GiftQuest can be initialized properly, and that the getters and
	 * setters work properly.
	 */
	@Test
	public void testInit()
	{
		NPC questGiver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		NPC questTarget = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		MockItem gift = new MockItem(1, 1);
		MockItem otherGift = new MockItem(5, 400); //5 cm^3, 400 grams
		Quest quest = new Quest("Quest","Description",questGiver);
		GiftReward reward = new GiftReward(quest, questTarget, gift, SocialCapitolCost.CHEAP, SocialNetworkDecayRates.NORMAL);

		assertEquals(gift, reward.getGift());

		reward.setGift(otherGift);
		assertEquals(otherGift, reward.getGift());

		assertEquals(330, reward.getGiftValue());
		
		assertFalse(reward.hasItemReward());
	}

	/**
	 * This test makes sure that a GiftQuest performs the proper behaviors when successfully
	 * completed. This test examines the cases that should occur during normal program flow.
	 * Differences in difficulty are not included in the test because the difficulty of a 
	 * GiftQuest has no impact on the changes to the relationship.
	 */
	@Test
	public void testQuestSuccess()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		MockItem gift = new MockItem(1, 100); //1 cm^3, 100 grams
		Quest quest = new Quest("Quest","Description",giver);
		GiftReward reward = new GiftReward(quest, target, gift, SocialCapitolCost.CHEAP, SocialNetworkDecayRates.NORMAL);
		Feelings giverFeels = new Feelings();
		Feelings targetFeels = new Feelings();

		//first check when friendship already exists and the gift is not a favorite of target
		giver.getSocialNetwork().addFriend(target, giverFeels);
		target.getSocialNetwork().addFriend(giver, targetFeels);
		
		reward.getItemReward();
		
		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(50, giverFeels.getIntimacy());
		assertEquals(50, targetFeels.getIntimacy());
		assertEquals(-350, giver.getSocialNetwork().getRelationships().get(target).getSocialDebtOwed());
		assertEquals(350, target.getSocialNetwork().getRelationships().get(giver).getSocialDebtOwed());

		//second check when friendship already exists and the gift IS a favorite of target
		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		target.addFavoriteItem(gift);

		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(50, giverFeels.getIntimacy());
		assertEquals(50, targetFeels.getIntimacy());
		assertEquals(-700, giver.getSocialNetwork().getRelationships().get(target).getSocialDebtOwed());
		assertEquals(700, target.getSocialNetwork().getRelationships().get(giver).getSocialDebtOwed());

		//third, check when friendship does not already exist
		giver.removeFriend(target);
		target.removeFriend(giver);

		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertTrue(giver.getSocialNetwork().hasFriend(target));
		assertTrue(target.getSocialNetwork().hasFriend(giver));
		assertEquals(65, giver.getSocialNetwork().getRelationships().get(target).getIntimacy());
		assertEquals(65, target.getSocialNetwork().getRelationships().get(giver).getIntimacy());
	}
	
	/**
	 * This test makes sure that a GiftQuest performs the proper actions upon being successfully
	 * completed. This test looks at cases that should not occur during normal program flow but are
	 * still covered for the sake of completeness.
	 */
	@Test
	public void testQuestSuccessfulHalfRelationships()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		MockItem gift = new MockItem(1, 100); //1 cm^3, 100 grams
		Quest quest = new Quest("Quest","Description",giver);
		GiftReward reward = new GiftReward(quest, target, gift, SocialCapitolCost.CHEAP, SocialNetworkDecayRates.NORMAL);
		Feelings giverFeels = new Feelings();
		Feelings targetFeels = new Feelings();

		//check when only the giver is friends with the target.
		giver.getSocialNetwork().addFriend(target, giverFeels);
		giverFeels.setIntimacy(50);
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		int duplicates = giver.getSocialNetwork().getFriends().lastIndexOf(target) - giver.getSocialNetwork().getFriends().indexOf(target);
		assertEquals(0, duplicates); //make sure that target exists only once in giver's list of friends
		assertTrue(target.getSocialNetwork().hasFriend(giver));
		assertEquals(65, giver.getSocialNetwork().getRelationships().get(target).getIntimacy());
		assertEquals(50, target.getSocialNetwork().getRelationships().get(giver).getIntimacy());

		//check when only the target is friends with the giver
		giver.removeFriend(target);
		target.removeFriend(giver);
		target.getSocialNetwork().addFriend(giver, targetFeels);
		targetFeels.setIntimacy(50);
		
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		duplicates = target.getSocialNetwork().getFriends().lastIndexOf(giver) - target.getSocialNetwork().getFriends().indexOf(giver);
		assertEquals(0, duplicates); //make sure that giver exists only once in target's list of friends
		assertTrue(giver.getSocialNetwork().hasFriend(target));
		assertEquals(50, giver.getSocialNetwork().getRelationships().get(target).getIntimacy());
		assertEquals(65, target.getSocialNetwork().getRelationships().get(giver).getIntimacy());
	}

	/**
	 * This test makes sure that a GiftQuest performs the proper behaviors
	 */
	@Test
	public void testGiftRewardOnQuestFail()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		MockItem gift = new MockItem(1, 100); //1 cm^3, 100 grams
		Quest quest = new Quest("Quest","Description",giver);
		GiftReward reward = new GiftReward(quest, target, gift, SocialCapitolCost.CHEAP, SocialNetworkDecayRates.NORMAL);
		Feelings giverFeels = new Feelings();
		Feelings targetFeels = new Feelings();
		giver.getSocialNetwork().setControl(0.0);
		target.getSocialNetwork().setControl(0.0);

		//if they SocialNPCs are not friends, nothing should happen
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertFalse(giver.getSocialNetwork().getFriends().contains(target));
		assertFalse(target.getSocialNetwork().getFriends().contains(giver));

		//if the SocialNPCs are friends, then their relationship should take a hit
		giver.getSocialNetwork().addFriend(target, giverFeels);
		target.getSocialNetwork().addFriend(giver, targetFeels);

		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(20, giverFeels.getIntimacy());
		assertEquals(20, targetFeels.getIntimacy());

		//make sure that control properly affects the amount of intimacy lost
		giver.getSocialNetwork().setControl(1.0);
		target.getSocialNetwork().setControl(1.0);
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		
		reward.failedQuest();
		
		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(95, giverFeels.getIntimacy());
		assertEquals(95, targetFeels.getIntimacy());
		
		giver.getSocialNetwork().setControl(0.5);
		target.getSocialNetwork().setControl(0.5);
		
		reward.failedQuest();
		
		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(85, giverFeels.getIntimacy());
		assertEquals(85, targetFeels.getIntimacy());
	}

}

