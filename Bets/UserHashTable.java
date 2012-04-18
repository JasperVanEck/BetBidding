import java.util.HashMap;

public class UserHashTable
{
	//private String userID;
	public HashMap<String, User> userHashTable;
	
	//default constructor, there will be only one of these
	public UserHashTable()
	{
		
	}
	
	//ga er vanuit dat als je een user wilt toevoegen, je ook gelijk een ticket wil toevoegen
	//waarom zou je anders een user willen toevoegen?
	// voor de volledigheid zou ik ook nieuwe constructor kunnen maken bij user.java die gewoon een lege
	//hash table creeert... maarja gaan we cker niet gebruiken
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
	
	public boolean checkUserExists(String userID)
	{
		return userHashTable.containsKey(userID);
	}
	
	//check if user exists, and it has no more orders
	public void deleteUser(String userID)
	{
		if(checkUserExists(userID) && userHashTable.get(userID).isEmpty())
		{
			userHashTable.remove(userID);
		}
	}	
}