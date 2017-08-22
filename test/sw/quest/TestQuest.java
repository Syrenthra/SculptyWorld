package sw.quest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import sw.lifeform.Player;


import org.junit.Test;

import sw.quest.Quest;


public class TestQuest
{
    
    @Test
    public void testGetters()
    {
        Quest quest = new MockQuest("Quest","Description");
        assertEquals("Quest",quest.getName());
        assertEquals("Description",quest.getDescription());
        assertEquals(0,quest.getNumPlayers());
        assertEquals(0,quest.getMaxReward());
    }

    @Test
    public void testAssignAndRemovePlayers()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Player dude2 = new Player(2,"Dude","Desc",50);
        Quest quest = new MockQuest("Quest","Description");
        quest.addPlayer(dude);
        quest.addPlayer(dude2);
        assertEquals(2,quest.getNumPlayers());
        assertTrue(quest.hasPlayer(dude));
        
        quest.removePlayer(dude);
        assertEquals(1,quest.getNumPlayers());
        assertTrue(quest.hasPlayer(dude2));
        assertFalse(quest.hasPlayer(dude));
    }
    
    @Test
    public void testSetMaxReward()
    {
        Quest quest = new MockQuest("Quest","Description");
        quest.setMaxReward(15);
        assertEquals(15,quest.getMaxReward());
    }
    
    @Test
    public void testCanSetQuestAsCompleted()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        Quest quest = new MockQuest("Name","Desc");
        quest.addPlayer(dude);
        quest.setCompleted(dude,true);
        assertEquals(true,quest.getCompleted(dude));
    }
    

    
}

class MockQuest extends Quest
{

    public MockQuest(String name, String desc)
    {
        super(name,desc);
    }

    @Override
    public void turnInQuest(Player player)
    {

    }

	@Override
	public int calculateReward(Player player)
	{
		// TODO Auto-generated method stub
		return 0;
	}
    
}
