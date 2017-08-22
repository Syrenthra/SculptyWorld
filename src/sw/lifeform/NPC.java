package sw.lifeform;

import java.util.Enumeration;
import java.util.Hashtable;

import sw.environment.Room;
import sw.environment.RoomObserver;
import sw.environment.SWRoomUpdateType;
import sw.quest.CreatureQuest;
import sw.quest.Quest;

public class NPC extends Lifeform implements RoomObserver
{
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

    protected Quest m_quest;

    protected boolean m_questActive = false;

    /**
     * Stores the last active status for a room.  Used to determine if a quest
     * should remain active or not.
     */
    private Hashtable<Room, Boolean> m_roomState = new Hashtable<Room, Boolean>();

    /**
     * Constructs the creature.
     * @param name
     * @param desc
     * @param life
     * @param damage
     * @param armor
     * @param speed
     */
    public NPC(int id, String name, String desc, int life, int damage, int armor, int speed)
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

    /**
     * Assigns a quest to this NPC.
     * @param quest
     */
    public void addAssignableQuest(Quest quest)
    {
        m_quest = quest;
    }

    /**
     * Gets the quest assigned to this NPC.
     * @return
     */
    public Quest getQuest()
    {
        return m_quest;
    }

    /**
     * Assigns the specified quest to the player.
     * @param player
     * @param i
     */
    public void assignQuest(Player player, int quest)
    {
        if (m_questActive)
        {
            player.addQuest(m_quest);
            m_quest.addPlayer(player);
        }

    }

    /**
     * Informs the NPC of an update from the room it is observing. 
     * Most likely an OutsideRoom.
     */
    @Override
    public void roomUpdate(Room room, Object source, SWRoomUpdateType type)
    {
        if (m_quest instanceof CreatureQuest)
        {
            if ((type == SWRoomUpdateType.CREATURE_ADDED) || (type == SWRoomUpdateType.CREATURE_REMOVED))
            {
                m_questActive = false;
                Creature[] creatures = room.getCreatures();
                Creature questCreature = ((CreatureQuest) m_quest).getCreature();

                for (int x = 0; x < room.getNumCreatures(); x++)
                {
                    Creature roomCreature = creatures[x];

                    if (roomCreature.isSame(questCreature))
                    {
                        m_roomState.remove(room);
                        m_roomState.put(room, true);
                        m_questActive = true;
                        break;
                    }
                }

                if (!m_questActive)
                {
                    m_roomState.remove(room);
                    m_roomState.put(room, false);
                    Enumeration<Boolean> values = m_roomState.elements();
                    while (values.hasMoreElements())
                    {
                        boolean roomValue = values.nextElement();
                        if (roomValue)
                        {
                            m_questActive = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns whether the NPC's quest is active or not.
     * @return
     */
    public boolean isQuestActive()
    {
        return m_questActive;
    }

    @Override
    public void updateTime(String name, int time)
    {
        // TODO Auto-generated method stub

    }

}
