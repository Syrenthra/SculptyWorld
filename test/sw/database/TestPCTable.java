package sw.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.DatabaseInfo;
import database.DatabaseManager;

import sw.environment.Room;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.HandLocation;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.lifeform.PC;


public class TestPCTable
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
        assertEquals("PC_TABLE",PCTable.NAME);
        assertEquals("PC_ID",PCTable.PC_ID);
        assertEquals("PC_NAME",PCTable.PC_NAME);
        assertEquals("PC_DATA",PCTable.PC_DATA);
    }
    
    @Test
    public void testCharacterTableStoresCharacterWithID()
    {
        PCTable ct = new PCTable();
        PC dude = new PC(1,"Dude","Desc",50);
        boolean result = ct.storePC(dude);
        assertTrue(result);
        PC retDude = ct.loadPC(1);
        assertNotNull(retDude);
        assertEquals(1,retDude.getID());
        assertEquals("Dude",retDude.getName());
        assertEquals("Desc",retDude.getDescription());
    }
    
    @Test
    public void testLoadCharacterUsingName()
    {
        PCTable ct = new PCTable();
        PC dude = new PC(1,"Dude","Desc",50);
        boolean result = ct.storePC(dude);
        assertTrue(result);
        PC retDude = ct.loadPC("Dude");
        assertNotNull(retDude);
        assertEquals(1,retDude.getID());
        assertEquals("Dude",retDude.getName());
        assertEquals("Desc",retDude.getDescription());
    }
    
    @Test
    public void testStoreCharacterWithOutID()
    {
        PCTable ct = new PCTable();
        PC dude = new PC(-1,"Dude","Desc",50);
        boolean result = ct.storePC(dude);
        assertTrue(result);
        PC retDude = ct.loadPC(1);
        assertNotNull(retDude);
        assertEquals(1,retDude.getID());
        assertEquals("Dude",retDude.getName());
        assertEquals("Desc",retDude.getDescription());
    }
    
    @Test
    public void testStoreCharacterWithAllItems()
    {
        PCTable ct = new PCTable();
        PC dude = new PC(1,"Dude","Desc",50);
        ItemContainer pack1 = new ItemContainer("Back","Desc",20,5,10);
        pack1.setItemID(2);
        pack1.setValidLocation(ContainerLocation.BACK);
        ItemContainer pack2 = new ItemContainer("Belt","Desc",20,5,10);
        pack2.setItemID(3);
        pack2.setValidLocation(ContainerLocation.BELT);
        Weapon weapon = new Weapon("Sword","A Sword",2,5,20,1);
        weapon.setItemID(4);
        Armor armor = new Armor("Plate","Ugly",5,50,ArmorLocation.BODY,30);
        armor.setItemID(5);
        Armor helmet = new Armor("Plate","Ugly",5,50,ArmorLocation.HEAD,30);
        helmet.setItemID(6);
        Armor boots = new Armor("Plate","Ugly",5,50,ArmorLocation.FEET,30);
        boots.setItemID(7);
        Armor gloves = new Armor("Plate","Ugly",5,50,ArmorLocation.HANDS,30);
        gloves.setItemID(8);
        Armor pants = new Armor("Plate","Ugly",5,50,ArmorLocation.LEGS,30);
        pants.setItemID(9);

        dude.equipContainer(pack1);
        dude.equipContainer(pack2);
        dude.holdInHand(weapon, HandLocation.RIGHT);
        dude.wearArmor(armor);
        dude.wearArmor(helmet);
        dude.wearArmor(boots);
        dude.wearArmor(gloves);
        dude.wearArmor(pants);
        boolean result = ct.storePC(dude);
        assertTrue(result);
        PC retDude = ct.loadPC(1);
        assertEquals(armor.getItemID(),retDude.getArmor(ArmorLocation.BODY).getItemID());
        assertEquals(helmet.getItemID(),retDude.getArmor(ArmorLocation.HEAD).getItemID());
        assertEquals(boots.getItemID(),retDude.getArmor(ArmorLocation.FEET).getItemID());
        assertEquals(gloves.getItemID(),retDude.getArmor(ArmorLocation.HANDS).getItemID());
        assertEquals(pants.getItemID(),retDude.getArmor(ArmorLocation.LEGS).getItemID());
        assertEquals(pack1.getItemID(),retDude.getContainer(ContainerLocation.BACK).getItemID());
        assertEquals(pack2.getItemID(),retDude.getContainer(ContainerLocation.BELT).getItemID());
        assertEquals(weapon.getItemID(),retDude.getHeldItem(HandLocation.RIGHT).getItemID());
    }
    
    /**
     * TODO: Get corresponding tests in TestPC to work first.
     */
    @Test
    public void testStorePCInAParty()
    {
        assertTrue(false);
    }
    
    @Test
    public void testStorePCWithQuests()
    {
        assertTrue(false);
    }
    
    @Test
    public void testStorePCWithActionQueue()
    {
        assertTrue(false);
    }

}
