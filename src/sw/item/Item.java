package sw.item;

import java.util.Hashtable;


/**
 * Items
 * Every Item has a weight and size.  Size is a representation
 * of cubic centimeters.  Weight is in terms of grams.
 * @author cdgira
 *
 */
public abstract class  Item implements Cloneable
{
    public static final int GENERIC = 1;
    public static final int ARMOR = 2;
    public static final int WEAPON = 3;
    public static final int CONTAINER = 4;
    
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String DESC = "DESC";
    public static final String SIZE = "SIZE";
    public static final String WEIGHT = "WEIGHT";
    public static final String TYPE = "TYPE";
    
    /**
     * The unique id for the item.
     */
    protected int m_id = -1;
    
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
    
    /**
     * What type is this item.
     */
    protected int m_type = GENERIC;
    
    /**
     * 
     * @param name
     * @param desc
     * @param size
     * @param weight
     */
    public Item(String name, String desc, int size, int weight)
    {
        m_name = name;
        m_description = desc;
        m_size = size;
        m_weight = weight;
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
    
    /**
     * Creates the Hashtable of information for the Item.
     * @return
     */
    public Hashtable<String,Object> getItemInfo()
    {
        Hashtable<String,Object> data = new Hashtable<String,Object>();
        
        data.put(ID, m_id);
        data.put(NAME, m_name);
        data.put(DESC, m_description);
        data.put(SIZE, m_size);
        data.put(WEIGHT, m_weight);
        data.put(TYPE, m_type);
        
        return data;
    }

    /**
     * Returns the unique ID of the item.
     * @return
     */
    public int getItemID()
    {
        return m_id;
    }

    /**
     * Sets the unique id for the item.
     * @param id
     */
    public void setItemID(int id)
    {
        m_id = id;   
    }
    
    /**
     * Returns what type the item is.
     * @return
     */
    public int getType()
    {
        return m_type;
    }
    
    /**
     * Compares to basic items using their base information.
     * @param item
     * @return
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean same = false;
        if (obj instanceof Item)
        {
            Item item = (Item)obj;
            if ((item.getName().equals(m_name)) && (item.getType() == m_type) && (item.getDescription().equals(m_description)))
            {
                same = true;
            }
        }
        return same;
    }

}
