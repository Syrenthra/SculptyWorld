package sw.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.Test;

import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.PC;


public class TestTheWorld
{
    @Test
    public void testInitialize()
    {
        TheWorld test = TheWorld.getInstance();
        
        assertNotNull(test);
    }
    
    @Test
    public void testAddAndRemovePlayer()
    {
        TheWorld test = TheWorld.getInstance();
        PC player = new PC(1,"Dude","Desc",50);
        test.addPlayer(player);
        Room room = new Room(1,"Tree","Forest");
        test.addRoom(room);
        room.addPC(player);
        assertEquals(player,test.getPlayer(player.getID()));
        test.removePlayer(player);
        assertNull(test.getPlayer(player.getID()));
        assertNull(room.getPC(player.getID()));
        
        test.removePlayer(player);
    }
    
    @Test
    public void testAddAndRemoveCreature()
    {
        TheWorld test = TheWorld.getInstance();
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        test.addCreature(creature);
        Room room = new Room(1,"Tree","Forest");
        test.addRoom(room);
        room.addCreature(creature);
        assertEquals(creature,test.getCreature(1));
        test.removeCreature(creature.getID());
        assertNull(test.getCreature(creature.getID()));
        assertNull(room.getCreature(creature.getID()));
        
        test.removeCreature(creature.getID());
    }
    
    @Test
    public void testAddAndRemoveNPC()
    {
        TheWorld test = TheWorld.getInstance();
        NPC npc = new NPC(1,"Dude", "Desc",50,10,5,15);
        test.addNPC(npc);
        Room room = new Room(1,"Tree","Forest");
        test.addRoom(room);
        room.addNPC(npc);
        assertEquals(npc,test.getNPC(1));
        test.removeNPC(npc.getID());
        assertNull(test.getNPC(npc.getID()));
        assertNull(room.getNPC(npc.getID()));
        
        test.removeNPC(npc.getID());
    }
    
    @Test
    public void testAddRoom()
    {
        TheWorld test = TheWorld.getInstance();
        Room room = new Room(1,"Tree","Forest");
        test.addRoom(room);
        assertEquals(room,test.getRoom(room.getID()));
    }
    
    @Test
    public void testPlayerCanDetermineRoomLocation()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        PC player = new PC(1,"Dude","Desc",50);
        test.addPlayer(player);
        
        assertNull(player.getCurrentRoom());
        
        room1.addPC(player);
        
        assertEquals(room1,player.getCurrentRoom());
    }
    
    @Test
    public void testPlayerMoveToNewRoom()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        PC player = new PC(1,"Dude","Desc",50);
        test.addPlayer(player);
        // Null test for player moving who is not in a room.
        assertFalse(test.movePlayer(player, Exit.EAST));
        assertNull(player.getCurrentRoom());
        
        // Base test for valid move.
        room1.addPC(player);
        assertTrue(test.movePlayer(player, Exit.EAST));
        assertEquals(room2,player.getCurrentRoom());
        
        // Base test for invalid move.
        assertFalse(test.movePlayer(player, Exit.WEST));
        assertEquals(room2,player.getCurrentRoom());
    }
    
    @Test
    public void testNPCMoveToNewRoom()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        PC player = new PC(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPC(player);
        test.movePlayer(player, Exit.EAST);
        assertEquals(room2,player.getCurrentRoom());
    }
    
    @Test
    public void testCreatureMoveToNewRoom()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        PC player = new PC(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPC(player);
        test.movePlayer(player, Exit.EAST);
        assertEquals(room2,player.getCurrentRoom());
    }
    
    @Test
    public void testCreatureResourcesGetUpdates()
    {
        TheWorld world = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        world.addRoom(room1);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        // We don't want any uncontrolled spawning or recovering.
        CreatureResource myRes = new CreatureResource(creature,10,100000,100000);
        myRes.setAmount(1);
        room1.addCreatureResource(myRes);
        myRes.addSpawnObserver(room1);
        assertTrue(world.m_spawnTimer.contains(myRes));
        
        // Test Timer removed on update.
        myRes.spawn();
        assertFalse(world.m_spawnTimer.contains(myRes));
    }
    

    @Test
    public void testBuildZoneGraph()
    {
        TheWorld world = TheWorld.getInstance();
        Room room1 = new Room(1, "Mountain 1","This is a small mountain.");
        room1.setZone(Zone.MOUNTAIN);
        Room room2 = new Room(2, "Mountain 2","This is a tall mountain.");
        room2.setZone(Zone.MOUNTAIN);
        Room room3 = new Room(3, "Forest 1","This is a small forest.");
        room3.setZone(Zone.FOREST);
        Room room4 = new Room(4, "Forest 2","This is a big forest.");
        room4.setZone(Zone.FOREST);
        Room room5 = new Room(5, "Forest 3","This is a medium forest.");
        room5.setZone(Zone.FOREST);
        Room room6 = new Room(6, "Desert 1","This is a hot desert.");
        room6.setZone(Zone.DESERT);
        Room room7 = new Room(7, "Desert 2","This is a cold desert.");
        room7.setZone(Zone.DESERT);
        
        // Dump all this stuff into the world
        world.addRoom(room1);
        world.addRoom(room2);
        world.addRoom(room3);
        world.addRoom(room4);
        world.addRoom(room5);
        world.addRoom(room6);
        world.addRoom(room7);
        
     // Attach the rooms together.
        room1.addExit(room2, Exit.EAST);
        
        room2.addExit(room1, Exit.WEST);
        room2.addExit(room3, Exit.EAST);
        
        room3.addExit(room2, Exit.WEST);
        room3.addExit(room4,Exit.EAST);
        
        room4.addExit(room3, Exit.WEST);
        room4.addExit(room5,Exit.EAST);
        
        room5.addExit(room4, Exit.WEST);
        room5.addExit(room6,Exit.EAST);
        
        room6.addExit(room5, Exit.WEST);
        room6.addExit(room7,Exit.EAST);
        
        room7.addExit(room6, Exit.WEST);
        
        TheWorld.getInstance().constructZoneGraph();
        
        WorldZone zone1 = TheWorld.getInstance().getZone(room1);
        assertTrue(zone1.containsRoom(1));
        assertTrue(zone1.containsRoom(2));
        
        ArrayList<WorldZone> zones = TheWorld.getInstance().getNeighboringZones(room3);
        assertEquals(2,zones.size());
        
        assertEquals(7,TheWorld.getInstance().getSize());
    }

}
