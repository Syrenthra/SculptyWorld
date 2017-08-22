package sw.combat;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.combat.Effect;
import sw.combat.EffectType;
import sw.environment.TheWorld;
import sw.lifeform.Player;
import sw.time.GameTimer;


public class TestEffect
{
	@Test
	public void testAddEffect()
	{
        Player dude = new Player(1,"Dude","Desc",50);
        Effect e = new Effect("Null", EffectType.BUFF, null, 1, 10, 1, dude);
        dude.addEffect(e);
        assertEquals(1, dude.getNumEffects());
        assertEquals(e, dude.getEffect(0));
	}
	
	@Test
	public void testEffectTickDamage()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		Player dude = new Player(1,"Dude","Desc",50);
		combat.addTimeObserver(dude);
        Effect e = new Effect("Null", EffectType.DOT, null, 1, 10, 1, dude);
        dude.addEffect(e);
        assertEquals(50, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(49, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testEffectTickHeal()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		Player dude = new Player(1,"Dude","Desc",50);
		combat.addTimeObserver(dude);
        Effect e = new Effect("Null", EffectType.HOT, null, 1, 10, 1, dude);
        dude.addEffect(e);
        assertEquals(50, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(50, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testEffectsTickDamageAndHeal()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		Player dude = new Player(1,"Dude","Desc",50);
		combat.addTimeObserver(dude);
        Effect eDamage = new Effect("Damage", EffectType.DOT, null, 10, 10, 1, dude);
        dude.addEffect(eDamage);
        Effect eHeal = new Effect("Heal", EffectType.HOT, null, 1, 10, 1, dude);
        dude.addEffect(eHeal);
        assertEquals(50, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(41, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testEffectTickSeconds()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		Player dude = new Player(1,"Dude","Desc",50);
		combat.addTimeObserver(dude);
        Effect e = new Effect("Null", EffectType.DOT, null, 10, 9, 3, dude);
        dude.addEffect(e);
        assertEquals(50, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(50, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(50, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(40, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testEffectRemoval()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		Player dude = new Player(1,"Dude","Desc",50);
		combat.addTimeObserver(dude);
        Effect e = new Effect("Null", EffectType.DOT, null, 1, 5, 1, dude);
        dude.addEffect(e);
        assertEquals(50, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(49, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(48, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(47, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(46, dude.getCurrentLifePoints());
        combat.timeChanged();
        assertEquals(45, dude.getCurrentLifePoints());
        assertEquals(0, dude.getNumEffects());
	}
}
