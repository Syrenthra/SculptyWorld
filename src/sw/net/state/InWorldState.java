package sw.net.state;

import sw.environment.CreatureResource;
import sw.environment.Exit;
import sw.environment.IdGen;
import sw.environment.Room;
import sw.environment.TheWorld;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.Player;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;
import sw.quest.CreatureQuest;
import sw.time.GameTimer;

/**
 * Once a player has joined the game they are in this state until they quit the game.
 * 
 * TODO: At present just supports a demo player for Too Many Games.
 * 
 * @author cdgira
 *
 */
public class InWorldState extends ServerConnectionState
{
    public InWorldState(SWServerConnection connection)
    {
        m_connection = connection;
    }

    /**
     * TODO: When we move to the full system will redo this with a command pattern.
     */
    @Override
    public void executeAction(SWMessage msg)
    {
        TheWorld world = TheWorld.getInstance();
        Player demoPlayer = world.getPlayer(1);
        
        if (msg.getMessage().equals("west"))
        {
            world.movePlayer(demoPlayer, Exit.WEST);
        }
        else if (msg.getMessage().equals("east"))
        {
            world.movePlayer(demoPlayer, Exit.EAST);
        }
        else if (msg.getMessage().equals("quests"))
        {
            SWMessage reply = null;
            Room room = world.getPlayer(1).getCurrentRoom();
            if ((room.getNumNPCs() > 0) && (room.getNPCs()[0].isQuestActive()))
                reply = new SWMessage(room.getNPCs()[0].getQuest().getName());
            else
                reply = new SWMessage("None");
            m_connection.sendMessage(reply); 
        }
        else if (msg.getMessage().equals("clear creatures"))
        {
            for (int x=1;x<8;x++)
            {
                Room room = world.getRoom(x);
                while (room.getNumCreatures() > 0)
                    room.removeCreature(room.getCreatures()[0].getID());
            }
        }

    }

    @Override
    public SWMessage getMessage()
    {
        TheWorld world = TheWorld.getInstance();
        Room room = world.getPlayer(1).getCurrentRoom();
        String roomInfo = room.getTitle()+"\n"+room.getDescription()+"\nExits: ";
        if (room.getExit(Exit.EAST) != null)
            roomInfo = roomInfo + "East ";
        if (room.getExit(Exit.WEST) != null)
            roomInfo = roomInfo + "West";
        roomInfo = roomInfo + "\nCreatures: ";
        if (room.getNumCreatures() == 0)
            roomInfo = roomInfo + "None";
        else 
            for (int x=0;x<room.getNumCreatures();x++)
                roomInfo = roomInfo + room.getCreatures()[x].getName()+" ";
        
        roomInfo = roomInfo + "\nNPCs: ";
        if (room.getNumNPCs() == 0)
            roomInfo = roomInfo + "None";
        else
            roomInfo = roomInfo + room.getNPCs()[0].getName();
        SWMessage msg = new SWMessage(roomInfo);
        return msg;
    }

}
