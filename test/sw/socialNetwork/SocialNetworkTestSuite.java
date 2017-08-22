package sw.socialNetwork;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import sw.item.TestFavorTarget;
import sw.lifeform.TestSocialNPC;
import sw.quest.TestFavorQuest;
import sw.quest.TestGiftQuest;
import sw.quest.TestHomewreckerQuest;
import sw.quest.TestRequestFavorQuest;
import sw.quest.TestSocialQuest;
import sw.socialNetwork.simulation.TestEventGeneration;

/**
 * @author David Abrams
 * 
 * Runs all of the tests for the Dynamic Social Network.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
        	TestSocialNPC.class,
        	TestFavorQuest.class,
        	TestFavorTarget.class,
        	TestGiftQuest.class,
        	TestHomewreckerQuest.class,
        	TestRequestFavorQuest.class,
        	TestSocialQuest.class,
        	TestFeelings.class,
        	TestFriendRequest.class,
        	TestQuestGenerator.class,
        	TestEventGeneration.class,
        	TestPersonality.class,
        	TestFavor.class
        })

public class SocialNetworkTestSuite
{
}
