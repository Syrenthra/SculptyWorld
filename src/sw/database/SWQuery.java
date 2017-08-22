package sw.database;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;
import sw.database.obj.SWToken;
import sw.database.obj.SWUser;
import database.DatabaseInfo;
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
    public static final String DATABASE = "SculptyWorldDB";

    /**
     * The constructor for WLQuery.
     * 
     * @param name The name of the database to use, this should match the name being used
     *             by the WLGeneralServer in its database lookup Hashtable.
     * @param wlsc The WLServerConnection that received the message.
     * @param wlm The message that created the WLQuery class object.
     * @see girard.ship.wl.io.WLServer#m_dbConnections
     */
    public SWQuery(SWServerConnection wlsc, SWMessage wlm)
    {
        super(DATABASE, wlsc);

        // TODO: I think we still need the SWServerConnection to write to the log file.
        // TODO: Look at changing this problem so SWServerConnection is not coupled to
        // TODO: Query.
        
        DatabaseInfo con = DatabaseManager.getInstance().getDBAddress(getQueryDB());
        setDatabaseInfo(con);
        setServerConnection(wlsc);
    }
    
    /**
     * This executes what ever operation the subclass needs to do with the database.
     * Results of the operation depends on the subclasses.  Actions that returned
     * data, should have that data stored in instance variables that can then be
     * retrieved with getters.
     * 
     * @return Returns true if the operation succeeds.
     */
    public abstract boolean executeQuery();

}
