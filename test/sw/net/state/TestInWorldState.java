package sw.net.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mock.MockSWServerConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.DemoTestWorld;
import sw.environment.Exit;
import sw.environment.TheWorld;
import sw.environment.commands.AcceptCommand;
import sw.environment.commands.DropCommand;
import sw.environment.commands.GetCommand;
import sw.environment.commands.JoinCommand;
import sw.environment.commands.LeaveCommand;
import sw.environment.commands.MoveCommand;
import sw.environment.commands.SayCommand;
import sw.lifeform.Creature;
import sw.lifeform.PC;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;


public class TestInWorldState
{
    InWorldState icsP1, icsP2, icsP3;
    SWServerConnection scP1, scP2, scP3;
    
    @Before
    public void before() throws Exception
    {
        DemoTestWorld.constructDemoWorld();
        
        scP1 = new MockSWServerConnection(null,null,null,null);
        icsP1 = new InWorldState(scP1,1,DemoTestWorld.getPlayer1());
        
        scP2 = new MockSWServerConnection(null,null,null,null);
        icsP2 = new InWorldState(scP2,2,DemoTestWorld.getPlayer2());
        
        scP3 = new MockSWServerConnection(null,null,null,null);
        icsP3 = new InWorldState(scP3,3,DemoTestWorld.getPlayer3());
    }
    
    @After
    public void after() throws Exception
    {
        TheWorld.reset();
    }
    
    @Test
    public void testCommandsLoaded()
    {
        assertTrue(icsP1.m_availableCommands.get("east") instanceof MoveCommand);
        assertTrue(icsP1.m_availableCommands.get("west") instanceof MoveCommand);
        assertTrue(icsP1.m_availableCommands.get("get") instanceof GetCommand);
        assertTrue(icsP1.m_availableCommands.get("drop") instanceof DropCommand);
        assertTrue(icsP1.m_availableCommands.get("say") instanceof SayCommand);
        assertTrue(icsP1.m_availableCommands.get("north") instanceof MoveCommand);
        assertTrue(icsP1.m_availableCommands.get("south") instanceof MoveCommand);
        assertTrue(icsP1.m_availableCommands.get("join") instanceof JoinCommand);
        assertTrue(icsP1.m_availableCommands.get("accept") instanceof AcceptCommand);
        assertTrue(icsP1.m_availableCommands.get("leave") instanceof LeaveCommand);
    }
    
    @Test
    public void testJustEnteredWorld()
    {
        assertEquals(DemoTestWorld.getPlayer1().getCurrentRoom().toString(), icsP1.getMessage().getMessage());
    }
   
    /**
     * Since each command is (or should be) fully tested we just need to make sure the command trigger
     * system is functioning.
     * @throws InterruptedException 
     */
    @Test
    public void testExecutesMoveCommandEastAndWest() throws InterruptedException
    {
        SWMessage msg = new SWMessage("west");
        icsP1.executeAction(msg);
        Thread.sleep(150);
        
        SWMessage outGoing = scP1.removeNextMessage();
        assertEquals("You walked to the west.\n", outGoing.getMessage());
        
        outGoing = scP2.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+" walked to the west.\n",outGoing.getMessage());
        
        outGoing = scP1.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getCurrentRoom().toString()+"\nYou walk in from the east.\n", outGoing.getMessage());
        
        msg = new SWMessage("east");
        icsP1.executeAction(msg);
        Thread.sleep(150);
        
        outGoing = scP1.removeNextMessage();
        assertEquals("You walked to the east.\n", outGoing.getMessage());
        
        outGoing = scP2.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+" walks in from the west.\n",outGoing.getMessage());
        
        outGoing = scP1.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getCurrentRoom().toString()+"\nYou walk in from the west.\n", outGoing.getMessage());
    }
    
    /**
     * Since each command is (or should be) fully tested we just need to make sure the command trigger
     * system is functioning.
     * @throws InterruptedException 
     */
    @Test
    public void testExecutesMoveCommandNorthAndSouth() throws InterruptedException
    {
        PC player = DemoTestWorld.getPlayer1();
        
        player.getCurrentRoom().removeRoomObserver(icsP1);
     // Move the player into Room3.
        TheWorld.getInstance().movePlayer(player, Exit.WEST);
        TheWorld.getInstance().movePlayer(player, Exit.WEST);
        TheWorld.getInstance().movePlayer(player, Exit.WEST);
        TheWorld.getInstance().movePlayer(player, Exit.WEST);
        
        player.getCurrentRoom().addRoomObserver(icsP1);
        
        // Move player2 into Room3
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        TheWorld.getInstance().movePlayer(DemoTestWorld.getPlayer2(), Exit.WEST);
        
        SWServerConnection sc2 = new MockSWServerConnection(null,null,null,null);
        InWorldState ics2 = new InWorldState(sc2,2,DemoTestWorld.getPlayer2());
        
        while (scP1.removeNextMessage() != null) {}
        while (sc2.removeNextMessage() != null) { }
        
        SWMessage msg = new SWMessage("north");
        icsP1.executeAction(msg);
        Thread.sleep(150);
        
        SWMessage outGoing = scP1.removeNextMessage();
        assertEquals("You walked to the north.\n", outGoing.getMessage());
        
        outGoing = sc2.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+" walked to the north.\n",outGoing.getMessage());
        
        outGoing = scP1.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getCurrentRoom().toString()+"\nYou walk in from the south.\n", outGoing.getMessage());
        
        msg = new SWMessage("south");
        icsP1.executeAction(msg);
        Thread.sleep(150);
        
        outGoing = scP1.removeNextMessage();
        assertEquals("You walked to the south.\n", outGoing.getMessage());
        
        outGoing = sc2.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+" walks in from the north.\n",outGoing.getMessage());
        
        outGoing = scP1.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getCurrentRoom().toString()+"\nYou walk in from the north.\n", outGoing.getMessage());
    }
   
    /**
     * @throws InterruptedException 
     * 
     */
    @Test
    public void testJoinAndAcceptCommand() throws InterruptedException
    {
        PC demoPlayer3 = DemoTestWorld.getPlayer3();
        
        DemoTestWorld.getPlayer1().getParty().mergeParties(demoPlayer3.getParty());
            
        SWMessage msg = new SWMessage("join Demo Player2");
        icsP1.executeAction(msg);
        Thread.sleep(150);
        
        SWMessage outGoing = scP1.removeNextMessage();
        assertEquals("You asked to join "+DemoTestWorld.getPlayer2().getName()+"'s party.\n", outGoing.getMessage());
        
        outGoing = scP2.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+ " asked to join your party.\n",outGoing.getMessage());
        
        msg = new SWMessage("accept Demo Player");
        icsP2.executeAction(msg);
        Thread.sleep(150);
        
        outGoing = scP2.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+ " has joined your party.\n",outGoing.getMessage());
        
        outGoing = scP1.removeNextMessage();
        assertEquals("You have joined "+DemoTestWorld.getPlayer2().getName()+"'s party.\n", outGoing.getMessage());
        
        outGoing = scP3.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+ " has joined the party.\n",outGoing.getMessage());
        
        assertEquals(DemoTestWorld.getPlayer1().getParty(),DemoTestWorld.getPlayer2().getParty());
    }
    
    /**
     * TODO: Really need to test this with a third player in the room
     * @throws InterruptedException 
     */
    @Test
    public void testJoinAndFailedAcceptCommand() throws InterruptedException
    {
        PC demoPlayer3 = DemoTestWorld.getPlayer3();
        PC demoPlayer = DemoTestWorld.getPlayer1();
        
        SWMessage msg = new SWMessage("join Demo Player2");
        icsP1.executeAction(msg);
        Thread.sleep(150);
        
        SWMessage outGoing = scP1.removeNextMessage();
        assertEquals("You asked to join "+DemoTestWorld.getPlayer2().getName()+"'s party.\n", outGoing.getMessage());
        
        outGoing = scP2.removeNextMessage();
        assertEquals(DemoTestWorld.getPlayer1().getName()+ " asked to join your party.\n",outGoing.getMessage());
        
        demoPlayer3.getParty().mergeParties(demoPlayer.getParty());
        
        msg = new SWMessage("accept Demo Player");
        icsP2.executeAction(msg);
        Thread.sleep(150);
        
        outGoing = scP2.removeNextMessage();
        assertEquals(demoPlayer.getName()+" joined a different party.\n",outGoing.getMessage());
        
        outGoing = scP1.removeNextMessage();
        assertEquals("You failed to join "+DemoTestWorld.getPlayer2().getName()+"'s party.\n", outGoing.getMessage());
    }
    
    @Test
    public void testFailedJoinRequest()
    {
        assertTrue(false);
    }
    
    @Test
    public void testFailedLeaveRequest()
    {
        assertTrue(false);
    }
    
    @Test
    public void testLeaveCommand() throws InterruptedException
    {
        PC player = DemoTestWorld.getPlayer1();
        PC player2 = DemoTestWorld.getPlayer2();
        PC player3 = DemoTestWorld.getPlayer3();
        
        player.getParty().mergeParties(player2.getParty());
        player.getParty().mergeParties(player3.getParty());
        
        SWMessage msg = new SWMessage("leave");
        icsP1.executeAction(msg);
        Thread.sleep(150);
        
        SWMessage outGoing = scP1.removeNextMessage();
        assertEquals("You left your party.\n", outGoing.getMessage());
        
        outGoing = scP2.removeNextMessage();
        assertEquals(player.getName()+" has left the party, you are now the party leader.\n",outGoing.getMessage());
        
        outGoing = scP3.removeNextMessage();
        assertEquals(player.getName()+" has left the party, "+player2.getName()+" is now the party leader.\n",outGoing.getMessage());
        
        assertTrue(player.getParty() != player2.getParty());
    }
       
    @Test
    public void testCreaturesAppearAsTimePasses()
    {
        //Move the player into the correct room.
        TheWorld world = TheWorld.getInstance();
        PC player = DemoTestWorld.getPlayer1();
        player.getCurrentRoom().removeRoomObserver(icsP1);
        player.getCurrentRoom().removePlayer(player.getID());
        world.getRoom(4).addPC(player);
        world.getRoom(4).addRoomObserver(icsP1);
        
        
        assertEquals(0,DemoTestWorld.getPlayer1().getCurrentRoom().getNumCreatures());
        
        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(1,DemoTestWorld.getPlayer1().getCurrentRoom().getNumCreatures());
        Creature entity = DemoTestWorld.getPlayer1().getCurrentRoom().getCreatures()[0];
        assertEquals("Forest Creature", entity.getName());
        assertNotNull(scP1.getNextMessage());
        assertEquals("A Forest Creature appears in the area.",scP1.getNextMessage().getMessage());
    }
    
    @Test
    public void testTwoPlayersTalkingToEachOther()
    {
        SWServerConnection sc2 = new MockSWServerConnection(null,null,null,null);
        InWorldState ics2 = new InWorldState(sc2,2,DemoTestWorld.getPlayer2());
        
     // Test with simple message.
        
     
        SWMessage msg = new SWMessage("say Hi Player2");
        icsP1.executeAction(msg);
        
        // Give threads time to run.
        try
        {
            Thread.sleep(250);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertNotNull(scP1.getNextMessage());
        assertEquals("You say, \"Hi Player2\"",scP1.removeNextMessage().getMessage());
        
        assertNotNull(sc2.getNextMessage());
        assertEquals("Demo Player says, \"Hi Player2\"",sc2.removeNextMessage().getMessage());
        
     // Test with say to PC message.
        msg = new SWMessage("say to Demo Player2 Hi");
        icsP1.executeAction(msg);
        
        // Give threads time to run.
        try
        {
            Thread.sleep(250);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertNotNull(scP1.getNextMessage());
        assertEquals("You say to Demo Player2, \"Hi\"",scP1.removeNextMessage().getMessage());
        
        assertNotNull(sc2.getNextMessage());
        assertEquals("Demo Player says to you, \"Hi\"",sc2.removeNextMessage().getMessage());
    }
    
    @Test
    public void testPlayerTalkingToNPC()
    {
        assertTrue(false);
    }

}
