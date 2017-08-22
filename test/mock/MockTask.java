package mock;

import java.util.Vector;

import sw.lifeform.PC;
import sw.lifeform.PCEvent;
import sw.quest.task.QuestTask;
import sw.quest.task.TaskType;

public class MockTask implements QuestTask
{
    public Vector<PC> players = new Vector<PC>();
    public Vector<Integer> complete = new Vector<Integer>();
    int overallComplete = 0;

    @Override
    public void processPCEvent(PCEvent event)
    {

        
    }

    @Override
    public void addPlayer(PC player)
    {
        players.addElement(player);
        complete.addElement(0);
    }

    @Override
    public void removePlayer(PC player)
    {
        int index = players.indexOf(player);
        players.remove(index);
        complete.remove(index);
        
    }
    
    public void setComplete(PC player, int complete)
    {
        int index = players.indexOf(player);
        this.complete.insertElementAt(complete, index);
        this.complete.remove(index+1);
    }
    
    public void setComplete(int complete)
    {
        overallComplete = complete;
    }

    @Override
    public TaskType getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int percentComplete(PC player)
    {
        int index = players.indexOf(player);
        return complete.elementAt(index);
    }

    @Override
    public int overallPercentComplete()
    {
        return overallComplete;
    }

    @Override
    public void questStateUpdate(PC player)
    {
        // TODO Auto-generated method stub
        
    }
    
    
}

