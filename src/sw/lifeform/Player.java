package sw.lifeform;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;


import sw.combat.Action;
import sw.combat.ActionType;
import sw.combat.Attack;
import sw.combat.AttackType;
import sw.combat.Effect;
import sw.combat.EffectStat;
import sw.combat.EffectType;
import sw.combat.Heal;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.quest.Goal;
import sw.quest.ItemQuest;
import sw.quest.Quest;


/**
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
public class Player extends Lifeform
{

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
    
    /** TODO
     * Implement
     */
    //private int m_spellResistance;

    private Hashtable<ArmorLocation, Armor> m_armor = new Hashtable<ArmorLocation, Armor>();

    private Hashtable<HandLocation, Item> m_heldItems = new Hashtable<HandLocation, Item>();

    private Hashtable<ContainerLocation, ItemContainer> m_containers = new Hashtable<ContainerLocation, ItemContainer>();

    private LinkedList<Attack> m_actionQueue = new LinkedList<Attack>();

    private int m_combatWait;

    private Vector<Quest> m_quests = new Vector<Quest>();

    private Party m_myParty;

    public Player(int id, String name, String desc, int life)
    {
    	/** TODO
         * Where do we set stat points?
         */
        super(id, name, desc, life);
        m_combatWait = this.getSpeed();
    }

    /**
     * Returns the players strength stat.
     * @return
     */
    public int getStrength()
    {
        return m_strength + getEffectMod(EffectStat.STRENGTH);
    }
    
    /**
     * Returns the player's strength modifier.
     * @return
     */
    public int getStrenghtMod()
    {
    	return (getStrength()-10)/2;
    }

    /**
     * Returns the player's constitution stat.
     * @return
     */
    public int getConstitution()
    {
        return m_constitution + getEffectMod(EffectStat.CONSTITUTION);
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
     * Returns the players dexterity stat.
     * @return
     */
    public int getDexterity()
    {
        return m_dexterity + getEffectMod(EffectStat.DEXTERITY);
    }
    
    /**
     * Returns the player's dexterity modifier.
     */
    public int getDexterityMod()
    {
    	return (getDexterity()-10)/2;
    }

    /**
     * Returns the players wisdom stat.
     * @return
     */
    public int getWisdom()
    {
        return m_wisdom + getEffectMod(EffectStat.WISDOM);
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
     * Returns the players intelligence stat.
     * @return
     */
    public int getIntelligence()
    {
        return m_intelligence + getEffectMod(EffectStat.INTELLIGENCE);
    }
    
    /**
     * Returns the player's intelligence modifier.
     * @return
     */
    public int getIntelligenceMod()
    {
    	return (getIntelligence()-10)/2;
    }

    /**
     * Returns the players charisma stat.
     * @return
     */
    public int getCharisma()
    {
        return m_charisma + getEffectMod(EffectStat.CHARISMA);
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
     * Returns the player's speed based off of their Dexterity and buffs/debuffs.
     * @return
     */
    public int getSpeed()
    {
    	return 10-this.getDexterityMod()+this.getEffectMod(EffectStat.SPEED);
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
        	Attack atkNull = new Attack(this.getStrength()/5, entity, AttackType.BLUDGEONING);
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
                damageRight += this.getStrength();
            case 2:
                damageRight += (this.getStrength() * 1.5);
            }
            Attack atkRight = new Attack(damageRight, entity, AttackType.BLUDGEONING);
            m_actionQueue.add(atkRight);

            //If dual-wielding
            if (getWeapon(HandLocation.LEFT) != null)
            {
                int damageLeft = getWeapon(HandLocation.RIGHT).getDamage() + this.getStrength();
                Attack atkLeft = new Attack(damageLeft, entity, AttackType.BLUDGEONING);
                m_actionQueue.add(atkLeft);
            }
        }

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

    @Override
    public void takeHeal(int magnitude)
    {
        m_currentLifePoints += magnitude;
        if (m_currentLifePoints > m_maxLifePoints)
            m_currentLifePoints = m_maxLifePoints;
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

    public Armor getArmor(ArmorLocation location)
    {
        return m_armor.get(location);
    }

    /**
     * Equips the player with the weapon provided, assuming they have hands free
     * to hold it.
     * @param location 
     * @param weapon
     */
    public boolean holdInHand(Item item, HandLocation location)
    {
        if ((m_heldItems.get(location) == null) && (m_heldItems.get(HandLocation.BOTH) == null))
        {
            if (item instanceof Weapon)
            {
                if (((Weapon) item).getNumHands() != 2)
                {
                    m_heldItems.put(location, item);
                    return true;
                }
                else if (location == HandLocation.BOTH)
                {
                    m_heldItems.put(location, item);
                    return true;
                }
            }
            else
            {
                m_heldItems.put(location, item);
                return true;
            }
        }
        return false;
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
     * Returns the requested quest at that index in the player's list
     * of quests.
     * @param index
     * @return
     */
    public Quest getQuest(int index)
    {
        Quest quest = null;
        if (index < m_quests.size())
            quest = m_quests.get(index);
        return quest;
    }

    /**
     * Adds this quest to the list of quests the player is trying to complete.
     * @param quest
     */
    public void addQuest(Quest quest)
    {
        m_quests.add(quest);

    }

    /**
     * Removes the quest from the list of quests the player is trying to complete.
     * @param quest
     */
    public void removeQuest(Quest quest)
    {
        m_quests.remove(quest);

    }
    
    /**
     * Tells us if the player is working on a certain quest
     * @param quest
     * @return
     */
    public boolean hasQuest(Quest quest)
    {
    	return m_quests.contains(quest);
    }

    /**
     * Allows the player to update quests after killing a specific creature.
     * @param deadGuy
     */
    public void killed(Creature deadGuy)
    {
        m_myParty.killed(deadGuy);
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
     * Sets the party that this player belongs to.
     * @param party
     */
    public void setParty(Party party)
    {
        m_myParty = party;
    }

    /**
     * Returns the quests this player is presently working on.
     * @return
     */
    protected Vector<Quest> getQuests()
    {
        return m_quests;
    }

    /**
     * Informs the player they have reached a possible goal location.
     * @param goal
     */
    public void goalReached(Goal goal)
    {
        for (Quest quest : m_quests)
        {
            if (quest instanceof ItemQuest)
            {
                ((ItemQuest) quest).visitGoal(this, goal);
            }
        }
    }

    /**
     * Gets an update from the timer every tick. Updates all effects on Player
     * and, if a sufficient number of ticks has passed, picks the next action
     * from the queue.
     */
    public void updateTime(String name, int time)
    {
        for (Effect i : m_effects)
        {
            i.updateEffect();
        }

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

        m_combatWait--;
        if (m_combatWait == 0)
        {
        	/** TODO
             * Make sure this is correct.
             */
        	Action act = m_actionQueue.poll();
    		if(act.getActionType() == ActionType.ATTACK)
    			((Attack)act).getTarget().takeHit(((Attack)act).getDamage());
    		if(act.getActionType() == ActionType.HEAL)
    			((Heal)act).getTarget().takeHeal(((Heal)act).getMagnitude());
    		m_combatWait = this.getSpeed();
        }

    }

    /**
     * Gets the total magnitude of all buffs/debuffs affecting the given stat.
     * @param stat
     * @return
     */
    private int getEffectMod(EffectStat stat)
    {
        int mod = 0;
        for (Effect i : m_effects)
        {
            if (i.getType() == EffectType.BUFF && i.getStatEffected() == stat)
            {
                mod += i.getMagnitude();
            }
            else if (i.getType() == EffectType.DEBUFF && i.getStatEffected() == stat)
            {
                mod -= i.getMagnitude();
            }

        }

        return mod;
    }

    /**
     * Allows the player to equip a storage container.  Storage containers can only
     * be equipped on certain valid locations.  A container may be limited as to
     * where it can be equipped.
     * @param storage
     * @param location
     */
    public boolean equipContainer(ItemContainer storage, ContainerLocation location)
    {
        if ((storage.validLocation(location)) && (!m_containers.containsKey(location)))
        {
            m_containers.put(location, storage);
            return true;
        }
        
        return false;
    }

    /**
     * Returns the container equipped at that location.
     */
    public ItemContainer getContainer(ContainerLocation location)
    {
        return m_containers.get(location);
    }

    /**
     * 
     */
    public ItemContainer removeContainer(ContainerLocation location)
    {
        return m_containers.remove(location);
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
     * Removes and returns the item held in that hand.  For two handed items, must
     * use the Both location.
     * @param location
     * @return
     */
    public Item dropFromHand(HandLocation location)
    {
        return m_heldItems.remove(location);
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

    /**
     * Removes the armor at the specified location.
     * @param body
     * @return
     */
    public Armor removeArmor(ArmorLocation body)
    {
        return m_armor.remove(body);
    }
}
