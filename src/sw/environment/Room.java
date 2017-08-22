package sw.environment;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import sw.item.Item;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.Player;
import sw.quest.Goal;
import sw.time.TimeObserver;

/**
 * Is the main room class for things to live inside of.  To work properly with TheWorld
 * nothing should be added to a room until after it has been added to TheWorld.
 * 
 * TODO: Race Conditions to address: Player, NPC, Items, CreatureResource
 * 
 * 
 * @author cdgira
 *
 */
public class Room implements TimeObserver, SpawnObserver, Goal
{
    protected int m_id;

    protected String m_title;

    protected String m_description;

    protected Zone m_zone;

    protected Hashtable<Exit, Room> m_exits = new Hashtable<Exit, Room>();

    protected Vector<Item> m_items = new Vector<Item>();

    protected Hashtable<Integer, Player> m_players = new Hashtable<Integer, Player>();

    protected Hashtable<Integer, Creature> m_creatures = new Hashtable<Integer, Creature>();

    protected Hashtable<Integer, NPC> m_npcs = new Hashtable<Integer, NPC>();

    Vector<CreatureResource> m_creatureResources = new Vector<CreatureResource>();

    Vector<RoomObserver> m_observers = new Vector<RoomObserver>();

    /**
     * Constructs the room.
     * @param title
     * @param description
     */
    public Room(int id, String title, String description)
    {
        m_id = id;
        m_title = title;
        m_description = description;
        m_zone = Zone.CITY;
    }

    /**
     * Returns the description.
     * @return
     */
    public String getDescription()
    {
        return m_description;
    }

    /**
     * Returns the title.
     * @return
     */
    public String getTitle()
    {
        return m_title;
    }

    /**
     * Add an exit to the room.
     * @param room
     * @param exit
     */
    public void addExit(Room room, Exit exit)
    {
        synchronized (m_exits)
        {
            m_exits.put(exit, room);
        }
        informObservers(exit, SWRoomUpdateType.EXIT_ADDED);

    }

    /**
     * Returns the room at the requested exit.
     * @param exit
     * @return
     */
    public Room getExit(Exit exit)
    {
        synchronized (m_exits)
        {
            return m_exits.get(exit);
        }
    }

    /**
     * Add an item to the Room.
     * @param item
     */
    public void addItem(Item item)
    {
        synchronized (m_items)
        {
            m_items.add(item);
        }
        informObservers(item, SWRoomUpdateType.ITEM_ADDED);

    }
    
    /**
     * Add an item to the Room.
     * @param item
     */
    public void removeItem(Item item)
    {
        synchronized (m_items)
        {
            m_items.remove(item);
        }
        informObservers(item, SWRoomUpdateType.ITEM_REMOVED);

    }

    /**
     * Gets an item at a specific location in the room.
     * @param location
     * @return
     */
    public Item getItem(int location)
    {
        synchronized (m_items)
        {
            return m_items.get(location);
        }
    }

    /**
     * Adds a player to the room.
     * @param dude
     */
    public void addPlayer(Player dude)
    {
        synchronized (m_players)
        {
            m_players.put(dude.getID(), dude);
            dude.setCurrentRoom(this);
        }
        informObservers(dude, SWRoomUpdateType.PLAYER_ADDED);

    }

    /**
     * Get player from a specific location.
     * @param location
     * @return
     */
    public Player getPlayer(int id)
    {
        synchronized (m_players)
        {
            return m_players.get(id);
        }
    }

    /**
     * Adds a new creature to the room
     * @param dude
     */
    public void addCreature(Creature dude)
    {
        synchronized (m_creatures)
        {
            if (dude.canTravel(m_zone))
            {
                dude.setCurrentRoom(this);
                m_creatures.put(dude.getID(), dude);
            }
        }
        informObservers(dude, SWRoomUpdateType.CREATURE_ADDED);

    }

    /**
     * Gets a creature from a specific location.
     * @param location
     * @return
     */
    public Creature getCreature(int id)
    {
        return m_creatures.get(id);

    }

    /**
     * Returns the zone in which the room lives.
     * @return
     */
    public Zone getZone()
    {
        return m_zone;
    }

    /**
     * Sets the zone in which this room is located.
     * @param zone
     */
    public void setZone(Zone zone)
    {
        m_zone = zone;
    }

    /**
     * Removes a creature from the room by providing an id.
     * @param location
     * @return
     */
    public Creature removeCreature(int id)
    {
        Creature creature = null;
        synchronized (m_creatures)
        {
            creature = m_creatures.remove(id);
            if ((creature != null) && (creature.getCurrentRoom() == this))
            {
                creature.setCurrentRoom(null);
            }
        }
        informObservers(creature, SWRoomUpdateType.CREATURE_REMOVED);
        return creature;
    }

    /**
     * Returns how many creatures are in the room.
     * @return
     */
    public int getNumCreatures()
    {
        return m_creatures.size();
    }

    /**
     * 
     * @return the unique id for the room.
     */
    public int getID()
    {
        return m_id;
    }

    /**
     * 
     * @param id
     * @return Whether that player is in the room or not.
     */
    public boolean hasPlayer(int id)
    {
        Player player = m_players.get(id);

        if (player != null)
            return true;

        return false;
    }

    /**
     * This method is only used for testing purposes right now.
     * 
     * TODO: Not Tested
     * @return
     */
    public Creature[] getCreatures()
    {
        Creature[] creatures = null;
        synchronized (m_creatures)
        {
            creatures = new Creature[m_creatures.size()];

            Enumeration<Creature> e = m_creatures.elements();
            int index = 0;
            while (e.hasMoreElements())
            {
                creatures[index] = e.nextElement();
                index++;
            }
        }
        return creatures;
    }

    /**
     * TODO: Not Tested
     * @return
     */
    public NPC[] getNPCs()
    {
        NPC[] npcs = null;
        synchronized (m_npcs)
        {
            npcs = new NPC[m_npcs.size()];

            Enumeration<NPC> e = m_npcs.elements();
            int index = 0;
            while (e.hasMoreElements())
            {
                npcs[index] = e.nextElement();
                index++;
            }
        }
        return npcs;
    }

    /**
     * Removes the requested player from the room.
     * @param id
     */
    public void removePlayer(int id)
    {
        Player player = null;
        synchronized (m_players)
        {
            player = m_players.get(id);
            m_players.remove(id);
            if ((player != null) && (player.getCurrentRoom() == this))
            {
                player.setCurrentRoom(null);
            }
        }
        informObservers(player, SWRoomUpdateType.PLAYER_REMOVED);
    }

    /**
     * Adds the NPC to the Room.
     * @param npc
     */
    public void addNPC(NPC npc)
    {
        synchronized (m_npcs)
        {
            m_npcs.put(npc.getID(), npc);
            npc.setCurrentRoom(this);
        }
        informObservers(npc, SWRoomUpdateType.NPC_ADDED);

    }

    /**
     * Returns the NPC if the room has that NPC.
     * @param id
     * @return
     */
    public NPC getNPC(int id)
    {
        synchronized (m_npcs)
        {
            return m_npcs.get(id);
        }
    }

    /**
     * Removes the NPC with that id from the room.
     * @param id
     * @return
     */
    public NPC removeNPC(int id)
    {
        NPC npc = null;
        synchronized (m_npcs)
        {
            npc = m_npcs.remove(id);
            if ((npc != null) && (npc.getCurrentRoom() == this))
            {
                npc.setCurrentRoom(null);
            }
        }
        informObservers(npc, SWRoomUpdateType.NPC_REMOVED);
        return npc;
    }

    /**
     * 
     * @return How many NPCs are presently in this room.
     */
    public int getNumNPCs()
    {
        synchronized (m_npcs)
        {

            return m_npcs.size();
        }
    }

    /**
     * Converts the Hashtable of possible exits into an array of possible exits.
     * 
     * @return The possible Exits from the room as an array.
     */
    public Exit[] getExits()
    {
        synchronized (m_exits)
        {
            Exit[] exits = new Exit[m_exits.size()];

            Enumeration<Exit> e = m_exits.keys();
            int index = 0;
            while (e.hasMoreElements())
            {
                exits[index] = e.nextElement();
                index++;
            }
            return exits;
        }
    }
    
    
    /**
     * @return The Rooms connected to this one
     */
    public Room[] getExitDestinations()
    {
        synchronized (m_exits)
        {
            Room[] attachedRooms = new Room[m_exits.size()];

            Collection<Room> rooms = m_exits.values();
            Iterator<Room> itr = rooms.iterator();
            int index = 0;
            while (itr.hasNext())
            {
            	attachedRooms[index] = itr.next();
                index++;
            }
            return attachedRooms;
        }
    }

    /**
     * Returns the resource attached to the outside room.
     * @return
     */
    public CreatureResource getCreatureResource(int location)
    {
        synchronized (m_creatureResources)
        {
            CreatureResource resource = null;
            if (m_creatureResources.size() > location)
                resource = m_creatureResources.get(location);
            return resource;
        }
    }

    /**
     * Whenever the room gets a spawn update, the room will check to see if 
     * any resources have been reduced to 0 creatures.  If they have, then that 
     * resource is removed.
     * 
     * TODO: The reduce to 0 has not been implemented.
     * 
     * When a room gets a creature from a resource it stops listening to the resource
     * until the creature has been killed or moved to another room.  The room
     * will check to turn resources back on during a time update.
     */
    @Override
    public void spawnUpdate(CreatureResource source, Creature spawn)
    {
        if (spawn != null)
        {
            addCreature(spawn);
            source.removeSpawnObserver(this);
        }

        if (source.getAmount() == 0)
        {
            this.removeCreatureResource(source);
        }

    }

    /**
     * During time updates the room checks to see if any resources need to be turned back
     * on or removed.
     * 
     * @param time
     */
    @Override
    public void updateTime(String name, int time)
    {
        if (TheWorld.ROOM_TIMER.equals(name))
        {
            for (int index = 0; index < m_creatureResources.size(); index++)
            {
                CreatureResource resource = m_creatureResources.elementAt(index);
                if (resource.getAmount() == 0)
                {
                    resource.removeSpawnObserver(this);
                    this.removeCreatureResource(resource);
                    index--;
                }
                else if (!resource.containsObserver(this))
                {
                    boolean activate = true;

                    synchronized (m_creatures)
                    {
                        Enumeration<Creature> e = m_creatures.elements();
                        while (e.hasMoreElements())
                        {
                            Creature creature = e.nextElement();

                            if (resource.getCreature().isSame(creature))
                            {
                                activate = false;
                                break;
                            }
                        }
                    }

                    if (activate)
                    {
                        resource.addSpawnObserver(this);
                    }
                }
            }
        }
    }

    /**
     * Adds a resource to the room that will spawn creatures into the room.
     * @param resource
     */
    public void addCreatureResource(CreatureResource resource)
    {
        m_creatureResources.add(resource);
        informObservers(resource, SWRoomUpdateType.CREATURE_RESOURCE_ADDED);
    }

    /**
     * When supported adds a observer to the room.
     * @param observer
     */
    public void addRoomObserver(RoomObserver observer)
    {
        m_observers.add(observer);

    }

    /**
     * When supported removes an observer from the room.
     * @param observer
     */
    public void removeRoomObserver(RoomObserver observer)
    {
        m_observers.remove(observer);
    }

    /**
     * When creatures, players, items, npcs, etc... are added or removed
     * from the room this is called to inform all the observers of what is going on.
     * 
     * @param type What type of change has occurred.  This helps the different observers focus on what has changed.
     */
    public void informObservers(Object source, SWRoomUpdateType type)
    {
        Vector<RoomObserver> temp = new Vector<RoomObserver>(m_observers.size());

        // TODO: Why are we doing this...try removing later.
        for (RoomObserver ro : m_observers)
        {
            temp.add(ro);
        }

        for (RoomObserver ro : temp)
        {
            ro.roomUpdate(this, source, type);
        }
    }

    /**
     * Removes a resource at a specific index.
     * @param index
     */
    public void removeCreatureResource(int index)
    {
        CreatureResource resource = m_creatureResources.remove(index);
        informObservers(resource, SWRoomUpdateType.CREATURE_RESOURCE_REMOVED);
    }

    /**
     * Removes a resource at a specific index.
     * @param index
     */
    public void removeCreatureResource(CreatureResource source)
    {
        if (m_creatureResources.remove(source))
            informObservers(source, SWRoomUpdateType.CREATURE_RESOURCE_REMOVED);
    }

    @Override
    public String toString()
    {
        return m_id + " : " + m_title;
    }

    /**
     * 
     * @return The CreatureResources presently in this room.
     */
    public Vector<CreatureResource> getCreatureResources()
    {
        return m_creatureResources;
    }

    /**
     * Returns only the exits that go to zones in the zones list.
     * @param zones
     * @return
     */
    public Exit[] getValidExits(Vector<Zone> zones)
    {
        Vector<Exit> exits = new Vector<Exit>();

        Enumeration<Exit> e = m_exits.keys();
        while (e.hasMoreElements())
        {
            Exit exit = e.nextElement();
            if (zones.contains(m_exits.get(exit).getZone()))
                exits.addElement(exit);
        }

        Exit[] returnExits = new Exit[exits.size()];
        for (int x = 0; x < exits.size(); x++)
        {
            returnExits[x] = exits.elementAt(x);
        }

        return returnExits;
    }

}
