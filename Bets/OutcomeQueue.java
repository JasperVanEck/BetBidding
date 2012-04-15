public class OutcomeQueue
{
	private TicketArrayQueue[] ticketArray;
	private int[] numberArray;
	private int outcome;
	
	public OutcomeQueue(int outcome)
	{
		this.ticketArray = new TicketArrayQueue[100][2](8);
		this.numberArray = new int[100][2]
		this.outcome = outcome;
	}
	
	public int getOutcome()
	{
		return this.outcome;
	}
	
	public int getNumberPrize(int prize, String askbid)
	{
		int number;
		if(askbid.equals("bid"))
		{
			number = this.numberArray[prize][0];
		}else if(askbid.equals("ask"))
		{
			number = this.numberArray[prize][1];
		}
		return number;
	}
	
	public boolean enqueue(Ticket ticket, String askbid)
	{
		boolean inserted = false;
		int prize = ticket.getPrize();
		int amount = ticket.getAmount();
		if(askbid.equals("bid"))
		{
			ticketArray[prize][0].enqueue(ticket);
			this.numberArray[prize][0] += amount;
			inserted = true;
		}else if(askbid.equals("ask"))
		{
			ticketArray[prize][1].enqueue(ticket);
			this.numberArray[prize][1] += amount;
			inserted = true;
		}
		return inserted;
	}
	
	
}