public class TokenArrayQueue extends Object implements TokenQueue{
	protected Token[] arr;
	public static int DEFAULT_INIT_CAPACITY = 16;
	protected int front;
	protected int rear;

	public TokenArrayQueue(){
		arr = new Token[DEFAULT_INIT_CAPACITY];
		front = 0;
		rear = 0;
	}

	public TokenArrayQueue(int initCapacity){
		if(initCapacity <= 0){
			arr = new Token[DEFAULT_INIT_CAPACITY];
		}else{
			arr = new Token[initCapacity];
		}
		front = 0;
		rear = 0;
	}

	public Token dequeue(){
		Token frontElem = arr[front];
		front++;
		return frontElem;
	}

	public Token enqueue(Token elem){
		ensureCapacity();
		rear++;
		arr[rear] = elem;
		return elem;
	}

	protected void ensureCapacity(){
		int size = size();

		if(rear >= size){
			Token[] temparray = new Token[2*size];

			for(int i = 0; i <= arr.length; i++){
				temparray[i] = arr[i];
			}

			arr = temparray;
		}
	}

	public Token front(){
		return arr[front];
	}

	public boolean isEmpty(){
		return rear == 0;
	}

	public int size(){
		return arr.length;
	}
}