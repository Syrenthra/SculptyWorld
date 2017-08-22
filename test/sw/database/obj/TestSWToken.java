package sw.database.obj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestSWToken
{

    @Test
    public void testBasics()
    {
        char[] token = new char[16];
        for (int x = 0; x < token.length; x++)
        {
            token[x] = 'a';
        }
        SWToken t = new SWToken(token);
        assertEquals(new String(token), t.toString());
    }

    @Test
    public void testTokenGeneration()
    {
        // Tokens should be generated quickly.
        for (int y = 0; y < 5; y++)
        {
            long start = System.currentTimeMillis();
            for (int x = 0; x < 100000; x++)
            {
                SWToken token1 = SWToken.constructToken();
                SWToken token2 = SWToken.constructToken();
            }
            long finish = System.currentTimeMillis();
            long totalTime = finish - start;
            assertTrue(totalTime < 300);
        }

        // Tokens should NOT equal each other
        for (int x = 0; x < 100000; x++)
        {
            SWToken token1 = SWToken.constructToken();
            SWToken token2 = SWToken.constructToken();
            assertFalse(token1.toString().equals(token2.toString()));
        }

    }

}
