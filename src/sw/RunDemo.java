package sw;

import sw.net.SWServer;
import sw.net.SWServerSocket;

public class RunDemo
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        SWServer sw = new SWServer(new SWServerSocket(null), 2000);
        
        
        
        SWClientDisplay display = new SWClientDisplay();
        display.connectToServer();
    }

}
