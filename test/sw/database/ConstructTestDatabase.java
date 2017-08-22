package sw.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DatabaseMetaData;

import database.DatabaseInfo;
import database.DatabaseManager;

public class ConstructTestDatabase
{
    public static void destroyTestDatabase() throws Exception
    {
        DatabaseManager dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);

        Connection dbConnection = di.makeConnection();
        
        String[] tables = { ItemTable.NAME, CreatureTable.NAME, RoomTable.NAME, PCTable.NAME, UserTable.NAME, NumberingTable.NAME };

        String[] types = { "TABLE" };
        DatabaseMetaData meta = dbConnection.getMetaData();
        
        for (int x=0;x<tables.length;x++)
        {
            ResultSet rs = meta.getTables(null, null, tables[x], types);
            if (rs.next())
            {
                //System.out.println("Dropping" +tables[x]);
                String dropTable = new String("DROP TABLE " + tables[x]);
                Statement stmt = dbConnection.createStatement();
                stmt.executeUpdate(dropTable);
            }
        }

        
        di.closeConnection();
        DatabaseManager.getInstance().shutdown();
    }

    public static void constructTestDatabase() throws Exception
    {
        DatabaseManager dm = DatabaseManager.getInstance();
        dm.activateJDBC();
        DatabaseInfo di = new DatabaseInfo(SWQuery.DB_LOCATION, SWQuery.LOGIN_NAME, SWQuery.PASSWORD);

        Connection dbConnection = di.makeConnection();

        String[] types = { "TABLE" };
        DatabaseMetaData meta = dbConnection.getMetaData();
        ResultSet rs = meta.getTables(null, null, NumberingTable.NAME, types);
        if (!rs.next())
        {

            String createTable = new String("CREATE TABLE " + NumberingTable.NAME + 
                    " (" + NumberingTable.TABLE_NAME + " VARCHAR(40) NOT NULL ," + 
                    NumberingTable.NEXT_NUMBER + " INTEGER NOT NULL ," + 
                    "PRIMARY KEY (" + NumberingTable.TABLE_NAME + ") )");
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(createTable);

            String insertData = new String("INSERT INTO " + NumberingTable.NAME + 
                    " (" + NumberingTable.TABLE_NAME + ", " + 
                    NumberingTable.NEXT_NUMBER + ") VALUES ( ?, ? )");
            PreparedStatement pStmt = dbConnection.prepareStatement(insertData.toString());
            pStmt.setString(1, UserTable.NAME);
            pStmt.setInt(2, 0);
            pStmt.execute();
            
            insertData = new String("INSERT INTO " + NumberingTable.NAME + 
                    " (" + NumberingTable.TABLE_NAME + ", " + 
                    NumberingTable.NEXT_NUMBER + ") VALUES ( ?, ? )");
            pStmt = dbConnection.prepareStatement(insertData.toString());
            pStmt.setString(1, ItemTable.NAME);
            pStmt.setInt(2, 0);
            pStmt.execute();
            
            insertData = new String("INSERT INTO " + NumberingTable.NAME + 
                    " (" + NumberingTable.TABLE_NAME + ", " + 
                    NumberingTable.NEXT_NUMBER + ") VALUES ( ?, ? )");
            pStmt = dbConnection.prepareStatement(insertData.toString());
            pStmt.setString(1, RoomTable.NAME);
            pStmt.setInt(2, 0);
            pStmt.execute();
            
            insertData = new String("INSERT INTO " + NumberingTable.NAME + 
                    " (" + NumberingTable.TABLE_NAME + ", " + 
                    NumberingTable.NEXT_NUMBER + ") VALUES ( ?, ? )");
            pStmt = dbConnection.prepareStatement(insertData.toString());
            pStmt.setString(1, PCTable.NAME);
            pStmt.setInt(2, 0);
            pStmt.execute();
        }

        rs = meta.getTables(null, null, UserTable.NAME, types);
        if (!rs.next())
        {
            String createTable = new String("CREATE TABLE " + UserTable.NAME + 
                    " (" + UserTable.USER_ID + " INTEGER NOT NULL ," + 
                    UserTable.USER_NAME + " VARCHAR(20) NOT NULL ," + 
                    UserTable.USER_PASSWORD + " VARCHAR(20) NOT NULL ," +
                    "PRIMARY KEY (" + UserTable.USER_ID + ") )");
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(createTable);
        }

        rs = meta.getTables(null, null, PCTable.NAME, types);
        if (!rs.next())
        {
        // Removed the auto-increment function (probably should not have used it anyway).
            String createTable = new String("CREATE TABLE " + PCTable.NAME + 
                " (" + PCTable.PC_ID + " INTEGER NOT NULL ," + 
                PCTable.PC_NAME + " VARCHAR(45) NOT NULL ," + 
                PCTable.PC_DATA + " BLOB NOT NULL ," + 
                "PRIMARY KEY ( " + PCTable.PC_ID + ") ," + 
                "UNIQUE KEY (" + PCTable.PC_NAME + "))"); 
          
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(createTable);
        }

        rs = meta.getTables(null, null, RoomTable.NAME, types);
        if (!rs.next())
        {
            String createTable = new String("CREATE TABLE " + RoomTable.NAME + 
                    " (" + RoomTable.ROOM_ID + " INTEGER NOT NULL ," + 
                    RoomTable.ROOM_DATA + " BLOB NOT NULL," + 
                    "PRIMARY KEY ("+RoomTable.ROOM_ID+") )");
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(createTable);
        }

        rs = meta.getTables(null, null, CreatureTable.NAME, types);
        if (!rs.next())
        {
            String createTable = new String("CREATE TABLE "+CreatureTable.NAME +
                "(" + CreatureTable.CREATURE_ID + " INTEGER NOT NULL , " + 
                CreatureTable.CREATURE_DATA + " BLOB NOT NULL," + 
                "PRIMARY KEY ("+CreatureTable.CREATURE_ID+") )");
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(createTable);
        }
        
        rs = meta.getTables(null, null, ItemTable.NAME, types);
        if (!rs.next())
        {
            String createTable = new String("CREATE TABLE " + ItemTable.NAME + 
                    " (" + ItemTable.ITEM_ID + " INTEGER NOT NULL ," + 
                    ItemTable.ITEM_TYPE + " INTEGER NOT NULL," + 
                    ItemTable.ITEM_DATA + " BLOB NOT NULL," + 
                    "PRIMARY KEY ("+ItemTable.ITEM_ID+") )");
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(createTable);
        }
        
        di.closeConnection();
    }

    public static void main(String[] args)
    {
        try
        {
            ConstructTestDatabase.constructTestDatabase();
            ConstructTestDatabase.destroyTestDatabase();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
