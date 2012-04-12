import java.util.Date;

public class Ticket{
	private String match;
	private int outcome;
	private double prize;
	private Date date;
	private int amount;
	private boolean locked;

	public Ticket(String match, int outcome, double prize, int amount, Date date)
	{
		this.match = match;
		this.prize = prize;
		if(amount >= 0)
		{
			this.amount = amount;
		}else
		{
			this.amount = 1;
		}
		this.date = date;
		this.locked = false;
		this.outcome = outcome;
	}

	public Ticket(String match, int outcome, double prize, int amount)
	{
		this.match = match;
		this.prize = prize;
		if(amount >= 0)
		{
			this.amount = amount;
		}else
		{
			this.amount = 1;
		}
		this.date = new Date();
		this.locked = false;
		this.outcome = outcome;
	}

	public int getAmount()
	{
		return this.amount;
	}
	
	public boolean setAmount(int amount)
	{
		boolean correct = true;
		
		if(amount <= 0){
			correct = false;
		}else
		{
			this.amount = amount;
		}
		
		return correct;
	}
	
	public Date getDate()
	{
		return this.date;
	}
	
	public double getPrize()
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
}