import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobReturn;
import org.gearman.GearmanServer;

public class JobGenerator
{
	public static void main(String[] args) throws InterruptedException
	{
		Gearman gearman = Gearman.createGearman();
		
		GearmanClient client = gearman.createGearmanClient();
		
		GearmanServer server = gearman.createGearmanServer("212.64.153.49", 4730);
		
		client.addServer(server);
		String data = "{ activity: Nederland-Duitsland, type: limit, userid: USER6786, outcome: 2, bet: 87, tickets: 5, bidask: ask, time: 01:02:03:04:04:04 }";
		
			GearmanJobReturn jobReturn = client.submitJob("order", data.getBytes());
		
			while (!jobReturn.isEOF()) 
			{
				// Poll the next job event (blocking operation)
				GearmanJobEvent event = jobReturn.poll();
				switch (event.getEventType()) {
				// success
				case GEARMAN_JOB_SUCCESS: // Job completed successfully
					// print the result
					System.out.println(new String(event.getData()));
					break;
				
				// failure
				case GEARMAN_SUBMIT_FAIL: // The job submit operation failed
				case GEARMAN_JOB_FAIL: // The job's execution failed
					System.err.println(event.getEventType() + ": "
							+ new String(event.getData()));
				}
			}
		
		gearman.shutdown();
	}
}