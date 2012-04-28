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
	private UserHashTable userHashTable;
	//private int[][] numberArray = new int[101][6];
	private int outcome;
	private int[] askLow = new int[3];
	private int[] bidHigh = new int[3];
	private String activity;
	
	private final static int BID = 0;
	private final static int ASK = 1;
	private final static int WIN = 0;
	private final static int DRAW = 1;
	private final static int LOSE = 2;
	
	public OrderBook(String activity)
	{
		this.activity = activity;
		for(int i = 0; i < askLow.length; i++)
		{
			this.askLow[i] = 100;
			this.bidHigh[i]= 0;
		}
		
	}
	
	public void processTicket(Ticket ticket, UserHashTable userHashTable2)
	{
		/**
		 * 1. Als deze gebruiker al een bod in het orderboek heeft staan op dezelfde activiteit, 
		 * dezelfde uitkomst en hetzelfde type (bid of ask), dan vervangt deze nieuwe order de al 
		 * bestaande order. Concreet betekend dit dat de al bestaande order verwijderd zal worden uit 
	 	 * de wachtrij, en vervolgens worden onderstaande stappen - net als een order van een gebruiker 
		 * die nog niets in het orderboek - doorlopen.
		 */
		 this.userHashTable = userHashTable2;
		 //First check if user hasn't already this ticket. Then the already existing ticket must be deleted
		if(userHashTable.checkUserHasTicketAlready(ticket) != -1)
		{
			deleteTicket(ticket, userHashTable);
		}
		
		/**
		 * 2. De engine checkt of de nieuwe order beter is dan een ander al bestaand bod in het 
		 * orderboek. Dat is het geval als een bid-bod een hogere bet heeft dan de maximale bidprijs 
		 * in het orderboek (de user is bereid meer te betalen dan iemand anders), of als een ask-bod 
		 * een lagere bet heeft dan de minimale askprijs (de user is bereid met minder genoegen te nemen 
		 * dan iemand anders). In die gevallen kan er een match tot stand komen, omdat vraag en aanbod 
		 * dichter bij elkaar zijn gekomen. Is dat niet zo, kan stap 3 overgeslagen worden.
		 */
		if(	(ticket.getBidOrAsk()  == ASK && ticket.getPrice() > askLow[ticket.getOutcome()] ) ||
			(ticket.getBidOrAsk()  == BID && ticket.getPrice() < bidHigh[ticket.getOutcome()]) )
		{
			/** 
			 * 4. Als er geen match gemaakt kan worden voor de nieuwe order, of de nieuwe order kan niet volledig
			 * vervuld worden met behulp van de bestaande orders in het orderboek, dan kan deze order (of het
			 * resterende deel ervan) in het orderboek (wachtrij) geplaatst worden.
			 */
			addTicketToOrderBookAndUserHashTable(ticket, userHashTable);
			
		} else
		{
			/**
			 * 3. De nieuwe order is beter, dus het heeft zin om te kijken of er nu wel een match gemaakt kan
			 * worden. 
			 * 	a. De engine checkt vervolgens eerst of dit nieuwe bod een match tot stand kan brengen met
			 * 	een order van de andere orderboek-zijde. Dus bij een bid-bod van 75 cent, wordt eerst 
			 * 	gecheckt of er een ask-bod van 75 cent of lager tegenover staat. 
			 * 	b. Als dat niet kan, of slechts een deel van de nieuwe order is op deze manier gematcht,
			 * 	dan checkt de engine of er een match tot stand kan worden gebracht met anderen uit dezelfde
			 * 	orderboek-zijde.
			 * 		i. Voor de bid-zijde kan dat, als de optelsom van maximale bets (prijzen) van elke
			 * 		outcome, optelt tot 100 cent of meer. Dus: user x is bereid 60 cent te betalen voor
			 * 		'Nederland wint', user y 30 cent voor 'Duitsland wint', en user z 10 cent voor
			 * 		'Gelijkspel'. Samen bieden ze exact 100 cent, dus kan er een match tot stand worden 
			 * 		gebracht aan de bid-zijde. Er hoeft geen bod te zijn op alle uitkomsten. Theoretisch
			 * 		zou iemand dus 100 cent op 'Nederland' kunnen bieden, waarbij er een 'match' tot stand
			 * 		komt. Zo lang de 100 cent maar gehaald wordt. In het geval dat niet elke outcome
			 * 		vertegenwoordigd is in de match, gaan de tickets die gecreeerd worden op de
			 * 		niet-vertegenwoordigde outcomes in 'onze pot'.
			 * 		ii. Voor de ask-zijde kan dat, als de optelsom van minimale bets (prijzen) van elke
			 * 		outcome, optelt tot 100 cent of minder. Dus: user x is bereid 60 cent te betalen voor
			 * 		'Nederland wint', user y 25 cent voor 'Duitsland wint', en user z 10 cent voor
			 * 		'Gelijkspel'. Samen bieden ze 95 cent, dus kan er een match tot stand worden gebracht
			 * 		aan de ask-zijde. Belangrijk hierbij is dat - itt tot bij de bid-zijde - hier alle 
			 * 		outcomes vertegenwoordigd moeten zijn. Een match aan de ask-zijde kan dus alleen tot 
			 * 		stand komen als er en op alle mogelijke uitkomsten een user zijn tickets wil verkopen,
			 * 		en dat de optelsom van de minimale bets per uitkomst gelijk zijn of kleiner dan 100 cent.
			 */
			
			//check if bidprice and askprice and ticket has positive amount
			while ( bidAskMeets(ticket) && ticket.getAmount() > 0)
			{
			 	if(ticket.getBidOrAsk() == ASK )
			 	{
			 		Ticket ticket2 = orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex() -1].front();
			 		int tradeAmount = getTradeAmount(ticket, ticket2);
			 		
			 		System.out.println("handel!!!!!!!!!!!!!!");
			 		//decrease both tickets with tradeAmount
			 		ticket.decreaseAmount(tradeAmount);
			 		ticket2.decreaseAmount(tradeAmount);
			 		
			 		//delete amount of ticket2 in userHashTable
			 		userHashTable.deleteTicket(orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex() -1].front(), tradeAmount);
			 		
			 		if(ticket2.getAmount() == 0)
			 		{
			 			orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex() -1].dequeue();
			 			
			 			//check if the queue is now empty
			 			if(orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex() -1].isEmpty())
			 			{
			 				//set new bidHigh
			 				setNewBidHigh(ticket.getOutcome(), ticket.getPrice());
			 			}
			 		}
			 		else
			 		{
			 			orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex() -1].editFront(ticket2);
				 	}
			 	}
			 	
			 	if(ticket.getBidOrAsk() == BID )
			 	{
			 		Ticket ticket2 = orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex() + 1].front();
			 		int tradeAmount = getTradeAmount(ticket, ticket2);
			 		
			 		//decrease both tickets with tradeAmount
			 		ticket.decreaseAmount(tradeAmount);
			 		ticket2.decreaseAmount(tradeAmount);
			 		
			 		//delete amount of ticket2 in userHashTable
			 		userHashTable.deleteTicket(orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex() +1].front(), tradeAmount);
			 		
			 		if(ticket2.getAmount() == 0)
			 		{
			 			orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex() +1].dequeue();
			 			
			 			//check if the queue is now empty
			 			if(orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex() +1].isEmpty())
			 			{
			 				//set new askLow
			 				setNewAskLow(ticket.getOutcome(), ticket.getPrice());
			 			}
			 		}
			 		else
			 		{
			 			orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex() +1].editFront(ticket2);
				 	}
			 	}
			 	
			 	
			 	
			 	
			}
				  
			
			
			/*3a
			 * while ( 3a kan)
			 * {
			 * 		make matches, change ticket (decrease amont
			 * }
			 * 
			 * 3b
			 * if(ticket.getBidOrAsk = ASK_
			 * {
			 *  do i.
			 * } else
			 * {
			 *  do ii.
			 * } 
			 * 
			 * 3c
			 * if(ticket.getAmount > 1)
			 * {
			 *    addTicketToOrderBookAndUserHashTable(ticket, userHashTable);
			 * }
			 */
			
		}
	}
	
	public void setNewBidHigh(int outcome , int oldHigh)
	{
		int newHigh = 0;
		oldHigh--;
		while(newHigh == 0)
		{
			if(!orderBook[oldHigh][outcome].isEmpty())
			{
				newHigh = oldHigh;
				this.bidHigh[outcome] = newHigh;
			} else
			{
				oldHigh--;
			}
		}
	}
	
	public void setNewAskLow(int outcome , int oldLow)
	{
		int newLow = 0;
		oldLow++;
		while(newLow == 0)
		{
			if(!orderBook[oldLow][outcome].isEmpty())
			{
				newLow = oldLow;
				this.bidHigh[outcome] = newLow;
			} else
			{
				oldLow++;
			}
		}
	}
		
	
	
	//get the minimum of the amounts of both tickets
	public int getTradeAmount(Ticket ticket1, Ticket ticket2)
	{
		if(ticket1.getAmount() > ticket2.getAmount())
		{
			return ticket2.getAmount();
		}
		else
		{
			return ticket1.getAmount();
		}
	}
	
	//get the minimum of the amounts of all three tickets
	public int getTradeAmount(Ticket ticket1, Ticket ticket2, Ticket ticket3)
	{
		if(ticket1.getAmount() > ticket3.getAmount() && ticket2.getAmount() > ticket3.getAmount())
		{
			return ticket3.getAmount();
		}
		else
		{
			return getTradeAmount(ticket1, ticket2);
		}
	}
	
	
	public boolean bidAskMeets(Ticket ticket)
	{
		if(	(ticket.getBidOrAsk() == ASK && ticket.getPrice() < bidHigh[ticket.getOutcome()] ) ||
			(ticket.getBidOrAsk() == BID && ticket.getPrice() > askLow[ticket.getOutcome()] ) )
		{
			return true;
		}
		else
			return false;
	}
	
	public void addTicketToOrderBookAndUserHashTable(Ticket ticket, UserHashTable userHashTable)
	{
		orderBook[ticket.getPrice()][ticket.getTicketIndex()].enqueue(ticket);
		userHashTable.addTicket(ticket);
	}
	
	
	
	public void deleteTicket(Ticket ticket, UserHashTable userHashTable)
	{
		int askOrBidPrice = userHashTable.checkUserHasTicketAlready(ticket);
		
		//if this ticket is the only one in this queue. set new bidHigh or askLow and delete Ticket form queue
		if(orderBook[askOrBidPrice][ticket.getTicketIndex()].size()==1)
		{
			onlyOneInQueue(ticket, askOrBidPrice);
		} else //not only one in row so only delete from queue
		{
			orderBook[askOrBidPrice][ticket.getTicketIndex()].deleteTicket(ticket); 
		}
		
		//always delete from userHashTable
		userHashTable.deleteTicket(ticket);
	}
	
	public void onlyOneInQueue(Ticket ticket, int askOrBidPrice)
	{
		//if so change into new lowest or highest
		if(ticket.getBidOrAsk() == BID && bidHigh[ticket.getOutcome()] == askOrBidPrice)
		{
			orderBook[askOrBidPrice][ticket.getTicketIndex()].dequeue();
		
			boolean setNew = false;
			int highestBid = askOrBidPrice-1;
			int ticketIndex = ticket.getTicketIndex();
			while(!setNew)
			{
				if(!orderBook[highestBid][ticketIndex].isEmpty() && highestBid == 0)
				{
					bidHigh[ticket.getOutcome()] = highestBid;
					setNew = true;
				} else
				{
					highestBid--;
				}
			}
			
		}
		
		if(ticket.getBidOrAsk() == ASK && askLow[ticket.getOutcome()] == askOrBidPrice )
		{
			orderBook[askOrBidPrice][ticket.getTicketIndex()].dequeue();
		
			boolean setNew = false;
			int lowestAsk = askOrBidPrice+1;
			int ticketIndex = ticket.getTicketIndex();
			while(!setNew)
			{
				if(!orderBook[lowestAsk][ticketIndex].isEmpty() || lowestAsk == 100)
				{
					askLow[ticket.getOutcome()] = lowestAsk;
					setNew = true;
				} else
				{
					lowestAsk++;
				}
			}
		
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
				int priceIndex = ticket.getPrice();
				int outcome = ticket.getOutcome();
				int bidOrAsk = ticket.getBidOrAsk();
				int columnIndex = outcome * 2 + bidOrAsk;
				setBidAskRate(priceIndex, bidOrAsk, outcome);
				orderBook[priceIndex][columnIndex].enqueue(ticket);
			}
		}
		return "ticket";
	}
	
	public Ticket removeTicket(int bidOrAsk, int priceIndex, int columnIndex)
	{
		Ticket ticket = orderBook[priceIndex][columnIndex].dequeue();
		int i = priceIndex;
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
	
	public void setBidAskRate(int price, int bidOrAsk, int outcome)
	{
		if(bidOrAsk == 0)
		{
			if(price > bidHigh[outcome])
			{
				this.bidHigh[outcome] = price;
			}
		}else
		{
			if(price < askLow[outcome])
			{
				this.askLow[outcome] = price;
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
		
		int priceIndex;
		if(bidOrAsk == 0)
		{
			priceIndex = this.bidHigh[outcome];
		}else
		{
			priceIndex = this.askLow[outcome];
		}
		int ticketAmount = ticket.getAmountLeft();
		
		Ticket ticketTry = orderBook[priceIndex][columnIndex].front();
		int ticketAmountTry = ticket.getAmountLeft();
		
		while(ticket.getAmountLeft() != 0)
		{
			if(bidOrAsk == 0)
			{
				priceIndex = this.bidHigh[outcome];
			}else
			{
				priceIndex = this.askLow[outcome];
			}
			
			if(ticketAmount == ticketAmountTry)
			{
				boughtQueue.enqueue(removeTicket(bidOrAsk, priceIndex, columnIndex));
				ticket.setAmountLeft(ticketAmount - ticketAmountTry);
			}else if(ticketAmount > ticketAmountTry)
			{
				ticket.setAmountLeft(ticketAmount - ticketAmountTry);
				ticketAmount = ticket.getAmountLeft();
				boughtQueue.editFront(ticket);
				boughtQueue.enqueue(removeTicket(bidOrAsk, priceIndex, columnIndex));
				ticketTry = orderBook[priceIndex][columnIndex].front();
				ticketAmountTry = ticketTry.getAmountLeft();
			}else
			{
				Ticket temp = ticketTry;
				ticketTry.setAmountLeft(ticketAmountTry - ticketAmount);
				orderBook[priceIndex][columnIndex].editFront(ticketTry);
				temp.setAmountLeft(ticketAmount);
				boughtQueue.enqueue(temp);
				ticket.setAmountLeft(0);
			}
		}
		return boughtQueue;
	}
	
	public TicketArrayQueue formBidAskMatch(Ticket ticket)
	{
		int ticketPrice = ticket.getPrice();
		int bidOrAsk = ticket.getBidOrAsk();
		TicketArrayQueue boughtQueue = new TicketArrayQueue(4);
		boughtQueue.enqueue(ticket);
		int outcome = ticket.getOutcome();
		if((this.askLow[outcome] <= ticketPrice && bidOrAsk == 0) || (this.bidHigh[outcome] >= ticketPrice && bidOrAsk == 1))
		{
			int columnIndex = outcome * 2;
			if(bidOrAsk == 0)
			{
				columnIndex++;
			}
			
			int priceIndex;
			if(bidOrAsk == 0)
			{
				priceIndex = this.bidHigh[outcome];
			}else
			{
				priceIndex = this.askLow[outcome];
			}
			
			int ticketAmount = ticket.getAmount();
			Ticket ticketTry = orderBook[priceIndex][columnIndex].front();
			int ticketAmountTry = ticketTry.getAmount();
			
			while((this.askLow[outcome] <= ticketPrice && bidOrAsk == 0) || (this.bidHigh[outcome] >= ticketPrice && bidOrAsk == 1))
			{
				if(bidOrAsk == 0)
				{
					priceIndex = this.bidHigh[outcome];
				}else
				{
					priceIndex = this.askLow[outcome];
				}
				
				if(ticketAmount == ticketAmountTry)
				{
					boughtQueue.enqueue(removeTicket(bidOrAsk, priceIndex, columnIndex));
					ticket.setAmountLeft(ticketAmount - ticketAmountTry);
				}else if(ticketAmount > ticketAmountTry)
				{
					ticket.setAmountLeft(ticketAmount - ticketAmountTry);
					ticketAmount = ticket.getAmount();
					boughtQueue.editFront(ticket);
					boughtQueue.enqueue(removeTicket(bidOrAsk, priceIndex, columnIndex));//moet nog ff iets komen dat als askLow enzo niet meer kloppen, maar fix morgen wel.
					ticketTry = orderBook[priceIndex][columnIndex].front();
					ticketAmountTry = ticketTry.getAmount();
				}else
				{
					Ticket temp = ticketTry;
					ticketTry.setAmount(ticketAmountTry - ticketAmount);
					orderBook[priceIndex][columnIndex].editFront(ticketTry);
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
			orderBook[ticketPrice][col].enqueue(ticket);
		}
		return boughtQueue;
	}
	
	public TicketArrayQueue formOutcomeMatch(Ticket ticket)
	{
		TicketArrayQueue matchQue = new TicketArrayQueue(3);
		Ticket[] ticketList = new Ticket[3];
		int bidOrAsk = ticket.getBidOrAsk();
		int priceIndex = ticket.getPrice();
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
