package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.environment.Room;
import sw.environment.CreatureResource;
import sw.environment.Zone;

import sw.item.Item;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.Player;
import sw.quest.CreatureQuest;
import sw.quest.ItemQuest;


public class TestNPC
{
    
    @Test
    public void testInitialization()
    {
        NPC dude = new NPC(1,"Dude", "Desc",50,10,5,15);
        assertEquals("Dude",dude.getName());
        assertEquals("Desc",dude.getDescription());
        assertEquals(10,dude.getDamage());
        assertEquals(5,dude.getArmor());
        assertEquals(15,dude.getSpeed()); 
    }
    
    @Test
    public void testAssignCreatureQuest()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        NPC dude = new NPC(1,"Dude", "Desc",50,10,5,15);
        dude.addAssignableQuest(quest);
        assertEquals(quest,dude.getQuest());
    }
    
    @Test
    public void testAssignItemQuest()
    {
        Item item = new MockItem("Test","Test Item",5,10);
        ItemQuest quest = new ItemQuest("Name","Desc",item,5);
        NPC dude = new NPC(1,"Dude", "Desc",50,10,5,15);
        dude.addAssignableQuest(quest);
        assertEquals(quest,dude.getQuest());
    }
    
    @Test
    public void testTakeHit()
    {
        NPC dude2 = new NPC(1,"Dude", "Desc",100,10,5,15);
        
        dude2.takeHit(10);
        assertEquals(95,dude2.getCurrentLifePoints()); 
    }
    
    @Test
    public void testAttack()
    {
        NPC dude1 = new NPC(1,"Dude", "Desc",100,10,5,15);
        NPC dude2 = new NPC(2,"Dude", "Desc",100,10,5,15);
        
        dude1.attack(dude2);
        assertEquals(95,dude2.getCurrentLifePoints());
    }
    
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
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
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
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
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
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        dude.addAssignableQuest(quest);
        Player player = new Player(1, "Dude","Desc",50);
        dude.setQuestIsActive(false);
        dude.assignQuest(player,0);
        assertNull(player.getQuest(0));
        assertEquals(0,quest.getNumPlayers());
        assertFalse(quest.hasPlayer(player));
        
        // Set the quest as active
        dude.setQuestIsActive(true);
        dude.assignQuest(player,0);
        assertEquals(quest,player.getQuest(0));
        assertEquals(1,quest.getNumPlayers());
        assertTrue(quest.hasPlayer(player));
        
    }
    
    @Test
    public void testCanObserveAndRespondCorrectlyForMultipleRooms()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        creature.addZone(Zone.CITY);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
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

/**
 * Used to access parts of NPC without making everything public
 * @author cdgira
 *
 */
class MockNPC extends NPC
{
    public MockNPC(String name, String desc, int life, int damage, int armor, int speed)
    {
        super(1,name, desc, life, damage, armor, speed);
    }

    /**
     * Force sets whether a quest is active or not regardless
     * of the state of any rooms being observed.
     * @param value
     */
    public void setQuestIsActive(boolean value)
    {
        m_questActive = value;
    }
}

class MockItem extends Item
{
    public MockItem(String name, String desc,int size, int weight)
    {
        super(name,desc,size,weight);
    }
}
