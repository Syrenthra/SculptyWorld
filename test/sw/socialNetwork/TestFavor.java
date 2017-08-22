package sw.socialNetwork;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.quest.MockSocialNPC;
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