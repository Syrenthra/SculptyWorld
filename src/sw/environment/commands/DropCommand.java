package sw.environment.commands;

import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.item.HandLocation;
import sw.item.Item;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;

/**
 * A command to get a player to drop an item he or she is holding.
 * @author cdgira
 *
 */
public class DropCommand implements InWorldCommand
{

    /**
     * If the player is holding the item requested to drop, then the player will
     * drop that item in his or her present room.  Other players are notified that
     * this has occurred.
     * TODO: Refactor the duplicate code.
     */
    @Override
    public void processCommand(Lifeform source, String command)
    {
        PC player = null;
        if (source instanceof PC)
            player = (PC)source;
        else
            return; // Not a PC.
        
        CommandResult cr = null;
        // trim off the drop
        String itemName = command.substring(5);
        
        Item item = player.getHeldItem(HandLocation.RIGHT);
        if ((item !=null) && (item.getName().equals(itemName)))
        {
            player.dropFromHand(HandLocation.RIGHT);
            player.getCurrentRoom().addItem(item);
            cr = new CommandResult(player,"You dropped the "+itemName+" to the ground.",player.getName()+" dropped the "+itemName+" to the ground.");
        }
        else
        {
            item = player.getHeldItem(HandLocation.LEFT);
            if ((item != null) && (item.getName().equals(itemName)))
            {
                player.dropFromHand(HandLocation.LEFT);
                player.getCurrentRoom().addItem(item);
                cr = new CommandResult(player,"You dropped the "+itemName+" to the ground.",player.getName()+" dropped the "+itemName+" to the ground.");
            }
            else
            {
                // TODO Turn this into and ERROR_MSG perhaps?
                cr = new CommandResult(player,"You are not holding any "+itemName+", no item dropped.","");
            }
        }
         
        RoomEventTracker.getInstance().addEvent(player.getCurrentRoom(),cr, RoomUpdateType.ITEM_DROPPED);
    }

}
