import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Main
{
	
	
	
	public static void main(String[] args)
	{
		//String om eerst mee te testen
		//File file = new File("C:\\Users\\petervantzand\\Desktop\\input.txt");
		//OrderBook orderbook = new OrderBook("test");
		
		try {
			//String om eerst mee te testen
			File file = new File("C:\\Users\\petervantzand\\Desktop\\input.txt");
			Scanner scanner = new Scanner(file).useDelimiter("\\s*[.?!]\\s*");
			
			
			String input = scanner.nextLine();
			
			//Maak ticket met deze input
			Ticket ticket = new Ticket(input);
			String activity = ticket.getActivity();
			
			OrderBook orderBook = new OrderBook(activity);
		//System.out.println("ticket" + ticket.getOutcome());
			/*
			Main m = new Main();
			System.out.println("ob: outcome = 0, getal=1 "+ m.otherBidColumnOrderBook(0, 1));
			System.out.println("ob: outcome = 0, getal=2 "+ m.otherBidColumnOrderBook(0, 2));
			System.out.println("bh: outcome = 0, getal=1 "+ m.otherBidColumnBidHigh(0, 1));
			System.out.println("bh: outcome = 0, getal=2 "+ m.otherBidColumnBidHigh(0, 2));
			
			System.out.println("ob: outcome = 1, getal=1 "+ m.otherBidColumnOrderBook(1, 1));
			System.out.println("ob: outcome = 1, getal=2 "+ m.otherBidColumnOrderBook(1, 2));
			System.out.println("bh: outcome = 1, getal=1 "+ m.otherBidColumnBidHigh(1, 1));
			System.out.println("bh: outcome = 1, getal=2 "+ m.otherBidColumnBidHigh(1, 2));
				
			System.out.println("ob: outcome = 2, getal=1 "+ m.otherBidColumnOrderBook(2, 1));
			System.out.println("ob: outcome = 2, getal=2 "+ m.otherBidColumnOrderBook(2, 2));
			System.out.println("bh: outcome = 2, getal=1 "+ m.otherBidColumnBidHigh(2, 1));
			System.out.println("bh: outcome = 2, getal=2 "+ m.otherBidColumnBidHigh(2, 2));
			*/
			System.out.println("ticket geprocessing begint");
			
			
			
			orderBook.processTicket(ticket);
			
			while(scanner.hasNextLine())
			{
				input = scanner.nextLine();
				ticket = new Ticket(input);
				//System.out.println("ticket" + ticket.getOutcome());
				orderBook.processTicket(ticket);
				
			}
			
		
				
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/*
	public static void main(String[] args)
	{
		//Maak het orderbook aan, dit gebeurt maar 1x (in de opzet dat maar 1 prog runt
		OrderBookHashTable orderbookHashTable = new OrderBookHashTable();
		
		//String om eerst mee te testen
		File file = new File("C:\\Users\\petervantzand\\Desktop\\input.txt");
		
		try {
			Scanner scanner = new Scanner(file).useDelimiter("\\s*[.?!]\\s*");
			String input = scanner.nextLine();
			
		
			//Maak ticket met deze input
			Ticket ticket = new Ticket(input);
			
			//Zet deze order in het orderbook		
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
				
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	*/
}
