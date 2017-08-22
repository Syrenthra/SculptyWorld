package sw.environment;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class WorldZone
{
    Hashtable<Integer, Room> m_rooms = new Hashtable<Integer,Room>();
    ArrayList<WorldZone> m_connectingZones = new ArrayList<WorldZone>();

    /**
     * Adds a room to this zone.  Only rooms that are all of the same Zone type may be
     * added to a Zone.
     * @param room
     */
    public boolean addRoom(Room room)
    {
        boolean result = false;
        if (m_rooms.size() == 0)
        {
            m_rooms.put(room.getID(), room);
            result = true;
        }
        else
        {
            Room roomInZone = m_rooms.elements().nextElement();
            if (roomInZone.getZone() == room.getZone())
            {
                m_rooms.put(room.getID(), room);
                result = true;
            }
        }
        return result;
        
    }

    /**
     * Returns true if this zone contains the room id supplied.
     * @param id
     * @return
     */
    public boolean containsRoom(int id)
    {  
        return m_rooms.containsKey(id);
    }

    /**
     * Removes the room based on the id provided from this zone.
     * @param room
     */
    public void removeRoom(int id)
    {
        m_rooms.remove(id);
    }

    /**
     * Connects two zones together signifying that there is likely a path from one
     * zone to the other zone by moving through the rooms.
     * @param zone
     */
    public void connectsTo(WorldZone zone)
    {
        if (!m_connectingZones.contains(zone))
            m_connectingZones.add(zone);
    }

    /**
     * Returns all the zones that neighbor this zone.
     * @return
     */
    public ArrayList<WorldZone> getNeighboringZones()
    {
        return m_connectingZones;
    }

    /**
     * Combines two WorldZones into one larger zone.
     * @param zone2
     */
    public void mergeZone(WorldZone zone)
    {
        Hashtable<Integer,Room> rooms = zone.getRooms();
        Enumeration<Room> enumRoom = rooms.elements();
        while (enumRoom.hasMoreElements())
        {
            Room room = enumRoom.nextElement();
            m_rooms.put(room.getID(), room);
        }
        
        for (int index=0;index<m_connectingZones.size();index++)
        {
            WorldZone connectedZone = m_connectingZones.get(index);
            if (connectedZone == zone)
            {
                m_connectingZones.remove(index);
                index--;
            }
                
        }
        
        for (WorldZone otherConnection : zone.getNeighboringZones())
        {
            if (otherConnection != this)
            {
                this.connectsTo(otherConnection);
                otherConnection.connectsTo(this);
                otherConnection.removeConnection(zone);
            }
        }
            
        
    }

    /**
     * Returns all the rooms that make up this WorldZone.
     * @return
     */
    public Hashtable<Integer, Room> getRooms()
    {
        return m_rooms;
    }

    /**
     * Removes a connection to another WorldZone.
     * @param zone
     */
    public void removeConnection(WorldZone zone)
    {
       m_connectingZones.remove(zone); 
    }

}
