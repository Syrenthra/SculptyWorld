package sw.quest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.lifeform.Party;

import mock.MockItem;
import mock.MockQuestReward;
import mock.MockTask;

import org.junit.Before;
import org.junit.Test;

import sw.quest.Quest;
import sw.quest.task.KillCreatureTask;


public class TestQuest
{
    Quest quest;
    NPC granter;
    @Before
    public void before()
    {
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
    }
    
    @Test
    public void testGetters()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        assertEquals("Quest",quest.getName());
        assertEquals("Description",quest.getDescription());
        assertEquals(0,quest.getNumPlayers());
        assertEquals(0,quest.getRewards().size());
        assertEquals(granter,quest.getGranter());
        assertEquals(null,quest.getCurrentState(dude));
        assertEquals(QuestState.INACTIVE,quest.getCurrentState());
    }

    
    @Test
    public void testAssignAndRemovePlayers()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        PC dude2 = new PC(2,"Dude","Desc",50);
        // We would like to use the addPlayer method directly,
        // but we make the remove method dependent on if quest
        // is native or inherited.
        dude.assignNativeQuest(quest);  // Will be native.
        dude2.updateQuests(quest);  // Will be inherited.
        assertEquals(2,quest.getNumPlayers());
        assertTrue(quest.hasPlayer(dude));
        
        assertEquals(QuestState.IN_PROGRESS,quest.getCurrentState(dude));
        assertEquals(QuestState.IN_PROGRESS,quest.getCurrentState(dude2));
        assertEquals(QuestState.IN_PROGRESS,quest.getCurrentState());
        
        // Not properly testing remove when player has quest as inherited quest.
        quest.removePlayer(dude2);
        assertEquals(1,quest.getNumPlayers());
        assertFalse(quest.hasPlayer(dude2));
        assertTrue(quest.hasPlayer(dude));
        assertEquals(null,quest.getCurrentState(dude2));
        assertEquals(QuestState.IN_PROGRESS,quest.getCurrentState());
        
        dude2.updateQuests(quest);  // Will be inherited.
        assertEquals(2,quest.getNumPlayers());
        // When the last player with the quest as native is removed
        // then all players should be removed from the quest.
        quest.removePlayer(dude);
        assertEquals(0,quest.getNumPlayers());
        assertFalse(quest.hasPlayer(dude2));
        assertFalse(quest.hasPlayer(dude));
        assertEquals(null,quest.getCurrentState(dude));
        assertEquals(QuestState.INACTIVE,quest.getCurrentState());
        
        // Can't add player if quest is COMPLETED or FAILED
        quest.m_questState = QuestState.COMPLETED;
        assertFalse(quest.addPlayer(dude));
        assertEquals(0,quest.getNumPlayers());
        
        quest.m_questState = QuestState.FAILED;
        assertFalse(quest.addPlayer(dude));
        assertEquals(0,quest.getNumPlayers());
    }
    
    @Test
    public void testCanChangeTheQuestStateOfPlayers()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        quest.addPlayer(dude);
        
        quest.setCurrentState(dude,QuestState.FAILED);
        
        assertEquals(QuestState.FAILED,quest.getCurrentState(dude));
    }
    
    @Test
    public void testAddReward()
    {
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
        MockQuestReward reward = new MockQuestReward();
        quest.addReward(reward);
        assertEquals(reward,quest.getRewards().elementAt(0));
    }
    
    @Test
    public void testCanAddAndRemoveQuestTasks()
    {
        MockTask task = new MockTask();
        quest.addTask(task);
        assertEquals(task,quest.m_tasks.elementAt(0));
        assertTrue(quest.containsTask(task.getType()));
        quest.removeTask(task);
        assertEquals(0,quest.m_tasks.size());
        assertFalse(quest.containsTask(task.getType()));
    }
    
    @Test
    public void testAssignAndRemovePlayersWithTask()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        MockTask task = new MockTask();
        quest.addTask(task);
        quest.addPlayer(dude);
        assertEquals(dude,task.players.elementAt(0));
        
        quest.removePlayer(dude);
        assertEquals(0,task.players.size());
    }
    
    @Test
    public void testGetCompletePercentWithZeroTasks()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        quest.addPlayer(dude);
        assertEquals(100,quest.getPercentComplete(dude));
        assertEquals(100,quest.getOverallCompletionPercent());
    }
    
    @Test
    public void testGetCompletePercentWithOneTask()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        MockTask task = new MockTask();
        quest.addTask(task);
        quest.addPlayer(dude);
        task.setComplete(dude,100);
        task.setComplete(100);
        assertEquals(100,quest.getPercentComplete(dude));
        assertEquals(100,quest.getOverallCompletionPercent());
        task.setComplete(dude,75);
        task.setComplete(75);
        assertEquals(75,quest.getPercentComplete(dude));
        assertEquals(75,quest.getOverallCompletionPercent());
    }
    
    @Test
    public void testGetCompletePercentWithManyTasksAndPCs()
    {
        // One person many tasks
        PC dude1 = new PC(1,"Dude1","Desc",50);
        MockTask task1 = new MockTask();
        MockTask task2 = new MockTask();
        quest.addTask(task1);
        quest.addTask(task2);
        dude1.assignNativeQuest(quest);
        task1.setComplete(dude1,100);
        task1.setComplete(100);
        task2.setComplete(dude1,50);
        task2.setComplete(50);
        assertEquals(75,quest.getPercentComplete(dude1));
        assertEquals(75,quest.getOverallCompletionPercent());
        
        // Two people many tasks
        PC dude2 = new PC(2,"Dude2","Desc",50);
        dude2.assignNativeQuest(quest);
        task1.setComplete(dude2, 75);
        task2.setComplete(dude2, 25);
        task1.setComplete(100);
        task2.setComplete(75);
        assertEquals(75,quest.getPercentComplete(dude1));
        assertEquals(50,quest.getPercentComplete(dude2));
        assertEquals(87,quest.getOverallCompletionPercent()); // Should always round down.
        
    }
    
    @Test
    public void testGetRewardFromQuestWithSingleReward()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
        MockQuestReward reward = new MockQuestReward();
        MockItem obj2 = new MockItem();
        reward.reward = obj2;
        quest.addReward(reward);
        dude.assignNativeQuest(quest);
        Item item = quest.getItemReward();
        assertEquals(obj2,item);
    }
    
    @Test
    public void testQuestInInactiveStateForPlayerDoesNotForwardUpdatesToTasks()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        quest.addTask(task);
        // This quest is still failing...don't know why.
        PC player1 = new PC(1,"Dude1","Desc",50);
        player1.assignNativeQuest(quest);
        quest.setCurrentState(player1, QuestState.INACTIVE);
        player1.killed(creature.clone());
        assertEquals(0,task.getCreaturesKilled(player1));
    }
    
    @Test
    public void testGetRewardFromQuestWithNullReward()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
        MockQuestReward reward = new MockQuestReward();
        reward.reward = null;
        quest.addReward(reward);
        dude.assignNativeQuest(quest);
        Item item = quest.getItemReward();
        assertEquals(null,item);
    }
    
    @Test
    public void testPlayersInSamePartyShareQuestResults()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        quest.addTask(task);
        // This quest is still failing...don't know why.
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        player1.assignNativeQuest(quest);
        
        player1.getParty().mergeParties(player2.getParty()); // Quest should be inherited for Player 2
        
        assertEquals(quest,player2.getInheritedQuest(0));
        
        player1.killed(creature.clone());
        player2.killed(creature.clone());
        assertEquals(2,task.getCreaturesKilled(player1));
        assertEquals(2,task.getCreaturesKilled(player2)); 
    }
    
    @Test
    public void testQuestUpdatesProperlyWithTwoPlayersInDifferentParties()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
        KillCreatureTask task = new KillCreatureTask(quest,creature,5);
        quest.addTask(task);
        
        PC player1 = new PC(2,"Dude1","Desc",50);
        player1.assignNativeQuest(quest);
        PC player2 = new PC(3,"Dude2","Desc",50);
        player2.assignNativeQuest(quest);
        player1.killed(creature.clone());
        player2.killed(creature.clone());
        assertEquals(1,task.getCreaturesKilled(player1));
        assertEquals(1,task.getCreaturesKilled(player2));
    }
    
    @Test
    public void testGetRewardFromQuestWithMulipleRewardsWithSomeNull()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        granter = new NPC(99,"Test","Desc",10,10,10,5);
        quest = new Quest("Quest","Description",granter);
        MockQuestReward reward1 = new MockQuestReward();
        MockItem obj2 = new MockItem();
        reward1.reward = obj2;
        MockQuestReward reward2 = new MockQuestReward();
        MockItem obj3 = new MockItem();
        reward2.reward = obj3;
        MockQuestReward reward3 = new MockQuestReward();
        reward3.reward = null;
        quest.addReward(reward1);
        quest.addReward(reward2);
        quest.addReward(reward3);
        dude.assignNativeQuest(quest);
        ItemContainer item = (ItemContainer)quest.getItemReward();
        assertEquals(2,item.getItems().size());
        assertEquals(obj2,item.getItems().elementAt(0));
        assertEquals(obj3,item.getItems().elementAt(1));
    }
    
    /**
     * Test that turning in a quest with all tasks done gets the reward.
     * 
     */
    @Test
    public void testSinglePlayerTurnInQuestWithAllTasksDone()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        quest = new Quest("Quest","Description",giver);
        MockQuestReward reward = new MockQuestReward();
        MockItem obj2 = new MockItem();
        reward.reward = obj2;
        reward.gold = 100;
        reward.xp = 1000;
        quest.addReward(reward);
        
        MockTask task1 = new MockTask();
        MockTask task2 = new MockTask();
        quest.addTask(task1);
        quest.addTask(task2);
        
        giver.addQuest(quest);
        
        dude.assignNativeQuest(quest);
        
        task1.setComplete(dude, 100);
        task1.setComplete(100);
        task2.setComplete(dude, 100);
        task2.setComplete(100);
        
        // All done so turning in the quest.
        assertTrue(quest.turnInQuest(dude));
        

        
        // Since only one player, that player should get all the reward.
        assertEquals(obj2,dude.getContentsInHand(HandLocation.RIGHT));
        assertEquals(100,dude.getGold());
        assertEquals(1000,dude.getXP());
        assertEquals(QuestState.COMPLETED,quest.getCurrentState());
        assertEquals(null,dude.getNativeQuest(0));
        assertEquals(0,quest.getNumPlayers());
        assertEquals(0,giver.getAvailableQuests().size());
    }
    
    /**
     * Test that turning in a quest with all tasks done gets the reward.
     * 
     */
    @Test
    public void testCallsGetItemRewardEvenIfNoItem()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        quest = new Quest("Quest","Description",giver);
        MockQuestReward reward = new MockQuestReward();
        MockItem obj2 = new MockItem();
        reward.reward = null;
        reward.gold = 100;
        reward.xp = 1000;
        quest.addReward(reward);
        
        MockTask task1 = new MockTask();
        MockTask task2 = new MockTask();
        quest.addTask(task1);
        quest.addTask(task2);
        
        giver.addQuest(quest);
        
        dude.assignNativeQuest(quest);
        
        task1.setComplete(dude, 100);
        task1.setComplete(100);
        task2.setComplete(dude, 100);
        task2.setComplete(100);
        
        // All done so turning in the quest.
        assertTrue(quest.turnInQuest(dude));
        

        
        // Since only one player, that player should get all the reward.
        assertTrue(reward.calledGetItemReward);
        assertEquals(100,dude.getGold());
        assertEquals(1000,dude.getXP());
        assertEquals(QuestState.COMPLETED,quest.getCurrentState());
        assertEquals(null,dude.getNativeQuest(0));
        assertEquals(0,quest.getNumPlayers());
        assertEquals(0,giver.getAvailableQuests().size());
    }
    
    @Test
    public void testPartyCantTurnInPartiallyCompletedQuestOrIfHandsAreFull()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty1 = player1.getParty();
        Party myParty2 = player2.getParty();
        myParty1.mergeParties(myParty2);
        
        NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        quest = new Quest("Quest","Description",giver);
        MockQuestReward reward = new MockQuestReward();
        MockItem obj2 = new MockItem();
        reward.reward = obj2;
        reward.gold = 100;
        reward.xp = 1000;
        quest.addReward(reward);
        
        MockTask task1 = new MockTask();
        quest.addTask(task1);
        
        player1.assignNativeQuest(quest);
        
        task1.setComplete(99);
        task1.setComplete(player1, 99);
        task1.setComplete(player2, 99);
        
        // This should not work.
        assertFalse(quest.turnInQuest(player1));
        
        assertEquals(QuestState.IN_PROGRESS,quest.getCurrentState());
        
        player2.holdInHand(obj2.clone(), HandLocation.RIGHT);
        player2.holdInHand(obj2.clone(), HandLocation.LEFT);
        task1.setComplete(100);
        
     // This should not work.
        quest.turnInQuest(player2);
        
        assertEquals(QuestState.IN_PROGRESS,quest.getCurrentState());
    }
    
    @Test
    public void testPartyTurnsInFullyCompletedQuestByBothMembers()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty1 = player1.getParty();
        Party myParty2 = player2.getParty();
        myParty1.mergeParties(myParty2);
        
        NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        quest = new Quest("Quest","Description",giver);
        MockQuestReward reward = new MockQuestReward();
        MockItem obj2 = new MockItem();
        reward.reward = obj2;
        reward.gold = 100;
        reward.xp = 1000;
        quest.addReward(reward);
        
        MockTask task1 = new MockTask();
        quest.addTask(task1);
        
        player1.assignNativeQuest(quest);
        
        task1.setComplete(100);
        task1.setComplete(player1, 100);
        task1.setComplete(player2, 100);
        
        assertTrue(quest.turnInQuest(player1));
        assertEquals(50,player1.getGold());
        assertEquals(50,player2.getGold());
        assertEquals(1000,player1.getXP());
        assertEquals(1000,player2.getXP());
        assertEquals(obj2,player1.getContentsInHand(HandLocation.RIGHT));
        
        assertEquals(QuestState.COMPLETED,quest.getCurrentState());
        
        assertFalse(quest.hasPlayer(player1));
        assertNull(player1.getNativeQuest(0));
        
        assertFalse(quest.hasPlayer(player2));
        assertNull(player2.getNativeQuest(0));
    }
    
    @Test
    public void testPartyTurnsFullyCompletedQuestThatEachOnlyPartiallyCompleted()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty1 = player1.getParty();
        Party myParty2 = player2.getParty();
        myParty1.mergeParties(myParty2);
       
        
        NPC giver = new NPC(0, "Mocky", "The MockSocialNPC", 100, 1, 50, 2);
        quest = new Quest("Quest","Description",giver);
        MockQuestReward reward = new MockQuestReward();
        MockItem obj2 = new MockItem();
        reward.reward = obj2;
        reward.gold = 100;
        reward.xp = 1000;
        quest.addReward(reward);
         
        MockTask task1 = new MockTask();
        quest.addTask(task1);
        
        player1.assignNativeQuest(quest);
        
        task1.setComplete(100);
        task1.setComplete(player1, 45);
        task1.setComplete(player2, 90);
        
        // Want to test that will put reward in left hand if right is full.
        player2.holdInHand(obj2.clone(), HandLocation.RIGHT);
        
        assertTrue(quest.turnInQuest(player2));
        assertEquals(33,player1.getGold());
        assertEquals(66,player2.getGold());
        assertEquals(450,player1.getXP());
        assertEquals(900,player2.getXP());
        assertEquals(obj2,player2.getContentsInHand(HandLocation.LEFT));
        
        assertEquals(QuestState.COMPLETED,quest.getCurrentState());
        
        assertFalse(quest.hasPlayer(player1));
        assertNull(player1.getNativeQuest(0));
        
        assertFalse(quest.hasPlayer(player2));
        assertNull(player2.getNativeQuest(0));
        
    }
 
}