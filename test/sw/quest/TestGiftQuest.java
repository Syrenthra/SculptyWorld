package sw.quest;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.item.Item;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.QuestGenerator;
import sw.socialNetwork.SocialNetworkDecayRates;
import sw.socialNetwork.SocialQuestState;

/**
 * @author David Abrams
 * 
 * The purpose of this set of tests is to make sure that GiftQuest behaves properly.
 */
public class TestGiftQuest
{
	/**
	 * This test makes sure that a GiftQuest can be initialized properly, and that the getters and
	 * setters work properly.
	 */
	@Test
	public void testInit()
	{
		MockSocialNPC questGiver = new MockSocialNPC();
		MockSocialNPC questTarget = new MockSocialNPC();
		MockGift gift = new MockGift(1, 1);
		MockGift otherGift = new MockGift(5, 400); //5 cm^3, 400 grams
		GiftQuest quest = new GiftQuest("Test GiftQuest", questGiver, questTarget, gift, SocialQuestDifficulty.EASY, SocialNetworkDecayRates.NORMAL);

		assertEquals(gift, quest.getGift());

		quest.setGift(otherGift);
		assertEquals(otherGift, quest.getGift());

		assertEquals(330, quest.getGiftValue());
		
		questGiver.getQuestGenerator().clear();
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
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockGift gift = new MockGift(1, 100); //1 cm^3, 100 grams
		GiftQuest quest = new GiftQuest("Test GiftQuest", giver, target, gift, SocialQuestDifficulty.EASY, SocialNetworkDecayRates.NORMAL);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();

		//first check when friendship already exists and the gift is not a favorite of target
		giver.addFriend(target, giverFeels);
		target.addFriend(giver, targetFeels);
		quest.questSuccessful();

		assertEquals(SocialQuestState.SUCCESS, quest.getCurrentState());
		assertEquals(50, giverFeels.getIntimacy());
		assertEquals(50, targetFeels.getIntimacy());
		assertEquals(-350, giver.getRelationships().get(target).getSocialDebtOwed());
		assertEquals(350, target.getRelationships().get(giver).getSocialDebtOwed());

		//second check when friendship already exists and the gift IS a favorite of target
		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		target.addFavoriteItem(gift);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);

		quest.questSuccessful();

		assertEquals(SocialQuestState.SUCCESS, quest.getCurrentState());
		assertEquals(50, giverFeels.getIntimacy());
		assertEquals(50, targetFeels.getIntimacy());
		assertEquals(-700, giver.getRelationships().get(target).getSocialDebtOwed());
		assertEquals(700, target.getRelationships().get(giver).getSocialDebtOwed());

		//third, check when friendship does not already exist
		giver.removeFriend(target);
		target.removeFriend(giver);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);

		quest.questSuccessful();

		assertTrue(giver.hasFriend(target));
		assertTrue(target.hasFriend(giver));
		assertEquals(65, giver.getRelationships().get(target).getIntimacy());
		assertEquals(65, target.getRelationships().get(giver).getIntimacy());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a GiftQuest performs the proper actions upon being successfully
	 * completed. This test looks at cases that should not occur during normal program flow but are
	 * still covered for the sake of completeness.
	 */
	@Test
	public void testQuestSuccessfulHalfRelationships()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockGift gift = new MockGift(1, 100); //1 cm^3, 100 grams
		GiftQuest quest = new GiftQuest("Test GiftQuest", giver, target, gift, SocialQuestDifficulty.EASY, SocialNetworkDecayRates.NORMAL);
		Feelings giverFeels = new Feelings();
		Feelings targetFeels = new Feelings();

		//check when only the giver is friends with the target.
		giver.addFriend(target, giverFeels);
		giverFeels.setIntimacy(50);
		quest.questSuccessful();

		int duplicates = giver.getFriends().lastIndexOf(target) - giver.getFriends().indexOf(target);
		assertEquals(0, duplicates); //make sure that target exists only once in giver's list of friends
		assertTrue(target.hasFriend(giver));
		assertEquals(65, giver.getRelationships().get(target).getIntimacy());
		assertEquals(50, target.getRelationships().get(giver).getIntimacy());

		//check when only the target is friends with the giver
		giver.removeFriend(target);
		target.removeFriend(giver);
		target.addFriend(giver, targetFeels);
		targetFeels.setIntimacy(50);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		quest.questSuccessful();

		duplicates = target.getFriends().lastIndexOf(giver) - target.getFriends().indexOf(giver);
		assertEquals(0, duplicates); //make sure that giver exists only once in target's list of friends
		assertTrue(giver.hasFriend(target));
		assertEquals(50, giver.getRelationships().get(target).getIntimacy());
		assertEquals(65, target.getRelationships().get(giver).getIntimacy());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a GiftQuest performs the proper behaviors
	 */
	@Test
	public void testQuestFail()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockGift gift = new MockGift(1, 100); //1 cm^3, 100 grams
		GiftQuest quest = new GiftQuest("Test GiftQuest", giver, target, gift, SocialQuestDifficulty.EASY, SocialNetworkDecayRates.NORMAL);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();
		giver.setControl(0.0);
		target.setControl(0.0);

		//if they SocialNPCs are not friends, nothing should happen
		quest.questFailed();

		assertEquals(SocialQuestState.FAILURE, quest.getCurrentState());
		assertFalse(giver.getFriends().contains(target));
		assertFalse(target.getFriends().contains(giver));

		//if the SocialNPCs are friends, then their relationship should take a hit
		giver.addFriend(target, giverFeels);
		target.addFriend(giver, targetFeels);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);

		quest.questFailed();

		assertEquals(20, giverFeels.getIntimacy());
		assertEquals(20, targetFeels.getIntimacy());
		assertEquals(SocialQuestState.FAILURE, quest.getCurrentState());

		//make sure that control properly affects the amount of intimacy lost
		giver.setControl(1.0);
		target.setControl(1.0);
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		
		quest.questFailed();
		
		assertEquals(95, giverFeels.getIntimacy());
		assertEquals(95, targetFeels.getIntimacy());
		
		giver.setControl(0.5);
		target.setControl(0.5);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		
		quest.questFailed();
		
		assertEquals(85, giverFeels.getIntimacy());
		assertEquals(85, targetFeels.getIntimacy());
		
		giver.getQuestGenerator().clear();
	}
	
	/**
	 * This test makes sure that the consequences of a GiftQuest only happen once.
	 */
	@Test
	public void testDuplicateCompletions()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockGift gift = new MockGift(1, 100); //1 cm^3, 100 grams
		GiftQuest quest = new GiftQuest("Test GiftQuest", giver, target, gift, SocialQuestDifficulty.EASY, SocialNetworkDecayRates.NORMAL);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();
		giver.addFriend(target, giverFeels);
		target.addFriend(giver, targetFeels);
		
		quest.questSuccessful();
		
		assertEquals(50, giverFeels.getIntimacy());
		assertEquals(50, giverFeels.getIntimacy());
		
		quest.questSuccessful();
		
		assertEquals(50, giverFeels.getIntimacy());
		assertEquals(50, giverFeels.getIntimacy());
		
		quest.questFailed();
		
		assertEquals(50, giverFeels.getIntimacy());
		assertEquals(50, giverFeels.getIntimacy());
		
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		quest.questFailed();
		
		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, giverFeels.getIntimacy());
		
		giver.getQuestGenerator().clear();
	}
}

class MockGift extends Item
{

	public MockGift(int size, int weight)
	{
		super("MockGift", "A MockItem used in TestGiftQuest", size, weight);
	}
}
