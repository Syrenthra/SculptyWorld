package mock;

import java.util.Vector;

import sw.environment.Room;
import sw.environment.RoomObserver;
import sw.environment.RoomUpdateType;

/**
 * Mock observer for use in testing commands that generate room events.
 */
public class MockCommandRoomObserver implements RoomObserver
{
    /**
     * The current time held by the observer
     */
    public Room myRoom = null;
    public Vector<Object> src = new Vector<Object>();
    public Vector<RoomUpdateType> myType = new Vector<RoomUpdateType>();
    
    public MockCommandRoomObserver()
    {
    }

    /**
     * Stores all the updates in a vector so we can check for correct updates
     * with the test.
     */
    @Override
    public void roomUpdate(Room room, Object source, RoomUpdateType type)
    {
        myRoom = room;
        src.addElement(source);
        myType.addElement(type);
        System.out.println("MockObserver: "+type);
    }
    
    /**
     * Clears out all updates received so far.
     */
    public void clearUpdates()
    {
        try
        {
            Thread.sleep(150);  // Wait for the RoomEventTrackerThread.
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        src.removeAllElements();
        myType.removeAllElements();
    }
}
