package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import mock.MockItem;

import org.junit.Test;

import sw.environment.Room;
import sw.environment.CreatureResource;
import sw.environment.Zone;

import sw.item.Item;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.quest.task.DeliverItemTask;
import sw.quest.task.KillCreatureTask;


public class TestNPC_OLD
{
    

    
    /**
     * 
     */
    @Test
    public void testOutsideRoomHavingCorrectCreatureResourceTriggersNPCQuestToActive()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        creature.addZone(Zone.CITY);
        CreatureResource myRes = new CreatureResource(creature,10,3,4);
        myRes.setAmount(5);
        KillCreatureTask quest = new KillCreatureTask("Name","Desc",creature,5);
        NPC dude1 = new NPC(1,"Dude", "Desc",100,10,5,15);
        dude1.addAssignableQuest(quest);
        Room room = new Room(1, "Tree","Forest");
        room.setZone(Zone.CITY);
        
     // Creature not in the room, and no resource
        room.addRoomObserver(dude1);
        assertFalse(dude1.isQuestActive());
        
     // Creature in the room, but no resource
        room.addCreature(creature.clone());    
        assertTrue(dude1.isQuestActive());
        
      // No creature in room, but has resource
        room.addCreatureResource(myRes);
        room.removeCreature(room.getCreatures()[0].getID());
        assertFalse(dude1.isQuestActive());
        
      // Creature in the room and has resource
        room.addCreature(creature.clone());
        assertTrue(dude1.isQuestActive());
        
      // Resource removed, room has creature
        room.removeCreatureResource(0);
        assertTrue(dude1.isQuestActive());
    }
    
    /**
     * Not sure what to test for here yet.
     */
    @Test
    public void testOutsideRoomHavingWrongCreatureResourceDoesntTriggersNPCQuestToActive()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        creature.addZone(Zone.CITY);
        Creature wrongCreature = new Creature(2,"Wrong", "Desc",50,10,5,15);
        CreatureResource wrongResource = new CreatureResource(wrongCreature,10,3,4);
        KillCreatureTask quest = new KillCreatureTask("Name","Desc",creature,5);
        NPC dude1 = new NPC(1, "Dude", "Desc",100,10,5,15);
        dude1.addAssignableQuest(quest);
        Room room = new Room(1, "Tree","Forest");
        room.setZone(Zone.CITY);
        room.addCreatureResource(wrongResource);
        room.addRoomObserver(dude1);
        // Wrong resource in the room.
        assertFalse(dude1.isQuestActive());
        room.addCreature(creature.clone());
        // Right Creature, wrong resource in the room
        assertTrue(dude1.isQuestActive());
    }
    
    @Test
    public void canAssignQuest()
    {
        MockNPC dude = new MockNPC("Dude", "Desc",100,10,5,15);
        Creature creature = new Creature(1, "Dude", "Desc",50,10,5,15);
        KillCreatureTask quest = new KillCreatureTask("Name","Desc",creature,5);
        dude.addAssignableQuest(quest);
        PC player = new PC(1, "Dude","Desc",50);
        dude.setQuestIsActive(false);
        dude.assignQuest(player,0);
        assertNull(player.getNativeQuest(0));
        assertEquals(0,quest.getNumPlayers());
        assertFalse(quest.hasPlayer(player));
        
        // Set the quest as active
        dude.setQuestIsActive(true);
        dude.assignQuest(player,0);
        assertEquals(quest,player.getNativeQuest(0));
        assertEquals(1,quest.getNumPlayers());
        assertTrue(quest.hasPlayer(player));
        
    }
    
    @Test
    public void testCanObserveAndRespondCorrectlyForMultipleRooms()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        creature.addZone(Zone.CITY);
        KillCreatureTask quest = new KillCreatureTask("Name","Desc",creature,5);
        NPC npcDude = new NPC(1,"Dude", "Desc",100,10,5,15);
        npcDude.addAssignableQuest(quest);
        
        // Setup Room One

        Room roomOne = new Room(1, "Tree","Forest");
        roomOne.setZone(Zone.CITY);
        
        
        //Setup Room Two

        Room roomTwo = new Room(2, "Tree","Forest");
        roomTwo.setZone(Zone.CITY);
        
        roomOne.addRoomObserver(npcDude);
        roomTwo.addRoomObserver(npcDude);
        
        roomOne.addCreature(creature);
        roomTwo.addCreature(creature);
        
        
        // Test for both rooms with creatures.
        assertTrue(npcDude.isQuestActive());
        
        // Test for Room one w/o creature and Room two with creature.
        roomOne.removeCreature(1);
        assertTrue(npcDude.isQuestActive());
        
        // Test both Rooms with no creatures
        roomTwo.removeCreature(1);
        assertFalse(npcDude.isQuestActive());
    }


}


