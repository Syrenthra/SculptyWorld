package sw.environment.commands;

import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.item.Armor;
import sw.item.HandLocation;
import sw.item.Item;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;

/**
 * Allows a player to put on equipment.
 * 
 * TODO: Should other players be made away when a player is putting on equipment?
 * 
 * @author cdgira
 *
 */
public class EquipCommand implements InWorldCommand
{

    @Override
    public void processCommand(Lifeform source, String command)
    {
        PC player = null;
        if (source instanceof PC)
            player = (PC)source;
        else
            return; // Not a PC.
        
        String itemName = command.substring(6);
        CommandResult cr = null;
        Item item = player.getContentsInHand(HandLocation.RIGHT);
        if ((item != null) && (item.getName().equals(itemName)))
        {
            player.wearArmor((Armor)item);
            player.dropFromHand(HandLocation.RIGHT);
            cr = new CommandResult(player,"You put on the "+itemName+".",player.getName()+" puts on the "+itemName+".");
        }
        else
        {
            item = player.getContentsInHand(HandLocation.LEFT);
            if ((item != null) && (item.getName().equals(itemName)))
            {
                player.wearArmor((Armor)item);
                player.dropFromHand(HandLocation.LEFT);
                cr = new CommandResult(player,"You put on the "+itemName+".",player.getName()+" puts on the "+itemName+".");
            }
        }
        if (cr != null)
        {
            RoomEventTracker.getInstance().addEvent(player.getCurrentRoom(),cr, RoomUpdateType.WEAR_ITEM);   
        }
    }

}
