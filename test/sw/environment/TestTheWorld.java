package sw.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.Player;


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
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        Room room = new Room(1,"Tree","Forest");
        test.addRoom(room);
        room.addPlayer(player);
        assertEquals(player,test.getPlayer(player.getID()));
        test.removePlayer(player);
        assertNull(test.getPlayer(player.getID()));
        assertNull(room.getPlayer(player.getID()));
        
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
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        
        assertNull(player.getCurrentRoom());
        
        room1.addPlayer(player);
        
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
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        // Null test for player moving who is not in a room.
        test.movePlayer(player, Exit.EAST);
        assertNull(player.getCurrentRoom());
        
        // Base test for valid move.
        room1.addPlayer(player);
        test.movePlayer(player, Exit.EAST);
        assertEquals(room2,player.getCurrentRoom());
        
        // Base test for invalid move.
        test.movePlayer(player, Exit.WEST);
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
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPlayer(player);
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
        Player player = new Player(1,"Dude","Desc",50);
        test.addPlayer(player);
        room1.addPlayer(player);
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

}
