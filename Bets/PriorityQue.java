public class PriorityQue
{
	private Ticket[] que;
	private int size;
	private int maxCap;
	private static int DEFAULT_INIT_CAP = 128;
	
	public PriorityQue(int maxCap)
	{
		if(maxCap <= 0)
		{
			this.que = new Ticket[DEFAULT_INIT_CAP];
		}else
		{
			this.que = new Ticket[maxCap];
		}
	}

	public PriorityQue()
	{
		this.que = new Ticket[DEFAULT_INIT_CAP];
	}
	
	public int getSize()
	{
		int size = -1;
		if(this.size != 0)
		{
			size = this.size;
		}
		return size;
	}
	
	public boolean isEmpty()
	{
		boolean empty = false;
		if(this.size == 0)
		{
			empty = true;
		}
		return empty;
	}
	
	public void ensureCapacity()
	{
		if(this.maxCap <= this.size){
			this.maxCap = this.maxCap * 2;
			Ticket[] temparray = new Ticket[maxCap];

			for(int i = 0; i <= this.que.length; i++){
				temparray[i] = this.que[i];
			}

			this.que = temparray;
		}
	}
	
	public Ticket dequeue()
	{
		return que[size--];
	}
	
	public Ticket peek()
	{
		return que[size - 1];
	}
	
	public void insert(Ticket ticket)
	{
		ensureCapacity();
		int i;
		if (this.size == 0)
		{
			this.que[size++] = ticket; // insert at 0
		}else
		{
			for (i = size - 1; i >= 0; i--) // start at end,
			{
				if (ticket.getPrize() > que[i].getPrize()) // if new item larger,
				{
					que[i + 1] = que[i]; // shift upward
				}else
				{
					// if smaller,
					break; // done shifting
				}
			}
			que[i + 1] = ticket; // insert it
			size++;
		} // end else (nItems > 0)
	}
}