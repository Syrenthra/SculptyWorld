package net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sw.net.SWClientSocket;
import sw.net.SWServer;
import sw.net.SWServerConnection;
import sw.net.SWServerSocket;
import sw.net.msg.SWMessage;
import sw.net.msg.SWTokenMsg;
import sw.net.state.InitialConnectionState;
import sw.net.state.ServerConnectionState;


public class TestMessageListener implements ActionListener
{
    int messageCount;
    SWServer server;
    SWClientSocket client = new SWClientSocket(null);

    @Before
    public void before()
    {
        server = new SWServer(new SWServerSocket(null), 2000);
        messageCount = 0;
    }

    @After
    public void after()
    {
        server.shutdown();
    }
    
    @Test
    public void testWillSendMessagesIfNoResponses() throws IOException, InterruptedException, ClassNotFoundException
    {
        
        MessageListener<SWMessage> ml = new MessageListener<SWMessage>(client);
        ml.addActionListener(this);
        ml.connect("127.0.0.1", 2000);
        ml.start();
        
        Thread.sleep(500);
        
        SWServerConnection sc = (SWServerConnection) server.getConnections().get(0);
        sc.setServerConnectionState(new NullServerConnectionState());

        Thread.sleep(500);

        SWMessage msg = new SWMessage("Msg1");
        ml.sendMessage(msg);

        Thread.sleep(500);

        msg = new SWMessage("Msg2");
        ml.sendMessage(msg);

        Thread.sleep(500);

        assertTrue(sc.getServerConnectionState() instanceof NullServerConnectionState);
        
        sc.setServerConnectionState(new InitialConnectionState(sc));
        
        msg = new SWMessage("Msg2");
        ml.sendMessage(msg);

        Thread.sleep(500);
        
        assertEquals(1, messageCount);
    }

    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        SWMessage sw = (SWMessage)arg0.getSource();
        System.out.println(sw.getMessage());
        messageCount++;
        
    }

}

class NullServerConnectionState extends ServerConnectionState
{

    @Override
    public void executeAction(SWMessage msg)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public SWMessage getMessage()
    {
        return null;
    }
    
}
