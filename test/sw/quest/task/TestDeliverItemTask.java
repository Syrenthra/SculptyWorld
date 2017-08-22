package sw.quest.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import sw.lifeform.PC;
import sw.lifeform.NPC;


import mock.MockItem;

import org.junit.Before;
import org.junit.Test;

import sw.item.ContainerLocation;
import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.quest.Quest;


public class TestDeliverItemTask
{
    Quest quest1;
    Quest quest2;
    NPC granter;
    
    @Before
    public void before()
    {
        granter = new NPC(1,"Test","Desc",10,5,10,4);
        quest1 = new Quest("Quest","Description",granter);
        quest2 = new Quest("Quest2","Description2",granter);
    }

    @Test
    public void testInitialize()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        DeliverItemTask task = new DeliverItemTask(quest1,item,5);
        assertEquals(item,task.getItem());
        assertEquals(5,task.getAmount());
        assertEquals(TaskType.ITEM_TASK,task.getType());
        assertEquals(granter,task.getTarget());
    }
 
    @Test
    public void testAddPlayer()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        DeliverItemTask task = new DeliverItemTask(quest1,item,5);
        PC player = new PC(1,"Dude","Desc",50);
        task.addPlayer(player);
        assertTrue(task.m_itemsFound.containsKey(player.getID()));
        assertEquals(new Integer(0),task.m_itemsFound.get(player.getID()));
        assertTrue(task.m_itemsTurnedIn.containsKey(player.getID()));
        assertEquals(new Integer(0),task.m_itemsTurnedIn.get(player.getID()));
    }
       
    @Test
    public void testCanHowManyQuestItemsAPlayerHas()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        item.setItemID(1);
        Item foundItem1 = new MockItem("Test","Test Item",5,10);
        foundItem1.setItemID(2);
        Item foundItem2 = new MockItem("Test","Test Item",5,10);
        foundItem2.setItemID(3);
        DeliverItemTask task = new DeliverItemTask(quest1,item,2);
        quest1.addTask(task);
        
        PC player = new PC(1,"Dude","Desc",50);
        
        player.assignNativeQuest(quest1);
        quest1.addPlayer(player);
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        storage.setValidLocation(ContainerLocation.BELT);
        player.equipContainer(storage);
        //Where does the player actually complete the quest?
        player.holdInHand(foundItem1, HandLocation.RIGHT);
        assertEquals(1,task.getNumPlayerItems(player));
        
        player.holdInHand(foundItem2, HandLocation.LEFT);
        assertEquals(2,task.getNumPlayerItems(player));
        player.dropFromHand(HandLocation.RIGHT);
        assertEquals(1,task.getNumPlayerItems(player));
    }
    
    @Test
    public void testTwoQuestsSameItemWithSameTarget()
    {
        MockItem item = new MockItem("Test","Test Item",5,10);
        DeliverItemTask task1 = new DeliverItemTask(quest1,item,1);
        quest1.addTask(task1);
        
        DeliverItemTask task2 = new DeliverItemTask(quest2,item,2);
        quest2.addTask(task2);
        
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        PC player3 = new PC(3,"Dude3","Desc",50);
        
        player1.assignNativeQuest(quest1);
        
        
        player2.assignNativeQuest(quest1);
        player2.assignNativeQuest(quest2);
        
        player3.assignNativeQuest(quest2);
        
        // This should cause quest1's task to finish for player1 and player2,
        // but the task for quest2 for player2 and player3 should not be finished.
        // Player2 and Player3 should still be able to give items to granter.
        // Player1 should not be able to give the item to granter.
        player1.holdInHand(item.clone(), HandLocation.RIGHT);
        player1.giveItemInHand(granter,HandLocation.RIGHT);
        
        assertEquals(1,task1.m_totalItemsTurnedIn);
        assertEquals(0,task2.m_totalItemsTurnedIn);
        assertTrue(""+task1.m_itemsTurnedIn.get(player1.getID()),1 == task1.m_itemsTurnedIn.get(player1.getID()));
        assertTrue(0 == task1.m_itemsTurnedIn.get(player2.getID())); // Because player2 not in same party.
        assertTrue(0 == task2.m_itemsTurnedIn.get(player2.getID()));
        assertTrue(0 == task2.m_itemsTurnedIn.get(player3.getID()));
        
        // Make sure that player 1 can't give something to the NPC.
        player1.holdInHand(item.clone(), HandLocation.RIGHT);
        player1.giveItemInHand(granter,HandLocation.RIGHT);
        
        assertEquals(item,player1.getContentsInHand(HandLocation.RIGHT));
        assertEquals(null,granter.getPersonalItem(1));
        
        // Need to test that player 2 or player 3 can still give the item.
        player2.holdInHand(item.clone(), HandLocation.RIGHT);
        player2.giveItemInHand(granter,HandLocation.RIGHT);
        
        assertEquals(1,task2.m_totalItemsTurnedIn);
        
        player3.holdInHand(item.clone(), HandLocation.RIGHT);
        player3.giveItemInHand(granter,HandLocation.RIGHT);
        
        assertEquals(1,task1.m_totalItemsTurnedIn);
        assertEquals(2,task2.m_totalItemsTurnedIn);
        assertTrue(""+task1.m_itemsTurnedIn.get(player1.getID()),1 == task1.m_itemsTurnedIn.get(player1.getID()));
        assertTrue(0 == task1.m_itemsTurnedIn.get(player2.getID()));
        assertTrue(1 == task2.m_itemsTurnedIn.get(player2.getID()));
        assertTrue(1 == task2.m_itemsTurnedIn.get(player3.getID()));
        
        
        // TODO: Need to test that if player4 goes active on quest1 that he can't still give item.
        PC player4 = new PC(4,"Dude4","Desc",50);
        player4.assignNativeQuest(quest1);
        player4.holdInHand(item.clone(), HandLocation.RIGHT);
        player4.giveItemInHand(granter,HandLocation.RIGHT);
        
        assertEquals(item,player1.getContentsInHand(HandLocation.RIGHT));
        assertEquals(1,task1.m_totalItemsTurnedIn);
        assertTrue(""+task1.m_itemsTurnedIn.get(player1.getID()),1 == task1.m_itemsTurnedIn.get(player1.getID()));
        assertTrue(0 == task1.m_itemsTurnedIn.get(player2.getID()));
        assertTrue(0 == task1.m_itemsTurnedIn.get(player4.getID()));
    }
    
    @Test
    public void testTurnInItemsToTarget()
    {
        MockItem item = new MockItem("Test","Test Item",5,10);
        DeliverItemTask task = new DeliverItemTask(quest1,item,5);
        quest1.addTask(task);
        
        PC player = new PC(1,"Dude","Desc",50);
        
        player.assignNativeQuest(quest1);
        player.holdInHand(item.clone(), HandLocation.RIGHT);
        player.giveItemInHand(granter,HandLocation.RIGHT);
        
        assertEquals(20,task.percentComplete(player));
        assertEquals(20,task.overallPercentComplete());
        assertEquals(1,task.m_totalItemsTurnedIn);
        assertTrue(1 == task.m_itemsTurnedIn.get(player.getID()));
        
        // NPC should not accept this item as not related to any tasks of any quests it offers.
        MockItem item2 = new MockItem("Test2","Test Item2",5,10);
        player.holdInHand(item2.clone(), HandLocation.RIGHT);
        player.giveItemInHand(granter, HandLocation.RIGHT);
        
        assertEquals(20,task.percentComplete(player));
        assertEquals(20,task.overallPercentComplete());
        assertTrue(""+task.m_itemsTurnedIn.get(player.getID()),1 == task.m_itemsTurnedIn.get(player.getID()));
        assertEquals("Test2",player.getContentsInHand(HandLocation.RIGHT).getName());
        assertEquals(null,granter.getPersonalItem(1));
        
        player.holdInHand(item.clone(), HandLocation.LEFT);
        player.giveItemInHand(granter,HandLocation.LEFT);
        
        player.holdInHand(item.clone(), HandLocation.LEFT);
        player.giveItemInHand(granter,HandLocation.LEFT);
        
        player.holdInHand(item.clone(), HandLocation.LEFT);
        player.giveItemInHand(granter,HandLocation.LEFT);
        
        player.holdInHand(item.clone(), HandLocation.LEFT);
        player.giveItemInHand(granter,HandLocation.LEFT);
        
        assertEquals(100,task.percentComplete(player));
        assertEquals(100,task.overallPercentComplete());
        assertTrue(5 == task.m_itemsTurnedIn.get(player.getID()));
        assertEquals(5,task.m_totalItemsTurnedIn);
        
        // NPC should not accept this item as the player has already turned in enough.
        player.holdInHand(item.clone(), HandLocation.LEFT);
        player.giveItemInHand(granter,HandLocation.LEFT);
        
        assertEquals(item,player.getContentsInHand(HandLocation.LEFT));
        assertEquals(null,granter.getPersonalItem(5));
        assertEquals(100,task.percentComplete(player));
        assertEquals(100,task.overallPercentComplete());
        assertTrue(5 == task.m_itemsTurnedIn.get(player.getID()));
        assertEquals(5,task.m_totalItemsTurnedIn);
    }
       
}

