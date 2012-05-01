import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Main
{
	public static void main(String[] args)
	{
		//Maak het orderbook aan, dit gebeurt maar 1x (in de opzet dat maar 1 prog runt
		OrderBookHashTable orderbookHashTable = new OrderBookHashTable();
		
		//String om eerst mee te testen
		File file = new File("C:\\Users\\petervantzand\\Desktop\\input.txt");
		
		try {
			Scanner scanner = new Scanner(file).useDelimiter("\\s*[.?!]\\s*");
			String input = scanner.nextLine();
			
			//	String input = "{"activity":"activity_test","userid":604243,"outcome":0,"bet":79,"tickets":116,"bidask":"bid","time":"01\/05\/2012 11:50:45:000000"}";
			
				//Maak ticket met deze input
				Ticket ticket = new Ticket(input);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//    String input = scanner.nextLine();
		
	//	String input = "{"activity":"activity_test","userid":604243,"outcome":0,"bet":79,"tickets":116,"bidask":"bid","time":"01\/05\/2012 11:50:45:000000"}";
	
		//Maak ticket met deze input
		//Ticket ticket = new Ticket(input);
	/*	
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
		}*/
	}
}
