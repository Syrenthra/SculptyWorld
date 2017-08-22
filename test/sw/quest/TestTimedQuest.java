package sw.quest;

import static org.junit.Assert.*;

import mock.MockQuestReward;

import org.junit.Test;

import sw.lifeform.PC;
import sw.lifeform.NPC;

/**
 * 
 * @author David Abrams
 * 
 * This set of tests makes sure that SocialQuest behaves properly.
 */
public class TestTimedQuest
{
    /**
     * For later if we decide to use a specific clock and have the quest test for that clock.
     */
    String questClock = "";
    
	/**
	 * Make sure that a SocialQuest can be initialized properly and that the getters and setters
	 * work.
	 */
	@Test
	public void testInitAndSetters()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        
		TimedQuest quest = new TimedQuest("Test", "Desc", giver);

		assertEquals("Test", quest.getName());
		assertEquals("Desc", quest.getDescription());
		assertEquals(giver, quest.getGranter());
		assertEquals(-1,quest.getTimeToHoldRemaining());
		assertEquals(-1, quest.getTimeToCompleteRemaining());
		assertEquals(QuestState.INACTIVE, quest.getCurrentState());

		quest.setTimeToCompleteRemaining(120);
		quest.setTimeToHoldRemaining(100);
		
		// TODO: Need to create a max for Time to Complete so we can reset to that.

		assertEquals(120, quest.getTimeToCompleteRemaining());
		assertEquals(100, quest.getTimeToHoldRemaining());
		assertEquals(120, quest.m_maxTimeToComplete);
	}

	/**
	 * Makes sure that the time-related pieces of SocialQuest work properly.
	 */
	@Test
	public void testTimeSensitivity()
	{
	    NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        
        PC player = new PC(1,"Dude","Desc",50);
        
		TimedQuest quest = new TimedQuest("Test", "Desc", giver);
		MockQuestReward reward = new MockQuestReward();
		quest.addReward(reward);

		quest.updateTime(questClock, 0);

		assertEquals(-1, quest.getTimeToHoldRemaining());
		assertEquals(-1, quest.getTimeToCompleteRemaining());

		quest.addPlayer(player);
		quest.updateTime(questClock, 0);

		assertEquals(-1, quest.getTimeToHoldRemaining());
		assertEquals(-1, quest.getTimeToCompleteRemaining());

		quest.removePlayer(player);

		quest.setTimeToHoldRemaining(100);
		quest.setTimeToCompleteRemaining(500);

		quest.updateTime(questClock, 0);

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
		
		quest.removePlayer(player);
		
		quest.updateTime(questClock, 0);
		
		assertEquals(98, quest.getTimeToHoldRemaining());
        assertEquals(500, quest.getTimeToCompleteRemaining()); // This should have reset.
		
	}
	
	@Test
	public void testQuestFailsDueToTimeToCompleteRunningOut()
	{
	       NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
	        
	        PC player = new PC(1,"Dude","Desc",50);
	        
	        TimedQuest quest = new TimedQuest("Test", "Desc", giver);
	        MockQuestReward reward = new MockQuestReward();
	        reward.quest = quest;
	        quest.addReward(reward);
	        
	        quest.addPlayer(player);
	        
	        quest.setTimeToCompleteRemaining(1);

	        quest.updateTime(questClock, 0);
	        
	        assertEquals(0, quest.getTimeToCompleteRemaining());
	        assertEquals(QuestState.FAILED, quest.getCurrentState());
	        assertEquals(0,quest.getPlayers().size());
	        assertEquals(QuestState.FAILED, giver.getLastQuestResult()); // This will be set by the Reward.
	}
	
	@Test
	public void testQuestFailsDueToTimeToHoldRunningOut()
	{
        NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        
        TimedQuest quest = new TimedQuest("Test", "Desc", giver);
        MockQuestReward reward = new MockQuestReward();
        reward.quest = quest;
        quest.addReward(reward);
        giver.addQuest(quest);
        
        quest.setTimeToHoldRemaining(1);

        quest.updateTime(questClock, 0);
        
        assertEquals(0, quest.getTimeToHoldRemaining());
        assertEquals(QuestState.FAILED, quest.getCurrentState());
        assertEquals(0,quest.getPlayers().size());
        assertEquals(QuestState.FAILED, giver.getLastQuestResult()); // This will be set by the Reward.
        assertEquals(0,giver.getAvailableQuests().size()); // Should not be available anymore.
	}
}