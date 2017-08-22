package sw.quest.task;

import sw.lifeform.PC;
import sw.lifeform.PCEvent;

public interface QuestTask
{
    /**
     * The quest will inform the task of what things the player is up to using
     * this method.  The quest observes the player and forwards all events to the task.
     * @param event
     */
    public void processPCEvent(PCEvent event);
    
    /**
     * Returns what percent the task was completed by the player (0-100).
     * @param player
     * @return
     */
    public int percentComplete(PC player);
    
    /**
     * Returns what percent overall the task has been completed by those
     * working on it.
     * @return
     */
    public int overallPercentComplete();
    
    /**
     * Add a player to this task.  This is called by the quest when the player is added
     * to the quest.
     * @param player
     */
    public void addPlayer(PC player);
    
    /**
     * Removes this player from the task.  This is called by the quest when the player is
     * removed from the quest.
     * @param player
     */
    public void removePlayer(PC player);
    
    /**
     * What type of task this is.
     * @return
     */
    public TaskType getType();
    
    /**
     * Let's the task know the quest state for a player changed.
     */
    public void questStateUpdate(PC player);

}
