package sw.socialNetwork.simulation;

import sw.lifeform.SocialNPC;

/**
 * @author David Abrams
 * 
 * This class is created whenever a SNPC performs an action.
 */
public class SocialNetworkEvent
{
	private SocialNPC sender;
	private SocialNPC target;
	private EventTypes type;
	private boolean readBySim;
	private int specialInfo;

	public SocialNetworkEvent(SocialNPC giver, SocialNPC reciever, EventTypes type)
	{
		init(giver, reciever, type);
	}

	public SocialNetworkEvent(SocialNPC giver, SocialNPC reciever, EventTypes type, int specialInfo)
	{
		this.specialInfo = specialInfo;
		init(giver, reciever, type);
	}

	private void init(SocialNPC giver, SocialNPC reciever, EventTypes type)
	{
		this.sender = giver;
		this.target = reciever;
		this.type = type;
		this.readBySim = false;
	}

	public SocialNPC getSender()
	{
		return sender;
	}

	public SocialNPC getTarget()
	{
		return target;
	}

	public EventTypes getType()
	{
		return type;
	}

	public void read()
	{
		readBySim = true;
	}

	public boolean getRead()
	{
		return readBySim;
	}

	public int getSpecialInfo()
	{
		return specialInfo;
	}

	@Override
	public String toString()
	{
		return this.type.toString();
	}
}
