package sw.net.msg;

public class SWErrorMsg extends SWMessage
    { 
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3618986654732529712L;
    
    public SWErrorMsg (String message)
        {
        super(message);
        }

    public String getError()
        {
        return getMessage();
        }
    }
