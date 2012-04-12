/**
 * Defines an interface for queues that contain {@link Token} instances.
 * 
 * @author Stephan Schroevers
 */
public interface TokenQueue {

  /**
   * Tells if the queue is empty.
   * 
   * @return <code>true</code> if the queue is empty, <code>false</code>
   *         otherwise
   */
  public boolean isEmpty();

  /**
   * Adds the given token to the rear of the queue.
   * 
   * @param elem
   *          the {@link Token} instance that must be added
   * @return the <code>Token</code> instance that was added
   */
  public Token enqueue(Token elem);

  /**
   * Returns the token at the front of the queue.
   * 
   * @return the {@link Token} instance at the front of the queue
   * @throws java.util.NoSuchElementException
   *           if the queue is empty
   */
  public Token front();

  /**
   * Removes and returns the token at the front of the queue.
   * 
   * @return the {@link Token} instance at the front of the queue
   * @throws java.util.NoSuchElementException
   *           if the queue is empty
   */
  public Token dequeue();

  /**
   * Returns the size of the queue.
   * 
   * @return a non-negative number
   */
  public int size();

}
