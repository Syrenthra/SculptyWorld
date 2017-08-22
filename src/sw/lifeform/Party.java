package sw.lifeform;

import java.util.Vector;

import sw.quest.Quest;
import sw.quest.QuestState;

/**
 * Used to organize one or more players as a group.
 * Useful in coordinating quest completion.
 * @author cdgira
 *
 */
public class Party implements PCObserver
{
    public static final int MAX_SIZE = 5;
    /**
     * Should be a small number so no need for a Hashtable.
     */
    protected Vector<PC> m_players = new Vector<PC>();
    /**
     * Should be a small number so no need for a Hashtable.
     */
    protected Vector<PC> m_joinRequests = new Vector<PC>();

    /**
     * This is the constructor.
     *
     * @param player
     */
    public Party(PC player)
    {
        if (player != null)
        {
            m_players.add(player);
            player.addPCObserver(this);
        }
    }


    /**
     * Merges two parties together.  The combined party
     * can't be over 5 members in size.
     * @param player1
     */
    public void mergeParties(Party party)
    {
        if (m_players.size() + party.getSize() <= MAX_SIZE)
        {
            PC originalPartyMemberA = m_players.elementAt(0);
            PC originalPartyMemberB = party.getPlayer(0);
            Vector<Quest> activeQuests = new Vector<Quest>();
            
            // Gather up all the active quests for the party the other party is being merged into.
            for (Quest quest : originalPartyMemberA.getNativeQuests())
            {
                activeQuests.add(quest);
            }
            for (Quest quest : originalPartyMemberA.getInheritedQuests())
            {
                if (quest.getCurrentState(originalPartyMemberA) == QuestState.IN_PROGRESS)
                    activeQuests.add(quest);
            }
            
            // Gather up all the active quests for the party being merged into this party
            for (Quest quest : originalPartyMemberB.getNativeQuests())
            {
                activeQuests.add(quest);
            }
            for (Quest quest : originalPartyMemberB.getInheritedQuests())
            {
                if (quest.getCurrentState(originalPartyMemberB) == QuestState.IN_PROGRESS)
                    activeQuests.add(quest);
            }
            
           // Since we are merging parties we won't need to make any quests inactive.
            
            // Merge all the people into one party.
            while (party.getSize() > 0)
            {
                PC player = party.removePlayer(0);
                player.removePCObserver(party);
                m_players.add(player);
                player.setParty(this);  
                player.addPCObserver(this);
            }
            
            // Update all the quests for all the players 
            for (PC player : m_players)
            {
                for (Quest quest : activeQuests)
                {
                    player.updateQuests(quest);    
                }
            }
            
        }

    }

    /**
     * Removes the player at that index from the party.  Will shift all other party
     * members down one.
     * @param i
     * @return
     */
    private PC removePlayer(int index)
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
    public PC getPlayer(int index)
    {
        PC player = null;
        if (index < m_players.size())
            player = m_players.get(index);
        return player;
    }
    
    /**
     * Returns the players in this party.
     * @return
     */
    public Vector<PC> getPlayers()
    {
        return m_players;
    }

    /**
     * This removes a player from a party without updating any of the quests.
     * @param player
     */
    public void removePlayer(PC player)
    {
        if (m_players.contains(player))
        {
            m_players.remove(player);
            player.removePCObserver(this);
            Party soloParty = new Party(player);
            player.setParty(soloParty);
            player.addPCObserver(soloParty);
        }
        
        // Since player is now solo all of it's inherited quests must be inactive.
        for (Quest quest : player.getInheritedQuests())
        {
            quest.setCurrentState(player, QuestState.INACTIVE);
        }
        
        // Any native quests of the player that just left that no one else has as
        // native quests should cause all related inherited quests of players still
        // in the other party to go to INACTIVE.
        for (Quest quest : player.getNativeQuests())
        {
            boolean inactive = true;
            for (PC member : m_players)
            {
                if (member.getNativeQuests().contains(quest))
                {
                    inactive = false;
                    break;
                }
            }
            if (inactive)
            {
                for (PC member : m_players)
                {
                    quest.setCurrentState(member, QuestState.INACTIVE);
                }
            }
        }
        
        // Now that we know those to make inactive update the needed quests.
        
    }


    /**
     * Will forward PC events to the other PC's in the party.  This will
     * likely be done by creating Proxy events that will be similar but processed differently
     * by the receiving objects.
     * @param event
     */
    @Override
    public void pcUpdate(PCEvent event)
    {
        if (event.getType() == PCEvent.NATIVE_QUEST_ADDED)
        {
            Quest quest = event.getQuest();
            for (PC player : m_players)
            {
                if (player != event.getPC())
                {
                    player.addNativeQuest(quest);
                    quest.addPlayer(player);
                }
            }
        }
        
    }


    /**
     * The leader of the party, should be the person at index 0 of m_players.
     * @return
     */
    public PC getPartyLeader()
    {
        if (m_players.size() == 0)
            return null;
        else
            return m_players.elementAt(0);
    }


    /**
     * This player has made a request to join the party.  This will
     * be checked by the party leader when the join is accepted.
     * @param player
     */
    public void addJoinRequest(PC player)
    {
        if (!m_players.contains(player))
            m_joinRequests.add(player);
    }

    /**
     * Checks to see if this player has made a join request to the party. 
     * @param player
     * @return
     */
    public boolean hasJoinRequest(String player)
    {
        boolean contains = false;
        for (PC requester : m_joinRequests)
        {
            if (requester.getName().equals(player))
            {
                contains = true;
                break;
            }
        }
        return contains;
    }


    /**
     * Removes this player from the list of those that want to join
     * the party.
     * @param player
     */
    public void removeJoinRequest(PC player)
    {
        m_joinRequests.remove(player);
    }


    /**
     * 
     * @param newMemberName
     * @return
     */
    public PC getJoinRequest(String playerName)
    {
        PC player = null;
        for (PC requester : m_joinRequests)
        {
            if (requester.getName().equals(playerName))
            {
                player = requester;
                break;
            }
        }
        return player;
    }

}
