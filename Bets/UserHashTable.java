import java.util.HashMap;

public class UserHashTable
{
	//private String userID;
	public HashMap<String, User> userHashTable;// = new HashMap();
	
	//default constructor, there will be only one of these
	public UserHashTable()
	{
		userHashTable = new HashMap<String, User>();
	}
	
public boolean addUser(Ticket ticket)
	{
		if(!checkUserExists(ticket.getUserID()))
		{
			User user = new User(ticket);
			userHashTable.put(ticket.getUserID(), user);
			return true;
		}
		else return false;
	}
	
	public void addTicket(Ticket ticket)
	{
		if(!checkUserExists(ticket.getUserID()))
		{
			addUser(ticket);
		}
		else
		{
			userHashTable.get(ticket.getUserID()).addTicket(ticket);
		}
	}
	
	public void deleteTicket(Ticket ticket, int amount)
	{
		if(checkUserExists(ticket.getUserID()))
		{
			userHashTable.get(ticket.getUserID()).removeTicket(ticket, amount);
		}
	}
	
	public void deleteTicket(Ticket ticket)
	{
		if(checkUserExists(ticket.getUserID()))
		{
			userHashTable.get(ticket.getUserID()).removeTicket(ticket);
		}
	}
	
	public boolean checkUserExists(String userID)
	{
		if(userHashTable.isEmpty())
		{
			return false;
		} else
		{
			return userHashTable.containsKey(userID);
		}
	}
	
	//check if user exists, and it has no more orders
	public void deleteUser(Ticket ticket)
	{
		if(checkUserExists(ticket.getUserID()) && userHashTable.get(ticket.getUserID()).isEmpty())
		{
			userHashTable.remove(ticket.getUserID());
		}
	}
	
	//Deze functie is denk het belangrijkst.
	//Deze functie kijkt of de gebruiker deze order al heeft,
	//zo ja, return de prijs, ander return -1
	public int checkUserHasTicketAlready(Ticket ticket)
	{
		int price = -1;
		
		if(checkUserExists(ticket.getUserID()) && userHashTable.get(ticket.getUserID()).checkUserHasActivity(ticket))
		{
			price = userHashTable.get(ticket.getUserID()).getPriceOfActivity(ticket);
		}
		
		return price;
	}
}
