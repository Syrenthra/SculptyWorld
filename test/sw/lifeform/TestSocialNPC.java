package sw.lifeform;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.Test;

import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.SWRoomUpdateType;
import sw.item.Item;
import sw.quest.FavorQuest;
import sw.quest.GiftQuest;
import sw.quest.HomewreckerQuest;
import sw.quest.RequestFavorQuest;
import sw.quest.SocialQuest;
import sw.quest.SocialQuestDifficulty;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FriendRequest;
import sw.socialNetwork.FriendRequestLists;
import sw.socialNetwork.FriendRequestStatus;
import sw.socialNetwork.Moods;
import sw.socialNetwork.Personality;
import sw.socialNetwork.QuestGenerator;
import sw.socialNetwork.SocialQuestState;
import sw.socialNetwork.simulation.EventTypes;
import sw.socialNetwork.simulation.SocialNetworkEvent;

/**
 * This set of tests makes sure that SocialNPC behaves properly.
 * 
 * @author David Abrams
 */
public class TestSocialNPC
{
	private double error = 0.05; //margin of error for tests checking chance of an event

	/**
	 * This test makes sure that a SocialNPC can be correctly initialized with the right default
	 * values.
	 */
	@Test
	public void testInitialization()
	{
		SocialNPC npc = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

		assertTrue(npc instanceof SocialNPC);
		assertEquals(0, npc.getFriends().size());
		assertEquals(0, npc.getRelationships().size());
		assertEquals(0, npc.getFavoriteItems().size());
		assertEquals(0.5, npc.getPersonability(), 0.001);
		assertEquals(0, npc.getTotalDesiredFriends());
		assertEquals(0, npc.getTotalDesiredCapital());
		assertEquals(0, npc.getCurrentCapital());
		assertEquals(0, npc.getControl(), 0.01);
		assertEquals(Moods.HAPPY, npc.getCurrentMood());
		assertEquals(0.5, npc.getGrumpiness(), 0.01);
		
		Personality pers = new Personality(0.1, 0.2, 0.3, 10, 1000);
		SocialNPC npc2 = new SocialNPC(1, "Bill", "He wears jeans.", 50, 1, 1, 1, pers);
		
		assertEquals(1, npc2.getID());
		assertEquals("Bill", npc2.getName());
		assertEquals(0.1, npc2.getControl(), 0.001);
		assertEquals(0.2, npc2.getGrumpiness(), 0.001);
		assertEquals(0.3, npc2.getPersonability(), 0.001);
		assertEquals(10, npc2.getTotalDesiredFriends());
		assertEquals(1000, npc2.getTotalDesiredCapital());

		QuestGenerator.clear();
	}
	
	
	/**
	 * ToString() should return the name of the SocialNPC.
	 */
	@Test
	public void testToString()
	{
		SocialNPC npc = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		
		assertEquals("Bob", npc.toString());
		
		QuestGenerator.clear();
	}
	
	/**
	 * Tests to make sure that SocialNPC cleans out events that have been read.
	 */
	@Test
	public void testCleanEvents()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		
		bob.updateTime("", 0);
		
		assertEquals(1, bob.getEvents().size());
		
		SocialNetworkEvent event = bob.getEvents().get(0);
		
		assertEquals(EventTypes.CAPITAL_CHANGED, event.getType());
		
		event.read();
		
		bob.updateTime("", 0);
		
		assertFalse(bob.getEvents().contains(event));
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that all the getters and setters work correctly.
	 */
	@Test
	public void testGettersSetters()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		QuestGenerator questGen = QuestGenerator.getInstance();

		assertEquals(questGen, bob.getQuestGenerator());

		bob.setPersonability(1.0);
		assertEquals(1.0, bob.getPersonability(), 0.001);
		//illegal values
		bob.setPersonability(2.0);
		assertEquals(1.0, bob.getPersonability(), 0.001);
		bob.setPersonability(-0.01);
		assertEquals(1.0, bob.getPersonability(), 0.001);
		
		bob.setTotalDesiredFriends(10);
		assertEquals(10, bob.getTotalDesiredFriends());

		bob.setTotalDesiredCapital(25);
		assertEquals(25, bob.getTotalDesiredCapital());

		bob.setCurrentCapital(100);
		assertEquals(100, bob.getCurrentCapital());

		bob.setControl(0.9);
		assertEquals(0.9, bob.getControl(), 0.01);
		//illegal values
		bob.setControl(1.1);
		assertEquals(0.9, bob.getControl(), 0.01);
		bob.setControl(-0.1);
		assertEquals(0.9, bob.getControl(), 0.01);

		bob.setCurrentMood(Moods.ANGRY);
		assertEquals(Moods.ANGRY, bob.getCurrentMood());

		bob.setGrumpiness(0.1);
		assertEquals(0.1, bob.getGrumpiness(), 0.01);
		//illegal values
		bob.setGrumpiness(1.1);
		assertEquals(0.9, bob.getControl(), 0.01);
		bob.setGrumpiness(-0.1);
		assertEquals(0.9, bob.getControl(), 0.01);

		SocialNPC bill = new SocialNPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC jane = new SocialNPC(0, "Jane", "She wears jeans.", 50, 5, 10, 1);
		SocialNPC fred = new SocialNPC(0, "Fred", "He wears nice shoes.", 50, 5, 10, 1);
		SocialNPC ian = new SocialNPC(0, "Ian", "He wears sandals.", 50, 5, 10, 1);
		SocialNPC scott = new SocialNPC(0, "Scott", "He wears weird hats.", 50, 5, 10, 1);
		SocialNPC wilfred = new SocialNPC(0, "Wilfred", "He wears a dog suit.", 50, 5, 10, 1);
		SocialNPC mrAnderson = new SocialNPC(0, "Mr. Anderson", "He wears a trench coat.", 50, 5, 10, 1);
		SocialNPC duncan = new SocialNPC(0, "Duncan", "He wears Scottish garb.", 50, 5, 10, 1);
		SocialNPC john = new SocialNPC(0, "John", "He wears heavy gloves.", 50, 5, 10, 1);
		SocialNPC yvonne = new SocialNPC(0, "Yvonne", "She wears a thick scarf.", 50, 5, 10, 1);
		SocialNPC andrew = new SocialNPC(0, "Andrew", "He wears boxers.", 50, 5, 10, 1);
		bob.addFriend(bill, new MockFeelings());
		assertTrue(bob.getFriends().contains(bill));
		assertTrue(bob.getRelationships().containsKey(bill));
		bob.addFriend(bill, new MockFeelings());
		assertEquals(1, bob.getFriends().size());

		bob.addFriend(jane, new MockFeelings());
		bob.addFriend(fred, new MockFeelings());
		bob.addFriend(ian, new MockFeelings());
		bob.addFriend(scott, new MockFeelings());
		bob.addFriend(wilfred, new MockFeelings());
		bob.addFriend(mrAnderson, new MockFeelings());
		bob.addFriend(duncan, new MockFeelings());
		bob.addFriend(yvonne);
		bob.addFriend(john);
		assertEquals(10, bob.getFriends().size());
		bob.addFriend(andrew, new MockFeelings());
		// max friends is 10, so Andrew should not have been added
		assertEquals(10, bob.getFriends().size());
		assertTrue(!bob.getFriends().contains(andrew));

		bob.removeFriend(bill);
		assertFalse(bob.getFriends().contains(bill));
		assertFalse(bob.getRelationships().containsKey(bill));

		MockSocialQuest quest = new MockSocialQuest();
		bob.addQuest(quest);
		assertTrue(bob.getAvailableQuests().contains(quest));
		bob.addQuest(quest);
		assertEquals(1, bob.getAvailableQuests().size());

		bob.removeQuest(quest);
		assertFalse(bob.getAvailableQuests().contains(quest));

		MockItem2 item = new MockItem2();
		bob.addFavoriteItem(item);
		assertTrue(bob.getFavoriteItems().contains(item));
		bob.addFavoriteItem(item);
		assertEquals(1, bob.getFavoriteItems().size());

		bob.removeFavoriteItem(item);
		assertFalse(bob.getFavoriteItems().contains(item));

		QuestGenerator.clear();
	}

	/**
	 * The purpose of this test is to make sure that a SocialNPC can accurately determine it's
	 * current total amount of social capital.
	 */
	@Test
	public void testUpdateCapital()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		bob.setControl(0.5);
		SocialNPC fred = new SocialNPC(0, "Fred", "He wears big shirts.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears hats.", 50, 5, 10, 1);
		MockFeelings feelsForFred = new MockFeelings();
		MockFeelings feelsForBill = new MockFeelings();

		bob.updateCapital();
		assertEquals(0, bob.getCurrentCapital());

		bob.addFriend(fred, feelsForFred);
		bob.addFriend(bill, feelsForBill);
		bob.updateCapital();

		assertEquals(70, bob.getCurrentCapital());

		feelsForFred.setIntimacy(10);
		feelsForFred.setTrust(5);
		bob.updateCapital();
		assertEquals(155, bob.getCurrentCapital());

		bob.setCurrentCapital(0);

		feelsForBill.setIntimacy(20);
		feelsForBill.setTrust(1);
		bob.updateCapital();
		assertEquals(70, bob.getCurrentCapital());

		bob.setCurrentCapital(0);

		bob.removeFriend(bill);
		bob.updateCapital();
		assertEquals(50, bob.getCurrentCapital());

		bob.removeFriend(fred);
		bob.updateCapital();
		assertEquals(50, bob.getCurrentCapital());

		QuestGenerator.clear();
	}

	/**
	 * The purpose of this test is to make sure that a SocialNPC changes moods properly during its turn.
	 */
	@Test
	public void testUpdateTime_MoodChange()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears gloves.", 50, 5, 10, 1);
		MockFeelings billsFeels = new MockFeelings();
		MockFeelings bobsFeels = new MockFeelings();
		MockItem2 item = new MockItem2();

		bob.addFriend(bill, bobsFeels);
		bill.addFriend(bob, billsFeels);
		bob.getQuestGenerator().roomUpdate(null, item, SWRoomUpdateType.ITEM_ADDED);		

		/**
		 * Set up Bob to check for proper mood change. Mood change from quests takes priority over
		 * mood change from mood propagation.
		 */
		bob.setGrumpiness(1.0);
		bob.setLastQuestResult(SocialQuestState.FAILURE);

		bob.updateTime("", 1);

		//bob should be angry that the most recently completed quest was failed
		assertEquals(Moods.ANGRY, bob.getCurrentMood());
		assertNull(bob.getLastQuestResult());

		/**
		 * now mood change from mood propagation should come into play (only when the most recently
		 * turned in SocialQuest has been dealt with)
		 */
		int counter = 0;
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			bob.updateTime("", 1);
			if (bob.getCurrentMood() == Moods.HAPPY)
			{
				counter++;
				bob.setCurrentMood(Moods.ANGRY);
			}
			bobsFeels.setIntimacy(20); //don't let Bob's intimacy with Bill decay
		}

		//should change mood 15% of the time (+/- error)
		assertTrue("counter: " + counter, counter >= numRuns * 0.15 - (numRuns * error) && counter <= numRuns * 0.15 + (numRuns * error));

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a SocialNPC knows the right time to try and make friends, and
	 * can do it properly.
	 */
	@Test
	public void testUpdateTime_GiftQuest()
	{
		QuestGenerator qgen = QuestGenerator.getInstance();
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(1, "Bill", "He wears gloves.", 50, 5, 10, 1);
		SocialNPC john = new SocialNPC(2, "John", "He wears kilts.", 50, 5, 10, 1);
		MockRoom2 room = new MockRoom2();
		MockItem2 item = new MockItem2();

		qgen.roomUpdate(room, item, SWRoomUpdateType.ITEM_ADDED);
		room.addNPC(bob);
		room.addNPC(bill);
		room.addNPC(john);
		bob.setTotalDesiredFriends(2);
		bill.setTotalDesiredFriends(1);
		john.setTotalDesiredFriends(1);
		bob.setCurrentCapital(5000);
		//the SNPCs will make friends every time if they want more friends
		bob.setPersonability(1.0);
		bill.setPersonability(1.0);
		john.setPersonability(1.0);

		bob.updateTime("", 0);

		//Bob wants friends, so he should send FriendRequests to Bill and John
		assertTrue(bob.getFrReqList().containsKey(bill));
		assertTrue(bob.getFrReqList().containsKey(john));
		assertTrue(bill.getFrResponseList().containsKey(bob));
		assertTrue(john.getFrResponseList().containsKey(bob));

		//Bill and John both want 1 friend, so they should both accept Bob's requests
		bill.updateTime("", 0);
		john.updateTime("", 0);

		assertTrue(bill.getFrResponseList().get(bob).getState().equals(FriendRequestStatus.ACCEPTED));
		assertTrue(john.getFrResponseList().get(bob).getState().equals(FriendRequestStatus.ACCEPTED));

		//Bob sees that his requests were accepted, and creates quests
		bob.updateTime("", 0);

		assertEquals(2, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0) instanceof GiftQuest);
		assertTrue(bob.getAvailableQuests().get(1) instanceof GiftQuest);
		assertFalse(bob.getFrReqList().containsKey(bill));
		assertFalse(bob.getFrReqList().containsKey(john));

		//one quest should target Bill, and the other should target John
		if (bob.getAvailableQuests().get(0).getQuestTarget().equals(bill))
		{
			bob.getAvailableQuests().get(1).getQuestTarget().equals(john);
		} else
		{
			bob.getAvailableQuests().get(1).getQuestTarget().equals(bill);
		}

		//both quests were successful, so the SNPCs should now all be friends
		bob.getAvailableQuests().get(0).questSuccessful();
		bob.getAvailableQuests().get(1).questSuccessful();

		assertTrue(bob.getFriends().contains(bill));
		assertTrue(bob.getFriends().contains(john));
		assertTrue(bill.getFriends().contains(bob));
		assertTrue(john.getFriends().contains(bob));

		QuestGenerator.clear();
	}

	/**
	* This test makes sure that a SNPC is using the right priorities when deciding how to spend its 
	* social capital. This test also makes sure that the other actions that updateTime() should
	* perform are happening a the right time and in the right situation. 
	*/ 
	@Test
	public void testUpdateTime_QuestCreationPriorities()
	{
		QuestGenerator qgen = QuestGenerator.getInstance();
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(1, "Bill", "He wears gloves.", 50, 5, 10, 1);
		SocialNPC john = new SocialNPC(2, "John", "He wears kilts.", 50, 5, 10, 1);
		SocialNPC jane = new SocialNPC(3, "Jane", "She wears large boots.", 50, 5, 10, 1);
		SocialNPC jimmy = new SocialNPC(4, "Jimmy", "He wears dumb shirts", 50, 5, 10, 1);
		SocialNPC mike = new SocialNPC(5, "Mike", "He wears hiking boots", 50, 5, 10, 1);
		MockItem2 item = new MockItem2();
		MockRoom2 room = new MockRoom2();
		MockFeelings bobForBill = new MockFeelings();
		MockFeelings bobForJohn = new MockFeelings();
		MockFeelings bobForJane = new MockFeelings();
		MockFeelings bobForMike = new MockFeelings();

		qgen.roomUpdate(room, item, SWRoomUpdateType.ITEM_ADDED);
		room.addNPC(bob);
		room.addNPC(bill);
		room.addNPC(john);
		room.addNPC(jane);
		room.addNPC(jimmy);
		room.addNPC(mike);

		bob.addFriend(bill, bobForBill);
		bob.addFriend(john, bobForJohn);
		bob.addFriend(jane, bobForJane);
		bob.addFriend(mike, bobForMike);
		bill.addFriend(bob);
		john.addFriend(bob);
		jane.addFriend(bob);

		bob.setCurrentCapital(500);
		bob.setTotalDesiredCapital(5000);
		bob.setTotalDesiredFriends(5);
		bobForBill.setSocialDebtOwed(750);
		john.getRelationships().get(bob).setSocialDebtOwed(500);

		bobForBill.setTrust(3);
		bobForBill.setIntimacy(50);
		bobForJohn.setTrust(-2);
		bobForJohn.setIntimacy(30);
		bobForJane.setTrust(3);
		bobForJane.setIntimacy(80);
		bobForMike.setTrust(-1);
		bobForMike.setIntimacy(15);

		bill.askFavor(bob);

		/********************************************************************
		 									turn #1							
		 -SNPC sends FriendRequest when it does not have as many friends as it wants
		 --request sent properly 
		 --request received properly
		 -properly identifies relationships in danger of being terminated soon
		 --identifies correct part of relationship that needs to be improved
		 ---asks favor of correct SNPC
		 ----favor properly sent
		 ----favor properly received
		 -spends social capital trying to fix a decaying relationship
		 -relationships decay properly
		**********************************************************************/
		
		/**
		 * The network looks like this
		 * 
		 *                  mike        jimmy
		 *                    \  
		 *                (t=-1, i=15)     
		 *                      \ 
		 * bill <--(t=3, i=50)-- bob --(t=-2, i=30)--> john
		 *            <favor owed |
		 *                  (t=3, i= 80)
		 *                        |
		 *                        |
		 *                       jane
		 *                       
		 *                     
		 * Bob only has enough social capital to make one quest. He should send a FriendRequest
		 * to Jimmy. While Jimmy is thinking about that, Bob should try to fix any relationships 
		 * that are in the danger zone, so he should ask John for a favor. While John is thinking 
		 * about that, Bob should make a FavorQuest to try and save his relationship with Mike.
		 */

		bob.updateTime("", 0);

		//FriendRequest to Jimmy
		assertEquals(1, bob.getFrReqList().size());
		assertTrue(bob.getFrReqListOrder().contains(jimmy));
		assertTrue(jimmy.getFrResponseListOrder().contains(bob));
		
		//ask John for a favor; John agrees because he owes Bob a social debt.
		assertEquals(1, john.getFavorRequests().size());
		assertTrue(john.getFavorRequests().contains(bob));
		//ask Mike a favor; Mike does not agree because he does not owe Bob any social debt
		assertEquals(0, mike.getFavorRequests().size());
		
		//Bob only has enough social capital to make one quest...
		assertEquals(1, bob.getAvailableQuests().size());
		//...so he should make a FavorQuest for Mike because his relationship with Mike is the closest to being
		//terminated due to low intimacy
		assertTrue(bob.getAvailableQuests().get(0) instanceof FavorQuest);
		assertEquals(mike, bob.getAvailableQuests().get(0).getQuestTarget());
		//Bob still owes Bill a favor, even though Bob didn't have enough social capital to make the quest this turn
		assertTrue(bob.getFavorRequests().contains(bill));
		//all relationships should decay according to their trust values
		assertEquals(48, bobForBill.getIntimacy());
		assertEquals(26, bobForJohn.getIntimacy());
		assertEquals(78, bobForJane.getIntimacy());
		assertEquals(12, bobForMike.getIntimacy());
		
		/********************************************************************
		  									turn #2
		 -quest toHold timer ticks properly
		 -social capital updates properly
		 -SNPC should not send out friendRequests when the amount of requests sent means the 
		 	SNPC *would* have enough friends if the sent requests were accepted
		 -identifies accepted requests
		 -relationships decay properly							
		********************************************************************/
		
		/**
		 * The network looks like this
		 * 
		 *                			  jimmy	
		 *                mike         /
		 *                   \    FriendRequest sent
		 *              (t=-1, i=11) /    
		 *                     \    /
		 * bill <--(t=3, i=48)-- bob --(t=-2, i=25)--> john
		 *            <favor owed |
		 *                  (t=3, i= 78)
		 *                        |
		 *                        |
		 *                       jane
		 */
		 
		//Bob can now gain social capital.
		bob.setControl(1.0); 
		//this quest should expire during cleanup beginning next turn
		FavorQuest bobFavorForMike = (FavorQuest) bob.getAvailableQuests().get(0);
		bobFavorForMike.setTimeToHoldRemaining(1);
		//Jimmy accepted Bob's FriendRequest
		jimmy.getFrResponseList().get(bob).accept();
		
		bob.updateTime("", 0);
		
		//Bob doesn't have enough social capital to make any quests
		assertEquals(282, bob.getCurrentCapital()); 
		//Bob should not send any new FriendRequests.
		assertEquals(0, bob.getFrReqList().size());
		//The FR sent to Jimmy should be removed, since Jimmy accepted it
		assertEquals(0, bob.getFrReqListOrder().size());
		//Bob should make a GiftQuest targeting Jimmy
		assertEquals(1, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0) instanceof GiftQuest);
		assertEquals(jimmy, bob.getAvailableQuests().get(0).getQuestTarget());
		//The intimacy of Bob's relationships should decay according to their trust
		assertEquals(46, bobForBill.getIntimacy());
		assertEquals(22, bobForJohn.getIntimacy());
		assertEquals(76, bobForJane.getIntimacy());
		assertEquals(9, bobForMike.getIntimacy());
		
		/********************************************************************
		  									turn #3
		 -identifies and removes expired quests properly
		 -**spends social capital on highest priority
		 --make a new friend using a GiftQuest
		 ---accepted FriendRequests are removed from both SNPCs when the corresponding quest is made
		 -relationships decay properly
		 -social capital updates properly							
		 ********************************************************************/
		
		/**
		 * The network looks like this
		 * 
		 *                			  jimmy	
		 *                mike         /
		 *                   \       FriendRequest accepted by Jimmy
		 *              (t=-1, i=7)  /    
		 *                     \    /
		 * bill <--(t=3, i=46)-- bob --(t=-2, i=20)--> john
		 *            <favor owed |
		 *                  (t=3, i= 76)
		 *                        |
		 *                        |
		 *                       jane
		 */
		
		bob.setCurrentCapital(0);
		bob.updateTime("", 0);
		
		//The FavorQuest Bob made for Mike should have expired and been removed.
		assertEquals(0, bobFavorForMike.getTimeToHoldRemaining());
		assertFalse(bob.getAvailableQuests().contains(bobFavorForMike));
		//Since Jimmy accepted Bob's FriendRequest, Bob should h8ave made a GiftQuest targetting Jimmy
		//Bob should also make a FavorQuest targeting Mike
		assertEquals(2, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0) instanceof GiftQuest);
		assertEquals(jimmy, bob.getAvailableQuests().get(0).getQuestTarget());
		assertTrue(bob.getAvailableQuests().get(1) instanceof FavorQuest);
		assertEquals(mike, bob.getAvailableQuests().get(1).getQuestTarget());
		//The FriendRequest should be removed from both SNPCs when the quest is created
		assertFalse(bob.getFrReqListOrder().contains(jimmy));
		assertFalse(jimmy.getFrResponseListOrder().contains(bob));
		//intimacy of relationships should change according to their trust
		assertEquals(44, bobForBill.getIntimacy());
		assertEquals(18, bobForJohn.getIntimacy());
		assertEquals(74, bobForJane.getIntimacy());
		assertEquals(6, bobForMike.getIntimacy());
		//After everything is said and done, Bob should have 256 social capital
		assertEquals(246, bob.getCurrentCapital());
		
		/********************************************************************
		  									turn #4

		 -completed GiftQuest results in proper formation of new friendship
		 --two SNPCs now consider each other friends
		 --giver socially indebted to receiver
		 -a SNPC should terminate a relationship in which the intimacy has decayed to the minimum
		 --quests associated with terminated relationships should be removed
		 -SNPC responds properly when it does not have as many friends as it wants
		 --correctly identifies potential new friends
		 --sends FriendRequests to potential new friends
		 -**spends social capital on highest priority
		 --can't make new friends, so next priority is fixing friendships in the danger zone
		 --identifies relationship that will be terminated *soonest* and creates FavorQuest to try 
		 	and buy more time
		 -relationships should decay properly
		 -social capital updates properly 							
		 ********************************************************************/
		
		/**
		 * The network looks like this
		 * 
		 *                			  jimmy	
		 *                mike         /
		 *                   \       GiftQuest created
		 *              (t=-1, i=3)  /    
		 *                     \    /
		 * bill <--(t=3, i=44)-- bob --(t=-2, i=15)--> john
		 *            <favor owed |
		 *                  (t=3, i= 74)
		 *                        |
		 *                        |
		 *                       jane
		 */
		
		//The GiftQuest that Bob created targeting Jimmy was successfully completed
		bob.getAvailableQuests().get(0).questSuccessful();
		MockPlayer player = new MockPlayer();
		player.addQuest(bob.getAvailableQuests().get(0));
		bob.getAvailableQuests().get(0).addPlayer(player);
		bob.getAvailableQuests().get(0).turnInQuest(player);
		
		//At some point, Bob made a quest targeting Mike. This quest should be removed when Bob
		//terminates his relationship with Mike.
		MockSocialQuest quest = new MockSocialQuest();
		quest.setGiver(bob);
		quest.setTarget(mike);
		bob.addQuest(quest);
		
		//Bob relationship with Mike has decayed to the point where Bob should no longer want
		//to be friends with Mike
		bobForMike.setIntimacy(1);
		bob.setControl(0.0);//Bob will not gain any social capital this turn
		bob.setCurrentCapital(500);
		
		bob.updateTime("", 0);
		
		//Since the quest was completed, Bob and Jimmy should now be friends
		assertTrue(bob.getFriends().contains(jimmy));
		assertTrue(jimmy.getFriends().contains(bob));
		//The exchange of a gift should cause Jimmy to be socially indebted to Bob
		assertTrue(jimmy.getRelationships().get(bob).getSocialDebtOwed() > 0);
		assertTrue(bob.getRelationships().get(jimmy).getSocialDebtOwed() < 0);
		//The intimacy Bob's relationship with Mike should have decayed to the point where Bob 
		//terminates the relationship
		assertFalse(bob.getFriends().contains(mike));
		//The quest targetting Mike should have been removed too
		assertFalse(bob.getAvailableQuests().contains(quest));
		//Bob doesn't have as many friends as he wants, so he should send FriendRequests to all
		//other valid SNPCs (Mike is the only valid target)
		assertTrue(bob.getFrReqListOrder().contains(mike));
		assertTrue(mike.getFrResponseListOrder().contains(bob));
		//Bob has enough social capital to create 1 quest. He can't make new friends, so he should pick
		//the relationship that's soonest to being terminated and make a FavorQuest
		assertEquals(1, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0) instanceof FavorQuest);
		//Bob should pick Bill, because that relationship is closest to being terminated
		assertEquals(john, bob.getAvailableQuests().get(0).getQuestTarget());
		//relationships should decay properly
		assertEquals(42, bobForBill.getIntimacy());
		assertEquals(14, bobForJohn.getIntimacy());
		assertEquals(72, bobForJane.getIntimacy());
		//Relationships that are the result of GiftQuests start at intimacy = 35
		assertEquals(48, bob.getRelationships().get(jimmy).getIntimacy());
		//After everything's said and done, Bob should have 126 social capital left over
		assertEquals(0, bob.getCurrentCapital());
		
		/********************************************************************
		  									turn #5
		 -keeps favor requests in order over time
		 -**spends social capital on highest priority
		 --if SNPC doesn't want more friends and doesn't have relationships in the danger zone, it 
		 	should perform any favors that it agreed to
		 ---RequestFavorQuest is properly created
		 ---favor requests are updated
		 -a FriendRequest is kept over time by both SNPCs until it is rejected
		 -relationships decay properly							
		 ********************************************************************/
		
		/**
		 * Magic! Bob now has no relationships in danger of being terminated any time soon. He 
		 * should now devote his efforts to acquiring more social capital instead of desperately
		 * trying to keep failing relationships afloat. 
		 * 
		 * The network looks like this
		 * 
		 *                			  jimmy	
		 *                mike         /
		 *                   \    (t=5, i=30)
		 * Bob sent a FriendRequest  /    
		 *                     \    /
		 * bill <--(t=3, i=50)-- bob --(t=1, i=60)--> john
		 *            <favor owed |
		 *                  (t=3, i= 80)
		 *                        |
		 *                        |
		 *                       jane
		 */
		
		Feelings bobForJimmy = bob.getRelationships().get(jimmy);
		bobForBill.setTrust(3);
		bobForBill.setIntimacy(50);
		bobForJane.setTrust(3);
		bobForJane.setIntimacy(80);
		bobForJohn.setTrust(1);
		bobForJohn.setIntimacy(60);
		bobForJimmy.setTrust(5);
		bobForJimmy.setIntimacy(30);
		
		bob.getAvailableQuests().clear(); //Don't need the previously created quests anymore
		bob.setControl(0.0); //Bob's social capital will not update this turn
		bob.setCurrentCapital(500); //Bob has enough social capital for 1 quest
		
		bob.updateTime("", 0);
		
		//Bob still owes Bill a favor, so that agreement should take priority
		assertEquals(1, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0) instanceof RequestFavorQuest);
		assertEquals(bill, bob.getAvailableQuests().get(0).getQuestTarget());
		assertFalse(bob.getFavorRequests().contains(bill));
		assertEquals(0, bob.getFavorRequests().size());
		//Bob sent Mike a FriendRequest
		assertTrue(bob.getFrReqListOrder().contains(mike));
		assertTrue(mike.getFrResponseListOrder().contains(bob));
		//Relationships should decay according to their trust
		assertEquals(48, bobForBill.getIntimacy());
		assertEquals(57, bobForJohn.getIntimacy());
		assertEquals(78, bobForJane.getIntimacy());
		assertEquals(29, bobForJimmy.getIntimacy());
		
		
		/********************************************************************
		  									turn #6	
		 -**spends social capital on highest priority
		 --SNPC doesn't have as many friends as it wants and a FriendRequest it sent was accepted, 
		 	so create a GiftQuest
		 ---GiftQuest is properly created
		 ---FriendRequest is removed from both sender and receiver after quest is created
		 -relationships decay according to their trust						
		 ********************************************************************/
		
		/**
		 * The network looks like this
		 * 
		 *                			     jimmy	
		 *                mike             /
		 *                   \      (t=5, i=29)
		 * Mike accepted Bob's request  /    
		 *                     \      /
		 * bill <--(t=3, i=48)-- bob --(t=1, i=57)--> john
		 * 						  |
		 *                  (t=3, i= 78)
		 *                        |
		 *                        |
		 *                       jane
		 */
		
		mike.getFrResponseList().get(bob).accept(); //Mike accepted Bob's request
		bob.setCurrentCapital(500);//Bob has enough social capital to create 1 quest
		
		bob.updateTime("", 0);
		
		//Mike accepted Bob's friend request, so creating that quest is Bob's first priority
		assertEquals(2, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(1) instanceof GiftQuest);
		assertEquals(mike, bob.getAvailableQuests().get(1).getQuestTarget());
		//Bob and Mike should get rid of the FriendRequest when it's accepted
		assertFalse(bob.getFrReqListOrder().contains(mike));
		assertFalse(bob.getFrReqList().containsKey(mike));
		assertFalse(mike.getFrResponseListOrder().contains(bob));
		assertFalse(mike.getFrResponseList().containsKey(bob));
		//relationships should decay according to their trust
		assertEquals(46, bobForBill.getIntimacy());
		assertEquals(54, bobForJohn.getIntimacy());
		assertEquals(76, bobForJane.getIntimacy());
		assertEquals(28, bobForJimmy.getIntimacy());
		
		/********************************************************************
		  									turn #7	
		 -SNPC has all desired friends, so no requests should be sent && all received requests
		 	should be rejected
		 -**spend social capital on highest priority
		 --SNPC should try to improve existing relationships if no other priorities apply
		 -relationships decay according to their trust						
		 ********************************************************************/
		
		/**
		 * The network looks like this
		 * 
		 *                			     jimmy	
		 *                mike            _/
		 *                   \      (t=5, i=28)
		 *              (t=5, i=35)    _/    
		 *                     \      /
		 * bill <--(t=3, i=46)-- bob --(t=1, i=54)--> john
		 * 						  |  \____
		 *                  (t=3, i= 76)  \
		 *                        |        \
		 *                        |       rocky sent a FriendRequest to bob
		 *                       jane        \
		 *                                  rocky
		 */
		
		SocialNPC rocky = new SocialNPC(0, "Rocky", "He wears no shirt.", 50, 5, 10, 1);
		room.addNPC(rocky);
		rocky.sendFriendRequest(bob);
		FriendRequest rockyToBob = rocky.getFrReqList().get(bob);
		bob.getAvailableQuests().get(1).questSuccessful();//Bob and Mike are friends again
		bob.setCurrentCapital(500);
		bob.getRelationships().get(mike).setTrust(5);
		
		
		
		bob.updateTime("", 0);
		
		assertEquals(FriendRequestStatus.REJECTED, rockyToBob.getState());

		
	}
	

	/**
	 * This test makes sure that a SocialNPC generates the appropriate quest types based on its mood.
	 */
	@Test
	public void testUpdateTime_QuestsBasedOnMood()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears gloves.", 50, 5, 10, 1);
		SocialNPC john = new SocialNPC(0, "John", "He wears kilts.", 50, 5, 10, 1);
		MockItem2 item = new MockItem2();
		MockFeelings bobsFeelsForBill = new MockFeelings();
		MockFeelings billsFeelsForBob = new MockFeelings();
		MockFeelings billsFeelsForJohn = new MockFeelings();
		MockFeelings johnsFeelsForBill = new MockFeelings();
		SocialQuest quest = null;

		bob.addFriend(bill, bobsFeelsForBill);
		bill.addFriend(bob, billsFeelsForBob);
		bill.addFriend(john, billsFeelsForJohn);
		john.addFriend(bill, johnsFeelsForBill);
		bob.setGrumpiness(0.5);
		bob.setTotalDesiredCapital(5000);
		bobsFeelsForBill.setTrust(3);
		bob.getQuestGenerator().roomUpdate(null, item, SWRoomUpdateType.ITEM_ADDED);

		/**
		 * The network looks like this.
		 * 
		 * bob --- bill --- john
		 */
		
		//Bob is angry, but doesn't have enough social capital to make a quest, so he should not
		//be able to
		bob.setCurrentMood(Moods.ANGRY);
		bob.updateTime("", 0);
		assertEquals(0, bob.getAvailableQuests().size());
		
		
		/**
		 * When Bob is angry, half of his quests should be HomewreckerQuests (based on Bob's
		 * grumpiness).
		 */
		bob.setCurrentCapital(500);

		int[] counter = { 0, 0 };
		int numRuns = 1000;
		for (int j = 0; j < 2; j++)
		{
			for (int i = 0; i < numRuns; i++)
			{
				bob.updateTime("", 1);
				
				if(bob.getAvailableQuests().size() > 0)
				{
					quest = bob.getAvailableQuests().get(0);
				}
				
				if (quest instanceof HomewreckerQuest)
				{
					counter[0]++;
				} else
				{
					counter[1]++;
				}
				bob.removeQuest(quest);
				bob.setCurrentCapital(500);
				bobsFeelsForBill.setIntimacy(20);//don't let Bob's intimacy with Bill decay
				quest = null;
			}

			bob.setCurrentMood(Moods.ANGRY);
			//Bill needs to be angry so Bob won't become happy through mood propagation
			bill.setCurrentMood(Moods.ANGRY);
		}

		/**
		 * In the first iteration of the loop, Bob is happy so none of the quests should be
		 * HomewreckerQuests. In the second iteration of the loop, Bob is angry, so half of
		 * the quests created should be HomewreckerQuests.
		 */
		assertTrue("# of HomewreckerQuests: " + counter[0], counter[0] >= (numRuns/2) - (numRuns * error) && counter[0] <= (numRuns/2) + (numRuns * error));
		assertTrue("# of other quests: " + counter[1], counter[1] >= (numRuns*1.5) - (numRuns * error) && counter[1] <= (numRuns*1.5) + (numRuns * error));

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a SocialNPC can pick a target relationship properly when
	 * making a HomewreckerQuest.
	 */
	@Test
	public void testMakehomewreckerQuest()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears gloves.", 50, 5, 10, 1);
		SocialNPC john = new SocialNPC(0, "John", "He wears kilts.", 50, 5, 10, 1);
		SocialNPC jane = new SocialNPC(0, "Jane", "She wears large boots.", 50, 5, 10, 1);
		MockFeelings bobsFeelsForBill = new MockFeelings();
		MockFeelings bobsFeelsForJane = new MockFeelings();
		MockFeelings billsFeelsForBob = new MockFeelings();
		MockFeelings billsFeelsForJohn = new MockFeelings();
		MockFeelings johnsFeelsForBill = new MockFeelings();
		MockFeelings janesFeelsForBob = new MockFeelings();
		MockFeelings bobsFeelsForJohn = new MockFeelings();
		MockFeelings johnsFeelsForBob = new MockFeelings();
		HomewreckerQuest quest;

		bob.addFriend(bill, bobsFeelsForBill);
		bob.addFriend(jane, bobsFeelsForJane);
		jane.addFriend(bob, janesFeelsForBob);
		bill.addFriend(bob, billsFeelsForBob);
		bill.addFriend(john, billsFeelsForJohn);
		john.addFriend(bill, johnsFeelsForBill);

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
			bob.setCurrentCapital(500);
			quest = bob.makeHomewreckerQuest();

			assertTrue(quest.getTargetRelationship().contains(billsFeelsForJohn));
			assertTrue(quest.getTargetRelationship().contains(johnsFeelsForBill));
		}

		bill.removeFriend(john);
		john.removeFriend(bill);
		bob.addFriend(john, bobsFeelsForJohn);
		john.addFriend(bob, johnsFeelsForBob);

		/**
		 * Now the network looks like this:
		 * jane --- bob --- bill
		 * |
		 * john
		 * 
		 * No quest should be generated now because Bob is friends with everyone.
		 */

		quest = bob.makeHomewreckerQuest();

		assertNull(quest);

		QuestGenerator.clear();
	}

	/**
	 * The purpose of this test is to make sure that a SocialNPC changes moods appropriately based
	 * on the moods of its friends.
	 */
	@Test
	public void testChangeMoodFromPropagation()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC fred = new SocialNPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
		SocialNPC mike = new SocialNPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
		MockFeelings bobsFeels = new MockFeelings();

		bob.addFriend(jill, bobsFeels);
		bob.addFriend(sandy, bobsFeels);
		bob.addFriend(fred, bobsFeels);
		bob.addFriend(mike, bobsFeels);

		jill.setCurrentMood(Moods.ANGRY);
		sandy.setCurrentMood(Moods.ANGRY);
		fred.setCurrentMood(Moods.ANGRY);
		mike.setCurrentMood(Moods.ANGRY);

		// bob is Happy, all his friends are Angry, so he should become Angry
		// about 15% of the time
		int counter = 0;
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			bob.changeMoodPropagation();
			if (bob.getCurrentMood() == Moods.ANGRY)
			{
				counter++;
				bob.setCurrentMood(Moods.HAPPY);
			}
		}

		//Bob should become angry 15% of the time (+/- error)
		assertTrue("mood changes: " + counter, (counter >= numRuns * 0.15 - (numRuns * error)) && (counter <= numRuns * 0.15 + (numRuns * error)));

		// make sure mood propagation also works from Angry to Happy
		bob.setCurrentMood(Moods.ANGRY);
		jill.setCurrentMood(Moods.HAPPY);
		sandy.setCurrentMood(Moods.HAPPY);
		fred.setCurrentMood(Moods.HAPPY);
		mike.setCurrentMood(Moods.HAPPY);

		counter = 0;
		for (int i = 0; i < numRuns; i++)
		{
			bob.changeMoodPropagation();
			if (bob.getCurrentMood() == Moods.HAPPY)
			{
				counter++;
				bob.setCurrentMood(Moods.ANGRY);
			}
		}

		//Bob should become happy 15% of the time (+/- error)
		assertTrue("mood changes: " + counter, (counter >= numRuns * 0.15 - (numRuns * error)) && (counter <= numRuns * 0.15 + (numRuns * error)));

		QuestGenerator.clear();
	}

	/**
	 * The purpose of this test is to make sure that a SocialNPC changes moods based on quest
	 * completion only when appropriate.
	 */
	@Test
	public void testChangeMoodFromQuests()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

		bob.setGrumpiness(.9); //90% chance to become angry

		int counter = 0;
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++)
		{
			bob.setLastQuestResult(SocialQuestState.FAILURE);
			bob.changeMoodQuest();
			if (bob.getCurrentMood() == Moods.ANGRY)
			{
				counter++;
				bob.setCurrentMood(Moods.HAPPY);
			}
		}

		//Bob should become angry 9 times out of 10 (+/- error)
		assertTrue("counter: " + counter, counter >= numRuns * 0.9 - (numRuns * error) && counter <= numRuns * 0.9 + (numRuns * error));

		//make sure that changing mood to happy works too
		counter = 0;
		bob.setGrumpiness(0.1); //90% chance to become happy
		bob.setCurrentMood(Moods.ANGRY);

		for (int i = 0; i < numRuns; i++)
		{
			bob.setLastQuestResult(SocialQuestState.SUCCESS);
			bob.changeMoodQuest();
			if (bob.getCurrentMood() == Moods.HAPPY)
			{
				counter++;
				bob.setCurrentMood(Moods.ANGRY);
			}

		}

		//Bob should become happy 9 times out of 10 (+/- error)
		assertTrue("counter: " + counter, counter >= numRuns * 0.9 - (numRuns * error) && counter <= numRuns * 0.9 + (numRuns * error));

		QuestGenerator.clear();
	}

	/**
	 * The purpose of this test is to make sure that a SocialNPC can correctly assign a quest to a
	 * Player and also use QuestGenerator to create a quest.
	 */
	@Test
	public void testCreateAndAssignQuest()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		Player player = new Player(1, "1337pwn3r", "The player", 100);
		MockSocialQuest quest1 = new MockSocialQuest();
		MockSocialQuest quest2 = new MockSocialQuest();

		//shouldn't be able to give quests that aren't in the SocialNPC's availableQuests
		bob.assignQuest(player, quest1);
		assertEquals(0, bob.getAvailableQuests().size());
		assertEquals(0, player.getQuests().size());
		bob.assignQuest(player, 0);
		assertEquals(0, bob.getAvailableQuests().size());
		assertEquals(0, player.getQuests().size());

		bob.addQuest(quest1);
		bob.addQuest(quest2);

		bob.assignQuest(player, quest1);
		assertTrue(player.getQuests().contains(quest1));
		assertTrue(quest1.hasPlayer(player));
		bob.assignQuest(player, 1);
		assertTrue(player.getQuests().contains(quest2));
		assertTrue(quest2.hasPlayer(player));
		

		//a quest should not be assigned to a player if its timer has expired
		MockSocialQuest quest3 = new MockSocialQuest();
		quest3.setTimeToHoldRemaining(0);
		bob.addQuest(quest3);

		bob.assignQuest(player, quest3);

		assertFalse(player.getQuests().contains(quest3));

		QuestGenerator.clear();
	}
	
	
	/**
	 * Make sure that TurnInQuest() works properly.
	 */
	@Test
	public void testTurnInQuest()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		MockSocialQuest successful = new MockSocialQuest();
		MockSocialQuest failed = new MockSocialQuest();
		
		successful.setGiver(bob);
		failed.setGiver(bob);
		
		//shouldn't be able to turn in quests that are still in progress
		bob.turnInQuest(successful);
		bob.turnInQuest(failed);
		assertNull(bob.getLastQuestResult());
		
		successful.questSuccessful();
		failed.questFailed();
		
		//should be able to turn in quests that Bob didn't have in his availableQuests
		bob.turnInQuest(successful);
		bob.turnInQuest(failed);
		assertNull(bob.getLastQuestResult());
		
		//lastQuestResult should change based on the most recently turned in quest
		bob.addQuest(successful);
		bob.addQuest(failed);
		
		bob.turnInQuest(successful);
		assertEquals(SocialQuestState.SUCCESS, bob.getLastQuestResult());
		
		bob.turnInQuest(failed);
		assertEquals(SocialQuestState.FAILURE, bob.getLastQuestResult());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that the hasFriend method works correctly.
	 */
	@Test
	public void testHasFriend()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);

		assertFalse(bob.hasFriend(bill));
		assertFalse(bill.hasFriend(bob));

		bob.addFriend(bill, new Feelings());

		assertTrue(bob.hasFriend(bill));
		assertFalse(bill.hasFriend(bob));

		bill.addFriend(bob, new Feelings());

		assertTrue(bob.hasFriend(bill));
		assertTrue(bill.hasFriend(bob));

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a SocialNPC picks new friendships in the right order. First
	 * priority should be given to ACCEPTED FriendRequests that the SocialNPC sent out previously.
	 * Second priority should be given to FriendRequests sent to this SocialNPC by others. Finally,
	 * if the SocialNPC still doesn't have enough friends, it should send out enough FriendRequests 
	 * so satisfy it's totalDesiredFriends requirement. This test uses a SNPC set to not be a
	 * node, so it will only look in the room it is in for new friends.
	 */
	@Test
	public void testPickNewFriendsNonbroker()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		MockRoom2 room = new MockRoom2();
		Hashtable<SocialNPC, FriendRequest> bobsSentRequests = bob.getFrReqList();
		Hashtable<SocialNPC, FriendRequest> bobsReceivedRequests = bob.getFrResponseList();
		ArrayList<SocialNPC> newFriends;

		room.addNPC(bob);
		room.addNPC(bill);
		room.addNPC(sandy);
		bob.setPersonability(1.0);
		bill.setPersonability(1.0);
		sandy.setPersonability(1.0);

		//Bob doesn't want any friends, so he should not pick any.
		assertEquals(0, bob.pickNewFriends().size());

		//Bob wants 1 friend. He sent a request to Bill, which was accepted. Sandy sent Bob a request.
		//Bob should decide to be friends with Bill over Sandy. Bob should not create any new requests.
		bob.setTotalDesiredFriends(1);
		bob.sendFriendRequest(bill);
		bill.getFrResponseList().get(bob).accept();
		sandy.sendFriendRequest(bob);
		FriendRequest sandyToBob = bobsReceivedRequests.get(sandy);

		newFriends = bob.pickNewFriends();

		assertEquals(1, newFriends.size()); //1 new friend
		assertTrue(newFriends.contains(bill)); //new friend is Bill
		assertEquals(FriendRequestStatus.REJECTED, sandyToBob.getState()); //don't want to be friends with Sandy
		//only sent request should be from Bob to Bill
		assertEquals(1, bobsSentRequests.size());
		assertTrue(bobsSentRequests.containsKey(bill));
		assertEquals(1, bill.getFrResponseList().size());
		assertTrue(bill.getFrResponseList().containsKey(bob));

		/**
		 * Bob wants 1 friend, but none of the requests he sent have been accepted. Sandy and Bill
		 * have both sent Bob requests. Because Sandy sent her request first, Bob should pick
		 * Sandy over Bill. Bob should not send any requests.
		 */
		bob.getFrReqList().clear();
		bob.getFrReqListOrder().clear();
		bob.getFrResponseList().clear();
		bob.getFrResponseListOrder().clear();
		bill.getFrResponseList().clear();
		bill.getFrResponseListOrder().clear();
		sandy.getFrReqList().clear();
		sandy.getFrReqListOrder().clear();

		sandy.sendFriendRequest(bob);
		bill.sendFriendRequest(bob);
		sandyToBob = sandy.getFrReqList().get(bob);
		FriendRequest billToBob = bill.getFrReqList().get(bob);

		newFriends = bob.pickNewFriends();

		//Bob isn't going to be doing the work to create the new friendship
		assertEquals(0, newFriends.size());
		//Bob shouldn't send any requests
		assertEquals(0, bobsSentRequests.size());
		//Bob should accept Sandy's request
		assertEquals(FriendRequestStatus.ACCEPTED, sandyToBob.getState());
		//Bob should reject Bill's request
		assertEquals(FriendRequestStatus.REJECTED, billToBob.getState());

		//Bob wants 2 friends. None of his previously sent requests were accepted and no requests have
		//been sent to him, so he should send out 2 requests: 1 to Sandy and one to Bill.
		sandy.getFrReqList().clear();
		sandy.getFrReqListOrder().clear();
		bill.getFrReqList().clear();
		bill.getFrReqListOrder().clear();
		bob.getFrResponseList().clear();
		bob.getFrResponseListOrder().clear();

		bob.setTotalDesiredFriends(2);

		newFriends = bob.pickNewFriends();

		assertEquals(0, newFriends.size());
		assertEquals(2, bobsSentRequests.size());
		assertTrue(bob.getFrReqList().containsKey(sandy));
		assertTrue(bob.getFrReqList().containsKey(bill));

		QuestGenerator.clear();
	}
	
	/**
	 * This test makes sure that a SNPC set up as a broker will properly pick new friends.
	 */
	@Test
	public void testPickNewFriendsBroker()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		MockRoom2 room1 = new MockRoom2();
		MockRoom2 room2 = new MockRoom2();
		Hashtable<SocialNPC, FriendRequest> bobsSentRequests = bob.getFrReqList();
		Hashtable<SocialNPC, FriendRequest> bobsReceivedRequests = bob.getFrResponseList();
		ArrayList<SocialNPC> newFriends;
		
		room1.setTitle("room 1");
		room2.setTitle("room 2");
		room1.addExit(room2, Exit.NORTH);
		room2.addExit(room1, Exit.SOUTH);
		
		room1.addNPC(bob);
		room2.addNPC(bill);
		//room2.addNPC(sandy);
		
		bob.setIsBrokerNode(true);
		bob.setPersonability(1.0);
		bob.setTotalDesiredFriends(2);

		assertEquals(bill, bob.pickNewFriendshipTarget());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that findAcceptedRequsts() works properly.
	 */
	@Test
	public void testFindAcceptedFriends()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);

		bob.setTotalDesiredFriends(2);
		bill.setTotalDesiredFriends(2);

		//if no requests have been sent, the frReqList is empty, so no ACCEPTED requests should be found
		assertEquals(0, bob.findAcceptedRequests().size());

		bob.sendFriendRequest(sandy);
		bob.sendFriendRequest(bill);
		bill.sendFriendRequest(bob);

		FriendRequest bobToSandy = sandy.getFrResponseList().get(bob);
		FriendRequest bobToBill = bill.getFrResponseList().get(bob);

		bobToSandy.reject();//Sandy doesn't want any new friends
		bobToBill.accept();//Bill does want a new friend
		//Bob hasn't gotten the chance to evaluate his received requests yet

		//Bill accepted Bob's request and Sandy did not
		assertEquals(1, bob.findAcceptedRequests().size());
		assertTrue(bob.findAcceptedRequests().contains(bill));

		//Bob hasn't decided about Bill's request yet
		assertEquals(0, bill.findAcceptedRequests().size());

		//if Bob already has as many friends as he wants, the list should be empty
		bob.setTotalDesiredFriends(1);
		bob.addFriend(bill, new MockFeelings());

		assertEquals(0, bob.findAcceptedRequests().size());

		QuestGenerator.clear();
	}

	/**
	 * TODO: personability should play some role in deciding when to accept a FriendRequest
	 * 
	 * 
	 * This test makes sure that evalFriendsRequests() works properly. A SocialNPC should be able to
	 * decide how many FriendRequests that it received it should accept.
	 */
	@Test
	public void testEvalFriendRequests()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC jane = new SocialNPC(3, "Jane", "She wears thick gloves.", 50, 5, 10, 1);

		bill.sendFriendRequest(bob);
		sandy.sendFriendRequest(bob);
		jane.sendFriendRequest(bob);
		bob.setPersonability(1.0);
		bill.setPersonability(1.0);
		sandy.setPersonability(1.0);
		jane.setPersonability(1.0);

		Hashtable<SocialNPC, FriendRequest> bobsRespList = bob.getFrResponseList();
		FriendRequest billToBob = bobsRespList.get(bill);
		FriendRequest sandyToBob = bobsRespList.get(sandy);
		FriendRequest janeToBob = bobsRespList.get(jane);

		bob.setTotalDesiredFriends(0);
		bob.evalFriendRequests(0);

		//Bob doesn't want any friends, so he should not accept any of the requests.
		assertEquals(FriendRequestStatus.REJECTED, billToBob.getState());
		assertEquals(FriendRequestStatus.REJECTED, sandyToBob.getState());
		assertEquals(FriendRequestStatus.REJECTED, janeToBob.getState());

		//FriendRequests can't be changed once they're accepted or rejected, so need to remake 
		//them each time...
		bobsRespList.clear();
		bob.getFrResponseListOrder().clear();
		bill.sendFriendRequest(bob);
		sandy.sendFriendRequest(bob);
		jane.sendFriendRequest(bob);
		billToBob = bobsRespList.get(bill);
		sandyToBob = bobsRespList.get(sandy);
		janeToBob = bobsRespList.get(jane);

		//Bob wants one friend now, so he should accept Bill's request and reject the other two.
		bob.setTotalDesiredFriends(1);
		bob.evalFriendRequests(0);

		assertEquals(FriendRequestStatus.ACCEPTED, billToBob.getState());
		assertEquals(FriendRequestStatus.REJECTED, sandyToBob.getState());
		assertEquals(FriendRequestStatus.REJECTED, janeToBob.getState());

		bobsRespList.clear();
		bob.getFrResponseListOrder().clear();
		bill.sendFriendRequest(bob);
		sandy.sendFriendRequest(bob);
		jane.sendFriendRequest(bob);
		billToBob = bobsRespList.get(bill);
		sandyToBob = bobsRespList.get(sandy);
		janeToBob = bobsRespList.get(jane);

		//Bob wants 5 friends now, so he should accept all the requests.
		bob.setTotalDesiredFriends(5);
		bob.evalFriendRequests(0);

		assertEquals(FriendRequestStatus.ACCEPTED, billToBob.getState());
		assertEquals(FriendRequestStatus.ACCEPTED, sandyToBob.getState());
		assertEquals(FriendRequestStatus.ACCEPTED, janeToBob.getState());

		bobsRespList.clear();
		bob.getFrResponseListOrder().clear();
		bill.sendFriendRequest(bob);
		sandy.sendFriendRequest(bob);
		jane.sendFriendRequest(bob);
		billToBob = bobsRespList.get(bill);
		sandyToBob = bobsRespList.get(sandy);
		janeToBob = bobsRespList.get(jane);

		//Bob wants 3 friends, but he already accepted a FriendRequest from someone else, 
		//so he should only accept the first two requests
		bob.setTotalDesiredFriends(3);
		bob.evalFriendRequests(1);

		assertEquals(FriendRequestStatus.ACCEPTED, billToBob.getState());
		assertEquals(FriendRequestStatus.ACCEPTED, sandyToBob.getState());
		assertEquals(FriendRequestStatus.REJECTED, janeToBob.getState());
		
		//FriendRequest acceptance rate should be based on personability of the SNPC
		double bobsPersonability = 0.5;
		bob.setTotalDesiredFriends(1);
		bob.setPersonability(bobsPersonability); //Bob should accept FriendRequests half the time
		
		int counter = 0;
		int numAccepted;
		int numRuns = 1000;
		for(int i = 0; i < numRuns; i++)
		{
			bobsRespList.clear();
			bob.getFrResponseListOrder().clear();
			bill.sendFriendRequest(bob);
			numAccepted = 0;
			
			bob.evalFriendRequests(0);
			
			for(int j = 0; j < bob.getFrResponseListOrder().size(); j++)
			{
				if(bob.getFrResponseList().get(bob.getFrResponseListOrder().get(j)).getState() ==
				   FriendRequestStatus.ACCEPTED)
				{
					numAccepted++;
				}
			}

			if(numAccepted == 1)
			{
				counter++;
			}else if(numAccepted == 0)
			{
				//no requests accepted
			}else
			{
				fail("Bob accepted " + counter + " requests. He should only accept 1.");
			}
		}
		
		//Bob should accept requests half the time (+/- error)
		assertTrue("Counter: " + counter, counter >= (numRuns * bobsPersonability) - (error * numRuns) &&
										  counter <= (numRuns * bobsPersonability) + (error * numRuns));

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that sendFriendRequests() works properly. It should send new
	 * FriendRequests to other valid SocialNPCs (using pickNewFriendshipTarget()) until 
	 * enough have been sent that if they were all accepted, the SocialNPC would meet it's 
	 * required totalDesiredFriends.
	 */
	@Test
	public void testSendFriendRequests()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC jane = new SocialNPC(3, "Jane", "She wears thick gloves.", 50, 5, 10, 1);
		MockRoom2 room = new MockRoom2();

		Hashtable<SocialNPC, FriendRequest> sentRequests = bob.getFrReqList();

		room.addNPC(bob);
		room.addNPC(bill);
		room.addNPC(sandy);
		room.addNPC(jane);

		//Bob doesn't want any friends, so he should not send any reqeusts.
		bob.setTotalDesiredFriends(0);
		bob.sendFriendRequests(0);

		assertEquals(0, sentRequests.size());

		//Bob wants 1 friend, so he should send 1 request.
		bob.setTotalDesiredFriends(1);
		bob.sendFriendRequests(0);

		assertEquals(1, sentRequests.size());

		//Bob wants 5 friends. Between sent and received requests, he's got 3 new friends lined up,
		//so he should only send 2 requests.
		sentRequests.clear();
		bob.setTotalDesiredFriends(5);
		bob.sendFriendRequests(3);

		assertEquals(2, sentRequests.size());

		//Bob wants 5 friends. He's already friends with Bill and Sandy, so he should send a request
		//to Jane
		sentRequests.clear();
		bob.addFriend(bill, new MockFeelings());
		bob.addFriend(sandy, new MockFeelings());
		bob.sendFriendRequests(0);

		assertEquals(1, sentRequests.size());
		assertTrue(sentRequests.containsKey(jane));

		//Bob wants 5 friends. He's already friends with Bill, Sandy, and Jane, so he should send
		//no requests
		sentRequests.clear();
		bob.addFriend(jane, new MockFeelings());
		bob.sendFriendRequests(0);

		assertEquals(0, sentRequests.size());
	}

	/**
	 * This test makes sure that the frReqList and frResponseList are emptied
	 */
	@Test
	public void testCleanFrReqLists()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC fred = new SocialNPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
		SocialNPC mike = new SocialNPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);

		//cleaning the lists when they have no entries should not cause any problems
		bob.cleanFrReqList(FriendRequestLists.REQUEST_LIST);
		bob.cleanFrReqList(FriendRequestLists.RESPONSE_LIST);

		//Bob will have 2 entries in his frResponseList
		jill.sendFriendRequest(bob);
		sandy.sendFriendRequest(bob);
		FriendRequest jillToBob = jill.getFrReqList().get(bob);
		FriendRequest sandyToBob = sandy.getFrReqList().get(bob);

		//...and 2 entries in his frReqList
		bob.sendFriendRequest(fred);
		bob.sendFriendRequest(mike);
		FriendRequest bobToFred = fred.getFrResponseList().get(bob);
		FriendRequest bobToMike = mike.getFrResponseList().get(bob);

		Hashtable<SocialNPC, FriendRequest> responseList = bob.getFrResponseList();
		ArrayList<SocialNPC> responseListOrder = bob.getFrResponseListOrder();
		Hashtable<SocialNPC, FriendRequest> requestList = bob.getFrReqList();
		ArrayList<SocialNPC> requestListOrder = bob.getFrReqListOrder();
		

		//Bob rejected Jill's request and ignore Sandy's
		jillToBob.reject();

		//Mike accepted Bob's request; Fred rejected it
		bobToMike.accept();
		bobToFred.reject();

		//each list should only have the rejected entries removed, and clean() should
		//only touch one list at a time
		
		bob.cleanFrReqList(FriendRequestLists.REQUEST_LIST);
		//sent requests
		assertEquals(1, requestList.size());
		assertEquals(1, requestListOrder.size());
		assertTrue(requestList.contains(bobToMike));
		assertTrue(requestListOrder.contains(mike));
		//received requests
		assertEquals(2, responseList.size());
		assertEquals(2, responseListOrder.size());
		assertTrue(responseList.contains(sandyToBob));
		assertTrue(responseListOrder.contains(sandy));
		assertTrue(responseList.contains(jillToBob));
		assertTrue(responseListOrder.contains(jill));
		
		
		bob.cleanFrReqList(FriendRequestLists.RESPONSE_LIST);
		//received requests 
		assertEquals(1, responseList.size());
		assertEquals(1, responseListOrder.size());
		assertTrue(responseList.contains(sandyToBob));
		assertTrue(responseListOrder.contains(sandy));


		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a SocialNPC can properly remove a FriendRequest using
	 * removeFriendRequest(). A request should only be removed if it is ACCEPTED.
	 */
	@Test
	public void testRemoveFriendRequest()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);

		bob.sendFriendRequest(jill);
		jill.sendFriendRequest(sandy);
		sandy.sendFriendRequest(bob);

		FriendRequest bobToJill = jill.getFrResponseList().get(bob);
		FriendRequest jillToSandy = sandy.getFrResponseList().get(jill);
		FriendRequest sandyToBob = bob.getFrResponseList().get(sandy);

		//All of the requests are WAITING, so none should be removed.
		bob.removeFriendRequest(jill);
		bob.removeFriendRequest(sandy);

		assertTrue(bob.getFrReqList().contains(bobToJill));
		assertTrue(bob.getFrReqListOrder().contains(jill));

		assertTrue(jill.getFrResponseList().contains(bobToJill));
		assertTrue(jill.getFrResponseListOrder().contains(bob));

		assertTrue(jill.getFrReqList().contains(jillToSandy));
		assertTrue(jill.getFrReqListOrder().contains(sandy));

		assertTrue(sandy.getFrResponseList().contains(jillToSandy));
		assertTrue(sandy.getFrResponseListOrder().contains(jill));

		assertTrue(sandy.getFrReqList().contains(sandyToBob));
		assertTrue(sandy.getFrReqListOrder().contains(bob));

		assertTrue(bob.getFrResponseList().contains(sandyToBob));
		assertTrue(bob.getFrResponseListOrder().contains(sandy));

		//Jill will accept Bob's request. Sandy will reject Jill's request. Bob will ignore Sandy's request.
		bobToJill.accept();
		jillToSandy.reject();

		bob.removeFriendRequest(jill);
		jill.removeFriendRequest(bob);
		jill.removeFriendRequest(sandy);
		sandy.removeFriendRequest(jill);
		bob.removeFriendRequest(sandy);
		sandy.removeFriendRequest(bob);

		assertFalse(bob.getFrReqList().contains(bobToJill));
		assertFalse(bob.getFrReqListOrder().contains(jill));

		assertFalse(jill.getFrResponseList().contains(bobToJill));
		assertFalse(jill.getFrResponseListOrder().contains(bob));

		assertTrue(jill.getFrReqList().contains(jillToSandy));
		assertTrue(jill.getFrReqListOrder().contains(sandy));

		assertTrue(sandy.getFrResponseList().contains(jillToSandy));
		assertTrue(sandy.getFrResponseListOrder().contains(jill));

		assertTrue(sandy.getFrReqList().contains(sandyToBob));
		assertTrue(sandy.getFrReqListOrder().contains(bob));

		assertTrue(bob.getFrResponseList().contains(sandyToBob));
		assertTrue(bob.getFrResponseListOrder().contains(sandy));
	}

	/**
	 * This test makes sure that a SocialNPC can properly send and receive FriendRequests.
	 */
	@Test
	public void testSendReceiveFriendRequests()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC jane = new SocialNPC(0, "Jane", "She wears overalls.", 50, 5, 10, 1);

		bob.sendFriendRequest(bill);
		jane.sendFriendRequest(bill);
		bob.sendFriendRequest(jane);

		Hashtable<SocialNPC, FriendRequest> bobsRequests = bob.getFrReqList();
		Hashtable<SocialNPC, FriendRequest> billsResponses = bill.getFrResponseList();
		ArrayList<SocialNPC> requestOrder = bob.getFrReqListOrder();
		ArrayList<SocialNPC> responseOrder = bill.getFrResponseListOrder();

		//test the order
		assertEquals(bill, requestOrder.get(0));
		assertEquals(jane, requestOrder.get(1));
		assertEquals(bob, responseOrder.get(0));
		assertEquals(jane, responseOrder.get(1));

		//make sure the list contains the right keys
		assertTrue(bobsRequests.containsKey(bill));
		assertTrue(bobsRequests.containsKey(jane));
		assertTrue(billsResponses.containsKey(bob));
		assertTrue(billsResponses.containsKey(jane));

		//make sure that the FriendRequests are being created correctly
		FriendRequest bobToBill = bobsRequests.get(bill);
		FriendRequest janeToBill = billsResponses.get(jane);
		FriendRequest bobToJane = bobsRequests.get(jane);

		assertEquals(bob, bobToBill.getRequester());
		assertEquals(bill, bobToBill.getRequestee());
		assertEquals(jane, janeToBill.getRequester());
		assertEquals(bill, janeToBill.getRequestee());
		assertEquals(bob, bobToJane.getRequester());
		assertEquals(jane, bobToJane.getRequestee());

		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a SocialNPC can identify relationships that are not useful to it.
	 */
	@Test
	public void testIdentifyUnproductiveFriends()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		MockFeelings bobForJill = new MockFeelings();
		MockFeelings bobForSandy = new MockFeelings();
		ArrayList<SocialNPC> toBeRemoved = null;

		//should work fine and return an empty ArrayList when Bob has no friends
		toBeRemoved = bob.identifyUnproductiveFriendships(1);
		assertEquals(0, toBeRemoved.size());
 
		bob.addFriend(bill);
		bill.addFriend(bob);

		/**
		 * The relationship has 0 trust initially, so it decays at 3 intimacy per turn.
		 * With 35 intimacy, it should reach min intimacy in 12 turns
		 */
		
		assertEquals(0, bob.identifyUnproductiveFriendships(11).size());
		assertEquals(1, bob.identifyUnproductiveFriendships(12).size());
		assertTrue(bob.identifyUnproductiveFriendships(12).contains(bill));
		
		/**
		 * List should be ordered...
		 * 
		 * jill (t=-5, i=20) decay in 4 turns
		 * bill (t=0, i=20) decay in 7 turns
		 * sandy (t=-3, i=40) decay in 8
		 */
		
		bobForJill.setTrust(-5);
		bobForJill.setIntimacy(20);
		bob.getRelationships().get(bill).setIntimacy(20);
		bob.getRelationships().get(bill).setTrust(0);
		bobForSandy.setTrust(-3);
		bobForSandy.setIntimacy(40);
		bob.addFriend(jill, bobForJill);
		bob.addFriend(sandy, bobForSandy);
		
		toBeRemoved = bob.identifyUnproductiveFriendships(10);		
		
		assertEquals(3, toBeRemoved.size());
		assertEquals(jill, toBeRemoved.get(0));
		assertEquals(bill, toBeRemoved.get(1));
		assertEquals(sandy, toBeRemoved.get(2));
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a SocialNPC can identify relationships that are dangerously
	 * near being terminated due to low intimacy.
	 */
	@Test
	public void testIdentifyLowIntimacyFriends()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		MockFeelings bobsFeelsForJill = new MockFeelings();
		MockFeelings bobsFeelsForSandy = new MockFeelings();
		ArrayList<SocialNPC> list;

		//the list should be empty if Bob has no friends
		list = bob.identifyLowIntimacyFriends();

		assertEquals(0, list.size());

		//Bob has 2 friends, both of which are productive relationships.
		bob.addFriend(jill, bobsFeelsForJill);
		bob.addFriend(sandy, bobsFeelsForSandy);
		bobsFeelsForJill.setIntimacy(50);
		bobsFeelsForSandy.setIntimacy(50);

		list = bob.identifyLowIntimacyFriends();

		//list should be empty since neither of Bobs friendships are close to being terminated
		assertEquals(0, list.size());

		//the relationships have decayed... (border cases)
		bobsFeelsForJill.setIntimacy(20);
		bobsFeelsForSandy.setIntimacy(19);

		list = bob.identifyLowIntimacyFriends();

		assertEquals(1, list.size());
		assertTrue(list.contains(sandy));

		//the elements of the list should be in ascending order
		bobsFeelsForJill.setIntimacy(5);

		list = bob.identifyLowIntimacyFriends();

		assertEquals(jill, list.get(0));
		assertEquals(sandy, list.get(1));

		bobsFeelsForSandy.setIntimacy(3);

		list = bob.identifyLowIntimacyFriends();

		assertEquals(jill, list.get(1));
		assertEquals(sandy, list.get(0));
	}

	/**
	 * This test makes sure that idnetifyLowTrustFriends works properly. The list should only
	 * contain friends of the SocialNPC that the SocialNPC does not trust (trust is < 0). The
	 * list should be in ascending order based on trust (lowest values at beginning).
	 */
	@Test
	public void testIdentifyLowTrustFriends()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		MockFeelings bobsFeelsForJill = new MockFeelings();
		MockFeelings bobsFeelsForSandy = new MockFeelings();
		ArrayList<SocialNPC> list;

		//the list should be empty if Bob has no friends
		list = bob.identifyLowIntimacyFriends();

		assertEquals(0, list.size());

		//Bob has 2 friends, both of which are productive relationships.
		bob.addFriend(jill, bobsFeelsForJill);
		bob.addFriend(sandy, bobsFeelsForSandy);
		bobsFeelsForJill.setTrust(2);
		bobsFeelsForSandy.setTrust(2);

		list = bob.identifyLowTrustFriends();

		//list should be empty since neither of Bobs friendships are close to being terminated
		assertEquals(0, list.size());

		//the relationships have decayed... (border cases)
		bobsFeelsForJill.setTrust(0);
		bobsFeelsForSandy.setTrust(-1);

		list = bob.identifyLowTrustFriends();

		assertEquals(1, list.size());
		assertTrue(list.contains(sandy));

		//the elements of the list should be in ascending order
		bobsFeelsForJill.setTrust(-2);

		list = bob.identifyLowTrustFriends();

		assertEquals(jill, list.get(0));
		assertEquals(sandy, list.get(1));

		bobsFeelsForSandy.setTrust(-3);

		list = bob.identifyLowTrustFriends();

		assertEquals(jill, list.get(1));
		assertEquals(sandy, list.get(0));
	}

	/**
	 * This test makes sure that the list returned by identifyGrowingRelationships() is
	 * ordered properly and contains the right elements.
	 */
	@Test
	public void testIdentifyGrowingRelationships()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC fred = new SocialNPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
		SocialNPC mike = new SocialNPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
		SocialNPC seamus = new SocialNPC(0, "Seamus", "He wears green.", 50, 5, 10, 1);
		SocialNPC lucy = new SocialNPC(0, "Lucy", "She wears pink.", 50, 5, 10, 1);
		SocialNPC don = new SocialNPC(0, "Don", "He wears green.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "She wears pink.", 50, 5, 10, 1);

		MockFeelings bobForJill = new MockFeelings();
		MockFeelings bobForSandy = new MockFeelings();
		MockFeelings bobForFred = new MockFeelings();
		MockFeelings bobForMike = new MockFeelings();
		MockFeelings bobForSeamus = new MockFeelings();
		MockFeelings bobForLucy = new MockFeelings();
		MockFeelings bobForDon = new MockFeelings();
		MockFeelings bobForBill = new MockFeelings();

		bob.addFriend(sandy, bobForSandy);
		bob.addFriend(fred, bobForFred);
		bob.addFriend(jill, bobForJill);
		bob.addFriend(mike, bobForMike);
		bob.addFriend(seamus, bobForSeamus);
		bob.addFriend(lucy, bobForLucy);
		bob.addFriend(don, bobForDon);
		bob.addFriend(bill, bobForBill);

		bobForJill.setTrust(1);
		bobForJill.setIntimacy(80);
		
		bobForSandy.setIntimacy(30);
		bobForSandy.setTrust(4);
		
		bobForFred.setTrust(1);
		bobForFred.setIntimacy(70);
		
		bobForMike.setTrust(5);
		bobForMike.setIntimacy(70);
		
		bobForSeamus.setTrust(-1);
		bobForLucy.setIntimacy(15);
		
		bobForBill.setTrust(3);
		bobForBill.setIntimacy(30);
		
		bobForDon.setTrust(5);
		bobForDon.setIntimacy(81);

		/**
		 * The list should be ordered:
		 * Sandy (t=4, i=30) decay in 20
		 * Bill (t=3, i=30) decay in 15
		 * Jill (t=1, i=80) decay in 27
		 * Fred (t=1, i=70) decay in 23
		 * Mike (t=5, i=70) decay in 70

		 * 
		 * Don is not listed (t = 5, i=81) decay in 81    intimacy above upper limit
		 * Seamus is not listed (t=-1, i=20) decay in 5   trust below lower limit
		 * Lucy is not listed (t = 0, i=15) decay in 4    decay too soon
		 */

		ArrayList<SocialNPC> list = bob.identifyGrowingRelationships(10);

		assertEquals(sandy, list.get(0));
		assertEquals(bill, list.get(1));
		assertEquals(jill, list.get(2));
		assertEquals(fred, list.get(3));
		assertEquals(mike, list.get(4));

		assertFalse(list.contains(don));
		assertFalse(list.contains(seamus));
		assertFalse(list.contains(lucy));
	}


	/**
	 * This test makes sure that a SocialNPC can properly terminate a given list of relationships.
	 */
	@Test
	public void testRemoveFriendSet()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC fred = new SocialNPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
		SocialNPC mike = new SocialNPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
		ArrayList<SocialNPC> toBeRemoved = new ArrayList<SocialNPC>();

		//Shouldn't cause any problems when the SocialNPC doesn't have any friends.
		bob.removeFriendSet(null);

		//Shouldn't do anything when trying to remove a SocialNPC that Bob is not friends with.
		//when Bob has no friends
		toBeRemoved.add(jill);
		bob.removeFriendSet(toBeRemoved);

		assertEquals(0, bob.getFriends().size());

		//Shouldn't do anything when trying to remove a SocialNPC that Bob is not friends with
		//when Bob has friends
		bob.addFriend(sandy, new MockFeelings());
		bob.addFriend(fred, new MockFeelings());
		bob.addFriend(mike, new MockFeelings());

		bob.removeFriendSet(toBeRemoved);

		assertEquals(3, bob.getFriends().size());

		//Should remove all the SocialNPCs specified and leave others alone.
		bob.addFriend(jill, new MockFeelings());
		toBeRemoved.add(sandy);

		bob.removeFriendSet(toBeRemoved);

		assertEquals(2, bob.getFriends().size());
		assertTrue(bob.getFriends().contains(fred));
		assertTrue(bob.getFriends().contains(mike));

		QuestGenerator.clear();
	}
	
	
	/**
	 * This test makes sure that SocialNPC can properly remove a current friend.
	 */
	@Test
	public void testUnfriend()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC ben = new SocialNPC(0, "Ben", "He wears button-down shirts", 50, 5, 10, 1);
		MockItem2 item = new MockItem2();
		
		bob.getQuestGenerator().roomUpdate(null, item, SWRoomUpdateType.ITEM_ADDED);
		
		bob.addFriend(jill);
		jill.addFriend(bob);
		jill.addFriend(ben);
		ben.addFriend(jill);
		bob.addFriend(sandy);
		sandy.addFriend(bob);
		
		/**
		 * The relationship between Bob and Jill should be cleanly severed
		 * sandy --- bob --- jill --- ben
		 */
		
		//Bob made a FavorQuest targeting Jill
		bob.setCurrentCapital(500);
		FavorQuest favQuest = bob.getQuestGenerator().genFavorQuest(bob, jill);
		bob.addQuest(favQuest);
		
		//Bob also made a HomewreckerQuest targeting the relationship between Jill and Ben
		bob.setCurrentCapital(500);
		HomewreckerQuest wreckQuest = bob.makeHomewreckerQuest();
		bob.addQuest(wreckQuest);
		
		//Bob created a GiftQuest targeting Jill
		bob.setCurrentCapital(500);
		GiftQuest giftQuest = bob.getQuestGenerator().genGiftQuest(bob, jill);
		bob.addQuest(giftQuest);
		
		//Bob owes a social debt to Jill, and Jill asked Bob for a favor
		bob.getRelationships().get(jill).setSocialDebtOwed(500);
		jill.askFavor(bob);
		
		assertTrue(bob.getFriends().contains(jill));
		assertTrue(bob.getRelationships().containsKey(jill));
		assertTrue(bob.getAvailableQuests().contains(favQuest));
		assertTrue(bob.getFavorRequests().contains(jill));
		
		bob.unFriend(jill);
		
		//Jill should not be in Bob's list of friends, and Sandy should
		assertFalse(bob.getFriends().contains(jill));
		assertFalse(bob.getRelationships().containsKey(jill));
		assertTrue(bob.getFriends().contains(sandy));
		assertTrue(bob.getRelationships().containsKey(sandy));
		//the FavorQuest targeting Jill should be gone too
		assertFalse(bob.getAvailableQuests().contains(favQuest));
		//the HomewreckerQuest targetting Jill and Ben should be gone
		assertFalse(bob.getAvailableQuests().contains(wreckQuest));
		//the GiftQuest should NOT be removed
		assertTrue(bob.getAvailableQuests().contains(giftQuest));
		//the favor from Jill that Bob agreed to perform should be gone
		assertFalse(bob.getFavorRequests().contains(jill));		

		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a SocialNPC can properly select a target when trying to initiate
	 * a new friendship
	 */
	@Test
	public void testSelectNewFriendshipTarget()
	{
		SocialNPC bob = new SocialNPC(1, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC jill = new SocialNPC(2, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
		SocialNPC sandy = new SocialNPC(3, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
		SocialNPC fred = new SocialNPC(4, "Fred", "He wears plaid.", 50, 5, 10, 1);
		SocialNPC mike = new SocialNPC(5, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
		SocialNPC john = new SocialNPC(5, "John", "He wears sweat pants.", 50, 5, 10, 1);
		SocialNPC tim = new SocialNPC(5, "tim", "He wears rainbow socks.", 50, 5, 10, 1);
		MockNPC2 someOtherGuy = new MockNPC2();
		MockRoom2 daRoom = new MockRoom2();
		MockRoom2 otherRoom = new MockRoom2();

		//this line should prompt the console to spit out an error
		bob.pickNewFriendshipTarget();

		daRoom.addNPC(bob);
		//Bob is in a room now, but he's alone. The method should return null.
		assertNull(bob.pickNewFriendshipTarget());

		/**
		 * Bob is friends with Jill.
		 * Fred and Mike are in a different room from Bob
		 * John sent Bob a FriendRequest.
		 * bob sent Tim a FriendRequest.
		 * someOtherGuy is not a SocialNPC.
		 * The only valid targets for Bob to try and make friends with are Sandy and Fred.
		 */

		bob.addFriend(jill, new Feelings());
		daRoom.addNPC(jill);
		daRoom.addNPC(sandy);
		daRoom.addNPC(someOtherGuy);
		daRoom.addNPC(fred);
		otherRoom.addNPC(mike);
		john.sendFriendRequest(bob);
		bob.sendFriendRequest(tim);

		int sandyCounter = 0;
		int fredCounter = 0;
		SocialNPC target = null;
		int numRuns = 1000;

		for (int i = 0; i < numRuns; i++)
		{
			target = bob.pickNewFriendshipTarget();

			if (target.equals(sandy))
			{
				sandyCounter++;
			} else if (target.equals(fred))
			{
				fredCounter++;
			} else
			{
				fail("Wrong target: " + target);
			}
		}

		//valid friends should have an equal chance (+/- error) of being selected
		assertTrue("Sandy: " + sandyCounter, sandyCounter >= numRuns * 0.5 - (numRuns * error) && sandyCounter <= numRuns * 0.5 + (numRuns * error));
		assertTrue("Fred: " + fredCounter, fredCounter >= numRuns * 0.5 - (numRuns * error) && fredCounter <= numRuns * 0.5 + (numRuns * error));

		QuestGenerator.clear();
	}

	/**
	 * This test checks to make sure that one SocialNPC can ask another a favor. The test also makes
	 * sure that the requestee properly evaluates the favor request and responds correctly.
	 */
	@Test
	public void testAskAndEvaluateFavor()
	{
		SocialNPC bob = new SocialNPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
		SocialNPC bill = new SocialNPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
		Feelings bobsFeels = new Feelings();
		Feelings billsFeels = new Feelings();
		SocialQuestDifficulty difficulty;

		//add the Item that the favor will be performed on
		MockItem2 favorItem = new MockItem2();
		bob.getQuestGenerator().roomUpdate(null, favorItem, SWRoomUpdateType.ITEM_ADDED);

		//if the two are not friends, Bob should not agree to any favors
		bill.askFavor(bob);

		assertEquals(0, bob.getFavorRequests().size());

		//if Bob does not owe Bill any social debt, then Bob should not agree to any favors
		bob.addFriend(bill, bobsFeels);
		bill.addFriend(bob, billsFeels);
		bobsFeels.setSocialDebtOwed(0);
		bill.askFavor(bob);

		assertEquals(0, bob.getFavorRequests().size());

		/**
		 * if Bob only has enough social capital for an Easy quest, then that's the difficulty
		 * he should pick, even if he owes more debt than that
		 */
		bob.setCurrentCapital(500);
		bobsFeels.setSocialDebtOwed(3000);

		bill.askFavor(bob);
		difficulty = bob.evalFavorRequest(bill);

		assertEquals(1, bob.getFavorRequests().size());
		assertEquals(SocialQuestDifficulty.EASY, difficulty);

		//same deal with medium difficulty
		bob.getFavorRequests().clear();
		bob.setCurrentCapital(1250);

		bill.askFavor(bob);
		difficulty = bob.evalFavorRequest(bill);

		assertEquals(1, bob.getFavorRequests().size());
		assertEquals(SocialQuestDifficulty.MEDIUM, difficulty);

		//same deal with hard difficulty
		bob.getFavorRequests().clear();
		bob.setCurrentCapital(1750);

		bill.askFavor(bob);
		difficulty = bob.evalFavorRequest(bill);

		assertEquals(1, bob.getFavorRequests().size());
		assertEquals(SocialQuestDifficulty.HARD, difficulty);

		//same deal with max difficulty
		bob.getFavorRequests().clear();
		bob.setCurrentCapital(2500);

		bill.askFavor(bob);
		difficulty = bob.evalFavorRequest(bill);

		assertEquals(1, bob.getFavorRequests().size());
		assertEquals(SocialQuestDifficulty.YOUMUSTBEPRO, difficulty);

		//make sure Bob doesn't waste his social capital
		bob.getFavorRequests().clear();
		bob.setCurrentCapital(3000);
		bobsFeels.setSocialDebtOwed(1000);

		bill.askFavor(bob);
		difficulty = bob.evalFavorRequest(bill);

		assertEquals(1, bob.getFavorRequests().size());
		assertEquals(SocialQuestDifficulty.MEDIUM, difficulty);
		QuestGenerator.clear();
	}

}

class MockFeelings extends Feelings
{
	public MockFeelings()
	{
		super();
		// TODO Auto-generated constructor stub
	}
}

class MockSocialQuest extends SocialQuest
{
	public MockSocialQuest()
	{
		super("MockSocialQuest", "A mock SocialQuest for testing", new SocialNPC(0, "Test", "Test SocialNPC.", 1, 1, 1, 1), new SocialNPC(0, "Test", "Test SocialNPC.", 1, 1, 1, 1), SocialQuestDifficulty.EASY);
		// TODO Auto-generated constructor stub
	}

	public void setGiver(SocialNPC giver)
	{
		questGiver = giver;
	}
	
	public void setTarget(SocialNPC target)
	{
		questTarget = target;
	}
}

class MockItem2 extends Item
{
	public MockItem2()
	{
		super("Test item", "A mock item for testing", 1, 1);
		// TODO Auto-generated constructor stub
	}
}

class MockRoom2 extends Room
{
	public MockRoom2()
	{
		super(0, "DaMockRoom", "A mock Room used for testing purposes");
		// TODO Auto-generated constructor stub
	}
	
	public void setTitle(String newName)
	{
		this.m_title = newName;
	}
}

class MockNPC2 extends NPC
{
	public MockNPC2()
	{
		super(0, "Mr. Mocky", "A mock NPC used for testing", 1, 1, 0, 1);
		// TODO Auto-generated constructor stub
	}
}

class MockPlayer extends Player
{
	public MockPlayer()
	{
		super(0, "Mocky", "A mock Player", 100);
		// TODO Auto-generated constructor stub
	}
}