package sw.combat;

import sw.lifeform.Lifeform;

public class Heal extends Action
{
	private int h_magnitude;
	private Lifeform h_target;
	
	public Heal(int mag, Lifeform target)
	{
		super(ActionType.HEAL);
		h_magnitude = mag;
		h_target = target;
	}
	
	public int getMagnitude()
	{
		return h_magnitude;
	}
	
	public Lifeform getTarget()
	{
		return h_target;
	}
}
