package sw.item;

import java.util.Hashtable;


public class Weapon extends Item
{
    public static final String DMG = "DAMAGE";
    public static final String HAND = "HANDS";

    /**
     * How much damage the weapon does.
     */
    private int m_damage;
    /**
     * How many hands needed to hold the weapon.
     */
    private int m_hands;
    
    public Weapon(String name, String desc, int size, int weight, int damage, int hands)
    {
        super(name,desc,size,weight);
        m_damage = damage;
        m_hands = hands;
        m_type = Item.WEAPON;
    }

    /**
     * Returns how much damage the weapon does.
     * @return
     */
    public int getDamage()
    {
        return m_damage;
    }

    /**
     * Returns how many hands the weapon takes to hold.
     * @return
     */
    public int getNumHands()
    {
        return m_hands;
    }
    
    /**
     * 
     */
    @Override
    public Hashtable<String,Object> getItemInfo()
    {
        Hashtable<String,Object> data = super.getItemInfo();
        
        data.put(DMG, m_damage);
        data.put(HAND, m_hands);
        
        return data;
    }
    
    /**
     * 
     * @param data
     * @return
     */
    public static Weapon constructWeapon(Hashtable<String,Object> data)
    {
        String name = (String)data.get(NAME);
        String desc = (String)data.get(DESC);
        int size = (Integer)data.get(SIZE);
        int weight = (Integer)data.get(WEIGHT);
        int damage = (Integer)data.get(DMG);
        int hand = (Integer)data.get(HAND);
        
        Weapon item = new Weapon(name,desc,size,weight,damage,hand);
        
        int id = (Integer)data.get(Item.ID);
        item.setItemID(id);
               
        return item;
    }
    
    /**
     * Creates of copy of this weapon.
     */
    @Override
    public Weapon clone()
    {
        return new Weapon(m_name, m_description, m_size, m_weight, m_damage, m_hands);
    }
}
