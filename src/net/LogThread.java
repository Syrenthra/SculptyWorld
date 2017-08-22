package net;

/**
 *  This is used to run a thread to periodically save log information
 *  to a log file.
 *
 * Author: Dudley Girard
 * Started: 9-25-2001
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class LogThread extends Thread
    {
    protected RandomAccessFile m_log;

    protected StringBuffer m_logData = new StringBuffer("");

    protected String m_fileName = "Base";

    protected boolean m_runFlag = true; // Used to stop the thread nicely.

    /**
     * Create our log server thread.  The Sname is the name of the thread and the log file.  The file
     * is created in the same directory where the LogThread was launched.
     */
    public LogThread(String sName)
        {
        super(sName);
        setPriority(5);

        m_fileName = sName;
        }

    /**
     * Updates m_logData to include the information stored in m_msg along with a
     * time stamp. If m_logData contains over 5000 characters of information
     * then we write out its information to then end of the file named in
     * m_fileName.
     * 
     */
    public synchronized void updateLog()
        {
        if (m_logData.length() > 5000)
            {
            try
                {
                m_log = new RandomAccessFile(m_fileName + ".log", "rw");
                m_log.seek(m_log.length());
                m_log.writeBytes(m_logData.toString());
                m_logData = new StringBuffer("");
                m_log.close();
                }
            catch (IOException ioe)
                {
                System.err.println(ioe);
                ioe.printStackTrace();
                }
            }
        }

    // Exit with an error message, when an exception occurs.
    public static void fail(Exception e, String msg)
        {
        System.err.println(msg + ": " + e);
        System.exit(1);
        }

    /**
     * Force the log to write out any information that may be stored in the m_logData
     * variable.
     * 
     */
    public synchronized void flushLog()
        {
        // System.err.println("Flushing: "+m_logData.toString());
        try
            {
            m_log = new RandomAccessFile( m_fileName + ".log", "rw");
            m_log.seek(m_log.length());
            m_log.writeBytes(m_logData.toString());
            m_logData = new StringBuffer("");
            m_log.close();
            }
        catch (IOException ioe)
            {
            System.err.println(ioe);
            ioe.printStackTrace();
            }
        }

    /**
     * Quietly runs while waiting to see if any messages need to be saved to the
     * log.
     */
    public void run()
        {
        while (m_runFlag)
            {
            try
                {
                Thread.sleep(100);
                }
            catch (InterruptedException e)
                {
                System.err.println(e);
                }

            if (m_logData.length() > 0)
                {
                updateLog();
                }
            }
        }

    public void shutdown()
        {
        flushLog();
        m_runFlag = false;
        }

    public void addMessageToLog(String msg)
        {
        m_logData.append(msg + " - " + Calendar.getInstance().getTime() + "\n");
        }

    public void addMessageToLog(Exception e)
        {
        StackTraceElement[] ste = e.getStackTrace();
        
        m_logData.append("ERROR - " + Calendar.getInstance().getTime() + "\n");
        m_logData.append("Message: "+e.getMessage()+"\n");
        for (int i=0;i<ste.length;i++)
            {
            m_logData.append(ste[i].toString()+"\n");
            }
        m_logData.append("--  End Error -- \n");
        }
    }
