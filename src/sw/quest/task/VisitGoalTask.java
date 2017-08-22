package sw.quest.task;

import java.util.Hashtable;
import java.util.Vector;

import sw.item.Item;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.lifeform.PCEvent;
import sw.quest.Goal;
import sw.quest.Quest;
import sw.quest.QuestState;

public class VisitGoalTask implements QuestTask
{
    private Quest m_quest;
    protected TaskType m_type = TaskType.VISIT_GOAL_TASK;
    
    /**
     * Used to track which goals each player that has this quest has
     * achieved.
     * <PC ID, Goals reached>
     */
    protected Hashtable<Integer,Vector<Goal>> m_goalsReached = new Hashtable<Integer,Vector<Goal>>();
    
    /**
     * The different goals that can be done to complete this quest.  A player
     * may not have to complete all the goals to complete this quest.
     */
    private Vector<Goal> m_goals = new Vector<Goal>();
    
    public VisitGoalTask(Quest quest)
    {
        m_quest = quest;
    }
    
    /**
     * Adds a goal to the quest that helps measure how much progress the player
     * has made in completing the quest.
     * @param node
     */
    public void setGoalNode(Goal node)
    {
        m_goals.add(node);
        
    }
    
    /**
     * Returns the goal node at that index.
     * @param index
     * @return
     */
    public Goal getGoalNode(int index)
    {
        Goal goal = null;
        
        if (index < m_goals.size())
            goal = m_goals.get(0);
        return goal;
    }
    
    /**
     * Removes the goal node at that index.
     * @param index
     */
    public boolean removeGoalNode(Goal goal)
    {
        if (m_goals.contains(goal))
        {
            m_goals.remove(goal);
            return true;
        }

        return false;
        
    }
    
    /**
     * Adds a goal to the players list of completed goals for this quest.
     * @param player
     * @param goal
     */
    public void visitGoal(PC player, Goal goal)
    {
        if (m_goalsReached.containsKey(player.getID()))
        {
            Vector<Goal> goals = m_goalsReached.get(player.getID());
            if (!goals.contains(goal))
            {
                goals.addElement(goal);
            }
        }
        else
        {
            Vector<Goal> goals = new Vector<Goal>();
            goals.addElement(goal);
            m_goalsReached.put(player.getID(), goals);
        }
    }
    
    /**
     * Has this player completed the goal.
     * @param player
     * @param index
     * @return
     */
    public boolean goalVisited(PC player, Goal goal)
    {
        if (m_goalsReached.containsKey(player.getID()))
        {
            return m_goalsReached.get(player.getID()).contains(goal);
        }
        return false;
    }
    
    /**
     * 
     */
    @Override
    public void addPlayer(PC player)
    {
        Vector<Goal> goals = new Vector<Goal>();
        m_goalsReached.put(player.getID(), goals);    
    }

    @Override
    public void processPCEvent(PCEvent event)
    {
        if (event.getType() == PCEvent.MOVED)
        {
            if (event.getDestRoom() != null)
                visitGoal(event.getPC(),event.getDestRoom());
            
        }
        
    }

    /**
     * Each party member must visit the goal nodes on his or her
     * own.
     */
    @Override
    public int percentComplete(PC player)
    {
        int percentComplete = 0;
        
        Vector<Goal> goals = m_goalsReached.get(player.getID());
        if (goals != null)
        {
            if (m_goals.size() == 0)
                percentComplete = 100;
            else
                percentComplete = (goals.size()*100)/m_goals.size();
        }
        return percentComplete;
    }

    /**
     * At least one party member has to visit all the goal
     * nodes for this task to be considered complete.
     */
    @Override
    public int overallPercentComplete()
    {
        int percentComplete = 0;
        for (PC player : m_quest.getPlayers())
        {
            percentComplete = Math.max(percentComplete,percentComplete(player));
        }
        return percentComplete;
    }

    @Override
    public void removePlayer(PC player)
    {
        m_goalsReached.remove(player.getID());
        
    }

    @Override
    public TaskType getType()
    {
        return m_type;
    }

    @Override
    public void questStateUpdate(PC player)
    {
        // TODO Auto-generated method stub
        
    }

}
