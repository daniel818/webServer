import java.util.LinkedList;
import java.util.Queue;


public class SyncQueue {

    private Queue<Runnable> runnablesQueue = new LinkedList<Runnable>();
    private int queueSize;

    //Construct a Queue with initilised size.
    public SyncQueue(int queueSize) {
        this.queueSize = queueSize;
    }
    
    //Checks if Queue is full.
    public boolean queueIsFull(){
    	
    	if (this.runnablesQueue.size() == this.queueSize){
    		return true;
    	}
		return false;	
    }
    
    //Push Runnable to the Queue
	public synchronized void push(Runnable run) throws InterruptedException {
       
		while (queueIsFull()) {
            wait();
        }

        if (this.runnablesQueue.isEmpty()) {
            notifyAll();
        }

        this.runnablesQueue.add(run);
    }
    
	//Retrieve a Runnable from the Queue.
    public synchronized Runnable pull() throws InterruptedException  {

    	Runnable run;
        
    	while (runnablesQueue.isEmpty()) {
            wait();
        }

        if (queueIsFull()) {
            notifyAll();
        }

        run = (Runnable) runnablesQueue.remove();
        return run;
    }
}