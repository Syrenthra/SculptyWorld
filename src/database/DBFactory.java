package database;

import java.lang.String;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBFactory
    {


/**
 * Used to add a table into the database.  Checks to make sure there is a
 * connection to the database before trying to insert the information.
 * Does not check to see if columns.length == dataTypes.length.
 *
 * @param tblName The name of the table to insert the row into.
 * @param columns A String array that contains the name for each column we are going to insert a value into.
 * @param dataTypes A String array that contains the dataTypes for each column.
 * @return Returns true if the insert is successful, false if fails.
 */
    public static boolean addTable(Connection database, String tblName, String[] columns, String[] dataTypes)
        {
        if (database == null)
            {
            System.err.println("No Connection Established - NULL");
            return false;
            }
        try
            {
            if (database.isClosed())
                {
                System.err.println("No Connection Established - Closed");
                return false;
                }
            StringBuffer sb = new StringBuffer("CREATE TABLE "+tblName+" ("+columns[0]+" "+dataTypes[0]);
            for (int i=1;i<columns.length;i++)
                {
                sb.append(", "+columns[i]+" "+dataTypes[i]);
                } 
            sb.append(")");
// Check out what SQL command sending to the database.
System.err.println("SB: "+sb.toString());

            Statement stmt = database.createStatement();
            if (stmt.executeUpdate(sb.toString()) > 0) 
                return true; 
            }
        catch (SQLException sqle)
            {
            sqle.printStackTrace();
            } 
        return false;
        }


/**
 * Removes a table from the database.
 * 
 * @param tblName The name of the table to remove from the database.
 * @return Returns true if the insert is successful, false if fails.
 */
    public static boolean dropTable(Connection database, String tblName)
        {
        try
        {
            if (database == null)
            {
                System.err.println("No connection - Null");
                return false;
            }
            if (database.isClosed())
            {
                System.err.println("No connection - Closed");
                return false;
            }
            Statement stmt = database.createStatement();
            if (stmt.executeUpdate("DROP TABLE " + tblName) > 0)
            {

                System.err.println("DROPPED " + tblName);
                return true;
            }
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
        }
        return false; 
        }

    public static void main(String[] args)
        {
// You should replace "Me" and "Me" with your login id for clipper.
        //JDBCHelper test = new JDBCHelper("jdbc:mysql://localhost/mary_kay","stella","veraann");
        
        }
    }
