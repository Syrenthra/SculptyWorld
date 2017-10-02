package sw.socialNetwork.simulation;

import org.junit.Test;

import sw.socialNetwork.simulation.Simulation;

/**
 * @author David Abrams
 * 
 * The purpose of this test is to demonstrate the full capabilities of the network.
 */
public class TestSimulation
{

	@Test
	public void testRunOneExperiment()
	{
		Simulation runner = new Simulation(30, 100, 50, 1, false, 0.75, 0);

		runner.runExperiments();
	}

	@Test
	public void testRunFullExperiments()
	{
		boolean holesPresent[] = { false};
		int networkCohesion[] = { 1, 2, 3 };
		double questSuccessRate[] = { 0.5, 0.75, 1.0 };

		int curCohesion;
		int curHoles;
		int curSuccessRate;
		int expNum = 1;
		
		long startTimeN = System.nanoTime();

		for (curCohesion = 0; curCohesion < 3; curCohesion++)
		{
			for (curHoles = 0; curHoles < 1; curHoles++)
			{
				for(curSuccessRate = 0; curSuccessRate < 3; curSuccessRate++)
				{
					Simulation sim = new Simulation(100, 1000, 50, networkCohesion[curCohesion], 
							holesPresent[curHoles], questSuccessRate[curSuccessRate], expNum);
					expNum++;
					
					sim.runExperiments();
				}
			}
		}
		
		long endTimeN = System.nanoTime();
		long durationN = endTimeN - startTimeN;
		
		System.out.println("");
		System.out.println("Total elapsed time (sec): " + durationN / 1e9);
	}

	
	@Test
	public void testStuff()
	{
		Simulation runner = new Simulation(30, 50, 50, 1, false, 1.0, 0);
		
		//runner.excel();
	}
}
