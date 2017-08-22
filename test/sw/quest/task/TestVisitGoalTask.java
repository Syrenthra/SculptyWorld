package sw.quest.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mock.MockItem;

import org.junit.Before;
import org.junit.Test;

import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.TheWorld;
import sw.item.Item;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.lifeform.Party;
import sw.quest.Goal;
import sw.quest.Quest;


public class TestVisitGoalTask
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
        VisitGoalTask task = new VisitGoalTask(quest1);
        assertNull(task.getGoalNode(0));
        assertEquals(TaskType.VISIT_GOAL_TASK,task.getType());
    }
    
    @Test
    public void testAddandRemovePlayer()
    {
        VisitGoalTask task = new VisitGoalTask(quest1);
        PC player = new PC(1,"Dude","Desc",50);
        task.addPlayer(player);
        assertTrue(task.m_goalsReached.containsKey(player.getID()));
        task.removePlayer(player);
        assertFalse(task.m_goalsReached.containsKey(player.getID()));
    }
    
    
    @Test
    public void testSetGoalNodes()
    {
        Goal node = new Room(1, "Tree","Forest");
        VisitGoalTask test = new VisitGoalTask(quest1);
        test.setGoalNode(node);
        assertEquals(node,test.getGoalNode(0));
    }

    @Test
    public void testRemoveGoalNode()
    {
        Goal node = new Room(1, "Tree","Forest");
        VisitGoalTask task = new VisitGoalTask(quest1);
        task.setGoalNode(node);
        boolean value = task.removeGoalNode(node);
        assertTrue(value);
        assertNull(task.getGoalNode(0));
        value = task.removeGoalNode(node);
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
        
        VisitGoalTask task = new VisitGoalTask(quest1);
        quest1.addTask(task);
        task.setGoalNode(room2);
        
        PC player = new PC(1,"Dude","Desc",50);
        player.assignNativeQuest(quest1);
        room1.addPC(player);
        
        quest1.addPlayer(player);
        assertFalse(task.goalVisited(player,room1));
        assertEquals(0,task.percentComplete(player));
        
        test.movePlayer(player, Exit.EAST);

        assertTrue(task.goalVisited(player,room2));
        assertEquals(100,task.percentComplete(player));
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
        
        VisitGoalTask task = new VisitGoalTask(quest1);
        quest1.addTask(task);
        task.setGoalNode(room2);
        task.setGoalNode(room3);
        
        PC player = new PC(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPC(player);
        
        player.assignNativeQuest(quest1);
        
        test.movePlayer(player, Exit.EAST);

        assertTrue(task.goalVisited(player,room2));
        assertFalse(task.goalVisited(player, room3));
        assertEquals(50,task.percentComplete(player));
        assertEquals(50,task.overallPercentComplete());
        
        test.movePlayer(player, Exit.EAST);
        assertTrue(task.goalVisited(player,room2));
        assertTrue(task.goalVisited(player, room3));
        assertEquals(100,task.percentComplete(player));
        assertEquals(100,task.overallPercentComplete());
    }
    
    @Test
    public void testEachPartyMemberParyHasToVisitGoalOnOwn()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty1 = player1.getParty();
        Party myParty2 = player2.getParty();
        myParty1.mergeParties(myParty2);
        
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        room2.addExit(room1, Exit.WEST);
        
        VisitGoalTask task = new VisitGoalTask(quest1);
        quest1.addTask(task);  
        task.setGoalNode(room2);
        task.setGoalNode(room1);
        
        room1.addPC(player1);
        room2.addPC(player2);
        
        player1.assignNativeQuest(quest1);
        test.movePlayer(player1, Exit.EAST);
        assertTrue(task.goalVisited(player1,room2));
        assertFalse(task.goalVisited(player2,room2));
        assertEquals(50,task.percentComplete(player1));
        assertEquals(0,task.percentComplete(player2));
        assertEquals(50,task.overallPercentComplete());
        
        test.movePlayer(player2, Exit.WEST);
        assertFalse(task.goalVisited(player1,room1));
        assertTrue(task.goalVisited(player2,room1));
        assertEquals(50,task.percentComplete(player1));
        assertEquals(50,task.percentComplete(player2));
        // At least one person has to visit all the goal nodes.
        assertEquals(50,task.overallPercentComplete());
    }
}
