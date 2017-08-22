package sw.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import database.FMSObjCon;
import sw.environment.Room;
import sw.item.Armor;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.lifeform.Creature;
import sw.lifeform.PC;

public class RoomTable extends SWQuery
{
    public static final String NAME = "ROOM_TABLE";
    
    public static final String ROOM_ID = "ROOM_ID";
    
    public static final String ROOM_DATA = "ROOM_DATA";

    public boolean storeRoom(Room room)
    {
        
        int roomID = room.getID();
        if (roomID == -1)
        {
            roomID = this.getNextIDNumber(NAME);
            room.setID(roomID);
        }
        System.out.println(""+roomID);
        
        // TODO: Store off stuff in the Room, however we want to be careful we don't create a loop back or massive 
        // cascade effect.
        ItemTable it = new ItemTable();
        for (Item i : room.getItems())
        {
            it.storeItem(i);
        }
        PCTable pt = new PCTable();
        Hashtable<Integer,PC> pcs = room.getPCs();
        Enumeration<PC> ePCs = pcs.elements();
        while (ePCs.hasMoreElements())
        {
            pt.storePC(ePCs.nextElement());
        }
        
        CreatureTable ct = new CreatureTable();
        for (Creature c :  room.getCreatures())
        {
            ct.storeCreature(c);
        }
        
        try
        {
            String insertion = "INSERT INTO " + NAME + "(" + ROOM_ID + " , " + ROOM_DATA+") VALUES( ?, ?)";
            PreparedStatement insertionStatement = m_databaseConnection.prepareStatement(insertion);
            insertionStatement.setInt(1, roomID);
            FMSObjCon.addObjectToStatement(2, room.getRoomInfo(), insertionStatement);
        
            insertionStatement.execute();
            return true;
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
            return false;
        }
    }

    public Room loadRoom(int id)
    {
        Room room = null;
        ResultSet result = null;
        try
        {
            Statement stmt = m_databaseConnection.createStatement();

            String select = "SELECT * FROM "+NAME+" WHERE "+ROOM_ID+"="+id;

            result = stmt.executeQuery(select);
            if (result.next())
            {
                int itemID = result.getInt(ROOM_ID);
                byte[] buf = result.getBytes(ROOM_DATA);
                
                Hashtable<String,Object> data = (Hashtable<String,Object>)FMSObjCon.convertBytesToObject(buf);
                 
                room = Room.constructRoom(data);
                
                Vector<Integer> itemIDs = (Vector<Integer>)data.get(Room.ITEMS);
                ItemTable it = new ItemTable();
                for (Integer tempID  : itemIDs)
                {
                    room.addItem(it.loadItem(tempID));
                }
                
                Vector<Integer> pcIDs = (Vector<Integer>)data.get(Room.PCs);
                PCTable pt = new PCTable();
                for (Integer tempID  : pcIDs)
                {
                    room.addPC(pt.loadPC(tempID));
                }
                
                Vector<Integer> creatureIDs = (Vector<Integer>)data.get(Room.CREATURES);
                CreatureTable ct = new CreatureTable();
                for (Integer tempID : creatureIDs)
                {
                    room.addCreature(ct.loadCreature(tempID));
                }
            }
            result.close();
            return room;
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
