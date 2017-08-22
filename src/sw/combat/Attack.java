package sw.combat;

import sw.lifeform.Lifeform;

public class Attack extends Action
{
	private int a_damage;
	private Lifeform a_target, a_effectTarget;
	private AttackType a_type;
	private Effect a_effect;
	
	public Attack(int damage, Lifeform target, AttackType type, Effect effect)
	{
		super(ActionType.ATTACK);
		a_damage = damage;
		a_target = target;
		a_type = type;
		a_effect = effect;
		if(effect != null)
			a_effectTarget = a_effect.getTarget();
	}
	
	public void apply()
	{
		a_target.takeHit(a_damage);
		if(a_effect != null)
			a_effectTarget.addEffect(a_effect);
	}
	
	public int getDamage()
	{
		return a_damage;
	}
	
	public Lifeform getTarget()
	{
		return a_target;
	}
	
	public AttackType getType()
	{
		return a_type;
	}
	
	public Effect getEffect()
	{
		return a_effect;
	}
}
