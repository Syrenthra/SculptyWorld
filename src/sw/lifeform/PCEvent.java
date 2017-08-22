package sw.lifeform;

import sw.environment.Room;
import sw.item.Item;
import sw.quest.Quest;

/**
 * Contains information related to something a PC has just done such as
 * kill a creature, move to a new room, get or drop an item, or say something.
 * @author cdgira
 *
 */
public class PCEvent
{
    public static final int INVALID = -1;
    
    public static final int DROP_ITEM = 1;
    public static final int GET_ITEM = 2;
    public static final int GIVE_ITEM = 3;
    public static final int KILLED_CREATURE = 4;
    public static final int MOVED = 5;
    public static final int NATIVE_QUEST_ADDED = 6;
    public static final int SAID_BY = 7;
    public static final int SAID_TO = 8;
    
    
    int m_eventType = -1;
    PC m_pc;
    Creature m_creature;
    String m_text;
    Room m_startRoom;
    Room m_destRoom;
    Lifeform m_target;
    Item m_item;
    Quest m_quest;
    
    private PCEvent(PC pc, int type)
    {
        m_pc = pc;
        m_eventType = type;
    }

    /**
     * Construct an event for the PC involving a creature.  Usually when the creature has been killed.
     * @param pc
     * @param creature
     * @param type
     */
    public PCEvent(PC pc, Creature creature,int type)
    {
        this(pc,type);
        m_creature = creature;
    }

    /**
     * Used to construct an event with a PC involving text. Usually something said by or to the PC.
     * @param pc
     * @param str
     * @param type
     */
    public PCEvent(PC pc, String str, int type)
    {
        this(pc,type);
        m_text = str;
    }

    /**
     * Used to construct an event for a PC involving rooms (usually moving between them).
     * @param pc
     * @param room1
     * @param room2
     * @param type
     */
    public PCEvent(PC pc, Room room1, Room room2, int type)
    {
        this(pc,type);
        m_startRoom = room1;
        m_destRoom = room2;
    }

    /**
     * Used to create an event by the PC where the PC gives an item to someone.
     * @param pc
     * @param target
     * @param item
     * @param type
     */
    public PCEvent(PC pc, Lifeform target, Item item, int type)
    {
        this(pc,type);
        m_target = target;
        m_item = item;
    }

    /**
     * Constructs an event for a PC where the PC likely either picked up or dropped an item.
     * @param pc
     * @param item
     * @param type
     */
    public PCEvent(PC pc, Item item, int type)
    {
        this(pc,type);
        m_item = item;
    }

    /**
     * Creates an event related to a quest, most likely either a quest
     * being added or removed.
     * @param pc
     * @param quest
     * @param type
     */
    public PCEvent(PC pc, Quest quest, int type)
    {
        this(pc,type);
        m_quest = quest;
    }

    /**
     * The PC that generated this event.
     * @return
     */
    public PC getPC()
    {
        return m_pc;
    }

    /**
     * The creature (if any) related to this event.  Likely a creature the PC just killed.
     * @return
     */
    public Creature getCreature()
    {
        return m_creature;
    }

    /**
     * Returns the quest (if any) related to this event.  Likely returns the quest
     * just added or removed by this player.
     * @return
     */
    public Quest getQuest()
    {
        return m_quest;
    }
    
    /**
     * Returns any text related to the event. Likely something the player said or was said to the player.
     * @return
     */
    public String getText()
    {
        return m_text;
    }

    /**
     * What type of event this is.  Useful in determining what the data means, such as
     * said by verses said to.
     * @return
     */
    public int getType()
    {
        return m_eventType;
    }

    /**
     * The start room for the event (usually the room the PC moved from).
     * @return
     */
    public Room getStartRoom()
    {
        return m_startRoom;
    }

    /**
     * The destination room for the event (usually the room the PC moved to).
     * @return
     */
    public Room getDestRoom()
    {
        return m_destRoom;
    }

    /**
     * Returns the target for an event (usually being given an item).
     * @return
     */
    public Lifeform getTarget()
    {
        return m_target;
    }

    /**
     * Returns the item related to the event.
     * @return
     */
    public Item getItem()
    {
        return m_item;
    }

}
