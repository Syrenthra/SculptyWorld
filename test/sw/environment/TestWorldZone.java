package sw.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;


public class TestWorldZone
{
    
    @Test
    public void testAddAndRemoveRoomsFromZone()
    {
        WorldZone zone = new WorldZone();
        
        Room room1 = new Room(1, "Mountain 1","This is a small mountain.");
        room1.setZone(Zone.MOUNTAIN);
        Room room3 = new Room(3, "Forest 1","This is a small forest.");
        room3.setZone(Zone.FOREST);
        
        assertTrue(zone.addRoom(room1));
        assertTrue(zone.containsRoom(room1.getID()));
        
        // Should not be able to add the Forest room to the same zone as a one that contains Mountains.
        assertFalse(zone.addRoom(room3));
        assertFalse(zone.containsRoom(room3.getID()));
        
        zone.removeRoom(room1.getID());
        assertFalse(zone.containsRoom(room1.getID()));
        
        // Now should be able to add room3 to an empty zone.
        assertTrue(zone.addRoom(room3));
        assertTrue(zone.containsRoom(room3.getID()));
    }
    
    @Test
    public void testCanConnectandDisconnectZonesTogether()
    {
        WorldZone zone1 = new WorldZone();
        WorldZone zone2 = new WorldZone();
        WorldZone zone3 = new WorldZone();
        
        Room room1 = new Room(1, "Mountain 1","This is a small mountain.");
        room1.setZone(Zone.MOUNTAIN);
        Room room3 = new Room(3, "Forest 1","This is a small forest.");
        room3.setZone(Zone.FOREST);
        Room room7 = new Room(7, "Desert 2","This is a cold desert.");
        room7.setZone(Zone.DESERT);
        
        zone1.addRoom(room1);
        zone2.addRoom(room3);
        zone3.addRoom(room7);
        
        zone1.connectsTo(zone2);
        zone2.connectsTo(zone1);
        zone2.connectsTo(zone3);
        zone3.connectsTo(zone2);
        
        ArrayList<WorldZone> zones = zone1.getNeighboringZones();
        assertEquals(1,zones.size());
        assertEquals(zone2,zones.get(0));
        
        zones = zone2.getNeighboringZones();
        assertEquals(2,zones.size());
        
        zones = zone3.getNeighboringZones();
        assertEquals(1,zones.size());
        assertEquals(zone2,zones.get(0)); 
        
        zone1.removeConnection(zone2);
        
        zones = zone1.getNeighboringZones();
        assertEquals(0,zones.size());
        
        zones = zone2.getNeighboringZones();
        assertEquals(2,zones.size());
    }
    
    @Test
    public void testMergeZone()
    {
        WorldZone zone1 = new WorldZone();
        WorldZone zone2 = new WorldZone();
        WorldZone zone3 = new WorldZone();
        
        Room room1 = new Room(1, "Mountain 1","This is a small mountain.");
        room1.setZone(Zone.MOUNTAIN);
        Room room3 = new Room(3, "Forest 1","This is a small forest.");
        room3.setZone(Zone.MOUNTAIN);
        Room room7 = new Room(7, "Desert 2","This is a cold desert.");
        room7.setZone(Zone.MOUNTAIN);
        
        zone1.addRoom(room1);
        zone2.addRoom(room3);
        zone3.addRoom(room7);
        
        zone1.connectsTo(zone2);
        zone2.connectsTo(zone1);
        zone2.connectsTo(zone3);
        zone3.connectsTo(zone2);
        
        zone1.mergeZone(zone2);
        
        ArrayList<WorldZone> zones = zone1.getNeighboringZones();
        assertEquals(1,zones.size());
        assertEquals(zone3,zones.get(0));
        assertTrue(zone1.containsRoom(3));
        assertTrue(zone1.containsRoom(1));
        
    }
    
    @Test
    public void testMergeZone2()
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
        
        WorldZone zone1 = new WorldZone();
        zone1.addRoom(room6);
        zone1.addRoom(room7);
        WorldZone zone2 = new WorldZone();
        zone2.addRoom(room5);
        zone2.addRoom(room4);
        zone2.addRoom(room3);
        WorldZone zone3 = new WorldZone();
        zone3.addRoom(room2);
        WorldZone zone4 = new WorldZone();
        zone4.addRoom(room1);
        
        zone1.connectsTo(zone2);
        zone2.connectsTo(zone1);
        zone2.connectsTo(zone3);
        zone3.connectsTo(zone2);
        
        zone4.mergeZone(zone3);
        
        assertEquals(2,zone2.getNeighboringZones().size());
        assertEquals(1,zone4.getNeighboringZones().size());
        
    }

}
