package sw.database;

import sw.quest.Quest;

public class QuestTable extends SWQuery
{
   public static final String NAME = "QUEST_TABLE";
    
    public static final String QUEST_ID = "QUEST_ID";
   
    public static final String QUEST_DATA = "QUEST_DATA";
    
    public boolean storeQuest(Quest quest)
    {
		return false;
        
    }
    
    public Quest loadQuest(int id)
    {
		return null;
        
    }
}
