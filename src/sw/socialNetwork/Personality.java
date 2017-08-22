package sw.socialNetwork;

import sw.lifeform.SocialNPC;

/**
 * @author David Abrams
 *
 * This class holds the information that makes SocialNPCs unique.
 */
public class Personality 
{
	/**
	 * Determines how forgiving this SocialNPC is when other SocialNPCs agree to perform a task and
	 * then fail to complete it. 1.0 = most forgiving, 0.0 = least forgiving. Note that a control
	 * value of 0.0 results in the SocialNPC not gaining any social capital from relationships.
	 */
	protected double control;
	static final double MAX_CONTROL = 1.0;
	static final double MIN_CONTROL = 0.0;
	
	/**
	 * Determines how likely this SocialNPC is to become Angry when a quest that it issued is
	 * failed.
	 */
	protected double grumpiness;
	static final double MAX_GRUMPINESS = 1.0;
	static final double MIN_GRUMPINESS = 0.0;
	
	/**
	 * Determines how easy it is for this SocialNPC to form new relationships. Valid values are 0.0
	 * through 1.0. In practice, this is the percent change that this SNPC has to accept a
	 * FriendRequest.
	 */
	protected double personability;
	static final double MAX_PERSONABILITY = 1.0;
	static final double MIN_PERSONABILITY = 0.0;
	
	//How many relationships this SocialNPC wants to have.
	protected int totalDesiredFriends;

	//How much social capital this SocialNPC wants to have.
	protected int totalDesiredCapital;
	
	/**
	 * Creates a new personality for a SNPC to use.
	 * @param control Must be between 0.0 and 1.0
	 * @param grumpiness Must be between 0.0 and 1.0
	 * @param personability Must be between 0.0 and 1.0
	 * @param desiredFriends Must be <= MAX_FRIENDS in SocialNPC
	 * @param desiredCapital
	 */
	public Personality(double control, double grumpiness, double personability, int desiredFriends, int desiredCapital)
	{
		if(validControl(control))
		{
			this.control = control;
		}else
		{
			this.control = 0.0;
		}
		
		if(validGrumpiness(grumpiness))
		{
			this.grumpiness = grumpiness;
		}else
		{
			grumpiness = 0.0;
		}
		
		if(validPersonability(personability))
		{
			this.personability = personability;
		}else
		{
			this.personability = 0.0;
		}

		if(validDesiredFriends(desiredFriends))
		{
			this.totalDesiredFriends = desiredFriends;
		}else
		{
			this.totalDesiredFriends = 0;
		}

		this.totalDesiredCapital = desiredCapital;
	}
	
	private boolean validControl(double value)
	{
		boolean result = false;
		
		if(value <= MAX_CONTROL && value >= MIN_CONTROL)
		{
			result = true;
		}
		
		return result;
	}
	
	private boolean validGrumpiness(double value)
	{
		boolean result = false;
		
		if(value <= MAX_CONTROL && value >= MIN_CONTROL)
		{
			result = true;
		}
		return result;
	}
	
	private boolean validPersonability(double value)
	{
		boolean result = false;
		if(value <= MAX_PERSONABILITY && value >= MIN_PERSONABILITY)
		{
			result = true;
		}
		return result;
	}
	
	private boolean validDesiredFriends(int value)
	{
		boolean result = false;
		if(value <= SocialNPC.MAX_FRIENDS)
		{
			result = true;
		}
		return result;
	}

	public double getControl() {
		return control;
	}

	public void setControl(double control) {
		if(validControl(control))
		{
			this.control = control;
		}
	}

	public double getGrumpiness() {
		return grumpiness;
	}

	public void setGrumpiness(double grumpiness) {
		if(validGrumpiness(grumpiness))
		{
			this.grumpiness = grumpiness;
		}
	}

	public double getPersonability() {
		return personability;
	}

	public void setPersonability(double personability) {
		if(validPersonability(personability))
		{
			this.personability = personability;
		}
	}

	public int getTotalDesiredFriends() {
		return totalDesiredFriends;
	}

	public void setTotalDesiredFriends(int totalDesiredFriends) {
		if(validDesiredFriends(totalDesiredFriends))
		{
			this.totalDesiredFriends = totalDesiredFriends;
		}
	}

	public int getTotalDesiredCapital() {
		return totalDesiredCapital;
	}

	public void setTotalDesiredCapital(int totalDesiredCapital) {
		this.totalDesiredCapital = totalDesiredCapital;
	}
}
