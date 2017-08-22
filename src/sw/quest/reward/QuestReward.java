package sw.quest.reward;

import sw.item.Item;

public interface QuestReward
{
    
    /**
     * Checks to see whether the reward will provide an item or not without
     * triggering any of the reward.  This is key as if a player has his or
     * her hands full the quest can't be turned in and we don't want to have
     * to undo a reward.
     * 
     * @param percentComplete
     * @return
     */
    public boolean hasItemReward();
    
    /**
     * Returns an item reward for the quest based on the percent complete the quest is.
     * @param percentComplete
     * @return
     */
    public Item getItemReward();
    
    /**
     * Returns the XP reward for the quest based on the percent complete the quest is.
     * @param percentComplete
     * @return
     */
    public int getXPReward();
    
    /**
     * Returns the gold reward for the quest based on the percent complete the quest is.
     * @param percentComplete
     * @return
     */
    public int getGoldReward();
    
    /**
     * What should the reward do if the quest was failed.
     */
    public void failedQuest();

}
