package sw.database;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestNumberingTable
{
    @Test
    public void testGlobals()
    {
        assertEquals("NUMBERING_TABLE",NumberingTable.NAME);
        assertEquals("NEXT_NUMBER",NumberingTable.NEXT_NUMBER);
        assertEquals("TABLE_NAME",NumberingTable.TABLE_NAME);
    }

}
