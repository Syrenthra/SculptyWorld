package sw.time;

/**
 * @author David Jones
 * This is an interface that all observers of Timer must follow
 */
public interface TimeObserver
{
    /**
     * Updates the time for the observer so any time dependent
     * methods can also update
     * @param time the new time given from the subject
     */
    public void updateTime(String name, int time);
}
