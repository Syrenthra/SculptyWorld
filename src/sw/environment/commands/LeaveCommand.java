package sw.environment.commands;

import sw.environment.Room;
import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;
import sw.lifeform.Party;

public class LeaveCommand implements InWorldCommand
{

    @Override
    public void processCommand(Lifeform source, String command)
    {
        PC player = null;
        if (source instanceof PC)
            player = (PC)source;
        else
            return; // Not a PC.
        
        Room currentRoom = player.getCurrentRoom();
        CommandResult cr = null;
        RoomUpdateType updateType = null;
        PC partyLeader = player.getParty().getPartyLeader();

        if (player.getParty().getPlayers().size() > 1)
        {
            Party oldParty = player.getParty();
            player.leaveParty();
            if (partyLeader == player)
            {   
                cr = new CommandResult(player, oldParty.getPartyLeader(), "You left your party.\n", player.getName() + " has left the party, you are now the party leader.\n", player.getName() + " has left the party, "+oldParty.getPartyLeader().getName()+" is now the party leader.\n");
                updateType = RoomUpdateType.LEAVE_REQUEST;
            }
            else
            {
                cr = new CommandResult(player, partyLeader, "You left " + partyLeader.getName() + "'s party.\n", "", player.getName() + " has left your party.\n");
                updateType = RoomUpdateType.LEAVE_REQUEST;
            }
        }
        else
        {
            cr = new CommandResult(player, CommandResult.NONE, "You can't leave a solo party.\n", "", "");
            updateType = RoomUpdateType.FAILED_LEAVE_REQUEST;
        }
        
        if (cr != null)
            RoomEventTracker.getInstance().addEvent(currentRoom, cr, updateType);

    }

}
