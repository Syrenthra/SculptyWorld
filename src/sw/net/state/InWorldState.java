package sw.net.state;


import java.util.Hashtable;

import sw.environment.Room;
import sw.environment.RoomObserver;
import sw.environment.RoomUpdateType;
import sw.environment.TheWorld;
import sw.environment.commands.AcceptCommand;
import sw.environment.commands.CommandResult;
import sw.environment.commands.DropCommand;
import sw.environment.commands.GetCommand;
import sw.environment.commands.InWorldCommand;
import sw.environment.commands.JoinCommand;
import sw.environment.commands.LeaveCommand;
import sw.environment.commands.MoveCommand;
import sw.environment.commands.SayCommand;

import sw.lifeform.Creature;
import sw.lifeform.PC;
import sw.net.SWServerConnection;
import sw.net.msg.SWMessage;


/**
 * Once a player has joined the game they are in this state until they quit the game.
 * 
 * TODO: At present just supports a demo player for Too Many Games.
 * 
 * @author cdgira
 *
 */
public class InWorldState extends ServerConnectionState implements RoomObserver
{
    /**
     * Which player this state is attached to.
     */
    int m_userID;
    
    /**
     * The character the player is actively using.
     */
    PC m_character;
    
    Hashtable<String,InWorldCommand> m_availableCommands = new Hashtable<String,InWorldCommand>();
    
    /**
     * 
     * @param connection
     * @param id
     * @param character
     */
    public InWorldState(SWServerConnection connection, int id, PC character)
    {
        m_connection = connection;
        m_userID = id;
        m_character = character;
        constructCommandTable();
        // TODO: Wonder if we need to test this for nulls
        m_message = m_character.getCurrentRoom().toString();
        m_character.getCurrentRoom().addRoomObserver(this);
    }
    
    /**
     * Load in all the possible actions a player can take.  Design based on the Command Pattern.
     */
    private void constructCommandTable()
    {
        MoveCommand mc = new MoveCommand(this);
        m_availableCommands.put("west", mc);
        m_availableCommands.put("east", mc);
        m_availableCommands.put("north", mc);
        m_availableCommands.put("south", mc);
        
        GetCommand gc = new GetCommand();
        m_availableCommands.put("get", gc);
        
        DropCommand dc = new DropCommand();
        m_availableCommands.put("drop", dc);
        
        SayCommand sc = new SayCommand();
        m_availableCommands.put("say", sc);
        
        JoinCommand jc = new JoinCommand();
        m_availableCommands.put("join", jc);
        
        AcceptCommand ac = new AcceptCommand();
        m_availableCommands.put("accept",ac);
        
        LeaveCommand lvc = new LeaveCommand();
        m_availableCommands.put("leave", lvc);
        
    }

    /**
     * TODO: Should probably put all command processing in a synchronized method?
     * Commands:
     * TODO: How many directions of movement to allow?
     * west or w
     * east or e
     * north or n
     * south or s
     * up or u
     * down or d
     * 
     * inventory or i
     * look or l
     * stats -> Lists your stats and skills
     * who -> Lists who is online
     * help -> Calls up the help system
     * 
     * TODO: Implement the concept of walk points?
     * sleep -> Character gets sleep (can be surprised attacked)
     * rest -> Character sits and rests
     * stand -> Character wakes up and stands.
     * 
     * TODO: How hard to make it to work with items?
     * get <item> -> picks up an item on the ground (must have a free hand)
     * drop <tem> -> drops and item to the ground
     * get <object> <item> -> gets and item out of a container (must have a free hand)
     * equip <item> -> Equips a piece of armor, weapon or container.
     * remove <item> -> removes a piece of equipment (again hand must be free).
     * 
     * TODO: We need an economic system that doesn't have infinite money.
     * sell <item> -> sells and item to a shop keeper (if one in same room)
     * buy <item> -> buys and item from a shop keeper
     * list -> Lists what a shop keeper has for sale
     * appraise <item> -> find out what an item is worth to the shop keeper
     * 
     * TODO: Do we want a food and water system?
     * eat <item> -> Eats a food
     * drink <item> -> Drinks from a water container
     * fill <item> <from> -> Fills a water container from the source provided
     * 
     * kill <creature> or k <creature>
     * use <skill> <target>-> Use target based skill
     * use <skill> -> Use non-target based skill
     * 
     * party -> Lists who is in your party
     * join <name of party leader> -> Asks to join/merge a party.
     * accept <player name> -> Accepts the join request
     * leave -> Leave the party (if the party leader then second person to join is the new leader)
     * 
     * logout -> Saves the present location of the character and quits the game
     * 
     */
    @Override
    public void executeAction(SWMessage msg)
    {
        TheWorld world = TheWorld.getInstance();
        // TODO: Nasty problem with handing off control from one room to the next on moves.
        String[] commands = msg.getMessage().split(" ");
        String command = commands[0].trim().toLowerCase();
        InWorldCommand action = m_availableCommands.get(command);
        if (action != null)
        {
            Room oldRoom = m_character.getCurrentRoom();
            action.processCommand(m_character, msg.getMessage());
            if (action instanceof MoveCommand)
            {
                Room newRoom = m_character.getCurrentRoom();
            }
        }
        else if (msg.getMessage().equals("quests"))
        {
            SWMessage reply = null;
            Room room = world.getPlayer(1).getCurrentRoom();
            if ((room.getNumNPCs() > 0) && (room.getNPCs()[0].getAvailableQuests().size() > 0))
                reply = new SWMessage(room.getNPCs()[0].getAvailableQuests().get(0).getName());
            else
                reply = new SWMessage("None");
            m_connection.sendMessage(reply); 
        }
        else if (msg.getMessage().equals("clear creatures"))
        {
            for (int x=1;x<8;x++)
            {
                Room room = world.getRoom(x);
                while (room.getNumCreatures() > 0)
                    room.removeCreature(room.getCreatures()[0].getID());
            }
        }
    }

    @Override
    public SWMessage getMessage()
    {
        SWMessage msg = new SWMessage(m_message);
        return msg;
    }

    /**
     * @return Returns the id of the User this InWorldState is tied to.
     */
    public int getUserID()
    {
        return -1;
    }

    public PC getCharacter()
    {
        return null;
    }

    /**
     * Provides the room the event occurred in, the source of the interaction (e.g. creature)
     * and the type of action (e.g. added).  Will create a message to be sent to the player based
     * on what occurred.
     * 
     * TODO: Refactoring of duplicate code to be done here - this method is getting way too big.
     * 
     */
    @Override
    public void roomUpdate(Room room, Object source, RoomUpdateType type)
    {
        if (type == RoomUpdateType.MOVE)
        {
            CommandResult data = (CommandResult)source;
            if ((m_character == data.getSource()) && (data.getSource().getCurrentRoom() != room))
            {
                room.removeRoomObserver(this);
                
                if ((data.getSource() == m_character) && (data.getMsgForSource().length() > 0))
                {
                    SWMessage msg = new SWMessage(data.getMsgForSource());
                    this.m_connection.sendMessage(msg);
                }
                else if ((data.getTarget() == m_character) && (data.getMsgForTarget().length() > 0))
                {
                    SWMessage msg = new SWMessage(data.getMsgForTarget());
                    this.m_connection.sendMessage(msg);
                }
                else if (data.getMsgForOthers().length() > 0)
                {
                    SWMessage msg = new SWMessage(data.getMsgForOthers());
                    this.m_connection.sendMessage(msg);
                }
            }
            
        }
        
        if (room == m_character.getCurrentRoom())
        {
            
            if (type == RoomUpdateType.ACCEPT_REQUEST)
            {
                CommandResult data = (CommandResult)source;
                if (data.getSource() == m_character)
                {
                    SWMessage msg = new SWMessage(data.getMsgForSource());
                    this.m_connection.sendMessage(msg);
                }
                else if (data.getTarget() == m_character)
                {
                    SWMessage msg = new SWMessage(data.getMsgForTarget());
                    this.m_connection.sendMessage(msg);
                }
                // TODO If Other party member not in room then won't get message.
                else if (data.getSource() == m_character.getParty().getPartyLeader())
                {
                    SWMessage msg = new SWMessage(data.getMsgForOthers());
                    this.m_connection.sendMessage(msg);
                }
            }
            else if (type == RoomUpdateType.CREATURE_ADDED)
            {
                Creature creature = (Creature) source;
                SWMessage msg = new SWMessage("A "+creature.getName()+ " appears in the area.");
                this.m_connection.sendMessage(msg);
            }
            else if (type == RoomUpdateType.CREATURE_RESOURCE_ADDED)
            {
                // No message, players should normally not be aware this has occurred.

            }
            else if (type == RoomUpdateType.CREATURE_RESOURCE_REMOVED)
            {
                // No message, players should normally not be aware this has occurred.
            }
            else if (type == RoomUpdateType.LEAVE_REQUEST)
            {
                CommandResult data = (CommandResult)source;
                if (data.getSource() == m_character)
                {
                    SWMessage msg = new SWMessage(data.getMsgForSource());
                    this.m_connection.sendMessage(msg);
                }
                else if (data.getTarget() == m_character)
                { 
                    SWMessage msg = new SWMessage(data.getMsgForTarget());
                    this.m_connection.sendMessage(msg);
                } 
                else if (data.getTarget() == m_character.getParty().getPartyLeader())
                { 
                    SWMessage msg = new SWMessage(data.getMsgForOthers());
                    this.m_connection.sendMessage(msg);
                } 
                
            }
            else if (source instanceof CommandResult)  // Default way to handle most CommandResults
            {
                CommandResult data = (CommandResult)source;
                if ((data.getSource() == m_character) && (data.getMsgForSource().length() > 0))
                {
                    SWMessage msg = new SWMessage(data.getMsgForSource());
                    this.m_connection.sendMessage(msg);
                }
                else if ((data.getTarget() == m_character) && (data.getMsgForTarget().length() > 0))
                {
                    SWMessage msg = new SWMessage(data.getMsgForTarget());
                    this.m_connection.sendMessage(msg);
                }
                else if (data.getMsgForOthers().length() > 0)
                {
                    SWMessage msg = new SWMessage(data.getMsgForOthers());
                    this.m_connection.sendMessage(msg);
                }
            }
        }
    }
}
