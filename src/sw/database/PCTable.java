package sw.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sw.environment.Room;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.lifeform.PC;

import database.FMSObjCon;

public class PCTable extends SWQuery
{
    public static final String NAME = "PC_TABLE";
    
    public static final String PC_ID = "PC_ID";
    
    /**
     * The PRIMARY key for the table - no two players can have a character with the same name.
     */
    public static final String PC_NAME = "PC_NAME";
    
    /**
     * Character data stored in an XML Format.
     * <Character>
     * <desc>A description of the player</desc>
     * <max_health> 100 </max_health>
     * <curr_health> 50 </curr_health>
     * <room>ID of Room Located In</room>
     * <effects></effects>
     * <str>15</str>
     * <dex>15</dex>
     * <con>15</con>
     * <wis>15</wis>
     * <int>15</int>
     * <cha>15</cha>
     * 
     * <items>
     * <id></id>
     * <location></location>
     * </items>
     * <quests>
     * </quests>
     * TODO: Work on this.
     * </Character>
     */
    public static final String PC_DATA = "PC_DATA";
    
    /**
     * Stores the PC in the database.  Also stores all the items the player holds in the DB as well.  Does
     * not try and store the room the player is in or any players the player is in a party with.
     * @param character
     * @return
     */
    public boolean storePC(PC character)
    {
        int characterID = character.getID();
        
        if (characterID == -1)
        {
            characterID = this.getNextIDNumber(NAME);
            character.setID(characterID);
        }
        System.out.println(""+characterID);
        
        // We need to store all the items the player is holding.
        /**
         * TODO: Is there a better way to setup the various Table classes?
         */
        ItemTable it = new ItemTable();
        for (ArmorLocation loc : ArmorLocation.values())
        {
            Armor armor = character.getArmor(loc);
            if (armor != null)
                it.storeItem(armor);
        }
        
        for (ContainerLocation loc : ContainerLocation.values())
        {
            ItemContainer ic = character.getContainer(loc);
            if (ic != null)
                it.storeItem(ic);
        }
        
        for (HandLocation loc : HandLocation.values())
        {
            Item item = character.getHeldItem(loc);
            if (item != null)
                it.storeItem(item);
        }
        
        try
        {
            String insertion = "INSERT INTO " + NAME + "(" + PC_ID + " , " + PC_NAME + " , " + PC_DATA+") VALUES( ?, ?, ?)";
            PreparedStatement insertionStatement = m_databaseConnection.prepareStatement(insertion);
            insertionStatement.setInt(1, characterID);
            insertionStatement.setString(2, character.getName());
            FMSObjCon.addObjectToStatement(3, character.getLifeformInfo(), insertionStatement);
        // System.out.println(insertion);
        
            insertionStatement.execute();
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads the PC with that ID from the database.  Assumes the room the PC is in has
     * already been loaded.
     * @param id
     * @return
     */
    public PC loadPC(int id)
    {
        String select = "SELECT * FROM "+NAME+" WHERE "+PC_ID+"= ?";
        try
        {
            PreparedStatement selectStatement = m_databaseConnection.prepareStatement(select);
            selectStatement.setInt(1, id);
            return loadPlayerCharacter(selectStatement);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Loads the player's character based on the name of the character.
     * @param name
     * @return
     */
    public PC loadPC(String name)
    {
        String select = "SELECT * FROM "+NAME+" WHERE "+PC_NAME+"= ?";
        try
        {
            PreparedStatement selectStatement = m_databaseConnection.prepareStatement(select);
            selectStatement.setString(1, name);
            return loadPlayerCharacter(selectStatement);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private PC loadPlayerCharacter(PreparedStatement stmt)
    {
        PC player = null;
        ResultSet result = null;
        try
        {

            result = stmt.executeQuery();
            if (result.next())
            {
                int pcID = result.getInt(PC_ID);
                byte[] buf = result.getBytes(PC_DATA);
                
                Hashtable<String,Object> data = (Hashtable<String,Object>)FMSObjCon.convertBytesToObject(buf);
                 
                player = PC.constructPC(data);
                
                //Load the Player's armor
                Vector<Integer> armorIDs = (Vector<Integer>)data.get(PC.ARMOR);
                ItemTable it = new ItemTable();
                for (Integer tempID  : armorIDs)
                {
                    Armor armor = (Armor)it.loadItem(tempID);
                    player.wearArmor(armor);
                }
                
                Vector<Integer> containerIDs = (Vector<Integer>)data.get(PC.CONTAINERS);
                for (Integer tempID  : containerIDs)
                {
                    ItemContainer ic = (ItemContainer)it.loadItem(tempID);
                    
                    player.equipContainer(ic);
                }
                
                Hashtable<String,Integer> heldItems = (Hashtable<String,Integer>)data.get(PC.HELD);
                for (HandLocation loc : HandLocation.values())
                {
                    if (heldItems.containsKey(loc.name()))
                    {
                        int itemID = heldItems.get(loc.name());
                        player.holdInHand(it.loadItem(itemID), loc);
                        
                    }
                }
                
            }
            result.close();
            return player;
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
