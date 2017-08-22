package sw.quest;

import java.util.ArrayList;

import sw.lifeform.SocialNPC;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FeelingsAttributes;

/**
 * @author David Abrams
 * 
 * Hurts a target relationship
 * 
 * Created when a SocialNPC is angry.
 * 
 * Success: -intimacy of target relationship decreases. (amount of decrease dependent on quest
 * difficulty)
 * 
 * Failure: -no change of target relationship
 */
public class HomewreckerQuest extends SocialQuest
{
	protected ArrayList<Feelings> targetRelationship;
	protected FeelingsAttributes targetAttribute;
	protected final int BASE_CHANGE = 10;

	/**
	 * Creates a new HomewreckerQuest.
	 * 
	 * @param name The name of the quest
	 * @param questGiver The SocialNPC that is handing the quest out
	 * @param targetRelationships An array containing the two Feelings that make up the relationship
	 * to be damaged
	 * @param difficulty How difficult the quest will be for the player to complete
	 * @param targetAttribute The part of the targeted relationship to be damaged
	 */
	public HomewreckerQuest(String name, SocialNPC questGiver, SocialNPC questTarget, ArrayList<Feelings> targetRelationships, SocialQuestDifficulty difficulty, FeelingsAttributes targetAttribute)
	{
		super(name, "This is a HomewreckerQuest", questGiver, questTarget, difficulty);
		this.targetRelationship = new ArrayList<Feelings>();
		this.targetRelationship.add(targetRelationships.get(0));
		this.targetRelationship.add(targetRelationships.get(1));
		this.targetAttribute = targetAttribute;
	}

	/**
	 * Damaged the selected part of the target relationship.
	 */
	@Override
	public void questSuccessful()
	{
		if(targetRelationship.get(0) == null)
		{
			System.err.print("No intermediate Feelings");
		}
		
		if(targetRelationship.get(1) == null)
		{
			System.err.print("No target Feelings! Giver is " + questGiver);
		}
		

		
		if (targetAttribute == FeelingsAttributes.INTIMACY)
		{			
			targetRelationship.get(0).changeIntimacy(-calculateIntimacyDamage());
			targetRelationship.get(1).changeIntimacy(-calculateIntimacyDamage());
		} else if (targetAttribute == FeelingsAttributes.TRUST)
		{
			for (int i = 0; i < 3; i++)
			{
				targetRelationship.get(0).trustDecrement();
				targetRelationship.get(1).trustDecrement();
			}
		}
	}

	/**
	 * @return The amount of intimacy to remove from the target relationship.
	 */
	private int calculateIntimacyDamage()
	{
		return BASE_CHANGE + (difficulty.getDifficulty() / 250);
	}

	public ArrayList<Feelings> getTargetRelationship()
	{
		return targetRelationship;
	}

	public FeelingsAttributes getTargetAttribute()
	{
		return targetAttribute;
	}

}
