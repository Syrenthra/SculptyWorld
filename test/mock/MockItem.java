package mock;

import sw.item.Item;

public class MockItem extends Item
{
    public MockItem(String name, String desc, int size, int weight)
    {
        super(name, desc, size, weight);
    }
    
    public MockItem(int size, int weight)
    {
        super("MockGift", "A MockItem used in TestGiftQuest", size, weight);
    }

    public MockItem()
    {
        super("MockItem", "MockItem description", 1, 1);
    }
    
    @Override
    public MockItem clone()
    {
        return new MockItem(m_name,m_description,m_size,m_weight);
    }

}
