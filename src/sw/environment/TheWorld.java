package sw.environment;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.time.GameTimer;

public class TheWorld implements RoomObserver
{
    public static final String COMBAT_TIMER = "Combat Timer";

    public static final String SPAWN_TIMER = "Spawn Timer";

    public static final String MOVE_TIMER = "Move Timer";

    public static final String ROOM_TIMER = "Room Timer";

    private static TheWorld m_theWorld = null;

    private Hashtable<Integer, PC> m_players = new Hashtable<Integer, PC>();

    private Hashtable<Integer, Creature> m_creatures = new Hashtable<Integer, Creature>();

    private Hashtable<Integer, NPC> m_npcs = new Hashtable<Integer, NPC>();

    private Hashtable<Integer, Room> m_rooms = new Hashtable<Integer, Room>();
    
    /**
     * The computed list of all the zones in the world and how they are interconnected. Created
     * using constructZoneGraph().
     */
    private ArrayList<WorldZone> m_zones = new ArrayList<WorldZone>();

    /**
     * TODO: Should probably protect the timers later.
     */
    public GameTimer m_combatTimer = new GameTimer(COMBAT_TIMER, 1000);

    /**
     * TODO: Just a temp fix for the demo.
     * TODO: Need to figure out what timers to have for the game and who/what each is assigned to.
     */
    public GameTimer m_spawnTimer = new GameTimer(SPAWN_TIMER, 2000);

    /**
     * Determines how often a creature tries to move.
     */
    public GameTimer m_moveTimer = new GameTimer(MOVE_TIMER, 3000);

    /**
     * Detemines when to have the room to check for changes.
     */
    public GameTimer m_roomTimer = new GameTimer(ROOM_TIMER, 5000);

    private TheWorld(boolean startTimers)
    {
        if (startTimers)
        {
            startTimers();
        }
    }
    
    /**
     * Starts the timers.
     */
    public void startTimers()
    {
        m_combatTimer.start();
        m_spawnTimer.start();
        m_moveTimer.start();
        m_roomTimer.start();
    }
    
    /**
     * Stops the timers.
     */
    public void stopTimers()
    {
        m_combatTimer.setFlag(false);
        m_spawnTimer.setFlag(false);
        m_moveTimer.setFlag(false);
        m_roomTimer.setFlag(false);
    }

    /**
     * Creates a new instance of TheWorld if there is none or returns the one that
     * does exist.  This version is mainly for testing purposes as it allow the
     * world to be created without the timers going.
     * @return
     */
    public static TheWorld getInstance(boolean startTimers)
    {
        if (m_theWorld == null)
        {
            m_theWorld = new TheWorld(startTimers);
        }
        return m_theWorld;
    }

    /**
     * Creates a new instance of TheWorld if there is none or returns the one that
     * does exist.
     * @return
     */
    public static TheWorld getInstance()
    {
        return TheWorld.getInstance(true);
    }

    /**
     * Adds a player to the world
     * @param player
     */
    public void addPlayer(PC player)
    {
        m_players.put(player.getID(), player);

    }

    /**
     * Gets the player with that id.
     * @param id
     * @return
     */
    public PC getPlayer(int id)
    {
        return m_players.get(id);
    }

    /**
     * Adds a room to the world.
     * @param room
     */
    public void addRoom(Room room)
    {
        m_rooms.put(room.getID(), room);
        room.addRoomObserver(this);
        m_roomTimer.addTimeObserver(room);
    }

    /**
     * 
     * @param id
     * @return The room with that id.
     */
    public Room getRoom(int id)
    {
        return m_rooms.get(id);
    }
    
    /**
     * Returns the number of rooms in the world
     * @return
     */
    public int getSize()
    {
    	return m_rooms.size();
    }

    /**
     * Moves a player from one room to another in the world.
     * @param player
     * @param exit
     * @return Returns true if moved player, false if did not.
     */
    public boolean movePlayer(PC player, Exit exit)
    {
        Room presentRoom = player.getCurrentRoom();
        if (presentRoom != null)
        {
            Room newRoom = presentRoom.getExit(exit);
            if (newRoom != null)
            {
                presentRoom.removePlayer(player.getID());
                newRoom.addPC(player);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a creature to the world.
     * @param creature
     */
    public void addCreature(Creature creature)
    {
        synchronized (m_creatures)
        {
            m_creatures.put(creature.getID(), creature);
            if (creature.getResource() != null)
            {
                m_moveTimer.addTimeObserver(creature);
            }
        }
        System.out.println("Total Creatures: " + m_creatures.size());
    }

    public Creature getCreature(int id)
    {
        return m_creatures.get(id);
    }

    /**
     * Removes the respected player from the world and any rooms 
     * he or she is in.
     * @param player
     */
    public void removePlayer(PC player)
    {
        int id = player.getID();
        Room room = player.getCurrentRoom();
        if (room != null)
        {
            room.removePlayer(id);
        }
        m_players.remove(id);
    }

    /**
     * Removes that creature from the world and any rooms it is in.
     * @param creature
     */
    public void removeCreature(int id)
    {
        synchronized (m_creatures)
        {
            Creature creature = m_creatures.get(id);
            if (creature != null)
            {
                Room room = creature.getCurrentRoom();
                if (room != null)
                {
                    room.removeCreature(id);
                }
                m_creatures.remove(id);
                m_moveTimer.removeTimeObserver(creature);
            }
        }

    }

    /**
     * Adds an NPC to the world.
     * @param npc
     */
    public void addNPC(NPC npc)
    {
        m_npcs.put(npc.getID(), npc);
        m_roomTimer.addTimeObserver(npc);

    }

    /**
     * Gets the NPC with that id.
     * @param id
     * @return
     */
    public NPC getNPC(int id)
    {
        return m_npcs.get(id);
    }

    /**
     * Removes the NPC with that id from the world completely.
     * @param id
     */
    public void removeNPC(int id)
    {
        NPC npc = m_npcs.get(id);
        if (npc != null)
        {
            Room room = npc.getCurrentRoom();
            if (room != null)
            {
                room.removeNPC(id);
            }
            m_npcs.remove(id);
            m_roomTimer.removeTimeObserver(npc);
        }
    }

    /**
     * Will try and have the creature exit the room it is in.
     * 
     * @param creature
     * @param exit
     */
    public void moveCreature(Creature creature, Exit exit)
    {
        synchronized (m_creatures)
        {
            Room presentRoom = creature.getCurrentRoom();
            if (presentRoom != null)
            {
                Room newRoom = presentRoom.getExit(exit);
                if (newRoom != null)
                {
                    presentRoom.removeCreature(creature.getID());
                    newRoom.addCreature(creature);
                }
            }
        }

    }

    /**
     * Informed of key updates to rooms so that if a new creature, etc
     * is created it can be kept track of and appropriate timers can be
     * attached.
     */
    @Override
    public void roomUpdate(Room room, Object source, RoomUpdateType type)
    {
        if (type == RoomUpdateType.CREATURE_ADDED)
        {
            Creature creature = (Creature) source;
            if (!m_creatures.containsKey(creature.getID()))
            {
                addCreature(creature);

            }
        }
        else if (type == RoomUpdateType.CREATURE_RESOURCE_ADDED)
        {
            CreatureResource cr = (CreatureResource) source;

            if (!m_spawnTimer.contains(cr))
            {
                m_spawnTimer.addTimeObserver(cr);
            }

        }
        else if (type == RoomUpdateType.CREATURE_RESOURCE_REMOVED)
        {
            CreatureResource cr = (CreatureResource) source;
            {
                if (m_spawnTimer.contains(cr))
                {
                    m_spawnTimer.removeTimeObserver(cr);
                }
            }
        }
    }

    /**
     * Removes the singleton from existence.  Mainly helps
     * with testing.
     */
    public static void reset()
    {
        if (m_theWorld != null)
        {
            m_theWorld.m_spawnTimer.setFlag(false);
            m_theWorld.m_moveTimer.setFlag(false);
            m_theWorld.m_roomTimer.setFlag(false);
            m_theWorld.m_combatTimer.setFlag(false);
        }
        m_theWorld = null;

    }

    /**
     * Returns how many total creatures exist in the world.
     * @return
     */
    public int getNumCreatures()
    {
        return m_creatures.size();
    }

    /**
     * Updates a creature as to whether it needs a timer or not.
     * Creatures with resources should be listening to the move timer.
     * 
     * @param creature
     */
    public void updateCreatureTimers(Creature creature)
    {
        synchronized (m_creatures)
        {
            if (m_creatures.containsKey(creature.getID()))
            {
                if (creature.getResource() != null)
                {
                    if (!m_moveTimer.contains(creature))
                        m_moveTimer.addTimeObserver(creature);
                }
                else
                {
                    m_moveTimer.removeTimeObserver(creature);
                }
            }
        }

    }

    /**
     * This will construct a graph of the zones that can be used to quickly
     * get the interconnected Zone a room belongs to or neighboring zone.  
     */
    public void constructZoneGraph()
    {
        m_zones = new ArrayList<WorldZone>();
        
        Enumeration<Room> roomEnum = m_rooms.elements();
        while(roomEnum.hasMoreElements())
        {
            WorldZone roomZone = new WorldZone();
            Room room = roomEnum.nextElement();
            roomZone.addRoom(room);
            m_zones.add(roomZone);
            
            // See if we need to connect the zone to another zone.
            for (Room exitRoom : room.getExitDestinations())
            {
                for (int index = 0; index < m_zones.size(); index++)
                {
                    WorldZone zone = m_zones.get(index);

                    if ((zone.containsRoom(exitRoom.getID())) && (zone != roomZone))
                    {
                        // If Zone type is the same then merge the WorldZones.
                        if (exitRoom.getZone() == room.getZone())
                        {
                            m_zones.remove(index);
                            roomZone.mergeZone(zone);
                            index--;
                        }
                        else
                        {
                            zone.connectsTo(roomZone);
                            roomZone.connectsTo(zone);
                        }
                    }
                }
            }
        }
    }

    public WorldZone getZone(Room room)
    {
        WorldZone result = null;
        
        for (WorldZone zone : m_zones)
        {
            if (zone.containsRoom(room.getID()))
            {
                result = zone;
                break;
            }
        }
        return result;
    }

    public ArrayList<WorldZone> getNeighboringZones(Room room)
    {
        WorldZone result = null;
        
        for (WorldZone zone : m_zones)
        {
            if (zone.containsRoom(room.getID()))
            {
                result = zone;
                break;
            }
        }
        if (result != null)
            return result.getNeighboringZones();
        else
            return null;
    }



}
