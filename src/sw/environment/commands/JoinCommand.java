package sw.environment.commands;

import sw.environment.Room;
import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;
import sw.lifeform.Party;

/**
 * Will join a solo player to an existing party assuming the party is not
 * at max members.  The leader of the party being joined must accept the
 * request to join.
 * For now the two player involved must be in the same room.
 * @author cdgira
 *
 */
public class JoinCommand implements InWorldCommand
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
        
        String partyLeaderName = command.substring(4).trim();
        if (partyLeaderName.equals(player.getParty().getPartyLeader().getName()))
        {
            cr = new CommandResult(player, CommandResult.NONE, "You are already in this party.\n", "", "");
            updateType = RoomUpdateType.FAILED_JOIN_REQUEST;
        }
        else
        {
            if (player == player.getParty().getPartyLeader())
            {

                PC partyLeader = currentRoom.getPlayer(partyLeaderName);
                if (partyLeader != null)
                {
                    if (partyLeader.getParty().getPartyLeader() == partyLeader)
                    {
                        
                        if ((partyLeader.getParty().getPlayers().size() + player.getParty().getPlayers().size()) <= Party.MAX_SIZE)
                        {
                            cr = new CommandResult(player, partyLeader, "You asked to join " + partyLeaderName + "'s party.\n", player.getName() + " asked to join your party.\n", "");
                            updateType = RoomUpdateType.JOIN_REQUEST;
                            partyLeader.getParty().addJoinRequest(player);
                        }
                        else
                        {
                            cr = new CommandResult(player, CommandResult.NONE, "Unable to join the party, cannot have more than "+Party.MAX_SIZE+" members in a party.\n", "", "");
                            updateType = RoomUpdateType.FAILED_JOIN_REQUEST;
                        }
                    }
                    else
                    {
                        cr = new CommandResult(player, CommandResult.NONE, "Unable to join the party, " + partyLeader.getName() + " is not the leader.\n", "", "");
                        updateType = RoomUpdateType.FAILED_JOIN_REQUEST;
                    }
                }
                else
                {
                    cr = new CommandResult(player, CommandResult.NONE, "Unable to request joining the party, that player is not in the room.\n", "", "");
                    updateType = RoomUpdateType.FAILED_JOIN_REQUEST;
                }
            }
            else
            {
                cr = new CommandResult(player, CommandResult.NONE, "Unable to join the other party, you are not the leader of your party.\n", "", "");
                updateType = RoomUpdateType.FAILED_JOIN_REQUEST;
            }
        }
        if (cr != null)
            RoomEventTracker.getInstance().addEvent(currentRoom, cr, updateType);

    }

}
