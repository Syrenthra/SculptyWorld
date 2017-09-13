package sw.socialNetwork;

import sw.lifeform.NPC;
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
	private NPC requester;
	private NPC requestee;

	/**
	 * Creates a new FriendshipRequest.
	 * 
	 * @param requester The SocialNPC initiating the new relationship
	 * @param requestee The SocialNPC who will respond to the request
	 */
	public FriendRequest(NPC requester, NPC requestee)
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

	public NPC getRequester()
	{
		return requester;
	}

	public NPC getRequestee()
	{
		return requestee;
	}

}