package sw.environment;

/**
 * Used by classes that create new creatures.
 * @author cdgira
 *
 */
public interface Spawn
{
    /**
     * Add an observer to the Spawner.
     * @param observer
     */
    public void addSpawnObserver(SpawnObserver observer);
    
    /**
     * Remove an observer from the Spawner.
     * @param observer
     */
    public void removeSpawnObserver(SpawnObserver observer);
    
    /**
     * Spawn a new creature.
     */
    public void spawn();

}
