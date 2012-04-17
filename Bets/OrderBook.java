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
	private int[] askLow;
	private int[] bidHigh;
	private String activity;
	
	public OrderBook(String activity)
	{
		this.activity = activity;
		this.askLow = {100, 100, 100};
		this.bidHigh = {0, 0, 0};
	}
	
	public void addTicket(Ticket ticket)
	{
		int prizeIndex = ticket.getPrize();
		int outcome = ticket.getOutcome();
		int bidOrAsk = ticket.getBidOrAsk();
		int columnIndex = outcome * 3 + bidOrAsk;
		setBidAskRate(prizeIndex, bidOrAsk, outcome);
		orderBook[prizeIndex][columnIndex].enqueue(ticket);
	}
	
	public void setBidAskRate(int prize, int bidOrAsk, int outcome)
	{
		if(bidOrAsk == 0)
		{
			if(prize > bidHigh)
			{
				this.bidHigh[outcome] = prize;
			}
		}else
		{
			if(prize < askLow)
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
	
	
}