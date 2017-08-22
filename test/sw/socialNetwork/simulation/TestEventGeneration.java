package sw.socialNetwork.simulation;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import sw.environment.SWRoomUpdateType;
import sw.item.Item;
import sw.lifeform.SocialNPC;
import sw.quest.FavorQuest;
import sw.quest.GiftQuest;
import sw.quest.RequestFavorQuest;
import sw.quest.SocialQuest;
import sw.quest.SocialQuestDifficulty;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FriendRequest;
import sw.socialNetwork.Moods;
import sw.socialNetwork.QuestGenerator;
import sw.socialNetwork.SocialNetworkDecayRates;
import sw.socialNetwork.SocialQuestState;

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
		
		bob.sendFriendRequest(bill);
		
		FriendRequest bobToBill = bill.getFrResponseList().get(bob);
		ArrayList<SocialNetworkEvent> bobsEvents = bob.getEvents();
		ArrayList<SocialNetworkEvent> billsEvents = bill.getEvents();
		
		assertEquals(1, bobsEvents.size());
		assertEquals(1, billsEvents.size());
		assertEquals(EventTypes.FRIEND_REQUEST_SENT, bobsEvents.get(0).getType());
		assertEquals(EventTypes.FRIEND_REQUEST_RECIEVED, billsEvents.get(0).getType());
		
		bill.acceptFriendRequest(bobToBill);
		
		billsEvents = bill.getEvents();
		assertEquals(2, billsEvents.size());
		assertEquals(EventTypes.FRIEND_REQUEST_ACCEPTED, billsEvents.get(1).getType());
		
		bill.sendFriendRequest(bob);
		FriendRequest billToBob = bob.getFrResponseList().get(bill);
		bob.rejectFriendRequest(billToBob);
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
		bob.setTotalDesiredFriends(1);
		bob.getQuestGenerator().roomUpdate(null, gift, SWRoomUpdateType.ITEM_ADDED);
		
		bob.sendFriendRequest(bill);
		bill.acceptFriendRequest(bob.getFrReqList().get(bill));
		
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
		bob.setGrumpiness(1.0);
		bob.setCurrentMood(Moods.ANGRY);
		bill.setCurrentMood(Moods.ANGRY);
		bob.setCurrentCapital(500);
		
		bob.addFriend(bill);
		bill.addFriend(bob);
		bill.addFriend(jane);
		jane.addFriend(bill);
		
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
	
		bob.getQuestGenerator().roomUpdate(null, new MockItem(), SWRoomUpdateType.ITEM_ADDED);
		
		bob.setTotalDesiredFriends(1);
		bob.setTotalDesiredCapital(5000);
		bob.setCurrentCapital(1000);
		bob.addFriend(bill);
		bill.addFriend(bob);
		
		//Generate event when a 
		bob.getRelationships().get(bill).setIntimacy(12); //3 turns until termination
		
		bob.updateTime("", 0);
		
		assertEquals(1, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0) instanceof FavorQuest);
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
		bob.getQuestGenerator().roomUpdate(null, new MockItem(), SWRoomUpdateType.ITEM_ADDED);
		
		bob.addFriend(bill);
		bill.addFriend(bob);
		
		//set up the relationship so Bob owes Bill a social debt & will do a favor when asked
		Feelings bobForBill = bob.getRelationships().get(bill);
		bobForBill.setSocialDebtOwed(1000);
		bobForBill.setIntimacy(50);
		bill.askFavor(bob);
		bob.setCurrentCapital(500);
		
		bob.updateTime("", 0);
		
		assertEquals(1, bob.getAvailableQuests().size());
		assertTrue(bob.getAvailableQuests().get(0) instanceof RequestFavorQuest);
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
		MockItem gift = new MockItem();
		MockGiftQuest quest = new MockGiftQuest(bob, bill, gift);
		
		quest.questSuccessful();
		
		assertTrue(bob.getFriends().contains(bill));
		assertTrue(bill.getFriends().contains(bob));
		assertEquals(2, bob.getEvents().size());
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
		
		bob.addFriend(bill);
		bill.addFriend(bob);
		bob.addFriend(jane);
		jane.addFriend(bob);
		
		//set up the relationships so Bob will terminate his friendship with Jane
		bob.getRelationships().get(jane).setIntimacy(1);
		
		bob.updateTime("", 0);
		
		assertFalse(bob.getFriends().contains(jane));
		assertEquals(2, bob.getEvents().size());
		//first event is change of social capital
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
		
		bill.setCurrentMood(Moods.ANGRY);
		jane.setCurrentMood(Moods.ANGRY);
		
		assertEquals(Moods.HAPPY, bob.getCurrentMood());
		bob.setGrumpiness(1.0);
		bob.setLastQuestResult(SocialQuestState.FAILURE);
		bob.changeMoodQuest();
		assertEquals(Moods.ANGRY, bob.getCurrentMood());
		assertEquals(1, bob.getEvents().size());
		assertEquals(EventTypes.MOOD_CHANGE_TO_ANGRY, bob.getEvents().get(0).getType());
		
		bob.setGrumpiness(0.0);
		bob.setLastQuestResult(SocialQuestState.SUCCESS);
		bob.changeMoodQuest();
		assertEquals(Moods.HAPPY, bob.getCurrentMood());
		assertEquals(2, bob.getEvents().size());
		assertEquals(EventTypes.MOOD_CHANGE_TO_HAPPY, bob.getEvents().get(1).getType());
		
		bob.setGrumpiness(1.0);
		bob.changeMoodPropagation();
	}
	
}

class MockSocialNPC extends SocialNPC
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

class MockSocialQuest extends SocialQuest
{
	public MockSocialQuest(SocialNPC giver, SocialNPC target) {
		super("The Quest of Mock", "A mock SocialQuest", giver, target, SocialQuestDifficulty.EASY);
		// TODO Auto-generated constructor stub
	}
}

class MockGiftQuest extends GiftQuest
{

	public MockGiftQuest(SocialNPC giver, SocialNPC target, Item item) {
		super("Gift of the Mocking", giver, target, item, SocialQuestDifficulty.EASY, SocialNetworkDecayRates.NORMAL);
		// TODO Auto-generated constructor stub
	}
	
}