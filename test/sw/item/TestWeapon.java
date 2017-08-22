package sw.item;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

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
        assertEquals(Item.WEAPON,weapon.getType());
    }
    
    @Test
    public void testGetItemInfo()
    {
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        weapon.setItemID(4);
        
        Hashtable<String,Object> data = weapon.getItemInfo();
        assertEquals(4,data.get(Item.ID));
        assertEquals("Sword",data.get(Item.NAME));
        assertEquals("A Sword",data.get(Item.DESC));
        assertEquals(10,data.get(Item.SIZE));
        assertEquals(5,data.get(Item.WEIGHT));
        assertEquals(20,data.get(Weapon.DMG));
        assertEquals(1,data.get(Weapon.HAND));
        assertEquals(Item.WEAPON,data.get(Item.TYPE));
    }
    
    @Test
    public void testConstructWeapon()
    {
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        weapon.setItemID(1);
        
        Hashtable<String,Object> data = weapon.getItemInfo();
        
        Weapon newWeap = Weapon.constructWeapon(data);
        
        assertEquals("Sword",newWeap.getName());
        assertEquals("A Sword",newWeap.getDescription());
        assertEquals(10,newWeap.getSize());
        assertEquals(5,newWeap.getWeight());
        assertEquals(20,newWeap.getDamage());
        assertEquals(1,newWeap.getNumHands());
        assertEquals(1,newWeap.getItemID());
    }
    
    @Test
    public void testCloneWeapon()
    {
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        Weapon cloneOfWeapon = weapon.clone();
        
        assertEquals(weapon.getName(),cloneOfWeapon.getName());
        assertEquals(weapon.getDamage(),cloneOfWeapon.getDamage());
        assertEquals(weapon.getDescription(),cloneOfWeapon.getDescription());
        assertEquals(weapon.getSize(),cloneOfWeapon.getSize());
        assertEquals(-1,cloneOfWeapon.getItemID());
        assertEquals(weapon.getWeight(),cloneOfWeapon.getWeight());
    }

}
