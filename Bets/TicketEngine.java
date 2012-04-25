import java.io.IOException;
import java.net.InetSocketAddress;

import static org.gearman.GearmanJobResult.workSuccessful;

import org.gearman.Gearman;
import org.gearman.GearmanFunction;
import org.gearman.GearmanJob;
import org.gearman.GearmanJobResult;
import org.gearman.GearmanWorker;
import org.gearman.core.GearmanConstants;

public class TicketEngine
{
	public static void main(String[] args)
	{
		Gearman gearman = new Gearman();
		
		GearmanWorker worker = gearman.createGearmanWorker();
		
		worker.addServer(new InetSocketAddress("localhost", GearmanConstants.DEFAULT_PORT));
		
		//Maak het orderbook aan, dit gebeurt maar 1x (in de opzet dat maar 1 prog runt
		//OrderbookHashTable orderbookHashTable = new OrderbookHashTable();
		
		//String om eerst mee te testen
		String input = "{ activity: Nederland-Duitsland, type: limit, userid: USER6786, outcome: 2, bet: 87, tickets: 5, bidask: ask, time: 01:02:03:04:05:06 }";
			
		//Maak ticket met deze input
		Ticket ticket = new Ticket(input);
		
		System.out.printf(ticket.dateToString()+ "\n");
		
		//Zet deze order in het orderbook		
		/*
		if (orderbookHashTable.orderbookArray.containsKey(ticket.getActivity()))
		{
			//place order in orderbook en kijk of er matches zijn
		} else
		{
			//create new orderbook
			OrderBook orderBook = new OrderBook(ticket.getActivity());
			
			//place order in orderbook, er kunnen nog geen matches zijn.
			orderbookHashTable.orderbookArray.put(ticket.getActivity(), orderBook);
		}
		*/
	}
}
