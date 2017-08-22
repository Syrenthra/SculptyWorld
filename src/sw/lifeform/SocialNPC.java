package sw.lifeform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import sw.environment.Room;
import sw.item.Item;
import sw.quest.FavorQuest;
import sw.quest.GiftQuest;
import sw.quest.HomewreckerQuest;
import sw.quest.RequestFavorQuest;
import sw.quest.SocialQuest;
import sw.quest.SocialQuestDifficulty;
import sw.socialNetwork.*;
import sw.socialNetwork.simulation.EventTypes;
import sw.socialNetwork.simulation.Simulation;
import sw.socialNetwork.simulation.SocialNetworkEvent;

/**
 * @author David Abrams
 * 
 * The purpose of this class is to provide a type of NPC that can interact with each other in a
 * social manner. Each SocialNPC will form a node in the social network. They can make form
 * relationships with one another, and they create quests for the players.
 * 
 * 
 * 
 * TODO:
 * -Need to find a way to remove completed/failed quests from a SNPC for the purpose of the
 * simulation.
 * Best way is probably have the quest removed from the SNPC when it is turned in, and have the sim
 * turn in each quest when it is finished.
 * 
 * -Might be a good idea to encapsulate personability, control, grumpiness, totalDesiredFriends
 * and totalDesiredCapital into a separate "personality" class
 */
public class SocialNPC extends NPC
{

	//Other SocialNPCs that this SocialNPC has relationships with.
	protected ArrayList<SocialNPC> friends;
	public static final int MAX_FRIENDS = 10;

	//How this SocialNPC feels about each of its friends.
	protected Hashtable<SocialNPC, Feelings> relationships;

	//The quests that this SocialNPC is currently offering to players.
	protected ArrayList<SocialQuest> availableQuests;

	//The items that this SocialNPC has a particular liking for.
	protected ArrayList<Item> favoriteItems;

	//Allows a SocialNPC to create quests.
	protected QuestGenerator questGenerator;

	//How much social capital this SocialNPC currently has.
	protected int currentCapital;

	//What mood this NPC is currently in.
	protected Moods currentMood;

	//Keeps track of the result of the most recently completed SocialQuest
	protected SocialQuestState lastQuestResult;

	//Used to help when making decisions
	protected Random rand;

	//This Hashtable contains the FriendRequests sent out by this SocialNPC, organized by who they were sent to
	protected Hashtable<SocialNPC, FriendRequest> frReqList;

	//This ArrayList keeps track of the order that elements were added to frReqList
	protected ArrayList<SocialNPC> frReqListOrder;

	//This Hashtable contains the FriendRequests that other SocialNPCs have submitted to this one, organized by who sent the request
	protected Hashtable<SocialNPC, FriendRequest> frResponseList;

	//This ArrayList keeps track of the order that elements were added to frResponseList
	protected ArrayList<SocialNPC> frResponseListOrder;

	//This list contains the SNPCs who have asked this SNPC to perform a favor for them.
	protected ArrayList<SocialNPC> favorRequests;

	//The personality of a SNPC is what makes it unique from other SNPCs in what it decides to do.
	protected sw.socialNetwork.Personality personality;

	//The SNPC holds everything it does during its turn and then sends all the events to the simulation
	protected ArrayList<SocialNetworkEvent> events;

	//The SNPC needs to be able to tell the simulation when events are happening
	protected Simulation sim;
	
	//Only a broker can make friends with SNPCs that are not in the same room as it is
	protected boolean isBrokerNode;

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
	public SocialNPC(int id, String name, String desc, int life, int damage, int armor, int speed)
	{
		super(id, name, desc, life, damage, armor, speed);

		double control = 0.0;
		double grumpiness = 0.5;
		double personability = 0.5;
		int desiredFriends = 0;
		int desiredCapital = 0;

		this.personality = new Personality(control, grumpiness, personability, desiredFriends, desiredCapital);

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
	public SocialNPC(int id, String name, String desc, int life, int damage, int armor, int speed, sw.socialNetwork.Personality pers)
	{
		super(id, name, desc, life, damage, armor, speed);

		this.personality = pers;

		init();
	}

	private void init()
	{
		friends = new ArrayList<SocialNPC>();
		relationships = new Hashtable<SocialNPC, Feelings>();
		availableQuests = new ArrayList<SocialQuest>();
		favoriteItems = new ArrayList<Item>();
		questGenerator = QuestGenerator.getInstance();
		currentCapital = 0;
		currentMood = Moods.HAPPY;
		lastQuestResult = null;
		rand = new Random();
		frReqList = new Hashtable<SocialNPC, FriendRequest>();
		frResponseList = new Hashtable<SocialNPC, FriendRequest>();
		frReqListOrder = new ArrayList<SocialNPC>();
		frResponseListOrder = new ArrayList<SocialNPC>();
		favorRequests = new ArrayList<SocialNPC>();
		events = new ArrayList<SocialNetworkEvent>();
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
	 * --player must be successful in order for the friendship to be created
	 * 
	 * 
	 * This is called when I do not have as many friends as I want. In order to reach my desired
	 * number of friends, I will...
	 * 
	 * First, see if any FriendRequests that I sent out previously have been accepted.
	 * Second, decide whether or not to accept FriendRequests sent to me.
	 * Third, send out new FriendRequests.
	 */
	public ArrayList<SocialNPC> pickNewFriends()
	{

		//find everyone who accepted a FriendRequest from me
		ArrayList<SocialNPC> targets = findAcceptedRequests();
		
		//now that I know how many others have accepted my requests, I know how many requests I can accept
		int incomingFriends = targets.size();
		
		//find out how many GiftQuests I sent out to make new friends
		incomingFriends += findInProgressFriendships();



		int numAccepted = evalFriendRequests(incomingFriends);

		//Find how many requests I accepted
		//incomingFriends += findAcceptedRequests().size();
		incomingFriends += numAccepted;

		//if I still don't have enough potential new friends, send out some requests
		sendFriendRequests(incomingFriends);

		return targets;
	}

	/**
	 * Find which FriendRequests that I sent out were accepted.
	 * 
	 * @return A list of SocialNPCs that accepted FriendRequests from me. The list will be <= my
	 * totalDesiredFriend
	 */
	public ArrayList<SocialNPC> findAcceptedRequests()
	{
		ArrayList<SocialNPC> targets = new ArrayList<SocialNPC>();
		FriendRequest request = null;
		SocialNPC key = null;
		boolean enoughFriends;

		Iterator<SocialNPC> itr = frReqListOrder.iterator();
		while (itr.hasNext())
		{
			key = (SocialNPC) itr.next();
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
	
	
	/**
	 * This method finds out how many GiftQuests are currently active that were made to
	 * form new friendships.
	 * @return Number of relationship-forming GiftQuests currently active
	 */
	public int findInProgressFriendships()
	{
		int count = 0;
		
		for(SocialQuest cur : availableQuests)
		{
			if(cur instanceof GiftQuest && !friends.contains(cur.getQuestTarget()))
			{
				count++;
			}
		}
		return count;
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
		
		if (frResponseList.size() == frResponseListOrder.size())
		{
			FriendRequest request = null;
			SocialNPC current = null;
			boolean enoughFriends = (potentialNewFriends + 1 + friends.size() > getTotalDesiredFriends());
			double num;

			Iterator<SocialNPC> itr = frResponseListOrder.iterator();
			while (itr.hasNext())
			{
				current = itr.next();
				request = frResponseList.get(current);
				num = rand.nextDouble();

				//if I want more friends and I pass a personability check...
				if (!enoughFriends && num <= getPersonability())
				{
					//accept this request
					//request.accept();
					acceptFriendRequest(request);
					numAccepted++;
					potentialNewFriends++;
				} else
				{
					//I don't want any more friends
					//request.reject();
					rejectFriendRequest(request);
				}

				enoughFriends = (potentialNewFriends + 1 + friends.size() > getTotalDesiredFriends());
			}
		}
		
		return numAccepted;
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
			SocialNPC target = pickNewFriendshipTarget();

			if (!(target == null))
			{
				sendFriendRequest(target);
				potentialNewFriends++;
				enoughFriends = (potentialNewFriends + 1 + friends.size() > getTotalDesiredFriends());

			} else
			{
				//no more valid targets, so I'm done even if I don't have as many friends as I want
				enoughFriends = true;
			}
		}
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
	 * Creates a FriendRequest and sends it to the target SocialNPC. Also creates a
	 * SocialNetworkEvent.
	 * 
	 * @param target The SocialNPC to which a new FriendRequest will be sent
	 */
	public void sendFriendRequest(SocialNPC target)
	{
		FriendRequest req = new FriendRequest(this, target);
		frReqList.put(target, req);
		frReqListOrder.add(target);

		newEvent(target, EventTypes.FRIEND_REQUEST_SENT);

		target.receiveFriendRequest(req);
	}

	/**
	 * Adds the given FriendRequest to the list for a response later. Also creates a
	 * SocialNetworkEvent
	 * 
	 * @param request The request to be added
	 */
	public void receiveFriendRequest(FriendRequest request)
	{
		frResponseList.put(request.getRequester(), request);
		frResponseListOrder.add(request.getRequester());

		newEvent(request.getRequester(), EventTypes.FRIEND_REQUEST_RECIEVED);
	}

	/**
	 * Removes any FriendRequests that are from or to the specified SocialNPC. A FriendRequest can
	 * only be removed in this way if the request is ACCEPTED.
	 * 
	 * @param target The SocialNPC whose requests will be removed
	 */
	public void removeFriendRequest(SocialNPC target)
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
	 * Removes entries from the given FriendRequestList that that have status REJECTED.
	 * WAITING requests are not removed because they still need to be evaluated.
	 * ACCEPTED requests are not removed because the SocialNPC still needs their information
	 * to create GiftQuests later when it has enough social capital.
	 */
	public void cleanFrReqList(FriendRequestLists list)
	{
		FriendRequest current;
		SocialNPC snpc = null;
		ArrayList<FriendRequest> garbageRequests = new ArrayList<FriendRequest>();
		ArrayList<SocialNPC> garbageSNPCs = new ArrayList<SocialNPC>();
		Collection<FriendRequest> theList = null;
		ArrayList<SocialNPC> listOrder = null;

		if (list == FriendRequestLists.REQUEST_LIST)
		{
			theList = frReqList.values();
			listOrder = frReqListOrder;
		} else if (list == FriendRequestLists.RESPONSE_LIST)
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
				} else if (list == FriendRequestLists.RESPONSE_LIST)
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
	 * This method selects a the target when sending FriendRequests. The two must not 
	 * already be friends. Normal SNPCs may only target others that are in the same room.
	 * Broker SNPCs may target others that are in different rooms.
	 * 
	 * @return null if this SocialNPC does not know what room it's in;
	 * otherwise, the SocialNPC to try and make friends with
	 */
	public SocialNPC pickNewFriendshipTarget()
	{
		SocialNPC target = null;

		if (m_currentRoom == null)
		{
			System.err.println(m_name + " tried to make a new friend, but didn't know what room to look in!");
		} else
		{
			//candidates stores the valid targets to try and make friends with
			ArrayList<SocialNPC> candidates = new ArrayList<SocialNPC>();
			//potentialFriends stores the SocialNPCs that this one is already trying to make friends with
			ArrayList<SocialNPC> potentialFriends = new ArrayList<SocialNPC>();

			/**
			 * neighbors stores the SocialNPCs that are inside the maximum allowable distance,
			 * relative to this SocialNPC, of forming a friendship. Currently, neighbors is
			 * set to allow broker SNPCs to select friends from their current room and also
			 * from rooms adjacent to their current room. Normal SNPCs maybe only select friends
			 * from their current room.
			 */
			ArrayList<NPC> neighbors = new ArrayList<NPC>();
			ArrayList<Room> roomsList = new ArrayList<Room>();
			Room currentRoom;
			NPC[] npcsInCurrent;

			roomsList.add(m_currentRoom);

			if(isBrokerNode)
			{
				//I am a broker, so I should select friends from this room and all adjacent
				//rooms also
				Room[] adjacentRooms = m_currentRoom.getExitDestinations();
				for(int i = 0; i < adjacentRooms.length; i++)
				{
					roomsList.add(adjacentRooms[i]);
				}
			}

			Iterator<Room> itr = roomsList.iterator();
			while(itr.hasNext())
			{
				currentRoom = itr.next();
				npcsInCurrent = currentRoom.getNPCs();
				for(int i = 0; i < npcsInCurrent.length; i++)
				{
					neighbors.add(npcsInCurrent[i]);
				}
			}
			
			
			SocialNPC current = null;

			//add the SocialNPCs that this one is already trying to make friends with into the list
			potentialFriends.addAll(frReqList.keySet());

			//add the SocialNPCs that are already trying to make friends with this one into the list
			potentialFriends.addAll(frResponseList.keySet());

			//pick a valid SocialNPC as the target
			for (int i = 0; i < neighbors.size(); i++)
			{
				//the target must be a SocialNPC
				if (neighbors.get(i) instanceof SocialNPC)
				{
					current = (SocialNPC) neighbors.get(i);

					if (potentialFriends.contains(current) || friends.contains(current) || this.equals(current))
					{
						/**
						 * the target must not be a friend of this SocialNPC and
						 * the two must not already be trying to make friends and
						 * the target cannot be this SocialNPC
						 */
					} else
					{
						candidates.add(current);
					}
				}
			}

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
	 * This method recalculates the total amount of social capital that this SocialNPC has and
	 * updates the currentCapital.
	 */
	public void updateCapital()
	{
		int sum = 0;
		int relationshipSocialWorth;
		SocialNPC current;
		Iterator<SocialNPC> itr = friends.iterator();
		while (itr.hasNext())
		{
			current = itr.next();
			relationshipSocialWorth = relationships.get(current).calculateSocialWorth();
			sum += relationshipSocialWorth * getControl();
		}
		currentCapital += sum;
		newEvent(null,EventTypes.CAPITAL_CHANGED, sum);
	}

	/**
	 * Asks the designated SocialNPC to perform a favor for this SocialNPC.
	 * 
	 * @param target SocialNPC to request the favor from
	 */
	public void askFavor(SocialNPC target)
	{
		target.requestFavor(this);
	}

	/**
	 * Adds the favor request to a list for this SNPC to process during its turn.
	 * 
	 * @param requester The SociaNPC asking this SocialNPC to perform a favor
	 */
	public void requestFavor(SocialNPC requester)
	{
		if (relationships.containsKey(requester) && relationships.get(requester).getSocialDebtOwed() > 0)
		{
			favorRequests.add(requester);
		}
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
	public SocialQuestDifficulty evalFavorRequest(SocialNPC requester)
	{
//
//		if(requester == null)
//		{
//			System.err.println("No requester!");
//		}else if(!relationships.containsKey(requester))
//		{
//			
//			System.err.println("I don't have a relationship with the requester!");
//		}
//		
//		System.out.println("	" + relationships.get(requester));
		
		int debt = relationships.get(requester).getSocialDebtOwed();
		SocialQuestDifficulty difficulty = null;
		SocialQuestDifficulty max = SocialQuestDifficulty.YOUMUSTBEPRO;
		SocialQuestDifficulty hard = SocialQuestDifficulty.HARD;
		SocialQuestDifficulty med = SocialQuestDifficulty.MEDIUM;
		SocialQuestDifficulty easy = SocialQuestDifficulty.EASY;

		if (debt > hard.getDifficulty() && currentCapital >= max.getDifficulty())
		{
			difficulty = max;
		} else if (debt > med.getDifficulty() && currentCapital >= hard.getDifficulty())
		{
			difficulty = SocialQuestDifficulty.HARD;
		} else if (debt >= easy.getDifficulty() && currentCapital >= med.getDifficulty())
		{
			difficulty = SocialQuestDifficulty.MEDIUM;
		} else if (currentCapital >= easy.getDifficulty())
		{
			difficulty = SocialQuestDifficulty.EASY;
		}

		return difficulty;
	}

	/**
	 * This method allows the definition of assignQuest from NPC to play nicely with the definition
	 * in SocialNPC
	 * 
	 * @param player The player to whom the SocialQuest should be given.
	 * @param quest The index of the SocialQuest to assign to the player in this SocialNPCs list of
	 * available quests.
	 */
	@Override
	public void assignQuest(Player player, int quest)
	{
		if (quest >= 0 && quest < availableQuests.size())
		{
			assignQuest(player, availableQuests.get(quest));
		}
	}

	/**
	 * Allows the SocialNPC to give a quest to a player. The quest must be one that the SocialNPC is
	 * offering.
	 * 
	 * @param player The player to whom the SocialQuest should be given.
	 * @param quest The SocialQuest to assign to the Player
	 */
	public void assignQuest(Player player, SocialQuest quest)
	{
		if (availableQuests.contains(quest) && quest.getTimeToHoldRemaining() != 0)
		{
			player.addQuest(quest);
			quest.addPlayer(player);
		}
	}

	/**
	 * This method handles everything that this SocialNPC should do when a SocialQuest that it gave
	 * out is turned back in by a Player.
	 * 
	 * @param quest The quest that has been finished
	 */
	public void turnInQuest(SocialQuest quest)
	{
		if (availableQuests.contains(quest) && quest.getCurrentState() != SocialQuestState.IN_PROGRESS)
		{
			availableQuests.remove(quest);

			lastQuestResult = quest.getCurrentState();
		}
	}

	/**
	 * Pays the cost in social capital for creating the given quest.
	 * 
	 * @param quest The quest being paid for
	 */
	private void payForQuest(SocialQuest quest)
	{
		payForQuest(quest.getDifficulty());
	}

	/**
	 * Pays to create a quest.
	 * 
	 * @param difficulty The difficulty of the quest
	 */
	private void payForQuest(SocialQuestDifficulty difficulty)
	{
		if (currentCapital < difficulty.getDifficulty())
		{
			//This condition should never be met
			System.err.println(m_name + " created a quest, but couldn't pay for it!");
		} else
		{
			currentCapital -= difficulty.getDifficulty();
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
	 * Order of things:
	 * -update current social capital
	 * -update mood (quest completion has priority over mood propagation)
	 * -respond to friend requests(?)
	 * -decide what social actions to take this turn
	 */
	@Override
	public void updateTime(String name, int time)
	{
		/**
		 * This variable is used later to decide what the cutoff is for identifying relationships
		 * that are in danger of being terminated soon. A relationship is terminated when its
		 * intimacy decays to the minimum. This value is how many turns away from being removed
		 * the SocialNPC considers a relationship to be "in danger."
		 */
		int turnsUntilRelationshipDecays = 10;

		int minCapitalForQuest = SocialQuestDifficulty.EASY.getDifficulty();

		/**
		 * These actions occur at the beginning of every turn:
		 * -update quest timers
		 * -update relationship timers
		 * -update current amount of social capital
		 * -change moods (if necessary)
		 * -remove unproductive friendships
		 * -remove last turn's events
		 */

		//update quests
		Iterator<SocialQuest> questItr = availableQuests.iterator();
		while (questItr.hasNext())
		{
			questItr.next().updateTime(name, time);
		}

		//update relationships
		Iterator<SocialNPC> relationshipsItr = friends.iterator();
		while (relationshipsItr.hasNext())
		{
			relationships.get(relationshipsItr.next()).updateTime(name, time);
		}

		updateCapital();

		//mood change
		if (lastQuestResult != null)
		{
			changeMoodQuest();
		} else
		{
			changeMoodPropagation();
		}

		//remove friendships in which the intimacy has decayed to minimum
		if (friends.size() > 0)
		{
			SocialNPC currentSNPC;
			int index = 0;
			while (index < friends.size())
			{
				currentSNPC = friends.get(index);
				if (relationships.get(currentSNPC).getIntimacy() == Feelings.getMinIntimacy())
				{
					unFriend(currentSNPC);
					currentSNPC.unFriend(this);
					
					newEvent(currentSNPC, EventTypes.FRIENDSHIP_TERMINATED);
				}else
				{
					index++;
				}
			}
		}

		//remove events that have been ready by the Simulation
		if (events.size() > 0)
		{
			ArrayList<SocialNetworkEvent> eventsToBeRemoved = new ArrayList<SocialNetworkEvent>();
			for (SocialNetworkEvent current : events)
			{
				if (current.getRead())
				{
					eventsToBeRemoved.add(current);
				}
			}
			events.removeAll(eventsToBeRemoved);
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
		if (num < getGrumpiness() && currentMood.equals(Moods.ANGRY))
		{
			//I am angry, so I will only make HomewreckerQuests
			if(currentCapital >= minCapitalForQuest && friends.size() > 0)
			{
				//[HomewreckerQuest]
				HomewreckerQuest homewrecker = makeHomewreckerQuest();
				if (homewrecker != null)
				{
					payForQuest(homewrecker);
					addQuest(homewrecker);
				}
			}
		} else
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
			if (friends.size() < getTotalDesiredFriends())
			{
				//I don't have enough friends, so pick some new ones...
				ArrayList<SocialNPC> friendshipTargets = pickNewFriends();

				//...and make quests for them
				int index = 0;
				GiftQuest giftquest;
				while (index < friendshipTargets.size() && currentCapital >= minCapitalForQuest)
				{
					giftquest = questGenerator.genGiftQuest(this, friendshipTargets.get(index));
					availableQuests.add(giftquest);
					payForQuest(giftquest);

					//since the quest was successfully created, remove the corresponding FriendRequest
					//from both my sent list and the targets received list
					removeFriendRequest(friendshipTargets.get(index));
					friendshipTargets.get(index).removeFriendRequest(this);

					index++;
				}

				/**
				 * Note: if this SocialNPC doesn't have enough social capital to create all the
				 * quests when it wants to, the FriendRequests will be left in the RequestList in
				 * their ACCEPTED state for later processing when the SocialNPC has enough social
				 * capital to make the quests.
				 */
			} else
			{
				//I have all the friends I want, so reject all friend requests.
				Iterator<SocialNPC> SNPCitr = frResponseListOrder.iterator();
				while (SNPCitr.hasNext())
				{
					frResponseList.get(SNPCitr.next()).reject();
				}
			}


			//check for relationships in danger of being terminated due to intimacy decay
			if (friends.size() > 0)
			{
				//low intimacy friends
				ArrayList<SocialNPC> lowIntimacy = identifyUnproductiveFriendships(turnsUntilRelationshipDecays);
				Iterator<SocialNPC> itr = lowIntimacy.iterator();
				FavorQuest favquest;
				while (itr.hasNext() && currentCapital >= minCapitalForQuest)
				{
					//I need to improve the intimacy of these relationships before they are terminated! [FavorQuest]
					favquest = questGenerator.genFavorQuest(this, itr.next());
					payForQuest(favquest);
					addQuest(favquest);
					
				}

				//low trust friends
				ArrayList<SocialNPC> lowTrust = identifyLowTrustFriends();
				itr = lowTrust.iterator();
				while (itr.hasNext())
				{
					askFavor(itr.next());
				}
			}
			
			
			//perform favors that I agreed to
			RequestFavorQuest reqFavQuest;
			ArrayList<SocialNPC> requestsPerformed = new ArrayList<SocialNPC>();
			int i = 0;
			while (i < favorRequests.size() && currentCapital >= minCapitalForQuest)
			{

				reqFavQuest = questGenerator.genReqFavQuest(this, favorRequests.get(i), evalFavorRequest(favorRequests.get(i)));
				addQuest(reqFavQuest);
				payForQuest(reqFavQuest);

				//I performed this favor, so remove the request from the list
				requestsPerformed.add(favorRequests.get(i));
				i++;
			}
			favorRequests.removeAll(requestsPerformed);
			

			//I don't have as much social capital as I want, so I should strengthen an existing friendship
			if (currentCapital < getTotalDesiredCapital() && friends.size() > 0 && currentCapital >= minCapitalForQuest)
			{
				//strengthen beneficial relationships
				ArrayList<SocialNPC> toStrengthen = identifyGrowingRelationships(turnsUntilRelationshipDecays);
				Iterator<SocialNPC> itr = toStrengthen.iterator();
				SocialNPC current;
				SocialQuest quest;
				while (itr.hasNext())
				{
					current = itr.next();
					if (relationships.get(current).getTrust() == Feelings.getMaxTrust() && currentCapital >= minCapitalForQuest)
					{
						//I want to improve the intimacy of this relationship [FavorQuest]
						quest = questGenerator.genFavorQuest(this, current);
						payForQuest(quest);
						addQuest(quest);
					} else
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

		cleanFrReqList(FriendRequestLists.REQUEST_LIST);
		cleanFrReqList(FriendRequestLists.RESPONSE_LIST);
		
		//remove expired quests
		if (availableQuests.size() > 0)
		{
			cleanQuests();
		}
	}

	/**
	 * This method allows the SocialNPC to pick which relationship to target when it wants to make a
	 * HomerweckerQuest. The SNPC picks the target relationship by choosing a relationship that one
	 * of its friends is involved in. This SNPC may not be the other participant in the
	 * relationship.
	 * 
	 * @return A Homewrecker quest
	 */
	public HomewreckerQuest makeHomewreckerQuest()
	{
		HomewreckerQuest quest = null;

		boolean finished = false;
		ArrayList<SocialNPC> list = new ArrayList<SocialNPC>(friends);
		int num;
		int index;
		SocialNPC intermediateSNPC = null;
		ArrayList<Feelings> targetRelationship = new ArrayList<Feelings>();

		if (list.size() != 0)
		{
			while (!finished)
			{
				//pick a random friend
				num = rand.nextInt(list.size());
				intermediateSNPC = list.get(num);

				//make sure that friend is friends with more than just this SocialNPC
				if (intermediateSNPC.getFriends().size() <= 1)
				{
					//if it's not, then remove it from this list
					list.remove(num);
				} else
				{
					//if it is, then grab one of its other friends as the target				
					index = rand.nextInt(intermediateSNPC.getFriends().size());
					SocialNPC target = intermediateSNPC.getFriends().get(index);

					while (target.equals(this))
					{
						//this SocialNPC cannot be part of the target relationship
						index = rand.nextInt(intermediateSNPC.getFriends().size());
						target = intermediateSNPC.getFriends().get(index);
					}

					targetRelationship.add(intermediateSNPC.getRelationships().get(target));
					targetRelationship.add(target.getRelationships().get(intermediateSNPC));
					finished = true;
				}

				if (list.size() == 0)
				{
					//quest can't be generated
					return null;
				}
			}

			quest = questGenerator.genHomewreckerQuest(this, intermediateSNPC, targetRelationship);
			newEvent(intermediateSNPC, EventTypes.QUEST_CREATED_HOMEWRECKER, quest.getDifficulty().getDifficulty());
		} else
		{
			//can't make the quest because I have no friends
		}

		return quest;
	}

	/**
	 * A SocialNPC has a base 15% chance to adopt the same mood as the majority of its friends.
	 */
	public void changeMoodPropagation()
	{
		int numDifferentFriends = 0;
		for (int i = 0; i < friends.size(); i++)
		{
			if (friends.get(i).getCurrentMood() != currentMood)
			{
				numDifferentFriends++;
			}
		}

		//majority of friends must have a different mood in order for this SocialNPC to change moods
		if (numDifferentFriends > friends.size() / 2)
		{
			int num = rand.nextInt(100);

			if (num <= 15)
			{
				moodSwap();
			}
		}
	}

	/**
	 * A SocialNPC has a chance to become angry if a SocialQuest it issued was failed. The chance is
	 * based on the grumpiness of the SocialNPC. The SocialNPC can also become happy if a
	 * SocialQuest it issued was successfully completed. A SocialNPC's chance to become happy is the
	 * inverse of its chance to become angry.
	 */
	public void changeMoodQuest()
	{
		double num = rand.nextDouble();

		if (lastQuestResult == SocialQuestState.SUCCESS && currentMood == Moods.ANGRY)
		{
			if (num <= 1 - getGrumpiness())
			{
				moodSwap();
			}
		} else if (lastQuestResult == SocialQuestState.FAILURE && currentMood == Moods.HAPPY)
		{
			if (num <= getGrumpiness())
			{
				moodSwap();
			}
		}

		/**
		 * mood change from a quest should only occur once per quest, so the last result shouldn't
		 * be remembered
		 */
		lastQuestResult = null;
	}

	/**
	 * Changes the mood of the SocialNPC.
	 */
	private void moodSwap()
	{
		if (currentMood == Moods.HAPPY)
		{
			currentMood = Moods.ANGRY;
			newEvent(null, EventTypes.MOOD_CHANGE_TO_ANGRY);
		} else
		{
			currentMood = Moods.HAPPY;
			newEvent(null, EventTypes.MOOD_CHANGE_TO_HAPPY);
		}
	}

	/**
	 * This method identifies the relationships that would decay to minimum intimacy in the
	 * specified number of turns.
	 * 
	 * @param turnsUntilMinIntimacy The number of turns to project out to
	 * @return List of SocialNPCs with who are not contributing enough to this SocialNPC's social
	 * capital
	 */
	public ArrayList<SocialNPC> identifyUnproductiveFriendships(int turnsUntilMinIntimacy)
	{
		ArrayList<SocialNPC> list = new ArrayList<SocialNPC>();
		SocialNPC current = null;
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

	/**
	 * This method identifies relationships that are in the "danger zone" for being
	 * terminated due to low intimacy. A relationship is in the danger zone if its
	 * intimacy is less than 20. The list is in ascending order (lowest intimacy
	 * first).
	 * 
	 * @return A list of friends that are close to being terminated due to low intimacy
	 */
	public ArrayList<SocialNPC> identifyLowIntimacyFriends()
	{
		ArrayList<SocialNPC> list = new ArrayList<SocialNPC>();
		SocialNPC npc;
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
	public ArrayList<SocialNPC> identifyLowTrustFriends()
	{
		ArrayList<SocialNPC> list = new ArrayList<SocialNPC>();
		SocialNPC npc;
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
	 * This method finds the relationships that this SocialNPC would benefit most from by
	 * strengthening and returns a list in descending order based on the benefits from growing the
	 * relationship. Trust is strengthened first because higher trust leads to slower intimacy
	 * decay, and intimacy scales multiplicitavely with trust.
	 * 
	 * @return A list of SocialNPCs that this SocialNPC should make quests for ordered in descending
	 * order based on how much this SocialNPC would benefit from improving the relationship
	 */
	public ArrayList<SocialNPC> identifyGrowingRelationships(int turnsUntilRemoval)
	{
		ArrayList<SocialNPC> orderedList = new ArrayList<SocialNPC>();
		Feelings relationship;
		Feelings atIndex;
		SocialNPC current;
		int index = 0;
		boolean rightSpot = false;

		//ProductiveRelationships has all the relationships that are not in danger of being
		//terminated and are not decaying overly fast
		ArrayList<SocialNPC> productiveRelationships = (ArrayList<SocialNPC>) friends.clone();
		productiveRelationships.removeAll(identifyUnproductiveFriendships(turnsUntilRemoval));
		productiveRelationships.removeAll(identifyLowTrustFriends());

		Iterator<SocialNPC> itr = productiveRelationships.iterator();
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
				} else if (relationship.getTrust() == atIndex.getTrust() && relationship.getIntimacy() > atIndex.getIntimacy())
				{
					rightSpot = true;
				} else
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

	@Override
	public String toString()
	{
		return m_name;
	}

	public QuestGenerator getQuestGenerator()
	{
		return questGenerator;
	}

	public double getPersonability()
	{
		return personality.getPersonability();
	}

	public void setPersonability(double personability)
	{
		personality.setPersonability(personability);
	}

	public int getTotalDesiredFriends()
	{
		return personality.getTotalDesiredFriends();
	}

	public void setTotalDesiredFriends(int totalDesiredFriends)
	{
		personality.setTotalDesiredFriends(totalDesiredFriends);
	}

	public int getTotalDesiredCapital()
	{
		return personality.getTotalDesiredCapital();
	}

	public void setTotalDesiredCapital(int totalDesiredCapital)
	{
		personality.setTotalDesiredCapital(totalDesiredCapital);
	}

	public int getCurrentCapital()
	{
		return currentCapital;
	}

	public void setCurrentCapital(int currentCapital)
	{
		this.currentCapital = currentCapital;
	}

	public double getControl()
	{
		return personality.getControl();
	}

	public void setControl(double control)
	{
		personality.setControl(control);
	}

	public Moods getCurrentMood()
	{
		return currentMood;
	}

	public void setCurrentMood(Moods currentMood)
	{
		this.currentMood = currentMood;
	}

	public double getGrumpiness()
	{
		return personality.getGrumpiness();
	}

	public void setGrumpiness(double grumpiness)
	{
		personality.setGrumpiness(grumpiness);
	}

	public ArrayList<SocialNPC> getFriends()
	{
		return friends;
	}

	/**
	 * Creates a new relationship with the designated SocialNPC. Total number of friends cannot
	 * exceed 8.
	 * 
	 * @param newFriend The SocialNPC to become friends with
	 */
	public void addFriend(SocialNPC newFriend, Feelings feelings)
	{
		if (!friends.contains(newFriend) && friends.size() < MAX_FRIENDS && !newFriend.equals(this))
		{
			friends.add(newFriend);
			relationships.put(newFriend, feelings);
		}
	}

	/**
	 * Creates a new relationship with the designated SocialNPC.
	 * 
	 * @param newFriend The SocialNPC to become friends with
	 */
	public void addFriend(SocialNPC newFriend)
	{
		addFriend(newFriend, new Feelings());
	}

	/**
	 * Terminates a relationship with the designated SocialNPC. Also removes any quests that this
	 * SNPC made which target the designated SNPC.
	 * 
	 * @param npc The SocialNPC with whom this SocialNPC will no longer have a relationship
	 */
	public void removeFriend(SocialNPC npc)
	{
		if (friends.contains(npc))
		{
			friends.remove(npc);
			relationships.remove(npc);
		}
	}
	
	
	/**
	 * Terminates the friendship between this SNPC and the target.
	 * @param snpc
	 */
	public void unFriend(SocialNPC target)
	{
		if(friends.contains(target))
		{
			//remove from my friends list
			friends.remove(target);
			relationships.remove(target);
			
			//remove related SocialQuests
			cleanQuests();		
			
			//remove favor-related info
			while(favorRequests.contains(target))
			{
				favorRequests.remove(target);
			}
		}
	}

	/**
	 * Terminates the given list of relationships.
	 * 
	 * @param toBeRemoved The list of SocialNPCs to be
	 */
	public void removeFriendSet(ArrayList<SocialNPC> toBeRemoved)
	{
		if (toBeRemoved != null)
		{
			SocialNPC current;
			for (int i = 0; i < toBeRemoved.size(); i++)
			{
				current = toBeRemoved.get(i);
				removeFriend(current);
				current.removeFriend(this);
			}
		}
	}

	/**
	 * @param friend The SocialNPC to check for
	 * @return true if this SocialNPC is friends with the given SocialNPC
	 */
	public boolean hasFriend(SocialNPC friend)
	{
		boolean result = false;

		if (friends.contains(friend) && friends.size() > 0)
		{
			result = true;
		}

		return result;
	}

	public Hashtable<SocialNPC, Feelings> getRelationships()
	{
		return relationships;
	}

	public ArrayList<SocialQuest> getAvailableQuests()
	{
		return availableQuests;
	}

	public void addQuest(SocialQuest quest)
	{
		if (!availableQuests.contains(quest))
		{
			availableQuests.add(quest);
		}
	}

	public void removeQuest(SocialQuest quest)
	{
		if (availableQuests.contains(quest))
		{
			availableQuests.remove(quest);
		}
	}
	
	/**
	 * Removes the specified SocialQuest and refunds the creation cost. This is used when
	 * a SocialQuest has become invalid after creation (ex: target friendship was terminated)
	 * @param quest The SocialQuest to refund
	 */
	public void refundQuest(SocialQuest quest)
	{
		if(availableQuests.contains(quest))
		{
			availableQuests.remove(quest);
			this.currentCapital += quest.getDifficulty().getDifficulty();
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
		Iterator<SocialQuest> questItr = availableQuests.iterator();
		SocialQuest currentQuest;
		ArrayList<SocialQuest> toBeRemoved = new ArrayList<SocialQuest>();
		while (questItr.hasNext())
		{
			currentQuest = questItr.next();

			if (currentQuest.getTimeToCompleteRemaining() == 0 || currentQuest.getTimeToHoldRemaining() == 0)
			{
				toBeRemoved.add(currentQuest);
			}else if(currentQuest instanceof HomewreckerQuest)
			{
				if(((HomewreckerQuest)currentQuest).getTargetRelationship().get(1) == null ||
					 !friends.contains(currentQuest.getQuestTarget()))
				{
					toBeRemoved.add(currentQuest);
				}
			}else if(!friends.contains(currentQuest.getQuestTarget()) &&
					 !(currentQuest instanceof GiftQuest))
			{
				toBeRemoved.add(currentQuest);
			}
		}
		
		availableQuests.removeAll(toBeRemoved);
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

	public SocialQuestState getLastQuestResult()
	{
		return lastQuestResult;
	}

	public void setLastQuestResult(SocialQuestState newValue)
	{
		lastQuestResult = newValue;
	}

	/**
	 * @return The list of which requests were sent to which SocialNPCs
	 */
	public Hashtable<SocialNPC, FriendRequest> getFrReqList()
	{
		return frReqList;
	}

	/**
	 * @return The list of which SocialNPCs sent me which FriendRequest
	 */
	public Hashtable<SocialNPC, FriendRequest> getFrResponseList()
	{
		return frResponseList;
	}

	/**
	 * @return The list of Favors that this SocialNPC has agreed to
	 */
	public ArrayList<SocialNPC> getFavorRequests()
	{
		return favorRequests;
	}

	public ArrayList<SocialNPC> getFrReqListOrder()
	{
		return frReqListOrder;
	}

	public ArrayList<SocialNPC> getFrResponseListOrder()
	{
		return frResponseListOrder;
	}

	public void setSimulation(Simulation sim)
	{
		this.sim = sim;
	}
	
	public void setIsBrokerNode(boolean state)
	{
		isBrokerNode = state;
	}

	public ArrayList<SocialNetworkEvent> getEvents()
	{
		return events;
	}
	
	public boolean getIsBrokerNode()
	{
		return isBrokerNode;
	}

	/**
	 * Creates a new SocialNetworkEvent and adds it to the list.
	 * 
	 * @param target The other SocialNPC involved in this event.
	 * @param type The type of event that occurred
	 */
	public void newEvent(SocialNPC target, EventTypes type)
	{
		SocialNetworkEvent event = new SocialNetworkEvent(this, target, type);
		addEvent(event);
	}
	
	/**
	 * Creates a new SocialNetworkEvent and adds it to the list.
	 * 
	 * @param target The other SocialNPC involved in this event.
	 * @param type The type of event that occurred
	 * @param info Special information that needs to be sent
	 */
	public void newEvent(SocialNPC target, EventTypes type, int info)
	{
		SocialNetworkEvent event = new SocialNetworkEvent(this, target, type, info);
		addEvent(event);
	}
	
	private void addEvent(SocialNetworkEvent event)
	{
		events.add(event);
	}
}