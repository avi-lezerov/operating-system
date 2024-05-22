import java.io.File;

/**
 * The Scouter class is responsible for starting the scouter thread. It lists directories under the root directory
 * and adds them to a queue. It then lists directories in the next level and enqueues them, and so on. This method
 * begins by registering to the directory queue as a producer and when it finishes, it unregisters from it.
 */
public class Scouter implements Runnable {

    private SynchronizedQueue<File> directoryQueue;
    private File root;

    /**
     * Initializes a new instance of the Scouter class.
     * 
     * @param root
     *            the root directory to start the search from
     * @param directoryQueue
     *            the directory queue to add the directories to
     */
    public Scouter(File root, SynchronizedQueue<File> directoryQueue) {
        this.root = root;
        this.directoryQueue = directoryQueue;
    }
    
    /**
     * The run method is the entry point for the scouter thread. It implements the logic for listing directories
     * and adding them to the queue.
     */
    public void run() {
        directoryQueue.registerProducer();
        enqueueDirectories(root);
        directoryQueue.unregisterProducer();
    }
    
    private void enqueueDirectories(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    directoryQueue.enqueue(file);
                    enqueueDirectories(file);
                }
            }
        }
    }
  
}
