package sw.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import org.junit.Test;

import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.ItemContainer;
import sw.item.Weapon;


public class TestItemContainer
{
    @Test
    public void testInitialize()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",4,5,10);
        assertEquals("Belt",pack.getName());
        assertEquals("Desc",pack.getDescription());
        assertEquals(10,pack.getMaxCapacity());
        assertEquals(4,pack.getSize());
        assertEquals(5,pack.getWeight());
        assertEquals(Item.CONTAINER,pack.getType());
        assertTrue(pack.validLocation(ContainerLocation.BACK));
    }
    
    @Test
    public void testUpdateValidLocation()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",10,5,10);
        assertEquals("Belt",pack.getName());
        assertEquals("Desc",pack.getDescription());
        assertEquals(10,pack.getMaxCapacity());
        pack.setValidLocation(ContainerLocation.BACK);
        assertTrue(pack.validLocation(ContainerLocation.BACK));
        assertFalse(pack.validLocation(ContainerLocation.BELT));
        
        pack.setValidLocation(ContainerLocation.BELT);
        assertEquals(ContainerLocation.BELT,pack.getValidLocation());
        assertFalse(pack.validLocation(ContainerLocation.BACK));
        assertTrue(pack.validLocation(ContainerLocation.BELT));
    }
    
    @Test
    public void testGetItemAndListOfItems()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",20,5,10);
        Weapon weapon = new Weapon("Sword","A Sword",2,5,20,1);
        weapon.setItemID(1);
        Armor armor = new Armor("Plate","Ugly",5,50,ArmorLocation.BODY,30);
        armor.setItemID(2);
        pack.store(weapon);
        pack.store(armor);
        
        Vector<Item> items = pack.getItems();
        assertEquals(2,items.size());
        assertEquals(weapon,items.elementAt(0));
        assertEquals(armor,items.elementAt(1));
        
        Item item = pack.removeItem("Plate"); // Use the name of the item
        assertEquals(armor,item);
        item = pack.removeItem(1); // Use the ID of the item
        assertEquals(weapon,item);
    }
    
    @Test
    public void testGetItemInfoSWithNoItems()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",20,5,10);
        pack.setValidLocation(ContainerLocation.BELT);
        pack.setItemID(1);
        Hashtable<String,Object> data = pack.getItemInfo();
        assertEquals(1,data.get(Item.ID));
        assertEquals("Belt",data.get(Item.NAME));
        assertEquals("Desc",data.get(Item.DESC));
        assertEquals(20,data.get(Item.SIZE));
        assertEquals(5,data.get(Item.WEIGHT));
        assertEquals(10,data.get(ItemContainer.CAP));
        assertTrue(data.get(ItemContainer.ITEMS) instanceof Vector);
        
        Vector<Integer> itemVals = (Vector<Integer>)data.get(ItemContainer.ITEMS);
        assertEquals(0,itemVals.size());
        
        String itemLoc = (String)data.get(ItemContainer.WEAR_LOC);
        assertEquals("BELT",itemLoc);
    }
    
    @Test
    public void testGetItemInfoWithItems()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",20,5,10);
        Weapon weapon = new Weapon("Sword","A Sword",2,5,20,1);
        weapon.setItemID(1);
        Armor armor = new Armor("Plate","Ugly",5,50,ArmorLocation.BODY,30);
        armor.setItemID(2);
        pack.store(weapon);
        pack.store(armor);
        
        Hashtable<String,Object> data = pack.getItemInfo();
        Vector<Integer> itemVals = (Vector<Integer>)data.get(ItemContainer.ITEMS);
        int id = itemVals.elementAt(0);
        assertEquals(1,id);
        id = itemVals.elementAt(1);
        assertEquals(2,id);
    }
    
    @Test
    public void testItemContainerInItemContainerFitsProperly()
    {
        ItemContainer pack1 = new ItemContainer("Belt","Desc",20,5,10);
        pack1.setItemID(1);
        ItemContainer pack2 = new ItemContainer("Belt","Desc",5,5,10);
        Weapon weapon = new Weapon("Sword","A Sword",2,5,20,1);
        weapon.setItemID(3);
        assertTrue(pack2.store(weapon));
        assertTrue(pack1.store(pack2));
        
        
        assertEquals(5,pack1.getRemainingCapacity());
    }
    
    @Test
    public void testConstructItemContainer()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",20,5,10);
        pack.setValidLocation(ContainerLocation.BELT);
        pack.setItemID(1);
        Hashtable<String,Object> data = pack.getItemInfo();
        
        ItemContainer item = ItemContainer.constructItemContainer(data);
        assertEquals(pack.getItemID(),item.getItemID());
        assertEquals(pack.getDescription(),item.getDescription());
        assertEquals(pack.getName(),item.getName());
        assertEquals(pack.getMaxCapacity(),item.getMaxCapacity());
        assertEquals(pack.getSize(),item.getSize());
        assertEquals(pack.getValidLocation(),item.getValidLocation());
    }
    
    @Test
    public void testStoringItems()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",4,5,10);
        Weapon weapon = new Weapon("Sword","A Sword",2,5,20,1);
        Armor armor = new Armor("Plate","Ugly",5,50,ArmorLocation.BODY,30);
        assertTrue(pack.store(weapon));
        assertEquals(4,pack.getSize());
        assertEquals(10,pack.getWeight());
        assertEquals(8,pack.getRemainingCapacity());
        
        assertTrue(pack.store(armor));
        assertEquals(7,pack.getSize());
        assertEquals(60,pack.getWeight());
        assertEquals(3,pack.getRemainingCapacity());
        
        assertFalse(pack.store(armor));
        assertEquals(7,pack.getSize());
        assertEquals(60,pack.getWeight());
        assertEquals(3,pack.getRemainingCapacity());
    }
    
    @Test
    public void testCanCreateClone()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",4,5,10);
        pack.setValidLocation(ContainerLocation.BACK);
        
        ItemContainer cloneOfPack = pack.clone();
        assertEquals(pack.getName(),cloneOfPack.getName());
        assertEquals(pack.getDescription(),cloneOfPack.getDescription());
        assertEquals(pack.getMaxCapacity(),cloneOfPack.getMaxCapacity());
        assertEquals(0,cloneOfPack.getItems().size());
        assertEquals(-1,cloneOfPack.getItemID());
    }
    

}
