package sw.lifeform;

import static org.junit.Assert.assertEquals;

import mock.MockItem;

import org.junit.Test;

import sw.environment.Room;
import sw.item.Item;
import sw.quest.Quest;


public class TestPCEvent
{
    @Test
    public void testInitializeWithCreature()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        Creature creature = new Creature(2,"Dude", "Desc",50,10,5,15);
        PCEvent event = new PCEvent(dude,creature,PCEvent.KILLED_CREATURE);
        assertEquals(dude,event.getPC());
        assertEquals(creature,event.getCreature());
        assertEquals(PCEvent.KILLED_CREATURE,event.getType());
    }
    
    @Test
    public void testInitializeWithText()
    {
        String str = "Said Something Here";
        PC dude = new PC(1,"Dude","Desc",50);
        PCEvent event = new PCEvent(dude,str,PCEvent.SAID_BY);
        assertEquals(dude,event.getPC());
        assertEquals(str,event.getText());
        assertEquals(PCEvent.SAID_BY,event.getType());
    }
    
    @Test
    public void testInitializeWithRooms()
    {
        Room testRoom1 = new Room(1, "Test Room1","This is a room.");
        Room testRoom2 = new Room(2, "Test Room2","This is a room.");
        PC dude = new PC(1,"Dude","Desc",50);
        PCEvent event = new PCEvent(dude,testRoom1,testRoom2,PCEvent.MOVED);
        assertEquals(dude,event.getPC());
        assertEquals(testRoom1,event.getStartRoom());
        assertEquals(testRoom2,event.getDestRoom());
        assertEquals(PCEvent.MOVED,event.getType());
    }
    
    @Test
    public void testInitializeWithItemAndTarget()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        Item lJunk = new MockItem("LJunk","Junk", 0, 0);
        PCEvent event = new PCEvent(dude,bob,lJunk,PCEvent.GIVE_ITEM);
        assertEquals(dude,event.getPC());
        assertEquals(bob,event.getTarget());
        assertEquals(lJunk,event.getItem());
        assertEquals(PCEvent.GIVE_ITEM,event.getType());
    }
    
    @Test
    public void testInitializeWithItem()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        Item lJunk = new MockItem("LJunk","Junk", 0, 0);
        PCEvent event = new PCEvent(dude,lJunk,PCEvent.GET_ITEM);
        assertEquals(dude,event.getPC());
        assertEquals(lJunk,event.getItem());
        assertEquals(PCEvent.GET_ITEM,event.getType());
    }
    
    @Test
    public void testInitializeWithQuest()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        NPC granter = new NPC(1,"Test","Desc",10,5,10,4);
        Quest quest = new Quest("Quest","Description",granter);
        PCEvent event = new PCEvent(dude,quest,PCEvent.NATIVE_QUEST_ADDED);
        assertEquals(dude,event.getPC());
        assertEquals(quest,event.getQuest());
        assertEquals(PCEvent.NATIVE_QUEST_ADDED,event.getType());
    }

}
