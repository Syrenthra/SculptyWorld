package sw.environment.commands;

import mock.MockCommandRoomObserver;

import org.junit.After;
import org.junit.Before;

import sw.DemoTestWorld;
import sw.environment.Exit;
import sw.environment.TheWorld;
import sw.item.Item;
import sw.item.Weapon;
import sw.lifeform.PC;


public class TestInWorldCommand
{

    Weapon weap;
    PC player1;
    PC player2;
    MockCommandRoomObserver roomObvOneP1;
    MockCommandRoomObserver roomObvTwoP1;
    MockCommandRoomObserver obv2;
    
    
    @Before
    public void before() throws Exception
    {
        DemoTestWorld.constructDemoWorld();
        player1 = DemoTestWorld.getPlayer1();
        player2 = DemoTestWorld.getPlayer2();
        
       // Player 1
        roomObvOneP1 = new MockCommandRoomObserver();
        roomObvTwoP1 = new MockCommandRoomObserver();
        player1.getCurrentRoom().addRoomObserver(roomObvOneP1);
        player1.getCurrentRoom().getExit(Exit.WEST).addRoomObserver(roomObvTwoP1);
        
        // Player 2
        obv2 = new MockCommandRoomObserver();
        DemoTestWorld.getPlayer2().getCurrentRoom().addRoomObserver(obv2);
        
    }
    
    @After
    public void after() throws Exception
    {
        TheWorld.reset();
    }
    
    public void processCommand(InWorldCommand cmd, String str, PC player)
    {
        cmd.processCommand(player,str);
        try
        {
            Thread.sleep(150);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}



/**
 * A simple item we can use for testing.
 * @author cdgira
 *
 */
class MockCommandItem extends Item
{

    public MockCommandItem(String name, String desc)
    {
        super(name, desc,10,10);
    }
    
}