package sw.lifeform;

import java.util.Vector;

import sw.combat.Effect;
import sw.environment.CreatureResource;
import sw.environment.Exit;
import sw.environment.IdGen;
import sw.environment.TheWorld;
import sw.environment.Zone;

/**
 * Used to represent a creature in the game.
 * @author cdgira
 *
 */
public class Creature extends Lifeform implements Cloneable
{

    /**
     * Let's the game know if the creature is alive or dead. Dead
     * creatures should be removed from the game.
     */
    protected boolean m_alive = true;

    /**
     * How much damage the creature does in an attack.
     */
    protected int m_damage;

    /**
     * How much armor the creature has to protect itself from damage.
     */
    protected int m_armor;

    /**
     * How often the creature can attack.
     */
    protected int m_speed;

    /**
     * The resource carried by the creature.  This will create a new
     * spawn area in a room that does not have a CreatureResource that
     * creates Creatures of the same type.  To do so the Creature will
     * wander randomly.
     */
    protected CreatureResource m_resource = null;

    /**
     * Which zones can this creature move to or live in.
     */
    protected Vector<Zone> m_zones = new Vector<Zone>();

    /**
     * Constructs the creature.
     * @param name
     * @param desc
     * @param life
     * @param damage
     * @param armor
     * @param speed
     */
    public Creature(int id, String name, String desc, int life, int damage, int armor, int speed)
    {
        super(id, name, desc, life);
        m_damage = damage;
        m_armor = armor;
        m_speed = speed;
    }

    /**
     * Returns how much damage this creature does.
     * @return
     */
    public int getDamage()
    {
        return m_damage;
    }

    /**
     * Returns how much armor the creature has.
     * @return
     */
    public int getArmor()
    {
        return m_armor;
    }

    /**
     * The attack speed of the creature.
     * @return
     */
    public int getSpeed()
    {
        return m_speed;
    }

    /**
     * Creature attacks the other lifeform using it's damage value.
     * @param entity
     */
    @Override
    public void attack(Lifeform entity)
    {
        entity.takeHit(m_damage);

    }

    /**
     * Creature takes damage equal damage done minus armor.
     */
    @Override
    public void takeHit(int damage)
    {
        int damageSustained = damage - m_armor;
        if (damageSustained > 0)
            m_currentLifePoints -= damageSustained;
        if (m_currentLifePoints < 0)
            m_currentLifePoints = 0;
    }

    @Override
    public void takeHeal(int magnitude)
    {
        m_currentLifePoints += magnitude;
        if (m_currentLifePoints > m_maxLifePoints)
            m_currentLifePoints = m_maxLifePoints;
    }

    @Override
    public Creature clone()
    {
        Creature newGuy = new Creature(IdGen.getID(), m_name, m_description, m_maxLifePoints, m_damage, m_armor, m_speed);
        for (Zone zone : m_zones)
            newGuy.addZone(zone);
        
        return newGuy;
    }

    /**
     * Compares to creatures to see if they are the same
     * type.
     * @param dude3
     * @return
     */
    public boolean isSame(Creature dude)
    {
        return m_name.equals(dude.getName());
    }

    /**
     * Gets an update from the timer every tick. Updates all effects on Player
     * and, if a sufficient number of ticks has passed, picks the next action
     * from the queue.
     */
    public void updateTime(String name, int time)
    {
        if (name.equals(TheWorld.COMBAT_TIMER))
        {
            for (Effect i : m_effects)
            {
                i.updateEffect();
            }

            for (int i = 0; i < m_effects.size(); i++)
            {
                if (m_effects.get(i).getRemovalFlag())
                {
                    m_effects.remove(i);
                    i--;
                }
            }
        }

        if (name.equals(TheWorld.MOVE_TIMER))
        {
            if ((m_resource != null) && (m_currentRoom != null))
            {
                boolean space = true;

                for (CreatureResource cr : m_currentRoom.getCreatureResources())
                {
                    if (m_resource.getCreature().isSame(cr.getCreature()))
                        space = false;
                }
                if (space)
                {
                    m_currentRoom.addCreatureResource(m_resource);
                    m_resource = null;
                    TheWorld.getInstance().updateCreatureTimers(this);
                }
                else
                {
                    Exit[] exits = m_currentRoom.getValidExits(m_zones);
                    if (exits.length > 0)
                    {
                        int exit = (int) (Math.random() * exits.length);
                        TheWorld.getInstance().moveCreature(this, exits[exit]);
                    }
                }
            }
        }
    }

    /**
     * 
     * @return The resource carried by this creature.
     */
    public CreatureResource getResource()
    {
        return m_resource;
    }

    /**
     * Assigns a resource to the creature.  Creatures with a resource will
     * wander randomly looking for a room without a resource to place it in.
     * 
     * @param resource
     */
    public void setResource(CreatureResource resource)
    {
        m_resource = resource;
        TheWorld.getInstance().updateCreatureTimers(this);
    }

    /**
     * Returns how many valid zones this creature can go into.
     * 
     * @return
     */
    public int getNumValidZones()
    {
        return m_zones.size();
    }

    /**
     * Adds a Zone the creature is allowed to travel in.
     * @param beach
     */
    public void addZone(Zone zone)
    {
        m_zones.addElement(zone);

    }

    /**
     * Checks to see if this creature can travel in the specified zone.
     * @param zone
     * @return
     */
    public boolean canTravel(Zone zone)
    {
        return m_zones.contains(zone);
    }

    /**
     * Removes a zone from those the creature is allowed to travel in.
     * 
     * @param beach
     */
    public void removeZone(Zone zone)
    {
        m_zones.remove(zone);

    }
}
