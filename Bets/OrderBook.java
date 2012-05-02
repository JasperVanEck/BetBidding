import java.util.GregorianCalendar;

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
	private int[] askLow = new int[3];
	private int[] bidHigh = new int[3];
	private String activity;
	
	private final static int BID = 0;
	private final static int ASK = 1;
	
	
	private String ADVANTAGE = "first";
	
	//This function creates a new orderBook
	public OrderBook(String activity)
	{
		this.activity = activity;
		for(int i = 0; i < askLow.length; i++)
		{
			this.askLow[i] = 100;
			this.bidHigh[i] = 0;
		}
		for(int i = 0; i < 6; i++)
		{
			for(int j = 0; j < 101; j++)
			{
				orderBook[j][i] = new TicketArrayQueue();
				
			}
		}
		//orderBook[2][2] = new TicketArrayQueue();
		this.userHashTable = new UserHashTable();
		
	}
	
	///This function returns the activity of this orderbook
	public String getActivity()
	{
		return this.activity;
	}
	
	public String getKoers(String bidOrAsk)
	{
		String koers;
		if(bidOrAsk.equals("bid"))
		{
			koers = "{ " + bidHigh[0] + ", " + bidHigh[1] + ", " + bidHigh[2] + " }";
		} else if(bidOrAsk.equals("ask"))
		{
			koers = "{ " + askLow[0] + ", " + askLow[1] + ", " + askLow[2] + " }";
		} else
		{
			koers = "error, geen bid or ask gevraagd";
		}
		return koers;
	}
	
	
	//This function sets the advantage to first or last
	public void setAdvantage(String advantage)
	{
		if(advantage.equals("first") || advantage.equals("last"))
		{
			this.ADVANTAGE = advantage;
		}
		else
		{
			System.out.println("advantage moet first of last zijn"); 
		}
	}
	
	//This function processes an order
	public void processTicket(Ticket ticket)
	{
		/*
		 * 1. Als deze gebruiker al een bod in het orderboek heeft staan op dezelfde activiteit, 
		 * dezelfde uitkomst en hetzelfde type (bid of ask), dan vervangt deze nieuwe order de al 
		 * bestaande order. Concreet betekend dit dat de al bestaande order verwijderd zal worden uit 
	 	 * de wachtrij, en vervolgens worden onderstaande stappen - net als een order van een gebruiker 
		 * die nog niets in het orderboek - doorlopen.
		 */
		 //First check if user hasn't already this ticket. Then the already existing ticket must be deleted
		System.out.print("\tStap 1, kijken of user al ticket heeft");
		int test = 0;
		if(userHashTable.checkUserHasTicketAlready(ticket) != -1)
		{
			System.out.printf("User heeft ticket al");
			System.out.println("\tTicket proberen te deleten\n");
			deleteTicket(ticket);
			System.out.printf("\tTicket deleted\n");
			test =1;
		}
		if(test ==0)
		{
			System.out.println("--> user heeft ticket nog niet");
		}
		
		System.out.print("\tMarket deal proberen");
		//extra, check of het een market deal is.. dan maakt de prijs niet uit.. dus  niet onderstaande stappen doorlopen.
		if(ticket.getType().equals("market"))
		{
			System.out.print("market match trachten\n");
			//System.out.println(" doBidAskMeetsTransactions proberen te maken\n"); 
			while(marketMatchPresent(ticket))
			{
				System.out.println("market deal gevonden!!!");
				ticket = doBidAskMeetsTransactions(ticket);
			}
			//System.out.printf("bidAskMeetsTransactions done\n");
		} else
		
		
		/*
		 * 2. De engine checkt of de nieuwe order beter is dan een ander al bestaand bod in het 
		 * orderboek. Dat is het geval als een bid-bod een hogere bet heeft dan de maximale bidprijs 
		 * in het orderboek (de user is bereid meer te betalen dan iemand anders), of als een ask-bod 
		 * een lagere bet heeft dan de minimale askprijs (de user is bereid met minder genoegen te nemen 
		 * dan iemand anders). In die gevallen kan er een match tot stand komen, omdat vraag en aanbod 
		 * dichter bij elkaar zijn gekomen. Is dat niet zo, kan stap 3 overgeslagen worden.
		 */
		
		//System.out.println("bid?"+ticket.getBidOrAsk());
		//System.out.println("price" + ticket.getPrice());
		//System.out.println("bidhigh " + bidHigh[ticket.getOutcome()]);
		System.out.println("--> Geen market deal");
		System.out.print("\tKijk of bestaand bod slechter is");
		if(	(ticket.getBidOrAsk()  == ASK && ticket.getPrice() > askLow[ticket.getOutcome()] ) ||
			(ticket.getBidOrAsk()  == BID && ticket.getPrice() < bidHigh[ticket.getOutcome()]) )
		{
			/* 
			 * 4. Als er geen match gemaakt kan worden voor de nieuwe order, of de nieuwe order kan niet volledig
			 * vervuld worden met behulp van de bestaande orders in het orderboek, dan kan deze order (of het
			 * resterende deel ervan) in het orderboek (wachtrij) geplaatst worden.
			 */
			
			System.out.println("--> Dit bod is slechter, voeg toe aan orderbook/userHT");
			addTicketToOrderBookAndUserHashTable(ticket, userHashTable);
			//System.out.printf("ticket added to orderBook\n");
		} else
		{
			/*
			 * 3. De nieuwe order is beter, dus het heeft zin om te kijken of er nu wel een match gemaakt kan
			 * worden. 
			 * 	a. De engine checkt vervolgens eerst of dit nieuwe bod een match tot stand kan brengen met
			 * 	een order van de andere orderboek-zijde. Dus bij een bid-bod van 75 cent, wordt eerst 
			 * 	gecheckt of er een ask-bod van 75 cent of lager tegenover staat. 
			 */
			System.out.println("--> order was beter, match proberen");
			System.out.print("\tKijken of vraag en aanbod elkaar tegenkomen");
			int amount = ticket.getAmount();
			while ( bidAskMeets(ticket) && ticket.getAmount() > 0)
			{
			 	ticket = doBidAskMeetsTransactions(ticket);	
			 	System.out.println("match gemaakt");
			}
			if(amount == ticket.getAmount())
			{
				System.out.println(" --> geen vraag/aanbod match");
			}
			/*
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
				/*
				 *	i. Voor de bid-zijde kan dat, als de optelsom van maximale bets (prijzen) van elke
				 * 	outcome, optelt tot 100 cent of meer. Dus: user x is bereid 60 cent te betalen voor
				 * 	'Nederland wint', user y 30 cent voor 'Duitsland wint', en user z 10 cent voor
				 * 	'Gelijkspel'. Samen bieden ze exact 100 cent, dus kan er een match tot stand worden 
				 * 	gebracht aan de bid-zijde. Er hoeft geen bod te zijn op alle uitkomsten. Theoretisch
				 * 	zou iemand dus 100 cent op 'Nederland' kunnen bieden, waarbij er een 'match' tot stand
				 * 	komt. Zo lang de 100 cent maar gehaald wordt. In het geval dat niet elke outcome
				 * 	vertegenwoordigd is in de match, gaan de tickets die gecreeerd worden op de
				 * 	niet-vertegenwoordigde outcomes in 'onze pot'.
				 */
				
				System.out.print("\tbidMatch proberen te vinden");
				int oldAmount = ticket.getAmount();
				while(bidMatchPresent(ticket) && ticket.getAmount() > 0 && ticket.getBidOrAsk() == BID)
				{
					System.out.println("er is een bid match");
					ticket = doBidMatchTransactions(ticket);
				}
				if(oldAmount == ticket.getAmount())
				{
					System.out.println(" --> geen bidmatch gevonden");
					
				}
				//System.out.printf("tickets gecreeerd\n");
				/* 
				 * 		ii. Voor de ask-zijde kan dat, als de optelsom van minimale bets (prijzen) van elke
				 * 		outcome, optelt tot 100 cent of minder. Dus: user x is bereid 60 cent te betalen voor
				 * 		'Nederland wint', user y 25 cent voor 'Duitsland wint', en user z 10 cent voor
				 * 		'Gelijkspel'. Samen bieden ze 95 cent, dus kan er een match tot stand worden gebracht
				 * 		aan de ask-zijde. Belangrijk hierbij is dat - itt tot bij de bid-zijde - hier alle 
				 * 		outcomes vertegenwoordigd moeten zijn. Een match aan de ask-zijde kan dus alleen tot 
				 * 		stand komen als er en op alle mogelijke uitkomsten een user zijn tickets wil verkopen,
				 * 		en dat de optelsom van de minimale bets per uitkomst gelijk zijn of kleiner dan 100 cent.
				 */
				
				System.out.print("\tAskMatch proberen te vinden");
				oldAmount = ticket.getAmount();
				while(askMatchPresent(ticket) && ticket.getAmount() > 0 && ticket.getBidOrAsk() == ASK)
				{
					ticket = doAskMatchTransactions(ticket);
				}
				
				if(oldAmount == ticket.getAmount())
				{
					System.out.println(" --> geen askMatch gevonden");
					
				}
		}
		//add remaining tickets to orderBook, and userHashTable
		System.out.println("**** adding remaining tickets to orderBook en hashtabel\n");
		addTicketToOrderBookAndUserHashTable(ticket, userHashTable);
		System.out.printf("added remaining to orderBook en hashtabel");
	}

	public Ticket doMarketTransactions(Ticket ticket1)
	{
		Ticket ticket2;
		if(ticket1.getOutcome() == BID)
		{
			ticket2  = orderBook[askLow[ticket1.getOutcome()]][ticket1.getTicketIndex()+1].front();
			
			int tradeAmount = getTradeAmount(ticket1, ticket2);
			ticket1.decreaseAmount(tradeAmount);
			ticket1.decreaseAmount(tradeAmount);
		} else
		//if(ticket1.getOutcome() == ASK)
		{
			ticket2  = orderBook[bidHigh[ticket1.getOutcome()]][ticket1.getTicketIndex()-1].front();
		} 
			
	
		
		return ticket1;
	}
	
	
	//check if a market deal can be made
	public boolean marketMatchPresent(Ticket ticket)
	{
		if( (ticket.getAmount() > 0 && ticket.getBidOrAsk() == BID && bidHigh[ticket.getOutcome()] > 0) ||
			(ticket.getAmount() > 0 && ticket.getBidOrAsk() == ASK && askLow[ticket.getOutcome()] < 100))		
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	//This function checks if there is an ask-Match possible
	public boolean askMatchPresent(Ticket ticket)
	{
		int[] tempAskLow = askLow;
		tempAskLow[ticket.getOutcome()] = ticket.getPrice();
		
		//check if al outcomes are represented and sum of minimums no more then  100c
		//if less than 100, no minimum cant be 100
		if((tempAskLow[0] + tempAskLow[1] + tempAskLow[2]) < 100)
		{
			return true;
		}
		else return false;
	}
	
	//This function checks if there is an bid-Match possible
	public boolean bidMatchPresent(Ticket ticket)
	{
		int[] tempBidHigh = bidHigh;
		tempBidHigh[ticket.getOutcome()] = ticket.getPrice();
		if(getSumArray(tempBidHigh) >= 100)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//This function does the askMatch transactions
	public Ticket doAskMatchTransactions(Ticket ticket)
	{
		Ticket ticket1 = ticket;
		Ticket ticket2 = orderBook[askLow[(ticket.getOutcome() + 1)%3]][(ticket.getOutcome() ) % 3].front();
		Ticket ticket3 = orderBook[askLow[(ticket.getOutcome() + 2)%3]][(ticket.getOutcome() ) % 3].front();
		
		int tradeAmount = getTradeAmount(ticket1, ticket2, ticket3);
		
		ticket1.decreaseAmount(tradeAmount);
		ticket2.decreaseAmount(tradeAmount);
		ticket3.decreaseAmount(tradeAmount);
		
		//delete amount of ticket2 in userHashTable
		userHashTable.deleteTicket(orderBook[askLow[ticket2.getOutcome()]][ticket2.getTicketIndex()].front(), tradeAmount);
	 	
		//delete amount ticket from orderbook
		removeTicketAmountFromOrderBook(ticket2);
		
		//delete amount of ticket3 in userHashTable
		userHashTable.deleteTicket(orderBook[askLow[ticket3.getOutcome()]][ticket3.getTicketIndex()].front(), tradeAmount);
	 	
		//delete amount ticket 3 from orderbook
		removeTicketAmountFromOrderBook(ticket3);
		return ticket;
	}
	
	//This function processes the bidMatch transactions
	public Ticket doBidMatchTransactions(Ticket ticket)
	{
		int[] tempBidHigh = bidHigh;
		tempBidHigh[ticket.getOutcome()] = ticket.getPrice();
	
			Ticket ticket1 = ticket;
			Ticket ticket2;
			Ticket ticket3;
			System.out.println("bh"+ bidHigh[2]);
			System.out.println("blaaa" + ticket.getOutcome());
			System.out.println("bla" + (ticket.getOutcome() + 1)%3);
			if(bidHigh[(ticket.getOutcome() + 1)%3] != 0)
			{
				ticket2 = orderBook[bidHigh[(ticket.getOutcome() + 1)%3]][(ticket.getOutcome() + 1) % 3].front();
				System.out.println("hierr?");
			} else
			{
			//	public Ticket(String activity, String userID, int outcome, int price, int amount, int bidOrAsk, int date)
				
				ticket2 = new Ticket(ticket1.getActivity(), "pot", (ticket1.getOutcome() + 1)%3 , ticket1.getPrice(), ticket1.getAmount(), ticket1.getBidOrAsk(), 1);
				ticket2.setAmount(ticket1.getAmount());
				
			}
			
			
			if(bidHigh[(ticket.getOutcome() + 2)%3] != 0)
			{
				ticket3 = orderBook[bidHigh[(ticket.getOutcome() + 2)%3]][(ticket.getOutcome() + 2) % 3].front();
				System.out.println("kom ik hier?");
			} else
			{
				System.out.println("kom ik h3ier?");
				
				ticket3 = new Ticket(ticket.getActivity(), "pot", (ticket.getOutcome() + 2)%3 , ticket.getPrice(), ticket.getAmount(), ticket.getBidOrAsk(), 1);
				ticket3.setAmount(ticket1.getAmount());
			}
			System.out.println(ticket1.getAmount());
			System.out.println(ticket2.getAmount());
			System.out.println(ticket3.getAmount());
			
			int tradeAmount = getTradeAmount(ticket1, ticket2, ticket3);
			
			Ticket advantage = getAdvantage(ticket1, ticket2, ticket3);
			int advantageSurplus = getSumArray(tempBidHigh) - 100;
			System.out.println("Userid " + advantage.getUserID() + " heeft " + ADVANTAGE + "-voordeel van " + advantageSurplus + " cent");
			
			ticket1.decreaseAmount(tradeAmount);
			ticket2.decreaseAmount(tradeAmount);
			ticket3.decreaseAmount(tradeAmount);
			
			//if not tickets from "pot", delete from userHashTable and orderBook
			if(!ticket2.getUserID().equals("pot"))
			{
				//delete amount of ticket2 in userHashTable
				userHashTable.deleteTicket(orderBook[bidHigh[ticket2.getOutcome()]][ticket2.getTicketIndex()].front(), tradeAmount);
	 		
				//delete amount ticket from orderbook
				removeTicketAmountFromOrderBook(ticket2);
			}
			
			//if not tickets from "pot", delete from userHashTable and orderBook
			if(!ticket3.getUserID().equals("pot"))
			{
				//delete amount of ticket3 in userHashTable
				userHashTable.deleteTicket(orderBook[bidHigh[ticket3.getOutcome()]][ticket3.getTicketIndex()].front(), tradeAmount);
	 		
				//delete amount ticket 3 from orderbook
				removeTicketAmountFromOrderBook(ticket3);
			}
	 	
		return ticket1;
	}
	
	//This function returns the ticket which has the advantage
	public Ticket getAdvantage(Ticket ticket1, Ticket ticket2, Ticket ticket3)
	{
		GregorianCalendar testDate = new GregorianCalendar(1,1,1,1,1,1);
		Ticket advantage = ticket1;
		
		if(this.ADVANTAGE.equals("first"))
		{
			GregorianCalendar oldestDate =  ticket1.getDate();
			if(!ticket2.getDate().equals(testDate) && ticket2.getDate().before(oldestDate))
			{
				advantage = ticket2;
				oldestDate = ticket2.getDate();
			}
			
			if(!ticket3.getDate().equals(testDate) && ticket3.getDate().before(oldestDate))
			{
				advantage = ticket3;
				oldestDate = ticket2.getDate();
			}
		} else 
		if(this.ADVANTAGE.equals("last"))
		{
			GregorianCalendar oldestDate =  ticket1.getDate();
			if(!ticket2.getDate().equals(testDate) && ticket2.getDate().after(oldestDate))
			{
				advantage = ticket2;
				oldestDate = ticket2.getDate();
			}
			
			if(!ticket3.getDate().equals(testDate) && ticket3.getDate().after(oldestDate))
			{
				advantage = ticket3;
				oldestDate = ticket2.getDate();
			}
			
			
		}
		return advantage;
	}
	
	//This function replaces old ticket with the new ticket (amount is changed)
	public void removeTicketAmountFromOrderBook(Ticket ticket)
	{
		if(ticket.getAmount() == 0)
 		{
 			orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex()].dequeue();
 			
 			//check if the queue is now empty
 			if(orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex()].isEmpty())
 			{
 				//set new bidHigh
 				setNewBidHigh(ticket.getOutcome(), ticket.getPrice());
 			}
 		}
 		else
 		{
 			orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex()].editFront(ticket);
	 	}
	}
	
	//This function returns the sum of an array
	public int getSumArray(int[] array)
	{
		int sum = 0;
		for(int i: array)
		{
			sum += i;
		}
		return sum;
	}
	
	//This function processes the bidAskMeetsTransactions
	public Ticket doBidAskMeetsTransactions(Ticket ticket)
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
	 	return ticket;
	}
	
	//this function sets the new bidHigh for an outcome
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
	
	///this function sets the new askLow for an outcome
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
	
	//this function checks if bid and ask meets, bid>ask
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
	
	//if no matches can be made this function adds ticket to orderbook and userhashtable
	public void addTicketToOrderBookAndUserHashTable(Ticket ticket, UserHashTable userHashTable)
	{
		//if(orderBook[ticket.getPrice()][ticket.getTicketIndex()].isEmpty())
			{
				//orderBook[ticket.getPrice()][ticket.getTicketIndex()] = new TicketArrayQueue();
			}
		
		orderBook[ticket.getPrice()][ticket.getTicketIndex()].enqueue(ticket);
		System.out.println("ticket aan orderboek toegevoegd");
		userHashTable.addTicket(ticket);
	}
	
	//this function deletes the old ticket of the user with a different price
	public void deleteTicket(Ticket ticket)
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
	
	//if you want to delete 
	public void onlyOneInQueue(Ticket ticket, int askOrBidPrice)
	{
		//dequeue this ticket
		orderBook[askOrBidPrice][ticket.getTicketIndex()].dequeue();
		
		//if this was the highest bid / lowest Ask , change into new lowest or highest
		if(ticket.getBidOrAsk() == BID && bidHigh[ticket.getOutcome()] == askOrBidPrice)
		{
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
}
