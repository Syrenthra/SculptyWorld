package sw;

import sw.environment.CreatureResource;
import sw.environment.Exit;
import sw.environment.IdGen;
import sw.environment.Room;
import sw.environment.TheWorld;
import sw.environment.Zone;
import sw.lifeform.Creature;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.quest.Quest;
import sw.quest.task.KillCreatureTask;

public class DemoTestWorld
{
    
    public static boolean ACTIVE = true;
     
    
    public static void constructDemoWorld()
    {   
        TheWorld world = TheWorld.getInstance();
        
        
        // Make some sample rooms - East West Rooms
        Room room1 = new Room(1, "Mountain 1","This is a small mountain.");
        room1.setZone(Zone.MOUNTAIN);
        Room room2 = new Room(2, "Mountain 2","This is a tall mountain.");
        room2.setZone(Zone.MOUNTAIN);
        Room room3 = new Room(3, "Forest 1","This is a small forest.");
        room3.setZone(Zone.FOREST);
        Room room4 = new Room(4, "Forest 2","This is a big forest.");
        room4.setZone(Zone.FOREST);
        Room room5 = new Room(5, "Forest 3","This is a medium forest.");
        room5.setZone(Zone.FOREST);
        Room room6 = new Room(6, "Desert 1","This is a hot desert.");
        room6.setZone(Zone.DESERT);
        Room room7 = new Room(7, "Desert 2","This is a cold desert.");
        room7.setZone(Zone.DESERT);
        
        // North and South Rooms
        Room room8 = new Room(8, "Forest 4","This is a small forest.");
        room8.setZone(Zone.FOREST);
        
     // Dump all this stuff into the world
        world.addRoom(room1);
        world.addRoom(room2);
        world.addRoom(room3);
        world.addRoom(room4);
        world.addRoom(room5);
        world.addRoom(room6);
        world.addRoom(room7);
        world.addRoom(room8);
        
        // Construct some creatures and spawners for the creatures.
        // The creatures are super weak and easy to kill.
        Creature mountainCreature = new Creature(IdGen.getID(),"Mountain Creature","Only lives on mountains",1,0,0,2);
        mountainCreature.addZone(Zone.MOUNTAIN);
        CreatureResource mountainCreatureRes = new CreatureResource(mountainCreature,10,3,4);
        mountainCreatureRes.setAmount(10);
        mountainCreatureRes.setSpecialCreatureRate(0.5);  // If set to 1 there is a danger of infinite creatures.
        
        Creature forestCreature = new Creature(IdGen.getID(),"Forest Creature","Only lives in forests",1,0,0,2);
        forestCreature.addZone(Zone.FOREST);
        CreatureResource forestCreatureRes = new CreatureResource(forestCreature,10,3,4);
        forestCreatureRes.setAmount(10);
        forestCreatureRes.setSpecialCreatureRate(0);
        
        // Construct two NPCs for Quests
        NPC mountainQuestDude = new NPC(1,"Mountain Quest Dude", "I give the mountain creature quest.",100,10,5,15);
        Quest mountainQuest = new Quest("Mountain Quest","Kill mountain creature",mountainQuestDude);
        KillCreatureTask mountainTask = new KillCreatureTask(mountainQuest,mountainCreature,1);
        mountainQuest.addTask(mountainTask);
        mountainQuestDude.addQuest(mountainQuest);
        room1.addRoomObserver(mountainQuestDude);
        room2.addRoomObserver(mountainQuestDude);
        
        
        NPC forestQuestDude = new NPC(2,"Forest Quest Dude", "I give the forest creature quest",100,10,5,15);
        Quest forestQuest = new Quest("Forest Quest","Kill forest creature",forestQuestDude);
        KillCreatureTask forestTask = new KillCreatureTask(forestQuest,forestCreature,1);
        forestQuest.addTask(forestTask);
        forestQuestDude.addQuest(forestQuest);
        room3.addRoomObserver(forestQuestDude);
        room4.addRoomObserver(forestQuestDude);
        room5.addRoomObserver(forestQuestDude);
        
        // Attach the rooms together.
        room1.addExit(room2, Exit.EAST);
        
        room2.addExit(room1, Exit.WEST);
        room2.addExit(room3, Exit.EAST);
        
        room3.addExit(room2, Exit.WEST);
        room3.addExit(room4,Exit.EAST);
        room3.addExit(room8, Exit.NORTH);
        
        room4.addExit(room3, Exit.WEST);
        room4.addExit(room5,Exit.EAST);
        
        room5.addExit(room4, Exit.WEST);
        room5.addExit(room6,Exit.EAST);
        
        room6.addExit(room5, Exit.WEST);
        room6.addExit(room7,Exit.EAST);
        
        room7.addExit(room6, Exit.WEST);
        
        room8.addExit(room3,Exit.SOUTH);
        
        room1.addCreatureResource(mountainCreatureRes);
        mountainCreatureRes.addSpawnObserver(room1);
        
        room4.addCreatureResource(forestCreatureRes);
        forestCreatureRes.addSpawnObserver(room4);
        
        PC demoPlayer = new PC(1, "Demo Player", "Demo Player for Too Many Games 2013", 100);
        
        PC demoPlayer2 = new PC(2, "Demo Player2", "Demo Player2 for Too Many Games 2013", 100);
        
        PC demoPlayer3 = new PC(3, "Demo Player3", "Demo Player3 for Too Many Games 2013", 100);
        
        room7.addPC(demoPlayer);
        room7.addPC(demoPlayer2);
        room7.addPC(demoPlayer3);
        room6.addNPC(forestQuestDude);
        room6.addRoomObserver(forestQuestDude);
        room7.addNPC(mountainQuestDude);
        room7.addRoomObserver(mountainQuestDude);

        world.addPlayer(demoPlayer);
        world.addPlayer(demoPlayer2);
        world.addPlayer(demoPlayer3);
        
        world.addNPC(mountainQuestDude);
        world.addNPC(forestQuestDude);
    }
    
    public static PC getPlayer1()
    {
        return TheWorld.getInstance().getPlayer(1);
    }
    
    public static PC getPlayer2()
    {
        return TheWorld.getInstance().getPlayer(2);
    }
    
    public static PC getPlayer3()
    {
        return TheWorld.getInstance().getPlayer(3);
    }
    
    public static NPC getMountainQuestDude()
    {
        return TheWorld.getInstance().getNPC(1);
    }

}
