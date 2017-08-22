package sw.combat;

import sw.lifeform.Lifeform;

public class Effect
{
	private String e_name;
	private EffectType e_type;
	private CharacterStat e_stat;
	private int e_magnitude, e_duration, e_frequency, e_tick;
	private Lifeform e_entity;
	private boolean e_removalFlag;
	
	public Effect(String name, EffectType type, CharacterStat stat, int effectMagnitude, int duration, int frequency, Lifeform entity)
	{
		e_name = name;
		e_type = type;
		e_stat = stat;	// NULL for DOT/HOT effects
		e_magnitude = effectMagnitude;
		e_duration = duration;
		e_frequency = frequency;
		e_tick = 0;
		e_entity = entity;
		e_removalFlag = false;
	}
	
	public void updateEffect()
	{
		if(e_entity != null)
		{
			e_tick++;
			if(e_tick >= e_frequency)
			{
				e_tick = 0;
				switch(e_type)
				{
					case DOT:
						e_entity.takeHit(e_magnitude);
						break;
					case HOT:
						e_entity.takeHeal(e_magnitude);
						break;
					default:
						break;
				}
			}
		
			e_duration--;
			if(e_duration <= 0)
			{
				e_removalFlag = true;
			}
		}
	}
	
	public String getName()
	{
		return e_name;
	}
	
	public EffectType getType()
	{
		return e_type;
	}
	
	public CharacterStat getStatEffected()
	{
		return e_stat;
	}
	
	public int getMagnitude()
	{
		return e_magnitude;
	}
	
	public boolean getRemovalFlag()
	{
		return e_removalFlag;
	}
	
	public Lifeform getTarget()
	{
		return e_entity;
	}
}
