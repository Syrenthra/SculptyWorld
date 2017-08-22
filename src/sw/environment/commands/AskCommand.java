package sw.environment.commands;

import java.util.Enumeration;

import sw.environment.RoomEventTracker;
import sw.environment.RoomUpdateType;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;

public class AskCommand implements InWorldCommand
{

    /**
     * Doesn't return anything as it relies on the room observer pattern to
     * pass on the information.
     */
    @Override
    public void processCommand(Lifeform source, String command)
    {
        CommandResult data = null;
        RoomUpdateType updateType = RoomUpdateType.ASK;

        // Remove the say
        String text = command.substring(3).trim();


            Lifeform target = confirmName(source, text);

            if (target != null)
            {

                text = text.substring(target.getName().length()).trim(); // Remove the name of the target.
                                
                String name = target.getName();
                
                if ((text.length() > 4) && (text.substring(0, 5).equals("about")))
                {
                    text = text.substring(5).trim(); // Remove the "about"

                    data = new CommandResult(source, target, "You ask " + name + " about " + text, source.getName() + " asks you about " + text, source.getName() + " asks " + name + " about " + text);
                }
                else
                {
                    data = new CommandResult(source, CommandResult.NONE, "What do you want to ask \"about\"?", "", "");
                    updateType = RoomUpdateType.ERROR_MSG;
                }
            }
            else
            {
                data = new CommandResult(source, CommandResult.NONE, "That person or creature is not here.", "", "");
                updateType = RoomUpdateType.ERROR_MSG;
            }

        if (data != null)
            RoomEventTracker.getInstance().addEvent(source.getCurrentRoom(),data, updateType);
    }

    /**
     * Compares the name of each Lifeform in the room to the beginning characters in
     * the message being said.
     * 
     * TODO Duplicate cade taken from SayCommand
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
