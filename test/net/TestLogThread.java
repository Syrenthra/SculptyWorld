package net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;

import org.junit.After;
import org.junit.Test;


public class TestLogThread
{
    /**
     * Remove any log files we created while testing.
     */
    @After
    public void after()
    {
        File f = new File("Test.log");
        if (f.exists())
        {
            f.delete();
        }
    }
    
    @Test
    public void testBasics()
    {
        LogThread log = new LogThread("Test");
        assertEquals(0,log.m_logData.length());
        File f = new File("Test.log");
        assertFalse(f.exists());
    }
    
    @Test
    public void testUpdateLog()
    {
        LogThread log = new LogThread("Test");
        int dateLength = Calendar.getInstance().getTime().toString().length()+4;
        String testString = "";
        for (int x=0;x<50-dateLength;x++)
        {
            testString+="t";
        }
        log.addMessageToLog(testString);
        assertEquals(50,log.m_logData.length());
        log.updateLog();
        assertEquals(50,log.m_logData.length());
        for (int x=0;x<98;x++)
        {
            log.addMessageToLog(testString);
        }
        assertEquals(4950,log.m_logData.length());
        log.updateLog();
        assertEquals(4950,log.m_logData.length());
        log.addMessageToLog(testString+"t");
        log.updateLog();
        assertEquals(0,log.m_logData.length());
        File f = new File("Test.log");
        assertTrue(f.exists());
    }
    
    @Test
    public void testFlushLog()
    {
        LogThread log = new LogThread("Test");
        int dateLength = Calendar.getInstance().getTime().toString().length()+4;
        String testString = "";
        for (int x=0;x<50-dateLength;x++)
        {
            testString+="t";
        }
        log.addMessageToLog(testString);
        assertEquals(50,log.m_logData.length());
        log.flushLog();
        assertEquals(0,log.m_logData.length());
        File f = new File("Test.log");
        assertTrue(f.exists());
    }

}
