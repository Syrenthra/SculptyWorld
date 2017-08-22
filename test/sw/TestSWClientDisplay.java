package sw;

import static org.junit.Assert.assertEquals;

import java.awt.event.KeyEvent;

import org.junit.Test;

import sw.SWClientDisplay;
import sw.net.SWServer;
import sw.net.SWServerSocket;



public class TestSWClientDisplay
{
    
    //@Test
    public void testInitialize()
    {
        SWClientDisplay sw = new SWClientDisplay();
        assertEquals("",sw.m_inputArea.getText());
        assertEquals("",sw.m_outputArea.getText());
    }
    
    //@Test
    public void testTextInput()
    {
        SWClientDisplay sw = new SWClientDisplay();
        sw.setInputText("Hello");
        assertEquals("Hello",sw.m_inputArea.getText());
        assertEquals("",sw.msgToSend);
        KeyEvent ke = new KeyEvent(sw.m_inputArea,1,1,1,KeyEvent.VK_ENTER);
        sw.keyTyped(ke);
        assertEquals("Hello",sw.msgToSend);
        assertEquals("",sw.m_inputArea.getText());
    }
    
    //@Test
    public void testOutputArea()
    {
        SWClientDisplay sw = new SWClientDisplay();
        sw.setOutputText("Hello");
        assertEquals("Hello",sw.m_outputArea.getText());
        sw.setOutputText("Temp");
        assertEquals("Temp",sw.m_outputArea.getText());
        sw.appendToOutputText("Hello");
        assertEquals("TempHello",sw.m_outputArea.getText());
    } 
    
    //@Test
    public void testCanConnectToServer()
    {
        SWServer server = new SWServer(new SWServerSocket(null), 2000);
        SWClientDisplay sw = new SWClientDisplay();
        sw.connectToServer();
        assertEquals(1,server.getConnections().size());
    }

}
