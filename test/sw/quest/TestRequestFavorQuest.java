package sw.quest;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author David Abrams
 * 
 * This set of tests makes sure that RequestFavorQuest behaves properly.
 */
public class TestRequestFavorQuest
{
	/**
	 * This test makes sure that RequestFavorQuest can be properly initialized and that all the
	 * getters and setters work properly.
	 */
	@Test
	public void testInit()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockItem objective = new MockItem();
		RequestFavorQuest quest = new RequestFavorQuest("da quest", giver, target, objective, SocialQuestDifficulty.EASY);

		assertEquals(giver, quest.getQuestGiver());
		assertEquals(target, quest.getQuestTarget());
		assertEquals(objective, quest.getFavorTarget());
		
		giver.getQuestGenerator().clear();
	}

	/**
	 * This test makes sure that RequestFavorQuest does all the right things when it is successfully
	 * completed. The only thing that a RequestFavorQuest does differently from a FavorQuest is
	 * incrementing the trust counter in the relationship.
	 */
	@Test
	public void testSuccess()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockItem objective = new MockItem();
		RequestFavorQuest quest = new RequestFavorQuest("da quest", giver, target, objective, SocialQuestDifficulty.EASY);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();

		//nothing should happen if the giver and target aren't friends
		quest.questSuccessful();

		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		//nothing should happen if only one SocialNPC is friends with the other
		giver.addFriend(target, giverFeels);
		quest.questSuccessful();

		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		giver.removeFriend(target);
		target.addFriend(giver, targetFeels);
		quest.questSuccessful();

		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		/**
		 * if both SocialNPCs are friends, then successfully completing the quest should increment
		 * the trust counter up to 3, at which point trust should start to increase
		 */
		giver.addFriend(target, giverFeels);

		for (int i = 1; i <= 3; i++)
		{
			quest.questSuccessful();

			assertEquals(0, giverFeels.getTrust());
			assertEquals(i, giverFeels.getTrend());
			assertEquals(0, targetFeels.getTrust());
			assertEquals(i, targetFeels.getTrend());
		}

		quest.questSuccessful();

		assertEquals(1, giverFeels.getTrust());
		assertEquals(3, giverFeels.getTrend());
		assertEquals(1, targetFeels.getTrust());
		assertEquals(3, targetFeels.getTrend());

		giver.getQuestGenerator().clear();
	}

	/**
	 * This test makes sure that RequestFavorQuest does all the right things when it is failed. The
	 * only functional difference between
	 */
	@Test
	public void testFailure()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockItem objective = new MockItem();
		RequestFavorQuest quest = new RequestFavorQuest("da quest", giver, target, objective, SocialQuestDifficulty.EASY);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();

		//nothing should happen if the giver and target aren't friends
		quest.questFailed();

		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		//nothing should happen if only one SocialNPC is friends with the other
		giver.addFriend(target, giverFeels);
		quest.questFailed();

		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		giver.removeFriend(target);
		target.addFriend(giver, targetFeels);
		quest.questFailed();

		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		/**
		 * if both SocialNPCs are friends, then successfully completing the quest should increment
		 * the trust counter up to 3, at which point trust should start to increase
		 */
		giver.addFriend(target, giverFeels);

		for (int i = 1; i <= 3; i++)
		{
			quest.questFailed();

			assertEquals(0, giverFeels.getTrust());
			assertEquals(-i, giverFeels.getTrend());
			assertEquals(0, targetFeels.getTrust());
			assertEquals(-i, targetFeels.getTrend());
		}

		quest.questFailed();

		assertEquals(-1, giverFeels.getTrust());
		assertEquals(-3, giverFeels.getTrend());
		assertEquals(-1, targetFeels.getTrust());
		assertEquals(-3, targetFeels.getTrend());
		
		giver.getQuestGenerator().clear();
	}

}
