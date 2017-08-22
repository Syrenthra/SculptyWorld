package sw.environment.commands;

import static org.junit.Assert.assertEquals;


import mock.MockSWServerConnection;

import org.junit.Test;

import sw.DemoTestWorld;
import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.RoomUpdateType;
import sw.environment.TheWorld;
import sw.net.SWServerConnection;
import sw.net.state.InWorldState;

public class TestMoveCommand extends TestInWorldCommand
{    
    
    @Test
    public void testMoveWestAndEast()
    {
        SWServerConnection sc2 = new MockSWServerConnection(null,null,null,null);
        InWorldState iws= new InWorldState(sc2,1,player1);
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
        Room westRoom = player1.getCurrentRoom().getExit(Exit.WEST);
        Room eastRoom = player1.getCurrentRoom();
        
        InWorldCommand cmd = new MoveCommand(iws);
        processCommand(cmd,"west", player1);
        
        assertEquals(RoomUpdateType.MOVE,roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You walked to the west.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walked to the west.\n",data.getMsgForOthers());
        // Test output for new room
        data = (CommandResult)(roomObvTwoP1.src.elementAt(1));
        assertEquals(player1,data.getSource());
        assertEquals(player1.getCurrentRoom().toString()+"\nYou walk in from the east.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walks in from the east.\n",data.getMsgForOthers());   
        assertEquals(westRoom,player1.getCurrentRoom());
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
        processCommand(cmd,"east", player1);
        
        assertEquals(RoomUpdateType.MOVE,roomObvTwoP1.myType.elementAt(0));
        // Test output for old room
        data = (CommandResult)(roomObvTwoP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You walked to the east.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walked to the east.\n",data.getMsgForOthers());
        // Test output for new room
        data = (CommandResult)(roomObvOneP1.src.elementAt(1));
        assertEquals(player1,data.getSource());
        assertEquals(player1.getCurrentRoom().toString()+"\nYou walk in from the west.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walks in from the west.\n",data.getMsgForOthers());
        
        assertEquals(eastRoom,player1.getCurrentRoom());
    }
    
    @Test
    public void testFailedToMoveEastAndWest()
    {
        SWServerConnection sc2 = new MockSWServerConnection(null,null,null,null);
        InWorldState iws= new InWorldState(sc2,1,player1);
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
        MoveCommand cmd = new MoveCommand(iws);
        processCommand(cmd,"east", player1);
        assertEquals(RoomUpdateType.MOVE_FAILED,roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("Unable to go east, no exit.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" tried to go east and failed.\n",data.getMsgForOthers());
        assertEquals(7,player1.getCurrentRoom().getID());
    }
    
    @Test
    public void testMoveNorthAndSouth()
    {   // We need to reposition the observers
        player1.getCurrentRoom().removeRoomObserver(roomObvOneP1);
        player1.getCurrentRoom().getExit(Exit.WEST).removeRoomObserver(roomObvTwoP1);
        DemoTestWorld.getPlayer2().getCurrentRoom().removeRoomObserver(obv2);
        
        // Move the player into Room3.
        TheWorld.getInstance().movePlayer(player1, Exit.WEST);
        TheWorld.getInstance().movePlayer(player1, Exit.WEST);
        TheWorld.getInstance().movePlayer(player1, Exit.WEST);
        TheWorld.getInstance().movePlayer(player1, Exit.WEST);
        
        // Move player2 into Room3
        
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        
        // Now put the observers back in place for testing
        player1.getCurrentRoom().addRoomObserver(roomObvOneP1);
        player1.getCurrentRoom().getExit(Exit.NORTH).addRoomObserver(roomObvTwoP1);
        DemoTestWorld.getPlayer2().getCurrentRoom().addRoomObserver(obv2);
        
        SWServerConnection sc2 = new MockSWServerConnection(null,null,null,null);
        InWorldState iws= new InWorldState(sc2,1,player1);
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
        // First try a fail move to the south
        MoveCommand cmd = new MoveCommand(iws);
        processCommand(cmd,"south", player1);
        assertEquals(RoomUpdateType.MOVE_FAILED,roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        CommandResult data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("Unable to go south, no exit.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" tried to go south and failed.\n",data.getMsgForOthers());
        assertEquals(3,player1.getCurrentRoom().getID());
        
        Room northRoom = player1.getCurrentRoom().getExit(Exit.NORTH);
        Room southRoom = player1.getCurrentRoom();
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
        // Now try success North
        processCommand(cmd,"north", player1);
        
        assertEquals(RoomUpdateType.MOVE,roomObvOneP1.myType.elementAt(0));
        // Test output for old room
        data = (CommandResult)(roomObvOneP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You walked to the north.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walked to the north.\n",data.getMsgForOthers());
        // Test output for new room
        data = (CommandResult)(roomObvTwoP1.src.elementAt(1));
        assertEquals(player1,data.getSource());
        assertEquals(player1.getCurrentRoom().toString()+"\nYou walk in from the south.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walks in from the south.\n",data.getMsgForOthers());   
        assertEquals(northRoom,player1.getCurrentRoom());
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
     // Now try fail move North
        processCommand(cmd,"north", player1);
        assertEquals(RoomUpdateType.MOVE_FAILED,roomObvTwoP1.myType.elementAt(0));
        // Test output for old room
        data = (CommandResult)(roomObvTwoP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("Unable to go north, no exit.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" tried to go north and failed.\n",data.getMsgForOthers());
        assertEquals(8,player1.getCurrentRoom().getID());
        
        roomObvOneP1.clearUpdates();
        roomObvTwoP1.clearUpdates();
        
        // Now success south
        processCommand(cmd,"south", player1);
        
        assertEquals(RoomUpdateType.MOVE,roomObvTwoP1.myType.elementAt(0));
        // Test output for old room
        data = (CommandResult)(roomObvTwoP1.src.elementAt(0));
        assertEquals(player1,data.getSource());
        assertEquals("You walked to the south.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walked to the south.\n",data.getMsgForOthers());
        // Test output for new room
        data = (CommandResult)(roomObvOneP1.src.elementAt(1));
        assertEquals(player1,data.getSource());
        assertEquals(player1.getCurrentRoom().toString()+"\nYou walk in from the north.\n",data.getMsgForSource());
        assertEquals(player1.getName()+" walks in from the north.\n",data.getMsgForOthers());
        
        assertEquals(southRoom,player1.getCurrentRoom());
    }

}