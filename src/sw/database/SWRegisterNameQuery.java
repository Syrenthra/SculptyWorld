package sw.database;

import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;

public class SWRegisterNameQuery extends SWQuery
{
    String m_name;

    public SWRegisterNameQuery(SWServerConnection wlsc, SWMessage wlm)
    {
        super(wlsc, wlm);
        
        m_name = wlm.getMessage();
    }

    /**
     * Will attempt to add this new user to the database.  If it succeeds it
     * returns true.
     */
    @Override
    public boolean executeQuery()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
