package sw.environment.commands;

import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.environment.TheWorld;
import sw.lifeform.Creature;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;
import sw.net.state.InWorldState;

/**
 * TODO: Duplicate code to be refactored.
 * TODO: What to do about extra text that may be after the move (e.g. "north blah blah") 
 * @author cdgira
 *
 */
public class MoveCommand implements InWorldCommand
{
    private InWorldState inWorldState;
    
    public MoveCommand(InWorldState state)
    {
        inWorldState = state;
    }
    
    public void processCommand(Lifeform source, String command)
    {
        String directionLeft = "";
        String directionArrived = "";
        RoomUpdateType updateType = null;
        Exit exitTook = null;
        Room initialRoom = source.getCurrentRoom();
        TheWorld world = TheWorld.getInstance();
        // TODO: Need to setup a notice before the player moves and after the player moves.
        CommandResult cr = null;
        if (command.equals("west"))
        {
            directionLeft = "west";
            
            if (initialRoom.getExit(Exit.WEST) != null)
            {
                directionArrived = "east";
                updateType = RoomUpdateType.MOVE;
                exitTook = Exit.WEST;
            }
            else
            {
                updateType = RoomUpdateType.MOVE_FAILED;
            }
        }
        else if (command.equals("east"))
        {
            directionLeft = "east";
            
            if (initialRoom.getExit(Exit.EAST) != null)
            {
                directionArrived = "west";
                updateType = RoomUpdateType.MOVE;
                exitTook = Exit.EAST;
            }
            else
            {
                updateType = RoomUpdateType.MOVE_FAILED;
            }
            
        }
        else if (command.equals("north"))
        {
            directionLeft = "north";
            
            if (initialRoom.getExit(Exit.NORTH) != null)
            {
                directionArrived = "south";
                updateType = RoomUpdateType.MOVE;
                exitTook = Exit.NORTH;
            }
            else
            {
                updateType = RoomUpdateType.MOVE_FAILED;
            }
            
        }
        else if (command.equals("south"))
        {
            directionLeft = "south";
            
            if (initialRoom.getExit(Exit.SOUTH) != null)
            {          
                directionArrived = "north";
                updateType = RoomUpdateType.MOVE;
                exitTook = Exit.SOUTH;
            }
            else
            {
                updateType = RoomUpdateType.MOVE_FAILED;
            }
            
        }
        
        if (updateType == RoomUpdateType.MOVE)
        {
            cr = new CommandResult(source,"You walked to the "+directionLeft+".\n",source.getName()+" walked to the "+directionLeft+".\n");
            RoomEventTracker.getInstance().addEvent(initialRoom,cr, updateType);
        
            if (source instanceof PC)
                world.movePlayer((PC)source, exitTook);
            else if (source instanceof Creature)
                world.moveCreature((Creature)source, exitTook);
            source.getCurrentRoom().addRoomObserver(inWorldState);
        
            cr = new CommandResult(source,source.getCurrentRoom().toString()+"\nYou walk in from the "+directionArrived+".\n",source.getName()+" walks in from the "+directionArrived+".\n");
            RoomEventTracker.getInstance().addEvent(source.getCurrentRoom(),cr, updateType);
        }
        else if (updateType == RoomUpdateType.MOVE_FAILED)
        {
            cr = new CommandResult(source,"Unable to go "+command+", no exit.\n",source.getName()+" tried to go "+directionLeft+" and failed.\n");
            RoomEventTracker.getInstance().addEvent(source.getCurrentRoom(),cr, updateType);
        }
    }

}
