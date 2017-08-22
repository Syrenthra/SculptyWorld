package sw.quest.reward;

import sw.item.Item;
import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.SocialCapitolCost;
import sw.socialNetwork.SocialNetwork;

/**
 * @author David Abrams and Dr. Girard
 * 
 * This reward is attached to a quest where one NPC asks another NPC to perform a favor on behalf of the first.
 * Ideally this should work as follows:
 * 1. First NPC creates a quest that has the players talk to a second NPC asking a Favor to be done.
 * 2. The second NPC then creates a quest to do the favor for the first NPC.
 * 
 * During completion of the quest,
 * nothing is different from a FavorQuest. Upon completion of a RequestFavorQuest, the trust counter
 * in the relationship of the SocialNPCs involved is incremented. After the counter hits 3, the
 * trust between the two SocialNPCs starts to increase. Likewise, after 3 consecutive failures, the
 * trust between the SocialNPCs will begin to decrease.
 * 
 * Success: -intimacy increase for both SocialNPCs (amount determined by difficulty of favor)
 * -completion of 3 consecutive RequestFavorQuest allows trust to increase
 * 
 * Failure: -intimacy penalty for both SocialNPCs -after 3 consecutive failures, trust begins to
 * decrease
 */
public class RequestFavorReward extends FavorReward
{

    /**
     * Creates a new RequestFavorQuest.
     * 
     * @param target This is the SocialNPC that asks for the favor
     * @param cost The difficulty of performing this favor
     */
    public RequestFavorReward(Quest quest, NPC target, SocialCapitolCost cost)
    {
        super(quest, target, cost);
    }

    /**
     * When a quest is successfully completed, the NPCs involved will receive the normal
     * increase in the strength of their relationship. Additionally, they will begin to trust each
     * other more.
     * 
     */
    @Override
    public Item getItemReward()
    {
        // First update the relationship based on the success or failure of the quest
        // just like a FavorReward.
        super.getItemReward();
        
        NPC questGiver = m_quest.getGranter();
        SocialNetwork giverNetwork = questGiver.getSocialNetwork();
        
        SocialNetwork targetNetwork = m_target.getSocialNetwork();

        // Now update trust as well.
            if (giverNetwork.hasFriend(m_target) && targetNetwork.hasFriend(questGiver))
            {
                giverNetwork.getRelationships().get(m_target).trustIncrement();
                targetNetwork.getRelationships().get(questGiver).trustIncrement();
            }


        return null;
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
     * When a quest is failed, the SocialNPCs involved will receive the normal penalty to the
     * strength of their relationship. Additionally, their trust will begin to decay.
     */
    @Override
    public void failedQuest()
    {
        super.failedQuest();
        
        NPC questGiver = m_quest.getGranter();
        SocialNetwork giverNetwork = questGiver.getSocialNetwork();
        
        SocialNetwork targetNetwork = m_target.getSocialNetwork();

        if (giverNetwork.hasFriend(m_target) && targetNetwork.hasFriend(questGiver))
        {
            giverNetwork.getRelationships().get(m_target).trustDecrement();
            targetNetwork.getRelationships().get(questGiver).trustDecrement();
        }
    }

}
