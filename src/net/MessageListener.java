package net;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Is a class object that uses Object streams to send and receive messages. It
 * notifies when new messages have been received by generating ActionEvents. If
 * there are no actionListeners listening when a message is received then the
 * message is added to a Vector.
 * 
 * This is used by clients that want to connect to a SecureServer.
 * 
 * @author Dudley Girard
 * @version ExNet III 3.2
 * @since JDK1.1
 */

public class MessageListener<M> extends Thread
{
    /**
     * Used to give each ListenRequest that is added to m_listenRequests a
     * unique lookup value.
     * 
     * @see girard.ship.io.ListenRequest
     */
    protected static int m_listenReqCounter = 0;

    /**
     * An abstract interface that is used to manage the connecting, sending and
     * receiving of objects. Allows for testing outside a network environment.
     */
    protected SecureObjectSocketInterface<M> m_socketManager;

    /**
     * Allows the MessageListener to notify attached listeners of action events.
     */
    protected transient ActionListener m_actionListener;

    /**
     * How many actionListeners are listening.
     */
    protected transient int m_listeners = 0;

    /**
     * Any messages to be sent by the MessageListener are placed here.
     */
    protected Vector<M> m_outgoing = new Vector<M>();

    /**
     * Any messages that were sent to the MessageListener when no one was
     * listening are placed here.
     */
    protected Vector<M> m_incoming = new Vector<M>();

    /**
     * The table of ListenRequests attached to this MessageListener.
     * 
     * @see girard.ship.io.ListenRequest
     */
    protected Hashtable<Integer, ListenRequest<M>> m_listenRequests = new Hashtable<Integer, ListenRequest<M>>();

    /**
     * Let's the MessageListener know when all connections have been closed
     * down.
     */
    protected boolean m_cleanUpFlag = false;

    /**
     * Let's the MessageListener know when to stop running.
     */
    protected boolean m_flag = true;

    /**
     * The constructor for the MessageListener.
     * 
     * @param host
     *            The host name or IP address of the computer where the Server
     *            is running.
     * @param port
     *            The port address that the Server is listening at.
     */
    public MessageListener(SecureObjectSocketInterface<M> os)
    {
        m_socketManager = os;
    }

    public void connect(String host, int port)
    {
        try
        {
            m_socketManager.connect(host, port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * What the MessageListener does while it is running. It checks to see if
     * there is an incoming message. If there is it retrieves it, then if there
     * are any ActionListeners attached it notifies them of the new message. If
     * there are no attached actionListeners it adds the message to the
     * m_incoming Vector. After checking for incoming messages it checks to see
     * if there are any messages in the m_outgoing queue and if so it sends them
     * on their way, clearing the queue as it does so.
     */
    public void run()
    {
        m_cleanUpFlag = true;

        try
        {
            while (m_flag)
            {
                synchronized (m_socketManager)
                {
                    while ((m_outgoing.size() > 0) && (m_flag))
                    {
                        m_socketManager.writeObject(m_outgoing.elementAt(0));

                        m_outgoing.removeElementAt(0);
                    }
                }

                synchronized (m_socketManager)
                {
                    boolean in_flag = true;

                    while ((in_flag) && (m_flag))
                    {
                        //System.out.println("Check for message");
                        M obj = (M) m_socketManager.readObject();

                        if (obj != null)
                        {
                            if (m_listeners > 0)
                                m_actionListener.actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "Message"));
                            else
                                m_incoming.addElement(obj);
                        }
                        else
                        {
                            in_flag = false;
                        }
                    }
                }

                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    System.err.println(e);
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            System.err.println(ioe);
        }
        catch (ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
            System.err.println(cnfe);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (m_outgoing.size() > 0)
            {
                System.err.println(m_outgoing.elementAt(0));
            }
        }

        m_cleanUpFlag = false;
    }

    /**
     * Adds a new ActionListener to the MessageListener.
     */
    public synchronized void addActionListener(ActionListener l)
    {
        m_actionListener = AWTEventMulticaster.add(m_actionListener, l);
        m_listeners++;
    }

    /**
     * Creates a ListenRequest thread for the message then adds the
     * ListenRequest thread to the m_listenRequests table.
     * 
     * @param req
     *            The message to be added to the m_listenRequests table.
     * @param wait
     *            How long to wait inbetween sendings of the message.
     * @return The id for where the ListenRequest was placed in the
     *         m_listenRequests table.
     * @see girard.ship.io.ListenRequest
     */
    public int addListenRequest(M req, int wait)
    {
        int reqNum;
        ListenRequest<M> lr;

        synchronized (m_listenRequests)
        {
            lr = new ListenRequest<M>(this, req, wait);
            m_listenRequests.put(new Integer(m_listenReqCounter), lr);
            reqNum = m_listenReqCounter;
            m_listenReqCounter++;
            lr.start();
        }

        return reqNum;
    }

    /**
     * Creates a ListenRequest thread for the message then adds the
     * ListenRequest thread to the m_listenRequests table. The thread only sends
     * the message a set number of times.
     * 
     * @param req
     *            The message to be added to the m_listenRequests table.
     * @param wait
     *            How long to wait inbetween sendings of the message.
     * @return The id for where the ListenRequest was placed in the
     *         m_listenRequests table.
     * @see girard.ship.io.ListenRequest
     */
    public int addListenRequest(M req, int wait, int iterations)
    {
        int reqNum;
        ListenRequest<M> lr;

        synchronized (m_listenRequests)
        {
            lr = new ListenRequest<M>(this, req, wait, iterations);
            m_listenRequests.put(new Integer(m_listenReqCounter), lr);
            reqNum = m_listenReqCounter;
            m_listenReqCounter++;
            lr.start();
        }

        return reqNum;
    }

    /**
     * Shuts down the MessageListener. Lets you decide if you want to wait to
     * let it finish processing any outgoing messages before completely shutting
     * down.
     * 
     * @param value
     *            Is the minimum number of outgoing messages before stopping, -1
     *            means infinite number allowed.
     */
    public void finalize(int value)
    {
        if (value > -1)
        {
            while ((m_outgoing.size() > value) && (m_cleanUpFlag))
            {
                try
                {
                    sleep(500);
                }
                catch (InterruptedException ie)
                {
                    ;
                }
            }
        }
        m_flag = false;
        int counter = 0; // So we don't loop fovever.
        while (m_cleanUpFlag)
        {
            try
            {
                sleep(500);
            }
            catch (InterruptedException ie)
            {
                ;
            }
            System.err.println("Waiting: " + m_cleanUpFlag);
            if (counter > 3)
                return;
            counter++;
        }
        return;
    }

    /**
     * Clean out any data that has been sent to the MessageListener but not read
     * yet.
     */
    public void flushSockets()
    {
        synchronized (m_incoming)
        {
            if (m_incoming.size() > 0)
            {
                m_incoming.removeAllElements();
            }
        }
    }

    /**
     * Gets the TCPMessage at the 0 index of the m_incoming Vector.
     * 
     * @return The TCPMessage at the 0 index or null if there are no messages.
     */
    public M getMessage()
    {
        M msg = null;

        synchronized (m_incoming)
        {
            if (m_incoming.size() > 0)
            {
                msg = m_incoming.elementAt(0);
                m_incoming.removeElementAt(0);
                return msg;
            }
        }
        return msg;
    }

    /**
     * How many messages are in the m_outgoing queue.
     * 
     * @return The number of messages in m_outgoing.
     */
    public int getNumOutMessages()
    {
        return m_outgoing.size();
    }

    /**
     * Removes an ActionListener from the MessageListener.
     * 
     * @param l
     *            The ActionListener to remove.
     */
    public synchronized void removeActionListener(ActionListener l)
    {
        if (m_listeners > 0)
        {
            m_actionListener = AWTEventMulticaster.remove(m_actionListener, l);
            m_listeners--;
        }
    }

    /**
     * Removes all ActionListeners that are attached to the MessageListener.
     */
    public void removeAllListenRequests()
    {
        synchronized (m_listenRequests)
        {
            Enumeration<ListenRequest<M>> enum1 = m_listenRequests.elements();
            while (enum1.hasMoreElements())
            {
                enum1.nextElement().setFlag(false);
            }
            m_listenRequests.clear();
        }
    }

    /**
     * Removes a ListenRequest from m_listenRequests.
     * 
     * @param id
     *            The id of the ListenRequest to be removed.
     */
    public void removeListenRequest(int id)
    {
        synchronized (m_listenRequests)
        {
            ListenRequest<M> obj = m_listenRequests.get(new Integer(id));
            if (obj != null)
            {
                m_listenRequests.remove(new Integer(id));
                obj.setFlag(false);
            }
        }
    }

    /**
     * Used to send a message on the MessageListener. Adds the message to the
     * m_outgoing Vector.
     * 
     * @param msg
     *            The TCPMessage to be sent.
     */
    public void sendMessage(M msg)
    {
        synchronized (m_outgoing)
        {
            m_outgoing.addElement(msg);
        }
    }

    /**
     * Sets the value of the m_flag.
     */
    public void setFlag(boolean value)
    {
        m_flag = false;
    }
}
