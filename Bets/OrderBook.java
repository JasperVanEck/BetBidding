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
	
	public String addTicket(Ticket ticket)
	{
		String type = ticket.getType();
		int ticketAmount = ticket.getAmount();
		if(type.equals("market"))
		{
			TicketArrayQueue que = formMarketMatch(ticket);
		}else
		{
			TicketArrayQueue que = formBidAskMatch(ticket);
			TicketArrayQueue matched = new TicketArrayQueue(4);
			if((que.size() == 1) && (que.front().getAmount() == ticketAmount))
			{
				matched = formOutcomeMatch(ticket);
			}else if((matched.size() == 1) || (que.front().getAmount() != ticketAmount))
			{
				int prizeIndex = ticket.getPrize();
				int outcome = ticket.getOutcome();
				int bidOrAsk = ticket.getBidOrAsk();
				int columnIndex = outcome * 2 + bidOrAsk;
				setBidAskRate(prizeIndex, bidOrAsk, outcome);
				orderBook[prizeIndex][columnIndex].enqueue(ticket);
			}
		}
		return "ticket";
	}
	
	public Ticket removeTicket(int bidOrAsk, int prizeIndex, int columnIndex)
	{
		Ticket ticket = orderBook[prizeIndex][columnIndex].dequeue();
		int i = prizeIndex;
		int outcome = ticket.getOutcome();
		
		if(bidOrAsk == 0)
		{
			while(orderBook[i][columnIndex].isEmpty())
			{
				i--;
			}
			this.bidHigh[outcome] = i;
		}else
		{
			while(orderBook[i][columnIndex].isEmpty())
			{
				i++;
			}
			this.askLow[outcome] = i;
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
	
	public TicketArrayQueue formMarketMatch(Ticket ticket)
	{
		TicketArrayQueue boughtQueue = new TicketArrayQueue(4);
		boughtQueue.enqueue(ticket);
		int bidOrAsk = ticket.getBidOrAsk();
		int outcome = ticket.getOutcome();
		int columnIndex = outcome * 2;
		if(bidOrAsk == 0)
		{
			columnIndex++;
		}
		
		int prizeIndex;
		if(bidOrAsk == 0)
		{
			prizeIndex = this.bidHigh[outcome];
		}else
		{
			prizeIndex = this.askLow[outcome];
		}
		int ticketAmount = ticket.getAmountLeft();
		
		Ticket ticketTry = orderBook[prizeIndex][columnIndex].front();
		int ticketAmountTry = ticket.getAmountLeft();
		
		while(ticket.getAmountLeft() != 0)
		{
			if(bidOrAsk == 0)
			{
				prizeIndex = this.bidHigh[outcome];
			}else
			{
				prizeIndex = this.askLow[outcome];
			}
			
			if(ticketAmount == ticketAmountTry)
			{
				boughtQueue.enqueue(removeTicket(bidOrAsk, prizeIndex, columnIndex));
				ticket.setAmountLeft(ticketAmount - ticketAmountTry);
			}else if(ticketAmount > ticketAmountTry)
			{
				ticket.setAmountLeft(ticketAmount - ticketAmountTry);
				ticketAmount = ticket.getAmountLeft();
				boughtQueue.editFront(ticket);
				boughtQueue.enqueue(removeTicket(bidOrAsk, prizeIndex, columnIndex));
				ticketTry = orderBook[prizeIndex][columnIndex].front();
				ticketAmountTry = ticketTry.getAmountLeft();
			}else
			{
				Ticket temp = ticketTry;
				ticketTry.setAmountLeft(ticketAmountTry - ticketAmount);
				orderBook[prizeIndex][columnIndex].editFront(ticketTry);
				temp.setAmountLeft(ticketAmount);
				boughtQueue.enqueue(temp);
				ticket.setAmountLeft(0);
			}
		}
		return boughtQueue;
	}
	
	public TicketArrayQueue formBidAskMatch(Ticket ticket)
	{
		int ticketPrize = ticket.getPrize();
		int bidOrAsk = ticket.getBidOrAsk();
		TicketArrayQueue boughtQueue = new TicketArrayQueue(4);
		boughtQueue.enqueue(ticket);
		int outcome = ticket.getOutcome();
		if((this.askLow[outcome] <= ticketPrize && bidOrAsk == 0) || (this.bidHigh[outcome] >= ticketPrize && bidOrAsk == 1))
		{
			int columnIndex = outcome * 2;
			if(bidOrAsk == 0)
			{
				columnIndex++;
			}
			
			int prizeIndex;
			if(bidOrAsk == 0)
			{
				prizeIndex = this.bidHigh[outcome];
			}else
			{
				prizeIndex = this.askLow[outcome];
			}
			
			int ticketAmount = ticket.getAmount();
			Ticket ticketTry = orderBook[prizeIndex][columnIndex].front();
			int ticketAmountTry = ticketTry.getAmount();
			
			while((this.askLow[outcome] <= ticketPrize && bidOrAsk == 0) || (this.bidHigh[outcome] >= ticketPrize && bidOrAsk == 1))
			{
				if(bidOrAsk == 0)
				{
					prizeIndex = this.bidHigh[outcome];
				}else
				{
					prizeIndex = this.askLow[outcome];
				}
				
				if(ticketAmount == ticketAmountTry)
				{
					boughtQueue.enqueue(removeTicket(bidOrAsk, prizeIndex, columnIndex));
					ticket.setAmountLeft(ticketAmount - ticketAmountTry);
				}else if(ticketAmount > ticketAmountTry)
				{
					ticket.setAmountLeft(ticketAmount - ticketAmountTry);
					ticketAmount = ticket.getAmount();
					boughtQueue.editFront(ticket);
					boughtQueue.enqueue(removeTicket(bidOrAsk, prizeIndex, columnIndex));//moet nog ff iets komen dat als askLow enzo niet meer kloppen, maar fix morgen wel.
					ticketTry = orderBook[prizeIndex][columnIndex].front();
					ticketAmountTry = ticketTry.getAmount();
				}else
				{
					Ticket temp = ticketTry;
					ticketTry.setAmount(ticketAmountTry - ticketAmount);
					orderBook[prizeIndex][columnIndex].editFront(ticketTry);
					temp.setAmount(ticketAmount);
					boughtQueue.enqueue(temp);
					ticket.setAmountLeft(0);
					break;
				}
			}
		}
		int col = outcome * 2;
		if(ticket.getAmountLeft() != 0)
		{
			if(bidOrAsk == 1)
			{
				col++;
			}
			orderBook[ticketPrize][col].enqueue(ticket);
		}
		return boughtQueue;
	}
	
	public TicketArrayQueue formOutcomeMatch(Ticket ticket)
	{
		TicketArrayQueue matchQue = new TicketArrayQueue(3);
		Ticket[] ticketList = new Ticket[3];
		int bidOrAsk = ticket.getBidOrAsk();
		int prizeIndex = ticket.getPrize();
		int outcome = ticket.getOutcome();
		int columnIndex = outcome * 2 + bidOrAsk;
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
					ticketList[i] = orderBook[this.bidHigh[outcome]][index].front();
				}
			}
		}else
		{
			while(i < 3)
			{
				if(i != outcome)
				{
					index = i * 2 + 1;
					ticketList[i] = orderBook[this.askLow[outcome]][index].front();
				}
			}
		}
		return matchQue;
	}
}
