package sw.environment;

/**
 * Used to by classes that want to get updates on changes in rooms.
 * @author cdgira
 *
 */
public interface RoomObserver
{
    public void roomUpdate(Room room, Object source, SWRoomUpdateType type);

}
