package sw.quest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import mock.MockFeelings;
import mock.MockItem;

import org.junit.Test;

import sw.environment.RoomUpdateType;
import sw.item.Item;
import sw.lifeform.NPC;
import sw.quest.QuestGenerator;
import sw.quest.SocialCapitolCost;
import sw.quest.reward.FavorReward;
import sw.quest.reward.GiftReward;
import sw.quest.reward.HomewreckerReward;
import sw.quest.reward.QuestReward;
import sw.quest.reward.RequestFavorReward;
import sw.quest.task.DeliverItemTask;
import sw.quest.task.TaskType;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FeelingsAttributes;
import sw.socialNetwork.SocialNetwork;
import sw.socialNetwork.SocialNetworkDecayRates;

/**
 * 
 * @author David Abrams and Dr. Girard
 * 
 * The purpose of this set of tests is to make sure that QuestGenerator can properly create all 4
 * types of SocialQuests, as well as make sure that QuestGenerator behaves properly as a singleton.
 */
public class TestQuestGenerator
{
	//margin of error for use in tests that look at events that should have a certain chance of occuring
	private double error = 0.05;

	/**
	 * Make sure that QuestGenerator can properly create a GiftQuest.
	 */
	@Test
	public void testGenGiftQuest()
	{
		QuestGenerator.clear();
		NPC questGiver = new NPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		NPC questTarget = new NPC(1, "Target", "target of quests", 1, 1, 1, 1);
		MockItem gift1 = new MockItem("Item1","Desc1",3,3);
		questGiver.getSocialNetwork().setCurrentCapital(500);

		//make sure that a GiftQuest can be created using a specific Item as a gift
		TimedQuest quest = QuestGenerator.genGiftQuest(questGiver, questTarget, gift1, SocialNetworkDecayRates.NORMAL);

		assertEquals(questGiver, quest.getGranter());
		GiftReward reward = (GiftReward)quest.getRewards().elementAt(0);
		assertEquals(questTarget, reward.getTarget());
		assertEquals(gift1, reward.getGift());
		DeliverItemTask task = (DeliverItemTask)quest.getTasks().elementAt(0);
		assertEquals(gift1,task.getItem());
		assertEquals("GiftQuest #1", quest.getName());
		assertEquals(SocialCapitolCost.CHEAP, reward.getCost());

		//make sure that a GiftQuest is properly created when choosing a random gift		
		MockItem gift2 = new MockItem("Item2","Desc2",3,3);
		MockItem gift3 = new MockItem("Item3","Desc3",3,3);
		MockItem gift4 = new MockItem("Item4","Desc4",3,3);
		questGiver.addQuestItem(gift1);
		questGiver.addQuestItem(gift2);
		questGiver.addQuestItem(gift3);
		questGiver.addQuestItem(gift4);
		int[] counter = new int[4];

		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			quest = QuestGenerator.genGiftQuest(questGiver, questTarget);
			reward = (GiftReward)quest.getRewards().elementAt(0);

			if (reward.getGift().equals(gift1))
			{
				counter[0]++;
			} else if (reward.getGift().equals(gift2))
			{
				counter[1]++;
			} else if (reward.getGift().equals(gift3))
			{
				counter[2]++;
			} else if (reward.getGift().equals(gift4))
			{
				counter[3]++;
			} else
			{
				fail("mismatched gift item");
			}

			assertEquals("GiftQuest #" + (i + 2), quest.getName());
		}

		//each gift should have a equal chance of being selected (+/- error)
		assertTrue(counter[0] + "", counter[0] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(counter[1] + "", counter[1] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(counter[2] + "", counter[2] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(counter[3] + "", counter[3] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));

		// Make sure the time is set properly
		assertEquals(60,quest.getTimeToHoldRemaining());
		assertEquals(60,quest.getTimeToCompleteRemaining());
	}

	/**
	 * This test makes sure that QuestGenerator can properly create a FavorQuest both when the
	 * objective is specified and when QuestGenerator must randomly select it.
	 */
	//Can be ignored as only item delivery is actually implemented
	@Test
	public void testGenFavorQuest()
	{
		QuestGenerator.clear();
		NPC questGiver = new NPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		SocialNetwork giverNetwork = questGiver.getSocialNetwork();
		NPC questTarget = new NPC(1, "Target", "target of quests", 1, 1, 1, 1);
		MockItem objective = new MockItem();
		giverNetwork.setCurrentCapital(500);

		TimedQuest quest = QuestGenerator.genFavorQuest(questGiver, questTarget, objective);
		questGiver.addQuest(quest);

		assertEquals(questGiver, quest.getGranter());
		FavorReward reward = (FavorReward)quest.getRewards().elementAt(0);
		DeliverItemTask task = (DeliverItemTask)quest.getTasks().elementAt(0);
		assertEquals(questTarget, reward.getTarget());
		assertEquals(questTarget, task.getTarget());
		assertEquals(SocialCapitolCost.CHEAP, reward.getCost());
		assertEquals("FavorQuest #1", quest.getName());
		assertTrue(questGiver.getAvailableQuests().contains(quest));

		//make sure that random selection of the objective works properly	
		int[] count = new int[4];
		MockItem obj2 = new MockItem();
		MockItem obj3 = new MockItem();
		MockItem obj4 = new MockItem();

		questGiver.addQuestItem(objective);
		questGiver.addQuestItem(obj2);
		questGiver.addQuestItem(obj3);
		questGiver.addQuestItem(obj4);
		
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			giverNetwork.setCurrentCapital(1000);
			quest = QuestGenerator.genFavorQuest(questGiver, questTarget);
			reward = (FavorReward)quest.getRewards().elementAt(0);
	        task = (DeliverItemTask)quest.getTasks().elementAt(0);
			questGiver.addQuest(quest);

			if (task.getItem().equals(objective))
			{
				count[0]++;
			} else if (task.getItem().equals(obj2))
			{
				count[1]++;
			} else if (task.getItem().equals(obj3))
			{
				count[2]++;
			} else if (task.getItem().equals(obj4))
			{
				count[3]++;
			} else
			{
				fail("wrong objective");
			}

			assertEquals(questGiver, quest.getGranter());
			assertEquals(questTarget, task.getTarget());
			assertEquals(questTarget, reward.getTarget());
			assertEquals("FavorQuest #" + (i + 2), quest.getName());
			assertTrue(questGiver.getAvailableQuests().contains(quest));

			questGiver.removeQuest(quest);
		}

		//make sure that the objective has the same chance of being selected (+/- error)
		assertTrue(count[0] + "", count[0] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[1] + "", count[1] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[2] + "", count[2] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[3] + "", count[3] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
	}

	/**
	 * Makes sure that QuestGenerator can properly create a RequestFavorQuest both when given a
	 * specific FavorTarget and when randomly selecting one.
	 */
	@Test
	public void testGenReqFavQuest()
	{
		QuestGenerator.clear();
		NPC questGiver = new NPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		NPC questTarget = new NPC(1, "Target", "target of quests", 1, 1, 1, 1);
		MockItem objective = new MockItem("Item1","Desc1",1,1);
		
		questGiver.getSocialNetwork().setCurrentCapital(500);
		questGiver.addQuestItem(objective);

		TimedQuest quest = QuestGenerator.genReqFavQuest(questGiver, questTarget, TaskType.ITEM_TASK);
		questGiver.addQuest(quest);

		assertEquals(questGiver, quest.getGranter());
		RequestFavorReward reward = (RequestFavorReward)quest.getRewards().elementAt(0);
		assertEquals(questTarget, reward.getTarget());
		DeliverItemTask task = (DeliverItemTask)quest.getTasks().elementAt(0);
		assertEquals(objective, task.getItem());
		assertEquals(quest,task.getQuest());
		assertEquals("RequestFavorQuest #1", quest.getName());
		assertTrue(questGiver.getAvailableQuests().contains(quest));

		//make sure that random selection of the objective works properly
		int[] count = new int[4];
		MockItem obj2 = new MockItem("Item2","Desc2",1,1);
		MockItem obj3 = new MockItem("Item3","Desc3",1,1);
		MockItem obj4 = new MockItem("Item4","Desc4",1,1);
		
		questGiver.addQuestItem(obj2);
		questGiver.addQuestItem(obj3);
		questGiver.addQuestItem(obj4);

		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			quest = QuestGenerator.genReqFavQuest(questGiver, questTarget);
			questGiver.addQuest(quest);
			task = (DeliverItemTask)quest.getTasks().elementAt(0);
			if (task.getItem().equals(objective))
			{
				count[0]++;
				assertEquals(quest,task.getQuest());
			} else if (task.getItem().equals(obj2))
			{
				count[1]++;
				assertEquals(quest,task.getQuest());
			} else if (task.getItem().equals(obj3))
			{
				count[2]++;
				assertEquals(quest,task.getQuest());
			} else if (task.getItem().equals(obj4))
			{
				count[3]++;
				assertEquals(quest,task.getQuest());
			} else
			{
				fail("wrong objective");
			}

			assertEquals(questGiver, quest.getGranter());
			assertEquals(questTarget, task.getTarget());
			assertEquals("RequestFavorQuest #" + (i + 2), quest.getName());
			assertTrue(questGiver.getAvailableQuests().contains(quest));

			questGiver.removeQuest(quest);
		}

		//make sure that the objective has the same chance of being selected (+/- error)
		assertTrue(count[0] + "", count[0] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[1] + "", count[1] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[2] + "", count[2] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[3] + "", count[3] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
	}

	@Test
	public void testGenHomewreckerQuest()
	{
		QuestGenerator.clear();
		NPC questGiver = new NPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		NPC questTarget1 = new NPC(0, "Target1", "target of quests", 1, 1, 1, 1);
		NPC questTarget2 = new NPC(0, "Target2", "target of quests", 1, 1, 1, 1);
		
		questGiver.getSocialNetwork().addFriend(questTarget1);
		questTarget1.getSocialNetwork().addFriend(questGiver);
		questTarget1.getSocialNetwork().addFriend(questTarget2);
		questTarget2.getSocialNetwork().addFriend(questTarget1);
		
		questGiver.getSocialNetwork().setCurrentCapital(500);

		TimedQuest quest = QuestGenerator.genHomewreckerQuest(questGiver);
		questGiver.addQuest(quest);
		
		// For now the first reward should be the Homewrecker effect.
		HomewreckerReward reward = (HomewreckerReward)quest.getRewards().get(0);

		assertEquals(questGiver, quest.getGranter());
		ArrayList<Feelings> holder = reward.getTargetRelationship();
		assertEquals(questTarget1.getSocialNetwork().getRelationships().get(questTarget2), holder.get(0));
		assertEquals(questTarget2.getSocialNetwork().getRelationships().get(questTarget1), holder.get(1));
		assertEquals("HomewreckerQuest #1", quest.getName());
		assertTrue(questGiver.getAvailableQuests().contains(quest));
		assertEquals(SocialCapitolCost.CHEAP, reward.getCost());

		//make sure that the attribute is randomly selected
		int counter = 0;
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			quest = QuestGenerator.genHomewreckerQuest(questGiver);

			// For now the first reward should be the Homewrecker effect.
	        reward = (HomewreckerReward)quest.getRewards().get(0);
	        
			if (reward.getTargetAttribute() == FeelingsAttributes.INTIMACY)
			{
				counter++;
			}
		}

		//make sure attributes have an equal chance of being selected (+/- error)
		assertTrue("Counter: " + counter, counter >= (numRuns / 2) - (numRuns * error) && counter <= (numRuns / 2) + (numRuns * error));
	}

    /**
     * This test makes sure that a SocialNPC can pick a target relationship properly when
     * making a HomewreckerQuest.
     */
    @Test
    public void testMakehomewreckerQuest()
    {
    	QuestGenerator.clear();
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(0, "Bill", "He wears gloves.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC john = new NPC(0, "John", "He wears kilts.", 50, 5, 10, 1);
        SocialNetwork johnNetwork = john.getSocialNetwork();
        NPC jane = new NPC(0, "Jane", "She wears large boots.", 50, 5, 10, 1);
        SocialNetwork janeNetwork = jane.getSocialNetwork();
        MockFeelings bobsFeelsForBill = new MockFeelings();
        MockFeelings bobsFeelsForJane = new MockFeelings();
        MockFeelings billsFeelsForBob = new MockFeelings();
        MockFeelings billsFeelsForJohn = new MockFeelings();
        MockFeelings johnsFeelsForBill = new MockFeelings();
        MockFeelings janesFeelsForBob = new MockFeelings();
        MockFeelings bobsFeelsForJohn = new MockFeelings();
        MockFeelings johnsFeelsForBob = new MockFeelings();
        TimedQuest quest;

        bobNetwork.addFriend(bill, bobsFeelsForBill);
        bobNetwork.addFriend(jane, bobsFeelsForJane);
        janeNetwork.addFriend(bob, janesFeelsForBob);
        billNetwork.addFriend(bob, billsFeelsForBob);
        billNetwork.addFriend(john, billsFeelsForJohn);
        johnNetwork.addFriend(bill, johnsFeelsForBill);

        /**
         * The network of friendships looks like this:
         * jane --- bob --- bill --- john
         * 
         * When Bob creates a HomewreckerQuest, it should target the relationship between
         * Bill John.
         */

        //repeat enough times to be sure that Bob won't pick a relationship involving himself
        for (int i = 0; i < 100; i++)
        {
            bobNetwork.setCurrentCapital(500);
            quest = QuestGenerator.genHomewreckerQuest(bob);
            
            HomewreckerReward homewreckerReward = (HomewreckerReward)quest.getRewards().elementAt(0);

            assertTrue(homewreckerReward.getTargetRelationship().contains(billsFeelsForJohn));
            assertTrue(homewreckerReward.getTargetRelationship().contains(johnsFeelsForBill));
        }

        bill.removeFriend(john);
        john.removeFriend(bill);
        bobNetwork.addFriend(john, bobsFeelsForJohn);
        johnNetwork.addFriend(bob, johnsFeelsForBob);

        /**
         * Now the network looks like this:
         * jane --- bob --- bill
         * |
         * john
         * 
         * No quest should be generated now because Bob is friends with everyone.
         */

        quest = QuestGenerator.genHomewreckerQuest(bob);

        assertNull(quest);
    }

	
	/**
	 * This test makes sure that QuestGenerator can properly pick the difficulty of a quest.
	 */
	@Test
	public void testDecideDifficulty()
	{
		QuestGenerator.clear();
	    NPC giver = new NPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		SocialCapitolCost diff;
		giver.getSocialNetwork().setCurrentCapital(2500);

		int[] counter = { 0, 0, 0, 0 };
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			diff = QuestGenerator.decideSocialCapitolCost(giver);

			if (diff.equals(SocialCapitolCost.CHEAP))
			{
				counter[0]++;
			} else if (diff.equals(SocialCapitolCost.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialCapitolCost.EXPENSIVE))
			{
				counter[2]++;
			} else if (diff.equals(SocialCapitolCost.EXTREME))
			{
				counter[3]++;
			}
		}

		/**
		 * each difficulty level should have the same chance (+/- 3%) to be selected if the
		 * SocialNPC has enough social capital to pay for it
		 */
		assertTrue("Easy counter: " + counter[0], counter[0] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue("Medium counter: " + counter[1], counter[1] >= (numRuns / 4) - (numRuns * error) && counter[1] <= (numRuns / 4) + (numRuns * error));
		assertTrue("Hard counter: " + counter[2], counter[2] >= (numRuns / 4) - (numRuns * error) && counter[2] <= (numRuns / 4) + (numRuns * error));
		assertTrue("Pro counter: " + counter[3], counter[3] >= (numRuns / 4) - (numRuns * error) && counter[3] <= (numRuns / 4) + (numRuns * error));

		/**
		 * If a SocialNPC doesn't have enough social capital to create the difficulty that was
		 * selected,
		 * then the next highest difficulty should be picked.
		 */

		//no pro quests
		counter[0] = 0;
		counter[1] = 0;
		counter[2] = 0;
		counter[3] = 0;
		giver.getSocialNetwork().setCurrentCapital(1500);

		for (int i = 0; i < numRuns; i++)
		{
			diff = QuestGenerator.decideSocialCapitolCost(giver);

			if (diff.equals(SocialCapitolCost.CHEAP))
			{
				counter[0]++;
			} else if (diff.equals(SocialCapitolCost.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialCapitolCost.EXPENSIVE))
			{
				counter[2]++;
			} else if (diff.equals(SocialCapitolCost.EXTREME))
			{
				counter[3]++;
			}
		}

		/**
		 * The SocialNPC doesn't have enough social capital to create pro quests, so there
		 * should be roughly twice as many (+/- error) hard quests created.
		 */
		assertTrue("Easy counter: " + counter[0], counter[0] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue("Medium counter: " + counter[1], counter[1] >= (numRuns / 4) - (numRuns * error) && counter[1] <= (numRuns / 4) + (numRuns * error));
		assertTrue("Hard counter: " + counter[2], counter[2] >= (numRuns / 2) - (numRuns * error) && counter[2] <= (numRuns / 2) + (numRuns * error));
		assertTrue("Pro counter: " + counter[3], counter[3] == 0);

		//no pro or hard quests
		counter[0] = 0;
		counter[1] = 0;
		counter[2] = 0;
		counter[3] = 0;
		giver.getSocialNetwork().setCurrentCapital(1000);

		for (int i = 0; i < numRuns; i++)
		{
			diff = QuestGenerator.decideSocialCapitolCost(giver);

			if (diff.equals(SocialCapitolCost.CHEAP))
			{
				counter[0]++;
			} else if (diff.equals(SocialCapitolCost.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialCapitolCost.EXPENSIVE))
			{
				counter[2]++;
			} else if (diff.equals(SocialCapitolCost.EXTREME))
			{
				counter[3]++;
			}
		}

		/**
		 * The SocialNPC doesn't have enough social capital to create pro or hard quests,
		 * so there should be roughly three times as many (+/- error) medium quests created.
		 */
		assertTrue("Easy counter: " + counter[0], counter[0] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue("Medium counter: " + counter[1], counter[1] >= (numRuns * 0.75) - (numRuns * error) && counter[1] <= (numRuns * 0.75) + (numRuns * error));
		assertTrue("Hard counter: " + counter[2], counter[2] == 0);
		assertTrue("Pro counter: " + counter[3], counter[3] == 0);

		//no pro, hard, or medium quests
		counter[0] = 0;
		counter[1] = 0;
		counter[2] = 0;
		counter[3] = 0;
		giver.getSocialNetwork().setCurrentCapital(500);

		for (int i = 0; i < numRuns; i++)
		{
			diff = QuestGenerator.decideSocialCapitolCost(giver);

			if (diff.equals(SocialCapitolCost.CHEAP))
			{
				counter[0]++;
			} else if (diff.equals(SocialCapitolCost.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialCapitolCost.EXPENSIVE))
			{
				counter[2]++;
			} else if (diff.equals(SocialCapitolCost.EXTREME))
			{
				counter[3]++;
			}
		}

		/**
		 * The SocialNPC doesn't have enough social capital to anything but easy quests.
		 */
		assertTrue("Easy counter: " + counter[0], counter[0] >= numRuns - (numRuns * error) && counter[0] <= numRuns + (numRuns * error));
		assertTrue("Medium counter: " + counter[1], counter[1] == 0);
		assertTrue("Hard counter: " + counter[2], counter[2] == 0);
		assertTrue("Pro counter: " + counter[3], counter[3] == 0);
	}
}