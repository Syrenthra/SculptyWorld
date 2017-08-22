package mock;

import java.util.Vector;

import sw.lifeform.PCEvent;
import sw.lifeform.PCObserver;

public class MockPCObserver implements PCObserver
{
    public PCEvent m_event;
    public Vector<PCEvent> m_events = new Vector<PCEvent>();

    @Override
    public void pcUpdate(PCEvent event)
    {
        m_event = event;
        m_events.add(event);
        
    }
    
    
}
