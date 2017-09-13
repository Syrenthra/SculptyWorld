package sw.socialNetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import java.util.Hashtable;

import sw.lifeform.NPC;
import sw.quest.QuestState;
import sw.quest.SocialCapitolCost;
import sw.socialNetwork.simulation.EventTypes;

public class SocialNetwork
{
    /**
     * The NPC who's social network this is.
     */
    NPC m_myNPC;

    /**
     * Other SocialNPCs that this SocialNPC has relationships with.
     */
    protected ArrayList<NPC> friends;

    public static final int MAX_FRIENDS = 10;

    /**
     * How this SocialNPC feels about each of its friends.
     */
    protected Hashtable<NPC, Feelings> relationships;

    /**
     * How much social capital this SocialNPC currently has.
     */
    protected int currentCapital;

    /**
     * What mood this NPC is currently in.
     */
    protected Moods currentMood;

    /**
     * This Hashtable contains the FriendRequests sent out by this SocialNPC, organized by who they were sent to
     */
    protected Hashtable<NPC, FriendRequest> frReqList;

    /**
     * This ArrayList keeps track of the order that elements were added to frReqList
     */
    protected ArrayList<NPC> frReqListOrder;

    /**
     * This Hashtable contains the FriendRequests that other SocialNPCs have submitted to this one, organized by who sent the request
     */
    protected Hashtable<NPC, FriendRequest> frResponseList;

    /**
     * This ArrayList keeps track of the order that elements were added to frResponseList
     */
    protected ArrayList<NPC> frResponseListOrder;

    /**
     * The personality of a SNPC is what makes it unique from other SNPCs in what it decides to do.
     */
    protected sw.socialNetwork.Personality personality;

    /**
     * Only a broker can make friends with SNPCs that are not in the same room as it is
     */
    protected boolean isBrokerNode;

    public SocialNetwork(NPC npc, double control, double grumpiness, double personability, int desiredFriends, int desiredCapital)
    {
        m_myNPC = npc;
        personality = new Personality(control, grumpiness, personability, desiredFriends, desiredCapital);
        init();
    }
    
    public SocialNetwork(NPC npc, Personality p)
    {
        m_myNPC = npc;
        personality = p;
        init();
    }

    /**
     * This SNPC accepts the given FriendRequest
     * 
     * @param req The FriendRequest to accept
     */
    public void acceptFriendRequest(FriendRequest req)
    {
        if (frResponseList.contains(req))
        {
            req.accept();
        }
    }

    /**
     * Creates a new relationship with the designated SocialNPC.
     * 
     * @param newFriend The SocialNPC to become friends with
     */
    public void addFriend(NPC newFriend)
    {
        addFriend(newFriend, new Feelings());
    }

    /**
     * Creates a new relationship with the designated SocialNPC. Total number of friends cannot
     * exceed MAX_FRIENDS.
     * 
     * @param newFriend The SocialNPC to become friends with
     */
    public void addFriend(NPC newFriend, Feelings feelings)
    {
        if (!friends.contains(newFriend) && friends.size() < MAX_FRIENDS && !newFriend.equals(this))
        {
            friends.add(newFriend);
            relationships.put(newFriend, feelings);
        }
    }

    /**
     * A SocialNPC has a base 15% chance to adopt the same mood as the majority of its friends.
     */
    public void changeMoodPropagation()
    {
        Random rand = new Random();
        int numDifferentFriends = 0;
        for (int i = 0; i < friends.size(); i++)
        {
            if (friends.get(i).getCurrentMood() != currentMood)
            {
                numDifferentFriends++;
            }
        }

        //majority of friends must have a different mood in order for this NPC to change moods
        if (numDifferentFriends > friends.size() / 2)
        {
            int num = rand.nextInt(100);

            if (num <= 15)
            {
                if (currentMood == Moods.HAPPY)
                    currentMood = Moods.ANGRY;
                else
                    currentMood = Moods.HAPPY;
            }
        }
    }

    /**
     * Removes entries from the given FriendRequestList that that have status REJECTED.
     * WAITING requests are not removed because they still need to be evaluated.
     * ACCEPTED requests are not removed because the SocialNPC still needs their information
     * to create GiftQuests later when it has enough social capital.
     */
    public void cleanFrReqList(FriendRequestLists list)
    {
        FriendRequest current;
        NPC snpc = null;
        ArrayList<FriendRequest> garbageRequests = new ArrayList<FriendRequest>();
        ArrayList<NPC> garbageSNPCs = new ArrayList<NPC>();
        Collection<FriendRequest> theList = null;
        ArrayList<NPC> listOrder = null;

        if (list == FriendRequestLists.REQUEST_LIST)
        {
            theList = frReqList.values();
            listOrder = frReqListOrder;
        }
        else if (list == FriendRequestLists.RESPONSE_LIST)
        {
            theList = frResponseList.values();
            listOrder = frResponseListOrder;
        }

        Iterator<FriendRequest> itr = theList.iterator();
        while (itr.hasNext())
        {
            current = (FriendRequest) itr.next();

            if (current.getState().equals(FriendRequestStatus.REJECTED))
            {
                garbageRequests.add(current);

                if (list == FriendRequestLists.REQUEST_LIST)
                {
                    //the key I use to ID this FR is the FR's requestee
                    snpc = current.getRequestee();
                }
                else if (list == FriendRequestLists.RESPONSE_LIST)
                {
                    //the key I use to ID this FR is the FR's requester
                    snpc = current.getRequester();
                }

                garbageSNPCs.add(snpc);
            }
        }

        //remove unneeded entries from the list
        theList.removeAll(garbageRequests);
        listOrder.removeAll(garbageSNPCs);
    }

    /**
     * Allows a SocialNPC to decide how much social capital it is willing to spend to repay a
     * favor to another SocialNPC. This is a modified version of decideDifficulty() from
     * QuestGerator. This version takes the debt owed by this SocialNPC into consideration
     * when picking the difficulty. The easiest quest the SocialNPC can afford that
     * will completely resolve the debt is chosen.
     * 
     * @param requester The SocialNPC who wants the favor done
     * @return true if this SocialNPC agrees to perform the favor, false if it does not
     */
    public SocialCapitolCost evalFavorRequest(NPC requester)
    {
        //
        //      if(requester == null)
        //      {
        //          System.err.println("No requester!");
        //      }else if(!relationships.containsKey(requester))
        //      {
        //          
        //          System.err.println("I don't have a relationship with the requester!");
        //      }
        //      
        //      System.out.println("    " + relationships.get(requester));

        int debt = relationships.get(requester).getSocialDebtOwed();
        SocialCapitolCost difficulty = null;
        SocialCapitolCost max = SocialCapitolCost.EXTREME;
        SocialCapitolCost hard = SocialCapitolCost.EXPENSIVE;
        SocialCapitolCost med = SocialCapitolCost.MEDIUM;
        SocialCapitolCost easy = SocialCapitolCost.CHEAP;

        if (debt > hard.getCost() && currentCapital >= max.getCost())
        {
            difficulty = max;
        }
        else if (debt > med.getCost() && currentCapital >= hard.getCost())
        {
            difficulty = SocialCapitolCost.EXPENSIVE;
        }
        else if (debt >= easy.getCost() && currentCapital >= med.getCost())
        {
            difficulty = SocialCapitolCost.MEDIUM;
        }
        else if (currentCapital >= easy.getCost())
        {
            difficulty = SocialCapitolCost.CHEAP;
        }

        return difficulty;
    }

    /**
     * Evaluates the FriendRequests that were sent to me.
     * 
     * @param potentialNewFriends The number of FriendRequests sent by this SocialNPC that have been
     * accepted by others
     */
    public int evalFriendRequests(int potentialNewFriends)
    {
        int numAccepted = 0;
        Random rand = new Random();

        if (frResponseList.size() == frResponseListOrder.size())
        {
            FriendRequest request = null;
            NPC current = null;
            boolean enoughFriends = (potentialNewFriends + 1 + friends.size() > getTotalDesiredFriends());
            double num;

            Iterator<NPC> itr = frResponseListOrder.iterator();
            while (itr.hasNext())
            {
                current = itr.next();
                request = frResponseList.get(current);
                num = rand.nextDouble();

                //checks for gift category compatibility
                double modifier=checkCategories(m_myNPC.getCategory(),current.getCategory());
                
                //if I want more friends and I pass a personability check...
                //the check is affected by the NPC gift categories
                if (!enoughFriends && num <= (modifier*getPersonability()))
                {
                    //accept this request
                    acceptFriendRequest(request);
                    numAccepted++;
                    potentialNewFriends++;
                }
                else
                {
                    //I don't want any more friends
                    rejectFriendRequest(request);
                }

                enoughFriends = (potentialNewFriends + 1 + friends.size() > getTotalDesiredFriends());
            }
        }

        return numAccepted;
    }

    /**
     * TODO: Figure out how to limit and maybe name categories to make less abstract than just #s
     * 
     * Determines the compatibility of the NPCs based on their gift categories
     * @param me the NPC's category
     * @param target The target's category
     * @return the modifier value
     */
    public double checkCategories(int me, int target) 
    {  	
    	//If the target has a -1, their category was not set, treated as neutral
    	if(target==-1)
    	{
    		return 1.0;
    	}
		/*Category modifier assignments can be changed
    	* Currently, same category will be liked, 1 higher will be neutral, 1 lower disliked
    	* where the numbers will be wrapped using modulus
    	*/
    	int catNum=GiftCategories.getCategoryNum();
    	int like =me;
    	int neutral=(me+1)%catNum;
    	int dislike=(me-1)%catNum;
    	//If the dislike value goes below 0, this wraps it to the higher category number
    	if(dislike<0)
    		dislike+=catNum;

    	if(target==like)
    	{
    		return 1.5;
    	}

    	if(target==neutral)
    	{
    		return 1.0;
    	}

    	if(target==dislike)
    	{
    		return 0.5;
    	}
    	
    	return 1.0;
	}

	/**
     * Find which FriendRequests that I sent out were accepted.
     * 
     * @return A list of SocialNPCs that accepted FriendRequests from me. The list will be <= my
     * totalDesiredFriend
     */
    public ArrayList<NPC> findAcceptedRequests()
    {
        ArrayList<NPC> targets = new ArrayList<NPC>();
        FriendRequest request = null;
        NPC key = null;
        boolean enoughFriends;

        Iterator<NPC> itr = frReqListOrder.iterator();
        while (itr.hasNext())
        {
            key = (NPC) itr.next();
            request = frReqList.get(key);
            enoughFriends = (targets.size() + 1 + friends.size() > getTotalDesiredFriends());

            //if this request was accepted by the other SocialNPC and I want more friends...
            if (request.getState() == FriendRequestStatus.ACCEPTED && !enoughFriends)
            {
                //add this SocialNPC to the list to make a quest for
                targets.add(request.getRequestee());
            }
        }

        return targets;
    }

    public double getControl()
    {
        return personality.getControl();
    }

    public int getCurrentCapital()
    {
        return currentCapital;
    }

    public Moods getCurrentMood()
    {
        return currentMood;
    }

    public ArrayList<NPC> getFriends()
    {
        return friends;
    }

    /**
     * @return The list of which requests were sent to which SocialNPCs
     */
    public Hashtable<NPC, FriendRequest> getFrReqList()
    {
        return frReqList;
    }

    public ArrayList<NPC> getFrReqListOrder()
    {
        return frReqListOrder;
    }

    /**
     * @return The list of which SocialNPCs sent me which FriendRequest
     */
    public Hashtable<NPC, FriendRequest> getFrResponseList()
    {
        return frResponseList;
    }

    public ArrayList<NPC> getFrResponseListOrder()
    {
        return frResponseListOrder;
    }

    public double getGrumpiness()
    {
        return personality.getGrumpiness();
    }

    public boolean getIsBrokerNode()
    {
        return isBrokerNode;
    }

    public double getPersonability()
    {
        return personality.getPersonability();
    }

    public Hashtable<NPC, Feelings> getRelationships()
    {
        return relationships;
    }

    public int getTotalDesiredCapital()
    {
        return personality.getTotalDesiredCapital();
    }

    public int getTotalDesiredFriends()
    {
        return personality.getTotalDesiredFriends();
    }

    /**
     * @param friend The SocialNPC to check for
     * @return true if this SocialNPC is friends with the given SocialNPC
     */
    public boolean hasFriend(NPC friend)
    {
        boolean result = false;

        if (friends.contains(friend) && friends.size() > 0)
        {
            result = true;
        }

        return result;
    }

    /**
     * This method finds the relationships that this SocialNPC would benefit most from by
     * strengthening and returns a list in descending order based on the benefits from growing the
     * relationship. Trust is strengthened first because higher trust leads to slower intimacy
     * decay, and intimacy scales multiplicitavely with trust.
     * 
     * @return A list of SocialNPCs that this SocialNPC should make quests for ordered in descending
     * order based on how much this SocialNPC would benefit from improving the relationship
     */
    public ArrayList<NPC> identifyGrowingRelationships(int turnsUntilRemoval)
    {
        ArrayList<NPC> orderedList = new ArrayList<NPC>();
        Feelings relationship;
        Feelings atIndex;
        NPC current;
        int index = 0;
        boolean rightSpot = false;

        //ProductiveRelationships has all the relationships that are not in danger of being
        //terminated and are not decaying overly fast
        ArrayList<NPC> productiveRelationships = new ArrayList<NPC>(friends);
        productiveRelationships.removeAll(identifyUnproductiveFriendships(turnsUntilRemoval));
        productiveRelationships.removeAll(identifyLowTrustFriends());

        Iterator<NPC> itr = productiveRelationships.iterator();
        while (itr.hasNext())
        {
            current = itr.next();
            relationship = relationships.get(current);

            //Creating a quest for a relationship in which the intimacy is above 80 would be a waste
            while (relationship.getIntimacy() > Feelings.getMaxIntimacy() - 20 && itr.hasNext())
            {
                current = itr.next();
                relationship = relationships.get(current);
            }

            while (orderedList.size() > index && !rightSpot)
            {
                /**
                 * Sort first based on trust. Highest trust that can be improved (trust < 5) in
                 * front. If trust is equal, higher intimacy comes first. Trust == 5 comes next.
                 * Sort those based on intimacy.
                 */
                atIndex = relationships.get(orderedList.get(index));

                if (relationship.getTrust() < Feelings.getMaxTrust() && relationship.getTrust() > atIndex.getTrust())
                {
                    rightSpot = true;
                }
                else if (relationship.getTrust() == atIndex.getTrust() && relationship.getIntimacy() > atIndex.getIntimacy())
                {
                    rightSpot = true;
                }
                else
                {
                    index++;
                }
            }

            orderedList.add(index, current);
            index = 0;
            rightSpot = false;
        }

        return orderedList;
    }

    /**
     * This method identifies relationships that are in the "danger zone" for being
     * terminated due to low intimacy. A relationship is in the danger zone if its
     * intimacy is less than 20. The list is in ascending order (lowest intimacy
     * first).
     * 
     * @return A list of friends that are close to being terminated due to low intimacy
     */
    public ArrayList<NPC> identifyLowIntimacyFriends()
    {
        ArrayList<NPC> list = new ArrayList<NPC>();
        NPC npc;
        Feelings relationship;
        int index = 0;

        for (int i = 0; i < friends.size(); i++)
        {
            npc = friends.get(i);
            relationship = relationships.get(npc);

            if (relationship.getIntimacy() < 20)
            {
                /**
                 * start at beginning of list
                 * compare intimacy of relationship at list[index] to current relationship
                 * if current is greater, index++
                 */
                while (list.size() > index && relationships.get(list.get(index)).getIntimacy() < relationship.getIntimacy())
                {
                    index++;
                }

                list.add(index, npc);
            }
        }

        return list;
    }

    /**
     * This method identifies all the friendships which have a negative trust value.
     * 
     * @return All the friends that this SocialNPC has a negative trust value associated with. The
     * list is sorted in ascending order.
     */
    public ArrayList<NPC> identifyLowTrustFriends()
    {
        ArrayList<NPC> list = new ArrayList<NPC>();
        NPC npc;
        Feelings relationship;
        int index = 0;

        for (int i = 0; i < friends.size(); i++)
        {
            npc = friends.get(i);
            relationship = relationships.get(npc);

            if (relationship.getTrust() < 0)
            {
                /**
                 * start at beginning of list
                 * compare intimacy of relationship at list[index] to current relationship
                 * if current is greater, index++
                 */
                while (list.size() > index && relationships.get(list.get(index)).getTrust() < relationship.getTrust())
                {
                    index++;
                }

                list.add(index, npc);
            }
        }

        return list;
    }

    /**
     * This method identifies the relationships that would decay to minimum intimacy in the
     * specified number of turns.
     * 
     * @param turnsUntilMinIntimacy The number of turns to project out to
     * @return List of SocialNPCs with who are not contributing enough to this SocialNPC's social
     * capital
     */
    public ArrayList<NPC> identifyUnproductiveFriendships(int turnsUntilMinIntimacy)
    {
        ArrayList<NPC> list = new ArrayList<NPC>();
        NPC current = null;
        int curIntimacy;
        int oneTurnLoss;
        int index = 0;
        int atIndexIntimacy;
        int atIndexOneTurnLoss;
        int turnsUntilDecay;
        int atIndexTurnsUntilDecay;

        for (int i = 0; i < friends.size(); i++)
        {
            current = friends.get(i);
            curIntimacy = relationships.get(current).getIntimacy();
            oneTurnLoss = relationships.get(current).calcDecay();
            turnsUntilDecay = 0;
            atIndexTurnsUntilDecay = 0;

            if (curIntimacy - (oneTurnLoss * turnsUntilMinIntimacy) <= Feelings.getMinIntimacy())
            {
                if (list.size() > index)
                {
                    atIndexIntimacy = relationships.get(list.get(index)).getIntimacy();
                    atIndexOneTurnLoss = relationships.get(list.get(index)).calcDecay();

                    //int tempCurInt = curIntimacy;
                    while (curIntimacy > Feelings.getMinIntimacy())
                    {
                        curIntimacy -= oneTurnLoss;
                        turnsUntilDecay++;
                    }

                    while (atIndexIntimacy > Feelings.getMinIntimacy())
                    {
                        atIndexIntimacy -= atIndexOneTurnLoss;
                        atIndexTurnsUntilDecay++;
                    }
                }

                //faster decay in the front of the list
                while (list.size() > index && turnsUntilDecay > atIndexTurnsUntilDecay)
                {
                    index++;
                }

                list.add(index, current);
            }
        }

        return list;
    }

    private void init()
    {
        friends = new ArrayList<NPC>();
        relationships = new Hashtable<NPC, Feelings>();

        currentCapital = 0;
        currentMood = Moods.HAPPY;

        frReqList = new Hashtable<NPC, FriendRequest>();
        frResponseList = new Hashtable<NPC, FriendRequest>();
        frReqListOrder = new ArrayList<NPC>();
        frResponseListOrder = new ArrayList<NPC>();

        isBrokerNode = false;
    }

    /**
     * Steps to initiate a new friendship:
     * -create a FriendRequest and send it to the target
     * -target evaluates and responds
     * -if the request is accepted
     * --create gift quest
     * --add quest to list of available quests
     * --quest is accepted by a player
     * 
     * 
     * This is called when I do not have as many friends as I want. In order to reach my desired
     * number of friends, I will...
     * 
     * First, see if any FriendRequests that I sent out previously have been accepted.
     * Second, decide whether or not to accept FriendRequests sent to me.
     * Third, send out new FriendRequests.
     */
    public ArrayList<NPC> pickNewFriends()
    {

        //find everyone who accepted a FriendRequest from me
        ArrayList<NPC> targets = findAcceptedRequests();

        //now that I know how many others have accepted my requests, I know how many requests I can accept
        int incomingFriends = targets.size();

        //find out how many GiftQuests I sent out to make new friends
        incomingFriends += m_myNPC.findInProgressFriendships();

        int numAccepted = evalFriendRequests(incomingFriends);

        //Find how many requests I accepted
        //incomingFriends += findAcceptedRequests().size();
        incomingFriends += numAccepted;

        //if I still don't have enough potential new friends, send out some requests
        sendFriendRequests(incomingFriends);

        return targets;
    }

    /**
     * This method selects a the target when sending FriendRequests. The two must not 
     * already be friends. Normal SNPCs may only target others that are in the same room.
     * Broker SNPCs may target others that are in different rooms.
     * 
     * @return null if this SocialNPC does not know what room it's in;
     * otherwise, the SocialNPC to try and make friends with
     */
    public ArrayList<NPC> filterNewFriendshipList(ArrayList<NPC> neighbors)
    {

        NPC current;
        //candidates stores the valid targets to try and make friends with
        ArrayList<NPC> candidates = new ArrayList<NPC>();

        //potentialFriends stores the SocialNPCs that this one is already trying to make friends with
        ArrayList<NPC> potentialFriends = new ArrayList<NPC>();

        //add the SocialNPCs that this one is already trying to make friends with into the list
        potentialFriends.addAll(frReqList.keySet());

        //add the SocialNPCs that are already trying to make friends with this one into the list
        potentialFriends.addAll(frResponseList.keySet());

        //pick a valid SocialNPC as the target
        for (int i = 0; i < neighbors.size(); i++)
        {

            current = neighbors.get(i);

            if (potentialFriends.contains(current) || hasFriend(current) || m_myNPC.equals(current))
            {
                /**
                 * the target must not be a friend of this SocialNPC and
                 * the two must not already be trying to make friends and
                 * the target cannot be this SocialNPC
                 */
            }
            else
            {
                candidates.add(current);
            }

        }

        return candidates;
    }

    /**
     * Adds the given FriendRequest to the list for a response later.
     * 
     * @param request The request to be added
     */
    public void receiveFriendRequest(NPC target, FriendRequest request)
    {
        frResponseList.put(target, request);
        frResponseListOrder.add(target);

    }

    /**
     * This SNPC rejects the given FriendRequest and creates the appropriate SocialNetworkEvent
     * 
     * @param req The FriendRequest to accept
     */
    public void rejectFriendRequest(FriendRequest req)
    {
        if (frResponseList.contains(req))
        {
            req.reject();

        }
    }

    /**
     * Terminates a relationship with the designated SocialNPC. Also removes any quests that this
     * SNPC made which target the designated SNPC.
     * 
     * @param npc The SocialNPC with whom this SocialNPC will no longer have a relationship
     */
    public void removeFriend(NPC npc)
    {
        if (friends.contains(npc))
        {
            friends.remove(npc);
            relationships.remove(npc);
            
            //remove related SocialQuests
            m_myNPC.cleanQuests();      
            
            //remove favor-related info - belongs to NPC
            while(m_myNPC.getFavorRequests().contains(npc))
            {
                m_myNPC.getFavorRequests().remove(npc);
            }
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
        if (frReqList.containsKey(target) && frReqList.get(target).getState() == FriendRequestStatus.ACCEPTED)
        {
            frReqList.remove(target);
            frReqListOrder.remove(target);
        }

        if (frResponseList.containsKey(target) && frResponseList.get(target).getState() == FriendRequestStatus.ACCEPTED)
        {
            frResponseList.remove(target);
            frResponseListOrder.remove(target);
        }
    }

    /**
     * Terminates the given list of relationships.
     * 
     * @param toBeRemoved The list of SocialNPCs to be
     */
    public void removeFriendSet(ArrayList<NPC> toBeRemoved)
    {
        if (toBeRemoved != null)
        {
            NPC current;
            for (int i = 0; i < toBeRemoved.size(); i++)
            {
                current = toBeRemoved.get(i);
                removeFriend(current);
                current.removeFriend(m_myNPC);
            }
        }
    }

    /**
     * Creates a FriendRequest and sends it to the target SocialNPC.
     * 
     * @param target The SocialNPC to which a new FriendRequest will be sent
     */
    public void sendFriendRequest(NPC target)
    {
        FriendRequest req = new FriendRequest(m_myNPC, target);
        frReqList.put(target, req);
        frReqListOrder.add(target);

        target.receiveFriendRequest(m_myNPC,req);
        m_myNPC.newEvent(target, EventTypes.FRIEND_REQUEST_SENT);
        target.newEvent(m_myNPC, EventTypes.FRIEND_REQUEST_RECIEVED);
    }

    /**
     * Sends out FriendRequests until this SocialNPC has enough potential new friends to fulfill
     * it's totalDesiredFriends requirement.
     * 
     * @param potentialNewFriends The sum of ACCEPTED FriendRequests from both frReqList and
     * frResponseList
     */
    public void sendFriendRequests(int potentialNewFriends)
    {
        boolean enoughFriends = (potentialNewFriends + 1 + friends.size() > getTotalDesiredFriends());

        while (!enoughFriends)
        {
            //keep sending out FriendRequests until I would have enough friends or there are no
            //more valid targets for me to send requests to
            NPC target = m_myNPC.pickNewFriendshipTarget();

            if (!(target == null))
            {
                sendFriendRequest(target);
                potentialNewFriends++;
                enoughFriends = (potentialNewFriends + 1 + friends.size() > getTotalDesiredFriends());

            }
            else
            {
                //no more valid targets, so I'm done even if I don't have as many friends as I want
                enoughFriends = true;
            }
        }
    }

    public void setControl(double control)
    {
        personality.setControl(control);
    }

    public void setCurrentCapital(int currentCapital)
    {
        this.currentCapital = currentCapital;
    }

    public void setCurrentMood(Moods currentMood)
    {
        this.currentMood = currentMood;
    }

    public void setGrumpiness(double grumpiness)
    {
        personality.setGrumpiness(grumpiness);
    }

    public void setIsBrokerNode(boolean state)
    {
        isBrokerNode = state;
    }

    public void setPersonability(double personability)
    {
        personality.setPersonability(personability);
    }

    public void setTotalDesiredCapital(int totalDesiredCapital)
    {
        personality.setTotalDesiredCapital(totalDesiredCapital);
    }

    public void setTotalDesiredFriends(int totalDesiredFriends)
    {
        personality.setTotalDesiredFriends(totalDesiredFriends);
    }

    /**
     * This method recalculates the total amount of social capital that this SocialNPC has and
     * updates the currentCapital.
     */
    public void updateCapital()
    {
        int sum = 0;
        int relationshipSocialWorth;
        NPC current;
        Iterator<NPC> itr = friends.iterator();
        while (itr.hasNext())
        {
            current = itr.next();
            relationshipSocialWorth = relationships.get(current).calculateSocialWorth();
            sum += relationshipSocialWorth * getControl();
        }
        currentCapital += sum;
        m_myNPC.newEvent(null,EventTypes.CAPITAL_CHANGED, sum);
    }

    /**
     * A NPC has a chance to become angry if a Quest it issued was failed. The chance is
     * based on the grumpiness of the NPC. The NPC can also become happy if a
     * lQuest it issued was successfully completed. A NPC's chance to become happy is the
     * inverse of its chance to become angry.
     */
    public void updateMood(QuestState result)
    {
        Random rand = new Random();
        double num = rand.nextDouble();

        if (result == QuestState.COMPLETED && currentMood == Moods.ANGRY)
        {
            if (num <= 1 - getGrumpiness())
            {
                currentMood = Moods.HAPPY;
                m_myNPC.newEvent(null, EventTypes.MOOD_CHANGE_TO_HAPPY);
            }
        }
        else if (result == QuestState.FAILED && currentMood == Moods.HAPPY)
        {
            if (num <= getGrumpiness())
            {
                currentMood = Moods.ANGRY;
                m_myNPC.newEvent(null, EventTypes.MOOD_CHANGE_TO_ANGRY);
            }
        }
    }

}
