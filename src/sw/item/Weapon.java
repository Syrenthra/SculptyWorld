package sw.item;


public class Weapon extends Item
{

    /**
     * How much damage the weapon does.
     */
    private int m_damage;
    /**
     * How many hands the weapon holds.
     */
    private int m_hands;
    
    public Weapon(String name, String desc, int size, int weight, int damage, int hands)
    {
        super(name,desc,size,weight);
        m_damage = damage;
        m_hands = hands;
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
}
