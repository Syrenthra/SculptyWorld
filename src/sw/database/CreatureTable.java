package sw.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import sw.item.Armor;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.lifeform.Creature;
import database.FMSObjCon;

public class CreatureTable extends SWQuery
{
    public static final String NAME = "CREATURE_TABLE";
    
    public static final String CREATURE_ID = "CREATURE_ID";
    
    public static final String CREATURE_DATA = "CREATURE_DATA";
    
    /**
     * Adds a new Item to the database.  Will not work for ItemContainers.
     * @param string
     */
    public boolean storeCreature(Creature creature)
    {       
        
        int creatureID = creature.getID();
        
        if (creatureID == -1)
        {
            creatureID = this.getNextIDNumber(NAME);
            creature.setID(creatureID);
        }
        System.out.println(""+creatureID);
        
        try
        {
            String insertion = "INSERT INTO " + NAME + "(" + CREATURE_ID + " , " + CREATURE_DATA+") VALUES( ?, ?)";
            PreparedStatement insertionStatement = m_databaseConnection.prepareStatement(insertion);
            insertionStatement.setInt(1, creatureID);
            FMSObjCon.addObjectToStatement(2, creature.getLifeformInfo(), insertionStatement);
        //System.out.println(insertion);
        
            insertionStatement.execute();
            return true;
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
            return false;
        }
        
    }

    /**
     * Loads a creature from the database.
     * @param id
     * @return
     */
    public Creature loadCreature(int id)
    {
        Creature creature = null;
        ResultSet result = null;
        try
        {
            Statement stmt = m_databaseConnection.createStatement();

            String select = "SELECT * FROM "+NAME+" WHERE "+CREATURE_ID+"="+id;

            result = stmt.executeQuery(select);
            if (result.next())
            {
                int itemID = result.getInt(CREATURE_ID);
                byte[] buf = result.getBytes(CREATURE_DATA);
                
                Hashtable<String,Object> data = (Hashtable<String,Object>)FMSObjCon.convertBytesToObject(buf);
                
                
                creature = Creature.constructCreature(data);

            }
            result.close();
            
            return creature;
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (result != null)
                try
                {
                    result.close();
                }
                catch (SQLException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            return null;
        }
    }
}
