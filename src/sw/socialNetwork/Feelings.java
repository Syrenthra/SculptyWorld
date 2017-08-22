package sw.socialNetwork;

import sw.time.TimeObserver;

/**
 * The purpose of this class is to capture how one SocialNPC in a relationship
 * regards the other.
 * 
 * @author David Abrams
 * 
 */
public class Feelings implements TimeObserver
{
	static protected final int MAX_INTIMACY = 100;
	static protected final int MIN_INTIMACY = 1;
	static protected final int MAX_TRUST = 5;
	static protected final int MIN_TRUST = -5;
	static protected final int MAX_TREND = 3;
	static protected final int MIN_TREND = -3;

	protected int intimacy;
	protected int trust;
	protected int trustTrend;
	protected int socialDebtOwed; // positive value is debt owed to other SocialNPC
	protected SocialNetworkDecayRates decayRate;

	/**
	 * Creates a new Feelings with a normal decay rate.
	 */
	public Feelings()
	{
		decayRate = SocialNetworkDecayRates.NORMAL;
		init();
	}
	
	/**
	 * Creates a new Feelings. The decay rate must be specified.
	 * @param decayRate The rate at which the relationship decays
	 */
	public Feelings(SocialNetworkDecayRates decayRate)
	{
		this.decayRate = decayRate;
		init();
	}
	
	private void init()
	{
		intimacy = 35;
		trust = 0;
		trustTrend = 0;
		socialDebtOwed = 0;
	}

	/**
	 * Calculates the social worth of this relationship. Three formulae are used:
	 * If trust is greater than zero, social worth = trust * intimacy
	 * If trust is zero, social worth = intimacy
	 * If trust is less than zero, social worth = intimacy - (intimacy * (|trust|/10))
	 * 
	 * @return The social worth of this relationship
	 */
	public int calculateSocialWorth()
	{
		int socialWorth = 0;
		
		if(trust > 0)
		{
			socialWorth = trust * intimacy;
		}else if(trust == 0)
		{
			socialWorth = intimacy;
		}else if (trust < 0)
		{
			socialWorth = intimacy - (int)Math.ceil((intimacy * (Math.abs(trust) / 10.0)));
		}
		
		socialWorth *= 2;
		
		return socialWorth;
	}

	/**
	 * This method is to be called after a trust-related SocialQuest is
	 * successfully completed. If the trust counter is less than 3, it is
	 * incremented. If the trust counter is 3, then trust is increased.
	 */
	public void trustIncrement()
	{
		if (trustTrend < 3)
		{
			trustTrend++;
		} else if (trustTrend == 3 && trust < 5) // trust may not exceed 5
		{
			trust++;
		}
	}

	/**
	 * This method is to be called after a trust-related SocialQuest is failed.
	 * If the trust counter is greater than -3, it is decremented. If the trust
	 * counter is -3, trust decreases.
	 */
	public void trustDecrement()
	{
		if (trustTrend > -3)
		{
			trustTrend--;
		} else if (trustTrend == -3 && trust > -5) // trust may not drop below -5
		{
			trust--;
		}
	}

	public int getIntimacy()
	{
		return intimacy;
	}

	/**
	 * @param intimacy Must be between 1 and 100
	 */
	public void setIntimacy(int intimacy)
	{
		if (intimacy <= MAX_INTIMACY && intimacy >= MIN_INTIMACY)
		{
			this.intimacy = intimacy;
		}
	}

	/**
	 * Increases the intimacy tracked by this Feelings by the given value.
	 * 
	 * @param changeAmt Amount to adjust intimacy by
	 */
	public void changeIntimacy(int changeAmt)
	{
		int newIntimacy;
		
		if(changeAmt + intimacy >= MAX_INTIMACY)
		{
			newIntimacy = MAX_INTIMACY;
		}else if(changeAmt + intimacy <= MIN_INTIMACY)
		{
			newIntimacy = MIN_INTIMACY;
		}else
		{
			newIntimacy = intimacy + changeAmt;
		}
		
		
		setIntimacy(newIntimacy);
	}

	public int getTrust()
	{
		return trust;
	}

	/**
	 * @param trust Must be between -5 and 5
	 */
	public void setTrust(int trust)
	{
		if (trust <= MAX_TRUST && trust >= MIN_TRUST)
		{
			this.trust = trust;
		}
	}

	public int getTrend()
	{
		return trustTrend;
	}

	/**
	 * @param trend Must be between -3 and 3
	 */
	public void setTrend(int trend)
	{
		if (trend <= MAX_TREND && trend >= MIN_TREND)
		{
			this.trustTrend = trend;
		}
	}

	public int getSocialDebtOwed()
	{
		return socialDebtOwed;
	}

	public void setSocialDebtOwed(int socialDebtOwed)
	{
		this.socialDebtOwed = socialDebtOwed;
	}

	public void changeSocialDebt(int changeAmt)
	{
		socialDebtOwed += changeAmt;
	}

	/**
	 * The intimacy of a relationship decays according to its trust.
	 * 
	 * @param name not used
	 * @param time not used
	 */
	@Override
	public void updateTime(String name, int time)
	{
		changeIntimacy(-calcDecay());
	}

	/**
	 * The intimacy of a relationship decays based on the current level of trust in the
	 * relationship and the overall decay rate of the network.
	 * 
	 * trust value      : -5 -4 -3 -2 -1 0 1 2 3 4 5
	 * high decay rate  : 2x normal (rounded up)
	 * normal decay rate:  5  5  4  4  3 3 3 2 2 1 1
	 * low decay rate   : 0.5x normal (rounded up)
	 * 
	 * @return amount of intimacy lost in 1 turn
	 */
	public int calcDecay()
	{
		int decay = 0;
		
		/**
		 * TODO: Make this conditional prettier.
		 */
		if(trust == 4 || trust == 5)
		{
			decay = 1;
		}else if(trust == 3 || trust == 2)
		{
			decay = 2;
		}else if(trust == 1 || trust == 0 || trust == -1)
		{
			decay = 3;
		}else if(trust == -2 || trust == -3)
		{
			decay = 4;
		}else if(trust == -4 || trust == -5)
		{
			decay = 5;
		}
		
		return (int)Math.ceil(decay * decayRate.getMultiplier());
	}
	
	public SocialNetworkDecayRates getDecayRate()
	{
		return decayRate;
	}

	static public int getMaxTrust()
	{
		return MAX_TRUST;
	}

	static public int getMinTrust()
	{
		return MIN_TRUST;
	}
	
	static public int getMaxIntimacy()
	{
		return MAX_INTIMACY;
	}
	
	static public int getMinIntimacy()
	{
		return MIN_INTIMACY;
	}
	
	static public int getMaxTrend()
	{
		return MAX_TREND;
	}
	
	static public int getMinTrend()
	{
		return MIN_TREND;
	}
	
	@Override
	public String toString()
	{
		return "["+intimacy+"]";
	}

}
