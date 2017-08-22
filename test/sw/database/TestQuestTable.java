package sw.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mock.MockItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.environment.Room;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.Item;
import sw.quest.Goal;
import sw.quest.task.DeliverItemTask;

import database.DatabaseInfo;
import database.DatabaseManager;


public class TestQuestTable
{
   DatabaseManager dm;
    
    @Before
    public void before() throws Exception
    {
        ConstructTestDatabase.constructTestDatabase();
        
        dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);
        dm.addDB(SWQuery.DATABASE, di);
    }
    
    @After
    public void after() throws Exception
    {
        ConstructTestDatabase.destroyTestDatabase();
        
        dm.shutdown();
    }
    
    @Test
    public void testGlobals()
    {
        assertEquals("QUEST_TABLE",QuestTable.NAME);
        assertEquals("QUEST_ID",QuestTable.QUEST_ID);
        assertEquals("QUEST_DATA",QuestTable.QUEST_DATA);
    }
    
    @Test
    public void testItemTableStoresItemQuest()
    {
        QuestTable qt = new QuestTable();
        Goal node = new Room(1, "Tree","Forest");
        Item item = new Armor("Helmet","Pretty",5,3,ArmorLocation.HEAD,10);
        DeliverItemTask quest = new DeliverItemTask("Name","Desc",item,5);
        quest.setGoalNode(node);
        boolean result = qt.storeQuest(quest);
        assertTrue(result);
        DeliverItemTask retQuest = qt.loadQuest(1);
        assertNotNull(retQuest);
        assertEquals(1,retQuest.getQuestID());
        assertTrue(retQuest.getGoalNode(0) instanceof Room);
        assertTrue(retQuest.getItem() instanceof Armor);
    }
}
