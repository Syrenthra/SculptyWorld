package sw.item;

import java.util.Vector;

import sw.quest.FavorQuest;

/**
 * Items
 * Every Item has a weight and size.  Size is a representation
 * of cubic centimeters.  Weight is in terms of grams.
 * @author cdgira
 *
 */
public abstract class Item extends FavorTarget
{
    /**
     * Name of the item.
     */
    protected String m_name;
    /**
     * Description of the item.
     */
    protected String m_description;
    
    /**
     * How much volume the item takes up in cm^3.
     */
    protected int m_size;
    
    /**
     * How much the item weighs in grams.
     */
    protected int m_weight;
    
    
    public Item(String name, String desc, int size, int weight)
    {
        m_name = name;
        m_description = desc;
        m_size = size;
        m_weight = weight;
        quests = new Vector<FavorQuest>();
    }
    
    /**
     * Returns the description of the item.
     * @return
     */
    public String getDescription()
    {
        return m_description;
    }
    
    /**
     * Returns the name of the item.
     * @return
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * 
     * @return The amount of volume the item takes up in cm^3
     */
    public int getSize()
    {

        return m_size;
    }

    /**
     * 
     * @return How much the item weighs in grams.
     */
    public int getWeight()
    {
        return m_weight;
    }

}
