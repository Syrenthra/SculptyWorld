package sw.quest;

import java.util.ArrayList;
import java.util.Random;

import sw.environment.Room;
import sw.environment.RoomObserver;
import sw.environment.RoomUpdateType;
import sw.environment.TheWorld;
import sw.item.Item;
import sw.lifeform.PC;
import sw.lifeform.NPC;
import sw.quest.reward.FavorReward;
import sw.quest.reward.GiftReward;
import sw.quest.reward.HomewreckerReward;
import sw.quest.reward.RequestFavorReward;
import sw.quest.task.DeliverItemTask;
import sw.quest.task.KillCreatureTask;
import sw.quest.task.QuestTask;
import sw.quest.task.TalkToNPCTask;
import sw.quest.task.TaskType;
import sw.socialNetwork.Feelings;
import sw.socialNetwork.FeelingsAttributes;
import sw.socialNetwork.SocialNetwork;
import sw.socialNetwork.SocialNetworkDecayRates;
import sw.socialNetwork.simulation.EventTypes;

/**
 * 
 * @author David Abrams and Dr. Girard
 * 
 * This class is a factory that creates SocialQuests as needed for SocialNPCs. It is a singleton.
 * All Social quests are setup as TimedQuests.
 * 
 * TODO: Hold Time and Complete Time should be determined based on additional factors (see UML Quest document).
 * 
 * TODO: Make sure all Social Quests are Timed Quests.
 */
public class QuestGenerator
{

    /**
     * Keeps track of how many of each type of SocialQuest has been created for naming purposes.
     * 0: giftQuest
     * 1: favorQuest
     * 2: reqFavQuest
     * 3: homewreckerQuest
     */
    protected static int[] questNumbers = new int[4];
    
    /**
     * Decay rate used by the Quest Generator
     */
    protected static SocialNetworkDecayRates rate=SocialNetworkDecayRates.NORMAL;

    /**
     * Just so no one tries to create an instance of one.
     */
    private QuestGenerator()
    {

    }

    /**
     * Creates a new GiftQuest. This is a Quest with a GiftReward for the social network and a task
     * to deliver a specific item (usually provided by the NPC giving the quest). The gift must be specified.
     * 
     * @param giver The NPC issuing the quest
     * @param target The NPC to deliver the gift to
     * @param gift The Item to deliver to the target
     * @return A new GiftQuest
     */
    public static TimedQuest genGiftQuest(NPC giver, NPC target, Item gift, SocialNetworkDecayRates decayRate)
    {
        questNumbers[0]++;
        SocialCapitolCost cost = decideSocialCapitolCost(giver);
        String name = "GiftQuest #" + questNumbers[0];

        TimedQuest quest = new TimedQuest(name, "Gift Quest Description.", giver);
        quest.setTimeToCompleteRemaining(60);
        quest.setTimeToHoldRemaining(60);

        GiftReward reward = new GiftReward(quest, target, gift, cost, decayRate);
        quest.addReward(reward);

        DeliverItemTask task = new DeliverItemTask(quest, target, gift, 1);
        quest.addTask(task);
        giver.newEvent(target, EventTypes.QUEST_CREATED_GIFTQUEST, cost.getCost());

        return quest;
    }

    /**
     * Creates a new GiftQuest. The gift is randomly selected.
     * 
     * @param giver The SocialNPC issuing the quest
     * @param target The SocialNPC to deliver the gift to
     * @return A new GiftQuest
     */
    public static TimedQuest genGiftQuest(NPC giver, NPC target)
    {
        Item gift = pickItemForDeliveryTask(giver.getQuestItems());

        return genGiftQuest(giver, target, gift, rate);
    }

    /**
     * Creates a new FavorQuest. This is a Quest with a FavorReward for the social network and a task
     * of some kind.The objective must be specified.
     * 
     * TODO: For now the task is just an item delivery task, need to add more variability.
     * 
     * @param giver The SocialNPC issuing the quest
     * @param target The SocialNPC that the favor is being performed for
     * @param type The type of favor to perform.
     * @return A new FavorQuest
     */
    public static TimedQuest genFavorQuest(NPC giver, NPC target, TaskType type)
    {
        questNumbers[1]++;
        String name = "FavorQuest #" + questNumbers[1];
        SocialCapitolCost cost = decideSocialCapitolCost(giver);

        TimedQuest quest = new TimedQuest(name, "Favor Quest Description.", giver);
        FavorReward reward = new FavorReward(quest, target, cost);
        quest.addReward(reward);
        giver.newEvent(target, EventTypes.QUEST_CREATED_FAVORQUEST, cost.getCost());

        if (type == TaskType.CREATURE_TASK)
        {
            // Randomly choose a creature to hunt.
            //KillCreatureTask task = new KillCreatureTask(quest,,1);
            //quest.addTask(objective);
        }
        else if (type == TaskType.ITEM_TASK)
        {
            Item gift = pickItemForDeliveryTask(giver.getQuestItems());
            DeliverItemTask task = new DeliverItemTask(quest, target, gift, 1);
            quest.addTask(task);
        }

        return quest;
    }

    /**
     * Creates a new FavorQuest with an Item Task. This is a Quest with a FavorReward for the social network and a task
     * of some kind.The objective must be specified.
     *
     * 
     * @param giver The SocialNPC issuing the quest
     * @param target The SocialNPC that the favor is being performed for
     * @param type The item to deliver.  Assumes just delivering one of them.
     * @return A new FavorQuest
     */
    public static TimedQuest genFavorQuest(NPC giver, NPC target, Item gift)
    {
        questNumbers[1]++;
        String name = "FavorQuest #" + questNumbers[1];
        SocialCapitolCost cost = decideSocialCapitolCost(giver);

        TimedQuest quest = new TimedQuest(name, "Favor Quest Description.", giver);
        FavorReward reward = new FavorReward(quest, target, cost);
        quest.addReward(reward);

        DeliverItemTask task = new DeliverItemTask(quest, target, gift, 1);
        quest.addTask(task);

        return quest;
    }

    /**
     * This is a Quest with a FavorReward for the social network and a task
     * of some kind. The objective is always an Deliver Item Task.
     * 
     * @param giver The SocialNPC issuing the quest
     * @param target The SocialNPC that the favor is being performed for
     * @return A new FavorQuest
     */
    public static TimedQuest genFavorQuest(NPC giver, NPC target)
    {
        return genFavorQuest(giver, target, TaskType.ITEM_TASK);
    }

    /**
     * Creates a new RequestFavorQuest. The giver, requester, and objective must be specified. The
     * difficulty is randomly selected.
     * 
     * @param giver The SocialNPC handing the quest to Players
     * @param target The SocialNPC that asked for the quest to be created
     * @param favorType The target of the quest
     * @return A new RequestFavorQuest with the specified parameters
     */
    public static TimedQuest genReqFavQuest(NPC giver, NPC target, TaskType favorType)
    {
        SocialCapitolCost cost = decideSocialCapitolCost(giver);

        TimedQuest quest = genReqFavQuest(giver, target, favorType, cost);
        return quest;
    }

    /**
     * Creates a new RequestFavorQuest. The giver and requester must be specified. The objective and
     * difficulty are randomly selected.
     * 
     * @param giver The SocialNPC handing the quest to Players
     * @param target The SocialNPC that asked for the quest to be created
     * @return A new RequestFavorQuest with the specified parameters
     */
    public static TimedQuest genReqFavQuest(NPC giver, NPC target)
    {
        SocialCapitolCost cost = decideSocialCapitolCost(giver);

        TimedQuest quest = genReqFavQuest(giver, target, TaskType.ITEM_TASK, cost);
        return quest;
    }

    /**
     * Creates a new RequestFavorQuest. The giver, requester, and difficulty must be specified. The
     * objective is randomly selected.
     * 
     * @param giver The SocialNPC handing the quest to Players
     * @param target The SocialNPC that asked for the quest to be created
     * @param difficulty How difficulty the quest will be for Players to complete
     * @return A new RequestFavorQuest with the specified parameters
     */
    public static Quest genReqFavQuest(NPC giver, NPC target, SocialCapitolCost cost)
    {
        // TODO Should pick a random task type in the future.
        Quest quest = genReqFavQuest(giver, target, TaskType.ITEM_TASK, cost);

        return quest;
    }

    /**
     * TODO: Who issues which part of this quest?
     * 
     * A quest where one NPC asks another NPC to perform a favor on behalf of the first. The
     * ????? NPC creates this quest and issues it to players. During completion of the quest,
     * nothing is different from a FavorQuest. Upon completion of a RequestFavorQuest, the trust counter
     * in the relationship of the SocialNPCs involved is incremented. After the counter hits 3, the
     * trust between the two SocialNPCs starts to increase. Likewise, after 3 consecutive failures, the
     * trust between the SocialNPCs will begin to decrease.
     * 
     * This should be a two or three part quest.  In the first part the PCs take the favor request to the target.
     * Then the target does the favor or has the PCs do the favor. Once the favor is done the PCs report back to 
     * the granter that the favor was completed.
     * 
     * Creates a new RequestFavorQuest. The giver, requester, objective, and social capitol cost must all be
     * specified.
     * 
     * @param giver The NPC wanting to ask another NPC to do a favor.
     * @param target The NPC that is being asked to do the favor.
     * @param favorType The favor that is to be done.
     * @param cost How much social capitol the quest will be to create the quest for the Players
     * @return A new RequestFavorQuest with the specified parameters
     */
    public static TimedQuest genReqFavQuest(NPC giver, NPC target, TaskType favorType, SocialCapitolCost cost)
    {
        TimedQuest quest;
        questNumbers[2]++;
        String name = "RequestFavorQuest #" + questNumbers[2];

        quest = new TimedQuest(name, "Description for Request Favor Quest.", giver);
        RequestFavorReward reward = new RequestFavorReward(quest, target, cost);
        quest.addReward(reward);

        // TODO This task is not functional yet.
        TalkToNPCTask firstTask = new TalkToNPCTask();

        // TODO Wonder if sometimes if the NPC will just do the favor, etc...
        if (favorType == TaskType.CREATURE_TASK)
        {
            // Randomly choose a creature to hunt.
            //KillCreatureTask task = new KillCreatureTask(quest,,1);
            //quest.addTask(objective);
        }
        else if (favorType == TaskType.ITEM_TASK)
        {
            Item gift = pickItemForDeliveryTask(giver.getQuestItems());
            DeliverItemTask favorTask = new DeliverItemTask(quest, target, gift, 1);
            quest.addTask(favorTask);
        }
        
        giver.newEvent(target, EventTypes.QUEST_CREATED_REQFAVQUEST, cost.getCost());
        
        return quest;
    }

    /**
     * This method allows the NPC to pick which relationship to target when it wants to make a
     * HomerweckerQuest. The SNPC picks the target relationship by choosing a relationship that one
     * of its friends is involved in. This SNPC may not be the other participant in the
     * relationship.
     * 
     * Creates a new HomewreckerQuest. The target relationship must be specified. The attribute is
     * randomly selected.
     * 
     * @param questGiver The SocialNPC giving out the quest
     * @param targetRelationship The relationship to be damaged
     * @return A new HomewreckerQuest
     */
    public static TimedQuest genHomewreckerQuest(NPC questGiver)
    {
        TimedQuest quest = null;

        // NPC questGiver, NPC target, ArrayList<Feelings> targetRelationship
        Random rand = new Random();
        boolean finished = false;
        SocialNetwork questGiverNetwork = questGiver.getSocialNetwork();
        ArrayList<NPC> list = new ArrayList<NPC>(questGiverNetwork.getFriends());
        int num;
        int index;
        NPC target = null;

        ArrayList<Feelings> targetRelationship = new ArrayList<Feelings>();

        if (list.size() != 0)
        {
            while ((!finished) && (list.size() > 0))
            {
                //pick a random friend
                num = rand.nextInt(list.size());
                target = list.get(num);

                //make sure that friend is friends with more than just this SocialNPC
                SocialNetwork targetNetwork = target.getSocialNetwork();
                if (targetNetwork.getFriends().size() <= 1)
                {
                    //if it's not, then remove it from this list
                    list.remove(num);
                }
                else
                {
                    //if it is, then grab one of its other friends as the target                
                    index = rand.nextInt(targetNetwork.getFriends().size());
                    NPC tmpTarget = targetNetwork.getFriends().get(index);

                    while (tmpTarget.equals(questGiver))
                    {
                        //this SocialNPC cannot be part of the target relationship
                        index = rand.nextInt(targetNetwork.getFriends().size());
                        tmpTarget = targetNetwork.getFriends().get(index);
                    }

                    targetRelationship.add(targetNetwork.getRelationships().get(tmpTarget));
                    targetRelationship.add(tmpTarget.getSocialNetwork().getRelationships().get(target));
                    finished = true;
                }    
            }
            if (list.size() > 0)
            {
                questNumbers[3]++;
                String name = "HomewreckerQuest #" + questNumbers[3];
                SocialCapitolCost cost = decideSocialCapitolCost(questGiver);
                FeelingsAttributes attribute;

                if (rand.nextDouble() > 0.5)
                {
                    attribute = FeelingsAttributes.INTIMACY;
                }
                else
                {
                    attribute = FeelingsAttributes.TRUST;
                }

                quest = new TimedQuest(name, "Homewreker Quest Description.", questGiver);
                HomewreckerReward reward = new HomewreckerReward(quest, target, targetRelationship, cost, attribute);
                quest.addReward(reward);
                questGiver.newEvent(target, EventTypes.QUEST_CREATED_HOMEWRECKER, cost.getCost());
                // TODO: Need to add a Task that will cause the quest to be successful.
            }
        }

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
    public static SocialCapitolCost decideSocialCapitolCost(NPC npc)
    {
        Random rand = new Random();
        SocialCapitolCost difficulty = null;
        int num = rand.nextInt(4);
        int capital = npc.getSocialNetwork().getCurrentCapital();

        if (num == 0 && capital >= 2500)
        {
            difficulty = SocialCapitolCost.EXTREME;
        }
        else if (num <= 1 && capital >= 1500)
        {
            difficulty = SocialCapitolCost.EXPENSIVE;
        }
        else if (num <= 2 && capital >= 1000)
        {
            difficulty = SocialCapitolCost.MEDIUM;
        }
        else if (num <= 3 && capital >= 500)
        {
            difficulty = SocialCapitolCost.CHEAP;
        }
        else
        {
            //something broke
            System.err.println(npc.getName() + " tried to create a quest, but couldn't afford it!");
        }

        return difficulty;
    }

    /**
     * Randomly selects the Item to be used in a FavorQuest
     * 
     * // TODO need a better system if Item selection for these types of tasks.
     * 
     * @return The item to be used as the target for a FavorQuest
     */
    private static Item pickItemForDeliveryTask(ArrayList<Item> itemsForQuest)
    {
    	
    	//For new system, can just create an item with the Giver's gift category
        Random rand = new Random();
        int num = rand.nextInt(itemsForQuest.size());
        Item target = itemsForQuest.get(num);

        return target;
    }
    
    /**
	 * Clears the reference to the QuestGenerator singleton. Used to make testing easier.
	 */
	public static void clear()
	{
		questNumbers = new int[4];
	}

	/**
	 * Allows the setting of a specific decay rate, since the default is normal
	 * @param newRate the new rate to be used for the creation of relations
	 */
	public static void setDecayRate(SocialNetworkDecayRates newRate) 
	{
		rate=newRate;
	}


}