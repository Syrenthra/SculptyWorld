package sw.time;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.time.GameTimer;
import sw.time.TimeObserver;

/**
 * @author David Jones
 * tests the functionality of SimpleTimer, a concrete
 * implementation of Timer. Note that there is a mock observer
 * at the bottom to allow for testing
 */
public class TestGameTimer
{

    /**
     * Tests the creation of a simple timer and checks that
     * all initialized values are correct
     */
    @Test
    public void testInitialization()
    {
        GameTimer timer = new GameTimer("Resource",500);
        
        assertEquals(0, timer.getTime());
        assertEquals("Resource",timer.getTimerName());
        assertEquals(500,timer.getIntervalLength());
        assertEquals(0, timer.getNumberOfObservers());
    }
    
    /**
     * tests both the adding of and updating of one observer
     * because we won't know if an observer is there unless
     * it updates properly
     */
    @Test
    public void testAddAndUpdate()
    {
        GameTimer timer = new GameTimer("Resource",500);
        MockSimpleTimerObserver obv = new MockSimpleTimerObserver();
        
        assertFalse(timer.contains(obv));
        
        timer.addTimeObserver(obv);
        
        assertTrue(timer.contains(obv));
        
        timer.timeChanged();
        
        assertEquals(1, timer.getNumberOfObservers());
        assertEquals(1, obv.myTime);
        assertEquals(1, timer.getTime());
    }
    
    /**
     * tests both the removing of and updating of one observer
     * because we won't know if an observer is there unless
     * it updates properly
     */
    @Test
    public void testRemoveAndUpdate()
    {
        GameTimer timer = new GameTimer("Resource",500);
        MockSimpleTimerObserver obv = new MockSimpleTimerObserver();
        
        timer.addTimeObserver(obv);
        timer.removeTimeObserver(obv);
        timer.timeChanged();
        
        assertEquals(0, timer.getNumberOfObservers());
        assertEquals(0, obv.myTime);
        assertEquals(1, timer.getTime());
    }
    
    @Test
    public void testThreadIntervalRate()
    {
        GameTimer timer = new GameTimer("Resource",500);
        MockSimpleTimerObserver obv = new MockSimpleTimerObserver();
        timer.addTimeObserver(obv);
        System.out.println(""+timer.getPriority()+" "+Thread.MAX_PRIORITY+" "+Thread.MIN_PRIORITY);
        timer.start();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i=1;i<5;i++)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            assertEquals(i,timer.getTime());
            assertEquals(i,obv.myTime);
        }
    }
    
    /**
     * Want to make sure that the removeSpawnListener doesn't cause the 
     * addSpawnListener to fail.
     */
    @Test
    public void testGameTimerHoldsUpWithMulipleListeners()
    {
        GameTimer timer = new GameTimer("Resource",500);
        MockTimeRemovesObserver observer1 = new MockTimeRemovesObserver(timer);
        MockTimeRemovesObserver observer2 = new MockTimeRemovesObserver(timer);
        timer.addTimeObserver(observer1);
        timer.addTimeObserver(observer2);
        timer.timeChanged();
        assertEquals(1,observer1.myTime);
        assertEquals(1,observer2.myTime);
    }

}


/**
 * @author David Jones
 * Mock observer for use in testing SimpleTimer
 */
class MockSimpleTimerObserver implements TimeObserver
{
    /**
     * The current time held by the observer
     */
    public int myTime = 0;
    
    /**
     * @see gameplay.TimeObserver#updateTime(int)
     * Simply puts the new time into an instance variable
     */
    @Override
    public void updateTime(String name, int time)
    {
        myTime = time;
    }
    
}

/**
 * @author David Jones
 * Mock observer for use in testing SimpleTimer
 */
class MockTimeRemovesObserver implements TimeObserver
{
    /**
     * The current time held by the observer
     */
    public int myTime = 0;
    public GameTimer m_timer;
    
    public MockTimeRemovesObserver(GameTimer timer)
    {
        m_timer = timer;
    }

    /**
     * @see gameplay.TimeObserver#updateTime(int)
     * Simply puts the new time into an instance variable
     */
    @Override
    public void updateTime(String name, int time)
    {
        myTime = time;
        m_timer.removeTimeObserver(this);
        
    }
    
}