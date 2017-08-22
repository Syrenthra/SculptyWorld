package sw.net.msg;

import sw.database.obj.SWToken;

public class SWTokenMsg extends SWMessage
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3832617370291614519L;

    private SWToken m_newToken;

    public SWTokenMsg(SWToken token)
    {
        super("TOKEN");

        m_newToken = token;
    }

    /**
     * 
     * @return The token that is being sent to the new connection to help ensure all future messages are from a specific user.
     */
    public SWToken getNewToken()
    {
        return m_newToken;
    }
}
