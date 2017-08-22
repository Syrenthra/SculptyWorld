package sw.environment.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.DemoTestWorld;
import sw.environment.RoomUpdateType;
import sw.lifeform.PC;


public class TestLeaveCommand extends TestInWorldCommand
{
    
    @Test
    public void testCanLeaveAParty()
    {
        PC player2 = DemoTestWorld.getPlayer2();
        player1.getParty().mergeParties(player2.getParty());

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new LeaveCommand();
        processCommand(cmd,"leave", player2);

        assertEquals(RoomUpdateType.LEAVE_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player2, data.getSource());
        assertEquals("You left "+player1.getName()+"'s party.\n", data.getMsgForSource());
        assertEquals(player2.getName() + " has left your party.\n", data.getMsgForOthers()); // This should only be sent to the other players in the party just left.
        assertTrue(player1.getParty() != player2.getParty());
        assertEquals(player1,data.getTarget());
    }
    
    @Test
    public void testPartyLeaderCanLeaveAParty()
    {
        PC player2 = DemoTestWorld.getPlayer2();
        player1.getParty().mergeParties(player2.getParty());

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new LeaveCommand();
        processCommand(cmd,"leave", player1);

        assertEquals(RoomUpdateType.LEAVE_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1, data.getSource());
        assertEquals("You left your party.\n", data.getMsgForSource());
        assertEquals(player1.getName() + " has left the party, "+player2.getName()+" is now the party leader.\n", data.getMsgForOthers()); // This should only be sent to the other player's in the party.
        assertEquals(player1.getName() + " has left the party, you are now the party leader.\n", data.getMsgForTarget());
        assertTrue(player1.getParty() != player2.getParty());
        assertEquals(player2,data.getTarget());
    }
    
    @Test
    public void testCantLeaveASoloParty()
    {
        PC player2 = DemoTestWorld.getPlayer2();

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new LeaveCommand();
        processCommand(cmd,"leave", player2);

        assertEquals(RoomUpdateType.FAILED_LEAVE_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player2, data.getSource());
        assertEquals("You can't leave a solo party.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers());
        assertEquals(CommandResult.NONE,data.getTarget());
    }

}
