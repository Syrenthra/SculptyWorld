package mock;

import java.io.IOException;

import net.SecureObjectSocketInterface;
import net.SecureServer;
import net.ServerVulture;
import sw.net.SWServer;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;


public class MockSWServerConnection extends SWServerConnection
{
   public MockSWServerConnection(SecureObjectSocketInterface<SWMessage> wlos, ThreadGroup threadgroup, ServerVulture vulture, SWServer app)
   {
       super(wlos, threadgroup, vulture, app);
   }

   @Override
   public void closeClient() throws IOException
   {
   }

   @Override
   public SecureObjectSocketInterface<SWMessage> getClient()
   {
       return m_client;
   }

   /**
    * Listens on the incoming stream for any messages. If a message is received
    * it checks to see if it is valid then runs getGeneralServerResponse. If
    * there is a message to send back it sends it back via the out stream. The
    * streams are then closed and the thread stops.
    * <p>
    * 
    * @see girard.ship.wl.io.msg.SWMessage
    */
   public void run()
   {

   }

   @Override
   public void addToLog(String str)
   {

   }

   @Override
   public void addToLog(Exception e)
   {
       
   }

   protected SecureServer getTheServer()
   {
       return m_theServer;
   }

   @Override
   public void notifyVulture()
   {

   }

   @Override
   public String toString()
   {
       return this.getName();
   }
}

