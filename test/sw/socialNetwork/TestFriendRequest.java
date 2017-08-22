package sw.socialNetwork;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.lifeform.NPC;

public class TestFriendRequest
{
	
	/**
	 * This test makes sure that FriendRequest can be properly created.
	 */
	@Test
	public void testInit()
	{
	    NPC requester = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC requestee = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		FriendRequest fr = new FriendRequest(requester, requestee);
		
		assertEquals(FriendRequestStatus.WAITING, fr.getState());
		assertEquals(requester, fr.getRequester());
		assertEquals(requestee, fr.getRequestee());
	}
	
	/**
	 * This test makes sure that accept() works properly.
	 */
	@Test
	public void testAccepted()
	{
	    NPC requester = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC requestee = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		FriendRequest fr = new FriendRequest(requester, requestee);
		
		fr.accept();
		assertEquals(FriendRequestStatus.ACCEPTED, fr.getState());
		
		//once accepted, the status should not be able to change
		fr.reject();
		assertEquals(FriendRequestStatus.ACCEPTED, fr.getState());
	}
	
	
	/**
	 * This test makes sure that reject() works properly.
	 */
	@Test
	public void testReject()
	{
	    NPC requester = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        NPC requestee = new NPC(1, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
		FriendRequest fr = new FriendRequest(requester, requestee);
		
		fr.reject();
		assertEquals(FriendRequestStatus.REJECTED, fr.getState());
		
		//once accepted, the status should not be able to change
		fr.accept();
		assertEquals(FriendRequestStatus.REJECTED, fr.getState());
	}
}
