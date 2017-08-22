package sw.quest.reward;

import static org.junit.Assert.*;

import mock.MockFeelings;

import org.junit.Test;

import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.quest.reward.FavorReward;
import sw.socialNetwork.SocialNetwork;

/**
 * @author David Abrams and Dr. Girard
 * 
 * The purpose of this set of tests is to make sure that FavorQuest behaves properly.
 */
public class TestFavorReward
{
	/**
	 * This test makes sure that a FavorQuest can be properly initialized and that the getters and
	 * setters work properly.
	 */
	@Test
	public void testInit()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        
        Quest quest = new Quest("Quest","Description",giver);

		FavorReward reward = new FavorReward(quest, target, SocialCapitolCost.CHEAP);
		quest.addReward(reward);

		assertEquals(target, reward.getTarget());

		assertEquals(quest,reward.m_quest);
		assertEquals(SocialCapitolCost.CHEAP,reward.getCost());
		assertFalse(reward.hasItemReward());
	}

	/**
	 * This test makes sure that a FavorQuest performs the correct actions upon being successfully
	 * completed.
	 */
	@Test
	public void testSuccess()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
	    SocialNetwork giverNetwork = giver.getSocialNetwork();
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        SocialNetwork targetNetwork = target.getSocialNetwork();

        Quest quest = new Quest("Quest","Description",giver);
        FavorReward reward = new FavorReward(quest, target, SocialCapitolCost.CHEAP);
        quest.addReward(reward);
        
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();
		giverNetwork.setControl(0.0);
		targetNetwork.setControl(0.0);

		//nothing should happen if both SocialNPCs aren't friends
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giverNetwork.addFriend(target, giverFeels);
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//if they are friends, then their relationship should increase accordingly
		targetNetwork.addFriend(giver, targetFeels);

		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(47, giverFeels.getIntimacy());
		assertEquals(47, targetFeels.getIntimacy());
		assertEquals(250, targetFeels.getSocialDebtOwed());
		assertEquals(-250, giverFeels.getSocialDebtOwed());

		//every difficulty level should work properly
		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		giverFeels.setSocialDebtOwed(0);
		targetFeels.setSocialDebtOwed(0);

		Quest quest1 = new Quest("Quest1","Description",giver);
		FavorReward r1 = new FavorReward(quest1, target, SocialCapitolCost.MEDIUM);
		
		Quest quest2 = new Quest("Quest2","Description",giver);
		FavorReward r2 = new FavorReward(quest2, target, SocialCapitolCost.EXPENSIVE);
		
		Quest quest3 = new Quest("Quest3","Description",giver);
		FavorReward r3 = new FavorReward(quest3, target, SocialCapitolCost.EXTREME);

		r1.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(34, targetFeels.getIntimacy());
		assertEquals(34, giverFeels.getIntimacy());
		assertEquals(500, targetFeels.getSocialDebtOwed());
		assertEquals(-500, giverFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		giverFeels.setSocialDebtOwed(0);
		targetFeels.setSocialDebtOwed(0);

		r2.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(36, targetFeels.getIntimacy());
		assertEquals(36, giverFeels.getIntimacy());
		assertEquals(750, targetFeels.getSocialDebtOwed());
		assertEquals(-750, giverFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		giverFeels.setSocialDebtOwed(0);
		targetFeels.setSocialDebtOwed(0);

		r3.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(40, targetFeels.getIntimacy());
		assertEquals(40, giverFeels.getIntimacy());
		assertEquals(1250, targetFeels.getSocialDebtOwed());
		assertEquals(-1250, giverFeels.getSocialDebtOwed());
	}

	/**
	 * This test makes sure that a FavorQuest performs the right actions upon being failed.
	 */
	@Test
	public void testFailure()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        SocialNetwork giverNetwork = giver.getSocialNetwork();
        NPC target = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        SocialNetwork targetNetwork = target.getSocialNetwork();
        
        Quest quest = new Quest("Quest","Description",giver);
        FavorReward reward = new FavorReward(quest, target, SocialCapitolCost.CHEAP);
        quest.addReward(reward);
        
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();
		giverNetwork.setControl(0.0);
		targetNetwork.setControl(0.0);

		//if the SocialNPCs aren't friends, nothing should happen
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//if only one SocialNPC is friends with the other, nothing should happen
		giverNetwork.addFriend(target, giverFeels);
		quest.setCurrentState(QuestState.IN_PROGRESS);
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giver.removeFriend(target);
		targetNetwork.addFriend(giver, targetFeels);
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//if the SocialNPCs are friends, then their relationship should take the proper hit
		giverNetwork.addFriend(target, giverFeels);
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		quest.setCurrentState(QuestState.IN_PROGRESS);
		reward.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(88, giverFeels.getIntimacy());
		assertEquals(88, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//every level of difficulty should work properly
	      Quest quest1 = new Quest("Quest1","Description",giver);
	        FavorReward r1 = new FavorReward(quest1, target, SocialCapitolCost.MEDIUM);
	        
	        Quest quest2 = new Quest("Quest2","Description",giver);
	        FavorReward r2 = new FavorReward(quest2, target, SocialCapitolCost.EXPENSIVE);
	        
	        Quest quest3 = new Quest("Quest3","Description",giver);
	        FavorReward r3 = new FavorReward(quest3, target, SocialCapitolCost.EXTREME);

		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		r1.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(86, giverFeels.getIntimacy());
		assertEquals(86, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		r2.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(84, giverFeels.getIntimacy());
		assertEquals(84, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		r3.failedQuest();

		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(80, giverFeels.getIntimacy());
		assertEquals(80, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());
		
		//make sure that a SocialNPC's control has the right affect on the penalty of failure
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		quest.setCurrentState(QuestState.IN_PROGRESS);
		giverNetwork.setControl(1.0);
		targetNetwork.setControl(1.0);
		reward.failedQuest();
		
		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(96, giverFeels.getIntimacy());
		assertEquals(96, targetFeels.getIntimacy());
		
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		quest.setCurrentState(QuestState.IN_PROGRESS);
		giverNetwork.setControl(0.5);
		targetNetwork.setControl(0.5);
		reward.failedQuest();
	
		assertEquals(QuestState.FAILED, giver.getLastQuestResult());
		assertEquals(92, giverFeels.getIntimacy());
		assertEquals(92, targetFeels.getIntimacy());
	}
}
