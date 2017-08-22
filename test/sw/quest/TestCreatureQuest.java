package sw.quest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.lifeform.Creature;
import sw.lifeform.Party;
import sw.lifeform.Player;
import sw.quest.CreatureQuest;



public class TestCreatureQuest
{
    
    @Test
    public void testInitialize()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        assertEquals("Name",quest.getName());
        assertEquals("Desc",quest.getDescription());
        assertEquals(creature,quest.getCreature());
        assertEquals(5,quest.getAmount());
    }
    
    @Test
    public void testCanAssignPlayerToTheQuest()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        quest.addPlayer(dude);
        assertTrue(quest.hasPlayer(dude));
    }
    
    @Test
    public void testCanRemoveAPlayerFromQuest()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        quest.addPlayer(dude);
        quest.removePlayer(dude);
        assertFalse(quest.hasPlayer(dude));
    }
    
    @Test
    public void testCanCompleteTheQuest()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        quest.addPlayer(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        assertEquals(3,quest.getCreaturesKilled(dude));
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        assertEquals(5,quest.getCreaturesKilled(dude));
        assertTrue(quest.getCompleted(dude));
    }
    
    @Test
    public void testCanTurnInForRewardWhenCompleted()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        quest.setMaxReward(10);
        quest.addPlayer(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        int reward = quest.calculateReward(dude);
        assertEquals(quest.getMaxReward(),reward);
        assertFalse(quest.hasPlayer(dude));
        assertNull(dude.getQuest(0));
    }
    
    @Test
    public void testRewardAmountBasedOnPartialCompletion()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        quest.addPlayer(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        quest.updateGoal(dude);
        int reward = quest.calculateReward(dude);
        assertEquals(quest.getMaxReward()*0.8,reward,0.001);
        assertFalse(quest.hasPlayer(dude));
        assertNull(dude.getQuest(0));
    }
    
    @Test
    public void testCannotTurnInQuestDontHave()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        int reward = quest.calculateReward(dude);
        assertEquals(-1,reward);
    }
    
    @Test
    public void testPlayersInSamePartyShareQuestResults()
    {
        Player player1 = new Player(1,"Dude1","Desc",50);
        Player player2 = new Player(2,"Dude2","Desc",50);
        Party myParty1 = new Party(0,player1);
        Party myParty2 = new Party(1,player2);
        myParty1.mergeParties(myParty2);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        quest.addPlayer(player1);
        quest.addPlayer(player2);
        player1.addQuest(quest);
        player2.addQuest(quest);
        player1.killed(creature.clone());
        player2.killed(creature.clone());
        assertEquals(2,quest.getCreaturesKilled(player1));
        assertEquals(2,quest.getCreaturesKilled(player2)); 
    }

}
