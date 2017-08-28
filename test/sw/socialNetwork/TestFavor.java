package sw.socialNetwork;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.lifeform.NPC;
import sw.quest.SocialCapitolCost;

/**
 * 
 * @author David Abrams
 * 
 * Makes sure that Favor behaves properly.
 */
public class TestFavor
{
	@Test
	public void testFavor()
	{
		MockSocialNPC snpc = new MockSocialNPC();
		Favor2 fav = new Favor2(snpc, SocialCapitolCost.EXTREME);
		
		assertEquals(snpc, fav.getRequester());
		assertEquals(SocialCapitolCost.EXTREME, fav.getDifficulty());
	}
}

class MockSocialNPC extends NPC
{
	public MockSocialNPC() 
	{
		super(0, "Mocky", "A mock SocialNPC", 1, 1, 0, 1);
		// TODO Auto-generated constructor stub
	}
}