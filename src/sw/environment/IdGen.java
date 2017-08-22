package sw.environment;

public class IdGen
{
    private static int m_id = 0;
    
    public static int getID()
    {
        return m_id++;
    }

}
