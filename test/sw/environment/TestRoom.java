package sw.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.item.Item;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.Player;

import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.Zone;


public class TestRoom
{
    @Before
    public void before()
    {
        TheWorld.getInstance(false);
    }
    
    /**
     * Reset the world so it will be back in default mode for other tests.
     */
    @After
    public void after()
    {
        TheWorld.reset();
    }
    
    @Test
    public void testInitialization()
    {
        Room testRoom = new Room(1, "Test Room","This is a room.");
        
        assertEquals("This is a room.",testRoom.getDescription());
        assertEquals("Test Room",testRoom.getTitle());
        assertEquals(Zone.CITY,testRoom.getZone());
        assertEquals(1,testRoom.getID());
        assertEquals(0,testRoom.getNumNPCs());
        assertEquals(0,testRoom.getNumCreatures());
    }
    
    @Test
    public void testSetExits()
    {
        Room testRoom1 = new Room(1,"Test1","Desc1");
        Room testRoom2 = new Room(2,"Test2","Desc2");
        
        testRoom1.addExit(testRoom2,Exit.EAST);
        assertEquals(testRoom2,testRoom1.getExit(Exit.EAST));
    }
    
    @Test
    public void testKeepsItems()
    {
        Room testRoom1 = new Room(1,"Test1","Desc1");
        MockItem item = new MockItem("Item", "Desc",5,6);
        
        testRoom1.addItem(item);
        assertEquals(item,testRoom1.getItem(0));
    }
    
    @Test
    public void testKeepAndRemovePlayers()
    {
        Room testRoom1 = new Room(IdGen.getID(),"Test1","Desc1");
        Player dude = new Player(IdGen.getID(),"Dude", "Desc",10);
        testRoom1.addPlayer(dude);
        assertEquals(dude,testRoom1.getPlayer(dude.getID()));
        
        assertTrue(testRoom1.hasPlayer(dude.getID()));
        assertEquals(testRoom1,dude.getCurrentRoom());
        
        testRoom1.removePlayer(dude.getID());
        
        assertFalse(testRoom1.hasPlayer(dude.getID()));
        assertNull(dude.getCurrentRoom());
        
        testRoom1.removePlayer(dude.getID());
    }
    
    @Test
    public void testKeepAndRemoveCreatures()
    {
        Room testRoom1 = new Room(1,"Test1","Desc1");
        testRoom1.setZone(Zone.CITY);
        Creature dude = new Creature(1,"Dude", "Desc",50,10,4,13);
        dude.addZone(Zone.CITY);
        testRoom1.addCreature(dude);
        assertEquals(dude,testRoom1.getCreature(dude.getID()));
        assertEquals(testRoom1,dude.getCurrentRoom());
        
        assertEquals(dude,testRoom1.removeCreature(dude.getID()));
        assertNull(testRoom1.getCreature(dude.getID()));
        assertNull(dude.getCurrentRoom());
        
        assertNull(testRoom1.removeCreature(dude.getID()));
    }
    
    @Test
    public void testKeepAndRemoveNPCs()
    {
        Room testRoom1 = new Room(1,"Test1","Desc1");
        NPC dude = new NPC(1,"Dude", "Desc",50,10,4,13);
        testRoom1.addNPC(dude);
        assertEquals(1,testRoom1.getNumNPCs());
        assertEquals(dude,testRoom1.getNPC(dude.getID()));
        assertEquals(testRoom1,dude.getCurrentRoom());
        
        assertEquals(dude,testRoom1.removeNPC(dude.getID()));
        assertNull(testRoom1.getCreature(dude.getID()));
        assertNull(dude.getCurrentRoom());
        
        assertNull(testRoom1.removeCreature(dude.getID()));
    }
    
    @Test
    public void testSetZone()
    {
        Room testRoom = new Room(1,"Test1","Desc1");
        testRoom.setZone(Zone.BEACH);
        assertEquals(Zone.BEACH,testRoom.getZone());
    }
    
    @Test
    public void testWithResources()
    {
        Creature dude = new Creature(1,"Dude","Some Dude",100,10,5,2);
        Creature dude2 = new Creature(2, "Other Dude","Some Dude",100,10,5,2);
        CreatureResource myRes = new CreatureResource(dude,10,3,4);
        CreatureResource myRes2 = new CreatureResource(dude2,10,3,4);
        Room room = new Room(1, "Tree","Forest");
        room.addCreatureResource(myRes);
        assertEquals(myRes,room.getCreatureResource(0));
        room.addCreatureResource(myRes2);
        assertEquals(myRes2,room.getCreatureResource(1));
        room.removeCreatureResource(0);
        assertEquals(myRes2,room.getCreatureResource(0));
    }
    
    @Test
    public void testProcessesSpawnUpdates()
    {
        Creature dude = new Creature(IdGen.getID(),"Dude","Some Dude",100,10,5,2);
        dude.addZone(Zone.CITY);
        CreatureResource myRes = new CreatureResource(dude,10,3,4);
        myRes.setAmount(5);
        Room room = new Room(IdGen.getID(), "Tree","Forest");
        room.setZone(Zone.CITY);
        room.addCreatureResource(myRes);
        assertEquals(0,room.getNumCreatures());
        myRes.addSpawnObserver(room);
        myRes.spawn();
        assertEquals(1,room.getNumCreatures());
        // Room should have stopped listening.
        myRes.spawn();
        assertEquals(4,myRes.getAmount());
        assertEquals(1,room.getNumCreatures());
        
        // Now remove the creature and see if the spawn observer is reattached.
        Creature[] creatures = room.getCreatures();
        for (int x=0;x<creatures.length;x++)
            room.removeCreature(creatures[x].getID());
        room.updateTime(TheWorld.ROOM_TIMER ,1);
        myRes.spawn();
        assertEquals(1,room.getNumCreatures());
        assertEquals(3,myRes.getAmount());
    }
    
    @Test
    public void testRoomInformsObserversOfChanges()
    {
        Creature dude = new Creature(IdGen.getID(),"Dude","Some Dude",100,10,5,2);
        dude.addZone(Zone.CITY);
        CreatureResource myRes = new CreatureResource(dude,10,3,4);
        myRes.setAmount(5);
        Room room = new Room(IdGen.getID(), "Tree","Forest");
        room.setZone(Zone.CITY);
        MockRoomRemovesObserver observer1 = new MockRoomRemovesObserver(room);
        room.addRoomObserver(observer1);
        room.addCreatureResource(myRes);
        assertEquals(0,room.getNumCreatures());
        myRes.addSpawnObserver(room);
        myRes.spawn();
        assertEquals(1,room.getNumCreatures());
        assertEquals(room,observer1.myRoom);
        
        // Should not get another creature.
        myRes.spawn();
        assertEquals(1,room.getNumCreatures());
    }
    
    /**
     * Want to make sure that the removeSpawnListener doesn't cause the 
     * addSpawnListener to fail.
     */
    @Test
    public void testHoldsUpWithMulipleListeners()
    {
        Room room = new Room(1, "Tree","Forest");
        MockRoomRemovesObserver observer1 = new MockRoomRemovesObserver(room);
        MockRoomRemovesObserver observer2 = new MockRoomRemovesObserver(room);
        room.addRoomObserver(observer1);
        room.addRoomObserver(observer2);
        assertNull(observer1.myRoom);
        assertNull(observer2.myRoom);
        room.informObservers(null,SWRoomUpdateType.CREATURE_ADDED);
        assertEquals(room,observer1.myRoom);
        assertEquals(room,observer2.myRoom);
    }
    
    @Test
    public void testCreatureResourceRemovedWhenReachesZero()
    {
        Creature dude = new Creature(IdGen.getID(),"Dude","Some Dude",100,10,5,2);
        dude.addZone(Zone.CITY);
        CreatureResource myRes = new CreatureResource(dude,10,3,4);
        myRes.setAmount(2);
        Room room = new Room(IdGen.getID(), "Tree","Forest");
        room.setZone(Zone.CITY);
        room.addCreatureResource(myRes);
        myRes.addSpawnObserver(room);
        myRes.spawn();
        assertEquals(1,room.getCreatureResources().size());
        room.removeCreature(room.getCreatures()[0].getID());
        room.updateTime(TheWorld.ROOM_TIMER, 3);
        myRes.spawn();
        assertEquals(0,myRes.getAmount());
        assertEquals(0,room.getCreatureResources().size());
    }
    
    @Test
    public void testThreeConnectedRoomsWithCreatureSpawners()
    {   
        TheWorld.reset();  // In case a test forgot to do this.
        // We need to turn off all the normal timers for testing reasons.
        TheWorld.getInstance(false);
        
        Room fredRoom = new Room(IdGen.getID(), "Fred Room","Forest");
        fredRoom.setZone(Zone.CITY);
        TheWorld.getInstance().addRoom(fredRoom);
        
        Room bobRoom = new Room(IdGen.getID(), "Bob Room","Forest");
        fredRoom.setZone(Zone.CITY);
        TheWorld.getInstance().addRoom(bobRoom);
        
        Room garyRoom = new Room(IdGen.getID(), "Gary Room","Forest");
        fredRoom.setZone(Zone.CITY);
        TheWorld.getInstance().addRoom(garyRoom);
        
        bobRoom.addExit(garyRoom, Exit.WEST);
        bobRoom.addExit(fredRoom, Exit.EAST);
        fredRoom.addExit(bobRoom, Exit.WEST);
        fredRoom.addExit(garyRoom, Exit.EAST);
        garyRoom.addExit(fredRoom, Exit.WEST);
        garyRoom.addExit(bobRoom, Exit.EAST);
        
        Creature fred = new Creature(IdGen.getID(),"Fred","Some Dude",100,10,1,1);
        fred.addZone(Zone.CITY);
        CreatureResource fredRes = new CreatureResource(fred,10,1,1);
        fredRes.setSpecialCreatureRate(1);
        fredRes.setAmount(10);
        
        fredRoom.addCreatureResource(fredRes);
        fredRes.addSpawnObserver(fredRoom);
        
        Creature bob = new Creature(IdGen.getID(),"Bob","Some Dude",100,10,1,1);
        bob.addZone(Zone.CITY);
        CreatureResource bobRes = new CreatureResource(bob,10,1,1);
        bobRes.setSpecialCreatureRate(1);
        bobRes.setAmount(10);
        
        bobRoom.addCreatureResource(bobRes);
        bobRes.addSpawnObserver(bobRoom);
        
        // Have time run for a bit
        for (int x=0;x<5000;x++)
        {
            TheWorld.getInstance().m_moveTimer.timeChanged();
            TheWorld.getInstance().m_combatTimer.timeChanged();
            TheWorld.getInstance().m_spawnTimer.timeChanged();
            TheWorld.getInstance().m_roomTimer.timeChanged();
        }
        
        int totalCreatures = TheWorld.getInstance().getNumCreatures();
        
        assertEquals(2,fredRoom.getCreatureResources().size());
        assertEquals(2,bobRoom.getCreatureResources().size());
        assertEquals(2,garyRoom.getCreatureResources().size());
        
        for (int x=0;x<200;x++)
        {
            TheWorld.getInstance().m_moveTimer.timeChanged();
            TheWorld.getInstance().m_combatTimer.timeChanged();
            TheWorld.getInstance().m_spawnTimer.timeChanged();
            TheWorld.getInstance().m_roomTimer.timeChanged();
        }
        
        // Still a random chance 1 new creature might get created.
        assertTrue(Math.abs(totalCreatures-TheWorld.getInstance().getNumCreatures()) < 2);
    }
    
    @Test
    public void testOnlyOneCreatureShouldPlaceItsResourceAndWontCrashOnRoomWithNoExits()
    {
        TheWorld.reset();
        TheWorld.getInstance(false);
        
        // Add rooms
        Room room = new Room(IdGen.getID(), "Room","Forest");
        room.setZone(Zone.CITY);
        TheWorld.getInstance().addRoom(room);
        
        // Add two of creatures with resources to place
        for (int x=0;x<2;x++)
        {
            Creature fred = new Creature(IdGen.getID(),"Fred","Some Dude"+x,100,10,5,2);
            fred.addZone(Zone.CITY);
            CreatureResource fredRes = new CreatureResource(fred,10,3,4);
            fredRes.setSpecialCreatureRate(1);
            fredRes.setAmount(10);
            fred.setResource(fredRes);
            room.addCreature(fred);
        }
        
        for (int x=0;x<10;x++)
        {
            TheWorld.getInstance().m_moveTimer.timeChanged();
            TheWorld.getInstance().m_combatTimer.timeChanged();
            TheWorld.getInstance().m_spawnTimer.timeChanged();
            TheWorld.getInstance().m_roomTimer.timeChanged();
        }
        
        assertEquals(2,room.getNumCreatures());
        assertEquals(1,room.getCreatureResources().size());
    }
    
    @Test
    public void testRaceConditionForMovingCreaturesAround()
    {
        TheWorld.reset();
        TheWorld.getInstance(false);
        
        // Add rooms
        Room[] rooms = new Room[3];
        for (int x=0;x<rooms.length;x++)
        {
            rooms[x] = new Room(IdGen.getID(), "Room"+x,"Forest"+x);
            TheWorld.getInstance().addRoom(rooms[x]);
        }
        
        for (int x=0;x<rooms.length;x++)
        {
            int dest1 = (x+1)%rooms.length;
            int dest2 = (x-1)%rooms.length;
            rooms[x].addExit(rooms[dest1], Exit.EAST);
            if (dest2 > 0)
                rooms[x].addExit(rooms[dest2], Exit.WEST);
        }
        
        // Add lots of creatures that move
        for (int x=0;x<1000;x++)
        {
            int room = (int)(Math.random()*rooms.length);
            Creature fred = new Creature(IdGen.getID(),"Fred","Some Dude"+x,100,10,5,2);
            CreatureResource fredRes = new CreatureResource(fred,10,3,4);
            fredRes.setSpecialCreatureRate(1);
            fredRes.setAmount(10);
            fred.setResource(fredRes);
            rooms[room].addCreature(fred);
        }
        
        ThreadForRaceTest test = new ThreadForRaceTest();
        test.start();
        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals("yes",test.done);
        
    }
    
    @Test
    public void testRemovesAllCreatureResourcesAtZero()
    {
        TheWorld.reset();
        TheWorld.getInstance(false);
        
        // Add rooms
        Room room = new Room(IdGen.getID(), "Room","Forest");
        TheWorld.getInstance().addRoom(room);
        
        // Add lots of creatures that move
        for (int x=0;x<10;x++)
        {
            Creature fred = new Creature(IdGen.getID(),"Fred"+x,"Some Dude"+x,100,10,5,1);
            CreatureResource fredRes = new CreatureResource(fred,10,3,1);
            room.addCreatureResource(fredRes);
            assertTrue(TheWorld.getInstance().m_spawnTimer.contains(fredRes));
        }
        
        assertEquals(10,room.getCreatureResources().size());
        

        TheWorld.getInstance().m_roomTimer.timeChanged();

        
        assertEquals(0,room.getCreatureResources().size());
    }
    
    @Test
    public void testCreaturesCanMoveOnlyInValidZones()
    {
        TheWorld.reset();  // In case a test forgot to do this.
        // We need to turn off all the normal timers for testing reasons.
        TheWorld.getInstance(false);
        
        // Add rooms
        Room[] rooms = new Room[3];
        for (int x=0;x<rooms.length;x++)
        {
            rooms[x] = new Room(IdGen.getID(), "Room"+x,"Forest"+x);
            rooms[x].setZone(Zone.FOREST);
            TheWorld.getInstance().addRoom(rooms[x]);
            
            Creature fred = new Creature(IdGen.getID(),"Fred","Some Dude"+x,100,10,5,2);
            CreatureResource fredRes = new CreatureResource(fred,10,3,4);
            fredRes.setSpecialCreatureRate(1);
            fredRes.setAmount(10);
            rooms[x].addCreatureResource(fredRes);
        }
        
        for (int x=0;x<rooms.length;x++)
        {
            int dest1 = (x+1)%rooms.length;
            int dest2 = (x-1)%rooms.length;
            rooms[x].addExit(rooms[dest1], Exit.EAST);
            if (dest2 > 0)
                rooms[x].addExit(rooms[dest2], Exit.WEST);
        }
        
        //Can't add creature to room that zone doesn't fit.
        Creature fred = new Creature(IdGen.getID(),"Fred","Some Dude",100,10,5,2);
        fred.addZone(Zone.BEACH);
        
        rooms[0].addCreature(fred);
        assertEquals(0,rooms[0].getNumCreatures());
        
        // Can add creature to room that zone does fit.
        fred.addZone(Zone.FOREST);
        rooms[0].addCreature(fred);
        assertEquals(1,rooms[0].getNumCreatures());
        
        // Creature will move into rooms it can.
        CreatureResource fredRes = new CreatureResource(fred,10,3,4);
        fredRes.setSpecialCreatureRate(1);
        fredRes.setAmount(10);
        fred.setResource(fredRes);
        TheWorld.getInstance().m_moveTimer.timeChanged();
        
        assertEquals(0,rooms[0].getNumCreatures());
        assertEquals(1,rooms[1].getNumCreatures());
        
        rooms[0].setZone(Zone.CITY);
        
        TheWorld.getInstance().m_moveTimer.timeChanged();
        
        assertEquals(rooms[2],fred.getCurrentRoom());
        
        rooms[1].setZone(Zone.CITY);
        
        TheWorld.getInstance().m_moveTimer.timeChanged();
        
        assertEquals(rooms[2],fred.getCurrentRoom());  
    }

}

/**
 * Mock observer for use in testing OutsideRoom
 */
class MockRoomRemovesObserver implements RoomObserver
{
    /**
     * The current time held by the observer
     */
    public Room myRoom = null;
    public Room m_timer;
    
    public MockRoomRemovesObserver(Room timer)
    {
        m_timer = timer;
    }

    /**
     * @see gameplay.TimeObserver#updateTime(int)
     * Simply puts the new time into an instance variable
     */
    @Override
    public void roomUpdate(Room room, Object source, SWRoomUpdateType type)
    {
        myRoom = room;
        m_timer.removeRoomObserver(this);
        
    }
    
}

class ThreadForRaceTest extends Thread
{
    public String done = "no";
    public void run()
    {
        for (int x=0;x<10000;x++)
        {
            TheWorld.getInstance().m_moveTimer.timeChanged();
            TheWorld.getInstance().m_combatTimer.timeChanged();
            TheWorld.getInstance().m_spawnTimer.timeChanged();
            TheWorld.getInstance().m_roomTimer.timeChanged();
        }
        done = "yes";
    }
}

class MockItem extends Item
{
    public MockItem(String name, String desc,int size, int weight)
    {
        super(name,desc,size,weight);
    }
}
