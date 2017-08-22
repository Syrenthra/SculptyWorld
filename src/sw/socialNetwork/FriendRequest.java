package sw.socialNetwork;

import sw.lifeform.SocialNPC;
import sw.socialNetwork.simulation.EventTypes;

/**
 * 
 * @author David Abrams
 * 
 * This class allows SocialNPC to attempt to initiate a new friendship with another SocialNPC.
 */
public class FriendRequest
{
	private FriendRequestStatus state;
	private SocialNPC requester;
	private SocialNPC requestee;

	/**
	 * Creates a new FriendshipRequest.
	 * 
	 * @param requester The SocialNPC initiating the new relationship
	 * @param requestee The SocialNPC who will respond to the request
	 */
	public FriendRequest(SocialNPC requester, SocialNPC requestee)
	{
		this.requester = requester;
		this.requestee = requestee;
		state = FriendRequestStatus.WAITING;
	}

	/**
	 * The requestee has accepted the requester's initiation of a new friendship.
	 */
	public void accept()
	{
		if (state == FriendRequestStatus.WAITING)
		{
			this.state = FriendRequestStatus.ACCEPTED;
			requestee.newEvent(requester, EventTypes.FRIEND_REQUEST_ACCEPTED);
		}
	}

	/**
	 * The requestee has rejected the requester's attempt to make a new friendship.
	 */
	public void reject()
	{
		if (state == FriendRequestStatus.WAITING)
		{
			this.state = FriendRequestStatus.REJECTED;
			requestee.newEvent(requester, EventTypes.FRIEND_REQUEST_REJECTED);
		}
	}

	public FriendRequestStatus getState()
	{
		return state;
	}

	public SocialNPC getRequester()
	{
		return requester;
	}

	public SocialNPC getRequestee()
	{
		return requestee;
	}

}