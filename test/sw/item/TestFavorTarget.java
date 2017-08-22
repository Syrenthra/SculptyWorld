package sw.item;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import sw.item.FavorTarget;
import sw.lifeform.Player;
import sw.lifeform.SocialNPC;
import sw.quest.FavorQuest;
import sw.quest.SocialQuestDifficulty;
import sw.socialNetwork.SocialQuestState;

public class TestFavorTarget
{

	/**
	 * Make sure that the FavorTarget can be used to complete the quest.
	 */
	@Test
	public void testPerformFavorAction()
	{
		MockPlayer player = new MockPlayer();
		MockFavorTarget target = new MockFavorTarget();
		MockFavorQuest quest = new MockFavorQuest(target);
	
		//if there are no quests associated with the FavorTarget, then nothing should happen
		target.performFavorAction(player);
		assertEquals(SocialQuestState.IN_PROGRESS, quest.getCurrentState());
		
		target.addQuest(quest);
		
		//if the player does not have the quest, nothing should happen
		target.performFavorAction(player);
		assertEquals(SocialQuestState.IN_PROGRESS, quest.getCurrentState());
		
		//only if the player and FavorTarget both have the quest should the quest be completed
		player.addQuest(quest);
		target.performFavorAction(player);
		assertEquals(SocialQuestState.SUCCESS, quest.getCurrentState());
	}

	@Test
	public void testQuestFunctions()
	{
		MockFavorTarget target = new MockFavorTarget();
		MockFavorQuest quest = new MockFavorQuest(target);
		
		target.addQuest(quest);
		
		assertTrue(target.hasQuest(quest));
		
		target.removeQuest(quest);
		
		assertFalse(target.hasQuest(quest));
	}
	
	
}

class MockFavorTarget extends FavorTarget
{
	MockFavorTarget()
	{
        quests = new Vector<FavorQuest>();
	}
}

class MockFavorQuest extends FavorQuest
{
	public MockFavorQuest(FavorTarget objective)
	{		
		super("Da quest",  new MockSocialNPC(),  new MockSocialNPC(), objective, SocialQuestDifficulty.EASY);
		// TODO Auto-generated constructor stub
	}
}

class MockPlayer extends Player
{
	public MockPlayer()
	{
		super(0, "Mocky", "The mock player", 5);
		// TODO Auto-generated constructor stub
	}
}

class MockSocialNPC extends SocialNPC
{

	public MockSocialNPC()
	{
		super(0, "Mockerton", "A mock SocialNPC", 5, 1, 1, 1);
		// TODO Auto-generated constructor stub
	}
	
}