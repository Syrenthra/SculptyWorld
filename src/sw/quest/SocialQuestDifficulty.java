package sw.quest;

public enum SocialQuestDifficulty
{
	/**
	 * The difficulty rating is how much social capital it costs a SocialNPC to create a SocialQuest
	 * at that difficulty level. The difficulty is also used by each type of SocialQuest to calculate
	 * rewards.
	 * 
	 * Note: be careful with adding or taking values away from this list. SocialNPC and QuestGenerator
	 * base decisions on the values here. Changing a difficulty value to something different shouldn't
	 * cause any problems, but adding or removing values will require modifying QuestGenerator a little
	 * and SocialNPC quite a bit.
	 */
	EASY (500),
	MEDIUM (1000),
	HARD (1500),
	YOUMUSTBEPRO (2500);
	
	private int difficulty;
	
	private SocialQuestDifficulty(int diff)
	{
		difficulty = diff;
	}
	
	public int getDifficulty()
	{
		return difficulty;
	}
	
}
