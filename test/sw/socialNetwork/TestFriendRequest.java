package sw.socialNetwork;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestFriendRequest
{
	
	/**
	 * This test makes sure that FriendRequest can be properly created.
	 */
	@Test
	public void testInit()
	{
		MockSocialNPC requester = new MockSocialNPC();
		MockSocialNPC requestee = new MockSocialNPC();
		FriendRequest fr = new FriendRequest(requester, requestee);
		
		assertEquals(FriendRequestStatus.WAITING, fr.getState());
		assertEquals(requester, fr.getRequester());
		assertEquals(requestee, fr.getRequestee());
		
		requester.getQuestGenerator().clear();
	}
	
	/**
	 * This test makes sure that accept() works properly.
	 */
	@Test
	public void testAccepted()
	{
		MockSocialNPC requester = new MockSocialNPC();
		MockSocialNPC requestee = new MockSocialNPC();
		FriendRequest fr = new FriendRequest(requester, requestee);
		
		fr.accept();
		assertEquals(FriendRequestStatus.ACCEPTED, fr.getState());
		
		//once accepted, the status should not be able to change
		fr.reject();
		assertEquals(FriendRequestStatus.ACCEPTED, fr.getState());
		
		requester.getQuestGenerator().clear();
	}
	
	
	/**
	 * This test makes sure that reject() works properly.
	 */
	@Test
	public void testReject()
	{
		MockSocialNPC requester = new MockSocialNPC();
		MockSocialNPC requestee = new MockSocialNPC();
		FriendRequest fr = new FriendRequest(requester, requestee);
		
		fr.reject();
		assertEquals(FriendRequestStatus.REJECTED, fr.getState());
		
		//once accepted, the status should not be able to change
		fr.accept();
		assertEquals(FriendRequestStatus.REJECTED, fr.getState());
		
		requester.getQuestGenerator().clear();
	}
}
