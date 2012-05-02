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
			System.out.println("ticket geprocessing begint");
			
			orderBook.processTicket(ticket);
			System.out.println("ticket geprocessed");
			while(scanner.hasNextLine())
			{
				input = scanner.nextLine();
				ticket = new Ticket(input);
				//System.out.println("ticket" + ticket.getOutcome());
				orderBook.processTicket(ticket);
				//System.out.println("Ticket processed");
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
