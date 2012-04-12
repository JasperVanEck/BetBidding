/**
 * Defines an interface for queues that contain {@link Ticket} instances.
 * 
 * @author Stephan Schroevers
 */
public interface TicketQueue {

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
   *          the {@link Ticket} instance that must be added
   * @return the <code>Ticket</code> instance that was added
   */
  public Ticket enqueue(Ticket elem);

  /**
   * Returns the token at the front of the queue.
   * 
   * @return the {@link Ticket} instance at the front of the queue
   * @throws java.util.NoSuchElementException
   *           if the queue is empty
   */
  public Ticket front();

  /**
   * Removes and returns the token at the front of the queue.
   * 
   * @return the {@link Ticket} instance at the front of the queue
   * @throws java.util.NoSuchElementException
   *           if the queue is empty
   */
  public Ticket dequeue();

  /**
   * Returns the size of the queue.
   * 
   * @return a non-negative number
   */
  public int size();

}
