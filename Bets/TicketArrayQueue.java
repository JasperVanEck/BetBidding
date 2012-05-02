public class TicketArrayQueue extends Object implements TicketQueue{
	protected Ticket[] arr;
	public static int DEFAULT_INIT_CAPACITY = 32;
	protected int front;
	protected int rear;
	protected int nItems;

	public TicketArrayQueue(){
		arr = new Ticket[DEFAULT_INIT_CAPACITY];
		front = 0;
	    rear = -1;
	    nItems = 0;
	}
	

	public TicketArrayQueue(int initCapacity){
		if(initCapacity <= 0){
			arr = new Ticket[DEFAULT_INIT_CAPACITY];
		}else{
			arr = new Ticket[initCapacity];
		}
		front = 0;
		rear = -1;
		nItems = 0;
	}

	public Ticket dequeue(){
		Ticket frontElem = arr[front++];
		nItems--;                      // one less item
	    return frontElem;
	}

	public Ticket enqueue(Ticket elem){
		ensureCapacity();
		arr[++rear] = elem;
		nItems++;
		return elem;
	}
	
	public void deleteTicket(Ticket ticket)
	{
		//iterate through tickets.
		for(int i = front; i <= rear; i++)
			//if ticket in arr[i] matches userId
			if(arr[i].getUserID() == ticket.getUserID())
			{
				//move all remaining tickets one place up
				for(int j = i; j < rear; j++)
				{
					arr[j] = arr[j+1];
				}
				rear--;
			}
	}
	
	protected void ensureCapacity(){
		//int size = size();

		if(rear == DEFAULT_INIT_CAPACITY)
		{
			DEFAULT_INIT_CAPACITY *=2;
			Ticket[] temparray = new Ticket[2*DEFAULT_INIT_CAPACITY];
			for(int i = front; i <= rear; i++)
			{
				temparray[i] = arr[i];
			}
			arr = temparray;
		}
	}

	public Ticket front()
	{
		return arr[front];
	}

	public Ticket editFront(Ticket ticket)
	{
		arr[front] = ticket;
		return ticket;
	}

	public boolean isEmpty()    // true if queue is empty
	{
	   return (nItems==0);
	}
	
	public int size()           // number of items in queue
    {
		return nItems;
    }
} 