package sw.quest;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.socialNetwork.SocialQuestState;

/**
 * @author David Abrams
 * 
 * The purpose of this set of tests is to make sure that FavorQuest behaves properly.
 */
public class TestFavorQuest
{
	/**
	 * This test makes sure that a FavorQuest can be properly initialized and that the getters and
	 * setters work properly.
	 */
	@Test
	public void testInit()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockItem objective = new MockItem();
		MockItem obj2 = new MockItem();
		FavorQuest quest = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.EASY);

		assertEquals(objective, quest.getFavorTarget());

		quest.setFavorTarget(obj2);

		assertEquals(obj2, quest.getFavorTarget());
		
		giver.getQuestGenerator().clear();
	}

	/**
	 * This test makes sure that a FavorQuest performs the correct actions upon being successfully
	 * completed.
	 */
	@Test
	public void testSuccess()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockItem objective = new MockItem();
		FavorQuest quest = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.EASY);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();
		giver.setControl(0.0);
		target.setControl(0.0);

		//nothing should happen if both SocialNPCs aren't friends
		quest.questSuccessful();

		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giver.addFriend(target, giverFeels);
		quest.questSuccessful();

		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//if they are friends, then their relationship should increase accordingly
		target.addFriend(giver, targetFeels);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		quest.questSuccessful();

		assertEquals(47, giverFeels.getIntimacy());
		assertEquals(47, targetFeels.getIntimacy());
		assertEquals(250, targetFeels.getSocialDebtOwed());
		assertEquals(-250, giverFeels.getSocialDebtOwed());

		//every difficulty level should work properly
		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		giverFeels.setSocialDebtOwed(0);
		targetFeels.setSocialDebtOwed(0);

		FavorQuest q1 = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.MEDIUM);
		FavorQuest q2 = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.HARD);
		FavorQuest q3 = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.YOUMUSTBEPRO);

		q1.questSuccessful();

		assertEquals(34, targetFeels.getIntimacy());
		assertEquals(34, giverFeels.getIntimacy());
		assertEquals(500, targetFeels.getSocialDebtOwed());
		assertEquals(-500, giverFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		giverFeels.setSocialDebtOwed(0);
		targetFeels.setSocialDebtOwed(0);

		q2.questSuccessful();

		assertEquals(36, targetFeels.getIntimacy());
		assertEquals(36, giverFeels.getIntimacy());
		assertEquals(750, targetFeels.getSocialDebtOwed());
		assertEquals(-750, giverFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(20);
		targetFeels.setIntimacy(20);
		giverFeels.setSocialDebtOwed(0);
		targetFeels.setSocialDebtOwed(0);

		q3.questSuccessful();

		assertEquals(40, targetFeels.getIntimacy());
		assertEquals(40, giverFeels.getIntimacy());
		assertEquals(1250, targetFeels.getSocialDebtOwed());
		assertEquals(-1250, giverFeels.getSocialDebtOwed());
		
		giver.getQuestGenerator().clear();
	}

	/**
	 * This test makes sure that a FavorQuest performs the right actions upon being failed.
	 */
	@Test
	public void testFailure()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockItem objective = new MockItem();
		FavorQuest quest = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.EASY);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();
		giver.setControl(0.0);
		target.setControl(0.0);

		//if the SocialNPCs aren't friends, nothing should happen
		quest.questFailed();

		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//if only one SocialNPC is friends with the other, nothing should happen
		giver.addFriend(target, giverFeels);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		quest.questFailed();

		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giver.removeFriend(target);
		target.addFriend(giver, targetFeels);
		quest.questFailed();

		assertEquals(35, giverFeels.getIntimacy());
		assertEquals(35, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//if the SocialNPCs are friends, then their relationship should take the proper hit
		giver.addFriend(target, giverFeels);
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		quest.questFailed();

		assertEquals(88, giverFeels.getIntimacy());
		assertEquals(88, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		//every level of difficulty should work properly
		FavorQuest q1 = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.MEDIUM);
		FavorQuest q2 = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.HARD);
		FavorQuest q3 = new FavorQuest("TestFavorQuest", giver, target, objective, SocialQuestDifficulty.YOUMUSTBEPRO);

		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		q1.questFailed();

		assertEquals(86, giverFeels.getIntimacy());
		assertEquals(86, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		q2.questFailed();

		assertEquals(84, giverFeels.getIntimacy());
		assertEquals(84, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());

		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		q3.questFailed();

		assertEquals(80, giverFeels.getIntimacy());
		assertEquals(80, targetFeels.getIntimacy());
		assertEquals(0, giverFeels.getSocialDebtOwed());
		assertEquals(0, targetFeels.getSocialDebtOwed());
		
		//make sure that a SocialNPC's control has the right affect on the penalty of failure
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		giver.setControl(1.0);
		target.setControl(1.0);
		quest.questFailed();
		
		assertEquals(96, giverFeels.getIntimacy());
		assertEquals(96, targetFeels.getIntimacy());
		
		giverFeels.setIntimacy(100);
		targetFeels.setIntimacy(100);
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		giver.setControl(0.5);
		target.setControl(0.5);
		quest.questFailed();
		
		assertEquals(92, giverFeels.getIntimacy());
		assertEquals(92, targetFeels.getIntimacy());
		
		giver.getQuestGenerator().clear();
	}
	
	/**
	 * This test makes sure that the consequences of a FavorQuest only happen once.
	 */
	@Test
	public void testDuplicateCompletions()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockGift gift = new MockGift(1, 100); //1 cm^3, 100 grams
		FavorQuest quest = new FavorQuest("Test GiftQuest", giver, target, gift, SocialQuestDifficulty.EASY);
		MockFeelings giverFeels = new MockFeelings();
		MockFeelings targetFeels = new MockFeelings();
		giver.addFriend(target, giverFeels);
		target.addFriend(giver, targetFeels);
		giver.setControl(0.0);
		target.setControl(0.0);
		
		quest.questSuccessful();
		
		assertEquals(47, giverFeels.getIntimacy());
		
		quest.questSuccessful();
		
		assertEquals(47, giverFeels.getIntimacy());
		
		quest.questFailed();
		
		assertEquals(47, giverFeels.getIntimacy());
		
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		quest.questFailed();
		
		assertEquals(35, giverFeels.getIntimacy());
		
		giver.getQuestGenerator().clear();
	}
}
