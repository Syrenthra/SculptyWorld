package sw.quest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import sw.lifeform.Party;
import sw.lifeform.Player;


import org.junit.Test;

import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.TheWorld;

import sw.item.ContainerLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.quest.Goal;
import sw.quest.ItemQuest;


public class TestItemQuest
{

    @Test
    public void testInitialize()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        assertEquals(item,quest.getItem());
        assertEquals(5,quest.getAmount());
        assertNull(quest.getGoalNode(0));
    }
    
    @Test
    public void testSetGoalNodes()
    {
        Goal node = new Room(1, "Tree","Forest");
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setGoalNode(node);
        assertEquals(node,quest.getGoalNode(0));
    }
    
    @Test
    public void testRemoveGoalNode()
    {
        Goal node = new Room(1, "Tree","Forest");
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setGoalNode(node);
        boolean value = quest.removeGoalNode(node);
        assertTrue(value);
        assertNull(quest.getGoalNode(0));
        value = quest.removeGoalNode(node);
        assertFalse(value);
    }
    
    @Test
    public void testRecognizedGoalNodeReached()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setGoalNode(room2);
        
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPlayer(player);
        
        player.addQuest(quest);
        quest.addPlayer(player);
        assertFalse(quest.goalVisited(player,room1));
        
        test.movePlayer(player, Exit.EAST);

        assertTrue(quest.goalVisited(player,room2));
    }
    
    @Test
    public void testRecognizedMultipleGoalsNodeReached()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        Room room3 = new Room(3,"Tree","Forest");
        test.addRoom(room3);
        room1.addExit(room2,Exit.EAST);
        room2.addExit(room3,Exit.EAST);
        
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setGoalNode(room2);
        quest.setGoalNode(room3);
        
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPlayer(player);
        
        player.addQuest(quest);
        quest.addPlayer(player);
        
        test.movePlayer(player, Exit.EAST);

        assertTrue(quest.goalVisited(player,room2));
        assertFalse(quest.goalVisited(player, room3));
        
        test.movePlayer(player, Exit.EAST);
        assertTrue(quest.goalVisited(player,room2));
        assertTrue(quest.goalVisited(player, room3));
    }
    
    @Test
    public void testAssignQuest()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        
        Player player = new Player(1,"Dude","Desc",50);
        
        player.addQuest(quest);
        quest.addPlayer(player);
        
        assertFalse(quest.getCompleted(player));
        assertEquals(quest,player.getQuest(0));
    }
    
    @Test
    public void testCompleteQuest()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        
        Player player = new Player(1,"Dude","Desc",50);
        
        player.addQuest(quest);
        quest.addPlayer(player);
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        storage.addValidLocation(ContainerLocation.BELT);
        player.equipContainer(storage,ContainerLocation.BELT);
        
        quest.setCompleted(player, true);
        assertTrue(quest.getCompleted(player));
    }
    
    @Test
    public void testTurnInCompletedQuest()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setMaxReward(10);
        
        Player player = new Player(1,"Dude","Desc",50);
        
        player.addQuest(quest);
        quest.addPlayer(player);
        
        quest.setCompleted(player, true);
        int reward = quest.calculateReward(player);
        assertEquals(quest.getMaxReward(),reward);
        assertFalse(quest.hasPlayer(player));
        assertNull(player.getQuest(0));
    }
    
    @Test
    public void testTurnInParitallyCompletedQuest()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setMaxReward(10);   
        quest.setGoalNode(room2);
        quest.setGoalNode(room1);
        
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPlayer(player);
        
        player.addQuest(quest);
        quest.addPlayer(player);
        
        test.movePlayer(player, Exit.EAST);
        
        assertFalse(quest.getCompleted(player));
        int reward = quest.calculateReward(player);
        assertEquals((int)(quest.getMaxReward()*0.25),reward);
        assertFalse(quest.hasPlayer(player));
        assertNull(player.getQuest(0));
    }
    
    @Test
    public void testPartyTurnsInCompletedQuest()
    {
        Player player1 = new Player(1,"Dude1","Desc",50);
        Player player2 = new Player(2,"Dude2","Desc",50);
        Party myParty1 = new Party(0,player1);
        Party myParty2 = new Party(1,player2);
        myParty1.mergeParties(myParty2);
        
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setMaxReward(10);
        
        player1.addQuest(quest);
        quest.addPlayer(player1);
        
        player2.addQuest(quest);
        quest.addPlayer(player2);
        
        quest.setCompleted(player1, true);
        assertTrue(quest.getCompleted(player2));
        
        int reward = quest.calculateReward(player1);
        assertEquals(quest.getMaxReward(),reward);
        assertFalse(quest.hasPlayer(player1));
        assertNull(player1.getQuest(0));
        
        reward = quest.calculateReward(player2);
        assertEquals(quest.getMaxReward(),reward);
        assertFalse(quest.hasPlayer(player2));
        assertNull(player2.getQuest(0));
    }
    
    @Test
    public void testEachPartyMemberParyHasToVisitGoalOnOwn()
    {
        Player player1 = new Player(1,"Dude1","Desc",50);
        Player player2 = new Player(2,"Dude2","Desc",50);
        Party myParty1 = new Party(0,player1);
        Party myParty2 = new Party(1,player2);
        myParty1.mergeParties(myParty2);
        
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        room2.addExit(room1, Exit.WEST);
        
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setMaxReward(10);   
        quest.setGoalNode(room2);
        quest.setGoalNode(room1);
        
        room1.addPlayer(player1);
        room2.addPlayer(player2);
        
        quest.addPlayer(player1);
        quest.addPlayer(player2);
        player1.addQuest(quest);
        player2.addQuest(quest);
        test.movePlayer(player1, Exit.EAST);
        assertTrue(quest.goalVisited(player1,room2));
        assertFalse(quest.goalVisited(player2,room2));
        
        test.movePlayer(player2, Exit.WEST);
        assertFalse(quest.goalVisited(player1,room1));
        assertTrue(quest.goalVisited(player2,room1));
    }
    
    @Test
    public void testPartyTurnsInPartialQuest()
    {
        Player player1 = new Player(1,"Dude1","Desc",50);
        Player player2 = new Player(2,"Dude2","Desc",50);
        Party myParty1 = new Party(0,player1);
        Party myParty2 = new Party(1,player2);
        myParty1.mergeParties(myParty2);
        
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        room2.addExit(room1, Exit.WEST);
        
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        quest.setMaxReward(10);   
        quest.setGoalNode(room2);
        quest.setGoalNode(room1);
        
        room1.addPlayer(player1);
        room2.addPlayer(player2);
        
        quest.addPlayer(player1);
        quest.addPlayer(player2);
        player1.addQuest(quest);
        player2.addQuest(quest);
        test.movePlayer(player1, Exit.EAST);
        
        test.movePlayer(player2, Exit.WEST);
        test.movePlayer(player1, Exit.WEST);
        
        assertFalse(quest.getCompleted(player1));
        assertFalse(quest.getCompleted(player2));
        int reward = quest.calculateReward(player1);
        assertEquals(quest.getMaxReward()*0.5,reward,0.001);
        assertFalse(quest.hasPlayer(player1));
        assertNull(player1.getQuest(0));
        
        reward = quest.calculateReward(player2);
        assertEquals((int)(quest.getMaxReward()*0.25),reward);
        assertFalse(quest.hasPlayer(player2));
        assertNull(player2.getQuest(0));
    }
    
}

