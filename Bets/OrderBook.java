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
		for(int i = 0; i < askLow.length; i++)
		{
			this.askLow[i] = 100;
			this.bidHigh[i]= 0;
		}
		
	}
	
	public void addTicket(Ticket ticket)
	{
		String type = ticket.getType();
		int ticketAmount = ticket.getAmount();
		TicketArrayQueue que = formBidAskMatch(ticket);
		boolean matched;
		if((que.size() == 1) && (que.front().getAmount() == ticketAmount))
		{
			matched = formOutcomeMatch(ticket);
		}else if(!matched || (que.front().getAmount() != ticketAmount))
		{
			int prizeIndex = ticket.getPrize();
			int outcome = ticket.getOutcome();
			int bidOrAsk = ticket.getBidOrAsk();
			int columnIndex = outcome * 2 + bidOrAsk;
			setBidAskRate(prizeIndex, bidOrAsk, outcome);
			orderBook[prizeIndex][columnIndex].enqueue(ticket);
		}
	}
	
	public Ticket removeTicket(int bidOrAsk, int prizeIndex, int columnIndex)
	{
		Ticket ticket = orderBook[prizeIndex][columnIndex].dequeue();
		int i = prizeIndex;
		
		if(bidOrAsk == 0)
		{
			while(orderBook[i][columnIndex].isEmpty())
			{
				i--;
			}
			this.bidHigh = i;
		}else
		{
			while(orderBook[i][columnIndex].isEmpty())
			{
				i++;
			}
			this.askLow = i;
		}
		return ticket;
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

	public int getOutcome()
	{
		return this.outcome;
	}
	
	public TicketArrayQueue formBidAskMatch(Ticket ticket)
	{
		int ticketPrize = ticket.getPrize();
		int bidOrAsk = ticket.getBidOrAsk();
		TicketArrayQueue boughtQueue = new TicketArrayQueue(4);
		boughtQueue.enqueue(ticket);
		if((this.askLow <= ticketPrize && bidOrAsk == 0) || (this.bidHigh >= ticketPrize && bidOrAsk == 1))
		{
			int outcome = ticket.getOutcome();
			int columnIndex = outcome * 2;
			if(bidOrAsk == 0)
			{
				columnIndex + 1;
			}
			
			int prizeIndex;
			if(bidOrAsk == 0)
			{
				prizeIndex = this.bidHigh;
			}
			{
				prizeIndex = this.askLow;
			}
			
			int ticketAmount = ticket.getAmount();
			Ticket ticketTry = orderBook[prizeIndex][columnIndex].front();
			int ticketAmountTry = ticketTry.getAmount();
			
			while((this.askLow <= ticketPrize && bidOrAsk == 0) || (this.bidHigh >= ticketPrize && bidOrAsk == 1))
			{
				if(bidOrAsk == 0)
				{
					prizeIndex = this.bidHigh;
				}
				{
					prizeIndex = this.askLow;
				}
				
				if(ticketAmount == ticketAmountTry)
				{
					boughtQueue.enqueue(removeTicket(bidOrAsk, prizeIndex, columnIndex));
					break;
				}else if(ticketAmount > ticketAmountTry)
				{
					ticket.setAmount(ticketAmount - ticketAmountTry);
					ticketAmount = ticket.getAmount();
					boughtQueue.editFront(ticket);
					boughtQueue.enqueue(removeTicket(bidOrAsk, prizeIndex, columnIndex));//moet nog ff iets komen dat als askLow enzo niet meer kloppen, maar fix morgen wel.
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
	
	public boolean formOutcomeMatch(Ticket ticket)
	{
		TicketArrayQueue matchQue = new TicketArrayQueue(3);
		Ticket[] ticketList = new Ticket[3];
		int bidOrAsk = ticket.getBidOrAsk();
		int prizeIndex = ticket.getPrize();
		int outcome = ticket.getOutcome();
		int columnIndexT = outcome * 2 + bidOrAsk;
		ticketList[outcome] = ticket;
		int i = 0;
		int index;
		
		if(bidOrAsk == 0)
		{
			while(i < 3)
			{
				if(i != outcome)
				{
					index = i * 2;
					ticketList[i] = orderBook[this.bidHigh][index].front();
				}
			}
		}else
		{
			while(i < 3)
			{
				if(i != outcome)
				{
					index = i * 2 + 1;
					ticketList[i] = orderBook[this.askLow][index].front();
				}
			}
		}
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