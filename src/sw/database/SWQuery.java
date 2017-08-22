package sw.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sw.database.obj.SWToken;
import database.DatabaseManager;
import database.Query;

/**
 * Base class for all classes that need to access the database.
 * <p>
 * Last Modified: 4-30-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
*/

public abstract class SWQuery extends Query
{
    public static final int FAILED = 0;
    public static final int SUCCESS = 1;
    public static final int INVALID_TOKEN = 2;
    public static final int INVALID_NAME = 3;
    public static final int DATA_ALREADY_EXISTS = 4;
    
    public static final String DATABASE = "SculptyWorldDB";
    
    public static final String DB_LOCATION = "jdbc:mysql://localhost/SculptyWorldDB";
    public static final String LOGIN_NAME = "sculpty";
    public static final String PASSWORD = "world12Big";
    
    

    /**
     * The constructor for WLQuery.
     * 
     * @param name The name of the database to use, this should match the name being used
     *             by the DatabaseManager being used by the SWServer in its database lookup Hashtable.
     * @param wlsc The WLServerConnection that received the message.
     * @param wlm The message that created the WLQuery class object.
     * @see girard.ship.wl.io.WLServer#m_dbConnections
     */
    public SWQuery()
    {
        super(DATABASE);
    }


    /**
     * A method provided by SWQuery that allows subclasses to generate a unique id for a DB entry.
     * TODO: This needs to be accessible by other classes as well or need to redo XML to work with this.
     * @param dbConnection
     * @param person
     * @return
     */
    protected int getNextIDNumber(String table)
    {
        int orderNum = -1;
        String selectStr = new String("SELECT * FROM " + NumberingTable.NAME + " WHERE " + NumberingTable.TABLE_NAME + " = '" + table + "'");
        try
        {
            Statement stmt = m_databaseConnection.createStatement();
            ResultSet data = stmt.executeQuery(selectStr);

            if (data.next())
            {
                orderNum = data.getInt(NumberingTable.NEXT_NUMBER);
            }

            StringBuffer updateStm = new StringBuffer("UPDATE " + NumberingTable.NAME + " SET " + NumberingTable.NEXT_NUMBER + "=?");
            updateStm.append(" WHERE " + NumberingTable.TABLE_NAME + "= '" + table + "'");

            orderNum++;

            PreparedStatement pStmt = m_databaseConnection.prepareStatement(updateStm.toString());
            pStmt.setInt(1, orderNum);
            pStmt.execute();
            if (pStmt.getUpdateCount() > 0)
                return orderNum;
        }
        catch (SQLException sqle)
        {

        }
        return -1; // We failed someplace.
    }
}
