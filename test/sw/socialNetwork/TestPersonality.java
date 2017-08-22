package sw.socialNetwork;

import static org.junit.Assert.*;

import org.junit.Test;

import sw.socialNetwork.Personality;


public class TestPersonality 
{
	@Test
	public void testInit()
	{
		double control = 0.5;
		double grumpiness = 0.5;
		double personability = 0.5;
		int desiredFriends = 5;
		int desiredCapital = 5000;
		
		Personality pers = new Personality(control, grumpiness, personability, desiredFriends, desiredCapital);
		
		assertEquals(control, pers.getControl(), 0.001);
		assertEquals(grumpiness, pers.getGrumpiness(), 0.001);
		assertEquals(personability, pers.getPersonability(), 0.001);
		assertEquals(desiredFriends, pers.getTotalDesiredFriends());
		assertEquals(desiredCapital, pers.getTotalDesiredCapital());
	}
	
	@Test
	public void testValidValues()
	{
		double control = 1.1;
		double grumpiness = 1.2;
		double personability = 1.3;
		int desiredFriends = 20;
		int desiredCapital = 5000;
		
		Personality pers = new Personality(control, grumpiness, personability, desiredFriends, desiredCapital);
		
		assertEquals(0.0, pers.getControl(), 0.001);
		assertEquals(0.0, pers.getGrumpiness(), 0.001);
		assertEquals(0.0, pers.getPersonability(), 0.001);
		assertEquals(0, pers.getTotalDesiredFriends());
		assertEquals(5000, pers.getTotalDesiredCapital());
	}

}
