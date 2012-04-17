public class OrderBook
{
	
	/*
	 * 100x6 array filled with queues, these queues are filled with tickets?
	 * win			draw		loss
	 * bid	ask		bid	ask		bid	ask
	 * -	-		-	-		-	-
	 * -	-		-	-		-	-
	 */
	private TicketArrayQueue[][] orderBook = new TicketArrayQueue[101][6];
	private int[][] numberArray = new int[101][6];
	private int outcome;
	private int[] askLow = new int[3];
	private int[] bidHigh = new int[3];
	private String activity;
	
	public OrderBook(String activity)
	{
		this.activity = activity;
		for(int i=0; i<askLow.length; i++)
		{
			this.askLow[i] = 100;
			this.bidHigh[i]= 0;
		}
		
	}
	
	public void addTicket(Ticket ticket)
	{
		String type = ticket.getType();
		TicketArrayQueue que = formBidAskMatch(ticket);
		if(que.isEmpty())
		{
			matched = formOutcomeMatch();
		}else if(!matched)
		{
			int prizeIndex = ticket.getPrize();
			int outcome = ticket.getOutcome();
			int bidOrAsk = ticket.getBidOrAsk();
			int columnIndex = outcome * 3 + bidOrAsk;
			setBidAskRate(prizeIndex, bidOrAsk, outcome);
			orderBook[prizeIndex][columnIndex].enqueue(ticket);
		}
	}
	
	public void setBidAskRate(int prize, int bidOrAsk, int outcome)
	{
		if(bidOrAsk == 0)
		{
			if(prize > bidHigh[outcome])
			{
				this.bidHigh[outcome] = prize;
			}
		}else
		{
			if(prize < askLow[outcome])
			{
				this.askLow[outcome] = prize;
			}
		}
	}
	/*
	public OrderBook(int outcome)
	{
		this.ticketArray = new TicketArrayQueue[100][2];//(8);
		this.numberArray = new int[100][2];
		this.outcome = outcome;
	}
	*/
	public int getOutcome()
	{
		return this.outcome;
	}
	
	public TicketArrayQueue formBidAskMatch(Ticket ticket)
	{
		int ticketPrize = ticket.getPrize();
		int bidOrAsk = ticket.getBidOrAsk();
		if((this.askLow <= ticketPrize && bidOrAsk == 0) || (this.bidHigh >= ticketPrize && bidOrAsk == 1))
		{
			int outcome = ticket.getOutcome();
			int columnIndex = outcome * 3 + !bidOrAsk;
			int prizeIndex;
			
			if(bidOrAsk == 0)
			{
				prizeIndex = this.bidHigh;
			}
			{
				prizeIndex = this.askLow;
			}
			
			Ticket ticketTry;
			int ticketAmount = ticket.getAmount();
			ticketTry = orderBook[prizeIndex][columnIndex].front();
			int ticketAmountTry = ticketTry.getAmount();
			TicketArrayQueue boughtQueue = new TicketArrayQueue(4);
			boughtQueue.enqueue(ticket);
			
			while(1)
			{
				if(ticketAmount == ticketAmountTry)
				{
					boughtQueue.enqueue(orderBook[prizeIndex][columnIndex].dequeue());
					break;
				}else if(ticketAmount > ticketAmountTry)
				{
					ticket.setAmount(ticketAmount - ticketAmountTry)
					ticketAmount = ticket.getAmount();
					ticketTry = orderBook[prizeIndex][columnIndex].front();
					ticketAmountTry = ticketTry.getAmount();
				}else
				{
					Ticket temp = ticketTry;
					ticketTry.setAmount(ticketAmountTry - ticketAmount)
					orderBook[prizeIndex][columnIndex].editFront(ticketTry);
					temp.setAmount(ticketAmount);
					boughtQueue.enqueue(temp);
					break;
				}
			}
		}
		return boughtQueue;
	}
	
	public boolean formOutcomeMatch()
	{
		
		
		return true;
	}
	
	/*
	public int getNumberPrize(int prize, String askbid)
	{
		int number = 0;
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
			orderBook[prize][0].enqueue(ticket);
			this.numberArray[prize][0] += amount;
			if(prize > this.bidHigh)
			{
				this.bidHigh = prize;
			}
			inserted = true;
		}else if(askbid.equals("ask"))
		{
			orderBook[prize][1].enqueue(ticket);
			this.numberArray[prize][1] += amount;
			if(prize > this.bidHigh)
			{
				this.bidHigh = prize;
			}
			inserted = true;
		}
		return inserted;
	}
	*/
	
}