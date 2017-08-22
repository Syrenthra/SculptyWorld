package sw.quest.reward;

import java.util.ArrayList;

import sw.item.Item;
import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FeelingsAttributes;

/**
 * @author David Abrams and Dr. Girard
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
public class HomewreckerReward extends SocialReward
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
     * @param cost How difficult the quest will be for the player to complete
     * @param targetAttribute The part of the targeted relationship to be damaged
     */
    public HomewreckerReward(Quest quest, NPC questTarget, ArrayList<Feelings> targetRelationships, SocialCapitolCost cost, FeelingsAttributes targetAttribute)
    {
        super(quest, questTarget, cost);

        this.targetRelationship = new ArrayList<Feelings>();
        this.targetRelationship.add(targetRelationships.get(0));
        this.targetRelationship.add(targetRelationships.get(1));
        this.targetAttribute = targetAttribute;
    }

    /**
     * Damaged the selected part of the target relationship.
     */
    @Override
    public Item getItemReward()
    {
        m_quest.getGranter().setLastQuestResult(QuestState.COMPLETED);
        if (targetRelationship.get(0) == null)
        {
            System.err.print("No intermediate Feelings");
        }

        if (targetRelationship.get(1) == null)
        {
            System.err.print("No target Feelings! Giver is " + m_quest.getGranter());
        }

        if (targetAttribute == FeelingsAttributes.INTIMACY)
        {
            targetRelationship.get(0).changeIntimacy(-calculateIntimacyDamage());
            targetRelationship.get(1).changeIntimacy(-calculateIntimacyDamage());
        }
        else if (targetAttribute == FeelingsAttributes.TRUST)
        {
            for (int i = 0; i < 3; i++)
            {
                targetRelationship.get(0).trustDecrement();
                targetRelationship.get(1).trustDecrement();
            }
        }

        return null;
    }

    /**
     * @return The amount of intimacy to remove from the target relationship.
     */
    private int calculateIntimacyDamage()
    {
        return BASE_CHANGE + (m_cost.getCost() / 250);
    }

    public ArrayList<Feelings> getTargetRelationship()
    {
        return targetRelationship;
    }

    public FeelingsAttributes getTargetAttribute()
    {
        return targetAttribute;
    }

    @Override
    public int getXPReward()
    {
        return 0;
    }

    @Override
    public int getGoldReward()
    {
        return 0;
    }

    /**
     * Returns whether this reward will give the player an item or not.
     */
    @Override
    public boolean hasItemReward()
    {
        return false;
    }

    /**
     * Let the quest granter know the quest failed.
     */
    @Override
    public void failedQuest()
    {
        m_quest.getGranter().setLastQuestResult(QuestState.FAILED);

    }

}
