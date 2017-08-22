package sw.socialNetwork;

import static org.junit.Assert.*;
import sw.time.*;
import org.junit.Test;


/**
 * 
 * @author David Abrams
 * 
 * The purpose of this set of tests is to ensure proper functionality of the Feelings class.
 *
 */
public class TestFeelings
{

	/**
	 * This test makes sure that Feelings can be properly initialized.
	 */
    @Test
    public void testInit()
    {
    	Feelings theFeels = new Feelings();
    	
    	assertEquals(35, theFeels.getIntimacy());
    	assertEquals(0, theFeels.getTrust());
    	assertEquals(0, theFeels.getTrend());
    	assertEquals(0, theFeels.getSocialDebtOwed());
    	assertEquals(SocialNetworkDecayRates.NORMAL, theFeels.getDecayRate());
    	
    	Feelings theFeels2 = new Feelings(SocialNetworkDecayRates.HIGH);
    	
    	assertEquals(SocialNetworkDecayRates.HIGH, theFeels2.getDecayRate());
    }
    
    /**
     * This test makes sure that the getters and setters work correctly.
     */
    @Test
    public void testGettersSetters()
    {
    	Feelings theFeels = new Feelings();
    	
    	theFeels.setIntimacy(50);
    	assertEquals(50, theFeels.getIntimacy());
    	//illegal values
    	theFeels.setIntimacy(101);
    	assertEquals(50, theFeels.getIntimacy());
    	theFeels.setIntimacy(0);
    	assertEquals(50, theFeels.getIntimacy());
    	
    	theFeels.changeIntimacy(20);
    	assertEquals(70, theFeels.getIntimacy());
    	
    	theFeels.changeIntimacy(-20);
    	assertEquals(50, theFeels.getIntimacy());
    	
    	theFeels.setTrust(5);
    	assertEquals(5, theFeels.getTrust());
    	//illegal values
    	theFeels.setTrust(6);
    	assertEquals(5, theFeels.getTrust());
    	theFeels.setTrust(-6);
    	assertEquals(5, theFeels.getTrust());
    	
    	theFeels.setTrend(3);
    	assertEquals(3, theFeels.getTrend());
    	//illegal values
    	theFeels.setTrend(4);
    	assertEquals(3, theFeels.getTrend());
    	theFeels.setTrend(-4);
    	assertEquals(3, theFeels.getTrend());
    	
    	theFeels.setSocialDebtOwed(1000);
    	assertEquals(1000, theFeels.getSocialDebtOwed());
    	
    	theFeels.changeSocialDebt(-500);
    	assertEquals(500, theFeels.getSocialDebtOwed());
    	
    	assertEquals(5, Feelings.getMaxTrust());
    	assertEquals(-5, Feelings.getMinTrust());
    	assertEquals(100, Feelings.getMaxIntimacy());
    	assertEquals(1, Feelings.getMinIntimacy());
    	assertEquals(3, Feelings.getMaxTrend());
    	assertEquals(-3, Feelings.getMinTrend());
    }
    
    @Test
    public void testToString()
    {
    	Feelings feels = new Feelings();
    	
    	assertEquals("[35]", feels.toString());
    }
    
    
    /**
     * Make sure that changeIntimacy works properly.
     */
    @Test
    public void testChangeIntimacy()
    {
    	Feelings feels = new Feelings();
    	
    	feels.changeIntimacy(200);

    	//new value exceeds max, so intimacy should be set to max
    	assertEquals(Feelings.MAX_INTIMACY, feels.getIntimacy());
    	
    	feels.changeIntimacy(-200);
    	
    	//new value is below min, so intimacy should be set to min
    	assertEquals(Feelings.MIN_INTIMACY, feels.getIntimacy());
    	
    	feels.changeIntimacy(50);
    	
    	//values that put intimacy between min and max should be allowed
    	assertEquals(51, feels.getIntimacy());
    	
    }
    
    /**
     * This test makes sure that Feelings can properly evaluate its contribution to the
     * social worth of a relationship.
     */
    @Test
    public void testCalculateSocialWorth()
    {
    	Feelings theFeels = new Feelings();
    	
    	assertEquals(70, theFeels.calculateSocialWorth());
    	
    	theFeels.setIntimacy(100);
    	theFeels.setTrust(5);
    	assertEquals(1000, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(4);
    	assertEquals(800, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(3);
    	assertEquals(600, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(2);
    	assertEquals(400, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(1);
    	assertEquals(200, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(0);
    	assertEquals(200, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(-1);
    	assertEquals(180, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(-2);
    	assertEquals(160, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(-3);
    	assertEquals(140, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(-4);
    	assertEquals(120, theFeels.calculateSocialWorth());
    	
    	theFeels.setTrust(-5);
    	assertEquals(100, theFeels.calculateSocialWorth());
    }
    
    /**
     * This test makes sure that when a trust-building quest is successfully completed,
     * Feelings properly updates trustTrend and Trust. Trust should only increase after 
     * trustTrend is at 3. Trust should only decrease when trustTruend is at -3.
     */
    @Test
    public void testTrustChange()
    {
    	Feelings theFeels = new Feelings();
    	
    	theFeels.trustIncrement();
    	assertEquals(1, theFeels.getTrend());
    	assertEquals(0, theFeels.getTrust());
    	
    	theFeels.trustIncrement();
    	assertEquals(2, theFeels.getTrend());
    	assertEquals(0, theFeels.getTrust());
    	
    	theFeels.trustIncrement();
    	assertEquals(3, theFeels.getTrend());
    	assertEquals(0, theFeels.getTrust());
    	
    	theFeels.trustIncrement();
    	assertEquals(3, theFeels.getTrend());
    	assertEquals(1, theFeels.getTrust());
    	
    	//if trustIncrement() is called when trust is already 5, nothing should change
    	theFeels.setTrust(5);
    	theFeels.trustIncrement();
    	assertEquals(3, theFeels.getTrend());
    	assertEquals(5, theFeels.getTrust());
    	
    	theFeels.trustDecrement();
    	assertEquals(2, theFeels.getTrend());
    	assertEquals(5, theFeels.getTrust());
    	
    	theFeels.setTrend(-2);
    	theFeels.trustDecrement();
    	assertEquals(-3, theFeels.getTrend());
    	assertEquals(5, theFeels.getTrust());
    	
    	theFeels.trustDecrement();
    	assertEquals(-3, theFeels.getTrend());
    	assertEquals(4, theFeels.getTrust());
    	
    	theFeels.setTrust(-4);
    	theFeels.trustDecrement();
    	assertEquals(-3, theFeels.getTrend());
    	assertEquals(-5, theFeels.getTrust());
    	
    	//if trust is already -5 and trustDecrement() is called, nothing should change
    	theFeels.trustDecrement();
    	assertEquals(-3, theFeels.getTrend());
    	assertEquals(-5, theFeels.getTrust());
    }
    
    
    
    /**
     * This test makes sure that Feelings is aware of time passing and that
     * relationships decay at the proper rate.
     */
    @Test
    public void testDecay()
    {
    	Feelings plusFive = new Feelings();
    	Feelings plusFour = new Feelings();
    	Feelings plusThree = new Feelings();
    	Feelings plusTwo = new Feelings();
    	Feelings plusOne = new Feelings();
    	Feelings zero = new Feelings();
    	Feelings minusOne = new Feelings();
    	Feelings minusTwo = new Feelings();
    	Feelings minusThree = new Feelings();
    	Feelings minusFour = new Feelings();
    	Feelings minusFive = new Feelings();
    	GameTimer time = new GameTimer("Test timer", 0);
    	
    	plusFive.setIntimacy(100);
    	plusFour.setIntimacy(100);
    	plusThree.setIntimacy(100);
    	plusTwo.setIntimacy(100);
    	plusOne.setIntimacy(100);
    	zero.setIntimacy(100);
    	minusOne.setIntimacy(100);
    	minusTwo.setIntimacy(100);
    	minusThree.setIntimacy(100);
    	minusFour.setIntimacy(100);
    	minusFive.setIntimacy(100);
    	
    	plusFive.setTrust(5);
    	plusFour.setTrust(4);
    	plusThree.setTrust(3);
    	plusTwo.setTrust(2);
    	plusOne.setTrust(1);
    	zero.setTrust(0);
    	minusOne.setTrust(-1);
    	minusTwo.setTrust(-2);
    	minusThree.setTrust(-3);
    	minusFour.setTrust(-4);
    	minusFive.setTrust(-5);
    	
    	time.addTimeObserver(plusFive);
    	time.addTimeObserver(plusFour);
    	time.addTimeObserver(plusThree);
    	time.addTimeObserver(plusTwo);
    	time.addTimeObserver(plusOne);
    	time.addTimeObserver(zero);
    	time.addTimeObserver(minusOne);
    	time.addTimeObserver(minusTwo);
    	time.addTimeObserver(minusThree);
    	time.addTimeObserver(minusFour);
    	time.addTimeObserver(minusFive);

    	time.timeChanged();
    	
    	assertEquals(99, plusFive.getIntimacy());
    	assertEquals(99, plusFour.getIntimacy());
    	assertEquals(98, plusThree.getIntimacy());
    	assertEquals(98, plusTwo.getIntimacy());
    	assertEquals(97, plusOne.getIntimacy());
    	assertEquals(97, zero.getIntimacy());
    	assertEquals(97, minusOne.getIntimacy());
    	assertEquals(96, minusTwo.getIntimacy());
    	assertEquals(96, minusThree.getIntimacy());
    	assertEquals(95, minusFour.getIntimacy());
    	assertEquals(95, minusFive.getIntimacy());
  	
    }
}
