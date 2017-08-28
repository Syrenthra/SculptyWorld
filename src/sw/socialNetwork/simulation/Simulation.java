package sw.socialNetwork.simulation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import sw.environment.Exit;
import sw.environment.Room;
import sw.environment.TheWorld;
import sw.environment.Zone;
import sw.item.Weapon;
import sw.lifeform.NPC;
import sw.lifeform.PC;
import sw.quest.SocialQuest;
import sw.socialNetwork.Personality;
import sw.socialNetwork.QuestGenerator;
import sw.socialNetwork.SocialNetworkDecayRates;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author David Abrams
 * 
 * This class runs through a simulation of the SNPCs interacting and then logs the events to a
 * file for later analysis. In order to run, use TestSimulation in the test package. Makes use
 * of the Apache POI API in order to generate an entire Excel workbook directly from the experiment
 * data.
 */
public class Simulation 
{
	private int population;
	private double questSuccessRate;
	private ArrayList<NPC> snpcs;
	private Random rand;
	private PC thePlayer;
	private int totalTurns;
	private int totalExperiments;
	private boolean holesPresent;
	private int networkCohesion; //1: loose, 2: medium, 3: strong
	private int totalDesiredFriendships; //sum of all SNPCs' totalDesiredFriends
	
	private String fileName = "";
	private String fileExt = ".xls";
	private String filePath = "../../Experiment Data/";
	private String subFolder = ""; 
	private OutputStream fstream;
	private HSSFWorkbook wb;
	private int turnsPerExperiment;
	private HSSFSheet dataSheet;
	private HSSFSheet summarySheet;
	private HSSFSheet totalsSheet;
	//need to change this if additional statistics are tracked
	char[] columnsUsedData = {'B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S'};
	String[] columnsUsedTotals = {"B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
			"AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL",};
	String[] columnTitlesData = {"FRs Sent","FRs Received","FRs Accepted","FRs Rejected","GiftQuests Created","FavorQuests Created","ReqFavQuests Created",
			"HomewreckerQuest Created","Total","Quests Completed","Quests Failed","Friendship Created","Friendships Terminated","% Friendships",
			"Social Capital Gained","Social Capital Spend on Quests","SNPCs Became Angry","SNPCs Became Happy"};
	String[] columnTitlesTotals = {"FRs Sent","FRs Sent StdDev","FRs Received","FRs Received StdDev","FRs Accepted","FRs Accepted StdDev","FRs Rejected","FRs Rejected StdDev",
			"GiftQuests Created","GiftQuests Created StdDev","FavorQuests Created","FavorQuests Created StdDev","ReqFavQuests Created","ReqFavQuests Created StdDev",
			"HomewreckerQuest Created","HomewreckerQuest Created StdDev","Total","Total StdDev","Quests Completed","Quests Completed StdDev","Quests Failed","Quests Failed StdDev",
			"Friendships Created","Friendships Created StdDev","Friendships Terminated","Friendships Terminated StdDev","% Friendships","% Friendships StdDev",
			"Social Capital Gained","Social Capital Gained StdDev","Social Capital Spend on Quests","Social Capital Spend on Quests StdDev","SNPCs Became Angry","SNPCs Became Angry StdDev",
			"SNPCs Became Happy","SNPCs Became Happy StdDev"};

	private EventLog perTurn;
	private EventLog total;	
	
	public Simulation(int numSNPCs, int numTurns, int numRepeats, int cohesion, boolean holes, double questSuccessRate, int expNum)
	{
		population = numSNPCs;
		this.questSuccessRate = questSuccessRate;
		totalTurns = numTurns;
		holesPresent = holes;
		networkCohesion = cohesion;
		rand = new Random();
		this.totalExperiments = numRepeats;
		turnsPerExperiment = numTurns + 10;
		
		thePlayer = new PC(0, "Playerrr", "The player.", 50);
		snpcs = new ArrayList<NPC>();
		rand = new Random();
		perTurn = new EventLog();
		total = new EventLog();
		
		//should have the same number of titles as columns used, otherwise the spreadsheet will be messed up
		assert(columnTitlesData.length == this.columnsUsedData.length);
		
		String coh = "";
		if(cohesion == 1)
		{
			coh = "loose";
		}else if(cohesion == 2)
		{
			coh = "medium";
		}else if(cohesion == 3)
		{
			coh = "strong";
		}
		fileName = coh + "_" + holes + "_" + (int)(questSuccessRate * 100);		
		
		try
		{
		    File dataFile = new File(filePath + subFolder + fileName + fileExt);
	    	int num = 1;
		   
		    while(dataFile.exists())
		    {
		    	dataFile = new File(filePath + subFolder + fileName + " - " + num + fileExt);
		    	num++;
		    }
		    
		    dataFile.createNewFile();
		    
		    //fileWriter = new BufferedWriter(new FileWriter(dataFile.getAbsoluteFile(), true));
		    fstream = new FileOutputStream(dataFile.getAbsoluteFile());
		    wb = new HSSFWorkbook();
		    summarySheet = wb.createSheet("summary");
		    totalsSheet = wb.createSheet("totals");
		    dataSheet = wb.createSheet("data");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	private void setUp(int simNumber)
	{
		System.out.println("Setting up simulation:");
		
		System.out.println("	Creating the world...");
		TheWorld world = TheWorld.getInstance(false);
		
		//make a room
		System.out.println("	Making main room...");
		Room mainRoom = new Room(1, "Main room","This is the main room");
		ArrayList<Room> rooms = new ArrayList<Room>();
		rooms.add(mainRoom);
		mainRoom.setZone(Zone.CITY);
		
		//add room to the world
		System.out.println("	Linking main room...");
		world.addRoom(mainRoom);
		
		if(holesPresent)
		{
			//make additional rooms to separate SNPCs into groups so that brokers may connect them
			
			System.out.println("	Making secondary rooms...");
			
			Room north = new Room(2, "Northern room", "The room north of the main room");
			Room south = new Room(3, "Southern room", "The room south of the main room");
			Room east = new Room(4, "Eastern room", "The room east of the main room");
			Room west = new Room(5, "Western room", "The room west of the main room");
			
			north.setZone(Zone.RURAL);
			south.setZone(Zone.RURAL);
			east.setZone(Zone.RURAL);
			west.setZone(Zone.RURAL);
			
			System.out.println("	Linking secondary rooms...");
			
			world.addRoom(north);
			world.addRoom(south);
			world.addRoom(east);
			world.addRoom(west);
			
			System.out.println("	Connecting rooms...");
			
			mainRoom.addExit(north, Exit.NORTH);
			north.addExit(mainRoom, Exit.SOUTH);
			mainRoom.addExit(south, Exit.SOUTH);
			south.addExit(mainRoom, Exit.NORTH);
			mainRoom.addExit(east, Exit.EAST);
			east.addExit(mainRoom, Exit.WEST);
			mainRoom.addExit(west, Exit.WEST);
			west.addExit(mainRoom, Exit.EAST);
			
			rooms.add(north);
			rooms.add(south);
			rooms.add(east);
			rooms.add(west);		
		}
		
		//make the quest generator for the SNPCs
		System.out.println("	Create quest generator...");
		QuestGenerator socialQuestGenerator = QuestGenerator.getInstance();
		socialQuestGenerator.autoAddPlayer(thePlayer);
		if(networkCohesion == 1)
		{
			socialQuestGenerator.setDecayRate(SocialNetworkDecayRates.HIGH);
		}else if(networkCohesion == 2)
		{
			socialQuestGenerator.setDecayRate(SocialNetworkDecayRates.NORMAL);
		}else if(networkCohesion == 3)
		{
			socialQuestGenerator.setDecayRate(SocialNetworkDecayRates.LOW);
		}
		
		//quest generator should listen to all the rooms
		for(Room cur : rooms)
		{
			cur.addRoomObserver(socialQuestGenerator);
		}
		
		//make some items
		System.out.println("	Creating items...");
		Weapon bo = new Weapon("Bo", "A wooden staff - not very dense", 10, 2, 5, 2);
		Weapon shortSword = new Weapon("Short Sword", "An iron short sword - moderately dense", 6, 2, 5, 1);
		Weapon mace = new Weapon("Mace", "A steel mace - very dense", 6, 8, 5, 2);
		
		//put the items in the main room
		System.out.println("	Adding items to room...");
		mainRoom.addItem(bo);
		mainRoom.addItem(shortSword);
		mainRoom.addItem(mace);
		
		//initialize several NPCs
		System.out.println("	Creating SocialNPCs (" + population + ")...");
		NPC currentSNPC;
		int id = 1;
		int numNPCs = 1;
		String name;
		String desc = "Description of this SocialNPC";
		int life = 100;
		int damage = 1;
		int armor = 50;
		int speed = 1;
		
		int startingCapital = 3000;
		int maxDesiredFriends = 10;
		int desiredFriends;
		int maxDesiredCapital = 6000;
		int desiredCapital;
		double control;
		double personability;
		double grumpiness;
				
		Personality pers;
		int roomToPlace = 0;
		
		for(int i = 0; i < population; i++)
		{
			name  = "SocialNPC #" + numNPCs;
			
			/**
			 * According to the experiment design, the following values are randomly chosen:
			 * personability
			 * control
			 * grumpiness
			 * totalDesiredFriends
			 * totalDesiredCapital
			 */
			
			desiredFriends = rand.nextInt(maxDesiredFriends + 1);
			desiredCapital = rand.nextInt((maxDesiredCapital / 1000) + 1) * 1000; //step in units of 1000
			control = ((double)(rand.nextInt(81) + 10)) / 100; //between 0.1 and 0.9
			grumpiness = ((double)(rand.nextInt(81) + 10)) / 100; //between 0.1 and 0.9
			personability = ((double)(rand.nextInt(81) + 10)) / 100; //between 0.1 and 0.9
			
			totalDesiredFriendships += desiredFriends;
			
			assert(control >= 0.1 && control <= 0.9);
			assert(grumpiness >= 0.1 && control <= 0.9);
			assert(personability >= 0.1 && control <= 0.9);
			
			pers = new Personality(control, grumpiness, personability, desiredFriends, desiredCapital);
			
			//System.out.println("		Creating SNPC (" + name + ")");
			currentSNPC = new NPC(id, name, desc, life, damage, armor, speed, pers);

			currentSNPC.setCurrentCapital(startingCapital);
			id++;
			numNPCs++;
			snpcs.add(currentSNPC);
			world.addNPC(currentSNPC);
			
			
			if(holesPresent)
			{
				//if this experiment requires structural holes, then split the SNPCs up
				//evenly between rooms
				
				Room room = rooms.get(roomToPlace);
				
				room.addRoomObserver(currentSNPC);
				room.addNPC(currentSNPC);
				
				//first SNPC in each room is the broker
				if (room.getNPCs().length == 1)
				{
					currentSNPC.setIsBrokerNode(true);
				}
				
				roomToPlace++;
				roomToPlace = roomToPlace % rooms.size();
			}else
			{
				//otherwise, just put them all in the main room
				mainRoom.addRoomObserver(currentSNPC);
				mainRoom.addNPC(currentSNPC);
			}
		}
		
		System.out.println("	Setting up output file...");
		
		createHeaderDataRow(simNumber, dataSheet);
	}
	
	public void runExperiments()
	{				
		for(int i = 0; i < totalExperiments; i++)
		{
			setUp(i + 1);
			runSim(i + 1);
			
			resetSimulation();
		}
		
		createTotalsSheet();
		createSummarySheet();
		
		try
		{
			wb.write(fstream);
			fstream.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void runSim(int simNumber)
	{		
		//start the simulation
		System.out.println("Starting simulation " + simNumber + "...");
		double num;
		long startTimeN = System.nanoTime();
		
		for(int currentTurn = 1; currentTurn <= totalTurns; currentTurn++)
		{			
			for(NPC current: snpcs)
			{				
				current.updateTime("", 0);

				int j = 0;
				while(current.getAvailableQuests().size() > j)
				{
					SocialQuest quest = current.getAvailableQuests().get(j);
					/**
					 * The current SNPCs quests have a chance of being completed based on
					 * the global quest completion rate.
					 */
					num = rand.nextDouble();
					if(num <= questSuccessRate)
					{
						quest.questSuccessful();
						quest.turnInQuest(thePlayer);
					}else
					{
						quest.questFailed();
						quest.turnInQuest(thePlayer);
					}
				}
				processTurnEvents(current.getEvents());
			}			
			createTurnDataRow(simNumber, currentTurn);
			
			perTurn.clearEvents();
		}
		
		long endTimeN = System.nanoTime();
		long durationN = endTimeN - startTimeN;
		
		System.out.println("Elapsed time (sec): " + durationN / 1e9);
		
		printEvents();
		
		createTotalsRow(simNumber);
		
		total.clearEvents();
	}
	
	private void resetSimulation()
	{
		TheWorld.reset();
		QuestGenerator.clear();
		snpcs.clear();
		totalDesiredFriendships = 0;
	}
	
	private void processTurnEvents(ArrayList<SocialNetworkEvent> happenings)
	{
		EventTypes type;
		for(SocialNetworkEvent cur : happenings)
		{
			type = cur.getType();
			switch(type)
			{
				case FRIEND_REQUEST_SENT:
					perTurn.incrementNumFRsSent();
					total.incrementNumFRsSent();
					break;
				case FRIEND_REQUEST_RECIEVED:
					perTurn.incrementNumFRsReceived();
					total.incrementNumFRsReceived();
					break;
				case FRIEND_REQUEST_ACCEPTED:
					perTurn.incrementnumFRsAccepted();
					total.incrementnumFRsAccepted();
					break;
				case FRIEND_REQUEST_REJECTED:
					perTurn.incrementNumFRsRejected();
					total.incrementNumFRsRejected();
					break;
				case QUEST_CREATED_GIFTQUEST:
					perTurn.incrementNumGiftQuestCreated();
					total.incrementNumGiftQuestCreated();
					perTurn.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					total.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					break;
				case QUEST_CREATED_FAVORQUEST:
					perTurn.incrementNumFavorQuestCreated();
					total.incrementNumFavorQuestCreated();
					perTurn.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					total.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					break;
				case QUEST_CREATED_REQFAVQUEST:
					perTurn.incrementNumReqFavQuestCreated();
					total.incrementNumReqFavQuestCreated();
					perTurn.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					total.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					break;
				case QUEST_CREATED_HOMEWRECKER:
					perTurn.incrementNumHomewreckerQuestCreated();
					total.incrementNumHomewreckerQuestCreated();
					perTurn.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					total.incrementCapitalSpentOnQuests(cur.getSpecialInfo());
					break;
				case QUEST_SUCCESSFUL:
					perTurn.incrementNumQuestSuccessful();
					total.incrementNumQuestSuccessful();
					break;
				case QUEST_FAILED:
					perTurn.incrementNumQuestFailed();
					total.incrementNumQuestFailed();
					break;
				case FRIENDSHIP_CREATED:
					perTurn.incrementNumFriendshipsCreated();
					total.incrementNumFriendshipsCreated();
					break;
				case FRIENDSHIP_TERMINATED:
					perTurn.incrementNumFriendshipsFailed();
					total.incrementNumFriendshipsFailed();
					break;
				case CAPITAL_CHANGED:
					perTurn.incrementTotalCapitalGained(cur.getSpecialInfo());
					total.incrementTotalCapitalGained(cur.getSpecialInfo());
					break;
				case MOOD_CHANGE_TO_HAPPY:
					perTurn.incrementNumTimesChangedToHappy();
					total.incrementNumTimesChangedToHappy();
					break;
				case MOOD_CHANGE_TO_ANGRY:
					perTurn.incrementNumTimesChangedToAngry();
					total.incrementNumTimesChangedToAngry();
					break;
			}
			
			cur.read();
		}
	}
	

	/**
	 * Creates a row to send export to the CSV file being created. Order of items in the entry:
	 * FRs sent, FRs received, FRs accepted, FRs rejected, GiftQuests created, FavorQuests created,
	 * RequestFavorQuests created, HomewreckerQuests created, quests successful, quests failed,
	 * friendships created, friendships failed, total capital gained, capital spent on quests,
	 * number of SNPCs that became angry, number of SNPCs that became happy.
	 */
	private void createTurnDataRow(int iterationNum, int currentTurn)
	{		
		int offset = (iterationNum - 1) * (10 + totalTurns);
		int currentRow = offset + 7 + currentTurn;
		int startRow = offset + 8;
		HSSFRow rowObj = dataSheet.createRow(currentRow - 1);
		
		rowObj.createCell(0).setCellValue(currentTurn);
		rowObj.createCell(1).setCellValue(perTurn.getNumFRsSent());
		rowObj.createCell(2).setCellValue(perTurn.getNumFRsReceived());
		rowObj.createCell(3).setCellValue(perTurn.getNumFRsAccepted());
		rowObj.createCell(4).setCellValue(perTurn.getNumFRsRejected());
		rowObj.createCell(5).setCellValue(perTurn.getNumGiftQuestCreated());
		rowObj.createCell(6).setCellValue(perTurn.getNumFavorQuestCreated());
		rowObj.createCell(7).setCellValue(perTurn.getNumReqFavQuestCreated());
		rowObj.createCell(8).setCellValue(perTurn.getNumHomewreckerQuestCreated());
		rowObj.createCell(9).setCellFormula("SUM(F" + currentRow + ":I" + currentRow +")");
		rowObj.createCell(10).setCellValue(perTurn.getNumQuestSuccessful());
		rowObj.createCell(11).setCellValue(perTurn.getNumQuestFailed());
		rowObj.createCell(12).setCellValue(perTurn.getNumFriendshipsCreated());
		rowObj.createCell(13).setCellValue(perTurn.getNumFriendshipsFailed());
		rowObj.createCell(14).setCellFormula("(SUM(M" + startRow + ":M" + currentRow + ")-SUM(N" + startRow + ":N" + currentRow + "))/H" + (offset + 1));
		rowObj.createCell(15).setCellValue(perTurn.getTotalCapitalGained());
		rowObj.createCell(16).setCellValue(perTurn.getCapitalSpentOnQuests());
		rowObj.createCell(17).setCellValue(perTurn.getNumTimesChangedToAngry());
		rowObj.createCell(18).setCellValue(perTurn.getNumTimesChangedToHappy());
	}
	
	/**
	 * Creates the summary tab
	 */
	private void createSummarySheet()
	{
		double confidenceLevel = 0.95;
		double conf = 1.0 - confidenceLevel;
				
		HSSFRow rowObj = summarySheet.createRow(0);
		HSSFCell cellObj;
		CellStyle style = wb.createCellStyle();
		style.setDataFormat(wb.createDataFormat().getFormat("0.000%"));
		int startRow = 2;
		int endRow = totalExperiments + 1;
		
		//grab the grand averages and std devs
		int row;
		for(row = 0; row < columnTitlesTotals.length; row += 2)
		{
			String title = columnTitlesTotals[row];
			String col = columnsUsedTotals[row];
			
			rowObj = summarySheet.createRow(row);

			rowObj.createCell(0).setCellValue(title);
			rowObj.createCell(1).setCellFormula("AVERAGE(totals!" + col + startRow + ":" + col + endRow + ")");

			rowObj = summarySheet.createRow(row + 1);
			
			String stdev = "STDEVP(totals!" + col + startRow + ":" + col + endRow + ")";
			rowObj.createCell(0).setCellValue("95% Confidence");
			rowObj.createCell(1).setCellFormula("CONFIDENCE(" + conf + "," + stdev + "," + totalExperiments + ")");


			
			if(rowObj.getCell(0).getStringCellValue().equals("% Friendships"))
			{
				rowObj.getCell(1).setCellStyle(style);
			}
		}
		
		//add in avg network life
		rowObj = summarySheet.createRow(row);
		cellObj = rowObj.createCell(0);
		cellObj.setCellValue("Avg Network Duration");
		cellObj = rowObj.createCell(1);
		cellObj.setCellFormula("AVERAGE(totals!AL" + startRow + ":AL" + endRow + ")");
		
		//add in confidence
		rowObj = summarySheet.createRow(++row);
		cellObj = rowObj.createCell(0);
		cellObj.setCellValue("95% Confidence");
		cellObj = rowObj.createCell(1);
		String stdev = "STDEVP(totals!AL" + startRow + ":AL" + endRow + ")";
		cellObj.setCellFormula("CONFIDENCE(" + conf + "," + stdev + "," + totalExperiments + ")");
		
		//add in % networks that survived the entire experiment
		rowObj = summarySheet.createRow(++row);
		rowObj.createCell(0).setCellValue("% Sims Lasted Full Experiment");
		rowObj.createCell(1).setCellFormula("COUNTIF(totals!AL" + startRow + ":AL" + endRow + ", \"=" + totalTurns + "\") / " + totalExperiments);
		rowObj.getCell(1).setCellStyle(style);
		
		summarySheet.setColumnWidth(0, 390 * 20);
	}
	
	private void createTotalsSheet()
	{
		createTotalsSheetHeaderRow();

		HSSFRow rowObj;
		HSSFCell cellObj;
		
		totalsSheet.getRow(0).getCell(0).setCellValue("Exp Num");
		int colCounter;
		int networkDurationCounter;
		
		for(int rowNum = 1; rowNum <= totalExperiments; rowNum++)
		{
			rowObj = totalsSheet.createRow(rowNum);
			int rowToGrabFrom = (totalTurns + 8) + (turnsPerExperiment * (rowNum - 1)); //the totals row
			colCounter = 0;
			networkDurationCounter = 0;
			
			//these values are for statistics calculated from the data
			int offset = (rowNum - 1) * (10 + totalTurns);
			int startRow = 8 + offset;
			int endRow = totalTurns + 7 + offset;
			int index = 0;
			
			for(int colNum = 0; colNum < columnTitlesTotals.length+1; colNum++)
			{				
				cellObj = rowObj.createCell(colNum);
				
				if(colNum == 0)
				{
					cellObj.setCellValue(rowNum);
				}else if(colCounter < columnsUsedData.length)
				{
					if(!columnTitlesTotals[index].contains("StdDev"))
					{
						//insert totals data
					cellObj.setCellFormula("data!" + columnsUsedData[colCounter] + rowToGrabFrom);
					}else
					{
						//insert stdev calculation
						cellObj.setCellFormula("STDEVP(data!"+columnsUsedData[colCounter] + startRow + ":" + columnsUsedData[colCounter] + endRow +")");
						colCounter++;
					}
					
					index++;
					
				}else
				{
					cellObj.setCellValue("error");
				}
				networkDurationCounter++;
				
//				if(colNum == 0)
//				{
//					cellObj.setCellValue(rowNum);
//				}else if(colCounter < columnsUsed.length && 
//						 index < columnTitlesTotalsSheet.length &&
//						 !columnTitlesTotalsSheet[index].contains("StdDev"))
//				{
//					//insert totals data
//					cellObj.setCellFormula("data!" + columnsUsed[colCounter] + rowToGrabFrom);
//				}
//				else if(colCounter < columnsUsed.length && 
//						index < columnTitlesTotalsSheet.length && 
//						columnTitlesTotalsSheet[index].contains("StdDev"))
//				{
//					cellObj.setCellFormula("STDEVP(data!"+columnsUsed[colCounter] + startRow + ":" + columnsUsed[colCounter] + endRow +")");
//					colCounter++;
//				}else
//				{
//					cellObj.setCellValue("error");
//				}
//				networkDurationCounter++;
			}
			
			//calculate the "network duration" statistic
			cellObj = rowObj.createCell(networkDurationCounter);
			cellObj.setCellFormula("COUNTIF(data!O" + startRow + ":" + "O" + endRow + ", \">0\") + 1");
		
		}
	}
	
	private void createHeaderDataRow(int simNumber, HSSFSheet sheet)
	{
		int curRow = (simNumber - 1) * turnsPerExperiment;
		HSSFRow rowObj = sheet.createRow(curRow);
		
		rowObj.createCell(0).setCellValue("Population size:");
		rowObj.createCell(3).setCellValue(population);
		rowObj.createCell(4).setCellValue("Total desired friendships:");
		rowObj.createCell(7).setCellValue(totalDesiredFriendships);

		curRow++;
		rowObj = sheet.createRow(curRow);

		rowObj.createCell(0).setCellValue("Quest completion rate:");
		rowObj.createCell(3).setCellValue(questSuccessRate);

		curRow++;
		rowObj = sheet.createRow(curRow);

		rowObj.createCell(0).setCellValue("Network cohesion:");

		String cohesion;
		if(networkCohesion == 1)
		{
			cohesion = "LOOSE";
		}else if(networkCohesion == 2)
		{
			cohesion = "MEDIUM";
		}else
		{
			cohesion = "STRONG";
		}

		rowObj.createCell(3).setCellValue(cohesion);

		curRow++;
		rowObj = sheet.createRow(curRow);

		rowObj.createCell(0).setCellValue("Structural holes:");
		rowObj.createCell(3).setCellValue(holesPresent);

		curRow++;
		rowObj = sheet.createRow(curRow);

		rowObj.createCell(0).setCellValue("Run length:");
		rowObj.createCell(3).setCellValue(totalTurns);
		rowObj.createCell(4).setCellValue("Iteration #");
		rowObj.createCell(5).setCellValue(simNumber);

		curRow += 2;
		
		createHeaderLabelsRow(simNumber, sheet, curRow);
	}
	
	private void createHeaderLabelsRow(int simNumber, HSSFSheet sheet, int offset)
	{
		int curRow = offset;
		HSSFRow rowObj = sheet.createRow(curRow);
		
		rowObj.createCell(0).setCellValue("Turn");
		
		for(int colNum = 1; colNum < 19; colNum++)
		{
			rowObj.createCell(colNum).setCellValue(columnTitlesData[colNum - 1]);
		}
	}
	
	private void createTotalsSheetHeaderRow()
	{
		HSSFRow rowObj = totalsSheet.createRow(0);
		
		rowObj.createCell(0).setCellValue("dummy");
		int counter = 0;
		
		for(int colNum = 1; colNum < columnTitlesTotals.length+1; colNum++)
		{
			rowObj.createCell(colNum).setCellValue(columnTitlesTotals[colNum-1]);
			counter++;
		}
		
		rowObj.createCell(counter+1).setCellValue("Network Duration");
	}
	
	
	private void createTotalsRow(int iterationNum)
	{
		int offset = (iterationNum - 1) * (10 + totalTurns);
		int startRow = 8 + offset;
		int endRow = totalTurns + 7 + offset;
		
		HSSFRow rowObj = dataSheet.createRow(endRow);
		HSSFCell cellObj;
		
		cellObj = rowObj.createCell(0);
		cellObj.setCellValue("TOTALS:");
		
		for(int index = 0; index < columnsUsedData.length; index++)
		{
			cellObj = rowObj.createCell(index + 1);
			if(columnsUsedData[index] != 'O')
			{
				cellObj.setCellFormula("SUM(" + columnsUsedData[index] + startRow + ":" + columnsUsedData[index] + endRow + ")");
			}else
			{
				cellObj.setCellFormula("AVERAGE(" + columnsUsedData[index] + startRow + ":" + columnsUsedData[index] + endRow + ")");
			}
		}
	}
	
	private void printEvents()
	{
		int totalQuests = total.getNumGiftQuestCreated() + total.getNumFavorQuestCreated() + 
				total.getNumReqFavQuestCreated() + total.getNumHomewreckerQuestCreated();
		
		System.out.println();
		System.out.println("Summary of event statistics:");
		System.out.println("	Friend Requests sent: " + total.getNumFRsSent());
		System.out.println("	Friend Requests received: " + total.getNumFRsReceived());
		System.out.println("	Friend Requests accepted: " + total.getNumFRsAccepted());
		System.out.println("	Friend Requests rejected: " + (total.getNumFRsRejected()));
		System.out.println("	GiftQuests created: " + total.getNumGiftQuestCreated());
		System.out.println("	FavorQuests created: " + total.getNumFavorQuestCreated());
		System.out.println("	RequestFavorQuests created: " + total.getNumReqFavQuestCreated());
		System.out.println("	HomewreckerQuests created: " + total.getNumHomewreckerQuestCreated());
		System.out.println("	Total SocialQuests created: " + totalQuests);
		System.out.println("	Successful quests: " + total.getNumQuestSuccessful());
		System.out.println("	Failed quests: " + total.getNumQuestFailed());
		System.out.println("	Friendships created: " + total.getNumFriendshipsCreated());
		System.out.println("	Friendships terminated: " + total.getNumFriendshipsFailed());
		System.out.println("	Total social capital generated: " + total.getTotalCapitalGained());
		System.out.println("	Total social capital spent on quests: " + total.getCapitalSpentOnQuests());
		System.out.println("	Average capital gained per SNPC: " + total.getTotalCapitalGained() / population);
		System.out.println("	Average capital gained per turn per SNPC: " + (total.getTotalCapitalGained() / population / totalTurns));
		System.out.println("	Number of times a SNPC became happy: " + total.getNumTimesChangedToHappy());
		System.out.println("	Number of times a SNPC became angry: " + total.getNumTimesChangedToAngry());		
	}
}