import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;


public class Ticket{
	private String activity;
	private String outcome2;
	private int outcome;
	private String userId;
	private int prize;
	private String type;
	private Calendar date;
	private int amount;
	private int amountLeft;
	private boolean locked;
	private int bidOrAsk;

	public Ticket(String stringToParse)
	{	//{ activity: Nederland-Duitsland, type: limit, userid: USER6786, outcome: 2, bet: 87, tickets: 5, bidask: ask, time: 01:02:03:04:05:06 }
		
		//{ activity: activity, type: type, userid: userid, outcome: outcome, bet: bet, tickets: tickets, bidask: bidask, time: time }
		Scanner lineScanner = new Scanner(stringToParse).useDelimiter("\\s*[{,:}]\\s*");
		
		lineScanner.next();
		
		this.activity = lineScanner.next();							lineScanner.next();
		this.type = lineScanner.next(); 								lineScanner.next();
		this.userId = lineScanner.next(); 							lineScanner.next();
		this.outcome = lineScanner.nextInt(); 		lineScanner.next();
		this.prize = lineScanner.nextInt(); 			lineScanner.next();
		this.amount = lineScanner.nextInt();			lineScanner.next();
		this.amountLeft = this.amount;
		String bidOrAsk = lineScanner.next();
		if(bidOrAsk.equals("bid"))
		{
			this.bidOrAsk = 0;
		} else
		{
			this.bidOrAsk = 1;
		}
		
		lineScanner.next();
		
		
		int year = lineScanner.nextInt();
		int month = lineScanner.nextInt();
		int day = lineScanner.nextInt();
		int hour = lineScanner.nextInt();
		int minute = lineScanner.nextInt();
		int second = lineScanner.nextInt();
		this.date = new GregorianCalendar(year, month, day, hour, minute, second);
		lineScanner.close();
	}

	public String getTicketKey()
	{
		return getActivity() +":" + getOutcome() + ":" + getBidOrAsk();
	}
	
	public String getActivity()
	{
		//String activityAndOutcome = this.activity + ":" + outcome;
		return activity;//AndOutcome;
	}
	
	public int getAmount()
	{
		return this.amount;
	}
	
	public boolean setAmount(int amount)
	{
		boolean correct = false;
		
		if(amount >= 0){
			correct = true;
			this.amount = amount;
			if(this.amountLeft > this.amount)
			{
				this.amountLeft = this.amount;
			}
		}
		return correct;
	}
	
	public int getAmountLeft()
	{
		return this.amountLeft;
	}
	
	public boolean setAmountLeft(int left)
	{
		boolean succes = false;
		if(left >= 0)
		{
			succes = true;
			this.amountLeft = left;
		}
		return succes;
	}
	
	/*
	 * This function return a number from 1-6
	 * win		draw	loss
	 * bid	ask	bid	ask	bid	ask	
	 * 1	2	3	4	5	6
	 * 
	 */
	public int getTicketIndex()
	{
		return outcome*2 + bidOrAsk;
	}
	
	
	
	/*
	 * decrease amount with int amountToRemove
	 * if you want delete more than there are tickets, set amount too zero
	 * return true if amount == zero, else false
	 */
	public boolean decreaseAmount(int amountToRemove)
	{
		if(this.amount >= amountToRemove)
		{
			this.amount -= amountToRemove;
			return true;
		} else 
		{
			this.amount = 0;
		}
		
		if(this.amount ==0)
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	
	public Calendar getDate()
	{
		return this.date;
	}
	
	public String dateToString()
	{
		SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmSS");
		return date_format.format(this.date.getTime());
	}
	
	public int getPrize()
	{
		return this.prize;
	}
	
	public boolean setLocked()
	{
		this.locked = true;
		return this.locked;
	}
	
	public boolean setUnLocked()
	{
		this.locked = false;
		return this.locked;
	}
	
	public int getOutcome()
	{
		return this.outcome;
	}
	
	public int getBidOrAsk()
	{
		return this.bidOrAsk;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public String getUserID()
	{
		return this.userId;
	}
	
	public String toString()
	{
		String output = "wedstrijd " + activity + "\n" + date.toString();
		return output;
	}
}