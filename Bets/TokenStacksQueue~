public class TokenStacksQueue extends Object implements TokenQueue{
	protected TokenStack in;
	protected TokenStack out;

	//Default constructor
	public TokenStacksQueue(){
		in = new TokenArrayStack();
		out = new TokenArrayStack();
	} 

	public TokenStacksQueue(TokenStack in, TokenStack out) {
		this.in = in;
		this.out = out;
	}

	//Check whether TokenStacksQueue is empty.
	public boolean isEmpty(){
		return in.isEmpty() && out.isEmpty();
	}

	//returns token at the front of the queue
	public Token front(){
		if(out.isEmpty()){
			rearrange();
		}
		return out.peek();
	}
	
	//Inserts Token at the rear of the queue
	public Token enqueue(Token elem){
		return in.push(elem);
	}

	//Retrieves the token at the front at the queue
	public Token dequeue(){
		if(out.isEmpty()){
			rearrange();
		}
		Token tmp = out.pop();
		return tmp;
	}

	//returns the size of the TokenStacksQueue.
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