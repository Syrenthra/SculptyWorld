package sw.quest.reward;

import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.SocialCapitolCost;

public abstract class SocialReward implements QuestReward
{
    protected Quest m_quest;

    protected NPC m_target;
    
    protected SocialCapitolCost m_cost;
    
    public SocialReward(Quest quest, NPC questTarget, SocialCapitolCost cost)
    {
        m_quest = quest;
        m_target = questTarget;
        m_cost = cost;
    }

    /**
     * Returns how much social capitol this reward costs to create.
     * @return
     */
    public SocialCapitolCost getCost()
    {
        return m_cost;
    }
    
    /**
     * Changes the social capitol cost for the granter.
     * @param cost
     */
    public void setCost(SocialCapitolCost cost)
    {
        m_cost = cost;
    }
    
    /**
     * Returns the NPC target for this reward (more of a punishment).
     * @return
     */
    public NPC getTarget()
    {
        return m_target;
    }
}
