package sw.environment;

import sw.lifeform.Creature;

/**
 * For those objects that want to be notified of new creatures being spawned
 * by Resource nodes.
 * @author cdgira
 *
 */
public interface SpawnObserver
{
    /**
     * Informs of the creature just created.
     * @param spawn
     */
    public void spawnUpdate(CreatureResource source, Creature spawn);

}
