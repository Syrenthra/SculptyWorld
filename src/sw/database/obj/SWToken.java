package sw.database.obj;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Completed_Task (8/31/2006): Must generate tokens that are acceptable to the database.
 * Generates security tokens so that we know a user is valid, these tokens are generated during
 * login and thrown away at logout.  The token is effectively a 16 digit base 62 value.
 *   
 * @author cdgira
 *
 */

public class SWToken implements Serializable
    {
    /**
     * 
     */
    private static final long serialVersionUID = -5789084554379259594L;
    
    public static final int TOKEN_LENGTH = 16;
    char[] m_token = new char[TOKEN_LENGTH];
    
    /**
     * Stores a copy of the first TOKEN_LENGTH values in the instance variable m_token.
     * @param token
     */
    public SWToken(char[] token)
        {
        m_token = Arrays.copyOf(token, m_token.length);
        }

    /**
     * Constructs a random new Token.
     * @return
     */
    public static SWToken constructToken()
        {
        SWToken newToken = null;
        
        char[] token = new char[TOKEN_LENGTH];
        for (int x=0;x<TOKEN_LENGTH;x++)
            {
            int group = (int)(Math.random()*3.0);
            int tokenVal = 48;
            switch (group)
                {
                case 0:
                    tokenVal = (int) (Math.random() * 10.0 + 48);
                    break;
                case 1:
                    tokenVal = (int) (Math.random() * 26.0 + 65);
                    break;
                case 2:
                    tokenVal = (int) (Math.random() * 26.0 + 97);
                    break;
                }
            
            token[x] = (char)(tokenVal); 
            }
        
        newToken = new SWToken(token);
        
        return newToken;
        }
    
    /**
     * 
     */
    public String toString()
        {
        return new String(m_token);
        }

    }
