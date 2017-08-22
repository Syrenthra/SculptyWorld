package sw.environment.commands;

import sw.lifeform.Lifeform;
import sw.lifeform.PC;
import sw.net.state.InWorldState;

/**
 * Gathers the name (and eventually other information) of the players in 
 * this player's party.  Will always put the player's name first.
 * @author cdgira
 *
 */
public class PartyCommand implements InWorldCommand
{
    private InWorldState inWorldState;
    
    public PartyCommand(InWorldState state)
    {
        inWorldState = state;
    }

    @Override
    public void processCommand(Lifeform source, String command)
    {
        PC player = null;
        if (source instanceof PC)
            player = (PC)source;
        else
            return; // Not a PC.
        
        StringBuffer partyInfo = new StringBuffer(player.getName()+"\n");
        
        for (PC partyMember : player.getParty().getPlayers())
        {
            if (partyMember != player)
                partyInfo.append(partyMember.getName()+"\n");
        }
        
        inWorldState.setMessage(partyInfo.toString());
        
    }

}
