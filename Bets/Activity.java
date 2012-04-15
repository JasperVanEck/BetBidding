import java.util.Date;

public class Activity
{
	private String matchName;
	private double[] buySellRate;
	private int result;
	private String resultString;
	private Date dateMatch;
	private int amountUsers;
	private int amountTickets;
	public TicketArrayQueue queueWinHome;
	public TicketArrayQueue queueWinTie;
	public TicketArrayQueue queueWinAway;
	
	public Activity(String matchName, double[] buySellRate, Date dateMatch)
	{
		this.matchName = matchName;
		this.buySellRate = buySellRate;
		this.dateMatch = dateMatch;
		this.outcomes = new int[3];
		this.queueWinHome = new TicketArrayQueue(64);
		this.queueWinTie = new TicketArrayQueue(64);
		this.queueWinAway = new TicketArrayQueue(64);
	}
	
	public String getMatchName()
	{
		return this.matchName;
	}
	
	public double getBuyRate()
	{
		return buySellRate[0];
	}
	
	public boolean setBuyRate(double buyRate)
	{
		boolean set = true;
		if(buyRate < 0 || buyRate > 1)
		{
			set = false;
		}else
		{
			this.buySellRate[0] = buyRate;
		}
		return set;
	}
	
	public double getSellRate()
	{
		return buySellRate[0];
	}
	
	public boolean setSellRate(double sellRate)
	{
		boolean set = true;
		if(sellRate < 0 || sellRate > 1)
		{
			set = false;
		}else
		{
			this.buySellRate[0] = sellRate;
		}
		return set;
	}
	
	public int[] getOutcomes()
	{
		return this.outcomes;
	}
	
	public boolean setResult(int result)
	{
		this.result = result;
		//Ook nog iets neer zetten van welk team gewonnen heeft ofzo? met resultString.
		return true;
	}
	
	public Date getDate()
	{
		return this.dateMatch;
	}
	
	public boolean setDate(Date newDate)
	{
		this.dateMatch = newDate;
		return true;
	}
	
	public int getAmountUsers()
	{
		return this.amountUsers;
	}
	
	public boolean updateAmountUsers(boolean add)
	{
		if(add)
		{
			this.amountUsers++;
		}else
		{
			this.amountUsers--;
		}
		return true;
	}
	
	public int getAmountTickets()
	{
		return this.amountTickets;
	}
	
	public boolean updateAmountTickets(boolean add)
	{
		if(add)
		{
			this.amountTickets++;
		}else
		{
			this.amountTickets--;
		}
		return true;
	}
}