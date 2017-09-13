package sw.quest.reward;

import sw.item.Item;
import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.SocialNetwork;
import sw.socialNetwork.SocialNetworkDecayRates;
import sw.socialNetwork.simulation.EventTypes;

/**
 * @author David Abrams and Dr. Girard
 * 
 * Assumes the player completed a Quest that required the player to deliver a gift to a certain SocialNPC on the
 * behalf of another.
 * 
 * GiftRewards are created when a SocialNPC wants to: -form a new relationship -significantly improve
 * the value of an existing relationship -repay another SocialNPC for a gift
 * 
 * Success: -increase intimacy for both SocialNPCs (amount depends on value of gift) -bonus intimacy
 * if the gift is one that the receiving SocialNPCs has a preference for -receiver is socially
 * indebted to the giver (amount depends on value of gift)
 * 
 * TODO: Need to figure out how to tie the gift to the Task or if we need to.
 * 
 * Failure: -intimacy penalty for both SocialNPCs
 */
public class GiftReward extends SocialReward
{
    /**
     * The item the player supposedly delivered to the NPC.  Should have been one of the tasks in the quest.
     */
    protected Item gift;

    /**
     * If this causes a new relationship to form this is used to determine what the decay rate
     * will be for that relationship.
     */
    protected SocialNetworkDecayRates decayRate;

    private final int BASE_CHANGE = 15;

    private final int BONUS = 15;

    /**
     * Creates a new GiftQuest
     * 
     * @param quest The quest this reward is attached to - giver NPC is attached to this.
     * @param target The SocialNPC to whom the gift should should have been delivered
     * @param gift The Item that should have been delivered to the target
     * @param cost The difficulty of the quest
     */
    public GiftReward(Quest quest, NPC target, Item gift, SocialCapitolCost cost, SocialNetworkDecayRates rate)
    {
        super(quest, target, cost);
        this.gift = gift;
        this.decayRate = rate;
    }

    /**
     * The target received the gift, so its relationship with the giver improves and it becomes
     * indebted to the giver. If the two SocialNPCs are not friends already, a new relationship is
     * formed between them.
     * 
     * The target and giver both receive an intimacy penalty when the quest is failed. If they are
     * not friends, then no relationship is formed.
     * 
     */
    @Override
    public Item getItemReward()
    {
        SocialNetwork targetNetwork = m_target.getSocialNetwork();
        NPC questGiver = m_quest.getGranter();

        SocialNetwork questGiverNetwork = questGiver.getSocialNetwork();

        questGiver.setLastQuestResult(QuestState.COMPLETED);
        Feelings giverFeels = null;
        Feelings targetFeels = null;

        if (!(targetNetwork.hasFriend(questGiver) && questGiverNetwork.hasFriend(m_target)))
        {
        	m_quest.getGranter().newEvent(m_target, EventTypes.FRIENDSHIP_CREATED);
            //part of the friendship doesn't exist
            if (targetNetwork.hasFriend(questGiver) && !questGiverNetwork.hasFriend(m_target))
            {
                //only target is already friends with giver
                targetFeels = targetNetwork.getRelationships().get(questGiver);
                giverFeels = new Feelings(decayRate);
                questGiverNetwork.addFriend(m_target, giverFeels);
            }
            else if (!targetNetwork.hasFriend(questGiver) && questGiverNetwork.hasFriend(m_target))
            {
                //only giver is already friends with target
                giverFeels = questGiverNetwork.getRelationships().get(m_target);
                targetFeels = new Feelings(decayRate);
                targetNetwork.addFriend(questGiver, targetFeels);
            }
            else
            {
                //friendship doesn't exist currently, so create a new one
                giverFeels = new Feelings(decayRate);
                targetFeels = new Feelings(decayRate);

                questGiverNetwork.addFriend(m_target, giverFeels);
                targetNetwork.addFriend(questGiver, targetFeels);
                
            }
        }
        else if (targetNetwork.hasFriend(questGiver) && questGiverNetwork.hasFriend(m_target))
        {
            //friendship exists already
            giverFeels = questGiverNetwork.getRelationships().get(m_target);
            targetFeels = targetNetwork.getRelationships().get(questGiver);
        }

        //increase intimacy of relationship.
        giverFeels.changeIntimacy(BASE_CHANGE);
        targetFeels.changeIntimacy(BASE_CHANGE);
        //set trust to 3
        giverFeels.setTrust(3);
        targetFeels.setTrust(3);

        //bonus if gift is something that target likes.
        if (m_target.getFavoriteItems().contains(gift))
        {
            giverFeels.changeIntimacy(BONUS);
            targetFeels.changeIntimacy(BONUS);
        }

        //receiver of gift becomes indebted to the giver based on value of gift
        targetFeels.changeSocialDebt(getGiftValue());
        giverFeels.changeSocialDebt(-getGiftValue());

        return null;
    }

    /**
     * This method allows the gift Item to be changed after the GiftQuest is created.
     */
    public void setGift(Item newGift)
    {
        this.gift = newGift;
    }

    /**
     * @return the Item that this GiftQuest requires a Player to retrieve
     */
    public Item getGift()
    {
        return gift;
    }

    /**
     * The difficulty of the quest and the density of item together determine how valuable the gift
     * is. The more dense the gift, the more valuable it is.
     * 
     * @return The value of the gift in social capital
     */
    public int getGiftValue()
    {
        return (m_cost.getCost() / 2) + (gift.getWeight() / gift.getSize());
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
     * The amount of intimacy lost is decreased by the control of the SocialNPC losing it. Control
     * can reduce the intimacy penalty down to a minimum of 5.
     */
    @Override
    public void failedQuest()
    {
        SocialNetwork targetNetwork = m_target.getSocialNetwork();
        NPC questGiver = m_quest.getGranter();

        SocialNetwork questGiverNetwork = questGiver.getSocialNetwork();
        questGiver.setLastQuestResult(QuestState.FAILED);
        if (questGiverNetwork.getFriends().contains(m_target) && targetNetwork.getFriends().contains(questGiver))
        {
            Feelings giverFeels = questGiverNetwork.getRelationships().get(m_target);
            Feelings targetFeels = targetNetwork.getRelationships().get(questGiver);

            giverFeels.changeIntimacy(-(int) (((BASE_CHANGE - 5) * (1 - questGiverNetwork.getControl())) + 5));
            targetFeels.changeIntimacy(-(int) (((BASE_CHANGE - 5) * (1 - targetNetwork.getControl())) + 5));
        }

    }
}
