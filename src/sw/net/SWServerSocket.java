package sw.net;


import net.SecureObjectSocketInterface;
import sw.net.msg.SWMessage;
import sw.database.obj.SWToken;
import sw.database.obj.SWUser;

public class SWServerSocket extends SWSocket
    {

    public SWServerSocket(SWToken token)
        {
        m_token = token;
        m_socketType = SERVER;
        }

    /**
     * TODO: Think about removing this method.
     */
    public SecureObjectSocketInterface<SWMessage> createInstance()
        {
        SWServerSocket escos = new SWServerSocket(null);

        return escos;
        }
    }
