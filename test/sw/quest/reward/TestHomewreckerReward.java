package sw.quest.reward;

import static org.junit.Assert.*;

import java.util.ArrayList;

import mock.MockItem;
import mock.MockQuestReward;
import mock.MockTask;

import org.junit.Test;

import sw.item.HandLocation;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.quest.reward.HomewreckerReward;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FeelingsAttributes;

/**
 * @author David Abrams and Dr. Girard
 * 
 * This set of tests makes sure that HomewreckerQuest behaves properly. There is no test to check
 * the failure of HomewreckerQuest. This is because the quest does nothing special if failed, so
 * it just uses questFailed() in SocialQuest.
 */
public class TestHomewreckerReward
{
    

	/**
	 * This test makes sure that a HomewreckerQuest can be initialized properly and that all the
	 * getters and setters work.
	 */
	@Test
	public void testInit()
	{
	    NPC giver = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC intermediateFriend = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		Feelings target1 = new Feelings();
		Feelings target2 = new Feelings();
		ArrayList<Feelings> relationship = new ArrayList<Feelings>();
		relationship.add(target1);
		relationship.add(target2);
		Quest quest = new Quest("Quest","Description",giver);
		HomewreckerReward reward = new HomewreckerReward(quest, intermediateFriend, relationship, SocialCapitolCost.CHEAP, FeelingsAttributes.INTIMACY);

		ArrayList<Feelings> holder = reward.getTargetRelationship();
		assertEquals(target1, holder.get(0));
		assertEquals(target2, holder.get(1));
		assertEquals(FeelingsAttributes.INTIMACY, reward.getTargetAttribute());
		assertFalse(reward.hasItemReward());
	}

	/**
	 * This test makes sure that a HomewreckerQuest will properly change the intimacy of the target
	 * relationship upon successful completion.
	 */
	@Test
	public void testSuccessfulReductionInIntimacy()
	{
		NPC giver = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		NPC intermediateFriend = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		Feelings target1 = new Feelings();
		Feelings target2 = new Feelings();
		ArrayList<Feelings> relationship = new ArrayList<Feelings>();
		relationship.add(target1);
		relationship.add(target2);
		Quest quest = new Quest("Quest","Description",giver);
		HomewreckerReward reward = new HomewreckerReward(quest, intermediateFriend, relationship, SocialCapitolCost.CHEAP, FeelingsAttributes.INTIMACY);

		target1.setIntimacy(100);
		target2.setIntimacy(100);

		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(88, target1.getIntimacy());
		assertEquals(88, target2.getIntimacy());

		reward.setCost(SocialCapitolCost.MEDIUM);

		target1.setIntimacy(100);
		target2.setIntimacy(100);
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(86, target1.getIntimacy());
		assertEquals(86, target2.getIntimacy());

		reward.setCost(SocialCapitolCost.EXPENSIVE);

		target1.setIntimacy(100);
		target2.setIntimacy(100);
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(84, target1.getIntimacy());
		assertEquals(84, target2.getIntimacy());

		reward.setCost(SocialCapitolCost.EXTREME);

		target1.setIntimacy(100);
		target2.setIntimacy(100);
		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(80, target1.getIntimacy());
		assertEquals(80, target2.getIntimacy());
	}

	/**
	 * This test makes sure that HomewreckerQuest will properly change the trust of the target
	 * relationship upon successful completion.
	 */
	@Test
	public void testSuccessTrust()
	{
		NPC giver = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		NPC intermediateFriend = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		Feelings target1 = new Feelings();
		Feelings target2 = new Feelings();
		ArrayList<Feelings> relationship = new ArrayList<Feelings>();
		relationship.add(target1);
		relationship.add(target2);
		Quest quest = new Quest("Quest","Description",giver);
		HomewreckerReward reward = new HomewreckerReward(quest, intermediateFriend, relationship, SocialCapitolCost.CHEAP, FeelingsAttributes.TRUST);

		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(-3, target1.getTrend());
		assertEquals(-3, target2.getTrend());
		assertEquals(0, target1.getTrust());
		assertEquals(0, target2.getTrust());

		reward.getItemReward();

		assertEquals(QuestState.COMPLETED, giver.getLastQuestResult());
		assertEquals(-3, target1.getTrend());
		assertEquals(-3, target2.getTrend());
		assertEquals(-3, target1.getTrust());
		assertEquals(-3, target2.getTrust());
	}
	
	@Test
	public void testCantBeRewardedTwiceDueToPlayerCantHoldReward()
	{
	    NPC giver = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC intermediateFriend = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        Feelings target1 = new Feelings();
        Feelings target2 = new Feelings();
        ArrayList<Feelings> relationship = new ArrayList<Feelings>();
        relationship.add(target1);
        relationship.add(target2);
        Quest quest = new Quest("Quest","Description",giver);
        HomewreckerReward reward = new HomewreckerReward(quest, intermediateFriend, relationship, SocialCapitolCost.CHEAP, FeelingsAttributes.TRUST);
        quest.addReward(reward);
        MockTask task = new MockTask();
        quest.addTask(task);
        MockItem obj2 = new MockItem();
        MockQuestReward reward2 = new MockQuestReward();
        reward2.reward = obj2;
        quest.addReward(reward2);
        
        
        PC dude = new PC(1,"Dude","Desc",50);
        
        dude.assignNativeQuest(quest);
        task.setComplete(100);
        task.setComplete(dude, 100);
        dude.holdInHand(obj2.clone(), HandLocation.RIGHT);
        dude.holdInHand(obj2.clone(), HandLocation.LEFT);
        
        assertFalse(quest.turnInQuest(dude));
        
        assertEquals(0, target1.getTrend());
        assertEquals(0, target2.getTrend());
        assertEquals(0, target1.getTrust());
        assertEquals(0, target2.getTrust());
	}
}
