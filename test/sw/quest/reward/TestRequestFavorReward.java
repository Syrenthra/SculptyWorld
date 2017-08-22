package sw.quest.reward;

import static org.junit.Assert.*;

import mock.MockFeelings;

import org.junit.Test;

import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.quest.reward.RequestFavorReward;
import sw.socialNetwork.SocialNetwork;

/**
 * @author David Abrams and Dr. Girard
 * 
 * This set of tests makes sure that RequestFavorQuest behaves properly.
 */
public class TestRequestFavorReward
{
	/**
	 * This test makes sure that RequestFavorQuest can be properly initialized and that all the
	 * getters and setters work properly.
	 */
	@Test
	public void testInit()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        
        Quest quest = new Quest("Quest","Description",giver);
        RequestFavorReward reward = new RequestFavorReward(quest, target, SocialCapitolCost.CHEAP);
        quest.addReward(reward);

        assertEquals(target, reward.getTarget());

        assertEquals(quest,reward.m_quest);
        assertEquals(SocialCapitolCost.CHEAP,reward.getCost());
        assertFalse(reward.hasItemReward());
	}

	/**
	 * This test makes sure that RequestFavorQuest does all the right things when it is successfully
	 * completed. The only thing that a RequestFavorQuest does differently from a FavorQuest is
	 * incrementing the trust counter in the relationship.
	 */
	@Test
	public void testSuccess()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        SocialNetwork giverNetwork = giver.getSocialNetwork();
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        SocialNetwork targetNetwork = target.getSocialNetwork();
        
        Quest quest = new Quest("Quest","Description",giver);
        RequestFavorReward reward = new RequestFavorReward(quest, target, SocialCapitolCost.CHEAP);
        quest.addReward(reward);
        
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();

		//nothing should happen if the giver and target aren't friends
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		//nothing should happen if only one SocialNPC is friends with the other
		giverNetwork.addFriend(target, giverFeels);
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		giver.removeFriend(target);
		targetNetwork.addFriend(giver, targetFeels);
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		/**
		 * if both SocialNPCs are friends, then successfully completing the quest should increment
		 * the trust counter up to 3, at which point trust should start to increase
		 */
		giverNetwork.addFriend(target, giverFeels);

		for (int i = 1; i <= 3; i++)
		{
		    reward.getItemReward();

		    assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
			assertEquals(0, giverFeels.getTrust());
			assertEquals(i, giverFeels.getTrend());
			assertEquals(0, targetFeels.getTrust());
			assertEquals(i, targetFeels.getTrend());
		}

		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(1, giverFeels.getTrust());
		assertEquals(3, giverFeels.getTrend());
		assertEquals(1, targetFeels.getTrust());
		assertEquals(3, targetFeels.getTrend());
	}

	/**
	 * This test makes sure that RequestFavorQuest does all the right things when it is failed. The
	 * only functional difference between
	 */
	@Test
	public void testFailure()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
	    SocialNetwork giverNetwork = giver.getSocialNetwork();
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        SocialNetwork targetNetwork = target.getSocialNetwork();
		
		Quest quest = new Quest("Quest","Description",giver);
        RequestFavorReward reward = new RequestFavorReward(quest, target, SocialCapitolCost.CHEAP);
        quest.addReward(reward);
        
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();

		//nothing should happen if the giver and target aren't friends
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		//nothing should happen if only one SocialNPC is friends with the other
		giverNetwork.addFriend(target, giverFeels);
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		giver.removeFriend(target);
		targetNetwork.addFriend(giver, targetFeels);
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(0, giverFeels.getTrust());
		assertEquals(0, giverFeels.getTrend());
		assertEquals(0, targetFeels.getTrust());
		assertEquals(0, targetFeels.getTrend());

		/**
		 * if both SocialNPCs are friends, then successfully completing the quest should increment
		 * the trust counter up to 3, at which point trust should start to increase
		 */
		giverNetwork.addFriend(target, giverFeels);

		for (int i = 1; i <= 3; i++)
		{
		    reward.failedQuest();

		    assertEquals(QuestState.FAILED, giver.getLastQuestResult());
			assertEquals(0, giverFeels.getTrust());
			assertEquals(-i, giverFeels.getTrend());
			assertEquals(0, targetFeels.getTrust());
			assertEquals(-i, targetFeels.getTrend());
		}

		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(-1, giverFeels.getTrust());
		assertEquals(-3, giverFeels.getTrend());
		assertEquals(-1, targetFeels.getTrust());
		assertEquals(-3, targetFeels.getTrend());
	}

}
