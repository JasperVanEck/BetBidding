import java.util.HashMap;
import java.util.Iterator;

public class User
{
	private String userID;
	public HashMap<String, Ticket> ticketsOfUser;
	
	//constructor, adds (the first) ticket
	public User(Ticket ticket)
	{
		this.userID = ticket.getUserID();
		addTicket(ticket);
	}

	
	
	public String getTicketKey(Ticket ticket)
	{
		return ticket.getActivity() +":" + ticket.getOutcome() + ":" + ticket.getBidOrAsk();
	}
	
	//add a ticket to the userTicketsHashTable
	public void addTicket(Ticket ticket)
	{
		
		//String activityKey = getTicketKey(ticket);
		
		if(checkUserHasActivity(ticket))
		{
			removeTicket(ticket);
			ticketsOfUser.put(ticket.getTicketKey(), ticket);
		} else
		{
			ticketsOfUser.put(ticket.getTicketKey(), ticket);
		}
	}

	//check if the user has any tickets
	public boolean isEmpty()
	{
		return ticketsOfUser.isEmpty();
	}

	//get the user id of this ticket hashtable
	public String getUserID()
	{
		return this.userID;
	}

	//decrease the amount of tickets of an activity
	public void removeTicket(Ticket ticket, int amount)
	{
		
		Ticket ticketToRemove = ticketsOfUser.get(ticket.getTicketKey());
		if(ticketToRemove.decreaseAmount(amount))
		{
			//if ticket has amount zero, delete this ticket
			ticketsOfUser.remove(ticket.getTicketKey());
		}
	}
	
	//decrease all the tickets of an activity
	public void removeTicket(Ticket ticket)
	{
		ticketsOfUser.remove(ticket.getTicketKey());
	}

	public boolean checkUserHasActivity(Ticket ticket)
	{
		if(ticketsOfUser.containsKey(getTicketKey(ticket)))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	/*
	 * iterate through the hash table and get sum of amount tickets
	 * outcome is optional, if you don't care what the outcome is,
	 * set outcome = -1
	 */
	public int getOutStandingOrderTickets(int bidOrAsk, int outcome)
	{
		Iterator<String> it = ticketsOfUser.keySet().iterator();

		int totalTickets = 0;
		//get maxCount
		while (it.hasNext())
		{
			String activity = (String) it.next();
			Ticket ticket = ticketsOfUser.get(activity);

			if(ticket.getBidOrAsk() ==  bidOrAsk && (outcome ==-1 || outcome == ticket.getOutcome()))
			{
				totalTickets += ticket.getAmount();
			}
		}
		return totalTickets;
	}

	/*
	 * Get total number of tickets, for any outcome
	 */
	public int getOutStandingOrderTickets(int bidOrAsk)
	{
		return getOutStandingOrderTickets(bidOrAsk, -1);
	}

	/*
	 * Get the total value of all the tickets for a certain outcome and bid or ask
	 */
	public int getOutStandingOrderAmount(int bidOrAsk, int outcome)
	{
		Iterator<String> it = ticketsOfUser.keySet().iterator();

		int totalTicketAmount = 0;
		//get maxCount
		while (it.hasNext())
		{
			String activity = (String) it.next();
			Ticket ticket = ticketsOfUser.get(activity);

			if(ticket.getBidOrAsk() ==  bidOrAsk && (outcome ==-1 || outcome == ticket.getOutcome()))
			{
				totalTicketAmount += ticket.getAmount() * ticket.getPrize();
			}
		}
		return totalTicketAmount;
	}
	
	/*
	 * Get the total value of all the tickets for bid or ask, for any outcome
	 */
	public int getOutStandingOrderAmount(int bidOrAsk)
	{
		return getOutStandingOrderAmount(bidOrAsk, -1);
	}

	/*
	 * Get the total price of all the different bid or ask prices
	 */
	public int getOutStandingOrderPrice(int bidOrAsk, int outcome)
	{
		Iterator<String> it = ticketsOfUser.keySet().iterator();

		int totalTicketPrize = 0;
		
		String activity = (String) it.next();
		Ticket ticket = ticketsOfUser.get(activity);

		if(ticket.getBidOrAsk() ==  bidOrAsk && outcome == ticket.getOutcome())
		{
			totalTicketPrize += ticket.getPrize();
		}
		
		return totalTicketPrize;
	}

	/*
	 * Check how many tickets this user has
	 */
	public int getAmountOfDifferentTickets()
	{
		return ticketsOfUser.size();
	}
}