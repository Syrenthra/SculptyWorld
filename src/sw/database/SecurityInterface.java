package sw.database;

import sw.database.obj.SWToken;

public interface SecurityInterface
{
    
    public SWToken getSecurityToken();
    
    public void setSecurityToken(SWToken token);

}
