package sw.database.req;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import sw.database.PCTable;
import sw.database.SWQuery;

/**
 * Loads the list of character names a player can choose from to play in the game.
 */
public class SWCharacterListQuery extends SWQuery
{
    Vector<String> m_characterList;
    int m_playerID;
    
    public SWCharacterListQuery(int id)
    {
        m_playerID = id;
    }

    /**
     * Loads the list of character names a player can choose from to play in the game.
     */
    public int executeQuery()
    {
        m_characterList = new Vector<String>();
        try
        {
            synchronized (m_databaseInfo)
            {   
                System.out.println(""+m_playerID);
                // First check to see if already in the table.
                String selectStr = new String("SELECT * FROM "+PCTable.NAME+" WHERE "+PCTable.PC_ID+" = "+m_playerID);
                Statement stmt = m_databaseConnection.createStatement();
                ResultSet data = stmt.executeQuery(selectStr);

                while (data.next()) 
                {
                    m_characterList.addElement(data.getString(PCTable.PC_NAME));
                }

                return SUCCESS;
            }
        }
        catch (SQLException e)
        {
            m_manager.addToLog(e);
            closeConnection();
        }
        return FAILED;
    }

    /**
     * 
     * @return The array m_characterList that stores the list of characters.
     */
    public Vector<String> getCharacterList()
    {
        return m_characterList;
    }

}
