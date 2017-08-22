package sw;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import sw.net.SWClientSocket;
import sw.net.msg.SWMessage;

import net.MessageListener;

public class SWClientDisplay extends JFrame implements KeyListener, ActionListener
{
    protected String msgToSend = "";
    protected JTextArea m_inputArea;
    protected JTextArea m_outputArea;
    protected JScrollPane m_scrollPane;
    
    protected MessageListener<SWMessage> m_connection;

    public SWClientDisplay()
    {
        setLayout(new BorderLayout());
        setTitle("Sculpty World - DEMO");
        m_inputArea = new JTextArea(2,80);
        m_inputArea.addKeyListener(this);
        add(m_inputArea,BorderLayout.SOUTH);
        
        
        m_outputArea = new JTextArea(30,80);
        m_outputArea.setEditable(false);
        m_scrollPane = new JScrollPane(m_outputArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(m_scrollPane,BorderLayout.CENTER);
        
        m_outputArea.setText("This VERY BASIC demo shows how NPCs will notice when creatures invade areas they care\n" +
        		"about and will offer quests to players to kill those creatures.  However if there are no\n" +
        		"creatures then the NPCs will not offer a quest\n\n" +
        		"Additionally, the demo illustraits how certain created creatures will seek to create additional\n" +
        		"creature spawn points for that creature type.\n\n" +
        		"Available Commands: west, east, clear creatures, quests\n\n"+
        		"To start the demo simply type: Demo");
        
        pack();
        setVisible(true);
    }
    
    
    public void setInputText(String data)
    {
        m_inputArea.setText(data);
    }


    @Override
    public void keyPressed(KeyEvent arg0)
    {

    }


    @Override
    public void keyReleased(KeyEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void keyTyped(KeyEvent evt)
    {
        char c = evt.getKeyChar();
        if(c == KeyEvent.VK_ENTER)
        {
            msgToSend = m_inputArea.getText().substring(0, m_inputArea.getText().length()-1);
            System.out.println("#"+msgToSend+"#");
            m_connection.sendMessage(new SWMessage(msgToSend));
            m_inputArea.setText("");
        }
        
    }


    /**
     * 
     * @return
     */
    public String getInputText()
    {
        return m_inputArea.getText();
    }


    /**
     * 
     * @param data
     */
    public void appendToOutputText(String data)
    {
        m_outputArea.append(data);
    }
    
    /**
     * 
     * @param data
     */
    public void setOutputText(String data)
    {
        m_outputArea.setText(data);
    }


    /**
     * Creates a connection to the MUD.
     */
    public void connectToServer()
    {
        SWClientSocket socket = new SWClientSocket(null);
        m_connection = new MessageListener<SWMessage>(socket);
        m_connection.connect("127.0.0.1", 2000);
        m_connection.addActionListener(this);
        m_connection.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        SWMessage msg = (SWMessage)arg0.getSource();
        appendToOutputText("\n"+msg.getMessage()+"\n");
        m_outputArea.setCaretPosition(m_outputArea.getText().length());
        
        
    }
    
    public static void main(String args[])
    {
        SWClientDisplay display = new SWClientDisplay();
        display.connectToServer();
    }



}
