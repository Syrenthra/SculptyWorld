package sw.socialNetwork;

/**
 * @author David Abrams
 * 
 * Specifies what the multiplier for each relationship rate is.
 */
public enum SocialNetworkDecayRates
{
	HIGH (2.0),
	NORMAL (1.0),
	LOW (0.5);
	
	private double multiplier;
	
	SocialNetworkDecayRates(double multiplier)
	{
		this.multiplier = multiplier;
	}
	
	public double getMultiplier()
	{
		return multiplier;
	}
}
