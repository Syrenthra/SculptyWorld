package sw.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertFalse(pack.validLocation(ContainerLocation.BACK));
        assertFalse(pack.validLocation(ContainerLocation.BELT));
        assertEquals(5,pack.getWeight());
    }
    
    @Test
    public void testUpdateValidLocation()
    {
        ItemContainer pack = new ItemContainer("Belt","Desc",10,5,10);
        assertEquals("Belt",pack.getName());
        assertEquals("Desc",pack.getDescription());
        assertEquals(10,pack.getMaxCapacity());
        pack.addValidLocation(ContainerLocation.BACK);
        assertTrue(pack.validLocation(ContainerLocation.BACK));
        assertFalse(pack.validLocation(ContainerLocation.BELT));
        
        pack.addValidLocation(ContainerLocation.BELT);
        assertTrue(pack.validLocation(ContainerLocation.BACK));
        assertTrue(pack.validLocation(ContainerLocation.BELT));
        
        pack.removeValidLocation(ContainerLocation.BELT);
        pack.removeValidLocation(ContainerLocation.BACK);
        assertFalse(pack.validLocation(ContainerLocation.BACK));
        assertFalse(pack.validLocation(ContainerLocation.BELT));
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
    

}
