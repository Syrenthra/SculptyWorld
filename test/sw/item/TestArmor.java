package sw.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    }

}
