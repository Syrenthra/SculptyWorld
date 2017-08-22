package sw.net;

import java.io.DataInputStream;
import java.util.Vector;

/**
 * This is used so that SWSocket does not block on a read.
 * 
 * @author cdgira
 *
 */
public class SWReadObjectThread extends Thread
{
    Vector<byte[]> m_messages = new Vector<byte[]>();

    DataInputStream m_in;

    public SWReadObjectThread(DataInputStream in)
    {
        m_in = in;
        start();
    }

    public void run()
    {
        while (true)
        {
            try
            {
                int size = m_in.readInt();

                byte[] data = new byte[size];

                m_in.readFully(data);

                m_messages.add(data);
            }
            catch (Exception e)
            {
                // Under normal circumstances this means that the socket
                // has closed so time to shutdown the thread.
                return;
            }
        }
    }
    
    public byte[] getNextMessage()
    {
        if (m_messages.size() > 0)
            return m_messages.remove(0);
        return null;
    }

}
