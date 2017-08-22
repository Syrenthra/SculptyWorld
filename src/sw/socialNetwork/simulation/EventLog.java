package sw.socialNetwork.simulation;

/**
 * @author David Abrams
 *
 * This class keeps track of events that happen during a run of the simulation.
 */
public class EventLog
{
	private int numFRsSent = 0;
	private int numFRsReceived = 0;
	private int numFRsAccepted = 0;
	private int numFRsRejected = 0;
	private int numGiftQuestCreated = 0;
	private int numFavorQuestCreated = 0;
	private int numReqFavQuestCreated = 0;
	private int numHomewreckerQuestCreated = 0;
	private int numQuestSuccessful = 0;
	private int numQuestFailed = 0;
	private int numFriendshipsCreated = 0;
	private int numFriendshipsFailed = 0;
	private int totalCapitalGained = 0;
	private int capitalSpentOnQuests = 0;
	private int numTimesChangedToAngry = 0;
	private int numTimesChangedToHappy = 0;

	public void clearEvents()
	{
		numFRsSent = 0;
		numFRsReceived = 0;
		numFRsAccepted = 0;
		numFRsRejected = 0;
		numGiftQuestCreated = 0;
		numFavorQuestCreated = 0;
		numReqFavQuestCreated = 0;
		numHomewreckerQuestCreated = 0;
		numQuestSuccessful = 0;
		numQuestFailed = 0;
		numFriendshipsCreated = 0;
		numFriendshipsFailed = 0;
		totalCapitalGained = 0;
		capitalSpentOnQuests = 0;
		numTimesChangedToAngry = 0;
		numTimesChangedToHappy = 0;
	}
	
	public void incrementNumFRsSent()
	{
		numFRsSent++;
	}
	
	public void incrementNumFRsReceived()
	{
		numFRsReceived++;
	}
	
	public void incrementnumFRsAccepted()
	{
		numFRsAccepted++;
	}
	
	public void incrementNumFRsRejected()
	{
		numFRsRejected++;
	}
	
	public void incrementNumGiftQuestCreated()
	{
		numGiftQuestCreated++;
	}
	
	public void incrementNumFavorQuestCreated()
	{
		numFavorQuestCreated++;
	}
	
	public void incrementNumReqFavQuestCreated()
	{
		numReqFavQuestCreated++;
	}
	
	public void incrementNumHomewreckerQuestCreated()
	{
		numHomewreckerQuestCreated++;
	}
	
	public void incrementNumQuestSuccessful()
	{
		numQuestSuccessful++;
	}
	
	public void incrementNumQuestFailed()
	{
		numQuestFailed++;
	}
	
	public void incrementNumFriendshipsCreated()
	{
		numFriendshipsCreated++;
	}
	
	public void incrementNumFriendshipsFailed()
	{
		numFriendshipsFailed++;
	}
	
	public void incrementTotalCapitalGained(int amt)
	{
		totalCapitalGained += amt;
	}
	
	public void incrementCapitalSpentOnQuests(int amt)
	{
		capitalSpentOnQuests += amt;
	}
	
	public void incrementNumTimesChangedToAngry()
	{
		numTimesChangedToAngry++;
	}
	
	public void incrementNumTimesChangedToHappy()
	{
		numTimesChangedToHappy++;
	}
	
	public int getNumFRsSent()
	{
		return numFRsSent;
	}

	public int getNumFRsReceived()
	{
		return numFRsReceived;
	}

	public int getNumFRsAccepted()
	{
		return numFRsAccepted;
	}

	public int getNumFRsRejected()
	{
		return numFRsRejected;
	}

	public int getNumGiftQuestCreated()
	{
		return numGiftQuestCreated;
	}

	public int getNumFavorQuestCreated()
	{
		return numFavorQuestCreated;
	}

	public int getNumReqFavQuestCreated()
	{
		return numReqFavQuestCreated;
	}

	public int getNumHomewreckerQuestCreated()
	{
		return numHomewreckerQuestCreated;
	}

	public int getNumQuestSuccessful()
	{
		return numQuestSuccessful;
	}

	public int getNumQuestFailed()
	{
		return numQuestFailed;
	}

	public int getNumFriendshipsCreated()
	{
		return numFriendshipsCreated;
	}

	public int getNumFriendshipsFailed()
	{
		return numFriendshipsFailed;
	}

	public int getTotalCapitalGained()
	{
		return totalCapitalGained;
	}

	public int getCapitalSpentOnQuests()
	{
		return capitalSpentOnQuests;
	}

	public int getNumTimesChangedToAngry()
	{
		return numTimesChangedToAngry;
	}

	public int getNumTimesChangedToHappy()
	{
		return numTimesChangedToHappy;
	}
}
