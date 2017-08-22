package sw.lifeform;

import java.util.Vector;

import sw.quest.CreatureQuest;
import sw.quest.Quest;

/**
 * Used to organize one or more players as a group.
 * Useful in coordinating quest completion.
 * @author cdgira
 *
 */
public class Party
{

    protected int m_id = -1;

    protected Vector<Player> m_players = new Vector<Player>();

    /**
     * This is the constructor.
     * @param id
     * @param player
     */
    public Party(int id, Player player)
    {
        m_id = id;
        m_players.add(player);
        player.setParty(this);
    }

    /**
     * Returns the unique id for this party.
     * @return
     */
    public int getID()
    {
        return m_id;
    }

    /**
     * Merges two parties together.  The combined party
     * can't be over 5 members in size.
     * @param player
     */
    public void mergeParties(Party party)
    {
        if (m_players.size() + party.getSize() < 6)
        {
            while (party.getSize() > 0)
            {
                Player player = party.removePlayer(0);
                m_players.add(player);
                player.setParty(this);
            }
        }

    }

    /**
     * Removes the player at that index from the party.  Will shift all other party
     * members down one.
     * @param i
     * @return
     */
    private Player removePlayer(int index)
    {
        return m_players.remove(index);
    }

    /**
     * How many players are in the party.
     * @return
     */
    private int getSize()
    {
        return m_players.size();
    }

    /**
     * Returns the player at that index location in the party.
     * @param index
     * @return
     */
    public Player getPlayer(int index)
    {
        Player player = null;
        if (index < m_players.size())
            player = m_players.get(index);
        return player;
    }
    
    /**
     * Returns the players in this party.
     * @return
     */
    public Vector<Player> getPlayers()
    {
        return m_players;
    }

    /**
     * Updates the quest status for all party members that have
     * a quest related to this creature.
     * @param deadGuy
     */
    public void killed(Creature deadGuy)
    {
        for (Player player : m_players)
        {
            for (Quest q : player.getQuests())
            {
                if (q instanceof CreatureQuest)
                {
                    CreatureQuest cq = (CreatureQuest) q;
                    if (deadGuy.isSame(cq.getCreature()))
                    {
                        cq.updateGoal(player);
                    }
                }
            }
        }

    }

}
