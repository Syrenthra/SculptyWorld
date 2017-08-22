package mock;

import sw.lifeform.Lifeform;

public class MockLifeform extends Lifeform
{
    public MockLifeform(String name, String desc, int life)
    {
        super(name,desc,life);
    }

    @Override
    public void attack(Lifeform entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void takeHit(int damage)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTime(String name, int time) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void takeHeal(int magnitude) {
        // TODO Auto-generated method stub
        
    }
}
