package sw.environment.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestCommandResult
{
    
    @Test
    public void testInitialize()
    {
        CommandResult result = new CommandResult(CommandResult.ALL,"Message 1","Message 2");
        assertEquals(CommandResult.ALL,result.getSource());
        assertEquals("Message 1",result.getMsgForSource());
        assertEquals("Message 2",result.getMsgForOthers());
    }
    
    @Test
    public void testInitialize2()
    {
        CommandResult result = new CommandResult(CommandResult.ALL,CommandResult.NONE, "Message 1","Message 3", "Message 2");
        assertEquals(CommandResult.ALL,result.getSource());
        assertEquals(CommandResult.NONE,result.getTarget());
        assertEquals("Message 1",result.getMsgForSource());
        assertEquals("Message 2",result.getMsgForOthers());
        assertEquals("Message 3",result.getMsgForTarget());
    }

}
