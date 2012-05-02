public class Foo extends Thread
{
  public boolean b;
  // declaring this method as
  // public synchronized boolean read()
  // would fix the race condition
  public boolean read()
  {
    if (b == true)
    {
      b = false; // lazy init
    }else
    {
      b = true;
    }
    return b;
  }
  public void run()
  {
    try{
    sleep(5000);
    System.out.println(read()); // 'false' can be printed
    }catch(InterruptedException e)
    {
      System.out.println(e.getMessage());
    }
  }
  public static void main(String[] args)
  {
    Foo obj = new Foo();
    obj.start(); // spawn new thread in run()
    System.out.println(obj.read()); // 'false' can be printed
  }
}