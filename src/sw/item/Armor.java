package sw.item;

/**
 * Used to represent all items that can be used to protect players.
 * @author cdgira
 *
 */
public class Armor extends Item
{
    /**
     * Where this item can be worn as protection.
     */
    private ArmorLocation m_location;
    /**
     * How much protection it provides.
     */
    private int m_protection;

    public Armor(String name, String desc, int size, int weight, ArmorLocation loc, int prot)
    {
        super(name,desc,size,weight);
        m_location = loc;
        m_protection = prot;
    }

    /**
     * 
     * @return The location of where to wear the armor.
     */
    public ArmorLocation getLocation()
    {
        return m_location;
    }

    /**
     * 
     * @return How much the armor will protect the player.
     */
    public int getProtection()
    {
        return m_protection;
    }
}
