package sw.item;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sw.item.Item;



public class TestItem
{
    @Test
    public void testInitialization()
    {
        Item pack = new MockItem("Belt","Desc",10,5);
        assertEquals("Belt",pack.getName());
        assertEquals("Desc",pack.getDescription());
        assertEquals(10,pack.getSize());
        assertEquals(5,pack.getWeight());
        assertEquals(-1,pack.getItemID());
    }
    
    @Test
    public void testSetItemID()
    {
        Item pack = new MockItem("Belt","Desc",10,5);
        pack.setItemID(4);
        assertEquals(4,pack.getItemID());
    }

}

class MockItem extends Item
{
    public MockItem(String name, String desc,int size, int weight)
    {
        super(name,desc,size,weight);
    }
}
