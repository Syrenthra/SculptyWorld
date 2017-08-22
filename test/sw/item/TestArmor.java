package sw.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;

import org.junit.Test;

import sw.item.Armor;
import sw.item.ArmorLocation;


public class TestArmor
{
    
    @Test
    public void testInitialize()
    {
        Armor armor = new Armor("Helmet","Pretty",5,3,ArmorLocation.HEAD,10);
        
        assertEquals(ArmorLocation.HEAD,armor.getLocation());
        assertEquals(10,armor.getProtection());
        assertEquals(Item.ARMOR,armor.getType());
    }
    
    @Test
    public void testGetItemInfo()
    {
        Armor armor = new Armor("Helmet","Pretty",5,3,ArmorLocation.HEAD,10);
        armor.setItemID(2);
        
        Hashtable<String,Object> data = armor.getItemInfo();
        assertEquals(2,data.get(Item.ID));
        assertEquals("Helmet",data.get(Item.NAME));
        assertEquals("Pretty",data.get(Item.DESC));
        assertEquals(5,data.get(Item.SIZE));
        assertEquals(3,data.get(Item.WEIGHT));
        assertEquals(ArmorLocation.HEAD,data.get(Armor.LOC));
        assertEquals(10,data.get(Armor.PROT));
        assertEquals(Item.ARMOR,data.get(Item.TYPE));
    }
    
    @Test
    public void testConstructArmor()
    {
        Armor armor = new Armor("Helmet","Pretty",5,3,ArmorLocation.HEAD,10);
        armor.setItemID(3);
        
        Hashtable<String,Object> data = armor.getItemInfo();
        
        Armor newArmor = Armor.constructArmor(data);
        
        assertEquals("Helmet",newArmor.getName());
        assertEquals("Pretty",newArmor.getDescription());
        assertEquals(5,newArmor.getSize());
        assertEquals(3,newArmor.getWeight());
        assertEquals(ArmorLocation.HEAD,newArmor.getLocation());
        assertEquals(10,newArmor.getProtection());
        assertEquals(3,newArmor.getItemID());
    }
    
    @Test
    public void testCreateClone()
    {
        Armor armor = new Armor("Helmet","Pretty",5,3,ArmorLocation.HEAD,10);
        Armor cloneOfArmor = armor.clone();
        
        assertEquals(armor.getName(),cloneOfArmor.getName());
        assertEquals(armor.getDescription(),cloneOfArmor.getDescription());
        assertEquals(armor.getProtection(),cloneOfArmor.getProtection());
        assertEquals(-1,cloneOfArmor.getItemID());
        assertEquals(armor.getSize(),cloneOfArmor.getSize());
        assertEquals(armor.getWeight(),cloneOfArmor.getWeight());
        assertEquals(armor.getLocation(),cloneOfArmor.getLocation());
        assertEquals(Item.ARMOR,cloneOfArmor.getType());
    }

}
