public class TicketArrayQueue extends Object implements TicketQueue{
	protected Ticket[] arr;
	public static int DEFAULT_INIT_CAPACITY = 32;
	protected int front;
	protected int rear;

	public TicketArrayQueue(){
		arr = new Ticket[DEFAULT_INIT_CAPACITY];
		front = 0;
		rear = 0;
	}

	public TicketArrayQueue(int initCapacity){
		if(initCapacity <= 0){
			arr = new Ticket[DEFAULT_INIT_CAPACITY];
		}else{
			arr = new Ticket[initCapacity];
		}
		front = 0;
		rear = 0;
	}

	public Ticket dequeue(){
		Ticket frontElem = arr[front];
		front++;
		return frontElem;
	}

	public Ticket enqueue(Ticket elem){
		ensureCapacity();
		rear++;
		arr[rear] = elem;
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
		int size = size();

		if(rear >= size){
			Ticket[] temparray = new Ticket[2*size];

			for(int i = 0; i <= arr.length; i++){
				temparray[i] = arr[i];
			}

			arr = temparray;
		}
	}

	public Ticket front(){
		return arr[front];
	}

	public Ticket editFront(Ticket ticket)
	{
		arr[front] = ticket;
		return ticket;
	}

	public boolean isEmpty(){
		return rear == 0;
	}

	public int size(){
		return arr.length;
	}
}