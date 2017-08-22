package sw.time;

import java.util.Vector;

/**
 * 
 * A concrete implementation of timer that acts as a subject
 * for all the objects implementing TimeObserver. 
 * 
 * @author David Jones
 */
public class GameTimer extends Thread implements Timer
{

    private String m_name = "Timer";
    /**
     * A value in milliseconds that determines the rate at which to increment what time it is by one.
     */
    private int m_intervalLength = 0;
    /**
     * The actual time the timer reports to all observers.  The rate this changes by is determined by the value in m_intervalLength.
     */
    private int m_time;
    private Vector <TimeObserver> m_observers = new Vector <TimeObserver>();
    /**
     * So long as this is true the timer will keep running.
     */
    private boolean m_flag = true;
    
    public GameTimer(String name, int rate)
    {
        m_name = name;
        m_intervalLength = rate;
    }

    /**
     * Allows an observer to get time updates.  Checks to make sure an 
     * observer does not get more than one update from the timer (can only
     * be added as an observer once).
     * 
     * @see gameplay.Timer#addTimeObserver(gameplay.TimeObserver)
     * Adds observers to theObservers
     */
    @Override
    public void addTimeObserver(TimeObserver observer)
    {
        if (!m_observers.contains(observer))  // Don't all observers to get double updates from a timer.
            m_observers.addElement(observer);
    
    }

    /** (non-Javadoc)
     * @see gameplay.Timer#removeTimeObserver(gameplay.TimeObserver)
     * removes observers from theObservers
     */
    @Override
    public void removeTimeObserver(TimeObserver observer)
    {
        m_observers.removeElement(observer);
    }

    /** (non-Javadoc)
     * @see gameplay.Timer#timeChanged()
     * updates all observers in theOberservers by calling the
     * updateTime method on each observer
     */
    @Override
    public void timeChanged()
    {
        m_time++;
        Vector<TimeObserver> temp = new Vector<TimeObserver>(m_observers.size());
        for (TimeObserver to : m_observers)
        {
            temp.add(to);
        }
        
        
        for(TimeObserver to : temp)
        {
            to.updateTime(m_name, m_time);
        }
    }

    /**
     * @return the number of rounds passed in the game
     */
    public int getTime()
    {
        
        return m_time;
    }

    /**
     * @return the number of observers currently observing
     * this subject
     */
    public int getNumberOfObservers()
    {
        
        return m_observers.size();
    }

    /**
     * Returns the name of the timer.
     * @return
     */
    public String getTimerName()
    {
        return m_name;
    }

    /**
     * Returns how long between update intervals for this timer.
     * @return
     */
    public int getIntervalLength()
    {
        return m_intervalLength;
    }
    
    /**
     * Sends out time updates based on the interval length.
     */
    @Override
    public void run()
    {
        while(m_flag)
        {
            try
            {
                Thread.sleep(m_intervalLength);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (m_flag)
                timeChanged();
        }
    }
    
    /**
     * Used to stop the thread.
     * @param value
     */
    public void setFlag(boolean value)
    {
        m_flag = value;
    }

    /**
     * Checks to see if this observer is already observing.
     * @param obv
     * @return
     */
    public boolean contains(TimeObserver obv)
    {
        return m_observers.contains(obv);
    }

}
