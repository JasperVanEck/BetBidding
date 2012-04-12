public class User
{
	private String name;
	private int userID;
	private double money;
	public static int DEFAULT_INIT_CAPACITY = 16;
	private Ticket[] tickets; //array gaat niet werken, moeten het makkelijk kunnen doorzoeken.
	private int ticketAmount;
	
	public User(String name, int userID, double money)
	{
		this.name = name;
		this.userID = userID;
		this.money = money;
		this.tickets = new Tickets[DEFAULT_INIT_CAPACITY];
		this.ticketAmount = 0;
	}

	public String getName()
	{
		return this.name;
	}

	public boolean setName(String name)
	{
		this.name = name;
		return true;
	}
	
	public int getUserID()
	{
		return this.userID;
	}
	
	public double getMoney()
	{
		return this.money;
	}
	
	public boolean setMoney(double money)
	{
		boolean set = true;
		if(money < 0)
		{
			set = false
		}else
		{
			this.money = money;
		}
		return set;
	}

	public double updateMoney(double additive)
	{
		this.money =+ additive;
		return this.money;
	}
	
	public boolean addTickets(Ticket ticket)
	{
		boolean added = true;
		if(!isEmpty())
		{
			tickets[ticketAmount] = ticket;
			ticketAmount++;
		}else
		{
			added = false;
		}
		return added;
	}
	
	public boolean removeTickets()
	{
		return false;
	}
	
	public int totalTickets()
	{
		int totalTicketAmount = 0;
		for(int i = 0; i < tickets.length; i++)
		{
			totalTicketAmount += tickets[i].getAmount();
		}
		return totalTicketAmount;
	}
	
	public boolean isEmpty()
	{
		boolean empty = false;
		if(ticketAmount == 0)
		{
			empty = true;
		}
		return empty;
	}
}