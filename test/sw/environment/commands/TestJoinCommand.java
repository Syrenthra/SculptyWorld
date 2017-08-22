package sw.environment.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.DemoTestWorld;
import sw.environment.Exit;
import sw.environment.RoomUpdateType;
import sw.environment.TheWorld;
import sw.lifeform.PC;
import sw.lifeform.Party;

public class TestJoinCommand extends TestInWorldCommand
{
    @Test
    public void testWorksWithSoloToSolo()
    {
        PC player2 = DemoTestWorld.getPlayer2();

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+player1.getName(), player2);

        assertEquals(RoomUpdateType.JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player2, data.getSource());
        assertEquals("You asked to join "+player1.getName()+"'s party.\n", data.getMsgForSource());
        assertEquals(player2.getName() + " asked to join your party.\n", data.getMsgForTarget());
        assertEquals("",data.getMsgForOthers());
        assertEquals(player1,data.getTarget());
        assertTrue(player1.getParty().hasJoinRequest(player2.getName()));

    }
    
    @Test
    public void testCanJoinIfBothPartyLeadersAndSpace()
    {
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        PC player2 = DemoTestWorld.getPlayer2();
        player2.getParty().mergeParties(demoPlayer3.getParty());

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+player1.getName(), player2);

        assertEquals(RoomUpdateType.JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player2, data.getSource());
        assertEquals("You asked to join "+player1.getName()+"'s party.\n", data.getMsgForSource());
        assertEquals(player2.getName() + " asked to join your party.\n", data.getMsgForTarget()); // This should only be sent to Player.
        assertEquals("",data.getMsgForOthers());
        assertEquals(player1,data.getTarget());
        assertTrue(player1.getParty().hasJoinRequest(player2.getName()));
    }
    
    @Test
    public void testCantJoinIfCombinedPartyTooBig()
    {
        PC player2 = DemoTestWorld.getPlayer2();
        
        for (int x=0; x<5; x++)
        {
            int tmpID = x + 3;
            PC demoPlayer3 = new PC(tmpID, "Demo Player"+tmpID, "Demo Player"+tmpID+" for Too Many Games 2013", 100);   
            player2.getParty().mergeParties(demoPlayer3.getParty());
            player1.getCurrentRoom().addPC(demoPlayer3);
        }

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+player1.getName(), player2);

        assertEquals(RoomUpdateType.FAILED_JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player2, data.getSource());
        assertEquals("Unable to join the party, cannot have more than "+Party.MAX_SIZE+" members in a party.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers());
        assertEquals("",data.getMsgForTarget());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertFalse(player1.getParty().hasJoinRequest(player2.getName()));
    }

    @Test
    public void testCantJoinAnotherPartyIfNotPartyLeader()
    {
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        PC player2 = DemoTestWorld.getPlayer2();
        player2.getParty().mergeParties(demoPlayer3.getParty());
        player1.getCurrentRoom().addPC(demoPlayer3);

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+player1.getName(), demoPlayer3);

        assertEquals(RoomUpdateType.FAILED_JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(demoPlayer3, data.getSource());
        assertEquals("Unable to join the other party, you are not the leader of your party.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers());
        assertEquals("",data.getMsgForTarget());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertFalse(player1.getParty().hasJoinRequest(player2.getName()));
    }

    @Test
    public void testCantJoinIfPlayerNotInRoom()
    {
         // Make it so each player in a different room.
        PC player2 = DemoTestWorld.getPlayer2();
        TheWorld.getInstance().movePlayer(player1, Exit.WEST);

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();


        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+player1.getName(), player2);

        assertEquals(RoomUpdateType.FAILED_JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player2, data.getSource());
        assertEquals("Unable to request joining the party, that player is not in the room.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers()); // This should not be sent to anyone.
        assertEquals("",data.getMsgForTarget());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertFalse(player1.getParty().hasJoinRequest(player2.getName()));
    }
    
    @Test
    public void testRequestToJoinPartyWithMaxMembers()
    {
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        PC demoPlayer4 = new PC(4, "Demo Player4", "Demo Player4 for Too Many Games 2013", 100);
        PC demoPlayer5 = new PC(5, "Demo Player5", "Demo Player5 for Too Many Games 2013", 100);
        PC demoPlayer6 = new PC(6, "Demo Player6", "Demo Player6 for Too Many Games 2013", 100);
        PC player2 = DemoTestWorld.getPlayer2();
        player2.getParty().mergeParties(demoPlayer3.getParty());
        player2.getParty().mergeParties(demoPlayer4.getParty());
        player2.getParty().mergeParties(demoPlayer5.getParty());
        player2.getParty().mergeParties(demoPlayer6.getParty());

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+player2.getName(), player1);

        assertEquals(RoomUpdateType.FAILED_JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1, data.getSource());
        assertEquals("Unable to join the party, cannot have more than "+Party.MAX_SIZE+" members in a party.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers());
        assertEquals("",data.getMsgForTarget());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertFalse(player2.getParty().hasJoinRequest(player1.getName()));
    }
    
    @Test
    public void testRequestToJoinFailsIfDontAskPartyLeader()
    {
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        player1.getCurrentRoom().addPC(demoPlayer3);
        TheWorld.getInstance().addPlayer(demoPlayer3);
        PC player2 = DemoTestWorld.getPlayer2();
        player2.getParty().mergeParties(demoPlayer3.getParty());

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+demoPlayer3.getName(), player1);

        assertEquals(RoomUpdateType.FAILED_JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1, data.getSource());
        assertEquals("Unable to join the party, "+demoPlayer3.getName()+" is not the leader.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers()); 
        assertEquals("",data.getMsgForTarget());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertFalse(demoPlayer3.getParty().hasJoinRequest(player1.getName()));
    }
    
    @Test
    public void testWeCantRequestToJoinAPartyWeBelongTo()
    {
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        
        PC player2 = DemoTestWorld.getPlayer2();
        player2.getParty().mergeParties(demoPlayer3.getParty());
        player2.getCurrentRoom().addPC(demoPlayer3);

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new JoinCommand();
        processCommand(cmd,"join "+player2.getName(), demoPlayer3);

        assertEquals(RoomUpdateType.FAILED_JOIN_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(demoPlayer3, data.getSource());
        assertEquals("You are already in this party.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers());
        assertEquals("", data.getMsgForTarget());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertFalse(player1.getParty().hasJoinRequest(demoPlayer3.getName()));
    }
}
