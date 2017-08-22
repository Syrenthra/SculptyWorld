package sw.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.DatabaseInfo;
import database.DatabaseManager;

import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;


public class TestItemTable
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
        assertEquals("ITEM_TABLE",ItemTable.NAME);
        assertEquals("ITEM_ID",ItemTable.ITEM_ID);
        assertEquals("ITEM_DATA",ItemTable.ITEM_DATA);
    }
    
    @Test
    public void testItemTableStoresArmorNoID()
    {
        ItemTable it = new ItemTable();
        Armor armor = new Armor("Helmet","Pretty",5,3,ArmorLocation.HEAD,10);
        boolean result = it.storeItem(armor);
        assertTrue(result);
        Item item = it.loadItem(1);
        assertNotNull(item);
        assertEquals(3,item.getWeight());
        assertEquals(1,item.getItemID());
        assertTrue(item instanceof Armor);
    }
    
    @Test
    public void testItemTableStoresArmorWithID()
    {
        ItemTable it = new ItemTable();
        Armor armor = new Armor("Helmet","Pretty",5,3,ArmorLocation.HEAD,10);
        armor.setItemID(10);
        boolean result = it.storeItem(armor);
        assertTrue(result);
        Item item = it.loadItem(10);
        assertNotNull(item);
        assertEquals(3,item.getWeight());
        assertEquals(10,item.getItemID());
        assertTrue(item instanceof Armor);
    }
    
    @Test
    public void testItemTableStoresWeapon()
    {
        ItemTable it = new ItemTable();
        Weapon weap = new Weapon("Helmet","Pretty",5,3,10,1);
        boolean result = it.storeItem(weap);
        assertTrue(result);
        Item item = it.loadItem(1);
        assertNotNull(item);
        assertEquals(3,item.getWeight());
        assertTrue(item instanceof Weapon);
    }
    
    @Test
    public void testItemTableStoresContainerWithoutIDAndItems()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",20,5,10);
        pack.setValidLocation(ContainerLocation.BELT);
        
        ItemTable it = new ItemTable();
        boolean result = it.storeItem(pack);
        assertTrue(result);
        ItemContainer item = (ItemContainer)it.loadItem(1);
        assertNotNull(item);
        assertEquals(5,item.getWeight());
        assertEquals(ContainerLocation.BELT,item.getValidLocation());
    }
    
    @Test
    public void testItemTableStoresContainerWithID()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",20,5,10);
        pack.setItemID(1);
        Weapon weapon = new Weapon("Sword","A Sword",2,5,20,1);
        weapon.setItemID(5);
        Armor armor = new Armor("Plate","Ugly",5,50,ArmorLocation.BODY,30);
        armor.setItemID(6);
        pack.store(weapon);
        pack.store(armor);
        
        ItemTable it = new ItemTable();
        boolean result = it.storeItem(pack);
        assertTrue(result);
        Item item = it.loadItem(1);
        assertNotNull(item);
        assertEquals(60,item.getWeight());
        assertTrue(item instanceof ItemContainer);
        Vector<Item> items = ((ItemContainer)item).getItems();
        Item newWeap = items.elementAt(0);
        assertTrue(newWeap instanceof Weapon);
        assertEquals(5,newWeap.getItemID());
        
        Item newArmor = items.elementAt(1);
        assertEquals(6,newArmor.getItemID());
        assertTrue(newArmor instanceof Armor);
    }

}
