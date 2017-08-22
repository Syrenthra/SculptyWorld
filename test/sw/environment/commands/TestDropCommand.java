package sw.environment.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import sw.environment.Room;
import sw.environment.RoomUpdateType;
import sw.item.HandLocation;



public class TestDropCommand extends TestInWorldCommand
{
    

    
    @Test
    public void testDropItemWhenBothHandsFull()
    {
     // clear out the observer
        roomObvOneP1.clearUpdates();
        
        MockCommandItem rJunk = new MockCommandItem("RJunk","Junk");
        player1.holdInHand(rJunk, HandLocation.RIGHT);
        MockCommandItem lJunk = new MockCommandItem("LJunk","Junk");
        player1.holdInHand(lJunk, HandLocation.LEFT);
        InWorldCommand cmd = new DropCommand();
        processCommand(cmd,"drop RJunk",player1);
        assertEquals(RoomUpdateType.ITEM_DROPPED,roomObvOneP1.myType.elementAt(1));
        CommandResult cr = (CommandResult)roomObvOneP1.src.elementAt(1);
        assertEquals(player1,cr.getSource());
        assertEquals(player1.getName()+" dropped the RJunk to the ground.",cr.m_otherMsg);
        assertEquals("You dropped the RJunk to the ground.",cr.m_sourceMsg);
        assertEquals(null,player1.getHeldItem(HandLocation.RIGHT));
        Room room = player1.getCurrentRoom();
        assertEquals(rJunk,room.getItem("RJunk"));
        
         // clear out the observer
        roomObvOneP1.clearUpdates();
        
        processCommand(cmd,"drop LJunk",player1);
        cr = (CommandResult)roomObvOneP1.src.elementAt(1);
        assertEquals(player1,cr.m_source);
        assertEquals(player1.getName()+" dropped the LJunk to the ground.",cr.m_otherMsg);
        assertEquals("You dropped the LJunk to the ground.",cr.m_sourceMsg);
        assertEquals(null,player1.getHeldItem(HandLocation.LEFT));
        assertEquals(lJunk,room.getItem("LJunk"));
    }
    
    @Test
    public void testDropItemNotBeingHeld()
    {
     // clear out the observer
        roomObvOneP1.clearUpdates();
        
        // Player holding nothing.
        DropCommand cmd = new DropCommand();
        processCommand(cmd,"drop RJunk",player1);
        CommandResult cr = (CommandResult)roomObvOneP1.src.elementAt(0);
        assertEquals(player1,cr.m_source);
        assertEquals("",cr.m_otherMsg);
        assertEquals("You are not holding any RJunk, no item dropped.",cr.m_sourceMsg);
        
        // clear out the observer
        roomObvOneP1.clearUpdates();
        
        // Player holding stuff.
        MockCommandItem rJunk = new MockCommandItem("RJunk","Junk");
        player1.holdInHand(rJunk, HandLocation.RIGHT);
        MockCommandItem lJunk = new MockCommandItem("LJunk","Junk");
        player1.holdInHand(lJunk, HandLocation.LEFT);
        
        processCommand(cmd,"drop Junk",player1);
        cr = (CommandResult)roomObvOneP1.src.elementAt(0);
        assertEquals(player1,cr.m_source);
        assertEquals("",cr.m_otherMsg);
        assertEquals("You are not holding any Junk, no item dropped.",cr.m_sourceMsg);
        assertEquals(rJunk,player1.getHeldItem(HandLocation.RIGHT)); 
        assertEquals(lJunk,player1.getHeldItem(HandLocation.LEFT));
    }

}
