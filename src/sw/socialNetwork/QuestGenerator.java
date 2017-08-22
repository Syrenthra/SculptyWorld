package sw.socialNetwork;

import java.util.ArrayList;
import java.util.Random;

import sw.environment.Room;
import sw.environment.RoomObserver;
import sw.environment.SWRoomUpdateType;
import sw.item.FavorTarget;
import sw.item.Item;
import sw.lifeform.Player;
import sw.lifeform.SocialNPC;
import sw.quest.FavorQuest;
import sw.quest.GiftQuest;
import sw.quest.HomewreckerQuest;
import sw.quest.RequestFavorQuest;
import sw.quest.SocialQuestDifficulty;
import sw.socialNetwork.simulation.EventTypes;

/**
 * 
 * @author David Abrams
 * 
 * This class is a factory that creates SocialQuests as needed for SocialNPCs. It is a singleton.
 */
public class QuestGenerator implements RoomObserver
{
	private ArrayList<Item> itemsInWorld;
	private static QuestGenerator instance = null;
	private Random rand;
	
	/**
	 * This var is used solely for allowing SocialQuests to function in the Simulator as if there was
	 * a player in the game picking up quests. They will have no purpose once this system is merged
	 * into the game.
	 */
	private Player thePlayer;
	private boolean autoAddPlayer;
	
	//used to tell GiftQuest how cohesive the network is
	private SocialNetworkDecayRates decayRate;

	/**
	 * Keeps track of how many of each type of SocialQuest has been created for naming purposes.
	 * 0: giftQuest
	 * 1: favorQuest
	 * 2: reqFavQuest
	 * 3: homewreckerQuest
	 */
	protected int[] questNumbers;

	/**
	 * Creates a new QuestGenerator.
	 */
	private QuestGenerator()
	{
		itemsInWorld = new ArrayList<Item>();
		questNumbers = new int[4];
		rand = new Random();
		autoAddPlayer = false;
		decayRate = SocialNetworkDecayRates.NORMAL;
	}

	/**
	 * @return The QuestGenerator
	 */
	public static QuestGenerator getInstance()
	{
		if (instance == null)
		{
			instance = new QuestGenerator();
		}

		return instance;
	}

	/**
	 * Clears the reference to the QuestGenerator singleton. Used to make testing easier.
	 */
	public static void clear()
	{
		instance = null;
	}

	/**
	 * Creates a new GiftQuest. The gift must be specified.
	 * 
	 * @param giver The SocialNPC issuing the quest
	 * @param target The SocialNPC to deliver the gift to
	 * @param gift The Item to deliver to the target
	 * @return A new GiftQuest
	 */
	public GiftQuest genGiftQuest(SocialNPC giver, SocialNPC target, Item gift)
	{
		questNumbers[0]++;
		SocialQuestDifficulty difficulty = decideDifficulty(giver);
		String name = "GiftQuest #" + questNumbers[0];
		
		GiftQuest quest = new GiftQuest(name, giver, target, gift, difficulty, decayRate);
		
		giver.newEvent(target, EventTypes.QUEST_CREATED_GIFTQUEST, difficulty.getDifficulty());
		
		if(autoAddPlayer)
		{
			quest.addPlayer(thePlayer);
		}
		
		return quest; 
	}

	/**
	 * Creates a new GiftQuest. The gift is randomly selected.
	 * 
	 * @param giver The SocialNPC issuing the quest
	 * @param target The SocialNPC to deliver the gift to
	 * @return A new GiftQuest
	 */
	public GiftQuest genGiftQuest(SocialNPC giver, SocialNPC target)
	{
		int num = rand.nextInt(itemsInWorld.size());

		Item gift = itemsInWorld.get(num);

		return genGiftQuest(giver, target, gift);
	}

	/**
	 * Creates a new FavorQuest. The objective must be specified.
	 * 
	 * @param giver The SocialNPC issuing the quest
	 * @param target The SocialNPC that the favor is being performed for
	 * @param objective The object on which to perform the favor
	 * @return A new FavorQuest
	 */
	public FavorQuest genFavorQuest(SocialNPC giver, SocialNPC target, FavorTarget objective)
	{
		FavorQuest quest;
		questNumbers[1]++;
		String name = "FavorQuest #" + questNumbers[1];
		SocialQuestDifficulty difficulty = decideDifficulty(giver);
		
		quest = new FavorQuest(name, giver, target, objective, difficulty);
		
		if(autoAddPlayer)
		{
			quest.addPlayer(thePlayer);
		}
		
		//make sure to link the quest and the objective
		objective.addQuest(quest);
		
		giver.newEvent(target, EventTypes.QUEST_CREATED_FAVORQUEST, difficulty.getDifficulty());
		
		return quest;
	}

	/**
	 * Creates a new FavorQuest. The objective is randomly selected.
	 * 
	 * @param giver The SocialNPC issuing the quest
	 * @param target The SocialNPC that the favor is being performed for
	 * @return A new FavorQuest
	 */
	public FavorQuest genFavorQuest(SocialNPC giver, SocialNPC target)
	{
		FavorTarget objective = favQuestPickItem();

		return genFavorQuest(giver, target, objective);
	}

	/**
	 * Creates a new RequestFavorQuest. The giver, requester, and objective must be specified. The
	 * difficulty is randomly selected.
	 * 
	 * @param giver The SocialNPC handing the quest to Players
	 * @param requester The SocialNPC that asked for the quest to be created
	 * @param objective The target of the quest
	 * @return A new RequestFavorQuest with the specified parameters
	 */
	public RequestFavorQuest genReqFavQuest(SocialNPC giver, SocialNPC requester, FavorTarget objective)
	{
		SocialQuestDifficulty difficulty = decideDifficulty(giver);

		RequestFavorQuest quest = genReqFavQuest(giver, requester, objective, difficulty);
		return quest;
	}

	/**
	 * Creates a new RequestFavorQuest. The giver and requester must be specified. The objective and
	 * difficulty are randomly selected.
	 * 
	 * @param giver The SocialNPC handing the quest to Players
	 * @param requester The SocialNPC that asked for the quest to be created
	 * @return A new RequestFavorQuest with the specified parameters
	 */
	public RequestFavorQuest genReqFavQuest(SocialNPC giver, SocialNPC requester)
	{
		Item target = favQuestPickItem();
		SocialQuestDifficulty difficulty = decideDifficulty(giver);

		RequestFavorQuest quest = genReqFavQuest(giver, requester, target, difficulty);
		return quest;
	}

	/**
	 * Creates a new RequestFavorQuest. The giver, requester, and difficulty must be specified. The
	 * objective is randomly selected.
	 * 
	 * @param giver The SocialNPC handing the quest to Players
	 * @param requester The SocialNPC that asked for the quest to be created
	 * @param difficulty How difficulty the quest will be for Players to complete
	 * @return A new RequestFavorQuest with the specified parameters
	 */
	public RequestFavorQuest genReqFavQuest(SocialNPC giver, SocialNPC requester, SocialQuestDifficulty difficulty)
	{
		Item target = favQuestPickItem();
		RequestFavorQuest quest = genReqFavQuest(giver, requester, target, difficulty);

		return quest;
	}

	/**
	 * Creates a new RequestFavorQuest. The giver, requester, objective, and difficulty must all be
	 * specified.
	 * 
	 * @param giver The SocialNPC handing the quest to Players
	 * @param requester The SocialNPC that asked for the quest to be created
	 * @param objective The target of the quest
	 * @param difficulty How difficulty the quest will be for Players to complete
	 * @return A new RequestFavorQuest with the specified parameters
	 */
	public RequestFavorQuest genReqFavQuest(SocialNPC giver, SocialNPC requester, FavorTarget objective, SocialQuestDifficulty difficulty)
	{
		RequestFavorQuest quest;
		questNumbers[2]++;
		String name = "RequestFavorQuest #" + questNumbers[2];
		
		quest = new RequestFavorQuest(name, giver, requester, objective, difficulty);
		
		if(autoAddPlayer)
		{
			quest.addPlayer(thePlayer);
		}
		
		//make sure to link the quest and the objective
		objective.addQuest(quest);
		
		giver.newEvent(requester, EventTypes.QUEST_CREATED_REQFAVQUEST, difficulty.getDifficulty());
		
		return quest;
	}

	/**
	 * Creates a new HomewreckerQuest. The target relationship must be specified. The attribute is
	 * randomly selected.
	 * 
	 * @param questGiver The SocialNPC giving out the quest
	 * @param targetRelationship The relationship to be damaged
	 * @return A new HomewreckerQuest
	 */
	public HomewreckerQuest genHomewreckerQuest(SocialNPC questGiver, SocialNPC target, ArrayList<Feelings> targetRelationship)
	{
		questNumbers[3]++;
		String name = "HomewreckerQuest #" + questNumbers[3];
		SocialQuestDifficulty difficulty = decideDifficulty(questGiver);
		FeelingsAttributes attribute;

		if (rand.nextDouble() > 0.5)
		{
			attribute = FeelingsAttributes.INTIMACY;
		} else
		{
			attribute = FeelingsAttributes.TRUST;
		}
		
		HomewreckerQuest quest = new HomewreckerQuest(name, questGiver, target, targetRelationship, difficulty, attribute);
		
		if(autoAddPlayer)
		{
			quest.addPlayer(thePlayer);
		}
		
		//the newEvent() call for the giver is in makeHomewreckerQuest() in SocialNPC

		return quest;
	}

	/**
	 * Decides how difficult to make a quest. A difficulty is randomly selected. Each
	 * difficulty level has an equal chance to be selected. If the SocialNPC does not have
	 * enough social capital to pay for a quest at that difficulty, the next highest
	 * difficulty is chosen instead.
	 * 
	 * @param npc The SocialNPC creating the quest
	 * @return The difficulty of the quest
	 */
	public SocialQuestDifficulty decideDifficulty(SocialNPC npc)
	{
		SocialQuestDifficulty difficulty = null;
		int num = rand.nextInt(4);
		int capital = npc.getCurrentCapital();

		if (num == 0 && capital >= 2500)
		{
			difficulty = SocialQuestDifficulty.YOUMUSTBEPRO;
		} else if (num <= 1 && capital >= 1500)
		{
			difficulty = SocialQuestDifficulty.HARD;
		} else if (num <= 2 && capital >= 1000)
		{
			difficulty = SocialQuestDifficulty.MEDIUM;
		} else if (num <= 3 && capital >= 500)
		{
			difficulty = SocialQuestDifficulty.EASY;
		} else
		{
			//something broke
			System.err.println(npc.getName() + " tried to create a quest, but couldn't afford it!");
		}

		return difficulty;
	}

	/**
	 * Randomly selects the Item to be used in a FavorQuest
	 * 
	 * @return The item to be used as the target for a FavorQuest
	 */
	private Item favQuestPickItem()
	{
		int num = rand.nextInt(itemsInWorld.size());
		Item target = itemsInWorld.get(num);

		return target;
	}

	/**
	 * Listens for when Items are added/removed from the game world. Keeps track of what Items are
	 * present for the purpose of creating quests.
	 * 
	 * TODO: Probably a bad design to have QuestGenerator keeping track of every item in the world.
	 */
	@Override
	public void roomUpdate(Room room, Object source, SWRoomUpdateType type)
	{
		if (type == SWRoomUpdateType.ITEM_ADDED)
		{
			itemsInWorld.add((Item) (source));
		} else if (type == SWRoomUpdateType.ITEM_REMOVED)
		{
			itemsInWorld.remove((Item) (source));
		}
	}
	
	/**
	 * Enables automatic adding of the specified Player to all SocialQuests.
	 * @param player The Player to add
	 */
	public void autoAddPlayer(Player player)
	{
		thePlayer = player;
		autoAddPlayer = true;
	}
	
	public ArrayList<Item> getItemsInWorld()
	{
		return itemsInWorld;
	}
	
	public void setDecayRate(SocialNetworkDecayRates newRate)
	{
		decayRate = newRate;
	}
}