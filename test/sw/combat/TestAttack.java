package sw.combat;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.item.HandLocation;
import sw.item.Weapon;
import sw.lifeform.Creature;
import sw.lifeform.PC;

public class TestAttack
{

	@Test
	public void testCombat()
	{
		PC dude = new PC(0, "Dude", "Desc", 50);
		Creature orc = new Creature(1, "Blagh", "Desc", 50, 5, 10, 9);
		Weapon w = new Weapon("Caladbolg", "Desc", 100, 5, 10, 1);
		dude.holdInHand(w, HandLocation.RIGHT);
		
		assertEquals(50, orc.getCurrentLifePoints());
		dude.attack(orc);
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(50, orc.getCurrentLifePoints());
		dude.updateTime("This doesn't matter...", 0);
		assertEquals(48, orc.getCurrentLifePoints());
		
	}

}
