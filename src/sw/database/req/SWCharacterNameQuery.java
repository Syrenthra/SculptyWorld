package sw.database.req;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sw.database.PCTable;
import sw.database.SWQuery;


public class SWCharacterNameQuery extends SWQuery
{

    int m_userID;
    String m_characterName;
    /**
     * For now all characters are the exact same except name.
     * TODO: Build a viable character generation system.
     * TODO: Do we need a default room that all players can be set to?
     */
    String m_characterData = "<Character>"+
                                "<desc>A description of the player</desc>"+
                                "<max_health>100</max_health>"+
                                "<curr_health>50</curr_health>"+
                                "<room></room>"+
                                "<effects></effects>"+
                                "<str>15</str>"+
                                "<dex>15</dex>"+
                                "<con>15</con>"+
                                "<wis>15</wis>"+
                                "<int>15</int>"+
                                "<cha>15</cha>"+
                                "<items></items>"+
                                "<quests></quests>"+
                                "</Character>";

    public SWCharacterNameQuery(int id, String name)
    {
        m_userID = id;
        m_characterName = name;
    }


    public int executeQuery()
    {
        if (SWQuery.isValidNameString(m_characterName))
        {
            try
            {
                synchronized (m_databaseInfo)
                {   
                    // First check to see if already in the table.
                    String selectStr = new String("SELECT * FROM "+PCTable.NAME+" WHERE "+PCTable.PC_NAME+" = '"+m_characterName+"'");
                    Statement stmt = m_databaseConnection.createStatement();
                    ResultSet data = stmt.executeQuery(selectStr);
                    if (data.next()) 
                    {
                        return SWQuery.DATA_ALREADY_EXISTS;
                    }
                    else
                    {
                        
                        StringBuffer insertStm = new StringBuffer("INSERT INTO "+PCTable.NAME+" ("+PCTable.PC_ID+" , "+PCTable.PC_NAME+" , "+PCTable.PC_DATA);
                        insertStm.append(") VALUES ( ?, ?, ?)");
                        PreparedStatement pStmt = m_databaseConnection.prepareStatement(insertStm.toString());
                        pStmt.setInt(1, m_userID);
                        pStmt.setString(2, m_characterName);
                        pStmt.setString(3, m_characterData);

                        pStmt.execute();
                        if (pStmt.getUpdateCount() > 0)
                            return SUCCESS;
                        return FAILED;
                    } 
                }
            }
            catch (SQLException e)
            {
                m_manager.addToLog(e);
                closeConnection();
                return SWQuery.FAILED;
            }
        }
        return SWQuery.INVALID_NAME;
    }

}
