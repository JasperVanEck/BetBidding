/**
 * Defines an interface for stacks that contain {@link Ticket} instances.
 * 
 * @author Stephan Schroevers
 */
public interface TicketStack {

  /**
   * Tells if the stack is empty.
   * 
   * @return <code>true</code> if the stack is empty, <code>false</code>
   *         otherwise
   */
  public boolean isEmpty();

  /**
   * Adds the given token to the top of the stack.
   * 
   * @param elem
   *          the {@link Ticket} instance that must be added
   * @return the <code>Ticket</code> instance that was added
   */
  public Ticket push(Ticket elem);

  /**
   * Returns the token at the top of the stack.
   * 
   * @return the {@link Ticket} instance at the top of the stack
   * @throws java.util.NoSuchElementException
   *           if the stack is empty
   */
  public Ticket peek();

  /**
   * Removes and returns the token at the top of the stack.
   * 
   * @return the {@link Ticket} instance at the top of the stack
   * @throws java.util.NoSuchElementException
   *           if the stack is empty
   */
  public Ticket pop();

  /**
   * Returns the size of the stack.
   * 
   * @return a non-negative number
   */
  public int size();

}
