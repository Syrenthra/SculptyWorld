package sw.socialNetwork.simulation;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import sw.item.Item;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.quest.Quest;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FriendRequest;
import sw.socialNetwork.Moods;
import sw.quest.QuestGenerator;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.quest.TimedQuest;
import sw.quest.reward.FavorReward;
import sw.quest.reward.RequestFavorReward;
import sw.quest.reward.SocialReward;
import sw.socialNetwork.SocialNetworkDecayRates;


/**
 * @author David Abrams
 * 
 * This set of tests makes sure that SocialNetworkEvents are generated at the right times.
 */
public class TestEventGeneration 
{
	/**
	 * Check to make sure that 	FRIEND_REQUEST_SENT, FRIEND_REQUEST_RECIEVED, 
	 * FRIEND_REQUEST_ACCEPTED, and FRIEND_REQUEST_REJECTED are all generated
	 * by SocialNPC at the correct time.
	 */
	@Test
	public void testFriendRequestEvents()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		
		bob.getSocialNetwork().sendFriendRequest(bill);
		
		FriendRequest bobToBill = bill.getSocialNetwork().getFrResponseList().get(bob);
		ArrayList<SocialNetworkEvent> bobsEvents = bob.getEvents();
		ArrayList<SocialNetworkEvent> billsEvents = bill.getEvents();
		
		assertEquals(1, bobsEvents.size());
		assertEquals(1, billsEvents.size());
		assertEquals(EventTypes.FRIEND_REQUEST_SENT, bobsEvents.get(0).getType());
		assertEquals(EventTypes.FRIEND_REQUEST_RECIEVED, billsEvents.get(0).getType());
		
		bill.getSocialNetwork().acceptFriendRequest(bobToBill);
		
		billsEvents = bill.getEvents();
		assertEquals(2, billsEvents.size());
		assertEquals(EventTypes.FRIEND_REQUEST_ACCEPTED, billsEvents.get(1).getType());
		
		bill.getSocialNetwork().sendFriendRequest(bob);
		FriendRequest billToBob = bob.getSocialNetwork().getFrResponseList().get(bill);
		bob.getSocialNetwork().rejectFriendRequest(billToBob);
		bobsEvents = bob.getEvents();
		
		assertEquals(3, bobsEvents.size());
		assertEquals(EventTypes.FRIEND_REQUEST_REJECTED, bobsEvents.get(2).getType());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that QUEST_CREATED_GIFTQUEST is created by
	 * SocialNPC at the right time.
	 */
	@Test
	public void testCreateGiftQuestEvent()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		MockItem gift = new MockItem();
		bob.setCurrentCapital(500);
		bob.getSocialNetwork().setTotalDesiredFriends(1);
		bob.addQuestItem(gift);
		//QuestGenerator.roomUpdate(null, gift, SWRoomUpdateType.ITEM_ADDED);
		
		bob.getSocialNetwork().sendFriendRequest(bill);
		bill.getSocialNetwork().acceptFriendRequest(bob.getSocialNetwork().getFrReqList().get(bill));
		
		bob.updateTime("", 0);
		
		assertEquals(1, bob.getAvailableQuests().size());
		assertEquals(3, bob.getEvents().size());
		//first event is FR sent, second is capital changed
		assertEquals(EventTypes.QUEST_CREATED_GIFTQUEST, bob.getEvents().get(2).getType());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that QUEST_CREATED_HOMEWRECKER is created by
	 * SocialNPC at the right time.
	 */
	@Test
	public void testCreateHomewreckerQuestEvent()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		MockSocialNPC jane = new MockSocialNPC();
		bob.getSocialNetwork().setGrumpiness(1.0);
		bob.getSocialNetwork().setCurrentMood(Moods.ANGRY);
		bill.getSocialNetwork().setCurrentMood(Moods.ANGRY);
		bob.setCurrentCapital(500);
		
		bob.getSocialNetwork().addFriend(bill);
		bill.getSocialNetwork().addFriend(bob);
		bill.getSocialNetwork().addFriend(jane);
		jane.getSocialNetwork().addFriend(bill);
		
		bob.updateTime("", 0);
		
		assertEquals(1, bob.getAvailableQuests().size());
		assertEquals(2, bob.getEvents().size());
		//first even is change of social capital
		assertEquals(EventTypes.QUEST_CREATED_HOMEWRECKER, bob.getEvents().get(1).getType());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that QUEST_CREATED_FAVORQUEST is created by
	 * SocialNPC at the right times.
	 */
	@Test
	public void testCreateFavorQuestEvent()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
	
		//bob.getQuestGenerator().roomUpdate(null, new MockItem(), SWRoomUpdateType.ITEM_ADDED);
		bob.addQuestItem(new MockItem());
		bob.getSocialNetwork().setTotalDesiredFriends(1);
		bob.getSocialNetwork().setTotalDesiredCapital(5000);
		bob.getSocialNetwork().setCurrentCapital(1000);
		bob.getSocialNetwork().addFriend(bill);
		bill.getSocialNetwork().addFriend(bob);
		
		//Generate event when a 
		bob.getSocialNetwork().getRelationships().get(bill).setIntimacy(12); //3 turns until termination
		
		bob.updateTime("", 0);
		
		assertEquals(1, bob.getAvailableQuests().size());
		//assertTrue(bob.getAvailableQuests().get(0) instanceof FavorQuest);
		assertTrue(bob.getAvailableQuests().get(0).getRewards().get(0) instanceof FavorReward);
		assertEquals(2, bob.getEvents().size());
		//first event is change of social capital
		assertEquals(EventTypes.QUEST_CREATED_FAVORQUEST, bob.getEvents().get(1).getType());
		
		//bob.getQuestGenerator().clear();
	}
	
	/**
	 * This test makes sure that QUEST_CREATE_REQFAVQUEST is created by
	 * SocialNPC at the right time
	 */
	@Test
	public void testCreateReqFavQuestEvent()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		//bob.getQuestGenerator().roomUpdate(null, new MockItem(), SWRoomUpdateType.ITEM_ADDED);
		bob.addQuestItem(new MockItem());
		bob.getSocialNetwork().addFriend(bill);
		bill.getSocialNetwork().addFriend(bob);
		
		//set up the relationship so Bob owes Bill a social debt & will do a favor when asked
		Feelings bobForBill = bob.getSocialNetwork().getRelationships().get(bill);
		bobForBill.setSocialDebtOwed(1000);
		bobForBill.setIntimacy(50);
		bill.askFavor(bob);
		bob.setCurrentCapital(500);
		
		bob.updateTime("", 0);
		
		assertEquals(1, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0).getRewards().get(0) instanceof RequestFavorReward);
		assertEquals(2, bob.getEvents().size());
		//first event is change of social capital
		assertEquals(EventTypes.QUEST_CREATED_REQFAVQUEST, bob.getEvents().get(1).getType());
		
		QuestGenerator.clear();
	}
	
	/**
	 * This test makes sure that QUEST_SUCCESSFUL is created by SocialNPC at
	 * the right time. 
	 */
	@Test
	public void testCreateQuestSuccessFailedEvents()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		MockSocialQuest quest1 = new MockSocialQuest(bob, bill);
		MockSocialQuest quest2 = new MockSocialQuest(bob, bill);
		PC thePlayer = new PC(0, "Playerrr", "The player.", 50);
		
		FavorReward reward1 = new FavorReward(quest1, bill, SocialCapitolCost.CHEAP);
		FavorReward reward2 = new FavorReward(quest2, bill, SocialCapitolCost.CHEAP);
		
		quest1.addPlayer(thePlayer);
		quest2.addPlayer(thePlayer);
		quest1.addReward(reward1);
		quest2.addReward(reward2);
		
		quest1.questSuccessful();
		quest2.questFailed();
		
		assertEquals(2, bob.getEvents().size());
		assertEquals(EventTypes.QUEST_SUCCESSFUL, bob.getEvents().get(0).getType());
		assertEquals(EventTypes.QUEST_FAILED, bob.getEvents().get(1).getType());
		
		QuestGenerator.clear();
	}

	
	/**
	 * This test makes sure that FRIENDSHIP_CREATED is created by SocialNPC at
	 * the right time. The only way that a new friendship can be created is by
	 * successfully completing a GiftQuest.
	 */
	@Test
	public void testCreateFriendshipCreatedEvent()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		PC thePlayer = new PC(0, "Playerrr", "The player.", 50);
		MockItem gift = new MockItem();
		//MockGiftQuest quest = new MockQuest(bob, bill, gift);
		bob.addQuestItem(gift);
		bob.setCurrentCapital(500);
		TimedQuest quest = QuestGenerator.genGiftQuest(bob, bill);
		quest.addPlayer(thePlayer);
		quest.questSuccessful();
		//quest.getItemReward();
		assertTrue(bob.getSocialNetwork().getFriends().contains(bill));
		assertTrue(bill.getSocialNetwork().getFriends().contains(bob));
		assertEquals(3, bob.getEvents().size());
		assertEquals(EventTypes.FRIENDSHIP_CREATED, bob.getEvents().get(1).getType());
		
		QuestGenerator.clear();
	}
	
	/**
	 * This test makes sure that FRIENDSHIP_TERMINATED is created by SocialNPC at
	 * the right time. 
	 */
	@Test
	public void testCreateFriendshipTerminatedEvent()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		MockSocialNPC jane = new MockSocialNPC();
		
		bob.getSocialNetwork().addFriend(bill);
		bill.getSocialNetwork().addFriend(bob);
		bob.getSocialNetwork().addFriend(jane);
		jane.getSocialNetwork().addFriend(bob);
		
		//set up the relationships so Bob will terminate his friendship with Jane
		bob.getSocialNetwork().getRelationships().get(jane).setIntimacy(1);
		
		bob.updateTime("", 0);
		
		assertFalse(bob.getSocialNetwork().getFriends().contains(jane));
		assertEquals(2, bob.getEvents().size());
		//capital change and then termination
		assertEquals(EventTypes.FRIENDSHIP_TERMINATED, bob.getEvents().get(1).getType());
		
		QuestGenerator.clear();
	}
	
	/**
	 * Make sure that events are generated when a SNPC changes moods.
	 */
	@Test
	public void testMoodChange()
	{
		MockSocialNPC bob = new MockSocialNPC();
		MockSocialNPC bill = new MockSocialNPC();
		MockSocialNPC jane = new MockSocialNPC();
		
		bill.getSocialNetwork().setCurrentMood(Moods.ANGRY);
		jane.getSocialNetwork().setCurrentMood(Moods.ANGRY);
		
		assertEquals(Moods.HAPPY, bob.getCurrentMood());
		bob.getSocialNetwork().setGrumpiness(1.0);
		bob.setLastQuestResult(QuestState.FAILED);
		bob.getSocialNetwork().updateMood(bob.getLastQuestResult());
		assertEquals(Moods.ANGRY, bob.getCurrentMood());
		assertEquals(1, bob.getEvents().size());
		assertEquals(EventTypes.MOOD_CHANGE_TO_ANGRY, bob.getEvents().get(0).getType());
		
		bob.getSocialNetwork().setGrumpiness(0.0);
		bob.setLastQuestResult(QuestState.COMPLETED);
		bob.getSocialNetwork().updateMood(bob.getLastQuestResult());
		assertEquals(Moods.HAPPY, bob.getCurrentMood());
		assertEquals(2, bob.getEvents().size());
		assertEquals(EventTypes.MOOD_CHANGE_TO_HAPPY, bob.getEvents().get(1).getType());
		
		bob.getSocialNetwork().setGrumpiness(1.0);
		bob.getSocialNetwork().changeMoodPropagation();
	}
	
}

class MockSocialNPC extends NPC
{
	public MockSocialNPC() 
	{
		super(0, "Mocky", "A mock SocialNPC", 1, 1, 0, 1);
		// TODO Auto-generated constructor stub
	}
}

class MockItem extends Item
{

	public MockItem() {
		super("Mock item", "A mock Item", 1, 1);
		// TODO Auto-generated constructor stub
	}
}

class MockSocialQuest extends Quest
{
	public MockSocialQuest(NPC giver, NPC target) 
	{
		super("The Quest of Mock", "A mock SocialQuest", giver/*, target, SocialCapitolCost.CHEAP*/);
		// TODO Auto-generated constructor stub
	}
}

class MockTimedQuest extends TimedQuest
{

	public MockTimedQuest(String name, String desc, NPC questGiver) {
		super(name, desc, questGiver);
		// TODO Auto-generated constructor stub
	}
	
}

//class MockGiftQuest extends Quest
//{
//
//	public MockGiftQuest (NPC giver, NPC target, Item item) {
//		super("Gift of the Mocking", giver, target, item, SocialCapitolCost.CHEAP, SocialNetworkDecayRates.NORMAL);
//		// TODO Auto-generated constructor stub
//	}
//	
//}