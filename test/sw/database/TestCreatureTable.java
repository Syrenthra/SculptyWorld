package sw.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.environment.Zone;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.Item;
import sw.lifeform.Creature;

import database.DatabaseInfo;
import database.DatabaseManager;


public class TestCreatureTable
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
        assertEquals("CREATURE_TABLE",CreatureTable.NAME);
        assertEquals("CREATURE_ID",CreatureTable.CREATURE_ID);
        assertEquals("CREATURE_DATA",CreatureTable.CREATURE_DATA);
    }
    
    @Test
    public void testCreatureableStoresACreature()
    {
        CreatureTable it = new CreatureTable();
        Creature dude = new Creature(1, "Dude", "Desc", 50, 10, 5, 15);
        dude.addZone(Zone.BEACH);
        dude.addZone(Zone.DESERT);
        boolean result = it.storeCreature(dude);
        assertTrue(result);
        Creature creature = it.loadCreature(1);
        assertNotNull(creature);
        assertEquals("Dude",creature.getName());
        assertEquals(1,creature.getID());
        assertEquals(50,creature.getCurrentLifePoints());
        assertEquals(2,creature.getNumValidZones());
        assertTrue(creature.canTravel(Zone.DESERT));
        assertTrue(creature.canTravel(Zone.BEACH));
    }

}
