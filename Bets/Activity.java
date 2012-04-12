public class Activity
{
	private String matchName;
	private int[] buySellRate;
	private int[] outcomes;
	private int result;
	private Date dateMatch;
	private int amountUsers;
	private int amountTickets;
	public TicketArrayQueue queue;
	
	public Activity(String matchName, int[] buySellRate, Date dateMatch)
	{
		this.matchName = matchName;
		this.buySellRate = buySellRate;
		this.dateMatch = dateMatch;
		this.outcomes = new int[3]
		
	}
}