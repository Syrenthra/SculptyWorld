package sw.item;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to model all containers that can hold items.
 * @author cdgira
 *
 */
public class ItemContainer extends Item
{
    public static final String CAP = "CAPACITY";
    public static final String ITEMS = "ITEMS";
    public static final String WEAR_LOC = "WEAR_LOC";
    /**
     * How much this container can hold in cm^3.
     */
    private int m_capacity;
    
    /**
     * The items held in this container.
     */
    private Vector<Item> m_items = new Vector<Item>();
    
    /**
     * Where can I equip this storage container.  ContainerLocation.BACK is the default value.
     */
    private ContainerLocation m_location = ContainerLocation.BACK;
    
    /**
     * Constructs and ItemContainer.
     * @param name
     * @param desc
     * @param capacity
     */
    public ItemContainer(String name, String desc, int size, int weight, int capacity)
    {
        super(name,desc,size,weight);
        m_capacity = capacity;
        m_type = Item.CONTAINER;
    }

    /**
     * Gets the capacity of the container in cm^3.
     * @return
     */
    public int getMaxCapacity()
    {
        return m_capacity;
    }

    /**
     * Is the location specified a valid place to equip this storage container.
     * @param location
     * @return
     */
    public boolean validLocation(ContainerLocation location)
    {  
        return m_location == location;
    }
    
    /**
     * Adds a valid equip location.
     * @param location
     */
    public void setValidLocation(ContainerLocation location)
    {
        m_location = location;
    }

    /**
     * Returns the total weight of the container, counting its weight and
     * the weight of the items stored in it.
     * @return
     */
    @Override
    public int getWeight()
    {
        int totalWeight = this.m_weight;
        
        for (Item item : m_items)
        {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }

    /**
     * Returns how much space is left to put items in the storage container.
     * @return
     */
    public int getRemainingCapacity()
    {
        int capacity = this.m_capacity;
        for (Item item : m_items)
        {
            capacity -= item.getSize();
        }
        return capacity;
    }

    /**
     * Stores an item in the container if there is space.
     * @param item
     * @return Returns true if the item was able to fit into the container.
     */
    public boolean store(Item item)
    {
        if (item.getSize() <= getRemainingCapacity())
        {
            m_items.add(item);
            return true;
        }
        return false;
        
    }
    

    /**
     * Because the container will expand as it is filled up a container's actual size
     * is the max of it's size or the size of all the items it is holding.
     */
    @Override
    public int getSize()
    {
        int amountStored = 0;
        for (Item item : m_items)
        {
            amountStored += item.getSize();
        }
        return Math.max(amountStored, m_size); 
    }
    
    /**
     * Returns the information about the ItemContainer as well as a list of items it contains.
     * The list of items is represented as a Vector of ints.  If the container contains a container
     * then just the id for that container is stored.
     */
    @Override
    public Hashtable<String,Object> getItemInfo()
    {
        Hashtable<String,Object> data = super.getItemInfo();
        
        data.put(CAP, m_capacity);

        Vector<Integer> items = new Vector<Integer>();
        for (Item item : m_items)
        {
            items.addElement(item.getItemID());   
        }
        data.put(ITEMS, items);
        
        data.put(WEAR_LOC,m_location.name());
        
        return data;
    }
    
    /**
     * 
     * @param data
     * @return
     */
    public static ItemContainer constructItemContainer(Hashtable<String,Object> data)
    {
        String name = (String)data.get(NAME);
        String desc = (String)data.get(DESC);
        int size = (Integer)data.get(SIZE);
        int weight = (Integer)data.get(WEIGHT);
        int capacity = (Integer)data.get(CAP);
        
        ItemContainer item = new ItemContainer(name, desc, size, weight, capacity);
        int id = (Integer)data.get(ID);
        item.setItemID(id);
        
        String loc = (String)data.get(WEAR_LOC);
        item.setValidLocation(ContainerLocation.valueOf(loc));
               
        return item;
    }

    /**
     * 
     * @return All items in the container stored in a Vector.
     */
    public Vector<Item> getItems()
    {
        Vector<Item> items = new Vector<Item>(); // So we can't mess up the actual Vector.
        
        for (Item item : m_items)
        {
            items.add(item);
        }
        
        return items;
    }

    /**
     * Removes the first item from the container whose name matches the string provided.
     * @param string
     * @return The item removed from the container.
     */
    public Item removeItem(String itemName)
    {
        Item item = null;
        for (int x=0;x<m_items.size();x++)
        {
            if (m_items.elementAt(x).getName().equals(itemName))
            {
                item = m_items.remove(x);
                break;
            }
        }
        return item;
    }

    /**
     * Removes theitem from the container whose id matches the id provided.
     * @param i
     * @return The item removed from the container.
     */
    public Item removeItem(int itemID)
    {
        Item item = null;
        for (int x=0;x<m_items.size();x++)
        {
            if (m_items.elementAt(x).getItemID() ==  itemID)
            {
                item = m_items.remove(x);
                break;
            }
        }
        return item;
    }

    /**
     * Returns the location where we can equip this container.
     * @return
     */
    public ContainerLocation getValidLocation()
    {
        return m_location;
    }
    
    /**
     * Makes a copy of this ItemContainer.
     */
    @Override
    public ItemContainer clone()
    {
        return new ItemContainer(m_name, m_description, m_size, m_weight, m_capacity);
    }
}
