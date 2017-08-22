package sw.quest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import sw.socialNetwork.Feelings;
import sw.socialNetwork.FeelingsAttributes;
import sw.socialNetwork.QuestGenerator;

/**
 * @author David Abrams
 * 
 * This set of tests makes sure that HomewreckerQuest behaves properly. There is no test to check
 * the failure of HomewreckerQuest. This is because the quest does nothing special if failed, so
 * it just uses questFailed() in SocialQuest.
 */
public class TestHomewreckerQuest
{

	/**
	 * This test makes sure that a HomewreckerQuest can be initialized properly and that all the
	 * getters and setters work.
	 */
	@Test
	public void testInit()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC intermediateFriend = new MockSocialNPC();
		MockFeelings target1 = new MockFeelings();
		MockFeelings target2 = new MockFeelings();
		ArrayList<Feelings> relationship = new ArrayList<Feelings>();
		relationship.add(target1);
		relationship.add(target2);
		HomewreckerQuest quest = new HomewreckerQuest("da quest", giver, intermediateFriend, relationship, SocialQuestDifficulty.EASY, FeelingsAttributes.INTIMACY);

		assertEquals(giver, quest.getQuestGiver());
		ArrayList<Feelings> holder = quest.getTargetRelationship();
		assertEquals(target1, holder.get(0));
		assertEquals(target2, holder.get(1));
		assertEquals(FeelingsAttributes.INTIMACY, quest.getTargetAttribute());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that a HomewreckerQuest will properly change the intimacy of the target
	 * relationship upon successful completion.
	 */
	@Test
	public void testSuccessfulIntimacy()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC intermediateFriend = new MockSocialNPC();
		MockFeelings target1 = new MockFeelings();
		MockFeelings target2 = new MockFeelings();
		ArrayList<Feelings> relationship = new ArrayList<Feelings>();
		relationship.add(target1);
		relationship.add(target2);
		HomewreckerQuest quest = new HomewreckerQuest("da quest", giver, intermediateFriend, relationship, SocialQuestDifficulty.EASY, FeelingsAttributes.INTIMACY);

		target1.setIntimacy(100);
		target2.setIntimacy(100);

		quest.questSuccessful();

		assertEquals(88, target1.getIntimacy());
		assertEquals(88, target2.getIntimacy());

		quest.setDifficulty(SocialQuestDifficulty.MEDIUM);

		target1.setIntimacy(100);
		target2.setIntimacy(100);
		quest.questSuccessful();

		assertEquals(86, target1.getIntimacy());
		assertEquals(86, target2.getIntimacy());

		quest.setDifficulty(SocialQuestDifficulty.HARD);

		target1.setIntimacy(100);
		target2.setIntimacy(100);
		quest.questSuccessful();

		assertEquals(84, target1.getIntimacy());
		assertEquals(84, target2.getIntimacy());

		quest.setDifficulty(SocialQuestDifficulty.YOUMUSTBEPRO);

		target1.setIntimacy(100);
		target2.setIntimacy(100);
		quest.questSuccessful();

		assertEquals(80, target1.getIntimacy());
		assertEquals(80, target2.getIntimacy());
		
		QuestGenerator.clear();
	}

	/**
	 * This test makes sure that HomewreckerQuest will properly change the trust of the target
	 * relationship upon successful completion.
	 */
	@Test
	public void testSuccessTrust()
	{
		MockSocialNPC giver = new MockSocialNPC();
		MockSocialNPC intermediateFriend = new MockSocialNPC();
		MockFeelings target1 = new MockFeelings();
		MockFeelings target2 = new MockFeelings();
		ArrayList<Feelings> relationship = new ArrayList<Feelings>();
		relationship.add(target1);
		relationship.add(target2);
		HomewreckerQuest quest = new HomewreckerQuest("da quest", giver, intermediateFriend, relationship, SocialQuestDifficulty.EASY, FeelingsAttributes.TRUST);

		quest.questSuccessful();

		assertEquals(-3, target1.getTrend());
		assertEquals(-3, target2.getTrend());
		assertEquals(0, target1.getTrust());
		assertEquals(0, target2.getTrust());

		quest.questSuccessful();

		assertEquals(-3, target1.getTrend());
		assertEquals(-3, target2.getTrend());
		assertEquals(-3, target1.getTrust());
		assertEquals(-3, target2.getTrust());
		
		QuestGenerator.clear();
	}
}
