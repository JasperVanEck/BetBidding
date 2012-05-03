//import java.io.IOException;
//import java.net.InetSocketAddress;

import org.gearman.Gearman;
import org.gearman.GearmanFunction;
import org.gearman.GearmanWorker;
import org.gearman.GearmanFunctionCallback;
import org.gearman.GearmanServer;

public class TicketEngine implements GearmanFunction 
{
	public static OrderBook orderBook;
	
	/**
	 * Main method serves for initiating gearman worker and orderBook.
	 * What it says above.
	 */
	public static void main(String[] args)
	{
		orderBook = new OrderBook(args[0]);
		
		Gearman gearman = Gearman.createGearman();
		
		GearmanWorker worker = gearman.createGearmanWorker();
		
		GearmanServer server = gearman.createGearmanServer("212.64.153.49", 4730);
		
		String function = args[0] + "_order";
		
		worker.addFunction(function, new TicketEngine());
		
		worker.addServer(server);
	}
	
	/**
	 * This function is the worker of the Engine.
	 * It retrieves jobs from the Gearman Jobserver, which it then starts to process.
	 * It creates a string of the retrieved data. The string is in JSON format.
	 * The string is then used as input for the Ticket constructor, which creates a ticket.
	 * After which it is entered into the orderBook.
	 */ 
	@Override
	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception
	{
		String input = new String(data);
		System.out.printf(input + "\n");
		Ticket ticket = new Ticket(input);
		System.out.printf("Ticket is aangemaakt! \n");
		this.orderBook.processTicket(ticket);
		System.out.printf(ticket.getType() + " verwerkt\n");
		return data;
	}
}
