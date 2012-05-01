import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;


public class Ticket{
	private String activity;
	private String outcome2;
	private int outcome;
	private String userId;
	private int price;
	private String type;
	private GregorianCalendar date;
	private int amount;
	private int amountLeft;
	private boolean locked;
	private int bidOrAsk;

	public Ticket(String stringToParse)
	{	
		
		//		{"activity":"activity_test","userid":604243,"outcome":0,"bet":79,"tickets":116,"bidask":"bid","time":"01\/05\/2012 11:50:45:000000"}

		
		Scanner lineScanner = new Scanner(stringToParse).useDelimiter("\\s*[ {:\",\\\\/]\\s*");
		
		lineScanner.next(); lineScanner.next(); lineScanner.next(); lineScanner.next();
		activity = lineScanner.next();
		System.out.println("activity: " + activity);
		
		lineScanner.next(); lineScanner.next(); lineScanner.next(); lineScanner.next();
		userId = lineScanner.next();
		System.out.println("userid: " + userId);
		
		lineScanner.next(); lineScanner.next(); lineScanner.next();
		
		this.outcome = lineScanner.nextInt();
		System.out.println("outcome: " + outcome);
		
		lineScanner.next(); lineScanner.next(); lineScanner.next();
		this.price = lineScanner.nextInt(); 	
		System.out.println("price: " + price);
		
		lineScanner.next(); lineScanner.next(); lineScanner.next();
		this.amount = lineScanner.nextInt(); 	
		System.out.println("amount: " + amount);
		
		lineScanner.next(); lineScanner.next(); lineScanner.next(); lineScanner.next();
		String bidOrAsk = lineScanner.next();
		if(bidOrAsk.equals("bid"))
		{
			this.bidOrAsk = 0;
		} else
		{
			this.bidOrAsk = 1;
		}
		System.out.println("bidOrAsk: " + this.bidOrAsk);
		
		lineScanner.next();  lineScanner.next();  lineScanner.next();lineScanner.next(); lineScanner.next();
		int day = lineScanner.nextInt(); 	 
		System.out.println("day: " + day);
		
		lineScanner.next();		 
		int month = lineScanner.nextInt() -1; 	 
		System.out.println("month: " + month);
		 
		lineScanner.next(); //lineScanner.next();  		 
		int year = lineScanner.nextInt(); 	 
		System.out.println("year: " + year);
		
		//lineScanner.next(); //lineScanner.next();  		 
		int hour = lineScanner.nextInt(); 	 
		System.out.println("hour: " + hour);
		
		int minute = lineScanner.nextInt(); 	 
		System.out.println("minute: " + minute);
		
		int second = lineScanner.nextInt(); 	 
		System.out.println("second: " + second);
		
		int millisec = lineScanner.nextInt();
		System.out.println("millisec: " + millisec);
		this.date = new GregorianCalendar(year, month, day, hour, minute, second);
		//date.set(year,  month, day, hour, second, minute);
		
		System.out.println(date.getTime());
		
			
		
		lineScanner.close();
	}
	
	public Ticket(String activity, String type, String userID, int outcome, int price, int amount, int bidOrAsk, int date)
	{
		this.activity = activity;
		this.type = type;
		this.userId = userID;
		this.outcome = outcome;
		this.price = price;
		this.amount = amount;
		this.amountLeft = this.amount;
		this.bidOrAsk = bidOrAsk;
		this.date = new GregorianCalendar(date, date, date, date, date, date);
		
	
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
	
	
	public GregorianCalendar getDate()
	{
		return this.date;
	}
	
	public String dateToString()
	{
		SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmSS");
		return date_format.format(this.date.getTime());
	}
	
	public int getPrice()
	{
		return this.price;
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