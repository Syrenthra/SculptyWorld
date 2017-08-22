package sw.socialNetwork;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.quest.SocialQuestDifficulty;

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
		Favor fav = new Favor(snpc, SocialQuestDifficulty.YOUMUSTBEPRO);
		
		assertEquals(snpc, fav.getRequester());
		assertEquals(SocialQuestDifficulty.YOUMUSTBEPRO, fav.getDifficulty());
	}
}