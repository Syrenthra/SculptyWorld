package sw.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.environment.Room;
import sw.environment.Zone;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.lifeform.Creature;
import sw.lifeform.PC;
import sw.quest.Goal;
import sw.quest.task.DeliverItemTask;

import database.DatabaseInfo;
import database.DatabaseManager;


public class TestRoomTable
{
    
   DatabaseManager dm;
    
    @Before
    public void before() throws Exception
    {
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
    }
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Test
    public void testGlobals()
    {
        assertEquals("ROOM_TABLE",RoomTable.NAME);
        assertEquals("ROOM_ID",RoomTable.ROOM_ID);
        assertEquals("ROOM_DATA",RoomTable.ROOM_DATA);
    }
    
    @Test
    public void testRoomTableStoresRoomWithID()
    {
        RoomTable rt = new RoomTable();
        Room room = new Room(1, "Tree","Forest");
        boolean result = rt.storeRoom(room);
        assertTrue(result);
        Room retRoom = rt.loadRoom(1);
        assertNotNull(retRoom);
        assertEquals(1,retRoom.getID());
        assertEquals("Tree",retRoom.getTitle());
        assertEquals("Forest",retRoom.getDescription());
    }
    
    @Test
    public void testRoomTableStoresRoomWithOutID()
    {
        RoomTable rt = new RoomTable();
        Room room = new Room("Tree","Forest");
        boolean result = rt.storeRoom(room);
        assertTrue(result);
        Room retRoom = rt.loadRoom(1);
        assertNotNull(retRoom);
        assertEquals(1,retRoom.getID());
        assertEquals("Tree",retRoom.getTitle());
        assertEquals("Forest",retRoom.getDescription());
    }
    
    @Test
    public void testRoomTableStoresRoomWithItems()
    {
        ItemContainer pack1 = new ItemContainer("Belt","Desc",20,5,10);
        pack1.setItemID(3);
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        weapon.setItemID(2);
        pack1.store(weapon);
        RoomTable rt = new RoomTable();
        Room room = new Room(1, "Tree","Forest");
        
        room.addItem(pack1);
        boolean result = rt.storeRoom(room);
        
        assertTrue(result);
        Room retRoom = rt.loadRoom(1);
        assertNotNull(retRoom);
        assertEquals(1,retRoom.getID());
        assertEquals("Tree",retRoom.getTitle());
        assertEquals("Forest",retRoom.getDescription());
        assertEquals(pack1.getItemID(),retRoom.getItem(0).getItemID());
        ItemContainer retPack = (ItemContainer)retRoom.getItem(0);
        assertEquals(1,retPack.getItems().size());
        assertEquals(weapon.getItemID(),retPack.getItems().elementAt(0).getItemID());
    }
    
    @Test
    public void testStoreRoomWithPlayerCharacter()
    {
        RoomTable rt = new RoomTable();
        Room room = new Room(1, "Oak","Plain");
        PC dude = new PC(1, "Dude", "Desc", 50);
        room.addPC(dude);
        
        boolean result = rt.storeRoom(room);
        assertTrue(result);
        Room retRoom = rt.loadRoom(1);
        assertNotNull(retRoom);
        assertEquals(1,retRoom.getID());
        assertEquals("Oak",retRoom.getTitle());
        assertEquals("Plain",retRoom.getDescription());
        assertEquals(dude.getID(),retRoom.getPC(1).getID());
    }
    
    @Test
    public void testStoreRoomWithCreature()
    {
        RoomTable rt = new RoomTable();
        Creature dude = new Creature(2, "Dude", "Desc", 50, 10, 5, 15);
        dude.addZone(Zone.BEACH);
        Room room = new Room(1, "Oak","Plain");
        room.setZone(Zone.BEACH);
        room.addCreature(dude);
        
        boolean result = rt.storeRoom(room);
        assertTrue(result);
        Room retRoom = rt.loadRoom(1);
        assertNotNull(retRoom);
        assertEquals(1,retRoom.getID());
        assertEquals("Oak",retRoom.getTitle());
        assertEquals("Plain",retRoom.getDescription());
        assertEquals(dude.getID(),retRoom.getCreature(2).getID());
    }

}
