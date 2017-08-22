package sw.environment.commands;

import java.util.Enumeration;

import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;

/**
 * Allows a Player to talk to other players in the same room.
 * @author cdgira
 *
 */
public class SayCommand implements InWorldCommand
{

    /**
     * Doesn't return anything as it relies on the room observer pattern to
     * pass on the information.
     */
    @Override
    public void processCommand(Lifeform source, String command)
    {
        CommandResult data = null;
        RoomUpdateType updateType = RoomUpdateType.SAID;

        // Remove the say
        String text = command.substring(4).trim();

        if (text.substring(0, 2).equals("to"))
        {
            text = text.substring(2).trim(); // Remove the "to"

            Lifeform target = confirmName(source, text);

            if (target != null)
            {

                text = text.substring(target.getName().length()).trim(); // Remove the name of the target.
                String name = target.getName();
                data = new CommandResult(source, target, "You say to " + name + ", \"" + text + "\"", source.getName() + " says to you, \"" + text + "\"", source.getName() + " says to " + name + ", \"" + text + "\"");
            }
            else
            {
                data = new CommandResult(source, CommandResult.NONE, "That person or creature is not here.", "", "");
                updateType = RoomUpdateType.ERROR_MSG;
            }
        }
        else
        {
            data = new CommandResult(source, "You say, \"" + text + "\"", source.getName() + " says, \"" + text + "\"");
        }

        if (data != null)
            RoomEventTracker.getInstance().addEvent(source.getCurrentRoom(), data, updateType);
    }

    /**
     * Compares the name of each Lifeform in the room to the beginning characters in
     * the message being said.
     * @param source
     * @param text
     * @return
     */
    private Lifeform confirmName(Lifeform source, String text)
    {
        Lifeform entity = null;
        for (Lifeform tmpEntity : source.getCurrentRoom().getNPCs())
        {
            if (tmpEntity.getName().length() <= text.length())
            {
                String name = text.substring(0, tmpEntity.getName().length());
                if (name.equals(tmpEntity.getName()))
                {
                    entity = tmpEntity;
                    break;
                }
            }
        }
        if (entity == null)
        {
            Enumeration<PC> ePC = source.getCurrentRoom().getPCs().elements();
            while ((ePC.hasMoreElements()) && (entity == null))
            {
                Lifeform tmpEntity = ePC.nextElement();
                if (tmpEntity.getName().length() <= text.length())
                {
                    String name = text.substring(0, tmpEntity.getName().length());

                    if (name.equals(tmpEntity.getName()))
                    {
                        entity = tmpEntity;
                    }
                }
            }

            if (entity == null)
            {
                for (Lifeform tmpEntity : source.getCurrentRoom().getCreatures())
                {
                    if (tmpEntity.getName().length() <= text.length())
                    {
                        String name = text.substring(0, tmpEntity.getName().length());
                        if (name.equals(tmpEntity.getName()))
                        {
                            entity = tmpEntity;
                            break;
                        }
                    }
                }
            }
        }
        return entity;
    }

}
