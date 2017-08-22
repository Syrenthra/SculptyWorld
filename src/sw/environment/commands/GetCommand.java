package sw.environment.commands;

import sw.environment.Room;
import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.item.HandLocation;
import sw.item.Item;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;

/**
 * Allows players to pick up items from the ground.
 * 
 * TODO: Should other players be able to see when a player picks up an item from the ground?
 * 
 * @author cdgira
 *
 */
public class GetCommand implements InWorldCommand
{

    @Override
    public void processCommand(Lifeform source, String command)
    {
        PC player = null;
        if (source instanceof PC)
            player = (PC)source;
        else
            return; // Not a PC.
        
        String itemName = command.substring(4);
        String playerMsg = "Failed to get "+itemName+", your hands are full.";
        String otherMsg = player.getName()+" tries to get "+itemName+", but his hands are full.";
        
        Room room = player.getCurrentRoom();
        Item item = room.getItem(itemName);
        if (player.holdInHand(item, HandLocation.RIGHT))
        {
            playerMsg =  "You got the "+itemName+", holding in your right hand.";
            otherMsg = player.getName()+" got the "+itemName+", holding in his right hand.";
        }
        else if (player.holdInHand(item, HandLocation.LEFT))
        {
            playerMsg = "You got the "+itemName+", holding in your left hand.";
            otherMsg = player.getName()+" got the "+itemName+", holding in his left hand.";
        }

        CommandResult cr = new CommandResult(player,playerMsg,otherMsg);
        RoomEventTracker.getInstance().addEvent(player.getCurrentRoom(),cr, RoomUpdateType.GET_ITEM);
    }

}
