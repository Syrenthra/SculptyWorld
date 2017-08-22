package sw.environment.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sw.environment.RoomUpdateType;
import sw.environment.TheWorld;
import sw.item.HandLocation;
import sw.item.Weapon;


public class TestGetCommand extends TestInWorldCommand
{    

    @Test
    public void testGetWeaponWhenHandsAreEmpty()
    {  
        weap = new Weapon("name", "desc", 5, 5, 5, 1);
        player1.getCurrentRoom().addItem(weap);
        
        roomObvOneP1.clearUpdates();
        
        InWorldCommand cmd = new GetCommand();
        processCommand(cmd,"get name",player1);
        
        assertEquals(RoomUpdateType.GET_ITEM,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.m_source);
        assertEquals("You got the name, holding in your right hand.",data.m_sourceMsg);
        assertEquals(player1.getName()+" got the name, holding in his right hand.",data.m_otherMsg);
        
        assertEquals(null,player1.getCurrentRoom().getItem("name"));
        assertEquals(weap,player1.getWeapon(HandLocation.RIGHT));
        assertEquals(null,player1.getWeapon(HandLocation.LEFT));
    }
    
    @Test
    public void testGetWeaponWhenRightHandFull()
    {
        weap = new Weapon("name", "desc", 5, 5, 5, 1);
        player1.getCurrentRoom().addItem(weap);
        
        MockCommandItem junk = new MockCommandItem("Junk","Junk");
        player1.holdInHand(junk, HandLocation.RIGHT);
        
        roomObvOneP1.clearUpdates();
        
        InWorldCommand cmd = new GetCommand();
        
        processCommand(cmd,"get name",player1);
        
        assertEquals(RoomUpdateType.GET_ITEM,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.m_source);
        assertEquals("You got the name, holding in your left hand.",data.m_sourceMsg);
        assertEquals(player1.getName()+" got the name, holding in his left hand.",data.m_otherMsg);
        
        assertEquals(weap,player1.getWeapon(HandLocation.LEFT));
    }
    
    @Test
    public void testGetWeaponWhenBothHandsFull()
    {
        MockCommandItem rJunk = new MockCommandItem("RJunk","Junk");
        player1.holdInHand(rJunk, HandLocation.RIGHT);
        MockCommandItem lJunk = new MockCommandItem("LJunk","Junk");
        player1.holdInHand(lJunk, HandLocation.LEFT);
        
        roomObvOneP1.clearUpdates();
        
        GetCommand cmd = new GetCommand();
        processCommand(cmd,"get name",player1);
        
        assertEquals(RoomUpdateType.GET_ITEM,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.m_source);
        assertEquals("Failed to get name, your hands are full.",data.m_sourceMsg);
        assertEquals(player1.getName()+" tries to get name, but his hands are full.",data.m_otherMsg);
        
        assertEquals(rJunk,player1.getHeldItem(HandLocation.RIGHT));
        assertEquals(lJunk,player1.getHeldItem(HandLocation.LEFT));
    }

}


