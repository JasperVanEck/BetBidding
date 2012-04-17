public class OutcomeQueue
{
	private TicketArrayQueue[] ticketArray;
	private int[] numberArray;
	private int askLow;
	private int bidHigh;
	private int outcome;
	
	public OutcomeQueue(int outcome)
	{
		this.ticketArray = new TicketArrayQueue[100][2](8);
		this.numberArray = new int[100][2]
		this.outcome = outcome;
		this.askLow = 100;
		this.bidHigh = 0;
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
			if(prize > this.bidHigh)
			{
				this.bidHigh = prize;
			}
			inserted = true;
		}else if(askbid.equals("ask"))
		{
			ticketArray[prize][1].enqueue(ticket);
			this.numberArray[prize][1] += amount;
			if(prize < this.askLow)
			{
				this.askLow = prize;
			}
			inserted = true;
		}
		return inserted;
	}
	
	public Ticket dequeue(int prize, String askbid)
	{
		Ticket ticket;
		if(askbid.equals("bid"))
		{
			ticket = ticketArray[prize][0].dequeue();
			this.numberArray[prize][0] -= ticket.getAmount();
			if(ticketArray[prize][0].isEmpty())
			{
				this.bidHigh--;
			}
		}else if(askbid.equals("ask"))
		{
			ticket = ticketArray[prize][1].dequeue();
			this.numberArray[prize][1] -= ticket.getAmount();
			if(ticketArray[prize][1].isEmpty())
			{
				this.askLow++;
			}
		}
		return ticket;
	}
	
	public boolean formMatch()
	{
		boolean matched = false;
		Ticket ticketBid;
		Ticket ticketAsk;
		if(askLow <= bidHigh)
		{
			ticketBid = ticketArray[bidHigh][0].front();
			ticketAsk = ticketArray[askLow][1].front();
			if(ticketBid.getAmount() >= ticketAsk.getAmount())
		}
		
		
		return matched;
	}
}