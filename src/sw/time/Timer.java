package sw.time;

/**
 * @author David Jones
 * This is an interface to creating timers, otherwise
 * known as the subject in this observer pattern
 */
public interface Timer
{
    /**
     * Adds another observer for Timer to update
     * @param observer the observer to be added
     */
    public void addTimeObserver(TimeObserver observer);
    
    /**
     * removes an observer from the update list
     * @param observer the observer to be removed
     */
    public void removeTimeObserver(TimeObserver observer);
    
    /**
     * updates all observers in the update list, saying
     * that time has updated
     */
    public void timeChanged();
}
