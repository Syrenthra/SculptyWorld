package sw.quest.reward;

import sw.item.Item;
import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.SocialNetwork;

/**
 * @author David Abrams and Dr. Girard
 * 
 * Creates a quest reward based on the idea that an NPC needs to do a favor for another NPC.
 * So a player should perform a favor for the granting NPC that is for the target NPC.
 * 
 * FavorRewards are generated when neither GiftReward or RequestFavorReward are appropriate. FavorReward
 * is the basic type of SocialReward.
 * 
 * Success:
 * -intimacy increase for both SocialNPCs (amount determined by difficulty of favor)
 * 
 * Failure:
 * -intimacy penalty for both SocialNPCs
 * 
 * 
 * TODO: Currently, FavorQuest only supports one target. Add functionality to support multiple targets.
 * This would require modifying FavorQuest, QuestGenerator, and FavorTarget.
 */
public class FavorReward extends SocialReward
{
    protected final int BASE_CHANGE = 10;

    /**
     * Creates a new FavorReward.
     * 
     * @param quest The quest this reward is attached to.
     * @param target The SocialNPC who will benefit from the favor
     * @param cost The social capitol cost of performing the favor
     */
    public FavorReward(Quest quest, NPC target, SocialCapitolCost cost)
    {
        super(quest, target, cost);
    }

    /**
     * This method performs the necessary actions upon successful completion of the quest. The
     * relationship between the giver and target is strengthened depending on the difficulty of the
     * favor.
     * 
     * Does not return any items as the reward here is a change to the NPC social network.
     */
    @Override
    public Item getItemReward()
    {
        NPC questGiver = m_quest.getGranter();
        SocialNetwork giverNetwork = questGiver.getSocialNetwork();
        SocialNetwork targetNetwork = m_target.getSocialNetwork();

        questGiver.setLastQuestResult(QuestState.COMPLETED);
        if (giverNetwork.hasFriend(m_target) && targetNetwork.hasFriend(questGiver))
        {
            Feelings targetFeels = targetNetwork.getRelationships().get(questGiver);
            Feelings giverFeels = giverNetwork.getRelationships().get(m_target);

            targetFeels.changeIntimacy(calculateRelationshipChange());
            giverFeels.changeIntimacy(calculateRelationshipChange());
            targetFeels.changeSocialDebt(m_cost.getCost() / 2);
            giverFeels.changeSocialDebt(-(m_cost.getCost() / 2));
        }

        return null;
    }

    /**
     * @return The amount that this favor will change the strength of the target relationship
     */
    private int calculateRelationshipChange()
    {
        int value = BASE_CHANGE + (m_cost.getCost() / 250);
        return value;
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
     * This method performs the necessary actions upon failure of the quest. The relationship
     * between the giver and target is weakened based on the difficulty of the favor. The control of
     * a SocialNPC can reduce the penalty to intimacy down to a minimum of 1/3 of the original value
     * rounded down.
     */
    @Override
    public void failedQuest()
    {

        NPC questGiver = m_quest.getGranter();
        SocialNetwork giverNetwork = questGiver.getSocialNetwork();
        SocialNetwork targetNetwork = m_target.getSocialNetwork();
        questGiver.setLastQuestResult(QuestState.FAILED);
        if (giverNetwork.hasFriend(m_target) && targetNetwork.hasFriend(questGiver))
        {
            Feelings targetFeels = targetNetwork.getRelationships().get(questGiver);
            Feelings giverFeels = giverNetwork.getRelationships().get(m_target);

            double change = calculateRelationshipChange();

            targetFeels.changeIntimacy(-(int) ((change * 1.0 / 3.0) + (2.0 / 3.0 * change * (1 - targetNetwork.getControl()))));
            giverFeels.changeIntimacy(-(int) ((change * 1.0 / 3.0) + (2.0 / 3.0 * change * (1 - giverNetwork.getControl()))));
        }

    }
}
