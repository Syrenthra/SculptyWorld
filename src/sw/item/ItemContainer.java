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
    /**
     * How much this container can hold in cm^3.
     */
    private int m_capacity;
    
    /**
     * The items held in this container.
     */
    private Vector<Item> m_items = new Vector<Item>();
    
    /**
     * Where can I equip this storage container.
     */
    private Hashtable<ContainerLocation,ContainerLocation> m_validLocations = new Hashtable<ContainerLocation,ContainerLocation>();
    
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
        return m_validLocations.containsKey(location);
    }
    
    /**
     * Adds a valid equip location.
     * @param location
     */
    public void addValidLocation(ContainerLocation location)
    {
        m_validLocations.put(location, location);
    }
    
    /**
     * Removes that location as a valid location.
     * @param location
     */
    public void removeValidLocation(ContainerLocation location)
    {
        m_validLocations.remove(location);
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
}
