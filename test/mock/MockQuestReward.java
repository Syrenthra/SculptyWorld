package mock;

import sw.item.Item;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.reward.QuestReward;

public class MockQuestReward implements QuestReward
{
    public Quest quest; 
    public Item reward;
    public int gold;
    public int xp;
    public boolean calledGetItemReward = false;

    
    @Override
    public Item getItemReward()
    {
        calledGetItemReward = true;
        return reward;
    }

    @Override
    public int getXPReward()
    {
        return xp;
    }

    @Override
    public int getGoldReward()
    {
        return gold;
    }

    @Override
    public boolean hasItemReward()
    {
        if (reward == null)
            return false;
        else
            return true;
    }

    @Override
    public void failedQuest()
    {
        if (quest != null)
            quest.getGranter().setLastQuestResult(QuestState.FAILED);
    }

}
