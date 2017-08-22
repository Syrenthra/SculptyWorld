package sw.quest;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.item.Item;
import sw.lifeform.Player;
import sw.lifeform.SocialNPC;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.Moods;
import sw.socialNetwork.SocialQuestState;

/**
 * 
 * @author David Abrams
 * 
 * This set of tests makes sure that SocialQuest behaves properly.
 */
public class TestSocialQuest
{
	/**
	 * Make sure that a SocialQuest can be initialized properly and that the getters and setters
	 * work.
	 */
	@Test
	public void testInit()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockSocialQuest quest = new MockSocialQuest("Test", "Desc", giver, target, SocialQuestDifficulty.EASY);

		assertEquals("Test", quest.getName());
		assertEquals("Desc", quest.getDescription());
		assertEquals(giver, quest.getQuestGiver());
		assertEquals(target, quest.getQuestTarget());
		assertEquals(-1, quest.getTimeToCompleteRemaining());
		assertEquals(-1, quest.getTimeToHoldRemaining());
		assertEquals(SocialQuestState.IN_PROGRESS, quest.getCurrentState());
		assertEquals(SocialQuestDifficulty.EASY, quest.getDifficulty());
		assertEquals(1000, quest.calculateReward(null));

		quest.setTimeToCompleteRemaining(120);
		quest.setTimeToHoldRemaining(100);
		quest.setDifficulty(SocialQuestDifficulty.YOUMUSTBEPRO);

		assertEquals(120, quest.getTimeToCompleteRemaining());
		assertEquals(100, quest.getTimeToHoldRemaining());
		assertEquals(SocialQuestDifficulty.YOUMUSTBEPRO, quest.getDifficulty());

		quest.setCurrentState(SocialQuestState.FAILURE);

		assertEquals(SocialQuestState.FAILURE, quest.getCurrentState());

		quest.setCurrentState(SocialQuestState.SUCCESS);

		assertEquals(SocialQuestState.SUCCESS, quest.getCurrentState());
		
		giver.getQuestGenerator().clear();
	}

	/**
	 * Makes sure that the time-related pieces of SocialQuest work properly.
	 */
	@Test
	public void testTimeSensitivity()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockPlayer player = new MockPlayer();
		MockSocialQuest quest = new MockSocialQuest("Test", "Desc", giver, target, SocialQuestDifficulty.EASY);

		quest.updateTime("", 0);

		assertEquals(-1, quest.getTimeToHoldRemaining());
		assertEquals(-1, quest.getTimeToCompleteRemaining());

		quest.addPlayer(player);
		quest.updateTime("", 0);

		assertEquals(-1, quest.getTimeToHoldRemaining());
		assertEquals(-1, quest.getTimeToCompleteRemaining());

		quest.removePlayer(player);

		quest.setTimeToHoldRemaining(100);
		quest.setTimeToCompleteRemaining(500);

		quest.updateTime("", 0);

		assertEquals(99, quest.getTimeToHoldRemaining());
		assertEquals(500, quest.getTimeToCompleteRemaining());

		quest.addPlayer(player);

		/**
		 * a player has accepted the quest, so now timeToHold should freeze and timeToComplete
		 * should start to count down
		 */

		quest.updateTime("", 0);

		assertEquals(99, quest.getTimeToHoldRemaining());
		assertEquals(499, quest.getTimeToCompleteRemaining());
		
		//make sure that a quest can expire
		
		quest.setTimeToCompleteRemaining(1);
		quest.updateTime("", 0);
		
		assertEquals(0, quest.getTimeToCompleteRemaining());
		assertEquals(SocialQuestState.FAILURE, quest.getCurrentState());
		
		giver.getQuestGenerator().clear();
	}

	/**
	 * Make sure that the type of completion (success/fail) is properly tracked.
	 */
	@Test
	public void testQuestCompletion()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC target = new MockSocialNPC();
		MockSocialQuest quest1 = new MockSocialQuest("Test", "Desc", giver, target, SocialQuestDifficulty.EASY);
		MockSocialQuest quest2 = new MockSocialQuest("Test", "Desc", giver, target, SocialQuestDifficulty.EASY);
		
		//initialized as IN_PROGRESS
		assertEquals(SocialQuestState.IN_PROGRESS, quest1.getCurrentState());
		assertEquals(SocialQuestState.IN_PROGRESS, quest2.getCurrentState());

		quest1.questFailed();
		assertEquals(SocialQuestState.FAILURE, quest1.getCurrentState());
		
		giver.setGrumpiness(0.0);
		
		quest2.questSuccessful();
		assertEquals(SocialQuestState.SUCCESS, quest2.getCurrentState());
		
		giver.getQuestGenerator().clear();
	}
	
	/**
	 * Tests to make sure that turning in a quest works properly. A SocialQuest has 3 criteria
	 * in order to be turned in: the giver must still be handing the quest out, the quest must
	 * have either been successful or failed (not still in progress), and the player must have
	 * the quest.
	 */
	@Test
	public void testTurnInQuest()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		MockPlayer player = new MockPlayer();
		MockSocialQuest quest = new MockSocialQuest("Test", "Desc", bob, bill, SocialQuestDifficulty.EASY);
		
		//Bob's last quest result starts as null. Can use this to test quest turning in.
		assertNull(bob.getLastQuestResult());
		
		//can't turn in a quest the giver isn't handing out
		quest.addPlayer(player);
		quest.setCurrentState(SocialQuestState.SUCCESS);
		
		quest.turnInQuest(player);
		
		//Last quest result should still be null because the quest is not turned in
		assertNull(bob.getLastQuestResult());
		
		//can't turn in a quest that is still in progress
		quest.setCurrentState(SocialQuestState.IN_PROGRESS);
		bob.addQuest(quest);
		
		quest.turnInQuest(player);
		
		assertNull(bob.getLastQuestResult());
		
		//can't turn in a quest that the player doesn't have
		quest.setCurrentState(SocialQuestState.SUCCESS);
		quest.removePlayer(player);
		
		quest.turnInQuest(player);
		
		assertNull(bob.getLastQuestResult());
		
		bob.getQuestGenerator().clear();
		
		//quest should be turned in when all 3 criteria are met
		quest.addPlayer(player);
		
		quest.turnInQuest(player);
		
		assertEquals(SocialQuestState.SUCCESS, bob.getLastQuestResult());
	}
}

class MockSocialQuest extends SocialQuest
{
	public MockSocialQuest(String name, String desc, SocialNPC questGiver, SocialNPC target, SocialQuestDifficulty difficulty)
	{
		super(name, desc, questGiver, target, difficulty);
		// TODO Auto-generated constructor stub
	}
}

class MockPlayer extends Player
{
	public MockPlayer()
	{
		super(0, "Mocky", "The MockPlayer", 100);
		// TODO Auto-generated constructor stub
	}
}

class MockItem extends Item
{
	public MockItem(String name, String desc, int size, int weight)
	{
		super(name, desc, size, weight);
	}

	public MockItem()
	{
		super("MockItem", "MockItem description", 1, 1);
	}
}

class MockSocialNPC extends SocialNPC
{
	boolean moodChanged = false;

	public MockSocialNPC()
	{
		super(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		// TODO Auto-generated constructor stub
	}
}

class MockFeelings extends Feelings
{

}