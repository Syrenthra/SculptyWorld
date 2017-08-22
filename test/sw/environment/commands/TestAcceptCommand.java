package sw.environment.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.DemoTestWorld;
import sw.environment.RoomUpdateType;
import sw.lifeform.PC;
import sw.lifeform.Party;


public class TestAcceptCommand extends TestInWorldCommand
{
    
    @Test
    public void testAcceptRequestFailedForPlayerNoJoinSent()
    {
        PC player2 = DemoTestWorld.getPlayer2();

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new AcceptCommand();
        processCommand(cmd,"accept "+player2.getName(), player1);

        assertEquals(RoomUpdateType.FAILED_ACCEPT_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1.getName(), data.getSource().getName());
        assertEquals("Accept failed, "+player2.getName()+" did not ask to join your party.\n", data.getMsgForSource());
        assertEquals("", data.getMsgForOthers()); // This should only be sent to the person who joined.
        assertTrue(player2.getParty() != player1.getParty());
        assertEquals(player1,player1.getParty().getPartyLeader());
        assertEquals(CommandResult.NONE,data.getTarget());
        assertEquals("",data.getMsgForTarget());
    }
    
    @Test
    public void testAcceptRequestFromPlayerInSameRoom()
    {
        PC player2 = DemoTestWorld.getPlayer2();
        // Prep here for previously executed join command.
        player1.getParty().addJoinRequest(player2);
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new AcceptCommand();
        processCommand(cmd,"accept "+player2.getName(), player1);

        assertEquals(RoomUpdateType.ACCEPT_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1.getName(), data.getSource().getName());
        assertEquals(player2.getName()+" has joined your party.\n", data.getMsgForSource());
        assertEquals(player2.getName()+" has joined the party.\n", data.getMsgForOthers());  // This should only be sent to other players in the party.
        assertEquals("You have joined "+player1.getName()+"'s party.\n", data.getMsgForTarget());
        assertEquals(player2.getParty(),player1.getParty());
        assertEquals(player1,player1.getParty().getPartyLeader());
        assertFalse(player1.getParty().hasJoinRequest(player2.getName()));
        assertEquals(player2,data.getTarget());
    }
    
    @Test
    public void testAcceptRequestForPartiesLargerThanOne()
    {
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        PC player2 = DemoTestWorld.getPlayer2();
        
        player2.getParty().mergeParties(demoPlayer3.getParty());  // After the join request we added to our party.
        // Prep here for previously executed join command.
        player1.getParty().addJoinRequest(player2);
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new AcceptCommand();
        processCommand(cmd,"accept "+player2.getName(), player1);

        assertEquals(RoomUpdateType.ACCEPT_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1.getName(), data.getSource().getName());
        assertEquals(player2.getName()+" has joined your party.\n", data.getMsgForSource());
        assertEquals(player2.getName()+" has joined the party.\n", data.getMsgForOthers());  // This should only be sent to other players in the party.
        assertEquals("You have joined "+player1.getName()+"'s party.\n", data.getMsgForTarget()); // This should only be sent to the person who joined.
        assertTrue(player2.getParty() == player1.getParty());
        assertEquals(player1,player1.getParty().getPartyLeader());
        assertFalse(player1.getParty().hasJoinRequest(player2.getName()));
        assertEquals(player2,data.getTarget());
    }
    
    @Test
    public void testAcceptRequestAfterRequesterBecamePartOfAnotherPartyBeforeAccept()
    {
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        PC player2 = DemoTestWorld.getPlayer2();
        // Prep here for previously executed join command.
        player1.getParty().addJoinRequest(player2);
     // After the join request we joined another party.
        demoPlayer3.getParty().mergeParties(player2.getParty());  
        
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new AcceptCommand();
        processCommand(cmd,"accept "+player2.getName(), player1);

        assertEquals(RoomUpdateType.FAILED_ACCEPT_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1.getName(), data.getSource().getName());
        assertEquals(player2.getName()+" joined a different party.\n", data.getMsgForSource());
        assertEquals("",data.getMsgForOthers());  // No one else in the party need know the accept failed.
        assertEquals("You failed to join "+player1.getName()+"'s party.\n", data.getMsgForTarget()); // This should only be sent to the person who joined.
        assertTrue(player2.getParty() != player1.getParty());
        assertEquals(player1,player1.getParty().getPartyLeader());
        assertFalse(player1.getParty().hasJoinRequest(player2.getName()));
        assertEquals(player2,data.getTarget());
    }
    
    /**
     * Parties were fine on initial request, but one party grew since then.
     */
    @Test
    public void testCantAcceptIfCombinedPartyTooBig()
    {
        PC player2 = DemoTestWorld.getPlayer2();
        
        player1.getParty().addJoinRequest(player2);
        
        // Grow Player2's party to be too big.
        for (int x=0; x<5; x++)
        {
            int tmpID = x + 3;
            PC demoPlayer3 = new PC(tmpID, "Demo Player"+tmpID, "Demo Player"+tmpID+" for Too Many Games 2013", 100);   
            player2.getParty().mergeParties(demoPlayer3.getParty());
            player1.getCurrentRoom().addPC(demoPlayer3);
        }

        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();

        InWorldCommand cmd = new AcceptCommand();
        processCommand(cmd,"accept "+player2.getName(), player1);

        assertEquals(RoomUpdateType.FAILED_ACCEPT_REQUEST, roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult) (roomObvOneP1.src.elementAt(0));
        assertEquals(player1.getName(), data.getSource().getName());
        assertEquals("Unable to join the party, cannot have more than "+Party.MAX_SIZE+" members in a party.\n", data.getMsgForTarget());
        assertEquals("Unable to merge the parties, cannot have more than "+Party.MAX_SIZE+" members in a party.\n", data.getMsgForSource());
        assertEquals(player2,data.getTarget());
        assertFalse(player1.getParty().hasJoinRequest(player2.getName()));
    }

}
