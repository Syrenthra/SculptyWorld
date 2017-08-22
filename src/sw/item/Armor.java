package sw.item;

import java.util.Hashtable;


/**
 * Used to represent all items that can be used to protect players.
 * @author cdgira
 *
 */
public class Armor extends Item
{
    public static final String LOC = "LOC";
    public static final String PROT = "PROT";
    
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
        m_type = Item.ARMOR;
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
    
    /**
     * 
     */
    @Override
    public Hashtable<String,Object> getItemInfo()
    {
        Hashtable<String,Object> data = super.getItemInfo();
        
        data.put(LOC, m_location);
        data.put(PROT, m_protection);
        
        return data;
    }
    

    /**
     * 
     * @param data
     * @return
     */
    public static Armor constructArmor(Hashtable<String,Object> data)
    {
        String name = (String)data.get(NAME);
        String desc = (String)data.get(DESC);
        int size = (Integer)data.get(SIZE);
        int weight = (Integer)data.get(WEIGHT);
        ArmorLocation loc = (ArmorLocation)data.get(LOC);
        int prot = (Integer)data.get(PROT);
        
        Armor item = new Armor(name,desc,size,weight,loc,prot);
        int id = (Integer)data.get(ID);
        item.setItemID(id);
               
        return item;
    }
    
    /**
     * Returns a copy of this piece of armor.
     */
    @Override
    public Armor clone()
    {

        return new Armor(m_name,m_description,m_size,m_weight,m_location,m_protection);
    }
}
