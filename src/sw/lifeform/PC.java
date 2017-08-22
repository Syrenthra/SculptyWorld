package sw.lifeform;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;


import sw.combat.Action;
import sw.combat.Attack;
import sw.combat.AttackType;
import sw.combat.Effect;
import sw.combat.CharacterStat;
import sw.combat.EffectType;
import sw.environment.Room;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.quest.Quest;
import sw.quest.QuestState;


/**
 * TODO: Define a Hash function for this class so I can more directly control it's use in Hashtables.
 * 
 * Players and Items
 * Armor is put on using the wearArmor command.  You give
 * it a piece of armor and a location.  Each piece of armor
 * is assigned one or more valid locations it can go.
 * --
 * If the armor is put in an invalid location should it throw
 * an error?
 * --
 * A weapon can be equipped using the holdItem
 * method.  You remove a weapon using the dropItem method.
 * --
 * Players may pick up an hold any type of item using
 * holdItem so long as they have enough hands empty for
 * that item.
 * --
 * Players may put and remove things in containers using the
 * storeItem and retrieveItem methods.
 * --
 * All the store/hold/wear methods return true if
 * successful and false if not.
 * 
 * @author cdgira
 *
 */
public class PC extends Lifeform
{

    public final static String ARMOR = "ARMOR";
    public final static String HELD = "HELD";
    public final static String CONTAINERS = "CONTAINERS";
    public final static String XP = "XP";
    public final static String GOLD = "GOLD";
    
    /**
     * Creates a player based on the data provided in the Hashtable.
     * @param data
     * @return
     */
    public static PC constructPC(Hashtable<String, Object> data)
    {
        int id = (Integer)data.get(ID);
        String name = (String)data.get(NAME);
        String desc = (String)data.get(DESC);
        int maxLife = (Integer)data.get(MAX_LIFE);
        
        
        PC dude = new PC(id,name,desc,maxLife);
        
        return dude;
    }

    /**
     * Damage done and encumberence limits
     */
    private int m_strength;

    /**
     * Accuracy and dodge
     */
    private int m_dexterity;

    /**
     * Health and toughness
     */
    private int m_constitution;

    private int m_wisdom;

    private int m_intelligence;
    
    private int m_charisma;
    
    /**
     * How much gold the PC has on them.
     */
    private int m_gold = 0;
    
    /**
     * How much XP the player has earned.
     * TODO: Possibly make XP spendable.
     */
    private int m_xp = 0;

    /** TODO
     * Implement
     */
    //private int m_spellResistance;

    private Hashtable<ArmorLocation, Armor> m_armor = new Hashtable<ArmorLocation, Armor>();

    private Hashtable<HandLocation, Item> m_heldItems = new Hashtable<HandLocation, Item>();

    private Hashtable<ContainerLocation, ItemContainer> m_containers = new Hashtable<ContainerLocation, ItemContainer>();

    private LinkedList<Attack> m_actionQueue = new LinkedList<Attack>();

    private int m_combatWait;

    /**
     * Quests that belong directly to this player and are always active regardless of party.
     */
    private Vector<Quest> m_nativeQuests = new Vector<Quest>();
    
    /**
     * Quests that were inherited from other players and are only active while in that player's party.
     */
    private Vector<Quest> m_inheritedQuests = new Vector<Quest>();

    private Party m_myParty;
    
    /**
     * Who is observing this resource.  Usually the observer is a quest the player is doing.
     */
    private Vector<PCObserver> m_observers = new Vector<PCObserver>();
    
    public PC(int id, String name, String desc, int life)
    {
    	/** TODO
         * Where do we set stat points?
         */
        super(id, name, desc, life);
        setStats(14, 10, 10, 10, 10, 10);
        m_combatWait = this.getSpeed();
        m_myParty = new Party(this);
    }

    /**
     * Adds this quest to the list of quests the player is trying to complete.
     * @param quest
     */
    private void addInheritedQuest(Quest quest)
    {
        m_inheritedQuests.add(quest);
        this.addPCObserver(quest);

    }
    
    /**
     * Adds this quest to the list of quests the player is trying to complete.
     * @param quest
     */
    void addNativeQuest(Quest quest)
    {
        m_nativeQuests.add(quest);
        this.addPCObserver(quest);

    }
    
    /**
     * Add a PCObserver.
     */
    public void addPCObserver(PCObserver observer)
    {
        m_observers.add(observer);
    }

    /**
     * Assigns the quest that is native to the player. This means regardless of group this quest is considered active
     * for the player.  Any groups this player joins will have these quests added to those party member's inherited quests.
     * This adds the quest to the player, the player to the quest and
     * any other players that are part of the same party as the player.
     * @param quest
     */
    public void assignNativeQuest(Quest quest)
    {  
        if (quest.addPlayer(this))
        {
            addNativeQuest(quest);
            PCEvent event = new PCEvent(this,quest,PCEvent.NATIVE_QUEST_ADDED);
            informObservers(event);
        }
    }
    
    /**
     * Attacks the entity using the weapon equipped, if no weapon then
     * the player punches the entity doing damage equal to 1/5th the
     * player's strength.
     */
    @Override
    public void attack(Lifeform entity)
    {
    	/** TODO
         * Implement
         */
    	//Refactor this
    	// Need to fix hands
    	// Bows?
    	// Two handed weapons are HandLocation.BOTH
    	// Non weapons in hands
        if (m_heldItems.get(HandLocation.RIGHT) == null)
        {
        	//Unarmed attacks
        	Attack atkNull = new Attack(this.getStrength()/5, entity, AttackType.BLUDGEONING, null);
            m_actionQueue.add(atkNull);
        }
        else
        {
            //NEED TO IMPLEMENT
            //	Bows (Arrow as separate damage, or just used for ammo, or don't worry?)

            //Main hand damage
            int damageRight = getWeapon(HandLocation.RIGHT).getDamage();
            switch (getWeapon(HandLocation.RIGHT).getNumHands())
            {
            	case 1:
            		damageRight += this.getStrengthMod();
            		break;
            	case 2:
            		damageRight += (this.getStrengthMod() * 1.5);
            		break;
            }
            Attack atkRight = new Attack(damageRight, entity, AttackType.BLUDGEONING, null);
            m_actionQueue.add(atkRight);

            //If dual-wielding
            if (getWeapon(HandLocation.LEFT) != null)
            {
                int damageLeft = getWeapon(HandLocation.RIGHT).getDamage() + this.getStrengthMod();
                Attack atkLeft = new Attack(damageLeft, entity, AttackType.BLUDGEONING, null);
                m_actionQueue.add(atkLeft);
            }
        }

    }
    
    /**
     * Checks to see whether this observer in question is already observing.
     * @param observer
     * @return
     */
    public boolean containsObserver(PCObserver observer)
    {
        return m_observers.contains(observer);
    }

    /**
     * Removes and returns the item held in that hand.  For two handed items, must
     * use the Both location.
     * @param location
     * @return
     */
    public Item dropFromHand(HandLocation location)
    {
        Item item = m_heldItems.remove(location);
        if (item != null)
        {
            PCEvent event = new PCEvent(this,item,PCEvent.DROP_ITEM);
            this.informObservers(event);
        }
        return item;
    }
    
    /**
     * Allows the player to equip a storage container.  Storage containers can only
     * be equipped on certain valid locations.  A container may be limited as to
     * where it can be equipped.
     * @param storage
     * @param location
     */
    public boolean equipContainer(ItemContainer storage)
    {
        if ((!m_containers.containsKey(storage.getValidLocation())))
        {
            m_containers.put(storage.getValidLocation(), storage);
            return true;
        }
        
        return false;
    }
    
    public Armor getArmor(ArmorLocation location)
    {
        return m_armor.get(location);
    }

    /**
     * Returns the players charisma stat.
     * @return
     */
    public int getCharisma()
    {
        return m_charisma + getEffectMod(CharacterStat.CHARISMA);
    }
    
    /**
     * Returns the player's charisma modifier.
     * @return
     */
    public int getCharismaMod()
    {
    	return (getCharisma()-10)/2;
    }

    /**
     * Returns the player's constitution stat.
     * @return
     */
    public int getConstitution()
    {
        return m_constitution + getEffectMod(CharacterStat.CONSTITUTION);
    }
    
    /**
     * Returns the player's constitution modifier.
     * @return
     */
    public int getConstitutionMod()
    {
    	return (getConstitution()-10)/2;
    }

    /**
     * Returns the container equipped at that location.
     */
    public ItemContainer getContainer(ContainerLocation location)
    {
        return m_containers.get(location);
    }
    
    /**
     * Returns whatever is being held in the requested hand.  You must use the location
     * BOTH if it is an item that requires two hands to hold.
     * @return
     */
    public Item getContentsInHand(HandLocation location)
    {
        return m_heldItems.get(location);
    }

    /**
     * Returns the players dexterity stat.
     * @return
     */
    public int getDexterity()
    {
        return m_dexterity + getEffectMod(CharacterStat.DEXTERITY);
    }

    /**
     * Returns the player's dexterity modifier.
     */
    public int getDexterityMod()
    {
    	return (getDexterity()-10)/2;
    }
    
    /**
     * Gets the total magnitude of all buffs/debuffs affecting the given stat.
     * @param stat
     * @return
     */
    private int getEffectMod(CharacterStat stat)
    {
        int mod = stat == CharacterStat.SPEED ? 1 : 0;
        for (Effect i : m_effects)
        {
            if (i.getType() == EffectType.BUFF && i.getStatEffected() == stat)
            {
            	if(stat == CharacterStat.SPEED)
            	{
            		mod *= i.getMagnitude();
            	}
            	else
            	{
            		mod += i.getMagnitude();
            	}
            }
            else if (i.getType() == EffectType.DEBUFF && i.getStatEffected() == stat)
            {
            	if(stat == CharacterStat.SPEED)
            	{
            		mod *= i.getMagnitude();
            	}
            	else
            	{
            		mod -= i.getMagnitude();
            	}
            }

        }

        return mod;
    }

    /**
     * Returns how much gold the player has.
     * @return
     */
    public int getGold()
    {
        return m_gold;
    }

    /**
     * 
     * @param location
     * @return The item held the requested hand.
     */
    public Item getHeldItem(HandLocation location)
    {
        return m_heldItems.get(location);
    }

    /**
     * Returns the requested quest at that index in the player's list
     * of inherited quests.
     * @param index
     * @return
     */
    public Quest getInheritedQuest(int index)
    {
        Quest quest = null;
        if (index < m_inheritedQuests.size())
            quest = m_inheritedQuests.get(index);
        return quest;
    }

    /**
     * Returns the inherited quests this player is presently working on.
     * @return
     */
    public Vector<Quest> getInheritedQuests()
    {
        return m_inheritedQuests;
    }

    /**
     * Returns the players intelligence stat.
     * @return
     */
    public int getIntelligence()
    {
        return m_intelligence + getEffectMod(CharacterStat.INTELLIGENCE);
    }
    
    /**
     * Returns the player's intelligence modifier.
     * @return
     */
    public int getIntelligenceMod()
    {
    	return (getIntelligence()-10)/2;
    }
    
    @Override
    public Hashtable<String,Object> getLifeformInfo()
    {
        Hashtable<String,Object> data = super.getLifeformInfo();
        
        Vector<Integer> armor = new Vector<Integer>();
        Enumeration<Armor> e = m_armor.elements();
        while (e.hasMoreElements())
        {
            Armor a = e.nextElement();
            armor.add(a.getItemID());   
        }
        data.put(ARMOR, armor);
        
        Hashtable<String,Integer> heldItems = new Hashtable<String,Integer>();
        Enumeration<HandLocation> e2 = m_heldItems.keys();
        while (e2.hasMoreElements())
        {
            HandLocation loc = e2.nextElement();
            Item i = m_heldItems.get(loc);
            heldItems.put(loc.name() , i.getItemID());   
        }
        data.put(HELD, heldItems);
        
        Vector<Integer> containers = new Vector<Integer>();
        Enumeration<ItemContainer> e3 = m_containers.elements();
        while (e3.hasMoreElements())
        {
            ItemContainer item = e3.nextElement();
            containers.add(item.getItemID());   
        }
        data.put(CONTAINERS, containers);
        
        return data;
    }

    /**
     * Returns the requested quest at that index in the player's list
     * of native quests.
     * @param index
     * @return
     */
    public Quest getNativeQuest(int index)
    {
        Quest quest = null;
        if (index < m_nativeQuests.size())
            quest = m_nativeQuests.get(index);
        return quest;
    }
    
    /**
     * Returns the native quests this player is presently working on.
     * @return
     */
    public Vector<Quest> getNativeQuests()
    {
        return m_nativeQuests;
    }
    
    /**
     * Returns the party that this player presently belongs to.
     * @return
     */
    public Party getParty()
    {
        return m_myParty;
    }

    /**
     * Returns the player's speed based off of their Dexterity and buffs/debuffs.
     * @return
     */
    public int getSpeed()
    {
    	return (10-this.getDexterityMod())*this.getEffectMod(CharacterStat.SPEED);
    }

    /**
     * Returns the players strength stat.
     * @return
     */
    public int getStrength()
    {
        return m_strength + getEffectMod(CharacterStat.STRENGTH);
    }
    
    /**
     * Returns the player's strength modifier.
     * @return
     */
    public int getStrengthMod()
    {
    	return (getStrength()-10)/2;
    }

    /**
     * Returns the weapon held in that hand. If it is not a weapon then
     * return null.
     * @param location
     * @return
     */
    public Weapon getWeapon(HandLocation location)
    {
        if (m_heldItems.get(location) instanceof Weapon)
            return (Weapon)m_heldItems.get(location);
        return null;
    }

    /**
     * Returns the players wisdom stat.
     * @return
     */
    public int getWisdom()
    {
        return m_wisdom + getEffectMod(CharacterStat.WISDOM);
    }

    /**
     * Returns the player's wisdom modifier.
     * @return
     */
    public int getWisdomMod()
    {
    	return (getWisdom()-10)/2;
    }

    /**
     * Returns how much XP the player has earned.
     * @return
     */
    public int getXP()
    {
        return m_xp;
    }

    /**
     * Will hand over the item in that hand location over to another Lifeform.  At this
     * point that would be an NPC, PC, or Creature.
     * @param entity
     * @param itemLocation
     */
    public void giveItemInHand(Lifeform entity, HandLocation itemLocation)
    {
        if (entity instanceof NPC)
        {
            NPC npc = (NPC)entity;
            if (m_heldItems.get(itemLocation) != null)
            {
                Item item = m_heldItems.remove(itemLocation);
                if (npc.addPersonalItem(this,item))
                {
                    PCEvent event = new PCEvent(this,npc,item,PCEvent.GIVE_ITEM);
                    this.informObservers(event);
                }
                else
                {
                    m_heldItems.put(itemLocation, item);
                }
            }
        }
        
    }
    
    /**
     * Tells us if the player is working on a certain quest
     * @param quest
     * @return
     */
    public boolean hasQuest(Quest quest)
    {
    	return m_nativeQuests.contains(quest);
    }

    /**
     * Equips the player with the weapon provided, assuming they have hands free
     * to hold it.
     * @param location 
     * @param weapon
     */
    public boolean holdInHand(Item item, HandLocation location)
    {
        boolean success = false;
        PCEvent event = null;
        if ((m_heldItems.get(location) == null) && (m_heldItems.get(HandLocation.BOTH) == null))
        {
            if (item instanceof Weapon)
            {
                if (((Weapon) item).getNumHands() != 2)
                {
                    m_heldItems.put(location, item);
                    event = new PCEvent(this,item,PCEvent.GET_ITEM);
                    success = true;
                }
                else if (location == HandLocation.BOTH)
                {
                    m_heldItems.put(location, item);
                    event = new PCEvent(this,item,PCEvent.GET_ITEM);
                    success = true;
                }
            }
            else
            {
                m_heldItems.put(location, item);
                event = new PCEvent(this,item,PCEvent.GET_ITEM);
                success = true;
            }
        }
        if (success)
            informObservers(event);
        return success;
    }

    /**
     * Informs an observers of an event that has taken place.
     * @param event
     */
    public void informObservers(PCEvent event)
    {
        for (PCObserver pco : m_observers)
        {
            pco.pcUpdate(event);
        }
    }

    /**
     * Allows the player to update quests after killing a specific creature.
     * @param deadGuy
     */
    public void killed(Creature deadGuy)
    {
        PCEvent event = new PCEvent(this,deadGuy,PCEvent.KILLED_CREATURE);
        this.informObservers(event);
    }
    
    /**
     * The player leaves his or her present part and goes solo.
     */
    public void leaveParty()
    {
        m_myParty.removePlayer(this);
    }

    /**
     * Removes the armor at the specified location.
     * @param body
     * @return
     */
    public Armor removeArmor(ArmorLocation body)
    {
        return m_armor.remove(body);
    }

    /**
     * 
     */
    public ItemContainer removeContainer(ContainerLocation location)
    {
        return m_containers.remove(location);
    }

    /**
     * Removes the quest from the list of inherited quests the player is trying to complete.
     * @param quest
     */
    public void removeInheritedQuest(Quest quest)
    {
        m_inheritedQuests.remove(quest);

    }

    /**
     * Removes the quest from the list of native quests the player is trying to complete.
     * @param quest
     */
    public void removeNativeQuest(Quest quest)
    {
        m_nativeQuests.remove(quest);

    }
    
    /**
     * Remove a PCObserver.
     */
    public void removePCObserver(PCObserver observer)
    {
        m_observers.remove(observer);
    }

    /**
     * Sets the room the lifeform is currently located in.
     * @param room
     */
    @Override
    public void setCurrentRoom(Room room)
    {
        PCEvent event = new PCEvent(this,m_currentRoom,room,PCEvent.MOVED);
        m_currentRoom = room;
        this.informObservers(event);
    }
    
    /**
     * Sets the party that this player belongs to.
     * @param party
     */
    public void setParty(Party party)
    {
        m_myParty = party;
    }

    public void setStats(int str, int dex, int con, int inte, int wis, int cha)
    {
    	m_strength = str;
    	m_dexterity = dex;
    	m_constitution = con;
    	m_intelligence = inte;
    	m_wisdom = wis;
    	m_charisma = cha;
    }

    /**
     * Stores an item in a container the player is hold if there is space.
     * @param item
     * @param location
     * @return True if successful in storing the item, false otherwise.
     */
    public boolean storeItem(Item item, ContainerLocation location)
    {
        if (m_containers.get(location) != null)
        {
            return m_containers.get(location).store(item);
        }
        return false;
    }

    @Override
    public void takeHeal(int magnitude)
    {
        m_currentLifePoints += magnitude;
        if (m_currentLifePoints > m_maxLifePoints)
            m_currentLifePoints = m_maxLifePoints;
    }

    /**
     * The player takes damage equal to the damage provided minus
     * the armor the player is wearing.
     */
    @Override
    public void takeHit(int damage)
    {
    	/** TODO
         * Spell resistance? Also, creatures.
         */
        int armorProtection = 0;
        for (ArmorLocation location : ArmorLocation.values())
        {
            Armor piece = m_armor.get(location);
            if (piece != null)
                armorProtection += piece.getProtection();
        }
        int damageTaken = damage - armorProtection;
        if (damageTaken > 0)
            m_currentLifePoints -= damageTaken;

        if (m_currentLifePoints < 0)
            m_currentLifePoints = 0;

    }

    public void updateGold(int additionalGold)
    {
        if (m_gold + additionalGold >= 0)
            m_gold = m_gold + additionalGold;
        
    }
    
    /**
     * Updates the quests assigned to this player.  If the player has this quest as a native quest ignore.
     * If the player has it as an inherited quest and it is inactive, the move to in-progress.  If the player
     * does not have this quest then add to the inherited list and make in-progress.
     * @param quest
     */
    public void updateQuests(Quest quest)
    {
        if (m_nativeQuests.contains(quest))
        {
            // do nothing.
        }
        else if (m_inheritedQuests.contains(quest))
        {
            quest.setCurrentState(this,QuestState.IN_PROGRESS);
        }
        else  // Need to add it to our list of inherited quests.
        {
             addInheritedQuest(quest);
             quest.addPlayer(this);
        }
        
    }
    
    /**
     * Gets an update from the timer every tick. Updates all effects on Player
     * and, if a sufficient number of ticks has passed, picks the next action
     * from the queue.
     */
    public void updateTime(String name, int time)
    {
    	//Update effects on Player.
        for (Effect i : m_effects)
        {
            i.updateEffect();
        }

        //Remove any effects that have expired.
        int i = 0;
        while(i < m_effects.size())
        {
            if (m_effects.get(i).getRemovalFlag())
            {
                m_effects.remove(i);
            }
            else
            {
            	i++;
            }
        }

        //If it is time for the Player to take an action, apply the first one in the queue.
        m_combatWait--;
        if (m_combatWait == 0)
        {
        	Action act = m_actionQueue.poll();
        	act.apply();
    		m_combatWait = this.getSpeed();
        }

    }

    public void updateXP(int additionalXP)
    {
        m_xp = m_xp + additionalXP;
        if (m_xp < 0)
            m_xp = 0;
    }

    /**
     * Has the user wear a piece of armor if there is none at that
     * location.
     * @param piece
     */
    public boolean wearArmor(Armor piece)
    {
        if (m_armor.get(piece.getLocation()) == null)
        {
            m_armor.put(piece.getLocation(), piece);
            return true;
        }
        return false;
    }
}
