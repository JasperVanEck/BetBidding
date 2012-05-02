import java.util.GregorianCalendar;
import java.sql.*;

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
	private int activityID;
	private int[] koers = new int[3];
	
	private final static int BID = 0;
	private final static int ASK = 1;
	
	DataBaseConnection dbConn;
	
	private String ADVANTAGE = "first";
	
	//This function creates a new orderBook
	public OrderBook(String activity)
	{
		this.activity = activity;
		for(int i = 0; i < askLow.length; i++)
		{
			this.askLow[i] = 100;
			this.bidHigh[i] = 0;
			this.koers[i] = 0;
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
		try{
			this.dbConn = new DataBaseConnection();
			this.activityID = this.dbConn.createBidHighAskLowTable(this.askLow, this.bidHigh, this.activity);
			this.dbConn.createOrderInputTable();
			this.dbConn.createOrderHandledTable(this.activity);
			this.dbConn.createKoersTable(this.activity);
		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		} 
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
		try{
		this.dbConn.insertNewOrder(ticket);
		/*
		 * 1. Als deze gebruiker al een bod in het orderboek heeft staan op dezelfde activiteit, 
		 * dezelfde uitkomst en hetzelfde type (bid of ask), dan vervangt deze nieuwe order de al 
		 * bestaande order. Concreet betekend dit dat de al bestaande order verwijderd zal worden uit 
	 	 * de wachtrij, en vervolgens worden onderstaande stappen - net als een order van een gebruiker 
		 * die nog niets in het orderboek - doorlopen.
		 */
		 //First check if user hasn't already this ticket. Then the already existing ticket must be deleted
		//System.out.println("koers bid: "+ getKoers("bid"));
		//System.out.println("koers ask: "+ getKoers("ask"));
		
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
		//System.out.println("koers bid: "+ getKoers("bid"));
		//System.out.println("koers ask: "+ getKoers("ask"));
		
		
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
		//System.out.println("koers bid: "+ getKoers("bid"));
		//System.out.println("koers ask: "+ getKoers("ask"));
		
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
			//System.out.println("koers bid: "+ getKoers("bid"));
			//System.out.println("koers ask: "+ getKoers("ask"));
			
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
		//	System.out.println("koers bid: "+ getKoers("bid"));
		//	System.out.println("koers ask: "+ getKoers("ask"));
			
			System.out.print("\tKijken of vraag en aanbod elkaar tegenkomen");
			int amount = ticket.getAmount();
			while ( bidAskMeets(ticket) && ticket.getAmount() > 0)
			{
			 	ticket = doBidAskMeetsTransactions(ticket);	
			 	System.out.println("match gemaakt");
			}
			//System.out.println("koers bid: "+ getKoers("bid"));
			//System.out.println("koers ask: "+ getKoers("ask"));
			
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
				
			//System.out.println("voor bidMatch zoeken: koers bid: "+ getKoers("bid"));
			//System.out.println("koers ask: "+ getKoers("ask"));
			
				System.out.print("\tbidMatch proberen te vinden");
				int oldAmount = ticket.getAmount();
				while(ticket.getBidOrAsk() == BID && ticket.getAmount() > 0 && bidMatchPresent(ticket)  )
				{
					//System.out.println("voor doBidMatch zoeken: koers bid: "+ getKoers("bid"));
					
					System.out.println(" --> er is een bid match");
					ticket = doBidMatchTransactions(ticket);
					//System.out.println("na doBidMatch: koers bid: "+ getKoers("bid"));
					
				}
				//System.out.println("na bidMatch: koers bid: "+ getKoers("bid"));
				//System.out.println("koers ask: "+ getKoers("ask"));
				
				
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
				//System.out.println("voor AskMatch koers bid: "+ getKoers("bid"));
				//System.out.println("koers ask: "+ getKoers("ask"));
				
				while( ticket.getAmount() > 0 && ticket.getBidOrAsk() == ASK && askMatchPresent(ticket) )
				{
					ticket = doAskMatchTransactions(ticket);
				}
				//System.out.println("na Askmatch koers bid: "+ getKoers("bid"));
				//System.out.println("koers ask: "+ getKoers("ask"));
				
				
				if(oldAmount == ticket.getAmount())
				{
					System.out.println(" --> geen askMatch gevonden");
					
				}
		}
		//add remaining tickets to orderBook, and userHashTable
		if(ticket.getAmount() > 0)
		{
			System.out.println("\t**** rest van tickets proberen aan orderBook en UserHT toevoegen");
			addTicketToOrderBookAndUserHashTable(ticket, userHashTable);
			System.out.println("\t**** rest van tickets toegevoegd");
		}
		System.out.println("********************************************");
		System.out.println("Ticket processed");
		System.out.println("********************************************");
		
		System.out.println("KOERS");
		System.out.println("bid "+ ": "+ getKoers("bid"));
		System.out.println("ask "+ ": "+ getKoers("ask"));
		System.out.println("********************************************");
		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
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
		//System.out.println("hier kom ik dus***************");
		//int[] tempBidHigh = bidHigh;
		//System.out.print("koers ervoor: " + getKoers("ask"));
				
		int[] tempAskLow = new int[3];
		tempAskLow[ticket.getOutcome()] = ticket.getPrice();
		tempAskLow[this.otherOutcomes(ticket.getOutcome(), 1)] = askLow[this.otherOutcomes(ticket.getOutcome(), 1)];
		tempAskLow[this.otherOutcomes(ticket.getOutcome(), 2)] = askLow[this.otherOutcomes(ticket.getOutcome(), 2)];
		//System.out.print("koers erna: " + getKoers("ask"));
				
		
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
		int[] tempBidHigh = new int[3];
		tempBidHigh[ticket.getOutcome()] = ticket.getPrice();
		tempBidHigh[this.otherOutcomes(ticket.getOutcome(), 1)] = bidHigh[this.otherOutcomes(ticket.getOutcome(), 1)];
		tempBidHigh[this.otherOutcomes(ticket.getOutcome(), 2)] = bidHigh[this.otherOutcomes(ticket.getOutcome(), 2)];
		
		//System.out.print("koers erna: " + getKoers("bid"));
		if(getSumArray(tempBidHigh) >= 100)
		{
			System.out.print(" --> Bid Surplus:" + ( getSumArray(tempBidHigh)-100));
			//System.out.println(getKoers("bid"));
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
		
		Ticket ticket2 = orderBook[askLow[this.otherOutcomes(ticket.getOutcome(), 1)]][this.otherBidColumnOrderBook(ticket.getOutcome(),1)+1].front();
		System.out.println("ticket2 bestaat?" + ticket2.getUserID());
		Ticket ticket3 = orderBook[askLow[this.otherOutcomes(ticket.getOutcome(), 2)]][this.otherBidColumnOrderBook(ticket.getOutcome(), 2)+1].front();
		
		int tradeAmount = getTradeAmount(ticket1, ticket2, ticket3);
		
		System.out.println("\tHANDEL:\tUserID: "+ ticket1.getUserID() + "\tPrice: "+ ticket1.getPrice() + "\tOutcome: " + ticket1.getOutcome()+ "\tbidOrAsk" + ticket1.getBidOrAsk() +"\tamount: "+ ticket1.getAmount()+" aan orderboek toegevoegd");
		ticket1.decreaseAmount(tradeAmount);
		
		System.out.println("\tHANDEL:\tUserID: "+ ticket2.getUserID() + "\tPrice: "+ ticket2.getPrice() + "\tOutcome: " + ticket2.getOutcome()+ "\tbidOrAsk" + ticket2.getBidOrAsk() +"\tamount: "+ ticket2.getAmount()+" aan orderboek toegevoegd");
		ticket2.decreaseAmount(tradeAmount);
		
		System.out.println("\tHANDEL:\tUserID: "+ ticket3.getUserID() + "\tPrice: "+ ticket3.getPrice() + "\tOutcome: " + ticket3.getOutcome()+ "\tbidOrAsk" + ticket3.getBidOrAsk() +"\tamount: "+ ticket3.getAmount()+" aan orderboek toegevoegd");
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
		Ticket ticket1 = ticket;
		Ticket ticket2;
		Ticket ticket3;
			
			//Kijk of ticket met andere uitkomst (1) bestaat
			if(bidHigh[this.otherOutcomes(ticket.getOutcome(), 1)] != 0)
			{
				
				
				//int testAmount = orderBook[bidHigh[this.otherOutcomes(ticket.getOutcome(), 1)]][this.otherBidColumnOrderBook(ticket.getOutcome(),1)].front().getAmount();
				//System.out.println("testAmount" + testAmount);
				ticket2 = orderBook[bidHigh[this.otherOutcomes(ticket.getOutcome(), 1)]][this.otherBidColumnOrderBook(ticket.getOutcome(),1)].front();
				//System.out.println("test"+ ticket2.getUserID());
			} else
			{
				ticket2 = new Ticket(ticket1.getActivity(), "pot", this.otherOutcomes(ticket.getOutcome(), 1) , ticket1.getPrice(), ticket1.getAmount(), ticket1.getBidOrAsk(), 1);
				ticket2.setAmount(ticket1.getAmount());
			}
			
			//Kijk of ticket met andere uitkomst (1) bestaat
			if(bidHigh[this.otherOutcomes(ticket.getOutcome(), 2)] != 0)
			{
				//int testAmount = orderBook[bidHigh[this.otherOutcomes(ticket.getOutcome(), 2)]][this.otherBidColumnOrderBook(ticket.getOutcome(),2)].front().getAmount();
				//System.out.println("testAmount" + testAmount);
				ticket3 = orderBook[bidHigh[this.otherOutcomes(ticket.getOutcome(), 2)]][this.otherBidColumnOrderBook(ticket.getOutcome(),2)].front();
				//System.out.println("test"+ ticket2.getUserID());
			} else
			{
				ticket3 = new Ticket(ticket1.getActivity(), "pot", this.otherOutcomes(ticket.getOutcome(), 2) , 0, ticket1.getAmount(), ticket1.getBidOrAsk(), 1);
				//ticket3.setAmount(ticket1.getAmount());
			}
			
			System.out.println("\tticket1, UserID: " +ticket1.getUserID() +" Outcome: "+ticket1.getOutcome() +" Amount: " +  ticket1.getAmount());
			System.out.println("\tticket2, UserID: " +ticket2.getUserID() +" Outcome: "+ticket2.getOutcome() +" Amount: " +  ticket2.getAmount());
			System.out.println("\tticket3, UserID: " +ticket3.getUserID() +" Outcome: "+ticket3.getOutcome() +" Amount: " +  ticket3.getAmount());
			
			int tradeAmount = getTradeAmount(ticket1, ticket2, ticket3);
			
			Ticket advantage = getAdvantage(ticket1, ticket2, ticket3);
			//decrease amount of ticket which has advantage
			if(ticket1.equals(advantage))
			{
				ticket1.setPrice(100 - ticket2.getPrice() - ticket3.getPrice());
			} else if(ticket2.equals(advantage))
			{
				ticket2.setPrice(100 - ticket1.getPrice() - ticket3.getPrice());
			} else
			{
				ticket3.setPrice(100 - ticket1.getPrice() - ticket2.getPrice());
			}
			
			
			
			
			int advantageSurplus = ticket1.getPrice() + bidHigh[this.otherOutcomes(ticket1.getOutcome(), 1)] + bidHigh[this.otherOutcomes(ticket1.getOutcome(), 2)]- 100;
			System.out.println("\tUserid " + advantage.getUserID() + " heeft " + ADVANTAGE + "-voordeel van " + advantageSurplus + " cent");
			
			System.out.println("\tHANDEL:\tUserID: "+ ticket1.getUserID() + "\tPrice: "+ ticket1.getPrice() + "\tOutcome: " + ticket1.getOutcome()+ "\tbidOrAsk: " + ticket1.getBidOrAsk() +"\tamount: "+ ticket1.getAmount()+" aan orderboek toegevoegd");
			ticket1.decreaseAmount(tradeAmount);
			
			System.out.println("\tHANDEL:\tUserID: "+ ticket2.getUserID() + "\tPrice: "+ ticket2.getPrice() + "\tOutcome: " + ticket2.getOutcome()+ "\tbidOrAsk: " + ticket2.getBidOrAsk() +"\tamount: "+ ticket2.getAmount()+" aan orderboek toegevoegd");
			ticket2.decreaseAmount(tradeAmount);
			
			System.out.println("\tHANDEL:\tUserID: "+ ticket3.getUserID() + "\tPrice: "+ ticket3.getPrice() + "\tOutcome: " + ticket3.getOutcome()+ "\tbidOrAsk: " + ticket3.getBidOrAsk() +"\tamount: "+ ticket3.getAmount()+" aan orderboek toegevoegd");
			ticket3.decreaseAmount(tradeAmount);
			
			
			
			//if not tickets from "pot", delete from userHashTable and orderBook
			if(!ticket2.getUserID().equals("pot"))
			{
				//delete amount of ticket2 in userHashTable
				userHashTable.deleteTicket(orderBook[bidHigh[ticket2.getOutcome()]][ticket2.getTicketIndex()].front(), tradeAmount);
	 		
				//delete amount ticket from orderbook
				System.out.println("ticket2, outcome: " + ticket2.getOutcome());
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
		if(ticket.getBidOrAsk() == BID)
		{
			if(ticket.getAmount() == 0)
			{
				orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex()].dequeue();
 			
				System.out.println("grootte"+ orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex()].isEmpty());
 			 		
				//check if the queue is now empty
				if(orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex()].isEmpty())
				{
					//set new bidHigh
					setNewBidHigh(ticket);//, ticket.getPrice());
				}
 			}
			else
			{
				orderBook[bidHigh[ticket.getOutcome()]][ticket.getTicketIndex()].editFront(ticket);
			}
		} else
		{
			if(ticket.getAmount() == 0)
			{
				orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex()].dequeue();
 			
				System.out.println("grootte"+ orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex()].isEmpty());
 			 		
				//check if the queue is now empty
				if(orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex()].isEmpty())
				{
					//set new bidHigh
					setNewAskLow(ticket);//, ticket.getPrice());
				}
 			}
			else
			{
				orderBook[askLow[ticket.getOutcome()]][ticket.getTicketIndex()].editFront(ticket);
			}
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
	 				setNewBidHigh(ticket);//, ticket.getPrice());
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
	 		System.out.println("\n\tbid ask meets!!!! wohooohooo");
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
	 				//System.out.println("kom ik hiuer\n\n\n &&&&&");
	 				setNewAskLow(ticket2);
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
	public void setNewBidHigh(Ticket ticket)// , int oldHigh)
	{
		//System.out.println("oldHigh" + oldHigh);
		//System.out.println("outcome" + outcome);
		
		int newHigh = 100;
		boolean changed = false;
		//oldHigh--;
		while(!changed && newHigh> 0)
		{
			if(!orderBook[newHigh][ticket.getTicketIndex()].isEmpty())
			{
				bidHigh[ticket.getOutcome()] = newHigh;
				changed = true;
			} else
			{
				newHigh--;
				//System.out.println("newHigh decr.");
			}
			if(newHigh ==0)
			{
				bidHigh[ticket.getOutcome()] = newHigh;
			}	
		}
	}
	
	///this function sets the new askLow for an outcome
	public void setNewAskLow(Ticket ticket)
	{
		int newLow = 0;
		boolean changed = false;
		while(!changed && newLow < 100)
		{
			if(!orderBook[newLow][ticket.getTicketIndex()].isEmpty())
			{
				askLow[ticket.getOutcome()] = newLow;
				changed = true;
			} else
			{
				newLow++;
			}
			if(newLow == 100)
			{
				askLow[ticket.getOutcome()] = newLow;
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
		if(	(ticket.getBidOrAsk() == ASK && ticket.getPrice() <= bidHigh[ticket.getOutcome()] ) ||
			(ticket.getBidOrAsk() == BID && ticket.getPrice() >= askLow[ticket.getOutcome()] ) )
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
		//	{
				//orderBook[ticket.getPrice()][ticket.getTicketIndex()] = new TicketArrayQueue();
		//	}
		//System.out.print("ticket index"+ ticket.getTicketIndex());
		orderBook[ticket.getPrice()][ticket.getTicketIndex()].enqueue(ticket);
		Ticket ticketTest = orderBook[ticket.getPrice()][ticket.getTicketIndex()].front();
		if(ticket.getBidOrAsk() == BID)
		{
			//System.out.println("Optie A");
			setNewBidHigh(ticket);
		} else
		{
			//System.out.println("Optie B");
			setNewAskLow(ticket);
		}
		System.out.println("\tHANDEL:\tUserID: "+ ticket.getUserID() + "\tPrice: "+ ticket.getPrice() + "\tOutcome: " + ticket.getOutcome()+ "\tbidOrAsk" + ticket.getBidOrAsk() +"\tamount: "+ ticket.getAmount()+" aan orderboek toegevoegd");
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
		System.out.println("kom ik hier??");
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
			//System.out.println("HIIERRRR DAN????");
			boolean setNew = false;
			int lowestAsk = askOrBidPrice+1;
			int ticketIndex = ticket.getTicketIndex();
			while(!setNew)
			{
				if(!orderBook[lowestAsk][ticketIndex].isEmpty() || lowestAsk == 100)
				{
					//System.out.println("kom ik hier??????\n\n\n\\n***");
					
					askLow[ticket.getOutcome()] = lowestAsk;
					setNew = true;
				} else
				{
					lowestAsk++;
				}
			}
		
		}
	}
	
	//return columnindexes of other bid columns in orderbook
	public int otherBidColumnOrderBook(int outcome, int oneOrTwo)
	{
		return ((2*outcome)+2*oneOrTwo)%6;
	}
	
	//return columnindexes of other bid columns in bidHigh or askLow
	public int otherOutcomes(int outcome, int oneOrTwo)
	{
		return ((outcome)+oneOrTwo)%3;
	}
}
