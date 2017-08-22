package sw.environment.commands;

import sw.lifeform.Lifeform;
import sw.lifeform.PC;

/**
 * Used to transmit what occurred in a room due to an action by a player.
 * @author Dr. Girard
 *
 */
public class CommandResult
{
    /**
     * Make sure these are not allowed to be names of players in the game.
     */
    public final static PC ALL = new PC(-1,"ALL","ALL PLAYERS",1);
    public final static PC NONE = new PC(-1,"NONE","NO PLAYERS",1);
    
    /**
     * Who (PC/NPC/Creature) that generated this result.
     */
    Lifeform m_source;
    
    /**
     * The target of the command, default is ALL.
     */
    Lifeform m_target = ALL;
    /**
     * What message to send to the source of this result due to actions taken.
     */
    String m_sourceMsg;
    /**
     * What message to send to players that are not the target (if target set to ALL or NONE).
     */
    String m_otherMsg;
    /**
     * If the target is a specific lifeform, then what message to send to that lifeform.
     */
    String m_targetMsg;

    public CommandResult(Lifeform p, String sourceMsg, String otherMsg)
    {
        this(p,null,sourceMsg,"",otherMsg);

    }
    
    public CommandResult(Lifeform source, Lifeform target, String sourceMsg, String targetMsg, String otherMsg)
    {
        m_source = source;
        m_target = target;
        m_sourceMsg = sourceMsg;
        m_otherMsg = otherMsg;
        m_targetMsg = targetMsg;
    }

    /**
     * Returns the name of the player that caused this result.
     * @return
     */
    public Lifeform getSource()
    {
        return m_source;
    }

    public String getMsgForSource()
    {
        return m_sourceMsg;
    }

    public String getMsgForOthers()
    {
        return m_otherMsg;
    }

    /**
     * The specific target of the message.  The default assumption is
     * that all players in the room are the target of the other message.
     * You can also set it to a specific player or set it to NONE for none
     * of the other players.
     * @return Who to send the m_otherMsg to.
     */
    public Lifeform getTarget()
    {
        return m_target;
    }

    /**
     * Returns the messge to be sent to the target of this CommandResult.
     * @return
     */
    public String getMsgForTarget()
    {
        return m_targetMsg;
    }

}
