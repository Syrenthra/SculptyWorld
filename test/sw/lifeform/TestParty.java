package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import mock.MockItem;
import mock.MockTask;

import org.junit.Test;

import sw.item.HandLocation;
import sw.item.Item;
import sw.lifeform.Party;
import sw.lifeform.PC;
import sw.quest.Quest;
import sw.quest.QuestState;
import sw.quest.task.DeliverItemTask;



public class TestParty
{
    @Test
    public void testInitialize()
    {
        Party myParty = new Party(null);
        assertEquals(0,myParty.getPlayers().size());
        assertNull(myParty.getPartyLeader());
    }
    
    @Test
    public void testAddPlayersToTheParty()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        
        Party myParty = player1.getParty();  
        assertEquals(player1,myParty.getPlayer(0));
        assertTrue(player1.containsObserver(myParty));
        assertEquals(player1,myParty.getPartyLeader());
        assertEquals(player2,player2.getParty().getPartyLeader());
        
        myParty.mergeParties(player2.getParty());
        assertEquals(player2,myParty.getPlayer(1));
        assertEquals(2,myParty.getPlayers().size());
        assertTrue(player2.containsObserver(myParty)); 
        assertEquals(player1,myParty.getPartyLeader());
    }
    
    @Test
    public void testPlayerCanLeaveParty()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty = player1.getParty();
        myParty.mergeParties(player2.getParty());
        myParty.removePlayer(player1);
      
        assertEquals(player2,myParty.getPlayer(0));
        assertFalse(player1.containsObserver(myParty));
        assertTrue(player2.containsObserver(myParty));
        // Player 1 should now be in a party by him/herself.
        assertNotNull(player1.getParty());
        assertEquals(player1,player1.getParty().getPlayer(0));
        assertTrue(player1.containsObserver(player1.getParty()));
    }
    
    @Test
    public void testPlayersInSamePartyAllAssignedQuest()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty1 = player1.getParty();
        myParty1.mergeParties(player2.getParty());
       
        NPC granter = new NPC(99,"Test","Desc",10,10,10,5);
        Quest quest = new Quest("Quest","Description",granter);
        player1.assignNativeQuest(quest);
        assertEquals(null,player1.getNativeQuest(1));
        assertEquals(null,player2.getNativeQuest(1));
        assertEquals(quest,player1.getNativeQuest(0));
        assertEquals(quest,player2.getNativeQuest(0));
    }
    
    @Test
    public void testPlayerInSoloPartyWithQuestJoinsAnotherSoloPartyWithQuestBothHaveBothOneNativeAndOneInheritedQuest()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        Party myParty1 = player1.getParty();

        NPC granter1 = new NPC(99,"Test","Desc",10,10,10,5);
        Quest quest1 = new Quest("Quest","Description",granter1);
        player1.assignNativeQuest(quest1);  
        
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty2 = player2.getParty();
        
        NPC granter2 = new NPC(99,"Test","Desc",10,10,10,5);
        Quest quest2 = new Quest("Quest","Description",granter2);
        player2.assignNativeQuest(quest2);
        
        myParty1.mergeParties(myParty2);
        assertEquals(1,player1.getNativeQuests().size());
        assertEquals(1,player1.getInheritedQuests().size());
        assertEquals(1,player2.getNativeQuests().size());
        assertEquals(1,player2.getInheritedQuests().size());
        assertEquals(quest2,player1.getInheritedQuest(0));
        assertEquals(quest1,player2.getInheritedQuest(0));
         
    }
    
    @Test
    public void testThreePlayersWithQuestsJoiningAndLeavingWithQuests()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        Party myParty1 = player1.getParty();

        NPC granter1 = new NPC(99,"Test1","Desc",10,10,10,5);
        Quest quest1 = new Quest("Quest1","Description",granter1);
        player1.assignNativeQuest(quest1);  
        
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty2 = player2.getParty();
        
        NPC granter2 = new NPC(99,"Test2","Desc",10,10,10,5);
        Quest quest2 = new Quest("Quest2","Description",granter2);
        player2.assignNativeQuest(quest2);
        
        PC player3 = new PC(3,"Dude3","Desc",50);
        Party myParty3 = player3.getParty();
        
        NPC granter3 = new NPC(99,"Test3","Desc",10,10,10,5);
        Quest quest3 = new Quest("Quest3","Description",granter3);
        
        
        myParty1.mergeParties(myParty2);
        player2.assignNativeQuest(quest3);
        
        myParty1.mergeParties(myParty3);
        player1.leaveParty();
        
        assertEquals(2,player1.getNativeQuests().size());
        assertEquals(1,player1.getInheritedQuests().size());
        assertEquals(QuestState.INACTIVE,player1.getInheritedQuest(0).getCurrentState(player1));
        assertEquals(QuestState.INACTIVE,player2.getInheritedQuest(0).getCurrentState(player2));
        assertEquals(3,player3.getInheritedQuests().size());
        assertEquals(QuestState.INACTIVE,player3.getInheritedQuest(0).getCurrentState(player3));
        
        player2.leaveParty();
        
        assertEquals(QuestState.INACTIVE,player3.getInheritedQuest(0).getCurrentState(player3));
        assertEquals(QuestState.INACTIVE,player3.getInheritedQuest(1).getCurrentState(player3));
        assertEquals(QuestState.INACTIVE,player3.getInheritedQuest(2).getCurrentState(player3));
    }
    
    @Test
    public void testPlayerInSoloPartyWithQuestJoinsAnotherSoloPartyWithSameQuestYouDontLooseQuestProgress()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        Party myParty1 = player1.getParty();
        
        NPC granter1 = new NPC(99,"Test","Desc",10,10,10,5);
        Quest quest1 = new Quest("Quest","Description",granter1);
        MockTask task = new MockTask();
        quest1.addTask(task);
        player1.assignNativeQuest(quest1);
        task.setComplete(player1,75);
        
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty2 = player2.getParty();
       
        player2.assignNativeQuest(quest1);
        task.setComplete(player2,50);
        
        myParty1.mergeParties(myParty2);
        assertEquals(75,task.percentComplete(player1));
        assertEquals(50,task.percentComplete(player2));
    }
    
    @Test
    public void testPlayerJoinsPartyWithDeliverItemTaskCanAlsoDeliverItems()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        Party myParty1 = player1.getParty();
        
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty2 = player2.getParty();
        
        NPC granter = new NPC(1,"Test","Desc",10,5,10,4);
        Quest quest = new Quest("Quest","Description",granter);
        MockItem item = new MockItem("Test","Test Item",5,10);
        DeliverItemTask task = new DeliverItemTask(quest,item,5);
        quest.addTask(task);
        
        granter.addQuest(quest);
        
        granter.assignQuest(player1, quest);
        
        assertTrue(granter.m_acceptablePersonalItems.containsKey(player1.getID()));
        
        myParty1.mergeParties(myParty2);
        
        player1.holdInHand(item.clone(), HandLocation.RIGHT);
        player1.giveItemInHand(granter, HandLocation.RIGHT);
        
        assertTrue(granter.m_acceptablePersonalItems.containsKey(player2.getID()));
        assertEquals(20,task.percentComplete(player1));
        assertEquals(20,task.percentComplete(player2));
        assertEquals(20,task.overallPercentComplete());
        
        player2.leaveParty();
        
        assertTrue(granter.m_acceptablePersonalItems.containsKey(player2.getID()));
        assertEquals(0,granter.m_acceptablePersonalItems.get(player2.getID()).size());
        
        player1.holdInHand(item.clone(), HandLocation.RIGHT);
        player1.giveItemInHand(granter, HandLocation.RIGHT);
        
        assertEquals(40,task.percentComplete(player1));
        assertEquals(20,task.percentComplete(player2));
        assertEquals(40,task.overallPercentComplete());
    }

    @Test
    public void testJoinPartyRequest()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        Party myParty1 = player1.getParty();
        
        PC player2 = new PC(2,"Dude2","Desc",50);
        
        myParty1.addJoinRequest(player2);
        assertTrue(myParty1.hasJoinRequest(player2.getName()));
        myParty1.removeJoinRequest(player2);
        assertFalse(myParty1.hasJoinRequest(player2.getName()));
        
        //Can't add join request if already in the party
        myParty1.addJoinRequest(player1);
        assertFalse(myParty1.hasJoinRequest(player1.getName()));
    }

}
