import org.gearman.Gearman;
import org.gearman.GearmanFunction;
import org.gearman.GearmanWorker;
import org.gearman.GearmanFunctionCallback;
import org.gearman.GearmanServer;

import java.io.*;

public class WriteJobs implements Gearman
{
	BufferedWriter bw;
	
	public static void main(String[] args)
	{
		try {
                        bw = new BufferedWriter(new FileWriter(new File(
                                        "jobs.txt"), true));
                } catch (Exception e) {
                }

		
		
		Gearman gearman = Gearman.createGearman();
		
		GearmanWorker worker = gearman.createGearmanWorker();
		
		GearmanServer server = gearman.createGearmanServer("212.64.153.49", 4730);
		
		worker.addFunction("order", new TicketEngine());
		
		worker.addServer(server);
		
		bw.close();
	}
	
	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception
	{
		String input = new String(data);
		
		
		return data;
	}
}