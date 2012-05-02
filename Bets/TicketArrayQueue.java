public class TicketArrayQueue extends Object implements TicketQueue{
	protected Ticket[] arr;
	public static int DEFAULT_INIT_CAPACITY = 32;
	protected int front;
	protected int rear;
	protected int nItems;

	/**
	 * TicketArrayQueue Constructor.
	 * Constructs a TicketArrayQueue, with the given default init Capacity.
	 */
	public TicketArrayQueue(){
		arr = new Ticket[DEFAULT_INIT_CAPACITY];
		front = 0;
		rear = -1;
		nItems = 0;
	}
	
	/**
	 * TicketArrayQueue Constructor.
	 * Constructs a TicketArrayQueue, with the given initCapacity.
	 */
	public TicketArrayQueue(int initCapacity){
		if(initCapacity <= 0)
		{
			arr = new Ticket[DEFAULT_INIT_CAPACITY];
		}else
		{
			arr = new Ticket[initCapacity];
		}
		front = 0;
		rear = -1;
		nItems = 0;
	}

	/**
	 * Returns and removes the front element of the queue.
	 */
	public Ticket dequeue(){
		Ticket frontElem = arr[front++];
		nItems--;                      // one less item
		return frontElem;
	}

	/**
	 * Puts the element at the back of the queue
	 */
	public Ticket enqueue(Ticket elem){
		ensureCapacity();
		arr[++rear] = elem;
		nItems++;
		return elem;
	}
	
	/**
	 * Deletes a ticket in the queue.
	 * It can be that the ticket is in the middle of queue, which isn't a problem.
	 */
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
	
	/**
	 * Ensures that the queue size is large enough for all the tickets to be added.
	 */
	protected void ensureCapacity(){
		//int size = size();

		if(rear == DEFAULT_INIT_CAPACITY)
		{
			DEFAULT_INIT_CAPACITY *= 2;
			Ticket[] temparray = new Ticket[2 * DEFAULT_INIT_CAPACITY];
			for(int i = front; i <= rear; i++)
			{
				temparray[i] = arr[i];
			}
			arr = temparray;
		}
	}

	/**
	 * Returns the front ticket of the queue.
	 */
	public Ticket front()
	{
		return arr[front];
	}

	/**
	 * Replaces the front ticket, with another one.
	 */
	public Ticket editFront(Ticket ticket)
	{
		arr[front] = ticket;
		return ticket;
	}

	/**
	 * Tells if there are any tickets left in the queue.
	 */
	public boolean isEmpty()    // true if queue is empty
	{
	   return (nItems==0);
	}
	
	/**
	 * Returns the size of the queue.
	 */
	public int size()           // number of items in queue
	{
		return nItems;
	}
} 