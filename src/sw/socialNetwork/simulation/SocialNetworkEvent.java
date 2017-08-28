package sw.socialNetwork.simulation;

import sw.lifeform.NPC;

/**
 * @author David Abrams
 * 
 * This class is created whenever a SNPC performs an action.
 */
public class SocialNetworkEvent
{
	private NPC sender;
	private NPC target;
	private EventTypes type;
	private boolean readBySim;
	private int specialInfo;

	public SocialNetworkEvent(NPC giver, NPC reciever, EventTypes type)
	{
		init(giver, reciever, type);
	}

	public SocialNetworkEvent(NPC giver, NPC reciever, EventTypes type, int specialInfo)
	{
		this.specialInfo = specialInfo;
		init(giver, reciever, type);
	}

	private void init(NPC giver, NPC reciever, EventTypes type)
	{
		this.sender = giver;
		this.target = reciever;
		this.type = type;
		this.readBySim = false;
	}

	public NPC getSender()
	{
		return sender;
	}

	public NPC getTarget()
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
