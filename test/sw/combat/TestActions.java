package sw.combat;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.environment.TheWorld;
import sw.lifeform.Creature;
import sw.lifeform.PC;
import sw.time.GameTimer;

public class TestActions
{	
	@Test
	public void testAttack()
	{
		PC dude = new PC(1,"Dude","Desc",50);
		Attack a = new Attack(50, dude, AttackType.BLUDGEONING, null);
		
		assertEquals(50, a.getDamage());
		assertEquals(dude, a.getTarget());
		assertEquals(AttackType.BLUDGEONING, a.getType());
		assertEquals(null, a.getEffect());
	}
	
	@Test
	public void testAttackWithEffect()
	{
		PC dude = new PC(1,"Dude","Desc",50);
		Effect e = new Effect("TestEffect", EffectType.DEBUFF, CharacterStat.STRENGTH, 5, 60, 0, dude);
		Attack a = new Attack(50, dude, AttackType.BLUDGEONING, e);
		
		assertEquals(0, dude.getNumEffects());
		
		a.apply();
		
		assertEquals(50, a.getDamage());
		assertEquals(dude, a.getTarget());
		assertEquals(AttackType.BLUDGEONING, a.getType());
		assertEquals(e, a.getEffect());
		assertEquals(1, dude.getNumEffects());
		assertEquals(0, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testAttackWithDot()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		PC dude = new PC(1,"Dude","Desc",50);
		combat.addTimeObserver(dude);
		Effect e = new Effect("TestEffect", EffectType.DOT, null, 5, 60, 0, dude);
		Attack a = new Attack(10, dude, AttackType.BLUDGEONING, e);
		
		assertEquals(0, dude.getNumEffects());
		assertEquals(50, dude.getCurrentLifePoints());
		combat.timeChanged();
		assertEquals(50, dude.getCurrentLifePoints());
		
		a.apply();
		
		assertEquals(1, dude.getNumEffects());
		assertEquals(40, dude.getCurrentLifePoints());
		combat.timeChanged();
		assertEquals(35, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testAttackWithBenefit()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		PC dude = new PC(1,"Dude","Desc",50);
		Creature cat = new Creature(0, "Cat", "Desc", 50, 5, 0, 5);
		combat.addTimeObserver(dude);
		combat.addTimeObserver(cat);
		
		dude.takeHit(20);
		Effect e = new Effect("TestEffect", EffectType.HOT, null, 5, 60, 0, dude);
		Attack a = new Attack(10, cat, AttackType.BLUDGEONING, e);
		
		assertEquals(30, dude.getCurrentLifePoints());
		assertEquals(0, dude.getNumEffects());
		
		a.apply();
		
		assertEquals(30, dude.getCurrentLifePoints());
		assertEquals(1, dude.getNumEffects());
		assertEquals(40, cat.getCurrentLifePoints());
		combat.timeChanged();
		assertEquals(35, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testHeal()
	{
		PC dude = new PC(1, "Dude", "Desc", 50);
		Heal h = new Heal(10, dude, null);
		dude.takeHit(20);
		
		assertEquals(30, dude.getCurrentLifePoints());
		h.apply();
		assertEquals(40, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testHealWithEffect()
	{
		PC dude = new PC(1, "Dude", "Desc", 50);
		Effect e = new Effect("TestEffect", EffectType.BUFF, CharacterStat.CONSTITUTION, 2, 60, 0, dude);
		Heal h = new Heal(10, dude, e);
		
		assertEquals(0, dude.getNumEffects());
		
		h.apply();
		
		assertEquals(1, dude.getNumEffects());
	}
	
	@Test
	public void testHealWithHot()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		PC dude = new PC(1, "Dude", "Desc", 50);
		combat.addTimeObserver(dude);
		Effect e = new Effect("TestEffect", EffectType.HOT, null, 5, 60, 0, dude);
		Heal h = new Heal(10, dude, e);
		
		dude.takeHit(30);
		
		h.apply();
		assertEquals(30, dude.getCurrentLifePoints());
		combat.timeChanged();
		assertEquals(35, dude.getCurrentLifePoints());
		combat.timeChanged();
		assertEquals(40, dude.getCurrentLifePoints());
	}
	
	@Test
	public void testHealWithDamage()
	{
		GameTimer combat = new GameTimer(TheWorld.COMBAT_TIMER, 1000);
		PC dude = new PC(1,"Dude","Desc",50);
		Creature cat = new Creature(0, "Cat", "Desc", 50, 5, 0, 5);
		combat.addTimeObserver(dude);
		combat.addTimeObserver(cat);
		
		dude.takeHit(20);
		Effect e = new Effect("TestEffect", EffectType.DOT, null, 5, 60, 0, cat);
		Heal h = new Heal(10, dude, e);
		
		assertEquals(30, dude.getCurrentLifePoints());
		assertEquals(0, dude.getNumEffects());
		
		h.apply();
		
		assertEquals(40, dude.getCurrentLifePoints());
		assertEquals(1, cat.getNumEffects());
		assertEquals(50, cat.getCurrentLifePoints());
		combat.timeChanged();
		assertEquals(45, cat.getCurrentLifePoints());
	}
}
