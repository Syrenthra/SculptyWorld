package sw.environment.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sw.DemoTestWorld;
import sw.DemoWorld;
import sw.environment.RoomUpdateType;
import sw.lifeform.NPC;


public class TestAskCommand extends TestInWorldCommand
{

    /**
     * Test phrases ask <LifeForm Name> about <general phrase here>.
     */
    @Test
    public void testAskSomeonSometing()
    {
      //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        String p2Name = DemoTestWorld.getPlayer2().getName();
        
        // Person in the room.
        AskCommand cmd = new AskCommand();
        processCommand(cmd,"ask "+p2Name+" about hi everybody.",player1);

        // Check the CommandResult
        assertEquals(RoomUpdateType.ASK,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You ask "+p2Name+" about hi everybody.",data.getMsgForSource());
        assertEquals(player1.getName()+" asks "+p2Name+" about hi everybody.",data.getMsgForOthers());
        assertEquals(DemoWorld.getPlayer2(),data.getTarget());
        assertEquals(player1.getName()+" asks you about hi everybody.",data.getMsgForTarget());
        
        //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        // Person not in the room.
        processCommand(cmd,"ask Player3 about hello, hi everybody.",player1);
        assertEquals(RoomUpdateType.ERROR_MSG,roomObvOneP1.myType.elementAt(0));
        data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("That person or creature is not here.",data.getMsgForSource());
        assertEquals("",data.getMsgForOthers());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertEquals("",data.getMsgForTarget());
    }
    
    @Test
    public void TestAskCommandForgotTheAbout()
    {
        //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        String p2Name = DemoTestWorld.getPlayer2().getName();
        
        // Person in the room.
        AskCommand cmd = new AskCommand();
        processCommand(cmd,"ask "+p2Name+" hi.",player1);
        
        assertEquals(RoomUpdateType.ERROR_MSG,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("What do you want to ask \"about\"?",data.getMsgForSource());
        assertEquals("",data.getMsgForOthers());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertEquals("",data.getMsgForTarget());
    }
    
    /**
     * Test phrases say to <LifeForm Name>
     */
    @Test
    public void testAskSomeoneSometingWithNoMessage()
    {
      //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        obv2.clearUpdates();
        
        AskCommand cmd = new AskCommand();
        processCommand(cmd,"ask Demo Player2 about",player1);

        assertEquals(RoomUpdateType.ASK,roomObvOneP1.myType.elementAt(0));
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You ask Demo Player2 about ",data.getMsgForSource());
        
      //Clear out the obv updates.
        roomObvOneP1.clearUpdates();
        
        assertEquals(RoomUpdateType.ASK,obv2.myType.elementAt(0));
        data = (CommandResult)(obv2.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals(player1.getName()+" asks Demo Player2 about ",data.getMsgForOthers());
        assertEquals("Demo Player2",data.getTarget().getName());
        assertEquals(player1.getName()+" asks you about ", data.getMsgForTarget());
        assertEquals("You ask Demo Player2 about ",data.getMsgForSource());
    }
    
    @Test
    public void testAskNPCAboutQuests()
    {
        roomObvOneP1.clearUpdates();
   
        NPC npc = DemoTestWorld.getMountainQuestDude();
        
        AskCommand cmd = new AskCommand();
        processCommand(cmd,"ask "+npc.getName()+" about quests",player1);
        
        assertEquals(RoomUpdateType.ASK,roomObvOneP1.myType.elementAt(0));
        assertEquals(RoomUpdateType.SAID,roomObvOneP1.myType.elementAt(1)); // NPC should say something back to the PC
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(1));
        assertEquals(player1,data.getTarget());
        assertEquals(npc,data.getSource());
        assertEquals(npc.getName()+" says to you, \"Mountain Quest\"",data.getMsgForTarget());
        assertEquals(npc.getName()+" says to "+player1.getName()+", \"Mountain Quest\"",data.getMsgForOthers());
        assertEquals("You say to "+player1.getName()+", \"Mountain Quest\"",data.getMsgForSource());
    }
}
