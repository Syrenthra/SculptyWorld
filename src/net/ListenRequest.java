package net;


/**
 * Is a thread that sends a message at set intervals.  Used with the MessageListener
 * class object.
 *
 * @author Dudley Girard
 * @version ExNet III 3.3
 * @since JDK1.1  
 */

public class ListenRequest<M> extends Thread
    {
/**
 * The MesssageListener that this ListenRequest is attached to.
 */
    MessageListener<M> m_ML;
/**
 * The message that is to be sent.
 */
    M m_request;
/**
 * The sleep time between sendings of the message.
 */
    int m_delay;

/**
 * How many times to send the message.
 */
    int m_iterations = -1;

/**
 * Used to tell the thread to stop.
 */
    protected boolean m_flag = true;

/**
 * The constructor for the ListenRequest.
 *
 * @param app The MessageListener the ListenRequest is attached to.
 * @param msg The message to send.
 * @param n The sleep time inbetween sending the message.
 */
    public ListenRequest(MessageListener<M> app, M msg, int n)
        {   
        m_ML = app;
        m_request = msg;
        m_delay = n;
        }

/**
 * The constructor for the ListenRequest.
 *
 * @param app The MessageListener the ListenRequest is attached to.
 * @param msg The message to send.
 * @param n The sleep time inbetween sending the message.
 */
    public ListenRequest(MessageListener<M> app, M msg, int n, int i)
        {   
        m_ML = app;
        m_request = msg;
        m_delay = n;
        if (i > 0)
            m_iterations = i;
        }

/**
 * Uses the sendMessage to send the message then sleeps for the value of the m_delay.
 */
    public void run() 
        {
        try
            {
            if (m_iterations == -1)
                {
                while (m_flag)
                    {
                    Thread.sleep(m_delay);
                    m_ML.sendMessage(m_request);
                    }
                }
            else
                {
                while ((m_flag) && (m_iterations > 0))
                    {
                    Thread.sleep(m_delay);
                    m_ML.sendMessage(m_request);
                    m_iterations--;
                    }
                }
            }
        catch (InterruptedException e) { System.err.println(e); }
        }

/**
 * Used to set the value of the m_flag variable.
 */
    public void setFlag(boolean value)
        {
        m_flag = value;
        }
    }