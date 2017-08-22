package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import sw.lifeform.Creature;
import sw.lifeform.Party;
import sw.lifeform.Player;
import sw.quest.CreatureQuest;



public class TestParty
{
    @Test
    public void testInitialize()
    {
        Player player = new Player(1,"Dude1","Desc",50);
        Party myParty = new Party(0,player);
        assertEquals(0,myParty.getID());
        assertEquals(player,myParty.getPlayer(0));
        assertEquals(myParty,player.getParty());
    }
    
    @Test
    public void testAddPlayer()
    {
        Player player1 = new Player(1,"Dude1","Desc",50);
        Player player2 = new Player(2,"Dude2","Desc",50);
        Party myParty1 = new Party(0,player1);
        Party myParty2 = new Party(1,player2);
        
        myParty1.mergeParties(myParty2);
        assertEquals(player1,myParty1.getPlayer(0));
        assertEquals(player2,myParty1.getPlayer(1));
        // myParty2 should be completely emptied at this point.
        assertNull(myParty2.getPlayer(0));
        assertEquals(myParty1,player2.getParty());
    }
    


}
