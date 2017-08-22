package mock;

import java.io.IOException;

import net.SecureServer;
import net.SecureServerConnection;
import net.ServerVulture;

public class MockServerConnection extends SecureServerConnection
{

    public MockServerConnection(ThreadGroup threadgroup, String threadname, ServerVulture vulture, SecureServer app)
    {
        super(threadgroup, threadname, vulture, app);
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void closeClient() throws IOException
    {
        // TODO Auto-generated method stub
        
    }
    
}
