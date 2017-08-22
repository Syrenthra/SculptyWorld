package sw.net.state;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.DemoWorld;
import sw.environment.TheWorld;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;


public class TestInWorldState
{
    @Before
    public void before()
    {
        DemoWorld.constructDemoWorld();
    }
    
    @After
    public void after()
    {
        TheWorld.reset();
    }
    
    @Test
    public void testJustEnteredWorld()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InWorldState ics = new InWorldState(sc);

        assertEquals("Desert 2\nThis is a cold desert.\nExits: West\nCreatures: None\nNPCs: Mountain Quest Dude", ics.getMessage().getMessage());
    }
    
    @Test
    public void testCanMoveWest()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InWorldState ics = new InWorldState(sc);
        SWMessage msg = new SWMessage("west");
        ics.executeAction(msg);
        
        assertEquals("Desert 1\nThis is a hot desert.\nExits: East West\nCreatures: None\nNPCs: Forest Quest Dude", ics.getMessage().getMessage());
    }
    
    @Test
    public void testCanMoveEast()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InWorldState ics = new InWorldState(sc);
        SWMessage msg = new SWMessage("west");
        ics.executeAction(msg);
        ics.executeAction(msg);
        msg = new SWMessage("east");
        ics.executeAction(msg);
        
        assertEquals("Desert 1\nThis is a hot desert.\nExits: East West\nCreatures: None\nNPCs: Forest Quest Dude", ics.getMessage().getMessage());
    }
    
    @Test
    public void testCreaturesAppearAsTimePasses()
    {
        SWServerConnection sc = new MockSWServerConnection(null,null,null,null);
        InWorldState ics = new InWorldState(sc);
        SWMessage msg = new SWMessage("west");
        ics.executeAction(msg);
        ics.executeAction(msg);
        ics.executeAction(msg);
        
        assertEquals("Forest 2\nThis is a big forest.\nExits: East West\nCreatures: None\nNPCs: None", ics.getMessage().getMessage());
        
        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals("Forest 2\nThis is a big forest.\nExits: East West\nCreatures: Forest Creature \nNPCs: None", ics.getMessage().getMessage());
    }

}
