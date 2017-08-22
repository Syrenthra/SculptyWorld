package sw.item;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sw.item.Weapon;

public class TestWeapon
{
    @Test
    public void testInitialize()
    {
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        assertEquals("Sword",weapon.getName());
        assertEquals("A Sword",weapon.getDescription());
        assertEquals(20,weapon.getDamage());
        assertEquals(1,weapon.getNumHands());
    }
    

}
