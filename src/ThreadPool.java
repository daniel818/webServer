import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class ThreadPool implements Executor {

	List<Thread> threadPool;

	SyncQueue executionQ;

	public  ThreadPool(int poolSize) {

		//Initilise thread pool
		threadPool = new ArrayList<Thread>(poolSize);
		executionQ = new SyncQueue(poolSize);

		for (int i = 0; i < poolSize; i++) {
			Thread t  = new Thread(new Runnable() {
				public void run() {
					
					while (true){
						
						Runnable r;
						
						try {
							r = executionQ.pull();
							
							if(r != null){
								r.run();
							}
						} catch (Exception  e) {
							System.out.println("Could not handle request, message: " + e.getMessage());
						}	
					}
				}
			});
			t.start();
			threadPool.add(t);
		}
	}


	@Override
	public void execute(Runnable command) {
		try {
			executionQ.push(command);
		} catch (Exception e) {
			System.out.println("Could not push to queue, message: " + e.getMessage());
		}
	}

}
