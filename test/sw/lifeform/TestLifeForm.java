package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import sw.environment.Room;
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
        assertEquals(5,dude.getID());
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
}
class MockLifeform extends Lifeform
{
    public MockLifeform(String name, String desc, int life)
    {
        super(5,name,desc,life);
    }

    @Override
    public void attack(Lifeform entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void takeHit(int damage)
    {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void updateTime(String name, int time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takeHeal(int magnitude) {
		// TODO Auto-generated method stub
		
	}
}

class MockRoom extends Room
{
    public MockRoom(int id, String name, String desc)
    {
        super(id, name,desc);
    }

    @Override
    public void updateTime(String name, int time)
    {
        // TODO Auto-generated method stub
        
    }
}
