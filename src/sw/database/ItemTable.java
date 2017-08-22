package sw.database;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import database.DatabaseInfo;
import database.DatabaseManager;
import database.FMSObjCon;

import sw.item.Armor;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;

/**
 * The information for the database table that stores the items in the Sculpty World.
 * @author cdgira
 *
 */
public class ItemTable extends SWQuery
{
    public static final String NAME = "ITEM_TABLE";
    
    public static final String ITEM_ID = "ITEM_ID";
    
    public static final String ITEM_TYPE = "ITEM_TYPE";
   
    public static final String ITEM_DATA = "ITEM_DATA";

    /**
     * Adds a new Item to the database.  Will not work for ItemContainers.
     * @param string
     */
    public boolean storeItem(Item item)
    {       
        if (item instanceof ItemContainer)
        {
            // Add all the items in the container into the database.
            for (Item anItem : ((ItemContainer)item).getItems())
            {
                storeItem(anItem);
            } 
        }
        
        int itemID = item.getItemID();
        
        if (itemID == -1)
        {
            itemID = this.getNextIDNumber(NAME);
            item.setItemID(itemID);
        }
        System.out.println(""+itemID);
        
        try
        {
            String insertion = "INSERT INTO " + NAME + "(" + ITEM_ID + ", " + ITEM_TYPE + " , " + ITEM_DATA+") VALUES( ?, ?, ?)";
            PreparedStatement insertionStatement = m_databaseConnection.prepareStatement(insertion);
            insertionStatement.setInt(1, itemID);
            insertionStatement.setInt(2, item.getType());
            FMSObjCon.addObjectToStatement(3, item.getItemInfo(), insertionStatement);
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
     * Loads an item into the database.
     * @param id
     * @return
     */
    public Item loadItem(int id)
    {
        Item item = null;
        ResultSet result = null;
        try
        {
            Statement stmt = m_databaseConnection.createStatement();

            String select = "SELECT * FROM "+NAME+" WHERE "+ITEM_ID+"="+id;

            result = stmt.executeQuery(select);
            if (result.next())
            {
                int itemID = result.getInt(ITEM_ID);
                int type = result.getInt(ITEM_TYPE);
                byte[] buf = result.getBytes(ITEM_DATA);
                
                Hashtable<String,Object> data = (Hashtable<String,Object>)FMSObjCon.convertBytesToObject(buf);
                
                
                if (type == Item.ARMOR)
                {
                    item = Armor.constructArmor(data);
                }
                else if (type == Item.WEAPON)
                {
                    item = Weapon.constructWeapon(data); 
                }
                else if (type == Item.CONTAINER)
                {
                    Vector<Integer> itemIDs = (Vector<Integer>)data.get(ItemContainer.ITEMS);
                    item = ItemContainer.constructItemContainer(data);
                    for (Integer tempID  : itemIDs)
                    {
                        ((ItemContainer)item).store(loadItem(tempID));
                    }
                }
            }
            result.close();
            return item;
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

    // NEEDS work
    public void updateItem(int i, String string, int j)
    {
        // TODO Auto-generated method stub
        
    }

    
    
}
