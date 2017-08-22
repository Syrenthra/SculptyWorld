package sw.environment;

import java.util.Vector;

import sw.environment.commands.CommandResult;
import sw.lifeform.Creature;
import sw.lifeform.Lifeform;
import sw.lifeform.PC;

/**
 * A singleton class that ensures each room update is fully processed before the next one.
 * This keeps events that trigger other events from generating results that will arrive before
 * the initial event was finished.
 * @author cdgira
 *
 */
public class RoomEventTracker extends Thread
{
    private static RoomEventTracker theInstance = null;
    private Vector<Room> m_rooms = new Vector<Room>();
    private Vector<Object> m_data = new Vector<Object>();
    private Vector<RoomUpdateType> m_updateTypes = new Vector<RoomUpdateType>();
    
    boolean runFlag = true;
    
    /**
     * Make people have to use the getInstance method.
     */
    private RoomEventTracker()
    {
        
    }

    public static RoomEventTracker getInstance()
    {
        if (theInstance == null)
        {
            theInstance = new RoomEventTracker();
            theInstance.start();
        }
        
        return theInstance;
    }
    
    public void run()
    {
        while(runFlag)
        {
            while (m_rooms.size() > 0)
            {
                Room room = m_rooms.remove(0);
                Object data = m_data.remove(0);
                RoomUpdateType updateType = m_updateTypes.remove(0);
                room.informObservers(data, updateType);
            }
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addEvent(Room room, Object data, RoomUpdateType updateType)
    {
        m_rooms.addElement(room);
        m_updateTypes.addElement(updateType);
        m_data.addElement(data);
    }
    
    public void stopEventTracker()
    {
        runFlag = false;
    }

}
