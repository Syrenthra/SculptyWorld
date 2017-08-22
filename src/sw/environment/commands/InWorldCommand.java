package sw.environment.commands;

import sw.lifeform.Lifeform;

/**
 * TODO: What do we do for messages if two players attack each other (there would be a msg for each player in the fight and those watching the fight).
 * @author cdgira
 *
 */
public interface InWorldCommand
{   
    public void processCommand(Lifeform source, String command);

}
