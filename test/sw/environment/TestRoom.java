package sw.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.PC;
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
        
        testRoom = new Room("Test Room2","This is a room too.");
        assertEquals("This is a room too.",testRoom.getDescription());
        assertEquals("Test Room2",testRoom.getTitle());
        assertEquals(-1,testRoom.getID());
        testRoom.setID(2);
        assertEquals(2,testRoom.getID());
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
        
        //Test won't crash on item not there
        assertNull(testRoom1.getItem(0));
        
        testRoom1.addItem(item);
        assertEquals(item,testRoom1.getItem(0));
        
        //Can Get an Item by Name.
        assertEquals(item,testRoom1.getItem("Item"));
    }
    
    @Test
    public void testKeepAndRemovePlayers()
    {
        Room testRoom1 = new Room(IdGen.getID(),"Test1","Desc1");
        PC dude = new PC(IdGen.getID(),"Dude", "Desc",10);
        testRoom1.addPC(dude);
        assertEquals(dude,testRoom1.getPC(dude.getID()));
        
        assertTrue(testRoom1.hasPlayer(dude.getID()));
        assertEquals(testRoom1,dude.getCurrentRoom());
        assertTrue(testRoom1.hasPlayer(dude.getName()));
        assertEquals(dude,testRoom1.getPlayer(dude.getName()));
        assertNull(testRoom1.getPlayer("GuyNotInRoom"));
        
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
    public void testGetLifeformByName()
    {
        Room testRoom1 = new Room(1,"Room","Desc1");
        NPC npcDude = new NPC(1,"NPC Dude", "Desc",50,10,4,13);
        testRoom1.addNPC(npcDude);
        testRoom1.setZone(Zone.CITY);
        Creature creatureDude = new Creature(1,"Creature Dude", "Desc",50,10,4,13);
        creatureDude.addZone(Zone.CITY);
        testRoom1.addCreature(creatureDude);
        PC pcDude = new PC(IdGen.getID(),"PC Dude", "Desc",10);
        testRoom1.addPC(pcDude);
        
        assertEquals(npcDude,testRoom1.getLifeform("NPC Dude"));
        assertEquals(creatureDude,testRoom1.getLifeform("Creature Dude"));
        assertEquals(pcDude,testRoom1.getLifeform("PC Dude"));
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
        room.informObservers(null,RoomUpdateType.CREATURE_ADDED);
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
    
    @Test
    public void testGetRoomInfoBasics()
    {
        Room room = new Room(1, "Tree","Forest");
        room.setZone(Zone.BEACH);
        room.addExit(room, Exit.EAST);

        Hashtable<String,Object> data = room.getRoomInfo();
        assertEquals(1,data.get(Room.ID));
        assertEquals("Tree",data.get(Room.TITLE));
        assertEquals("Forest",data.get(Room.DESCRIPTION));
        assertEquals("BEACH",data.get(Room.ZONE));
        Hashtable<String,Integer> exits = (Hashtable<String,Integer>)data.get(Room.EXITS);
        assertEquals(1,exits.size());
        Integer value = exits.get("EAST");
        assertEquals(1,value.intValue());
    }
    
    @Test
    public void testGetRoomInfoWithPCandItems()
    {
        ItemContainer pack1 = new ItemContainer("Belt","Desc",20,5,10);
        pack1.setItemID(3);
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        weapon.setItemID(2);
        pack1.store(weapon);
        PC dude = new PC(4,"Dude","Desc",50);
        Room room = new Room(1, "Tree","Forest");
        room.setZone(Zone.BEACH);
        Hashtable<String,Object> data = room.getRoomInfo();
        
        Vector<Integer> items = (Vector<Integer>)data.get(Room.ITEMS);
        assertEquals(1,items.size());
        Integer value = items.elementAt(0);
        assertEquals(3,value.intValue());
        
        Vector<Integer> pcs = (Vector<Integer>)data.get(Room.PCs);
        assertEquals(1,pcs.size());
        value = pcs.elementAt(0);
        assertEquals(4,value.intValue());
        
    }
    
    @Test
    public void testGetRoomInfoWithCreatures()
    {
        Creature dude = new Creature(2, "Dude", "Desc", 50, 10, 5, 15);
        dude.addZone(Zone.BEACH);
        dude.addZone(Zone.DESERT);
        Room room = new Room(1, "Tree","Forest");
        room.setZone(Zone.BEACH);
        room.addCreature(dude);

        Hashtable<String,Object> data = room.getRoomInfo();
        
        Vector<Integer> creatures = (Vector<Integer>)data.get(Room.CREATURES);
        assertEquals(1,creatures.size());
        int value = creatures.elementAt(0);
        assertEquals(2,value);
        
        
    }
    
    @Test
    public void testConstructRoom()
    {
        Hashtable<String,Object> data = new Hashtable<String,Object>();
        data.put(Room.ID, 1);
        data.put(Room.ZONE, "BEACH");
        data.put(Room.TITLE, "Title");
        data.put(Room.DESCRIPTION, "Description");
        Room room = Room.constructRoom(data);
        
        assertEquals(1,room.getID());
        assertEquals("Title",room.getTitle());
        assertEquals("Description",room.getDescription());
        assertEquals(Zone.BEACH,room.getZone());
        
    }
    
    @Test
    public void testGetZoneCluster()
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
        
        world.constructZoneGraph();
        
        Hashtable<Integer, Room> forestGroup = world.getZone(room4).getRooms();
        assertEquals(3,forestGroup.size());
        assertTrue(forestGroup.containsKey(new Integer(3)));
        assertTrue(forestGroup.containsKey(new Integer(4)));
        assertTrue(forestGroup.containsKey(new Integer(5)));
        
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
    public void roomUpdate(Room room, Object source, RoomUpdateType type)
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
