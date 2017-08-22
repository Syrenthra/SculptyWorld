package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.environment.CreatureResource;
import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.TheWorld;
import sw.environment.Zone;
import sw.item.Item;
import sw.item.Weapon;
import sw.lifeform.Creature;

public class TestCreature
{
    @Before
    public void before()
    {
        TheWorld.getInstance(false);
    }
    
    @After
    public void after()
    {
        TheWorld.reset();
    }

    @Test
    public void testInitialization()
    {
        Creature dude = new Creature(1, "Dude", "Desc", 50, 10, 5, 15);
        assertEquals("Dude", dude.getName());
        assertEquals("Desc", dude.getDescription());
        assertEquals(50,dude.getCurrentLifePoints());
        assertEquals(10, dude.getDamage());
        assertEquals(5, dude.getArmor());
        assertEquals(15, dude.getSpeed());
        assertNull(dude.getResource());
        assertEquals(0,dude.getNumValidZones());
    }

    @Test
    public void testCanCarryResource()
    {
        Creature dude = new Creature(1, "Dude", "Desc", 50, 10, 5, 15);
        CreatureResource resource = new CreatureResource(dude, 10, 3, 4);
        dude.setResource(resource);
        assertEquals(resource, dude.getResource());
    }

    @Test
    public void testTakeHit()
    {
        Creature dude2 = new Creature(1, "Dude", "Desc", 100, 10, 5, 15);

        dude2.takeHit(10);
        assertEquals(95, dude2.getCurrentLifePoints());
    }

    @Test
    public void testAttack()
    {
        Creature dude1 = new Creature(1, "Dude", "Desc", 100, 10, 5, 15);
        Creature dude2 = new Creature(1, "Dude", "Desc", 100, 10, 5, 15);

        dude1.attack(dude2);
        assertEquals(95, dude2.getCurrentLifePoints());
    }

    @Test
    public void testIsSame()
    {
        Creature dude1 = new Creature(1, "Dude1", "Desc", 100, 10, 5, 15);
        Creature dude2 = new Creature(2, "Dude2", "Desc", 100, 10, 5, 15);
        Creature dude3 = dude1.clone();
        assertTrue(dude1.equals(dude3));
        assertFalse(dude1.equals(dude2));
    }

    @Test
    public void testNoNullPointerWhenNotInRoom()
    {
        Creature dude = new Creature(1, "Dude1", "Desc", 100, 10, 5, 15);
        CreatureResource resource = new CreatureResource(dude, 10, 3, 4);
        resource.setAmount(5);
        dude.setResource(resource);

        dude.updateTime("Test", 3);
    }

    @Test
    public void testWillWanderIfCanPlaceResource()
    {
        Creature dude = new Creature(1, "Dude1", "Desc", 100, 10, 5, 15);
        CreatureResource resource = new CreatureResource(dude, 10, 3, 4);
        resource.setAmount(5);
        dude.setResource(resource);
        dude.addZone(Zone.FOREST);

        TheWorld world = TheWorld.getInstance(false);
        
        Room room1 = new Room(1, "Mountain 1", "This is a small mountain.");
        room1.setZone(Zone.FOREST);
        Room room2 = new Room(2, "Mountain 2", "This is a tall mountain.");
        room2.setZone(Zone.FOREST);
        Room room3 = new Room(3, "Forest 1", "This is a small forest.");
        room3.setZone(Zone.FOREST);

        world.addRoom(room1);
        world.addRoom(room2);
        world.addRoom(room3);
        
        // Attach the rooms together.
        room1.addExit(room2, Exit.EAST);

        room2.addExit(room1, Exit.WEST);
        room2.addExit(room3, Exit.EAST);

        room3.addExit(room2, Exit.WEST);
 
        room1.addCreature(dude);
        CreatureResource resource2 = new CreatureResource(dude, 10, 3, 4);
        resource2.setAmount(5);
        room1.addCreatureResource(resource2); // Force the creature to wander.

        dude.updateTime(TheWorld.MOVE_TIMER, 1);

        assertEquals(room2, dude.getCurrentRoom());
        assertNull(room2.getCreatureResource(0));

        dude.updateTime(TheWorld.MOVE_TIMER, 2);
        assertEquals(room2, dude.getCurrentRoom());
        assertEquals(resource, room2.getCreatureResource(0));
        assertNull(dude.getResource());

        room2.removeCreature(dude.getID());
        room1.removeCreatureResource(0);

        assertEquals(0, room2.getNumCreatures());
        
        room2.updateTime(TheWorld.ROOM_TIMER, 1);
        
        assertTrue(resource.containsObserver(room2));

        resource.spawn();

        assertEquals(1, room2.getNumCreatures());
    }

    @Test
    public void testEqualChangeToGoWestOrEast()
    {
        int roomCount = 0;
        for (int x = 0; x < 1000; x++)
        {
            TheWorld.reset();
            Creature dude = new Creature(1, "Dude1", "Desc", 100, 10, 5, 15);
            CreatureResource resource = new CreatureResource(dude, 10, 100000, 100000);
            resource.setAmount(5);
            dude.setResource(resource);

            TheWorld world = TheWorld.getInstance(false);
            Room room1 = new Room(1, "Mountain 1", "This is a small mountain.");
            Room room2 = new Room(2, "Mountain 2", "This is a tall mountain.");
            Room room3 = new Room(3, "Forest 1", "This is a small forest.");

            // Attach the rooms together.
            room1.addExit(room2, Exit.EAST);

            room2.addExit(room1, Exit.WEST);
            room2.addExit(room3, Exit.EAST);

            room3.addExit(room2, Exit.WEST);
            
            world.addRoom(room1);
            world.addRoom(room2);
            world.addRoom(room3);

            world.addCreature(dude);
            room2.addCreature(dude);
            
            CreatureResource resource2 = new CreatureResource(dude, 10, 100000, 100000);
            room2.addCreatureResource(resource2); // Force the creature to wander.
            
            dude.updateTime("clock", 1);
            
            if (dude.getCurrentRoom() == room1)
                roomCount++;
            if (dude.getCurrentRoom() == room3)
                roomCount--;
        }
        assertTrue(""+roomCount,roomCount > -50);
        assertTrue(""+roomCount,roomCount < 50);
    }
    
    @Test
    public void testCanAddValidZones()
    {
        Creature dude = new Creature(1, "Dude", "Desc", 50, 10, 5, 15);
        dude.addZone(Zone.BEACH);
        assertEquals(1,dude.getNumValidZones());
        dude.addZone(Zone.DESERT);
        assertEquals(2,dude.getNumValidZones());
        assertTrue(dude.canTravel(Zone.DESERT));
        assertTrue(dude.canTravel(Zone.BEACH));
        assertFalse(dude.canTravel(Zone.CITY));
        
        dude.removeZone(Zone.BEACH);
        assertEquals(1,dude.getNumValidZones());
        assertTrue(dude.canTravel(Zone.DESERT));
        assertFalse(dude.canTravel(Zone.BEACH));
    }
    
    @Test
    public void testGetCreatureInfo()
    {
        Creature dude = new Creature(1, "Dude", "Desc", 50, 10, 5, 15);
        dude.addZone(Zone.BEACH);
        dude.addZone(Zone.DESERT);
        CreatureResource resource = new CreatureResource(dude, 10, 100000, 100000);
        resource.setAmount(5);
        dude.setResource(resource);
        Hashtable<String,Object> data = dude.getLifeformInfo();
        assertEquals(1,data.get(Item.ID));
        assertEquals("Dude",data.get(Lifeform.NAME));
        assertEquals("Desc",data.get(Lifeform.DESC));
        assertEquals(50,data.get(Lifeform.MAX_LIFE));
        assertEquals(50,data.get(Lifeform.CURRENT_LIFE));
        assertEquals(-1,data.get(Lifeform.CURRENT_ROOM));
        assertEquals(10,data.get(Creature.DMG));
        assertEquals(5,data.get(Creature.ARMOR));
        assertEquals(15,data.get(Creature.SPEED));
        Vector<String> zones = (Vector)data.get(Creature.ZONES);
        assertEquals(2,zones.size());
        assertEquals("BEACH",zones.elementAt(0));
        assertEquals("DESERT",zones.elementAt(1));
    }
    
    /**
     * We need to fix how we store enum values into the database.
     */
    @Test
    public void testConstructCreature()
    {
        Creature dude = new Creature(1, "Dude", "Desc", 50, 10, 5, 15);
        dude.addZone(Zone.BEACH);
        dude.addZone(Zone.DESERT);
        Hashtable<String,Object> data = dude.getLifeformInfo();
        Creature newDude = Creature.constructCreature(data);
        assertEquals("Dude", newDude.getName());
        assertEquals("Desc", newDude.getDescription());
        assertEquals(50,newDude.getCurrentLifePoints());
        assertEquals(10, newDude.getDamage());
        assertEquals(5, newDude.getArmor());
        assertEquals(15, newDude.getSpeed());
        assertNull(newDude.getResource());
        
        assertEquals(2,newDude.getNumValidZones());
        assertTrue(newDude.canTravel(Zone.DESERT));
        assertTrue(newDude.canTravel(Zone.BEACH));
    }
    
    @Test
    public void testGetCreatureInfoWithCreatureResourceAndZones()
    {
        assertTrue(false);
    }

}
