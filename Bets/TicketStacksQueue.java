public class TicketStacksQueue extends Object implements TicketQueue{
	protected TicketStack in;
	protected TicketStack out;

	//Default constructor
	public TicketStacksQueue(){
		in = new TicketArrayStack();
		out = new TicketArrayStack();
	} 

	public TicketStacksQueue(TicketStack in, TicketStack out) {
		this.in = in;
		this.out = out;
	}

	//Check whether TicketStacksQueue is empty.
	public boolean isEmpty(){
		return in.isEmpty() && out.isEmpty();
	}

	//returns token at the front of the queue
	public Ticket front(){
		if(out.isEmpty()){
			rearrange();
		}
		return out.peek();
	}
	
	//Inserts Ticket at the rear of the queue
	public Ticket enqueue(Ticket elem){
		return in.push(elem);
	}

	//Retrieves the token at the front at the queue
	public Ticket dequeue(){
		if(out.isEmpty()){
			rearrange();
		}
		Ticket tmp = out.pop();
		return tmp;
	}

	//returns the size of the TicketStacksQueue.
	public int size(){
		return in.size() + out.size();
	}

	//If 'out' is empty, it stores all the instances of 'in' into 'out'.
	protected boolean rearrange(){
		if(out.isEmpty()){
			while(!in.isEmpty()){
				out.push(in.pop());
			}
			
			return true;
		}
		
		return false;
	}
}