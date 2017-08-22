package sw.combat;

import sw.lifeform.Lifeform;

public class Heal extends Action
{
	private int h_magnitude;
	private Lifeform h_target, h_effectTarget;
	private Effect h_effect;
	
	public Heal(int mag, Lifeform target, Effect effect)
	{
		super(ActionType.HEAL);
		h_magnitude = mag;
		h_target = target;
		h_effect = effect;
		if(effect != null)
			h_effectTarget = h_effect.getTarget();
	}
	
	public void apply()
	{
		h_target.takeHeal(h_magnitude);
		if(h_effect != null)
			h_effectTarget.addEffect(h_effect);
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
