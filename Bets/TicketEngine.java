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
	
	public static void main(String[] args)
	{
		orderBook = new OrderBook(args[0]);
		
		Gearman gearman = Gearman.createGearman();
		
		GearmanWorker worker = gearman.createGearmanWorker();
		
		GearmanServer server = gearman.createGearmanServer("212.64.153.49", 4730);
		
		worker.addFunction("order", new TicketEngine());
		
		worker.addServer(server);
	}
	
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
