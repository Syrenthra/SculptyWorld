package sw.environment;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import sw.environment.CreatureResource;
import sw.environment.SpawnObserver;
import sw.lifeform.Creature;
import sw.time.GameTimer;


public class TestCreatureResource
{
    
    @Before
    public void before()
    {
        TheWorld.getInstance(false);
    }
    
    @After
    public void after()
    {
        TheWorld.reset();
    }
    
    @Test
    public void testInitialize()
    {
        Creature dude = new Creature(1,"Dude","Some Dude",100,10,5,2);
        CreatureResource resource = new CreatureResource(dude,10,3,4);
        assertEquals(dude,resource.getCreature());
        assertEquals(10,resource.getMaxAmount());
        assertEquals(0,resource.getAmount());
        assertEquals(3,resource.getRecoverRate());
        assertEquals(4,resource.getSpawnRate());
    }
    
    @Test
    public void testSetAmount()
    {
        Creature dude = new Creature(1,"Dude","Some Dude",100,10,5,2);
        CreatureResource resource = new CreatureResource(dude,10,3,4);
        resource.setAmount(10);
        assertEquals(10,resource.getAmount());
        
        resource.setAmount(12);
        assertEquals(10,resource.getAmount());
    }
    
    @Test
    public void testWorksWithGameTimer()
    {
        Creature dude = new Creature(1,"Dude","Some Dude",100,10,5,2);
        dude.addZone(Zone.CITY);
        CreatureResource resource = new CreatureResource(dude,10,3,2);
        GameTimer timer = new GameTimer(TheWorld.SPAWN_TIMER,500);
        Room room = new Room(1,"Room","Test Room");
        room.setZone(Zone.CITY);
        room.addCreatureResource(resource);
        resource.addSpawnObserver(room);
        timer.addTimeObserver(resource);
        timer.timeChanged(); // 1
        assertEquals(0,resource.getAmount());
        timer.timeChanged(); // 2
        assertEquals(0,resource.getAmount());
        assertEquals(0,room.getNumCreatures());
        timer.timeChanged(); // 3
        assertEquals(1,resource.getAmount());
        assertEquals(0,room.getNumCreatures());
        assertEquals(1,room.getCreatureResources().size());
        timer.timeChanged(); // 4
        assertEquals(1,room.getNumCreatures());
    }
    
    @Test
    public void testCanAddAndRemoveSpawnObservers()
    {
        Creature dude = new Creature(1,"Dude","Some Dude",100,10,5,2);
        CreatureResource resource = new CreatureResource(dude,10,3,2);
        MockSpawnObserver observer = new MockSpawnObserver();
        resource.setAmount(5);
        resource.addSpawnObserver(observer);
        resource.spawn();
        assertTrue(dude != observer.creature);
        assertEquals("Dude",observer.creature.getName());
        Creature spawn = observer.creature;
        resource.removeSpawnObserver(observer);
        resource.spawn();
        assertTrue(spawn == observer.creature);
        assertEquals(4,resource.getAmount());
        
    }
    
    /**
     * Want to make sure that the removeSpawnListener doesn't cause the 
     * addSpawnListener to fail.
     */
    @Test
    public void testResourceHoldsUpWithMulipleListeners()
    {
        Creature dude = new Creature(1,"Dude","Some Dude",100,10,5,2);
        CreatureResource resource = new CreatureResource(dude,10,3,2);
        resource.setAmount(5);
        MockSpawnRemovesObserver observer1 = new MockSpawnRemovesObserver(resource);
        MockSpawnRemovesObserver observer2 = new MockSpawnRemovesObserver(resource);
        resource.addSpawnObserver(observer1);
        resource.addSpawnObserver(observer2);
        resource.spawn();
        assertEquals("Dude",observer1.creature.getName());
        assertEquals("Dude",observer2.creature.getName());
        assertEquals(4,resource.getAmount());
    }
    
    @Test
    public void testResourceGeneratesCreaturesAtCorrectRateWithCreatureResourceAttached()
    {
        Creature dude = new Creature(1,"Dude","Some Dude",100,10,5,2);
        CreatureResource resource = new CreatureResource(dude,100,200,1);
        resource.setSpecialCreatureRate(0.5);
        resource.setAmount(100);
        MockSpawnRemovesObserver observer1 = new MockSpawnRemovesObserver(resource);
        resource.addSpawnObserver(observer1);
        
        int totalNormal = 0;
        for (int x=0;x<100;x++)
        {
            resource.spawn();
            if (observer1.creature.getResource() == null)
                totalNormal++;
            resource.addSpawnObserver(observer1);
        }
        
        assertTrue(totalNormal > 40);
        assertTrue(totalNormal < 60);
    }

}

class MockSpawnObserver implements SpawnObserver
{
    Creature creature = null;

    @Override
    public void spawnUpdate(CreatureResource source, Creature spawn)
    {
        creature = spawn;
        
    }
    
}

class MockSpawnRemovesObserver implements SpawnObserver
{
    Creature creature = null;
    CreatureResource resource = null;
    
    public MockSpawnRemovesObserver(CreatureResource res)
    {
        resource = res;
    }

    @Override
    public void spawnUpdate(CreatureResource source, Creature spawn)
    {
        creature = spawn;
        resource.removeSpawnObserver(this);
        
    }
}
