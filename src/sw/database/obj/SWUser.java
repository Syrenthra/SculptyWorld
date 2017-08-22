package sw.database.obj;

import java.io.Serializable;

import database.InvalidDBStringException;
import database.Query;

/**
 * Stores all the information related to a user of the MK Inventory management system.  Tracks
 * the person's name, email, password, and login id.
 * 
 * @author cdudleygirard
 *
 */
public class SWUser implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -1315662486004919657L;

    int m_userID = -1;

    String m_firstName = null;

    String m_lastName = null;

    String m_email = null;

    /**
     * All letters are stored upper case ensuring all players have a unique login name.
     */
    String m_userLoginName = null;

    String m_password = null;

    SWToken m_accessToken = null;
    
    public SWUser()
    {
        
    }

    public SWUser(String fName, String lName, String userName, String password, String email) throws InvalidDBStringException
    {
        setFirstName(fName);
        setLastName(lName);
        setUserLoginName(userName);
        setPassword(password);
        setEmail(email);
    }

    public void setUserLoginName(String name) throws InvalidDBStringException
    {
        if (Query.isValidNameString(name))
            m_userLoginName = name.toUpperCase();
        else
            throw new InvalidDBStringException(name);
    }

    public void setEmail(String email)
    {
        m_email = email;
    }
    
    public void setFirstName(String name) throws InvalidDBStringException
    {
        if (Query.isValidNameString(name))
            m_firstName = name;
        else
            throw new InvalidDBStringException(name);
    }
    
    public void setLastName(String name) throws InvalidDBStringException
    {
        if (Query.isValidNameString(name))
            m_lastName = name;
        else
            throw new InvalidDBStringException(name);
    }
    
    public void setPassword(String password) throws InvalidDBStringException
    {
        if (Query.isValidNameString(password))
            m_password = password;
        else
            throw new InvalidDBStringException(password);
    }

    public void setUserID(int i)
    {
        m_userID = i;
    }

    public int getUserID()
    {
        return m_userID;
    }

    public void setAccessToken(SWToken token)
    {
        m_accessToken = token;
    }

    public String getEmail()
    {
        return m_email;
    }

    public String getFirstName()
    {
        return m_firstName;
    }

    public String getLastName()
    {
        return m_lastName;
    }

    public String getPassword()
    {
        return m_password;
    }

    public SWToken getAccessToken()
    {
        return m_accessToken;
    }

    public String getUserLoginName()
    {
        return m_userLoginName;
    }

}
