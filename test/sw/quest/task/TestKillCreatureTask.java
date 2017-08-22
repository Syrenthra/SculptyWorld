package sw.quest.task;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import sw.lifeform.Creature;
import sw.lifeform.PC;
import sw.lifeform.NPC;
import sw.quest.Quest;
import sw.quest.task.KillCreatureTask;
import sw.quest.task.TaskType;


public class TestKillCreatureTask
{
    Quest quest;
    NPC granter;
    
    @Before
    public void before()
    {
        granter = new NPC(1,"Test","Desc",10,5,10,4);
        quest = new Quest("Quest","Description",granter);
    }
    
    @Test
    public void testInitialize()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        assertEquals(creature,task.getCreature());
        assertEquals(5,task.getAmount());
        assertEquals(TaskType.CREATURE_TASK,task.getType());
    }
    
    @Test
    public void testCanAssignPlayerToTheTask()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        task.addPlayer(dude);
        assertEquals(0,task.getCreaturesKilled(dude));
    }
    
    @Test
    public void testCanRemoveAPlayerFromTask()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        task.addPlayer(dude);
        task.removePlayer(dude);
        assertEquals(-1,task.getCreaturesKilled(dude));
    }
    
    @Test
    public void testCanCompleteTheTask()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        Creature creatureKilled = new Creature(2,"Dude", "Desc",50,10,5,15);
        Creature creatureNotToKill = new Creature(3,"Dude2", "Desc2",50,10,5,15);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        quest.addTask(task);
        
        dude.assignNativeQuest(quest);
        dude.killed(creatureKilled);
        dude.killed(creatureKilled);
        dude.killed(creatureKilled);
        dude.killed(creatureNotToKill);
        assertEquals(3,task.getCreaturesKilled(dude));
        assertEquals(60,task.percentComplete(dude));
        dude.killed(creatureKilled);
        dude.killed(creatureKilled);
        assertEquals(5,task.getCreaturesKilled(dude));
        assertEquals(100,task.percentComplete(dude));
    }
    
    @Test
    public void testTwoPlayersInSamePartyWithQuestCreditedWithKill()
    {
        PC dude1 = new PC(1,"Dude1","Desc",50);
        PC dude2 = new PC(2,"Dude2","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        Creature creatureKilled = new Creature(2,"Dude", "Desc",50,10,5,15);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        quest.addTask(task);
        
        dude1.getParty().mergeParties(dude2.getParty());
        dude1.assignNativeQuest(quest);
        
        dude1.killed(creatureKilled);
        assertEquals(1,task.getCreaturesKilled(dude1));
        assertEquals(1,task.getCreaturesKilled(dude2));
        
        dude2.leaveParty();
        // Both should still have the quest as active, but don't get credited for each
        // other's kills.
        dude1.killed(creatureKilled);
        assertEquals(2,task.getCreaturesKilled(dude1));
        assertEquals(1,task.getCreaturesKilled(dude2));
        
        dude2.killed(creatureKilled);
        assertEquals(2,task.getCreaturesKilled(dude1));
        assertEquals(2,task.getCreaturesKilled(dude2));
    }
    

}
