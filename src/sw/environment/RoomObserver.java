package sw.environment;

/**
 * Used to by classes that want to get updates on changes in rooms.
 * @author cdgira
 *
 */
public interface RoomObserver
{
    /**
     * TODO: Should we create a RoomEvent that stores all three pieces of information into one class?
     * @param room
     * @param source
     * @param type
     */
    public void roomUpdate(Room room, Object source, RoomUpdateType type);

}
