package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import mock.MockLifeform;
import mock.MockRoom;

import org.junit.Test;

import sw.environment.CreatureResource;
import sw.environment.Room;
import sw.environment.Zone;
import sw.item.Item;
import sw.lifeform.Lifeform;



public class TestLifeForm
{

    @Test
    public void testInitialization()
    {
        Lifeform dude = new MockLifeform("Dude","The Dude",10);
        assertEquals("Dude",dude.getName());
        assertEquals("The Dude",dude.getDescription());
        assertEquals(10,dude.getMaxLifePoints());
        assertEquals(10,dude.getCurrentLifePoints());
        assertEquals(-1,dude.getID());
        assertNull(dude.getCurrentRoom());
    }
    
    @Test
    public void testPlaceInRoom()
    {
        Lifeform dude = new MockLifeform("Dude","The Dude",10);
        Room testRoom = new MockRoom(1,"Test1","Desc1");
        dude.setCurrentRoom(testRoom);
        assertEquals(testRoom,dude.getCurrentRoom());
    }
    
    @Test
    public void testSetID()
    {
        Lifeform dude = new MockLifeform("Dude","The Dude",10);
        dude.setID(15);
        assertEquals(15,dude.getID());
    }
    
    @Test
    public void testSetCurrentLifePoints()
    {
        Lifeform dude = new MockLifeform("Dude","The Dude",10);
        dude.setCurrentLifePoints(5);
        assertEquals(5,dude.getCurrentLifePoints());
        dude.setCurrentLifePoints(11);
        assertEquals(10,dude.getCurrentLifePoints());
    }
    
    
}



