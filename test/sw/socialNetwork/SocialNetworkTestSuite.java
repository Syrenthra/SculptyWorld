package sw.socialNetwork;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import sw.lifeform.TestNPC;
import sw.quest.TestQuestGenerator;
import sw.quest.TestTimedQuest;
import sw.quest.reward.TestFavorReward;
import sw.quest.reward.TestGiftReward;
import sw.quest.reward.TestHomewreckerReward;
import sw.quest.reward.TestRequestFavorReward;

/**
 * @author David Abrams
 * 
 * Runs all of the tests for the Dynamic Social Network.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
        	TestNPC.class,
        	TestFavorReward.class,
        	TestGiftReward.class,
        	TestHomewreckerReward.class,
        	TestRequestFavorReward.class,
        	TestTimedQuest.class,
        	TestFeelings.class,
        	TestFriendRequest.class,
        	TestQuestGenerator.class,
        	TestPersonality.class,
        	TestFavor.class
        })

public class SocialNetworkTestSuite
{
}
