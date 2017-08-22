package sw.quest.task;

public enum TaskType
{
    INVALID ("INVALID"), 
    CREATURE_TASK ("CREATURE_TASK"),
    ITEM_TASK ("ITEM_TASK"), 
    VISIT_GOAL_TASK ("VISIT_GOAL_TASK");
    
    public final String type;
    
    TaskType(String t)
    {
        type = t;
    }

}
