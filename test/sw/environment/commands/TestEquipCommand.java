package sw.environment.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import sw.environment.RoomUpdateType;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.HandLocation;


public class TestEquipCommand extends TestInWorldCommand
{

    @Test
    public void testEquipArmorHeldInRightOrLeftHand()
    {
        roomObvOneP1.clearUpdates();
        
        Armor rArmor = new Armor("Plate","Ugly",20,50,ArmorLocation.BODY,30);
        player1.holdInHand(rArmor, HandLocation.RIGHT);
        Armor lArmor = new Armor("Helmet","Ugly",20,50,ArmorLocation.HEAD,30);
        player1.holdInHand(lArmor, HandLocation.LEFT);
        InWorldCommand cmd = new EquipCommand();
        processCommand(cmd,"equip Plate",player1);
        
        assertEquals(RoomUpdateType.WEAR_ITEM,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You put on the Plate.",data.getMsgForSource());
        assertEquals(player1.getName()+" puts on the Plate.",data.getMsgForOthers());
        
        assertEquals(null,player1.getHeldItem(HandLocation.RIGHT));
        assertEquals(lArmor,player1.getHeldItem(HandLocation.LEFT));
        
        roomObvOneP1.clearUpdates();
        
        processCommand(cmd,"equip Helmet",player1);
        
        assertEquals(RoomUpdateType.WEAR_ITEM,roomObvOneP1.myType.elementAt(0));
        data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You put on the Helmet.",data.getMsgForSource());
        assertEquals(player1.getName()+" puts on the Helmet.",data.getMsgForOthers());
        assertEquals(null,player1.getHeldItem(HandLocation.RIGHT));
        assertEquals(null,player1.getHeldItem(HandLocation.LEFT));
    }
    
    @Test
    public void testEquipWeaponHeldInRightOrLeftHand()
    {
        fail("Not implemented.");
    }
    
    @Test
    public void testEquipTwoHandedWeaponHeldInRightOrLeftHand()
    {
        fail("Not implemented.");
    }
    
    @Test
    public void testContainerHeldInRightOrLeftHand()
    {
        fail("Not implemented.");
    }
    
    @Test
    public void testNonEquipableHeldInRightOrLeftHand()
    {
        fail("Not implemented.");
    }
}
