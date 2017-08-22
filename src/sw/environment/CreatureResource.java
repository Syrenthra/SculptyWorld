package sw.environment;

import java.util.Vector;

import sw.lifeform.Creature;
import sw.time.TimeObserver;

/**
 * Resources recovery at a specific rate, with a certain maximum. As
 * resources decline this can affect respawn rates of creatures.
 * 
 * TODO: Add a variable that determines the chance for a created creature to also be able to place a new Creature Resource.
 * 
 * @author cdgira
 *
 */
public class CreatureResource implements TimeObserver, Spawn
{
    /**
     * The creature type this resource spawns.
     */
    private Creature m_creature;

    /**
     * Max number of creatures this resource can save up to.
     */
    private int m_maxAmount;

    /**
     * How many creatures the resource has to spawn.
     */
    private int m_amount;

    /**
     * The rate at which creatures are added until m_maxAmount is reached.
     */
    private int m_recoveryRate;

    /**
     * The rate at which new creatures are spawned assuming there is space.
     */
    private int m_spawnRate;

    /**
     * Who is observing this resource.  Usually the observer is a room where the created creature is placed.
     */
    private Vector<SpawnObserver> m_observers = new Vector<SpawnObserver>();

    /**
     * The rate at which a creature is created with a CreatureResource attached.
     * Be careful when setting this to 1 as in the right room setup inifinite creatures
     * can be created.
     */
    private double m_specialSpawnRate = 0;

    public CreatureResource(Creature dude, int max, int recoverRate, int spawnRate)
    {
        m_creature = dude;
        m_maxAmount = max;
        m_recoveryRate = recoverRate;
        m_spawnRate = spawnRate;
        m_amount = 0;
    }

    /**
     * 
     * @return The creature that this resource creates.
     */
    public Creature getCreature()
    {
        return m_creature;
    }

    /**
     * 
     * @return Maximum number of creatures this resource can hold.
     */
    public int getMaxAmount()
    {
        return m_maxAmount;
    }

    /**
     * 
     * @return Present number of creatures held by this resource.
     */
    public int getAmount()
    {
        return m_amount;
    }

    /**
     * 
     * @return The rate at which this resource recovers creatures that have been lost.
     */
    public int getRecoverRate()
    {
        return m_recoveryRate;
    }

    /**
     * Creates a brand new creature based off the one it contains ONLY if 
     * there is anyone observing.
     * @return
     */
    public void spawn()
    {
        Creature newGuy = null;
        if ((m_amount > 0) && (m_observers.size() > 0))
        {
            m_amount--;
            newGuy = m_creature.clone();

            if (Math.random() < m_specialSpawnRate)
            {
                CreatureResource cr = new CreatureResource(m_creature.clone(), m_maxAmount, m_recoveryRate, m_spawnRate);
                cr.setSpecialCreatureRate(m_specialSpawnRate);
                newGuy.setResource(cr);
            }

            Vector<SpawnObserver> temp = new Vector<SpawnObserver>(m_observers.size());
            for (SpawnObserver so : m_observers)
            {
                temp.add(so);
            }

            for (SpawnObserver so : temp)
            {
                so.spawnUpdate(this, newGuy);
            }

        }

    }

    /**
     * Sets the present amount of creatures this resource can spawn
     * before running out.  If the value is above m_maxAmount it will be
     * set to m_maxAmount.
     * @param count
     */
    public void setAmount(int count)
    {
        if (count > m_maxAmount)
            m_amount = m_maxAmount;
        else
            m_amount = count;

    }

    /**
     * As time passes the amount of resources available should go
     * up depending on the rate set for this resource.
     */
    @Override
    public void updateTime(String name, int time)
    {
        if (TheWorld.SPAWN_TIMER.equals(name))
        {
            if (time % m_spawnRate == 0)
            {
                spawn();
            }
            if (time % m_recoveryRate == 0)
            {
                setAmount(m_amount + 1);
            }
        }

    }

    /**
     * 
     * @return How fast this resource spawns new creatures.
     */
    public int getSpawnRate()
    {
        return m_spawnRate;
    }

    /**
     * Add a SpawnObserver.
     */
    @Override
    public void addSpawnObserver(SpawnObserver observer)
    {
        m_observers.add(observer);
    }

    /**
     * Remove a SpawnObserver.
     */
    @Override
    public void removeSpawnObserver(SpawnObserver observer)
    {
        m_observers.remove(observer);
    }

    /**
     * Used to change the rate at which creatures with CreatureResources are spawned.
     * 
     * @param rate
     */
    public void setSpecialCreatureRate(double rate)
    {
        m_specialSpawnRate = rate;
    }

    /**
     * Checks to see whether this observer in question is already observing.
     * @param observer
     * @return
     */
    public boolean containsObserver(SpawnObserver observer)
    {
        return m_observers.contains(observer);
    }

}
