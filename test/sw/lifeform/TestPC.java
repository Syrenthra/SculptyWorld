package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import mock.MockItem;
import mock.MockPCObserver;
import mock.MockTask;

import org.junit.Test;

import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.TheWorld;
import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.lifeform.Creature;
import sw.lifeform.Party;
import sw.lifeform.PC;
import sw.quest.Quest;
import sw.quest.QuestState;

public class TestPC
{

    @Test
    public void testInitialize()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        assertEquals("Dude",dude.getName());
        assertEquals("Desc",dude.getDescription());
        assertEquals(14,dude.getStrength());
        assertEquals(10,dude.getConstitution());
        assertEquals(10,dude.getDexterity());
        assertEquals(10,dude.getWisdom());
        assertEquals(10,dude.getIntelligence());
        assertEquals(10,dude.getCharisma());
        assertEquals(0,dude.getGold());
        assertEquals(0,dude.getXP());
        assertNull(dude.getContentsInHand(HandLocation.RIGHT));
        assertNull(dude.getContentsInHand(HandLocation.LEFT));
        assertNull(dude.getContentsInHand(HandLocation.BOTH));
        assertNull(dude.getContainer(ContainerLocation.BACK));
        assertNull(dude.getContainer(ContainerLocation.BELT));
        assertNotNull(dude.getParty());
        assertEquals(dude,dude.getParty().getPlayer(0));
    }
    
    @Test
    public void testUpdateXP()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        dude.updateXP(50);
        assertEquals(50,dude.getXP());
        dude.updateXP(-25);
        assertEquals(25,dude.getXP());
        // XP won't go below 0
        dude.updateXP(-30);
        assertEquals(0,dude.getXP());
    }
    
    @Test
    public void testUpdateGold()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        dude.updateGold(50);
        assertEquals(50,dude.getGold());
        dude.updateGold(-25);
        assertEquals(25,dude.getGold());
        // Gold won't go below 0, if a change would cause it to, the change is refused.
        dude.updateGold(-30);
        assertEquals(25,dude.getGold());
    }
    
    @Test
    public void testWearArmor()
    {
        Armor armor = new Armor("Plate","Ugly",20,50,ArmorLocation.BODY,30);
        PC dude = new PC(1,"Dude","Desc",50);
        assertTrue(dude.wearArmor(armor));
        assertEquals(armor,dude.getArmor(ArmorLocation.BODY));
        
        Armor leather = new Armor("Leather","Ugly",20,50,ArmorLocation.BODY,30);
        assertFalse(dude.wearArmor(leather));
        assertEquals(armor,dude.getArmor(ArmorLocation.BODY));
    }
    
    @Test
    public void testRemoveArmor()
    {
        Armor armor = new Armor("Plate","Ugly",20,50,ArmorLocation.BODY,30);
        PC dude = new PC(1,"Dude","Desc",50);
        dude.wearArmor(armor);
        Armor removed = dude.removeArmor(ArmorLocation.BODY);
        assertEquals(armor,removed);
    }
    
    @Test
    public void testHoldingAWeapon()
    {
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        PC dude = new PC(1,"Dude","Desc",50);
        
        // Hold weapon in right hand.
        assertTrue(dude.holdInHand(weapon, HandLocation.RIGHT));
        assertEquals(weapon,dude.getWeapon(HandLocation.RIGHT));
        
        // Hold weapon in left hand
        assertTrue(dude.holdInHand(weapon, HandLocation.LEFT));
        assertEquals(weapon,dude.getWeapon(HandLocation.LEFT));
        
        dude.dropFromHand(HandLocation.RIGHT);
        dude.dropFromHand(HandLocation.LEFT);
        
        // Holding a two handed weapon
        Weapon twoHands = new Weapon("Sword","A Sword",10,5,20,2);
        assertFalse(dude.holdInHand(twoHands,HandLocation.RIGHT));
        assertNull(dude.getWeapon(HandLocation.RIGHT));
        assertNull(dude.getWeapon(HandLocation.LEFT));
        
        assertFalse(dude.holdInHand(twoHands,HandLocation.LEFT));
        assertNull(dude.getWeapon(HandLocation.RIGHT));
        assertNull(dude.getWeapon(HandLocation.LEFT));
        
        assertTrue(dude.holdInHand(twoHands,HandLocation.BOTH));
        assertNull(dude.getWeapon(HandLocation.LEFT));
        assertNull(dude.getWeapon(HandLocation.RIGHT));
        assertEquals(twoHands,dude.getWeapon(HandLocation.BOTH));
    }
    
    @Test
    public void testPCObserverReportKill()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        MockPCObserver observer = new MockPCObserver();
        dude.addPCObserver(observer);
        dude.killed(creature);
        assertEquals(PCEvent.KILLED_CREATURE,observer.m_event.getType());
        assertEquals(creature,observer.m_event.getCreature());
    }
    
    @Test
    public void testPCObserverReportGotItem()
    {
        PC dude = new PC(1,"Dude","Desc",50);
        Item lJunk = new MockItem("LJunk","Junk",10,10);
        MockPCObserver observer = new MockPCObserver();
        dude.addPCObserver(observer);
        dude.holdInHand(lJunk, HandLocation.RIGHT);
        assertEquals(PCEvent.GET_ITEM,observer.m_event.getType());
        assertEquals(lJunk,observer.m_event.getItem());
    }
    
    @Test
    public void testPCObserverReportsMoved()
    {
        TheWorld test = TheWorld.getInstance();
        Room room1 = new Room(1,"Tree","Forest");
        test.addRoom(room1);
        Room room2 = new Room(2,"Tree","Forest");
        test.addRoom(room2);
        room1.addExit(room2,Exit.EAST);
        
        PC dude = new PC(1,"Dude","Desc",50);
        room1.addPC(dude);
        
        MockPCObserver observer = new MockPCObserver();
        dude.addPCObserver(observer);
        
        test.movePlayer(dude, Exit.EAST);
        
        assertEquals(PCEvent.MOVED,observer.m_events.elementAt(0).getType());
        assertNull(observer.m_events.elementAt(0).getDestRoom());
        assertEquals(room1,observer.m_events.elementAt(0).getStartRoom());
        
        assertEquals(PCEvent.MOVED,observer.m_events.elementAt(1).getType());
        assertEquals(room2,observer.m_events.elementAt(1).getDestRoom());
        assertNull(observer.m_events.elementAt(1).getStartRoom());
    }
    
    @Test
    public void testGiveItemToNPC()
    {
        Item lJunk = new MockItem("LJunk","Junk",10,10);
        PC dude = new PC(1,"Dude","Desc",50);
        MockPCObserver observer = new MockPCObserver();
        dude.addPCObserver(observer);
        
        NPC bob = new NPC(0, "Bob", "He wears overalls.", 50, 5, 10, 1);
        NPC fred = new NPC(0, "Fred", "He wears pants.", 50, 5, 10, 1);
        bob.addAcceptablePersonalItem(dude, lJunk);
        
        // This should work because dude does have the item and bob wants it from dude.
        dude.holdInHand(lJunk, HandLocation.LEFT);
        
        assertEquals(PCEvent.GET_ITEM,observer.m_events.elementAt(0).getType());
        
        dude.giveItemInHand(bob,HandLocation.LEFT);
        
        assertEquals(lJunk,bob.getPersonalItem(0));
        
        assertEquals(PCEvent.GIVE_ITEM,observer.m_events.elementAt(1).getType());

        // This should not work since we have nothing in our hand now.
        dude.giveItemInHand(fred,HandLocation.LEFT);
        
        assertNull(fred.getPersonalItem(0));
        assertEquals(2,observer.m_events.size());
        
    }
    
    @Test
    public void testGetHeldItem()
    {
        Item rJunk = new MockItem("RJunk","Junk",10,10);
        Item lJunk = new MockItem("LJunk","Junk",10,10);
        PC dude = new PC(1,"Dude","Desc",50);
        
        // Hold weapon in right hand.
        assertTrue(dude.holdInHand(rJunk, HandLocation.RIGHT));
        assertEquals(rJunk,dude.getHeldItem(HandLocation.RIGHT));
        
        // Hold weapon in left hand
        assertTrue(dude.holdInHand(lJunk, HandLocation.LEFT));
        assertEquals(lJunk,dude.getHeldItem(HandLocation.LEFT));
 
        dude = new PC(1,"Dude","Desc",50);
        
        // Holding a two handed weapon
        Weapon twoHands = new Weapon("Sword","A Sword",10,5,20,2);
        assertFalse(dude.holdInHand(twoHands,HandLocation.RIGHT));
        assertNull(dude.getWeapon(HandLocation.RIGHT));
        assertNull(dude.getWeapon(HandLocation.LEFT));
        
        assertFalse(dude.holdInHand(twoHands,HandLocation.LEFT));
        assertNull(dude.getWeapon(HandLocation.RIGHT));
        assertNull(dude.getWeapon(HandLocation.LEFT));
        
        assertTrue(dude.holdInHand(twoHands,HandLocation.BOTH));
        assertNull(dude.getWeapon(HandLocation.LEFT));
        assertNull(dude.getWeapon(HandLocation.RIGHT));
        assertEquals(twoHands,dude.getWeapon(HandLocation.BOTH));
    }
    
    @Test
    public void testEquipContainer()
    {
        PC player = new PC(1,"Dude","Desc",50);
        
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        
        assertTrue(player.equipContainer(storage));
        assertEquals(storage,player.getContainer(ContainerLocation.BACK));
        
        ItemContainer storage2 = new ItemContainer("Bag","A Bag",100, 5,100);
        storage2.setValidLocation(ContainerLocation.BACK);
        assertFalse(player.equipContainer(storage));
        assertEquals(storage,player.getContainer(ContainerLocation.BACK));
    }
    
    @Test
    public void testStoreItemsInAContainer()
    {
        PC player = new PC(1,"Dude","Desc",50);
        
        ItemContainer storage = new ItemContainer("Bag","A Bag",5, 5,100);
        storage.setValidLocation(ContainerLocation.BELT);
        player.equipContainer(storage);
        
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        
        assertFalse(player.storeItem(weapon,ContainerLocation.BACK));
        assertEquals(5,storage.getWeight());
        assertTrue(player.storeItem(weapon,ContainerLocation.BELT));
        assertEquals(10,storage.getWeight());
    }
    
    @Test
    public void testRemoveContainer()
    {
        PC player = new PC(1,"Dude","Desc",50);
        
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        storage.setValidLocation(ContainerLocation.BACK);
        player.equipContainer(storage);
        assertEquals(storage,player.getContainer(ContainerLocation.BACK));
        assertEquals(storage,player.removeContainer(ContainerLocation.BACK));
        assertNull(player.getContainer(ContainerLocation.BACK));
    }
    
    @Test
    public void testTakeHit()
    {
        // No Armor
        PC dude = new PC(1,"Dude","Desc",50);
        dude.takeHit(10);
        assertEquals(40,dude.getCurrentLifePoints());
        
        // With Armor
        Armor armor = new Armor("Plate","Ugly",20,50,ArmorLocation.BODY,30);
        dude.wearArmor(armor);
        dude.takeHit(40);
        assertEquals(30,dude.getCurrentLifePoints());
    }
    
    @Test
    public void testAttack()
    {
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        PC dude = new PC(1,"Dude","Desc",50);
        dude.holdInHand(weapon,HandLocation.RIGHT);
        PC dude2 = new PC(2,"Dude2","Desc2",50);
        dude.attack(dude2);
        assertEquals(30,dude2.getCurrentLifePoints());
    }
    
    @Test
    public void testCanLeaveAParty()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        Party myParty1 = player1.getParty();
        
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty2 = player2.getParty();
        
        myParty1.mergeParties(myParty2);
        
        player1.leaveParty();
        
        assertEquals(1,player1.getParty().getPlayers().size());
        assertEquals(1,player2.getParty().getPlayers().size());
        assertEquals(myParty1,player2.getParty());
    }
    
    @Test
    public void testCanGiveAndRemoveNativeQuest()
    {
        Quest quest = new Quest("Name","Desc",null);
        PC player = new PC(1,"Dude","Desc",50);
        player.assignNativeQuest(quest);
        assertEquals(quest,player.getNativeQuest(0));
        assertEquals(QuestState.IN_PROGRESS,quest.getCurrentState(player));
        player.removeNativeQuest(quest);
        assertNull(player.getNativeQuest(0));
        
        //Can't get a quest if quest is Completed or failed.
        quest.setCurrentState(QuestState.COMPLETED);
        player.assignNativeQuest(quest);
        assertNull(player.getNativeQuest(0));
        
        quest.setCurrentState(QuestState.FAILED);
        player.assignNativeQuest(quest);
        assertNull(player.getNativeQuest(0));
        
    }
    
    @Test
    public void testCanUpdateQuests()
    {
        Quest quest1 = new Quest("Name","Desc",null);
        MockTask task1 = new MockTask();
        quest1.addTask(task1);
        Quest quest2 = new Quest("Name","Desc",null);
        MockTask task2 = new MockTask();
        quest2.addTask(task2);
        Quest quest3 = new Quest("Name","Desc",null);
        PC player = new PC(1,"Dude","Desc",50);
        player.assignNativeQuest(quest1);
        task1.setComplete(player, 75);
        player.updateQuests(quest2); // Want this added to the InheritedQuest List.
        task2.setComplete(player, 85);
        // If quest is not native quest add to inherited
        player.updateQuests(quest3);
        assertEquals(quest3,player.getInheritedQuest(1));
        // If quest is a native quest don't add
        player.updateQuests(quest1);
        assertEquals(1,player.getNativeQuests().size());
        assertEquals(75,task1.percentComplete(player));
        // If quest is already inherited and active don't add
        player.updateQuests(quest2);
        assertEquals(2,player.getInheritedQuests().size());
        assertEquals(85,task2.percentComplete(player));
        // If quest is already inherited and inactive make active
        quest2.setCurrentState(player,QuestState.INACTIVE);
        player.updateQuests(quest2);
        assertEquals(QuestState.IN_PROGRESS,quest2.getCurrentState(player));
    }
    
    @Test
    public void testCanGiveAndRemoveInheritedQuest()
    {
        Quest quest = new Quest("Name","Desc",null);
        PC player = new PC(1,"Dude","Desc",50);
        player.updateQuests(quest); // How inherited quests are added.
        assertEquals(quest,player.getInheritedQuest(0));
        player.removeInheritedQuest(quest);
        assertNull(player.getInheritedQuest(0));
    }
    
    @Test
    public void testPlayersInSamePartyAllAssignedQuest()
    {
        PC player1 = new PC(1,"Dude1","Desc",50);
        PC player2 = new PC(2,"Dude2","Desc",50);
        Party myParty1 = player1.getParty();
        myParty1.mergeParties(player2.getParty());
        NPC granter = new NPC(99,"Test","Desc",10,10,10,5);
        Quest quest = new Quest("Quest","Description",granter);
        player1.assignNativeQuest(quest);
        assertEquals(null,player1.getNativeQuest(1));
        assertEquals(null,player2.getNativeQuest(1));
        assertEquals(quest,player1.getNativeQuest(0));
        assertEquals(quest,player2.getNativeQuest(0));
    }
    
    
    @Test
    public void testCanHoldArmorAndContainersItems()
    {
        PC player = new PC(1,"Dude","Desc",50);
        
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        player.holdInHand(storage,HandLocation.RIGHT);
        assertEquals(storage, player.getContentsInHand(HandLocation.RIGHT));
        assertNull(player.getWeapon(HandLocation.RIGHT));
        
        
        Armor armor = new Armor("Plate","Ugly",20,50,ArmorLocation.BODY,30);
        player.holdInHand(armor,HandLocation.LEFT);
        assertEquals(armor, player.getContentsInHand(HandLocation.LEFT));
        assertNull(player.getWeapon(HandLocation.LEFT));
  
        Item item = player.dropFromHand(HandLocation.RIGHT);
        assertEquals(storage,item);
        assertNull(player.getContentsInHand(HandLocation.RIGHT));
        
        item = player.dropFromHand(HandLocation.LEFT);
        assertEquals(armor,item);
        assertNull(player.getContentsInHand(HandLocation.LEFT));
    }
    
    @Test
    public void testGetPlayerInfo()
    {
        PC dude = new PC(1, "Dude", "Desc", 50);
        // Add a Weapon.
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        weapon.setItemID(2);
        dude.holdInHand(weapon, HandLocation.LEFT);
        // Add Armor
        Armor armor = new Armor("Plate","Ugly",20,50,ArmorLocation.BODY,30);
        armor.setItemID(3);
        dude.wearArmor(armor);
        // Add a container.
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        storage.setItemID(4);
        storage.setValidLocation(ContainerLocation.BACK);
        dude.equipContainer(storage);
        ItemContainer storage2 = new ItemContainer("Bag","A Bag",100, 5,100);
        storage2.setItemID(9);
        storage2.setValidLocation(ContainerLocation.BELT);
        dude.equipContainer(storage2);
        
        Hashtable<String,Object> data = dude.getLifeformInfo();
        assertEquals(1,data.get(Item.ID));
        assertEquals("Dude",data.get(Lifeform.NAME));
        assertEquals("Desc",data.get(Lifeform.DESC));
        assertEquals(50,data.get(Lifeform.MAX_LIFE));
        assertEquals(50,data.get(Lifeform.CURRENT_LIFE));
        assertEquals(0,data.get(PC.GOLD));
        assertEquals(0,data.get(PC.XP));
        Vector<Integer> armorTable = (Vector<Integer>)data.get(PC.ARMOR);
        int armorID = armorTable.elementAt(0);
        assertEquals(3,armorID);
        Hashtable<String,Integer> heldTable = (Hashtable<String,Integer>)data.get(PC.HELD);
        int itemID = heldTable.get("LEFT");
        assertEquals(2,itemID);
        Vector<Integer> containers = (Vector<Integer>)data.get(PC.CONTAINERS);
        assertEquals(2,containers.size());
        int container = containers.elementAt(0);
        assertTrue(container == 4 || container == 9);
        container = containers.elementAt(1);
        assertTrue(container == 4 || container == 9);
    }
   
    
    @Test
    public void testGetPlayerInfoWithPCInAParty()
    {
        assertTrue(false);
    }
    
    @Test
    public void testGetPlayerInfoWithPCWithQuests()
    {
        assertTrue(false);
    }
    
    @Test
    public void testGetPlayerInfoWithPCWithActionQueue()
    {
        assertTrue(false);
    }
    
    /**
     * We need to fix how we store enum values into the database.
     */
    @Test
    public void testConstructPlayer()
    {
        PC dude = new PC(1, "Dude", "Desc", 50);
        Hashtable<String,Object> data = dude.getLifeformInfo();
        PC newDude = PC.constructPC(data);
        assertEquals("Dude", newDude.getName());
        assertEquals("Desc", newDude.getDescription());
        assertEquals(50,newDude.getCurrentLifePoints());
    }
    
}
