package sw.lifeform;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import sw.environment.Room;
import sw.environment.RoomEventTracker;
import sw.environment.RoomObserver;
import sw.environment.RoomUpdateType;
import sw.environment.TheWorld;
import sw.environment.WorldZone;
import sw.environment.commands.CommandResult;
import sw.environment.commands.SayCommand;
import sw.item.Item;
import sw.quest.Quest;
import sw.quest.QuestGenerator;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.quest.TimedQuest;
import sw.quest.reward.GiftReward;
import sw.quest.reward.HomewreckerReward;
import sw.quest.reward.QuestReward;
import sw.quest.reward.SocialReward;
import sw.socialNetwork.*;

/**
 * @author David Abrams and Dr. Girard
 * 
 * The purpose of this class is to provide a type of NPC that can interact with each other in a
 * social manner. Each SocialNPC will form a node in the social network. They can make form
 * relationships with one another, and they create quests for the players.
 * 
 * 
 * 
 * TODO: So players can't horde quests need to put a limit on how many native quests a player can have at a time.
 * TODO: So players can't troll and grab and not do quests need to have player develop a reputation for completing or not completing quests.
 * TODO: May be best to attach the reputation to the PC's human's account.
 * 
 * 
 */
public class NPC extends Lifeform implements RoomObserver
{
    /**
     * How much damage the creature does in an attack.
     */
    protected int m_damage;

    /**
     * How much armor the creature has to protect itself from damage.
     */
    protected int m_armor;

    /**
     * How often the creature can attack.
     */
    protected int m_speed;

    /**
     * Stores the last active status for a room.  Used to determine if a quest
     * should remain active or not.
     */
    private Hashtable<Room, Boolean> m_roomState = new Hashtable<Room, Boolean>();

    /**
     * The quests that this NPC is currently offering to players.
     */
    protected ArrayList<Quest> m_availableQuests;

    /**
     * The items that this NPC has a particular liking for.
     */
    protected ArrayList<Item> favoriteItems;

    /**
     * The items that this NPC likes to create quests around.
     */
    protected ArrayList<Item> m_questItems;

    /**
     * Items that the NPC has with himself or herself.  
     */
    protected ArrayList<Item> m_personalItems;

    /**
     * Items that the NPC is allowed to accept. For example,
     * items that are to be turned in as part of a quest.
     * <PC ID, Items accepted>
     */
    protected Hashtable<Integer, ArrayList<Item>> m_acceptablePersonalItems;

    /**
     * This list contains the SNPCs who have asked this SNPC to perform a favor for them.
     */
    protected ArrayList<NPC> favorRequests;

    SocialNetwork m_socialNetwork;

    //Keeps track of the result of the most recently completed SocialQuest
    protected QuestState lastQuestResult;

    /**
     * Creates a new SocialNPC with the default Personality.
     * 
     * @param id The unique ID number assigned to this SocialNPC
     * @param name The in-game name of this SocialNPC
     * @param desc A description of this SocialNPC
     * @param life Amount of HP
     * @param damage Damage dealt per melee attack
     * @param armor Amount of armor
     * @param speed How frequently attacks are made
     */
    public NPC(int id, String name, String desc, int life, int damage, int armor, int speed)
    {
        super(id, name, desc, life);

        m_damage = damage;
        m_armor = armor;
        m_speed = speed;

        double control = 0.0;
        double grumpiness = 0.5;
        double personability = 0.5;
        int desiredFriends = 0;
        int desiredCapital = 0;

        m_socialNetwork = new SocialNetwork(this, control, grumpiness, personability, desiredFriends, desiredCapital);

        init();
    }

    /**
     * Allows a predefined Personality to be specified.
     * 
     * @param id The unique ID number assigned to this SocialNPC
     * @param name The in-game name of this SocialNPC
     * @param desc A description of this SocialNPC
     * @param life Amount of HP
     * @param damage Damage dealt per melee attack
     * @param armor Amount of armor
     * @param speed How frequently attacks are made
     * @param pers The Personality for this SNPC to use
     */
    public NPC(int id, String name, String desc, int life, int damage, int armor, int speed, Personality personality)
    {
        super(id, name, desc, life);

        m_damage = damage;
        m_armor = armor;
        m_speed = speed;

        m_socialNetwork = new SocialNetwork(this, personality);

    }

    /**
     * TODO Pull up into the instance variable creation?
     */
    private void init()
    {
        m_availableQuests = new ArrayList<Quest>();
        lastQuestResult = null;
        favorRequests = new ArrayList<NPC>();
        favoriteItems = new ArrayList<Item>();
        m_questItems = new ArrayList<Item>();
        m_personalItems = new ArrayList<Item>();
        m_acceptablePersonalItems = new Hashtable<Integer, ArrayList<Item>>();
    }

    /**
    * Returns how much damage this creature does.
    * @return
    */
    public int getDamage()
    {
        return m_damage;
    }

    /**
     * Returns how much armor the creature has.
     * @return
     */
    public int getArmor()
    {
        return m_armor;
    }

    /**
     * The attack speed of the creature.
     * @return
     */
    public int getSpeed()
    {
        return m_speed;
    }

    /**
     * Creature attacks the other lifeform using it's damage value.
     * @param entity
     */
    @Override
    public void attack(Lifeform entity)
    {
        entity.takeHit(m_damage);

    }

    /**
     * Creature takes damage equal damage done minus armor.
     */
    @Override
    public void takeHit(int damage)
    {
        int damageSustained = damage - m_armor;
        if (damageSustained > 0)
            m_currentLifePoints -= damageSustained;
        if (m_currentLifePoints < 0)
            m_currentLifePoints = 0;
    }

    @Override
    public void takeHeal(int magnitude)
    {
        m_currentLifePoints += magnitude;
        if (m_currentLifePoints > m_maxLifePoints)
            m_currentLifePoints = m_maxLifePoints;
    }

    /**
     * Informs the NPC of an update from the room it is observing. 
     * Most likely an OutsideRoom.
     * 
     * TODO Need to rethink how this will work with the new quest design.
     * TODO How the NPC will respond to being talked to? - First simple if say "quests" will generate a say update with the quest list for only the player that asked.
     */
    @Override
    public void roomUpdate(Room room, Object source, RoomUpdateType type)
    {
        if (type == RoomUpdateType.ASK)
        {
            CommandResult data = (CommandResult)source;
            // Demo Player asks you about quests. <- actual message sent to the NPC
            int count = data.getSource().getName().length() + 16;
            String msg = data.getMsgForTarget().substring(count).trim();
            if (msg.equals("quests"))
            {
                msg = "say to "+data.getSource().getName()+" ";
                for (Quest quest : m_availableQuests)
                {
                    msg = msg + quest.getName();
                }
                SayCommand cmd = new SayCommand();
                cmd.processCommand(this, msg);
            }
        }
        /*      for (QuestTask task : m_quest.getTasks())
              {
                  if ((type == RoomUpdateType.CREATURE_ADDED) || (type == RoomUpdateType.CREATURE_REMOVED))
                  {
                      m_questActive = false;
                      Creature[] creatures = room.getCreatures();
                      Creature questCreature = ((KillCreatureTask) task).getCreature();

                      for (int x = 0; x < room.getNumCreatures(); x++)
                      {
                          Creature roomCreature = creatures[x];

                          if (roomCreature.equals(questCreature))
                          {
                              m_roomState.remove(room);
                              m_roomState.put(room, true);
                              m_questActive = true;
                              break;
                          }
                      }

                      if (!m_questActive)
                      {
                          m_roomState.remove(room);
                          m_roomState.put(room, false);
                          Enumeration<Boolean> values = m_roomState.elements();
                          while (values.hasMoreElements())
                          {
                              boolean roomValue = values.nextElement();
                              if (roomValue)
                              {
                                  m_questActive = true;
                                  break;
                              }
                          }
                      }
                  }
              }*/
    }

    public boolean completQuest(PC player, Quest quest)
    {
        boolean completed = false;
        if (quest.getGranter() == this)
        {
            if (quest.turnInQuest(player))
            {
                // TODO: Not sure what to do with the quest once it is completed as want to have a record of past quests.
                completed = true;
            }
        }
        return completed;
    }

    /**
     * This method finds out how many GiftQuests are currently active that were made to
     * form new friendships.
     * @return Number of relationship-forming GiftQuests currently active
     */
    public int findInProgressFriendships()
    {
        int count = 0;

        for (Quest cur : m_availableQuests)
        {
            for (QuestReward reward : cur.getRewards())
            {
                if (reward instanceof GiftReward)
                {
                    GiftReward gReward = (GiftReward) reward;
                    if (!m_socialNetwork.getFriends().contains(gReward.getTarget()))
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * This method selects a the target when sending FriendRequests. The two must not 
     * already be friends. Normal SNPCs may only target others that are in the same room.
     * Broker SNPCs may target others that are in different rooms.
     * 
     * @return null if this SocialNPC does not know what room it's in;
     * otherwise, the SocialNPC to try and make friends with
     */
    public NPC pickNewFriendshipTarget()
    {
        NPC target = null;

        if (m_currentRoom == null)
        {
            System.err.println(m_name + " tried to make a new friend, but didn't know what room to look in!");
        }
        else
        {

            /**
             * neighbors stores the SocialNPCs that are inside the maximum allowable distance,
             * relative to this SocialNPC, of forming a friendship. Currently, neighbors is
             * set to allow broker SNPCs to select friends from their current room and also
             * from rooms adjacent to their current room. Normal SNPCs maybe only select friends
             * from their current room.
             */
            ArrayList<NPC> neighbors = new ArrayList<NPC>();
            ArrayList<Room> roomsList = new ArrayList<Room>();
            NPC[] npcsInCurrent;
            
            WorldZone myZone = TheWorld.getInstance().getZone(m_currentRoom);
             
            Enumeration<Room> myZoneE = myZone.getRooms().elements();
            while (myZoneE.hasMoreElements())
            {
                Room room = myZoneE.nextElement();
                roomsList.add(room);
            }
            
            //I am a broker, so I should select friends from this room and all adjacent
            //zones also
            if (m_socialNetwork.getIsBrokerNode())
            {               
                for (WorldZone zone : myZone.getNeighboringZones())
                {
                
                    myZoneE = zone.getRooms().elements();
                    while (myZoneE.hasMoreElements())
                    {
                        Room room = myZoneE.nextElement();
                        roomsList.add(room);
                    }
                }
            }

            Iterator<Room> itr = roomsList.iterator();
            while (itr.hasNext())
            {
                Room currentRoom = itr.next();
                npcsInCurrent = currentRoom.getNPCs();
                for (int i = 0; i < npcsInCurrent.length; i++)
                {
                    neighbors.add(npcsInCurrent[i]);
                }
            }

            //candidates stores the valid targets to try and make friends with
            ArrayList<NPC> candidates = m_socialNetwork.filterNewFriendshipList(neighbors);

            Random rand = new Random();
            //pick the target from the list of candidates
            if (candidates.size() > 0)
            {
                int num = rand.nextInt(candidates.size());
                target = candidates.get(num);
            }
        }

        return target;
    }

    /**
     * Asks the designated SocialNPC to perform a favor for this SocialNPC.
     * 
     * @param target SocialNPC to request the favor from
     */
    public void askFavor(NPC target)
    {
        target.requestFavor(this);
    }

    /**
     * Adds the favor request to a list for this SNPC to process during its turn.
     * 
     * TODO: Probably some refactoring to do here.
     * 
     * @param requester The SociaNPC asking this SocialNPC to perform a favor
     */
    public void requestFavor(NPC requester)
    {
        if (m_socialNetwork.getRelationships().containsKey(requester) && m_socialNetwork.getRelationships().get(requester).getSocialDebtOwed() > 0)
        {
            favorRequests.add(requester);
        }
    }

    /**
     * This method allows the definition of assignQuest from NPC to play nicely with the definition
     * in SocialNPC
     * 
     * @param player The player to whom the SocialQuest should be given.
     * @param quest The index of the SocialQuest to assign to the player in this SocialNPCs list of
     * available quests.
     */
    public void assignQuest(PC player, int quest)
    {
        if (quest >= 0 && quest < m_availableQuests.size())
        {
            assignQuest(player, m_availableQuests.get(quest));
        }
    }

    /**
     * Allows the NPC to give a quest to a player. The quest must be one that the SocialNPC is
     * offering.
     * 
     * @param player The player to whom the Quest should be given.
     * @param quest The Quest to assign to the Player
     */
    public void assignQuest(PC player, Quest quest)
    {
        if (m_availableQuests.contains(quest))
        {
            if (quest instanceof TimedQuest)
            {
                TimedQuest timedQuest = (TimedQuest) quest;
                if (timedQuest.getTimeToHoldRemaining() != 0)
                {
                    player.assignNativeQuest(quest);
                }
            }
            else
            {
                player.assignNativeQuest(quest);
            }
        }
    }

    /**
     * This method handles everything that this NPC should do when a Quest that it gave
     * out is turned back in by a Player.
     * 
     * TODO: So not implemented.
     * 
     * @param quest The quest that has been finished
     */
    public void turnInQuest(Quest quest)
    {
        /*if (availableQuests.contains(quest) && quest.getCurrentState() != QuestState.IN_PROGRESS)
        {
            availableQuests.remove(quest);

            lastQuestResult = quest.getCurrentState();
        }*/
    }

    /**
     * Adds the given FriendRequest to the list for a response later.
     * 
     * @param request The request to be added
     */
    public void receiveFriendRequest(NPC target, FriendRequest req)
    {
        m_socialNetwork.receiveFriendRequest(target, req);
    }

    /**
     * Pays the cost in social capital for creating the given quest.
     * 
     * @param quest The quest being paid for
     */
    private void payForQuest(Quest quest)
    {
        for (QuestReward reward : quest.getRewards())
        {
            if (reward instanceof SocialReward)
            {
                SocialCapitolCost cost = ((SocialReward) reward).getCost();
                if (m_socialNetwork.getCurrentCapital() < cost.getCost())
                {
                    //This condition should never be met
                    System.err.println(m_name + " created a quest, but couldn't pay for it!");
                }
                else
                {
                    m_socialNetwork.setCurrentCapital(m_socialNetwork.getCurrentCapital() - cost.getCost());
                }
            }
        }
    }

    /**
     * Thoughts...
     * 
     * Gifts/favors:
     * -max social worth of a relationship is 500
     * -max social capital a SocialNPC can gain per turn is (8*500) = 4000
     * -costs of creating favor/gift quests is in SocialQuestDifficulty
     * 
     * 
     * 
     * updateTime() method:
     * -updates currentCapital at the start/end(?) of each turn
     * -keep track of when quests expire & get rid of expired quests
     * -mood change: propagation from neighbors
     * -mood change: successful/failed quests (priority between this and mood propagation?)
     * -SocialNPC should be able to decide, based on current and desired number of friends and
     * amount of social capital whether to try and make a new friend or terminate an unproductive
     * relationship
     * 
     * 
     * TODO: Many of these actions could be extracted into their own little methods for easier
     * readability.
     * 
     * TODO: Pretty sure a lot of this should be sent over to the SocialNetwork class.
     * 
     * TODO: We need to figure out which clocks each of these will operate on.
     * 
     * Order of things:
     * -update current social capital
     * -update mood (quest completion has priority over mood propagation)
     * -respond to friend requests(?)
     * -decide what social actions to take this turn
     */
    @Override
    public void updateTime(String name, int time)
    {
        Hashtable<NPC, Feelings> relationships = m_socialNetwork.getRelationships();
        ArrayList<NPC> friends = m_socialNetwork.getFriends();
        Random rand = new Random();
        /**
         * This variable is used later to decide what the cutoff is for identifying relationships
         * that are in danger of being terminated soon. A relationship is terminated when its
         * intimacy decays to the minimum. This value is how many turns away from being removed
         * the SocialNPC considers a relationship to be "in danger."
         */
        int turnsUntilRelationshipDecays = 10;

        int minCapitalForQuest = SocialCapitolCost.CHEAP.getCost();

        /**
         * These actions occur at the beginning of every turn:
         * -update current amount of social capital
         * -change moods (if necessary)
         * -remove unproductive friendships
         * -remove last turn's events
         */

        //update relationships - Relationships are not Time Observers, but do have the updateTime method.
        Iterator<NPC> relationshipsItr = friends.iterator();
        while (relationshipsItr.hasNext())
        {
            relationships.get(relationshipsItr.next()).updateTime(name, time);
        }
        
        m_socialNetwork.updateCapital();

        //mood change
        if (lastQuestResult != null)
        {
            updateMood();
        }
        else
        {
            m_socialNetwork.changeMoodPropagation();
        }

        //remove friendships in which the intimacy has decayed to minimum
        if (friends.size() > 0)
        {
            NPC currentSNPC;
            int index = 0;
            while (index < friends.size())
            {
                currentSNPC = friends.get(index);
                if (relationships.get(currentSNPC).getIntimacy() == Feelings.getMinIntimacy())
                {
                    removeFriend(currentSNPC);
                    currentSNPC.removeFriend(this);
                }
                else
                {
                    index++;
                }
            }
        }

        /**
         * Logic flow for social actions...
         * 
         * -do i have enough friends?
         * --yes: good; continue
         * --no : try to make a new friend [GiftQuest]
         * 
         * -are any of my friendships in danger of being terminated?
         * --yes: oh no, quick make a quest to fix it! [FavorQuest or ReqFavQuest]
         * --no: good, continue
         * 
         * -perform favors that I agreed to
         * 
         * -do i have enough social capital stored?
         * --yes: good; do nothing
         * --no: i want more social capital, so i should strengthen an existing relationship
         * ---what relationship(s) do i want to strengthen? (the most productive ones first)
         * ---what kind of quest do i want to make? [FavorQuest or ReqFavQuest]
         */

        //any quest should be replaced with a HomewreckerQuest if the SocialNPC is Angry and passes
        //the grumpiness check
        float num = rand.nextFloat();
        if (num < m_socialNetwork.getGrumpiness() && m_socialNetwork.getCurrentMood().equals(Moods.ANGRY))
        {
            //I am angry, so I will only make HomewreckerQuests
            if (m_socialNetwork.getCurrentCapital() >= minCapitalForQuest && friends.size() > 0)
            {
                //[HomewreckerQuest]
                Quest homewrecker = QuestGenerator.genHomewreckerQuest(this);
                if (homewrecker != null)
                {
                    payForQuest(homewrecker);
                    addQuest(homewrecker);
                }
            }
        }
        else
        {
            /**
             * Priorities for use of social capital:
             * 1)
             * The way to earn the most social capital per turn is to have the maximum allowable
             * number of friends. This means that the first priority for all SNPCs is to form
             * as many friendships as possible as quickly as possible.
             * 
             * 
             * 2)
             * The SNPC should identify relationships that are in the "danger zone" for
             * being terminated. It should try to bolster these relationships first and
             * foremost.
             * 
             * danger zone criteria:
             * -intimacy is low enough that the friendship will be terminated within 10 turns
             * [favorQuest]
             * -trust is negative (decreasing intimacy) [ReqFavQuest]
             * 
             * 
             * 3)
             * The SNPC should perform favors that it agreed to.
             * 
             * 
             * 4)
             * If there is social capital left over, the SNPC should then pick the most
             * beneficial relationships and try to improve those so as to generate more
             * social capital next turn.
             * 
             * most beneficial relationship:
             * -increase trust first (multiplies intimacy & reduces decay)
             * -increase intimacy second
             */

            //Make productive quests instead of a HomewreckerQuest

            //first spend social capital on making new friends
            if (friends.size() < m_socialNetwork.getTotalDesiredFriends())
            {
                //I don't have enough friends, so pick some new ones...
                ArrayList<NPC> friendshipTargets = m_socialNetwork.pickNewFriends();

                //...and make quests for them
                int index = 0;
                Quest giftquest;
                while (index < friendshipTargets.size() && m_socialNetwork.getCurrentCapital() >= minCapitalForQuest)
                {
                    giftquest = QuestGenerator.genGiftQuest(this, friendshipTargets.get(index));
                    m_availableQuests.add(giftquest);
                    payForQuest(giftquest);

                    //since the quest was successfully created, remove the corresponding FriendRequest
                    //from both my sent list and the targets received list
                    m_socialNetwork.removeFriendRequest(friendshipTargets.get(index));
                    friendshipTargets.get(index).removeFriendRequest(this);

                    index++;
                }

                /**
                 * Note: if this SocialNPC doesn't have enough social capital to create all the
                 * quests when it wants to, the FriendRequests will be left in the RequestList in
                 * their ACCEPTED state for later processing when the SocialNPC has enough social
                 * capital to make the quests.
                 */
            }
            else
            {
                //I have all the friends I want, so reject all friend requests.
                Iterator<NPC> SNPCitr = m_socialNetwork.getFrResponseListOrder().iterator();
                while (SNPCitr.hasNext())
                {
                    m_socialNetwork.getFrResponseList().get(SNPCitr.next()).reject();
                }
            }

            //check for relationships in danger of being terminated due to intimacy decay
            if (friends.size() > 0)
            {
                //low intimacy friends
                ArrayList<NPC> lowIntimacy = m_socialNetwork.identifyUnproductiveFriendships(turnsUntilRelationshipDecays);
                Iterator<NPC> itr = lowIntimacy.iterator();
                Quest favquest;
                while (itr.hasNext() && m_socialNetwork.getCurrentCapital() >= minCapitalForQuest)
                {
                    //I need to improve the intimacy of these relationships before they are terminated! [FavorQuest]
                    favquest = QuestGenerator.genFavorQuest(this, itr.next());
                    payForQuest(favquest);
                    addQuest(favquest);

                }

                //low trust friends
                ArrayList<NPC> lowTrust = m_socialNetwork.identifyLowTrustFriends();
                itr = lowTrust.iterator();
                while (itr.hasNext())
                {
                    askFavor(itr.next());
                }
            }

            //perform favors that I agreed to
            Quest reqFavQuest;
            ArrayList<NPC> requestsPerformed = new ArrayList<NPC>();
            int i = 0;
            while (i < favorRequests.size() && m_socialNetwork.getCurrentCapital() >= minCapitalForQuest)
            {

                reqFavQuest = QuestGenerator.genReqFavQuest(this, favorRequests.get(i), m_socialNetwork.evalFavorRequest(favorRequests.get(i)));
                addQuest(reqFavQuest);
                payForQuest(reqFavQuest);

                //I performed this favor, so remove the request from the list
                requestsPerformed.add(favorRequests.get(i));
                i++;
            }
            favorRequests.removeAll(requestsPerformed);

            //I don't have as much social capital as I want, so I should strengthen an existing friendship
            if (m_socialNetwork.getCurrentCapital() < m_socialNetwork.getTotalDesiredCapital() && friends.size() > 0 && m_socialNetwork.getCurrentCapital() >= minCapitalForQuest)
            {
                //strengthen beneficial relationships
                ArrayList<NPC> toStrengthen = m_socialNetwork.identifyGrowingRelationships(turnsUntilRelationshipDecays);
                Iterator<NPC> itr = toStrengthen.iterator();
                NPC current;
                Quest quest;
                while (itr.hasNext())
                {
                    current = itr.next();
                    if (relationships.get(current).getTrust() == Feelings.getMaxTrust() && m_socialNetwork.getCurrentCapital() >= minCapitalForQuest)
                    {
                        //I want to improve the intimacy of this relationship [FavorQuest]
                        quest = QuestGenerator.genFavorQuest(this, current);
                        payForQuest(quest);
                        addQuest(quest);
                    }
                    else
                    {
                        //I want to improve the trust in this relationship [ReqFavQuest]
                        askFavor(current);
                    }
                }
            }
        }

        /**
         * These actions need to be done at the end of every turn:
         * -cleanFrReqList()
         * -remove expired quests
         * -remove quests that target SNPCs I am no longer friends with
         */

        m_socialNetwork.cleanFrReqList(FriendRequestLists.REQUEST_LIST);
        m_socialNetwork.cleanFrReqList(FriendRequestLists.RESPONSE_LIST);

        //remove expired quests
        if (m_availableQuests.size() > 0)
        {
            cleanQuests();
        }
    }

    /**
     * Removes any FriendRequests that are from or to the specified SocialNPC. A FriendRequest can
     * only be removed in this way if the request is ACCEPTED.
     * 
     * @param target The SocialNPC whose requests will be removed
     */
    public void removeFriendRequest(NPC target)
    {
        m_socialNetwork.removeFriendRequest(target);
    }

    /**
     * A NPC has a chance to become angry if a Quest it issued was failed. The chance is
     * based on the grumpiness of the NPC. The NPC can also become happy if a
     * lQuest it issued was successfully completed. A NPC's chance to become happy is the
     * inverse of its chance to become angry.
     * 
     * TODO: This needs work, but leaving functionaly as is till tests get redone.
     */
    public void updateMood()
    {
        m_socialNetwork.updateMood(lastQuestResult);

        /**
         * mood change from a quest should only occur once per quest, so the last result shouldn't
         * be remembered
         */
        lastQuestResult = null;
    }

    @Override
    public String toString()
    {
        return m_name;
    }

    /**
     * Terminates a relationship with the designated SocialNPC. Also removes any quests that this
     * SNPC made which target the designated SNPC.
     * 
     * @param npc The SocialNPC with whom this SocialNPC will no longer have a relationship
     */
    public void removeFriend(NPC npc)
    {
        m_socialNetwork.removeFriend(npc);

        //remove related SocialQuests
        cleanQuests();

        //remove favor-related info
        while (favorRequests.contains(npc))
        {
            favorRequests.remove(npc);
        }
    }

    public ArrayList<Quest> getAvailableQuests()
    {
        return m_availableQuests;
    }

    public void addQuest(Quest quest)
    {
        if (!m_availableQuests.contains(quest))
        {
            m_availableQuests.add(quest);
        }
    }

    public void removeQuest(Quest quest)
    {
        if (m_availableQuests.contains(quest))
        {
            m_availableQuests.remove(quest);
        }
    }

    /**
     * Removes the specified SocialQuest and refunds the creation cost. This is used when
     * a SocialQuest has become invalid after creation (ex: target friendship was terminated)
     * @param quest The SocialQuest to refund
     */
    public void refundQuest(Quest quest)
    {
        if (m_availableQuests.contains(quest))
        {
            m_availableQuests.remove(quest);
            for (QuestReward reward : quest.getRewards())
            {
                if (reward instanceof SocialReward)
                {
                    SocialCapitolCost cost = ((SocialReward) reward).getCost();
                    m_socialNetwork.setCurrentCapital(m_socialNetwork.getCurrentCapital() + cost.getCost());
                }
            }
        }
    }

    /**
     * Removes SocialQuests that meet these criteria:
     * -quest has an expired timer
     * -I am no longer friends with the target
     * -HomewreckerQuests in which the intermediate and target are no longer friends
     */
    public void cleanQuests()
    {
        ArrayList<Quest> toBeRemoved = new ArrayList<Quest>();
        for (Quest currentQuest : m_availableQuests)
        {
            if (currentQuest instanceof TimedQuest)
            {
                TimedQuest timedQuest = (TimedQuest) currentQuest;

                if (timedQuest.getTimeToCompleteRemaining() == 0 || timedQuest.getTimeToHoldRemaining() == 0)
                {
                    toBeRemoved.add(currentQuest);
                }
            }
            for (QuestReward reward : currentQuest.getRewards())
            {
                if (reward instanceof HomewreckerReward)
                {
                    HomewreckerReward hwReward = (HomewreckerReward) reward;
                    if (hwReward.getTargetRelationship().get(1) == null || !m_socialNetwork.getFriends().contains(hwReward.getTarget()))
                    {
                        toBeRemoved.add(currentQuest);
                        // TODO Remove this break statement once we have the tests working.
                        break;
                    }
                }
                else if (reward instanceof SocialReward)
                {
                    SocialReward socialReward = (SocialReward) reward;
                    if (!m_socialNetwork.getFriends().contains(socialReward.getTarget()) && !(socialReward instanceof GiftReward))
                    {
                        toBeRemoved.add(currentQuest);
                        break;
                    }
                }
            }
        }

        m_availableQuests.removeAll(toBeRemoved);
    }

    public ArrayList<Item> getFavoriteItems()
    {
        return favoriteItems;
    }

    public void addFavoriteItem(Item item)
    {
        if (!favoriteItems.contains(item))
        {
            favoriteItems.add(item);
        }
    }

    public void removeFavoriteItem(Item item)
    {
        if (favoriteItems.contains(item))
        {
            favoriteItems.remove(item);
        }
    }

    public QuestState getLastQuestResult()
    {
        return lastQuestResult;
    }

    public void setLastQuestResult(QuestState newValue)
    {
        lastQuestResult = newValue;
    }

    /**
     * @return The list of Favors that this SocialNPC has agreed to
     */
    public ArrayList<NPC> getFavorRequests()
    {
        return favorRequests;
    }

    /**
     * Returns the current mood of the NPC.
     * @return
     */
    public Moods getCurrentMood()
    {
        return m_socialNetwork.getCurrentMood();
    }

    /**
     * Returns the social network that this NPC is apart of.
     * @return
     */
    public SocialNetwork getSocialNetwork()
    {
        return m_socialNetwork;
    }

    public ArrayList<Item> getQuestItems()
    {
        return m_questItems;
    }

    /**
     * Adds an item to the list of items NPC will use as part of quest tasks.
     * @param item
     */
    public void addQuestItem(Item item)
    {
        m_questItems.add(item);

    }

    /**
     * Removes an item to the list of items NPC will use as part of quest tasks.
     * @param index
     */
    public void removeQuestItem(int index)
    {
        m_questItems.remove(index);

    }

    /**
     * Adds an item to the NPC's horde of personal items stored in m_personalItems.
     * A version of the item (e.g. clone) must be in the m_accetablePersonalItems list.
     * @param item
     * @Return Returns true if successful, false otherwise.
     */
    public boolean addPersonalItem(PC player, Item item)
    {
        boolean success = false;

        if (m_acceptablePersonalItems.containsKey(player.getID()))
        {
            ArrayList<Item> items = m_acceptablePersonalItems.get(player.getID());

            for (Item allowableItem : items)
            {
                if (allowableItem.equals(item))
                {
                    m_personalItems.add(item);
                    success = true;
                    break;
                }
            }
        }

        return success;
    }

    /**
     * Gives away the item at that location in the list of the NPC's personal items
     * stored in m_myItems.
     * @param index
     * @return
     */
    public Item givePersonalItem(int index)
    {
        Item item = null;
        if (index < m_personalItems.size())
            item = m_personalItems.remove(index);
        return item;
    }

    /**
     * Returns the personal item stored at that location in m_myItems.
     * @param index
     * @return
     */
    public Item getPersonalItem(int index)
    {
        Item item = null;
        if (index < m_personalItems.size())
            item = m_personalItems.get(index);
        return item;
    }

    /**
     * Adds an item to the list of items the NPC will accept from a player.
     * @param player 
     * @param item
     */
    public void addAcceptablePersonalItem(PC player, Item item)
    {

        if (m_acceptablePersonalItems.containsKey(player.getID()))
        {
            ArrayList<Item> items = m_acceptablePersonalItems.get(player.getID());
            boolean exists = false;

            for (Item inListItem : items)
            {
                if (inListItem.equals(item))
                {
                    exists = true;
                    break;
                }
            }

            if (!exists)
                items.add(item);

            items.add(item);
        }
        else
        {
            ArrayList<Item> items = new ArrayList<Item>();
            items.add(item);
            m_acceptablePersonalItems.put(player.getID(), items);
        }

    }

    /**
     * Removes an item from the list of items the NPC will accept from a player.
     * @param player 
     * @param item
     */
    public void removeAcceptablePersonalItem(PC player, Item item)
    {
        if (m_acceptablePersonalItems.containsKey(player.getID()))
        {
            ArrayList<Item> items = m_acceptablePersonalItems.get(player.getID());
            
            for (int index = 0; index < items.size(); index++)
            {
                if (items.get(index).equals(item))
                {
                    items.remove(index);
                    break;
                }
            }
        }
    }
    
    
    public void setCurrentCapital(int capitol)
    {
    	m_socialNetwork.setCurrentCapital(capitol);
    }

    
}