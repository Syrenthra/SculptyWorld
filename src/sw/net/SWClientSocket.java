package sw.net;


import net.SecureObjectSocketInterface;
import sw.net.msg.SWMessage;
import sw.database.obj.SWToken;
import sw.database.obj.SWUser;

public class SWClientSocket extends SWSocket
    {
    public SWClientSocket(SWToken token)
        {
        m_token = token;
        m_socketType = CLIENT;
        }

    /**
     * TODO: See if this should be removed.
     */
    public SecureObjectSocketInterface<SWMessage> createInstance()
        {
        SWClientSocket escos = new SWClientSocket(null);

        return escos;
        }
    }
