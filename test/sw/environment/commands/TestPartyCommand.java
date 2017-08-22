package sw.environment.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mock.MockSWServerConnection;

import org.junit.Test;

import sw.DemoTestWorld;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;
import sw.net.state.InWorldState;


public class TestPartyCommand extends TestInWorldCommand
{

    @Test
    public void testPartyOfOne()
    {
        SWServerConnection sc2 = new MockSWServerConnection(null,null,null,null);
        InWorldState iws= new InWorldState(sc2,1,player1);
        
        roomObvOneP1.clearUpdates();
        
        InWorldCommand cmd = new PartyCommand(iws);
        processCommand(cmd,"party", player1);
        
        assertNotNull(iws.getMessage());
        // Test output for old room
        SWMessage data = iws.getMessage();
        assertEquals(player1.getName()+"\n",data.getMessage());
    }
    
    @Test
    public void testPartyOfTwo()
    {
        SWServerConnection sc2 = new MockSWServerConnection(null,null,null,null);
        InWorldState iws= new InWorldState(sc2,1,player1);
        
        roomObvOneP1.clearUpdates();
        
        player1.getParty().mergeParties(DemoTestWorld.getPlayer2().getParty());
        
        InWorldCommand cmd = new PartyCommand(iws);
        processCommand(cmd,"party", player1);
        
        assertNotNull(iws.getMessage());
        // Test output for old room
        SWMessage data = iws.getMessage();
        assertEquals(player1.getName()+"\n"+DemoTestWorld.getPlayer2().getName()+"\n",data.getMessage());
    }
}
