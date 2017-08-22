package sw.socialNetwork;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import sw.environment.SWRoomUpdateType;
import sw.item.Item;
import sw.lifeform.SocialNPC;
import sw.quest.FavorQuest;
import sw.quest.GiftQuest;
import sw.quest.HomewreckerQuest;
import sw.quest.RequestFavorQuest;
import sw.quest.SocialQuest;
import sw.quest.SocialQuestDifficulty;

/**
 * 
 * @author David Abrams
 * 
 * The purpose of this set of tests is to make sure that QuestGenerator can properly create all 4
 * types of SocialQuests, as well as make sure that QuestGenerator behaves properly as a singleton.
 */
public class TestQuestGenerator
{
	//margin of error for use in tests that look at events that should have a certain chance of occuring
	private double error = 0.05;

	/**
	 * This test makes sure that QuestGenerator is a singleton.
	 */
	@Test
	public void testInit()
	{
		QuestGenerator qgen1 = QuestGenerator.getInstance();
		QuestGenerator qgen2 = QuestGenerator.getInstance();

		assertEquals(qgen1, qgen2);

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that QuestGenerator can properly keep track of Items as they are added
	 * and removed from the world.
	 */
	@Test
	public void testItemTracking()
	{
		QuestGenerator qgen = QuestGenerator.getInstance();
		MockItem item1 = new MockItem();
		MockItem item2 = new MockItem();
		MockItem item3 = new MockItem();

		assertEquals(0, qgen.getItemsInWorld().size());

		qgen.roomUpdate(null, item1, SWRoomUpdateType.ITEM_ADDED);

		assertTrue(qgen.getItemsInWorld().contains(item1));

		qgen.roomUpdate(null, item2, SWRoomUpdateType.ITEM_ADDED);

		assertTrue(qgen.getItemsInWorld().contains(item2));

		qgen.roomUpdate(null, item3, SWRoomUpdateType.ITEM_ADDED);

		assertTrue(qgen.getItemsInWorld().contains(item3));

		qgen.roomUpdate(null, item2, SWRoomUpdateType.ITEM_REMOVED);

		assertFalse(qgen.getItemsInWorld().contains(item2));

		QuestGenerator.clear();
	}

	/**
	 * Make sure that QuestGenerator can properly create a GiftQuest.
	 */
	@Test
	public void testGenGiftQuest()
	{
		QuestGenerator qgen = QuestGenerator.getInstance();
		SocialQuest quest = null;

		SocialNPC questGiver = new SocialNPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		SocialNPC questTarget = new SocialNPC(1, "Target", "target of quests", 1, 1, 1, 1);
		MockItem gift = new MockItem();
		questGiver.setCurrentCapital(500);

		//make sure that a GiftQuest can be created using a specific Item as a gift
		quest = qgen.genGiftQuest(questGiver, questTarget, gift);
		questGiver.addQuest(quest);

		assertEquals(questGiver, quest.getQuestGiver());
		assertEquals(questTarget, quest.getQuestTarget());
		assertEquals(gift, ((GiftQuest) quest).getGift());
		assertEquals("GiftQuest #1", quest.getName());
		assertTrue(questGiver.getAvailableQuests().contains(quest));
		assertEquals(SocialQuestDifficulty.EASY, quest.getDifficulty());

		//make sure that a GiftQuest is properly created when choosing a random gift
		MockItem gift2 = new MockItem();
		MockItem gift3 = new MockItem();
		MockItem gift4 = new MockItem();
		qgen.roomUpdate(null, gift, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, gift2, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, gift3, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, gift4, SWRoomUpdateType.ITEM_ADDED);
		int[] counter = new int[4];

		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			quest = qgen.genGiftQuest(questGiver, questTarget);
			questGiver.addQuest(quest);

			if (((GiftQuest) quest).getGift().equals(gift))
			{
				counter[0]++;
			} else if (((GiftQuest) quest).getGift().equals(gift2))
			{
				counter[1]++;
			} else if (((GiftQuest) quest).getGift().equals(gift3))
			{
				counter[2]++;
			} else if (((GiftQuest) quest).getGift().equals(gift4))
			{
				counter[3]++;
			} else
			{
				fail("mismatched gift item");
			}

			assertEquals(questGiver, quest.getQuestGiver());
			assertEquals(questTarget, quest.getQuestTarget());
			assertEquals("GiftQuest #" + (i + 2), quest.getName());
			assertTrue(questGiver.getAvailableQuests().contains(quest));
			assertEquals(SocialQuestDifficulty.EASY, quest.getDifficulty());

			questGiver.removeQuest(quest);
		}

		//each gift should have a equal chance of being selected (+/- error)
		assertTrue(counter[0] + "", counter[0] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(counter[1] + "", counter[1] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(counter[2] + "", counter[2] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(counter[3] + "", counter[3] >= (numRuns / 4) - (numRuns * error) && counter[0] <= (numRuns / 4) + (numRuns * error));

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that QuestGenerator can properly create a FavorQuest both when the
	 * objective is specified and when QuestGenerator must randomly select it.
	 */
	@Test
	public void testGenFavorQuest()
	{
		QuestGenerator qgen = QuestGenerator.getInstance();
		SocialNPC questGiver = new SocialNPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		SocialNPC questTarget = new SocialNPC(1, "Target", "target of quests", 1, 1, 1, 1);
		MockItem objective = new MockItem();
		questGiver.setCurrentCapital(500);

		FavorQuest quest = qgen.genFavorQuest(questGiver, questTarget, objective);
		questGiver.addQuest(quest);

		assertEquals(questGiver, quest.getQuestGiver());
		assertEquals(questTarget, quest.getQuestTarget());
		assertEquals(objective, quest.getFavorTarget());
		assertTrue(objective.hasQuest(quest));
		assertEquals(SocialQuestDifficulty.EASY, quest.getDifficulty());
		assertEquals("FavorQuest #1", quest.getName());
		assertTrue(questGiver.getAvailableQuests().contains(quest));

		//make sure that random selection of the objective works properly
		int[] count = new int[4];
		MockItem obj2 = new MockItem();
		MockItem obj3 = new MockItem();
		MockItem obj4 = new MockItem();

		qgen.roomUpdate(null, objective, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, obj2, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, obj3, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, obj4, SWRoomUpdateType.ITEM_ADDED);

		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			questGiver.setCurrentCapital(1000);
			quest = qgen.genFavorQuest(questGiver, questTarget);
			questGiver.addQuest(quest);

			if (quest.getFavorTarget().equals(objective))
			{
				count[0]++;
				assertTrue(objective.hasQuest(quest));
			} else if (quest.getFavorTarget().equals(obj2))
			{
				count[1]++;
				assertTrue(obj2.hasQuest(quest));
			} else if (quest.getFavorTarget().equals(obj3))
			{
				count[2]++;
				assertTrue(obj3.hasQuest(quest));
			} else if (quest.getFavorTarget().equals(obj4))
			{
				count[3]++;
				assertTrue(obj4.hasQuest(quest));
			} else
			{
				fail("wrong objective");
			}

			assertEquals(questGiver, quest.getQuestGiver());
			assertEquals(questTarget, quest.getQuestTarget());
			assertEquals("FavorQuest #" + (i + 2), quest.getName());
			assertTrue(questGiver.getAvailableQuests().contains(quest));

			questGiver.removeQuest(quest);
		}

		//make sure that the objective has the same chance of being selected (+/- error)
		assertTrue(count[0] + "", count[0] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[1] + "", count[1] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[2] + "", count[2] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[3] + "", count[3] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));

		QuestGenerator.clear();
	}

	/**
	 * Makes sure that QuestGenerator can properly create a RequestFavorQuest both when given a
	 * specific FavorTarget and when randomly selecting one.
	 */
	@Test
	public void testGenReqFavQuest()
	{
		QuestGenerator qgen = QuestGenerator.getInstance();
		SocialNPC questGiver = new SocialNPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		SocialNPC questTarget = new SocialNPC(1, "Target", "target of quests", 1, 1, 1, 1);
		MockItem objective = new MockItem();
		
		questGiver.setCurrentCapital(500);

		RequestFavorQuest quest = qgen.genReqFavQuest(questGiver, questTarget, objective);
		questGiver.addQuest(quest);

		assertEquals(questGiver, quest.getQuestGiver());
		assertEquals(questTarget, quest.getQuestTarget());
		assertEquals(objective, quest.getFavorTarget());
		assertTrue(objective.hasQuest(quest));
		assertEquals("RequestFavorQuest #1", quest.getName());
		assertTrue(questGiver.getAvailableQuests().contains(quest));

		//make sure that random selection of the objective works properly
		int[] count = new int[4];
		MockItem obj2 = new MockItem();
		MockItem obj3 = new MockItem();
		MockItem obj4 = new MockItem();

		qgen.roomUpdate(null, objective, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, obj2, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, obj3, SWRoomUpdateType.ITEM_ADDED);
		qgen.roomUpdate(null, obj4, SWRoomUpdateType.ITEM_ADDED);

		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			quest = qgen.genReqFavQuest(questGiver, questTarget);
			questGiver.addQuest(quest);

			if (quest.getFavorTarget().equals(objective))
			{
				count[0]++;
				assertTrue(objective.hasQuest(quest));
			} else if (quest.getFavorTarget().equals(obj2))
			{
				count[1]++;
				assertTrue(obj2.hasQuest(quest));
			} else if (quest.getFavorTarget().equals(obj3))
			{
				count[2]++;
				assertTrue(obj3.hasQuest(quest));
			} else if (quest.getFavorTarget().equals(obj4))
			{
				count[3]++;
				assertTrue(obj4.hasQuest(quest));
			} else
			{
				fail("wrong objective");
			}

			assertEquals(questGiver, quest.getQuestGiver());
			assertEquals(questTarget, quest.getQuestTarget());
			assertEquals("RequestFavorQuest #" + (i + 2), quest.getName());
			assertTrue(questGiver.getAvailableQuests().contains(quest));

			questGiver.removeQuest(quest);
		}

		//make sure that the objective has the same chance of being selected (+/- error)
		assertTrue(count[0] + "", count[0] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[1] + "", count[1] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[2] + "", count[2] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));
		assertTrue(count[3] + "", count[3] >= (numRuns / 4) - (numRuns * error) && count[0] <= (numRuns / 4) + (numRuns * error));

		QuestGenerator.clear();
	}

	@Test
	public void testGenHomewreckerQuest()
	{
		QuestGenerator qgen = QuestGenerator.getInstance();
		SocialNPC questGiver = new SocialNPC(0, "Giver", "gives quests", 1, 1, 1, 1);
		SocialNPC questTarget = new SocialNPC(0, "Target", "target of quests", 1, 1, 1, 1);
		Feelings target1 = new Feelings();
		Feelings target2 = new Feelings();
		ArrayList<Feelings> relationship = new ArrayList<Feelings>();
		questGiver.setCurrentCapital(500);
		relationship.add(target1);
		relationship.add(target2);

		SocialQuest quest = qgen.genHomewreckerQuest(questGiver, questTarget, relationship);
		questGiver.addQuest(quest);

		assertEquals(questGiver, quest.getQuestGiver());
		ArrayList<Feelings> holder = ((HomewreckerQuest) (quest)).getTargetRelationship();
		assertEquals(relationship.get(0), holder.get(0));
		assertEquals(relationship.get(1), holder.get(1));
		assertEquals("HomewreckerQuest #1", quest.getName());
		assertTrue(questGiver.getAvailableQuests().contains(quest));
		assertEquals(SocialQuestDifficulty.EASY, quest.getDifficulty());

		//make sure that the attribute is randomly selected
		int counter = 0;
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			quest = qgen.genHomewreckerQuest(questGiver, questTarget, relationship);

			if (((HomewreckerQuest) (quest)).getTargetAttribute() == FeelingsAttributes.INTIMACY)
			{
				counter++;
			}
		}

		//make sure attributes have an equal chance of being selected (+/- error)
		assertTrue("Counter: " + counter, counter >= (numRuns / 2) - (numRuns * error) && counter <= (numRuns / 2) + (numRuns * error));

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that QuestGenerator can properly pick the difficulty of a quest.
	 */
	@Test
	public void testDecideDifficulty()
	{
		MockSocialNPC giver = new MockSocialNPC();
		QuestGenerator qgen = QuestGenerator.getInstance();
		SocialQuestDifficulty diff;
		giver.setCurrentCapital(2500);

		int[] counter = { 0, 0, 0, 0 };
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			diff = qgen.decideDifficulty(giver);

			if (diff.equals(SocialQuestDifficulty.EASY))
			{
				counter[0]++;
			} else if (diff.equals(SocialQuestDifficulty.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialQuestDifficulty.HARD))
			{
				counter[2]++;
			} else if (diff.equals(SocialQuestDifficulty.YOUMUSTBEPRO))
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
		giver.setCurrentCapital(1500);

		for (int i = 0; i < numRuns; i++)
		{
			diff = qgen.decideDifficulty(giver);

			if (diff.equals(SocialQuestDifficulty.EASY))
			{
				counter[0]++;
			} else if (diff.equals(SocialQuestDifficulty.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialQuestDifficulty.HARD))
			{
				counter[2]++;
			} else if (diff.equals(SocialQuestDifficulty.YOUMUSTBEPRO))
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
		giver.setCurrentCapital(1000);

		for (int i = 0; i < numRuns; i++)
		{
			diff = qgen.decideDifficulty(giver);

			if (diff.equals(SocialQuestDifficulty.EASY))
			{
				counter[0]++;
			} else if (diff.equals(SocialQuestDifficulty.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialQuestDifficulty.HARD))
			{
				counter[2]++;
			} else if (diff.equals(SocialQuestDifficulty.YOUMUSTBEPRO))
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
		giver.setCurrentCapital(500);

		for (int i = 0; i < numRuns; i++)
		{
			diff = qgen.decideDifficulty(giver);

			if (diff.equals(SocialQuestDifficulty.EASY))
			{
				counter[0]++;
			} else if (diff.equals(SocialQuestDifficulty.MEDIUM))
			{
				counter[1]++;
			} else if (diff.equals(SocialQuestDifficulty.HARD))
			{
				counter[2]++;
			} else if (diff.equals(SocialQuestDifficulty.YOUMUSTBEPRO))
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

		QuestGenerator.clear();
	}
}

class MockItem extends Item
{

	public MockItem()
	{
		super("Mock item", "A mock item for testing purposes", 1, 1);
	}
}

class MockSocialNPC extends SocialNPC
{
	public MockSocialNPC()
	{
		super(0, "Mocky", "A MockSocialNPC", 50, 5, 5, 1);
	}
}