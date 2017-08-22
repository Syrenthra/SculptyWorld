package sw.socialNetwork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Hashtable;

import mock.MockFeelings;
import mock.MockRoom;

import org.junit.Test;

import sw.environment.Exit;
import sw.environment.TheWorld;
import sw.environment.Zone;
import sw.lifeform.NPC;
import sw.quest.QuestState;


public class TestSocialNetwork
{
    private double error = 0.05; //margin of error for tests checking chance of an event
    
    /**
     * This test makes sure that all the getters and setters work correctly.
     */
    @Test
    public void testGettersSetters()
    {
        NPC npc = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork socialNet = npc.getSocialNetwork();

        socialNet.setPersonability(1.0);
        assertEquals(1.0, socialNet.getPersonability(), 0.001);
        //illegal values
        socialNet.setPersonability(2.0);
        assertEquals(1.0, socialNet.getPersonability(), 0.001);
        socialNet.setPersonability(-0.01);
        assertEquals(1.0, socialNet.getPersonability(), 0.001);
        
        socialNet.setTotalDesiredFriends(10);
        assertEquals(10, socialNet.getTotalDesiredFriends());

        socialNet.setTotalDesiredCapital(25);
        assertEquals(25, socialNet.getTotalDesiredCapital());

        socialNet.setCurrentCapital(100);
        assertEquals(100, socialNet.getCurrentCapital());

        socialNet.setControl(0.9);
        assertEquals(0.9, socialNet.getControl(), 0.01);
        //illegal values
        socialNet.setControl(1.1);
        assertEquals(0.9, socialNet.getControl(), 0.01);
        socialNet.setControl(-0.1);
        assertEquals(0.9, socialNet.getControl(), 0.01);

        socialNet.setCurrentMood(Moods.ANGRY);
        assertEquals(Moods.ANGRY, socialNet.getCurrentMood());

        socialNet.setGrumpiness(0.1);
        assertEquals(0.1, socialNet.getGrumpiness(), 0.01);
        //illegal values
        socialNet.setGrumpiness(1.1);
        assertEquals(0.9, socialNet.getControl(), 0.01);
        socialNet.setGrumpiness(-0.1);
        assertEquals(0.9, socialNet.getControl(), 0.01);

        NPC bill = new NPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
        NPC jane = new NPC(0, "Jane", "She wears jeans.", 50, 5, 10, 1);
        NPC fred = new NPC(0, "Fred", "He wears nice shoes.", 50, 5, 10, 1);
        NPC ian = new NPC(0, "Ian", "He wears sandals.", 50, 5, 10, 1);
        NPC scott = new NPC(0, "Scott", "He wears weird hats.", 50, 5, 10, 1);
        NPC wilfred = new NPC(0, "Wilfred", "He wears a dog suit.", 50, 5, 10, 1);
        NPC mrAnderson = new NPC(0, "Mr. Anderson", "He wears a trench coat.", 50, 5, 10, 1);
        NPC duncan = new NPC(0, "Duncan", "He wears Scottish garb.", 50, 5, 10, 1);
        NPC john = new NPC(0, "John", "He wears heavy gloves.", 50, 5, 10, 1);
        NPC yvonne = new NPC(0, "Yvonne", "She wears a thick scarf.", 50, 5, 10, 1);
        NPC andrew = new NPC(0, "Andrew", "He wears boxers.", 50, 5, 10, 1);
        
        socialNet.addFriend(bill, new MockFeelings());
        assertTrue(socialNet.getFriends().contains(bill));
        assertTrue(socialNet.getRelationships().containsKey(bill));
        socialNet.addFriend(bill, new MockFeelings());
        assertEquals(1, socialNet.getFriends().size());

        socialNet.addFriend(jane, new MockFeelings());
        socialNet.addFriend(fred, new MockFeelings());
        socialNet.addFriend(ian, new MockFeelings());
        socialNet.addFriend(scott, new MockFeelings());
        socialNet.addFriend(wilfred, new MockFeelings());
        socialNet.addFriend(mrAnderson, new MockFeelings());
        socialNet.addFriend(duncan, new MockFeelings());
        socialNet.addFriend(yvonne);
        socialNet.addFriend(john);
        assertEquals(10, socialNet.getFriends().size());
        socialNet.addFriend(andrew, new MockFeelings());
        // max friends is 10, so Andrew should not have been added
        assertEquals(10, socialNet.getFriends().size());
        assertTrue(!socialNet.getFriends().contains(andrew));

        socialNet.removeFriend(bill);
        assertFalse(socialNet.getFriends().contains(bill));
        assertFalse(socialNet.getRelationships().containsKey(bill));
    }

    /**
     * The purpose of this test is to make sure that a SocialNPC can accurately determine it's
     * current total amount of social capital.
     */
    @Test
    public void testUpdateCapital()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork socialNetwork = bob.getSocialNetwork();
        socialNetwork.setControl(0.5);
        NPC fred = new NPC(0, "Fred", "He wears big shirts.", 50, 5, 10, 1);
        NPC bill = new NPC(0, "Bill", "He wears hats.", 50, 5, 10, 1);
        MockFeelings feelsForFred = new MockFeelings();
        MockFeelings feelsForBill = new MockFeelings();

        socialNetwork.updateCapital();
        assertEquals(0, socialNetwork.getCurrentCapital());

        socialNetwork.addFriend(fred, feelsForFred);
        socialNetwork.addFriend(bill, feelsForBill);
        socialNetwork.updateCapital();

        assertEquals(70, socialNetwork.getCurrentCapital());

        feelsForFred.setIntimacy(10);
        feelsForFred.setTrust(5);
        socialNetwork.updateCapital();
        assertEquals(155, socialNetwork.getCurrentCapital());

        socialNetwork.setCurrentCapital(0);

        feelsForBill.setIntimacy(20);
        feelsForBill.setTrust(1);
        socialNetwork.updateCapital();
        assertEquals(70, socialNetwork.getCurrentCapital());

        socialNetwork.setCurrentCapital(0);

        socialNetwork.removeFriend(bill);
        socialNetwork.updateCapital();
        assertEquals(50, socialNetwork.getCurrentCapital());

        socialNetwork.removeFriend(fred);
        socialNetwork.updateCapital();
        assertEquals(50, socialNetwork.getCurrentCapital());
    }
    
    /**
     * This test makes sure that a SocialNPC can properly terminate a given list of relationships.
     */
    @Test
    public void testRemoveFriendSet()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        NPC fred = new NPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
        NPC mike = new NPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
        ArrayList<NPC> toBeRemoved = new ArrayList<NPC>();
        SocialNetwork bobNetwork = bob.getSocialNetwork();

        //Shouldn't cause any problems when the SocialNPC doesn't have any friends.
        bobNetwork.removeFriendSet(null);

        //Shouldn't do anything when trying to remove a SocialNPC that Bob is not friends with.
        //when Bob has no friends
        toBeRemoved.add(jill);
        bobNetwork.removeFriendSet(toBeRemoved);

        assertEquals(0, bobNetwork.getFriends().size());

        //Shouldn't do anything when trying to remove a SocialNPC that Bob is not friends with
        //when Bob has friends
        bobNetwork.addFriend(sandy, new MockFeelings());
        bobNetwork.addFriend(fred, new MockFeelings());
        bobNetwork.addFriend(mike, new MockFeelings());

        bobNetwork.removeFriendSet(toBeRemoved);

        assertEquals(3, bobNetwork.getFriends().size());

        //Should remove all the SocialNPCs specified and leave others alone.
        bobNetwork.addFriend(jill, new MockFeelings());
        toBeRemoved.add(sandy);

        bobNetwork.removeFriendSet(toBeRemoved);

        assertEquals(2, bobNetwork.getFriends().size());
        assertTrue(bobNetwork.getFriends().contains(fred));
        assertTrue(bobNetwork.getFriends().contains(mike));
    }
    
    /**
     * This test makes sure that the list returned by identifyGrowingRelationships() is
     * ordered properly and contains the right elements.
     */
    @Test
    public void testIdentifyGrowingRelationships()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        NPC fred = new NPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
        NPC mike = new NPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
        NPC seamus = new NPC(0, "Seamus", "He wears green.", 50, 5, 10, 1);
        NPC lucy = new NPC(0, "Lucy", "She wears pink.", 50, 5, 10, 1);
        NPC don = new NPC(0, "Don", "He wears green.", 50, 5, 10, 1);
        NPC bill = new NPC(0, "Bill", "She wears pink.", 50, 5, 10, 1);

        MockFeelings bobForJill = new MockFeelings();
        MockFeelings bobForSandy = new MockFeelings();
        MockFeelings bobForFred = new MockFeelings();
        MockFeelings bobForMike = new MockFeelings();
        MockFeelings bobForSeamus = new MockFeelings();
        MockFeelings bobForLucy = new MockFeelings();
        MockFeelings bobForDon = new MockFeelings();
        MockFeelings bobForBill = new MockFeelings();

        SocialNetwork bobNetwork = bob.getSocialNetwork();
        
        bobNetwork.addFriend(sandy, bobForSandy);
        bobNetwork.addFriend(fred, bobForFred);
        bobNetwork.addFriend(jill, bobForJill);
        bobNetwork.addFriend(mike, bobForMike);
        bobNetwork.addFriend(seamus, bobForSeamus);
        bobNetwork.addFriend(lucy, bobForLucy);
        bobNetwork.addFriend(don, bobForDon);
        bobNetwork.addFriend(bill, bobForBill);

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

        ArrayList<NPC> list = bobNetwork.identifyGrowingRelationships(10);

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
     * This test makes sure that NPC can properly remove a current friend.
     */
    @Test
    public void testCreatingAndRemovingFriends()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        NPC ben = new NPC(0, "Ben", "He wears button-down shirts", 50, 5, 10, 1);

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

        assertTrue(jillNetwork.getFriends().contains(bob));
        assertTrue(jillNetwork.getFriends().contains(ben));
        assertTrue(benNetwork.getFriends().contains(jill));
        assertTrue(bobNetwork.getFriends().contains(jill));
        assertTrue(bobNetwork.getFriends().contains(sandy));
        assertTrue(sandyNetwork.getFriends().contains(bob));
        
        // Should we test for the other relationships being created?
        assertTrue(bobNetwork.getRelationships().containsKey(jill));

        bobNetwork.removeFriend(jill);

        //Jill should not be in Bob's list of friends, and Sandy should
        assertFalse(bobNetwork.getFriends().contains(jill));
        assertFalse(bobNetwork.getRelationships().containsKey(jill));
        assertTrue(bobNetwork.getFriends().contains(sandy));
        assertTrue(bobNetwork.getRelationships().containsKey(sandy));
    }
    
    /**
     * This test makes sure that idnetifyLowTrustFriends works properly. The list should only
     * contain friends of the SocialNPC that the SocialNPC does not trust (trust is < 0). The
     * list should be in ascending order based on trust (lowest values at beginning).
     */
    @Test
    public void testIdentifyLowTrustFriends()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        MockFeelings bobsFeelsForJill = new MockFeelings();
        MockFeelings bobsFeelsForSandy = new MockFeelings();
        ArrayList<NPC> list;

        //the list should be empty if Bob has no friends
        list = bobNetwork.identifyLowIntimacyFriends();

        assertEquals(0, list.size());

        //Bob has 2 friends, both of which are productive relationships.
        bobNetwork.addFriend(jill, bobsFeelsForJill);
        bobNetwork.addFriend(sandy, bobsFeelsForSandy);
        bobsFeelsForJill.setTrust(2);
        bobsFeelsForSandy.setTrust(2);

        list = bobNetwork.identifyLowTrustFriends();

        //list should be empty since neither of Bobs friendships are close to being terminated
        assertEquals(0, list.size());

        //the relationships have decayed... (border cases)
        bobsFeelsForJill.setTrust(0);
        bobsFeelsForSandy.setTrust(-1);

        list = bobNetwork.identifyLowTrustFriends();

        assertEquals(1, list.size());
        assertTrue(list.contains(sandy));

        //the elements of the list should be in ascending order
        bobsFeelsForJill.setTrust(-2);

        list = bobNetwork.identifyLowTrustFriends();

        assertEquals(jill, list.get(0));
        assertEquals(sandy, list.get(1));

        bobsFeelsForSandy.setTrust(-3);

        list = bobNetwork.identifyLowTrustFriends();

        assertEquals(jill, list.get(1));
        assertEquals(sandy, list.get(0));
    }
    
    /**
     * This test makes sure that a SocialNPC can identify relationships that are dangerously
     * near being terminated due to low intimacy.
     */
    @Test
    public void testIdentifyLowIntimacyFriends()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        MockFeelings bobsFeelsForJill = new MockFeelings();
        MockFeelings bobsFeelsForSandy = new MockFeelings();
        ArrayList<NPC> list;

        //the list should be empty if Bob has no friends
        list = bobNetwork.identifyLowIntimacyFriends();

        assertEquals(0, list.size());

        //Bob has 2 friends, both of which are productive relationships.
        bobNetwork.addFriend(jill, bobsFeelsForJill);
        bobNetwork.addFriend(sandy, bobsFeelsForSandy);
        bobsFeelsForJill.setIntimacy(50);
        bobsFeelsForSandy.setIntimacy(50);

        list = bobNetwork.identifyLowIntimacyFriends();

        //list should be empty since neither of Bobs friendships are close to being terminated
        assertEquals(0, list.size());

        //the relationships have decayed... (border cases)
        bobsFeelsForJill.setIntimacy(20);
        bobsFeelsForSandy.setIntimacy(19);

        list = bobNetwork.identifyLowIntimacyFriends();

        assertEquals(1, list.size());
        assertTrue(list.contains(sandy));

        //the elements of the list should be in ascending order
        bobsFeelsForJill.setIntimacy(5);

        list = bobNetwork.identifyLowIntimacyFriends();

        assertEquals(jill, list.get(0));
        assertEquals(sandy, list.get(1));

        bobsFeelsForSandy.setIntimacy(3);

        list = bobNetwork.identifyLowIntimacyFriends();

        assertEquals(jill, list.get(1));
        assertEquals(sandy, list.get(0));
    }
    
    /**
     * This test makes sure that a SocialNPC can identify relationships that are not useful to it.
     */
    @Test
    public void testIdentifyUnproductiveFriends()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        MockFeelings bobForJill = new MockFeelings();
        MockFeelings bobForSandy = new MockFeelings();
        ArrayList<NPC> toBeRemoved = null;

        //should work fine and return an empty ArrayList when Bob has no friends
        toBeRemoved = bobNetwork.identifyUnproductiveFriendships(1);
        assertEquals(0, toBeRemoved.size());

        bobNetwork.addFriend(bill);
        billNetwork.addFriend(bob);

        /**
         * The relationship has 0 trust initially, so it decays at 3 intimacy per turn.
         * With 35 intimacy, it should reach min intimacy in 12 turns
         */

        assertEquals(0, bobNetwork.identifyUnproductiveFriendships(11).size());
        assertEquals(1, bobNetwork.identifyUnproductiveFriendships(12).size());
        assertTrue(bobNetwork.identifyUnproductiveFriendships(12).contains(bill));

        /**
         * List should be ordered...
         * 
         * jill (t=-5, i=20) decay in 4 turns
         * bill (t=0, i=20) decay in 7 turns
         * sandy (t=-3, i=40) decay in 8
         */

        bobForJill.setTrust(-5);
        bobForJill.setIntimacy(20);
        bobNetwork.getRelationships().get(bill).setIntimacy(20);
        bobNetwork.getRelationships().get(bill).setTrust(0);
        bobForSandy.setTrust(-3);
        bobForSandy.setIntimacy(40);
        bobNetwork.addFriend(jill, bobForJill);
        bobNetwork.addFriend(sandy, bobForSandy);

        toBeRemoved = bobNetwork.identifyUnproductiveFriendships(10);

        assertEquals(3, toBeRemoved.size());
        assertEquals(jill, toBeRemoved.get(0));
        assertEquals(bill, toBeRemoved.get(1));
        assertEquals(sandy, toBeRemoved.get(2));
    }
    
    /**
     * This test makes sure that a SocialNPC can properly send and receive FriendRequests.
     */
    @Test
    public void testSendReceiveFriendRequests()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(0, "Bill", "He wears jeans.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC jane = new NPC(0, "Jane", "She wears overalls.", 50, 5, 10, 1);
        SocialNetwork janeNetwork = jane.getSocialNetwork();

        bobNetwork.sendFriendRequest(bill);
        janeNetwork.sendFriendRequest(bill);
        bobNetwork.sendFriendRequest(jane);

        Hashtable<NPC, FriendRequest> bobsRequests = bobNetwork.getFrReqList();
        Hashtable<NPC, FriendRequest> billsResponses = billNetwork.getFrResponseList();
        ArrayList<NPC> requestOrder = bobNetwork.getFrReqListOrder();
        ArrayList<NPC> responseOrder = billNetwork.getFrResponseListOrder();

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
    }
    
    /**
     * This test makes sure that findAcceptedRequsts() works properly.
     */
    @Test
    public void testFindAcceptedFriends()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC sandy = new NPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        SocialNetwork sandyNetwork = sandy.getSocialNetwork();

        bobNetwork.setTotalDesiredFriends(2);
        billNetwork.setTotalDesiredFriends(2);

        //if no requests have been sent, the frReqList is empty, so no ACCEPTED requests should be found
        assertEquals(0, bobNetwork.findAcceptedRequests().size());

        bobNetwork.sendFriendRequest(sandy);
        bobNetwork.sendFriendRequest(bill);
        billNetwork.sendFriendRequest(bob);

        FriendRequest bobToSandy = sandyNetwork.getFrResponseList().get(bob);
        FriendRequest bobToBill = billNetwork.getFrResponseList().get(bob);

        bobToSandy.reject();//Sandy doesn't want any new friends
        bobToBill.accept();//Bill does want a new friend
        //Bob hasn't gotten the chance to evaluate his received requests yet

        //Bill accepted Bob's request and Sandy did not
        assertEquals(1, bobNetwork.findAcceptedRequests().size());
        assertTrue(bobNetwork.findAcceptedRequests().contains(bill));

        //Bob hasn't decided about Bill's request yet
        assertEquals(0, billNetwork.findAcceptedRequests().size());

        //if Bob already has as many friends as he wants, the list should be empty
        bobNetwork.setTotalDesiredFriends(1);
        bobNetwork.addFriend(bill, new MockFeelings());

        assertEquals(0, bobNetwork.findAcceptedRequests().size());
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
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();
        NPC sandy = new NPC(2, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        SocialNetwork sandyNetwork = sandy.getSocialNetwork();
        
        MockRoom room = new MockRoom(1, "Room", "Desc");
        Hashtable<NPC, FriendRequest> bobsSentRequests = bobNetwork.getFrReqList();
        Hashtable<NPC, FriendRequest> bobsReceivedRequests = bobNetwork.getFrResponseList();
        ArrayList<NPC> newFriends;

        room.addNPC(bob);
        room.addNPC(bill);
        room.addNPC(sandy);
        
        TheWorld.getInstance().addRoom(room);
        TheWorld.getInstance().constructZoneGraph();
        
        bobNetwork.setPersonability(1.0);
        billNetwork.setPersonability(1.0);
        sandyNetwork.setPersonability(1.0);

        //Bob doesn't want any friends, so he should not pick any.
        assertEquals(0, bobNetwork.pickNewFriends().size());

        //Bob wants 1 friend. He sent a request to Bill, which was accepted. Sandy sent Bob a request.
        //Bob should decide to be friends with Bill over Sandy. Bob should not create any new requests.
        bobNetwork.setTotalDesiredFriends(1);
        bobNetwork.sendFriendRequest(bill);
        billNetwork.getFrResponseList().get(bob).accept();
        sandyNetwork.sendFriendRequest(bob);
        FriendRequest sandyToBob = bobsReceivedRequests.get(sandy);

        newFriends = bobNetwork.pickNewFriends();

        assertEquals(1, newFriends.size()); //1 new friend
        assertTrue(newFriends.contains(bill)); //new friend is Bill
        assertEquals(FriendRequestStatus.REJECTED, sandyToBob.getState()); //don't want to be friends with Sandy
        //only sent request should be from Bob to Bill
        assertEquals(1, bobsSentRequests.size());
        assertTrue(bobsSentRequests.containsKey(bill));
        assertEquals(1, billNetwork.getFrResponseList().size());
        assertTrue(billNetwork.getFrResponseList().containsKey(bob));

        /**
         * Bob wants 1 friend, but none of the requests he sent have been accepted. Sandy and Bill
         * have both sent Bob requests. Because Sandy sent her request first, Bob should pick
         * Sandy over Bill. Bob should not send any requests.
         */
        bobNetwork.getFrReqList().clear();
        bobNetwork.getFrReqListOrder().clear();
        bobNetwork.getFrResponseList().clear();
        bobNetwork.getFrResponseListOrder().clear();
        billNetwork.getFrResponseList().clear();
        billNetwork.getFrResponseListOrder().clear();
        sandyNetwork.getFrReqList().clear();
        sandyNetwork.getFrReqListOrder().clear();

        sandyNetwork.sendFriendRequest(bob);
        billNetwork.sendFriendRequest(bob);
        sandyToBob = sandyNetwork.getFrReqList().get(bob);
        FriendRequest billToBob = billNetwork.getFrReqList().get(bob);

        newFriends = bobNetwork.pickNewFriends();

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
        sandyNetwork.getFrReqList().clear();
        sandyNetwork.getFrReqListOrder().clear();
        billNetwork.getFrReqList().clear();
        billNetwork.getFrReqListOrder().clear();
        bobNetwork.getFrResponseList().clear();
        bobNetwork.getFrResponseListOrder().clear();

        bobNetwork.setTotalDesiredFriends(2);

        newFriends = bobNetwork.pickNewFriends();

        assertEquals(0, newFriends.size());
        assertEquals(2, bobsSentRequests.size());
        assertTrue(bobNetwork.getFrReqList().containsKey(sandy));
        assertTrue(bobNetwork.getFrReqList().containsKey(bill));
    }
    
    /**
     * This test makes sure that the hasFriend method works correctly.
     */
    @Test
    public void testHasFriend()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
        SocialNetwork billNetwork = bill.getSocialNetwork();

        assertFalse(bobNetwork.hasFriend(bill));
        assertFalse(billNetwork.hasFriend(bob));

        bobNetwork.addFriend(bill, new Feelings());

        assertTrue(bobNetwork.hasFriend(bill));
        assertFalse(billNetwork.hasFriend(bob));

        billNetwork.addFriend(bob, new Feelings());

        assertTrue(bobNetwork.hasFriend(bill));
        assertTrue(billNetwork.hasFriend(bob));
    }
    
    /**
     * The purpose of this test is to make sure that a SocialNPC changes moods based on quest
     * completion only when appropriate.
     */
    @Test
    public void testChangeMoodFromQuests()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();

        bobNetwork.setGrumpiness(.9); //90% chance to become angry

        int counter = 0;
        int numRuns = 1000;
        for (int i = 0; i < numRuns; i++)
        {
            bob.setLastQuestResult(QuestState.FAILED);
            bob.updateMood();
            if (bob.getCurrentMood() == Moods.ANGRY)
            {
                counter++;
                bobNetwork.setCurrentMood(Moods.HAPPY);
            }
        }

        //Bob should become angry 9 times out of 10 (+/- error)
        assertTrue("counter: " + counter, counter >= numRuns * 0.9 - (numRuns * error) && counter <= numRuns * 0.9 + (numRuns * error));

        //make sure that changing mood to happy works too
        counter = 0;
        bobNetwork.setGrumpiness(0.1); //90% chance to become happy
        bobNetwork.setCurrentMood(Moods.ANGRY);

        for (int i = 0; i < numRuns; i++)
        {
            bob.setLastQuestResult(QuestState.COMPLETED);
            bob.updateMood();
            if (bob.getCurrentMood() == Moods.HAPPY)
            {
                counter++;
                bobNetwork.setCurrentMood(Moods.ANGRY);
            }

        }

        //Bob should become happy 9 times out of 10 (+/- error)
        assertTrue("counter: " + counter, counter >= numRuns * 0.9 - (numRuns * error) && counter <= numRuns * 0.9 + (numRuns * error));

    }
    
    /**
     * The purpose of this test is to make sure that a SocialNPC changes moods appropriately based
     * on the moods of its friends.
     */
    @Test
    public void testChangeMoodFromPropagation()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC jill = new NPC(0, "Jill", "She wears pantaloons.", 50, 5, 10, 1);
        SocialNetwork jillNetwork = bob.getSocialNetwork();
        NPC sandy = new NPC(0, "Sandy", "She wears bell bottoms.", 50, 5, 10, 1);
        SocialNetwork sandyNetwork = sandy.getSocialNetwork();
        NPC fred = new NPC(0, "Fred", "He wears plaid.", 50, 5, 10, 1);
        SocialNetwork fredNetwork = fred.getSocialNetwork();
        NPC mike = new NPC(0, "Mike", "He wears jumpsuits.", 50, 5, 10, 1);
        SocialNetwork mikeNetwork = mike.getSocialNetwork();
        
        MockFeelings bobsFeels = new MockFeelings();

        bobNetwork.addFriend(jill, bobsFeels);
        bobNetwork.addFriend(sandy, bobsFeels);
        bobNetwork.addFriend(fred, bobsFeels);
        bobNetwork.addFriend(mike, bobsFeels);

        jillNetwork.setCurrentMood(Moods.ANGRY);
        sandyNetwork.setCurrentMood(Moods.ANGRY);
        fredNetwork.setCurrentMood(Moods.ANGRY);
        mikeNetwork.setCurrentMood(Moods.ANGRY);

        // bob is Happy, all his friends are Angry, so he should become Angry
        // about 15% of the time
        int counter = 0;
        int numRuns = 1000;
        for (int i = 0; i < numRuns; i++)
        {
            bobNetwork.changeMoodPropagation();
            if (bob.getCurrentMood() == Moods.ANGRY)
            {
                counter++;
                bobNetwork.setCurrentMood(Moods.HAPPY);
            }
        }

        //Bob should become angry 15% of the time (+/- error)
        assertTrue("mood changes: " + counter, (counter >= numRuns * 0.15 - (numRuns * error)) && (counter <= numRuns * 0.15 + (numRuns * error)));

        // make sure mood propagation also works from Angry to Happy
        bobNetwork.setCurrentMood(Moods.ANGRY);
        jillNetwork.setCurrentMood(Moods.HAPPY);
        sandyNetwork.setCurrentMood(Moods.HAPPY);
        fredNetwork.setCurrentMood(Moods.HAPPY);
        mikeNetwork.setCurrentMood(Moods.HAPPY);

        counter = 0;
        for (int i = 0; i < numRuns; i++)
        {
            bobNetwork.changeMoodPropagation();
            if (bob.getCurrentMood() == Moods.HAPPY)
            {
                counter++;
                bobNetwork.setCurrentMood(Moods.ANGRY);
            }
        }

        //Bob should become happy 15% of the time (+/- error)
        assertTrue("mood changes: " + counter, (counter >= numRuns * 0.15 - (numRuns * error)) && (counter <= numRuns * 0.15 + (numRuns * error)));
    }
    
    /**
     * This test makes sure that a SNPC set up as a broker will properly pick new friends.
     */
    @Test
    public void testPickNewFriendsBroker()
    {
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        SocialNetwork bobNetwork = bob.getSocialNetwork();
        NPC bill = new NPC(1, "Bill", "He wears jeans.", 50, 5, 10, 1);
        
        MockRoom room1 = new MockRoom(1, "Room1", "Desc1");
        room1.setZone(Zone.CITY);
        MockRoom room2 = new MockRoom(2, "Room2", "Desc2");
        room2.setZone(Zone.BEACH);
        MockRoom room3 = new MockRoom(3, "Room3", "Desc3");
        room3.setZone(Zone.BEACH);

        room1.addExit(room2, Exit.NORTH);
        room2.addExit(room1, Exit.SOUTH);
        
        room3.addExit(room2, Exit.SOUTH);
        room2.addExit(room3, Exit.NORTH);
        
        room1.addNPC(bob);
        room3.addNPC(bill);

        TheWorld.getInstance().addRoom(room1);
        TheWorld.getInstance().addRoom(room2);
        TheWorld.getInstance().addRoom(room3);
        TheWorld.getInstance().constructZoneGraph();
        
        bobNetwork.setIsBrokerNode(true);
        bobNetwork.setPersonability(1.0);
        bobNetwork.setTotalDesiredFriends(2);

        assertEquals(bill, bob.pickNewFriendshipTarget());
        // Bill should not be able to find a new friend as he is not a broker.
        assertNull(bill.pickNewFriendshipTarget());
    }
    
}
