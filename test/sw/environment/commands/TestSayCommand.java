package sw.environment.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.DemoTestWorld;
import sw.DemoWorld;
import sw.environment.RoomUpdateType;
import sw.lifeform.NPC;
import sw.lifeform.PC;


public class TestSayCommand extends TestInWorldCommand
{
    
    /**
     * Test phrases say <general phrase here>.
     */
    @Test
    public void testSaySomethingInRoom()
    {          
        //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        obv2.clearUpdates();
        
        SayCommand cmd = new SayCommand();
        processCommand(cmd,"say hello, hi everybody.",player1);

        assertEquals(RoomUpdateType.SAID,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You say, \"hello, hi everybody.\"",data.getMsgForSource());
        
      //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        assertEquals(RoomUpdateType.SAID,obv2.myType.elementAt(0));
        data = (CommandResult)(obv2.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals(player1.getName()+" says, \"hello, hi everybody.\"",data.getMsgForOthers());
        assertEquals("You say, \"hello, hi everybody.\"",data.getMsgForSource());
    }
    
    /**
     * Test phrases say to <LifeForm Name> <general phrase here>.
     */
    @Test
    public void testSaySometingToSomeone()
    {
      //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        String p2Name = DemoTestWorld.getPlayer2().getName();
        
        // Person in the room.
        SayCommand cmd = new SayCommand();
        processCommand(cmd,"say to "+p2Name+" hello, hi everybody.",player1);

        // Check the CommandResult
        assertEquals(RoomUpdateType.SAID,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You say to "+p2Name+", \"hello, hi everybody.\"",data.getMsgForSource());
        assertEquals(player1.getName()+" says to "+p2Name+", \"hello, hi everybody.\"",data.getMsgForOthers());
        assertEquals(DemoWorld.getPlayer2(),data.getTarget());
        assertEquals(player1.getName()+" says to you, \"hello, hi everybody.\"",data.getMsgForTarget());
        
        //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        // Person not in the room.
        processCommand(cmd,"say to Player3 hello, hi everybody.",player1);
        assertEquals(RoomUpdateType.ERROR_MSG,roomObvOneP1.myType.elementAt(0));
        data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("That person or creature is not here.",data.getMsgForSource());
        assertEquals("",data.getMsgForOthers());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertEquals("",data.getMsgForTarget());
    }
    
    /**
     * Test phrases say to <LifeForm Name>
     */
    @Test
    public void testSaySometingToSomeoneWithNoMessage()
    {
      //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        obv2.clearUpdates();
        
        SayCommand cmd = new SayCommand();
        processCommand(cmd,"say to Demo Player2",player1);

        assertEquals(RoomUpdateType.SAID,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You say to Demo Player2, \"\"",data.getMsgForSource());
        
      //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        assertEquals(RoomUpdateType.SAID,obv2.myType.elementAt(0));
        data = (CommandResult)(obv2.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals(player1.getName()+" says to Demo Player2, \"\"",data.getMsgForOthers());
        assertEquals("Demo Player2",data.getTarget().getName());
        assertEquals(player1.getName()+" says to you, \"\"", data.getMsgForTarget());
        assertEquals("You say to Demo Player2, \"\"",data.getMsgForSource());
    }
    
    @Test
    public void testSayToNPC()
    {
        NPC npc = DemoTestWorld.getMountainQuestDude();
        roomObvOneP1.clearUpdates();
        
        
        SayCommand cmd = new SayCommand();
        processCommand(cmd,"say to "+npc.getName()+" Hello",player1);
        
        assertEquals(RoomUpdateType.SAID,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You say to "+npc.getName()+", \"Hello\"",data.getMsgForSource());
        assertEquals(player1.getName()+" says to "+npc.getName()+", \"Hello\"",data.getMsgForOthers());
        
        
        
        
    }
    
}


