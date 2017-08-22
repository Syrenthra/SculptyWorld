package sw.combat;

import sw.lifeform.Lifeform;

public class Attack extends Action
{
	private int a_damage;
	private Lifeform a_target;
	private AttackType a_type;
	
	public Attack(int damage, Lifeform target, AttackType type)
	{
		super(ActionType.ATTACK);
		a_damage = damage;
		a_target = target;
		a_type = type;
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
}
