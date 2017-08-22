package sw;

import sw.net.SWServer;
import sw.net.SWServerSocket;

public class DemoServer
{
    public static void main(String[] args)
    {
        SWServer sw = new SWServer(new SWServerSocket(null), 2000);
    }

}
