package sw.environment;

import static org.junit.Assert.assertEquals;
import mock.MockCommandRoomObserver;

import org.junit.Test;

import sw.environment.commands.CommandResult;


public class TestRoomEventTracker
{
    
    @Test
    public void testFunctionality() throws InterruptedException
    {
        Room room = new Room(1,"Room","Desc1");
        RoomEventTracker tracker = RoomEventTracker.getInstance();
        CommandResult data = new CommandResult(null, CommandResult.NONE, "What do you want to ask \"about\"?", "", "");
        RoomUpdateType updateType = RoomUpdateType.ERROR_MSG;
        tracker.addEvent(room,data,updateType);
        
        MockCommandRoomObserver observer1 = new MockCommandRoomObserver();
        room.addRoomObserver(observer1);
        Thread.sleep(250);
        assertEquals(RoomUpdateType.ERROR_MSG,observer1.myType.elementAt(0));
        assertEquals(data,observer1.src.elementAt(0));
    }

}
