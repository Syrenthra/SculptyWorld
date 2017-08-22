package sw.lifeform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sw.item.Armor;
import sw.item.ArmorLocation;
import sw.item.ContainerLocation;
import sw.item.HandLocation;
import sw.item.Item;
import sw.item.ItemContainer;
import sw.item.Weapon;
import sw.lifeform.Creature;
import sw.lifeform.Party;
import sw.lifeform.Player;
import sw.quest.CreatureQuest;

public class TestPlayer
{

    @Test
    public void testInitialize()
    {
        Player dude = new Player(1,"Dude","Desc",50);
        assertEquals("Dude",dude.getName());
        assertEquals("Desc",dude.getDescription());
        assertEquals(0,dude.getStrength());
        assertEquals(0,dude.getConstitution());
        assertEquals(0,dude.getDexterity());
        assertEquals(0,dude.getWisdom());
        assertEquals(0,dude.getIntelligence());
        assertEquals(0,dude.getCharisma());
        assertNull(dude.getContentsInHand(HandLocation.RIGHT));
        assertNull(dude.getContentsInHand(HandLocation.LEFT));
        assertNull(dude.getContentsInHand(HandLocation.BOTH));
        assertNull(dude.getContainer(ContainerLocation.BACK));
        assertNull(dude.getContainer(ContainerLocation.BELT));
    }
    
    @Test
    public void testWearArmor()
    {
        Armor armor = new Armor("Plate","Ugly",20,50,ArmorLocation.BODY,30);
        Player dude = new Player(1,"Dude","Desc",50);
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
        Player dude = new Player(1,"Dude","Desc",50);
        dude.wearArmor(armor);
        Armor removed = dude.removeArmor(ArmorLocation.BODY);
        assertEquals(armor,removed);
    }
    
    @Test
    public void testHoldingAWeapon()
    {
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        Player dude = new Player(1,"Dude","Desc",50);
        
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
    public void testEquipContainer()
    {
        Player player = new Player(1,"Dude","Desc",50);
        
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        
        assertFalse(player.equipContainer(storage,ContainerLocation.BELT));
        assertNull(player.getContainer(ContainerLocation.BELT));
        
        storage.addValidLocation(ContainerLocation.BELT);
        assertTrue(player.equipContainer(storage,ContainerLocation.BELT));
        assertEquals(storage,player.getContainer(ContainerLocation.BELT));
    }
    
    @Test
    public void testStoreItemsInAContainer()
    {
        Player player = new Player(1,"Dude","Desc",50);
        
        ItemContainer storage = new ItemContainer("Bag","A Bag",5, 5,100);
        storage.addValidLocation(ContainerLocation.BELT);
        player.equipContainer(storage,ContainerLocation.BELT);
        
        Weapon weapon = new Weapon("Sword","A Sword",10,5,20,1);
        
        assertFalse(player.storeItem(weapon,ContainerLocation.BACK));
        assertEquals(5,storage.getWeight());
        assertTrue(player.storeItem(weapon,ContainerLocation.BELT));
        assertEquals(10,storage.getWeight());
    }
    
    @Test
    public void testRemoveContainer()
    {
        Player player = new Player(1,"Dude","Desc",50);
        
        ItemContainer storage = new ItemContainer("Bag","A Bag",100, 5,100);
        storage.addValidLocation(ContainerLocation.BACK);
        player.equipContainer(storage,ContainerLocation.BACK);
        assertEquals(storage,player.getContainer(ContainerLocation.BACK));
        assertEquals(storage,player.removeContainer(ContainerLocation.BACK));
        assertNull(player.getContainer(ContainerLocation.BACK));
    }
    
    @Test
    public void testTakeHit()
    {
        // No Armor
        Player dude = new Player(1,"Dude","Desc",50);
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
        Player dude = new Player(1,"Dude","Desc",50);
        dude.holdInHand(weapon,HandLocation.RIGHT);
        Player dude2 = new Player(2,"Dude2","Desc2",50);
        dude.attack(dude2);
        assertEquals(30,dude2.getCurrentLifePoints());
    }
    
    @Test
    public void testCanGiveAndRemoveQuest()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        Player player = new Player(1,"Dude","Desc",50);
        player.addQuest(quest);
        assertEquals(quest,player.getQuest(0));
        player.removeQuest(quest);
        assertNull(player.getQuest(0));
    }
    
    @Test
    public void testCanUpdateQuestAsCreaturesKilled()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        Player player1 = new Player(2,"Dude1","Desc",50);
        Party party = new Party(1,player1);
        player1.addQuest(quest);
        quest.addPlayer(player1);
        CreatureQuest playerQuest = (CreatureQuest)player1.getQuest(0);
        assertEquals(0,playerQuest.getCreaturesKilled(player1));
        player1.killed(creature.clone());
        assertEquals(1,playerQuest.getCreaturesKilled(player1));
        player1.killed(creature.clone());
        player1.killed(creature.clone());
        player1.killed(creature.clone());
        player1.killed(creature.clone());
        player1.killed(creature.clone());
        assertEquals(5,playerQuest.getCreaturesKilled(player1));
    }
    
    @Test
    public void testCanHoldArmorAndContainersItems()
    {
        Player player = new Player(1,"Dude","Desc",50);
        
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
    public void testQuestUpdatesProperlyWithTwoPlayers()
    {
        Creature creature = new Creature(1,"Dude", "Desc",50,10,5,15);
        CreatureQuest quest = new CreatureQuest("Name","Desc",creature,5);
        Player player1 = new Player(2,"Dude1","Desc",50);
        Party party1 = new Party(1,player1);
        Player player2 = new Player(3,"Dude2","Desc",50);
        Party party2 = new Party(2,player2);
        player1.addQuest(quest);
        quest.addPlayer(player1);
        player2.addQuest(quest);
        quest.addPlayer(player2);
        player1.killed(creature.clone());
        player2.killed(creature.clone());
        assertEquals(1,quest.getCreaturesKilled(player1));
        assertEquals(1,quest.getCreaturesKilled(player2));
    }
}
