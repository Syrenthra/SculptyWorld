package sw.lifeform;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import mock.MockFeelings;
import mock.MockItem;
import mock.MockRoom;
import mock.MockTask;

import org.junit.Test;


import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.Zone;
import sw.item.HandLocation;
import sw.item.Item;
import sw.quest.Quest;
import sw.quest.QuestGenerator;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.quest.TimedQuest;
import sw.quest.reward.FavorReward;
import sw.quest.reward.GiftReward;
import sw.quest.reward.HomewreckerReward;
import sw.quest.reward.QuestReward;
import sw.quest.reward.RequestFavorReward;
import sw.quest.task.DeliverItemTask;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FriendRequest;
import sw.socialNetwork.FriendRequestLists;
import sw.socialNetwork.FriendRequestStatus;
import sw.socialNetwork.Moods;
import sw.socialNetwork.Personality;
import sw.socialNetwork.SocialNetwork;

/**
 * This set of tests makes sure that SocialNPC behaves properly.
 * 
 * @author David Abrams
 */
public class TestNPC
{
    private double error = 0.05; //margin of error for tests checking chance of an event

    private String socialClock = "";
    
    private String questClock = "";

    @Test
    public void testTakeHit()
    {
        NPC dude2 = new NPC(1, "Dude", "Desc", 100, 10, 5, 15);

        dude2.takeHit(10);
        assertEquals(95, dude2.getCurrentLifePoints());
    }

    @Test
    public void testAttack()
    {
        NPC dude1 = new NPC(1, "Dude", "Desc", 100, 10, 5, 15);
        NPC dude2 = new NPC(2, "Dude", "Desc", 100, 10, 5, 15);

        dude1.attack(dude2);
        assertEquals(95, dude2.getCurrentLifePoints());
    }

    @Test
    public void testManageQuestItemList()
    {
        NPC npc = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        MockItem item = new MockItem();
        npc.addQuestItem(item);
        assertEquals(item, npc.getQuestItems().get(0));
        npc.removeQuestItem(0);
        assertEquals(0, npc.getQuestItems().size());
    }

    @Test
    public void testAddAcceptablePersonalItems()
    {
        PC dude = new PC(1, "Dude", "Desc", 50);
        NPC npc = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

        MockItem item = new MockItem("Test", "Test", 5, 5);
        npc.addAcceptablePersonalItem(dude, item.clone());
        assertEquals(1, npc.m_acceptablePersonalItems.size());
        // Should not add an item that is already on the list.
        npc.addAcceptablePersonalItem(dude, item.clone());
        assertEquals(1, npc.m_acceptablePersonalItems.size());
        assertTrue(npc.m_acceptablePersonalItems.containsKey(dude.getID()));
    }

    @Test
    public void testNPCAbleToHoldAndGiveItems()
    {
        PC dude = new PC(1, "Dude", "Desc", 50);
        NPC npc = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

        MockItem item = new MockItem("Test", "Test", 5, 5);
        npc.addAcceptablePersonalItem(dude, item.clone());

        boolean success = npc.addPersonalItem(dude, item);
        assertTrue(success);
        assertEquals(1, npc.m_personalItems.size());
        assertEquals(item, npc.m_personalItems.get(0));

        Item givenItem = npc.givePersonalItem(0);
        assertEquals(item, givenItem);
        assertEquals(0, npc.m_personalItems.size());

        Item nullItem = npc.givePersonalItem(0);
        assertNull(nullItem);

        // Can remove acceptable item.
        npc.removeAcceptablePersonalItem(dude, item.clone());
        assertFalse(npc.addPersonalItem(dude, item));
        assertEquals(0, npc.m_acceptablePersonalItems.get(dude.getID()).size());
    }

    @Test
    public void testNPCWontAcceptItemsNotOnList()
    {
        PC dude = new PC(1, "Dude", "Desc", 50);
        NPC npc = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

        MockItem item = new MockItem();
        boolean success = npc.addPersonalItem(dude, item);
        assertFalse(success);
        assertEquals(0, npc.m_personalItems.size());
    }

    /**
     * This test makes sure that a SocialNPC can be correctly initialized with the right default
     * values.
     */
    @Test
    public void testInitialization()
    {
        NPC npc = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

        assertEquals("Bob", npc.getName());
        assertEquals("He wears overalls.", npc.getDescription());
        assertEquals(5, npc.getDamage());
        assertEquals(10, npc.getArmor());
        assertEquals(1, npc.getSpeed());

        assertTrue(npc instanceof NPC);
        SocialNetwork network = npc.m_socialNetwork;
        assertEquals(0, network.getFriends().size());
        assertEquals(0, network.getRelationships().size());
        assertEquals(0, npc.getFavoriteItems().size());
        assertEquals(0.5, network.getPersonability(), 0.001);
        assertEquals(0, network.getTotalDesiredFriends());
        assertEquals(0, network.getTotalDesiredCapital());
        assertEquals(0, network.getCurrentCapital());
        assertEquals(0, network.getControl(), 0.01);
        assertEquals(Moods.HAPPY, npc.getCurrentMood());
        assertEquals(0.5, network.getGrumpiness(), 0.01);
        assertEquals(0, npc.getQuestItems().size());

        Personality pers = new Personality(0.1, 0.2, 0.3, 10, 1000);
        NPC npc2 = new NPC(1, "Bill", "He wears jeans.", 50, 1, 1, 1, pers);

        assertEquals(1, npc2.getID());
        network = npc2.m_socialNetwork;
        assertEquals("Bill", npc2.getName());
        assertEquals(0.1, network.getControl(), 0.001);
        assertEquals(0.2, network.getGrumpiness(), 0.001);
        assertEquals(0.3, network.getPersonability(), 0.001);
        assertEquals(10, network.getTotalDesiredFriends());
        assertEquals(1000, network.getTotalDesiredCapital());
    }

    /**
     * ToString() should return the name of the SocialNPC.
     */
    @Test
    public void testToString()
    {
        NPC npc = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

        assertEquals("Bob", npc.toString());
    }

    /**
     * This test makes sure that all the getters and setters work correctly.
     */
    @Test
    public void testGettersSetters()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);

        Quest quest = new Quest("Test", "Test Desc", bob);
        bob.addQuest(quest);
        assertTrue(bob.getAvailableQuests().contains(quest));
        bob.addQuest(quest);
        assertEquals(1, bob.getAvailableQuests().size());

        bob.removeQuest(quest);
        assertFalse(bob.getAvailableQuests().contains(quest));

        MockItem item = new MockItem();
        bob.addFavoriteItem(item);
        assertTrue(bob.getFavoriteItems().contains(item));
        bob.addFavoriteItem(item);
        assertEquals(1, bob.getFavoriteItems().size());

        bob.removeFavoriteItem(item);
        assertFalse(bob.getFavoriteItems().contains(item));
        
        bob.setCategory(2);
        assertEquals(2,bob.getCategory());
    }

    @Test
    public void testPlayerCanRequestQuestInAvailableList()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        Quest quest = new Quest("Test", "Test Desc", bob);
        bob.addQuest(quest);

        PC dude = new PC(1, "Dude", "Desc", 50);

        bob.assignQuest(dude, quest);

        assertEquals(QuestState.IN_PROGRESS, quest.getCurrentState(dude));
    }

    @Test
    public void testRelatedItemsAndPlayersAddedToListOfWhoCanGiveWhatToTheNPC()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        Quest quest = new Quest("Test", "Test Desc", bob);
        Item item = new MockItem("Test", "Test Item", 5, 10);
        DeliverItemTask task = new DeliverItemTask(quest, item, 5);
        quest.addTask(task);

        bob.addQuest(quest);

        PC dude = new PC(1, "Dude", "Desc", 50);

        bob.assignQuest(dude, quest);

        ArrayList<Item> items = bob.m_acceptablePersonalItems.get(dude.getID());

        assertTrue(item.equals(items.get(0)));

        quest.setCurrentState(dude, QuestState.INACTIVE);

        assertEquals(0, items.size());

    }

    /**
     * The purpose of this test is to make sure that a SocialNPC changes moods properly during its turn.
     */
    @Test
    public void testUpdateTime_MoodChange()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC bill = new NPC(0, "Bill", "He wears gloves.", 50, 5, 10, 1);
        MockFeelings billsFeels = new MockFeelings();
        MockFeelings bobsFeels = new MockFeelings();
        MockItem item = new MockItem();

        bob.getSocialNetwork().addFriend(bill, bobsFeels);
        bill.getSocialNetwork().addFriend(bob, billsFeels);
        bob.addQuestItem(item);

        /**
         * Set up Bob to check for proper mood change. Mood change from quests takes priority over
         * mood change from mood propagation.
         */
        bob.getSocialNetwork().setGrumpiness(1.0);
        bob.setLastQuestResult(QuestState.FAILED);

        bob.updateTime(socialClock, 1);

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
                bob.getSocialNetwork().setCurrentMood(Moods.ANGRY);
            }
            bobsFeels.setIntimacy(20); //don't let Bob's intimacy with Bill decay
        }

        //should change mood 15% of the time (+/- error)
        assertTrue("counter: " + counter, counter >= numRuns * 0.15 - (numRuns * error) && counter <= numRuns * 0.15 + (numRuns * error));
    }

    /**
     * This test makes sure that a SocialNPC knows the right time to try and make friends, and
     * can do it properly.
     */
    @Test
    public void testUpdateTime_GiftQuest()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobSocialNet = bob.getSocialNetwork();
        MockItem item = new MockItem();
        bob.addQuestItem(item);

        NPC bill = new NPC(1, "Bill", "He wears gloves.", 50, 5, 10, 1);
        SocialNetwork billSocialNetwork = bill.getSocialNetwork();

        NPC john = new NPC(2, "John", "He wears kilts.", 50, 5, 10, 1);
        SocialNetwork johnSocialNetwork = john.getSocialNetwork();

        MockRoom room = new MockRoom(0, "Room", "Desc");
        room.addNPC(bob);
        room.addNPC(bill);
        room.addNPC(john);

        // Setup the initial Social Network
        bobSocialNet.setTotalDesiredFriends(2);
        billSocialNetwork.setTotalDesiredFriends(1);
        johnSocialNetwork.setTotalDesiredFriends(1);
        bobSocialNet.setCurrentCapital(5000);

        //the SNPCs will make friends every time if they want more friends
        bobSocialNet.setPersonability(1.0);
        billSocialNetwork.setPersonability(1.0);
        johnSocialNetwork.setPersonability(1.0);

        bob.updateTime(socialClock, 0);

        //Bob wants friends, so he should send FriendRequests to Bill and John
        assertTrue(bobSocialNet.getFrReqList().containsKey(bill));
        assertTrue(bobSocialNet.getFrReqList().containsKey(john));
        assertTrue(billSocialNetwork.getFrResponseList().containsKey(bob));
        assertTrue(johnSocialNetwork.getFrResponseList().containsKey(bob));
        assertTrue(billSocialNetwork.getFrResponseList().get(bob).getState().equals(FriendRequestStatus.WAITING));
        assertTrue(johnSocialNetwork.getFrResponseList().get(bob).getState().equals(FriendRequestStatus.WAITING));

        //Bill and John both want 1 friend, so they should both accept Bob's requests
        bill.updateTime(socialClock, 0);
        john.updateTime(socialClock, 0);

        assertTrue(billSocialNetwork.getFrResponseList().get(bob).getState().equals(FriendRequestStatus.ACCEPTED));
        assertTrue(johnSocialNetwork.getFrResponseList().get(bob).getState().equals(FriendRequestStatus.ACCEPTED));

        assertTrue(bobSocialNet.getFrReqList().get(bill).getState().equals(FriendRequestStatus.ACCEPTED));
        assertTrue(bobSocialNet.getFrReqList().get(john).getState().equals(FriendRequestStatus.ACCEPTED));

        //Bob sees that his requests were accepted, and creates quests
        bob.updateTime(socialClock, 0);

        assertEquals(2, bob.getAvailableQuests().size());
        assertTrue(questsContainsCorrectGiftReward(bob.getAvailableQuests(), bill));
        assertTrue(questsContainsCorrectGiftReward(bob.getAvailableQuests(), john));
        assertFalse(bobSocialNet.getFrReqList().containsKey(billSocialNetwork));
        assertFalse(bobSocialNet.getFrReqList().containsKey(johnSocialNetwork));
    }

    private boolean questsContainsCorrectGiftReward(ArrayList<Quest> quests, NPC target)
    {
        boolean doesContain = false;
        for (Quest quest : quests)
        {
            for (QuestReward reward : quest.getRewards())
            {
                if (reward instanceof GiftReward)
                {
                    if (((GiftReward) reward).getTarget() == target)
                        doesContain = true;
                }
            }
        }

        return doesContain;
    }

    /**
    * This test makes sure that a SNPC is using the right priorities when deciding how to spend its 
    * social capital. This test also makes sure that the other actions that updateTime() should
    * perform are happening a the right time and in the right situation. 
    */
    @Test
    public void testUpdateTime_QuestCreationPriorities()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(1, "Bill", "He wears gloves.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC john = new NPC(2, "John", "He wears kilts.", 50, 5, 10, 1);
        SocialNetwork johnNetwork = john.getSocialNetwork();
        NPC jane = new NPC(3, "Jane", "She wears large boots.", 50, 5, 10, 1);
        SocialNetwork janeNetwork = jane.getSocialNetwork();
        NPC jimmy = new NPC(4, "Jimmy", "He wears dumb shirts", 50, 5, 10, 1);
        SocialNetwork jimmyNetwork = jimmy.getSocialNetwork();
        NPC mike = new NPC(5, "Mike", "He wears hiking boots", 50, 5, 10, 1);
        SocialNetwork mikeNetwork = mike.getSocialNetwork();

        MockItem item = new MockItem();
        MockRoom room = new MockRoom(0, "Room", "Desc");
        MockFeelings bobForBill = new MockFeelings();
        MockFeelings bobForJohn = new MockFeelings();
        MockFeelings bobForJane = new MockFeelings();
        MockFeelings bobForMike = new MockFeelings();

        bob.addQuestItem(item);
        bill.addQuestItem(item);
        john.addQuestItem(item);
        jane.addQuestItem(item);
        jimmy.addQuestItem(item);
        mike.addQuestItem(item);

        room.addNPC(bob);
        room.addNPC(bill);
        room.addNPC(john);
        room.addNPC(jane);
        room.addNPC(jimmy);
        room.addNPC(mike);

        bobNetwork.addFriend(bill, bobForBill);
        bobNetwork.addFriend(john, bobForJohn);
        bobNetwork.addFriend(jane, bobForJane);
        bobNetwork.addFriend(mike, bobForMike);
        billNetwork.addFriend(bob);
        johnNetwork.addFriend(bob);
        janeNetwork.addFriend(bob);

        bobNetwork.setCurrentCapital(500);
        bobNetwork.setTotalDesiredCapital(5000);
        bobNetwork.setTotalDesiredFriends(5);
        bobForBill.setSocialDebtOwed(750);
        johnNetwork.getRelationships().get(bob).setSocialDebtOwed(500);

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

        bob.updateTime(socialClock, 0);

        //FriendRequest to Jimmy
        assertEquals(1, bobNetwork.getFrReqList().size());
        assertTrue(bobNetwork.getFrReqListOrder().contains(jimmy));
        assertTrue(jimmyNetwork.getFrResponseListOrder().contains(bob));

        //ask John for a favor; John agrees because he owes Bob a social debt.
        assertEquals(1, john.getFavorRequests().size());
        assertTrue(john.getFavorRequests().contains(bob));
        //ask Mike a favor; Mike does not agree because he does not owe Bob any social debt
        assertEquals(0, mike.getFavorRequests().size());

        //Bob only has enough social capital to make one quest...
        assertEquals(1, bob.getAvailableQuests().size());
        //...so he should make a FavorQuest for Mike because his relationship with Mike is the closest to being
        //terminated due to low intimacy
        Quest bobsQuest = bob.getAvailableQuests().get(0);
        assertTrue(bobsQuest instanceof TimedQuest);
        FavorReward reward = (FavorReward) bobsQuest.getRewards().elementAt(0);
        assertEquals(mike, reward.getTarget());
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
         *              (t=-1, i=12) /    
         *                     \    /
         * bill <--(t=3, i=48)-- bob --(t=-2, i=26)--> john
         *            <favor owed |
         *                  (t=3, i= 78)
         *                        |
         *                        |
         *                       jane
         */

        //Bob can now gain social capital.
        bobNetwork.setControl(1.0);
        //this quest should expire during cleanup beginning next turn
        TimedQuest bobFavorQuestForMike = (TimedQuest) bob.getAvailableQuests().get(0);
        bobFavorQuestForMike.setTimeToHoldRemaining(1);
        //Jimmy accepted Bob's FriendRequest
        jimmyNetwork.getFrResponseList().get(bob).accept();

        bob.updateTime(socialClock, 0);
        bobFavorQuestForMike.updateTime(questClock, 0);  // Get the favor quest to clear out.

        //Bob doesn't have enough social capital to make any quests
        assertEquals(282, bobNetwork.getCurrentCapital());
        //Bob should not send any new FriendRequests.
        assertEquals(0, bobNetwork.getFrReqList().size());
        //The FR sent to Jimmy should be removed, since Jimmy accepted it
        assertEquals(0, bobNetwork.getFrReqListOrder().size());
        //Bob should make a GiftQuest targeting Jimmy
        assertEquals(1, bob.getAvailableQuests().size());
        bobsQuest = (TimedQuest) bob.getAvailableQuests().get(0);
        GiftReward giftReward = (GiftReward) bobsQuest.getRewards().elementAt(0);
        assertEquals(jimmy, giftReward.getTarget());
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

        bobNetwork.setCurrentCapital(0);
        bob.updateTime(socialClock, 0);

        //The FavorQuest Bob made for Mike should have expired and been removed.
        assertEquals(0, bobFavorQuestForMike.getTimeToHoldRemaining());
        assertFalse(bob.getAvailableQuests().contains(bobFavorQuestForMike));
        //Since Jimmy accepted Bob's FriendRequest, Bob should have made a GiftQuest targeting Jimmy
        //Bob should also make a FavorQuest targeting Mike
        assertEquals(2, bob.getAvailableQuests().size());
        TimedQuest quest0 = (TimedQuest) bob.getAvailableQuests().get(0);
        assertTrue(quest0.getRewards().elementAt(0) instanceof GiftReward);
        GiftReward reward0 = (GiftReward) quest0.getRewards().elementAt(0);
        assertEquals(jimmy, reward0.getTarget());
        TimedQuest quest1 = (TimedQuest) bob.getAvailableQuests().get(1);
        assertTrue(quest1.getRewards().elementAt(0) instanceof FavorReward);
        FavorReward reward1 = (FavorReward) quest1.getRewards().elementAt(0);
        assertEquals(mike, reward1.getTarget());
        //The FriendRequest should be removed from both SNPCs when the quest is created
        assertFalse(bobNetwork.getFrReqListOrder().contains(jimmy));
        assertFalse(jimmyNetwork.getFrResponseListOrder().contains(bob));
        //intimacy of relationships should change according to their trust
        assertEquals(44, bobForBill.getIntimacy());
        assertEquals(18, bobForJohn.getIntimacy());
        assertEquals(74, bobForJane.getIntimacy());
        assertEquals(6, bobForMike.getIntimacy());
        //After everything is said and done, Bob should have 256 social capital
        assertEquals(246, bobNetwork.getCurrentCapital());

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
        PC dude = new PC(1, "Dude", "Desc", 50);
        dude.assignNativeQuest(bob.getAvailableQuests().get(0));
        dude.holdInHand(item.clone(), HandLocation.RIGHT);
        dude.giveItemInHand(jimmy, HandLocation.RIGHT);
        bob.getAvailableQuests().get(0).turnInQuest(dude);

        //At some point, Bob made a quest targeting Mike. This quest should be removed when Bob
        //terminates his relationship with Mike.
        TimedQuest quest = QuestGenerator.genFavorQuest(bob, mike);
        bob.addQuest(quest);

        //Bob relationship with Mike has decayed to the point where Bob should no longer want
        //to be friends with Mike
        bobForMike.setIntimacy(1);
        bobNetwork.setControl(0.0);//Bob will not gain any social capital this turn
        bobNetwork.setCurrentCapital(500);

        bob.updateTime(socialClock, 0);
        quest.updateTime(questClock, 0);

        //Since the quest was completed, Bob and Jimmy should now be friends
        assertTrue(bobNetwork.getFriends().contains(jimmy));
        assertTrue(jimmyNetwork.getFriends().contains(bob));
        //The exchange of a gift should cause Jimmy to be socially indebted to Bob
        assertTrue(jimmyNetwork.getRelationships().get(bob).getSocialDebtOwed() > 0);
        assertTrue(bobNetwork.getRelationships().get(jimmy).getSocialDebtOwed() < 0);
        //The intimacy Bob's relationship with Mike should have decayed to the point where Bob 
        //terminates the relationship
        assertFalse(bobNetwork.getFriends().contains(mike));
        //The quest targetting Mike should have been removed too
        assertFalse(bob.getAvailableQuests().contains(quest));
        //Bob doesn't have as many friends as he wants, so he should send FriendRequests to all
        //other valid SNPCs (Mike is the only valid target)
        assertTrue(bobNetwork.getFrReqListOrder().contains(mike));
        assertTrue(mikeNetwork.getFrResponseListOrder().contains(bob));
        //Bob has enough social capital to create 1 quest. He can't make new friends, so he should pick
        //the relationship that's soonest to being terminated and make a FavorQuest
        assertEquals(1, bob.getAvailableQuests().size());
        TimedQuest questATurn4 = (TimedQuest) bob.getAvailableQuests().get(0);
        FavorReward rewardATurn4 = (FavorReward) questATurn4.getRewards().elementAt(0);
        //Bob should pick Bill, because that relationship is closest to being terminated
        assertEquals(john, rewardATurn4.getTarget());
        //relationships should decay properly
        assertEquals(42, bobForBill.getIntimacy());
        assertEquals(14, bobForJohn.getIntimacy());
        assertEquals(72, bobForJane.getIntimacy());
        //Relationships that are the result of GiftQuests start at intimacy = 35
        assertEquals(48, bobNetwork.getRelationships().get(jimmy).getIntimacy());
        //After everything's said and done, Bob should have 126 social capital left over
        assertEquals(0, bobNetwork.getCurrentCapital());

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

        Feelings bobForJimmy = bobNetwork.getRelationships().get(jimmy);
        bobForBill.setTrust(3);
        bobForBill.setIntimacy(50);
        bobForJane.setTrust(3);
        bobForJane.setIntimacy(80);
        bobForJohn.setTrust(1);
        bobForJohn.setIntimacy(60);
        bobForJimmy.setTrust(5);
        bobForJimmy.setIntimacy(30);

        bob.getAvailableQuests().clear(); //Don't need the previously created quests anymore
        bobNetwork.setControl(0.0); //Bob's social capital will not update this turn
        bobNetwork.setCurrentCapital(500); //Bob has enough social capital for 1 quest

        bob.updateTime(socialClock, 0);

        //Bob still owes Bill a favor, so that agreement should take priority
        assertEquals(1, bob.getAvailableQuests().size());
        TimedQuest questTurn5 = (TimedQuest) bob.getAvailableQuests().get(0);
        RequestFavorReward rewardTurn5 = (RequestFavorReward) questTurn5.getRewards().elementAt(0);
        assertEquals(bill, rewardTurn5.getTarget());
        assertFalse(bob.getFavorRequests().contains(bill));
        assertEquals(0, bob.getFavorRequests().size());
        //Bob sent Mike a FriendRequest
        assertTrue(bobNetwork.getFrReqListOrder().contains(mike));
        assertTrue(mikeNetwork.getFrResponseListOrder().contains(bob));
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

        mikeNetwork.getFrResponseList().get(bob).accept(); //Mike accepted Bob's request
        bobNetwork.setCurrentCapital(500);//Bob has enough social capital to create 1 quest

        bob.updateTime(socialClock, 0);

        //Mike accepted Bob's friend request, so creating that quest is Bob's first priority
        assertEquals(2, bob.getAvailableQuests().size());
        TimedQuest questTurn6 = (TimedQuest) bob.getAvailableQuests().get(1);
        GiftReward rewardTurn6 = (GiftReward) questTurn6.getRewards().elementAt(0);
        assertEquals(mike, rewardTurn6.getTarget());
        //Bob and Mike should get rid of the FriendRequest when it's accepted
        assertFalse(bobNetwork.getFrReqListOrder().contains(mike));
        assertFalse(bobNetwork.getFrReqList().containsKey(mike));
        assertFalse(mikeNetwork.getFrResponseListOrder().contains(bob));
        assertFalse(mikeNetwork.getFrResponseList().containsKey(bob));
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

        NPC rocky = new NPC(0, "Rocky", "He wears no shirt.", 50, 5, 10, 1);
        room.addNPC(rocky);
        rocky.getSocialNetwork().sendFriendRequest(bob);
        FriendRequest rockyToBob = rocky.getSocialNetwork().getFrReqList().get(bob);
        rewardTurn6.getItemReward(); // This should be the same effect as the line below.
        //bob.getAvailableQuests().get(1).questSuccessful();//Bob and Mike are friends again
        bobNetwork.setCurrentCapital(500);
        bobNetwork.getRelationships().get(mike).setTrust(5);

        bob.updateTime(socialClock, 0);

        assertEquals(FriendRequestStatus.REJECTED, rockyToBob.getState());

    }

    /**
     * This test makes sure that a SocialNPC generates the appropriate quest types based on its mood.
     */
    @Test
    public void testUpdateTime_QuestsBasedOnMood()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(0, "Bill", "He wears gloves.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC john = new NPC(0, "John", "He wears kilts.", 50, 5, 10, 1);
        SocialNetwork johnNetwork = john.getSocialNetwork();
        MockItem item = new MockItem();
        MockFeelings bobsFeelsForBill = new MockFeelings();
        MockFeelings billsFeelsForBob = new MockFeelings();
        MockFeelings billsFeelsForJohn = new MockFeelings();
        MockFeelings johnsFeelsForBill = new MockFeelings();

        bobNetwork.addFriend(bill, bobsFeelsForBill);
        billNetwork.addFriend(bob, billsFeelsForBob);
        billNetwork.addFriend(john, billsFeelsForJohn);
        johnNetwork.addFriend(bill, johnsFeelsForBill);
        bobNetwork.setGrumpiness(0.5);
        bobNetwork.setTotalDesiredCapital(5000);
        bobsFeelsForBill.setTrust(3);
        bob.addQuestItem(item);

        /**
         * The network looks like this.
         * 
         * bob --- bill --- john
         */

        //Bob is angry, but doesn't have enough social capital to make a quest, so he should not
        //be able to
        bobNetwork.setCurrentMood(Moods.ANGRY);
        bob.updateTime(socialClock, 0);
        assertEquals(0, bob.getAvailableQuests().size());

        /**
         * When Bob is angry, half of his quests should be HomewreckerQuests (based on Bob's
         * grumpiness).
         */
        bobNetwork.setCurrentCapital(500);
        Quest quest = null;

        int[] counter = { 0, 0 };
        int numRuns = 1000;
        for (int j = 0; j < 2; j++)
        {
            for (int i = 0; i < numRuns; i++)
            {
                bob.updateTime(socialClock, 1);

                if (bob.getAvailableQuests().size() > 0)
                {
                    quest = bob.getAvailableQuests().get(0);

                    if (quest.getRewards().elementAt(0) instanceof HomewreckerReward)
                    {
                        counter[0]++;
                    }
                    else
                    {
                        counter[1]++;
                    }
                    bob.removeQuest(quest);
                    bobNetwork.setCurrentCapital(500);
                    bobsFeelsForBill.setIntimacy(20);//don't let Bob's intimacy with Bill decay
                    quest = null;
                }
            }

            bobNetwork.setCurrentMood(Moods.ANGRY);
            //Bill needs to be angry so Bob won't become happy through mood propagation
            billNetwork.setCurrentMood(Moods.ANGRY);
        }

        /**
         * In the first iteration of the loop, Bob is happy so none of the quests should be
         * HomewreckerQuests. In the second iteration of the loop, Bob is angry, so half of
         * the quests created should be HomewreckerQuests.
         */
        assertTrue("# of HomewreckerQuests: " + counter[0], counter[0] >= (numRuns / 2) - (numRuns * error) && counter[0] <= (numRuns / 2) + (numRuns * error));
        assertTrue("# of other quests: " + counter[1], counter[1] >= (numRuns * 1.5) - (numRuns * error) && counter[1] <= (numRuns * 1.5) + (numRuns * error));
    }

    /**
     * The purpose of this test is to make sure that a SocialNPC can correctly assign a quest to a
     * Player and also use QuestGenerator to create a quest.
     */
    @Test
    public void testCreateAndAssignQuest()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        PC player = new PC(1, "1337pwn3r", "The player", 100);

        Quest quest1 = new Quest("Quest1", "Description", bob);
        MockTask task1 = new MockTask();
        quest1.addTask(task1);

        Quest quest2 = new Quest("Quest2", "Description", bob);
        MockTask task2 = new MockTask();
        quest2.addTask(task2);

        //shouldn't be able to give quests that aren't in the SocialNPC's availableQuests
        bob.assignQuest(player, quest1);
        assertEquals(0, bob.getAvailableQuests().size());
        assertEquals(0, player.getNativeQuests().size());
        bob.assignQuest(player, 0);
        assertEquals(0, bob.getAvailableQuests().size());
        assertEquals(0, player.getNativeQuests().size());

        bob.addQuest(quest1);
        bob.addQuest(quest2);

        bob.assignQuest(player, quest1);
        assertTrue(player.getNativeQuests().contains(quest1));
        assertTrue(quest1.hasPlayer(player));
        bob.assignQuest(player, 1);
        assertTrue(player.getNativeQuests().contains(quest2));
        assertTrue(quest2.hasPlayer(player));

        //a quest should not be assigned to a player if its timer has expired
        TimedQuest quest3 = new TimedQuest("Quest3", "Description", bob);
        quest3.setTimeToHoldRemaining(0);
        bob.addQuest(quest3);

        bob.assignQuest(player, quest3);

        assertFalse(player.getNativeQuests().contains(quest3));
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
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC sandy = new NPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        SocialNetwork sandyNetwork = sandy.getSocialNetwork();
        NPC jane = new NPC(3, "Jane", "She wears thick gloves.", 50, 5, 10, 1);
        SocialNetwork janeNetwork = jane.getSocialNetwork();

        billNetwork.sendFriendRequest(bob);
        sandyNetwork.sendFriendRequest(bob);
        janeNetwork.sendFriendRequest(bob);
        bobNetwork.setPersonability(1.0);
        billNetwork.setPersonability(1.0);
        sandyNetwork.setPersonability(1.0);
        janeNetwork.setPersonability(1.0);

        Hashtable<NPC, FriendRequest> bobsRespList = bobNetwork.getFrResponseList();
        FriendRequest billToBob = bobsRespList.get(bill);
        FriendRequest sandyToBob = bobsRespList.get(sandy);
        FriendRequest janeToBob = bobsRespList.get(jane);

        bobNetwork.setTotalDesiredFriends(0);
        bobNetwork.evalFriendRequests(0);

        //Bob doesn't want any friends, so he should not accept any of the requests.
        assertEquals(FriendRequestStatus.REJECTED, billToBob.getState());
        assertEquals(FriendRequestStatus.REJECTED, sandyToBob.getState());
        assertEquals(FriendRequestStatus.REJECTED, janeToBob.getState());

        //FriendRequests can't be changed once they're accepted or rejected, so need to remake 
        //them each time...
        bobsRespList.clear();
        bobNetwork.getFrResponseListOrder().clear();
        billNetwork.sendFriendRequest(bob);
        sandyNetwork.sendFriendRequest(bob);
        janeNetwork.sendFriendRequest(bob);
        billToBob = bobsRespList.get(bill);
        sandyToBob = bobsRespList.get(sandy);
        janeToBob = bobsRespList.get(jane);

        //Bob wants one friend now, so he should accept Bill's request and reject the other two.
        bobNetwork.setTotalDesiredFriends(1);
        bobNetwork.evalFriendRequests(0);

        assertEquals(FriendRequestStatus.ACCEPTED, billToBob.getState());
        assertEquals(FriendRequestStatus.REJECTED, sandyToBob.getState());
        assertEquals(FriendRequestStatus.REJECTED, janeToBob.getState());

        bobsRespList.clear();
        bobNetwork.getFrResponseListOrder().clear();
        billNetwork.sendFriendRequest(bob);
        sandyNetwork.sendFriendRequest(bob);
        janeNetwork.sendFriendRequest(bob);
        billToBob = bobsRespList.get(bill);
        sandyToBob = bobsRespList.get(sandy);
        janeToBob = bobsRespList.get(jane);

        //Bob wants 5 friends now, so he should accept all the requests.
        bobNetwork.setTotalDesiredFriends(5);
        bobNetwork.evalFriendRequests(0);

        assertEquals(FriendRequestStatus.ACCEPTED, billToBob.getState());
        assertEquals(FriendRequestStatus.ACCEPTED, sandyToBob.getState());
        assertEquals(FriendRequestStatus.ACCEPTED, janeToBob.getState());

        bobsRespList.clear();
        bobNetwork.getFrResponseListOrder().clear();
        billNetwork.sendFriendRequest(bob);
        sandyNetwork.sendFriendRequest(bob);
        janeNetwork.sendFriendRequest(bob);
        billToBob = bobsRespList.get(bill);
        sandyToBob = bobsRespList.get(sandy);
        janeToBob = bobsRespList.get(jane);

        //Bob wants 3 friends, but he already accepted a FriendRequest from someone else, 
        //so he should only accept the first two requests
        bobNetwork.setTotalDesiredFriends(3);
        bobNetwork.evalFriendRequests(1);

        assertEquals(FriendRequestStatus.ACCEPTED, billToBob.getState());
        assertEquals(FriendRequestStatus.ACCEPTED, sandyToBob.getState());
        assertEquals(FriendRequestStatus.REJECTED, janeToBob.getState());

        //FriendRequest acceptance rate should be based on personability of the SNPC
        double bobsPersonability = 0.5;
        bobNetwork.setTotalDesiredFriends(1);
        bobNetwork.setPersonability(bobsPersonability); //Bob should accept FriendRequests half the time

        int counter = 0;
        int numAccepted;
        int numRuns = 1000;
        for (int i = 0; i < numRuns; i++)
        {
            bobsRespList.clear();
            bobNetwork.getFrResponseListOrder().clear();
            billNetwork.sendFriendRequest(bob);
            numAccepted = 0;

            bobNetwork.evalFriendRequests(0);

            for (int j = 0; j < bobNetwork.getFrResponseListOrder().size(); j++)
            {
                if (bobNetwork.getFrResponseList().get(bobNetwork.getFrResponseListOrder().get(j)).getState() == FriendRequestStatus.ACCEPTED)
                {
                    numAccepted++;
                }
            }

            if (numAccepted == 1)
            {
                counter++;
            }
            else if (numAccepted == 0)
            {
                //no requests accepted
            }
            else
            {
                fail("Bob accepted " + counter + " requests. He should only accept 1.");
            }
        }

        //Bob should accept requests half the time (+/- error)
        assertTrue("Counter: " + counter, counter >= (numRuns * bobsPersonability) - (error * numRuns) && counter <= (numRuns * bobsPersonability) + (error * numRuns));
    }

    /**
     * This test makes sure that sendFriendRequests() works properly. It should send new
     * FriendRequests to other valid SocialNPCs (using pickNewFriendshipTarget()) until 
     * enough have been sent that if they were all accepted, the SocialNPC would meet it's 
     * required totalDesiredFriends.
     * 
     * Modified to make sure now works across rooms in the same zone.
     */
    @Test
    public void testSendFriendRequests()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
        NPC sandy = new NPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        NPC jane = new NPC(3, "Jane", "She wears thick gloves.", 50, 5, 10, 1);
        // Some rooms.
        Room room3 = new Room(3, "Forest 1","This is a small forest.");
        room3.setZone(Zone.FOREST);
        Room room4 = new Room(4, "Forest 2","This is a big forest.");
        room4.setZone(Zone.FOREST);
        Room room5 = new Room(5, "Forest 3","This is a medium forest.");
        room5.setZone(Zone.FOREST);
        Room room6 = new Room(6, "Forest 4","This is a huge forest.");
        room6.setZone(Zone.FOREST);
        // Add some exits, should not need to add these to TheWorld.
        room3.addExit(room4,Exit.EAST);
        
        room4.addExit(room3, Exit.WEST);
        room4.addExit(room5,Exit.EAST);
        
        room5.addExit(room4, Exit.WEST);
        room5.addExit(room6,Exit.EAST);
        
        room6.addExit(room5, Exit.WEST);

        Hashtable<NPC, FriendRequest> sentRequests = bobNetwork.getFrReqList();

        room3.addNPC(bob);
        room4.addNPC(bill);
        room5.addNPC(sandy);
        room6.addNPC(jane);

        //Bob doesn't want any friends, so he should not send any reqeusts.
        bobNetwork.setTotalDesiredFriends(0);
        bobNetwork.sendFriendRequests(0);

        assertEquals(0, sentRequests.size());

        //Bob wants 1 friend, so he should send 1 request.
        bobNetwork.setTotalDesiredFriends(1);
        bobNetwork.sendFriendRequests(0);

        assertEquals(1, sentRequests.size());

        //Bob wants 5 friends. Between sent and received requests, he's got 3 new friends lined up,
        //so he should only send 2 requests.
        sentRequests.clear();
        bobNetwork.setTotalDesiredFriends(5);
        bobNetwork.sendFriendRequests(3);

        assertEquals(2, sentRequests.size());

        //Bob wants 5 friends. He's already friends with Bill and Sandy, so he should send a request
        //to Jane
        sentRequests.clear();
        bobNetwork.addFriend(bill, new MockFeelings());
        bobNetwork.addFriend(sandy, new MockFeelings());
        bobNetwork.sendFriendRequests(0);

        assertEquals(1, sentRequests.size());
        assertTrue(sentRequests.containsKey(jane));

        //Bob wants 5 friends. He's already friends with Bill, Sandy, and Jane, so he should send
        //no requests
        sentRequests.clear();
        bobNetwork.addFriend(jane, new MockFeelings());
        bobNetwork.sendFriendRequests(0);

        assertEquals(0, sentRequests.size());
    }

    /**
     * This test makes sure that the frReqList and frResponseList are emptied
     */
    @Test
    public void testCleanFrReqLists()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        SocialNetwork jillNetwork = jill.getSocialNetwork();
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        SocialNetwork sandyNetwork = sandy.getSocialNetwork();
        NPC fred = new NPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
        SocialNetwork fredNetwork = fred.getSocialNetwork();
        NPC mike = new NPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
        SocialNetwork mikeNetwork = mike.getSocialNetwork();

        //cleaning the lists when they have no entries should not cause any problems
        bobNetwork.cleanFrReqList(FriendRequestLists.REQUEST_LIST);
        bobNetwork.cleanFrReqList(FriendRequestLists.RESPONSE_LIST);

        //Bob will have 2 entries in his frResponseList
        jillNetwork.sendFriendRequest(bob);
        sandyNetwork.sendFriendRequest(bob);
        FriendRequest jillToBob = jillNetwork.getFrReqList().get(bob);
        FriendRequest sandyToBob = sandyNetwork.getFrReqList().get(bob);

        //...and 2 entries in his frReqList
        bobNetwork.sendFriendRequest(fred);
        bobNetwork.sendFriendRequest(mike);
        FriendRequest bobToFred = fredNetwork.getFrResponseList().get(bob);
        FriendRequest bobToMike = mikeNetwork.getFrResponseList().get(bob);

        Hashtable<NPC, FriendRequest> responseList = bobNetwork.getFrResponseList();
        ArrayList<NPC> responseListOrder = bobNetwork.getFrResponseListOrder();
        Hashtable<NPC, FriendRequest> requestList = bobNetwork.getFrReqList();
        ArrayList<NPC> requestListOrder = bobNetwork.getFrReqListOrder();

        //Bob rejected Jill's request and ignore Sandy's
        jillToBob.reject();

        //Mike accepted Bob's request; Fred rejected it
        bobToMike.accept();
        bobToFred.reject();

        //each list should only have the rejected entries removed, and clean() should
        //only touch one list at a time

        bobNetwork.cleanFrReqList(FriendRequestLists.REQUEST_LIST);
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

        bobNetwork.cleanFrReqList(FriendRequestLists.RESPONSE_LIST);
        //received requests 
        assertEquals(1, responseList.size());
        assertEquals(1, responseListOrder.size());
        assertTrue(responseList.contains(sandyToBob));
        assertTrue(responseListOrder.contains(sandy));
    }

    /**
     * This test makes sure that a SocialNPC can properly remove a FriendRequest using
     * removeFriendRequest(). A request should only be removed if it is ACCEPTED.
     */
    @Test
    public void testRemoveFriendRequest()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        SocialNetwork jillNetwork = jill.getSocialNetwork();
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        SocialNetwork sandyNetwork = sandy.getSocialNetwork();

        bobNetwork.sendFriendRequest(jill);
        jillNetwork.sendFriendRequest(sandy);
        sandyNetwork.sendFriendRequest(bob);

        FriendRequest bobToJill = jillNetwork.getFrResponseList().get(bob);
        FriendRequest jillToSandy = sandyNetwork.getFrResponseList().get(jill);
        FriendRequest sandyToBob = bobNetwork.getFrResponseList().get(sandy);

        //All of the requests are WAITING, so none should be removed.
        bob.removeFriendRequest(jill);
        bob.removeFriendRequest(sandy);

        assertTrue(bobNetwork.getFrReqList().contains(bobToJill));
        assertTrue(bobNetwork.getFrReqListOrder().contains(jill));

        assertTrue(jillNetwork.getFrResponseList().contains(bobToJill));
        assertTrue(jillNetwork.getFrResponseListOrder().contains(bob));

        assertTrue(jillNetwork.getFrReqList().contains(jillToSandy));
        assertTrue(jillNetwork.getFrReqListOrder().contains(sandy));

        assertTrue(sandyNetwork.getFrResponseList().contains(jillToSandy));
        assertTrue(sandyNetwork.getFrResponseListOrder().contains(jill));

        assertTrue(sandyNetwork.getFrReqList().contains(sandyToBob));
        assertTrue(sandyNetwork.getFrReqListOrder().contains(bob));

        assertTrue(bobNetwork.getFrResponseList().contains(sandyToBob));
        assertTrue(bobNetwork.getFrResponseListOrder().contains(sandy));

        //Jill will accept Bob's request. Sandy will reject Jill's request. Bob will ignore Sandy's request.
        bobToJill.accept();
        jillToSandy.reject();

        bob.removeFriendRequest(jill);
        jill.removeFriendRequest(bob);
        jill.removeFriendRequest(sandy);
        sandy.removeFriendRequest(jill);
        bob.removeFriendRequest(sandy);
        sandy.removeFriendRequest(bob);

        assertFalse(bobNetwork.getFrReqList().contains(bobToJill));
        assertFalse(bobNetwork.getFrReqListOrder().contains(jill));

        assertFalse(jillNetwork.getFrResponseList().contains(bobToJill));
        assertFalse(jillNetwork.getFrResponseListOrder().contains(bob));

        assertTrue(jillNetwork.getFrReqList().contains(jillToSandy));
        assertTrue(jillNetwork.getFrReqListOrder().contains(sandy));

        assertTrue(sandyNetwork.getFrResponseList().contains(jillToSandy));
        assertTrue(sandyNetwork.getFrResponseListOrder().contains(jill));

        assertTrue(sandyNetwork.getFrReqList().contains(sandyToBob));
        assertTrue(sandyNetwork.getFrReqListOrder().contains(bob));

        assertTrue(bobNetwork.getFrResponseList().contains(sandyToBob));
        assertTrue(bobNetwork.getFrResponseListOrder().contains(sandy));
    }

    /**
     * This test makes sure that NPC can properly remove a current friend.
     */
    @Test
    public void testRemoveFriendUpdatesQuestsCorrectly()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        NPC ben = new NPC(0, "Ben", "He wears button-down shirts", 50, 5, 10, 1);
        MockItem item = new MockItem();

        bob.addQuestItem(item);

        SocialNetwork bobNetwork = bob.getSocialNetwork();
        SocialNetwork jillNetwork = jill.getSocialNetwork();
        SocialNetwork benNetwork = ben.getSocialNetwork();
        SocialNetwork sandyNetwork = sandy.getSocialNetwork();

        bobNetwork.addFriend(jill);
        jillNetwork.addFriend(bob);
        jillNetwork.addFriend(ben);
        benNetwork.addFriend(jill);
        bobNetwork.addFriend(sandy);
        sandyNetwork.addFriend(bob);

        /**
         * The relationship between Bob and Jill should be cleanly severed
         * sandy --- bob --- jill --- ben
         */

        //Bob made a FavorQuest targeting Jill
        bobNetwork.setCurrentCapital(500);
        Quest favQuest = QuestGenerator.genFavorQuest(bob, jill);
        bob.addQuest(favQuest);

        //Bob also made a HomewreckerQuest targeting the relationship between Jill and Ben
        bobNetwork.setCurrentCapital(500);
        Quest wreckQuest = QuestGenerator.genHomewreckerQuest(bob);
        bob.addQuest(wreckQuest);

        //Bob created a GiftQuest targeting Jill
        bobNetwork.setCurrentCapital(500);
        Quest giftQuest = QuestGenerator.genGiftQuest(bob, jill);
        bob.addQuest(giftQuest);

        //Bob owes a social debt to Jill, and Jill asked Bob for a favor
        bobNetwork.getRelationships().get(jill).setSocialDebtOwed(500);
        jill.askFavor(bob);

        assertTrue(bob.getAvailableQuests().contains(favQuest));
        assertTrue(bob.getFavorRequests().contains(jill));

        bobNetwork.removeFriend(jill);

        //the FavorQuest targeting Jill should be gone
        assertFalse(bob.getAvailableQuests().contains(favQuest));
        //the HomewreckerQuest targeting Jill and Ben should be gone
        assertFalse(bob.getAvailableQuests().contains(wreckQuest));
        //the GiftQuest should NOT be removed
        assertTrue(bob.getAvailableQuests().contains(giftQuest));
        //the favor from Jill that Bob agreed to perform should be gone
        assertFalse(bob.getFavorRequests().contains(jill));
    }

    /**
     * This test makes sure that a SocialNPC can properly select a target when trying to initiate
     * a new friendship
     */
    @Test
    public void testSelectNewFriendshipTarget()
    {
        NPC bob = new NPC(1, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC jill = new NPC(2, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(3, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        NPC fred = new NPC(4, "Fred", "He wears plaid.", 50, 5, 10, 1);
        NPC mike = new NPC(5, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
        NPC john = new NPC(5, "John", "He wears sweat pants.", 50, 5, 10, 1);
        NPC tim = new NPC(5, "tim", "He wears rainbow socks.", 50, 5, 10, 1);
        MockRoom daRoom = new MockRoom(0, "Da", "Desc");
        MockRoom otherRoom = new MockRoom(1, "Other", "Desc");

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
         * The only valid targets for Bob to try and make friends with are Sandy and Fred.
         */

        bob.getSocialNetwork().addFriend(jill, new Feelings());
        daRoom.addNPC(jill);
        daRoom.addNPC(sandy);
        daRoom.addNPC(fred);
        otherRoom.addNPC(mike);
        john.getSocialNetwork().sendFriendRequest(bob);
        bob.getSocialNetwork().sendFriendRequest(tim);

        int sandyCounter = 0;
        int fredCounter = 0;
        NPC target = null;
        int numRuns = 1000;

        for (int i = 0; i < numRuns; i++)
        {
            target = bob.pickNewFriendshipTarget();

            if (target.equals(sandy))
            {
                sandyCounter++;
            }
            else if (target.equals(fred))
            {
                fredCounter++;
            }
            else
            {
                fail("Wrong target: " + target);
            }
        }

        //valid friends should have an equal chance (+/- error) of being selected
        assertTrue("Sandy: " + sandyCounter, sandyCounter >= numRuns * 0.5 - (numRuns * error) && sandyCounter <= numRuns * 0.5 + (numRuns * error));
        assertTrue("Fred: " + fredCounter, fredCounter >= numRuns * 0.5 - (numRuns * error) && fredCounter <= numRuns * 0.5 + (numRuns * error));
    }

    /**
     * This test checks to make sure that one SocialNPC can ask another a favor. The test also makes
     * sure that the requestee properly evaluates the favor request and responds correctly.
     */
    @Test
    public void testAskAndEvaluateFavor()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC bill = new NPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
        Feelings bobsFeels = new Feelings();
        Feelings billsFeels = new Feelings();
        SocialCapitolCost difficulty;

        //add the Item that the favor will be performed on
        MockItem favorItem = new MockItem();
        bob.addQuestItem(favorItem);

        //if the two are not friends, Bob should not agree to any favors
        bill.askFavor(bob);

        assertEquals(0, bob.getFavorRequests().size());

        //if Bob does not owe Bill any social debt, then Bob should not agree to any favors
        bob.getSocialNetwork().addFriend(bill, bobsFeels);
        bill.getSocialNetwork().addFriend(bob, billsFeels);
        bobsFeels.setSocialDebtOwed(0);
        bill.askFavor(bob);

        assertEquals(0, bob.getFavorRequests().size());

        /**
         * if Bob only has enough social capital for an Easy quest, then that's the difficulty
         * he should pick, even if he owes more debt than that
         */
        bob.getSocialNetwork().setCurrentCapital(500);
        bobsFeels.setSocialDebtOwed(3000);

        bill.askFavor(bob);
        difficulty = bob.getSocialNetwork().evalFavorRequest(bill);

        assertEquals(1, bob.getFavorRequests().size());
        assertEquals(SocialCapitolCost.CHEAP, difficulty);

        //same deal with medium difficulty
        bob.getFavorRequests().clear();
        bob.getSocialNetwork().setCurrentCapital(1250);

        bill.askFavor(bob);
        difficulty = bob.getSocialNetwork().evalFavorRequest(bill);

        assertEquals(1, bob.getFavorRequests().size());
        assertEquals(SocialCapitolCost.MEDIUM, difficulty);

        //same deal with hard difficulty
        bob.getFavorRequests().clear();
        bob.getSocialNetwork().setCurrentCapital(1750);

        bill.askFavor(bob);
        difficulty = bob.getSocialNetwork().evalFavorRequest(bill);

        assertEquals(1, bob.getFavorRequests().size());
        assertEquals(SocialCapitolCost.EXPENSIVE, difficulty);

        //same deal with max difficulty
        bob.getFavorRequests().clear();
        bob.getSocialNetwork().setCurrentCapital(2500);

        bill.askFavor(bob);
        difficulty = bob.getSocialNetwork().evalFavorRequest(bill);

        assertEquals(1, bob.getFavorRequests().size());
        assertEquals(SocialCapitolCost.EXTREME, difficulty);

        //make sure Bob doesn't waste his social capital
        bob.getFavorRequests().clear();
        bob.getSocialNetwork().setCurrentCapital(3000);
        bobsFeels.setSocialDebtOwed(1000);

        bill.askFavor(bob);
        difficulty = bob.getSocialNetwork().evalFavorRequest(bill);

        assertEquals(1, bob.getFavorRequests().size());
        assertEquals(SocialCapitolCost.MEDIUM, difficulty);
    }

}