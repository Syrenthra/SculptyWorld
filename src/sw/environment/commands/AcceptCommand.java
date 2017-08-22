package sw.environment.commands;

import sw.environment.Room;
import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;
import sw.lifeform.Party;

public class AcceptCommand implements InWorldCommand
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
        RoomUpdateType updateType = RoomUpdateType.ACCEPT_REQUEST;

        String newMemberName = command.substring(6).trim();
        Party myParty = player.getParty();
        if (myParty.hasJoinRequest(newMemberName))
        {

            PC requester = myParty.getJoinRequest(newMemberName);
            if (requester.getParty().getPartyLeader() == requester)
            {
                if (requester.getParty().getPlayers().size() + myParty.getPlayers().size() <= Party.MAX_SIZE)
                {
                    myParty.mergeParties(requester.getParty());
                    myParty.removeJoinRequest(requester);

                    cr = new CommandResult(player, requester, newMemberName + " has joined your party.\n", "You have joined " + player.getName() + "'s party.\n", newMemberName + " has joined the party.\n");
                    updateType = RoomUpdateType.ACCEPT_REQUEST;
                }
                else
                {
                    cr = new CommandResult(player, requester, "Unable to merge the parties, cannot have more than "+Party.MAX_SIZE+" members in a party.\n", "Unable to join the party, cannot have more than "+Party.MAX_SIZE+" members in a party.\n", "");
                    updateType = RoomUpdateType.FAILED_ACCEPT_REQUEST;
                    myParty.removeJoinRequest(requester);
                }
            }
            else
            {
                cr = new CommandResult(player, requester, newMemberName + " joined a different party.\n", "You failed to join " + player.getName() + "'s party.\n", "");
                updateType =  RoomUpdateType.FAILED_ACCEPT_REQUEST;
                myParty.removeJoinRequest(requester);  
            }
        }
        else
        {
            cr = new CommandResult(player, CommandResult.NONE, "Accept failed, " + newMemberName + " did not ask to join your party.\n", "", "");
            updateType = RoomUpdateType.FAILED_ACCEPT_REQUEST;
        }
        if (cr != null)
            RoomEventTracker.getInstance().addEvent(currentRoom, cr, updateType );

    }

}
