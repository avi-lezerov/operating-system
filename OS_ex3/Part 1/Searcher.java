import java.io.File;
import java.io.FilenameFilter;

/**
 * A searcher thread. Searches for files containing a given pattern and that end
 * with a specific extension
 * in all directories listed in a directory queue.
 */
public class Searcher extends java.lang.Object implements java.lang.Runnable {
    private final String pattern;
    private final String extension;
    private final SynchronizedQueue<File> directoryQueue;
    private final SynchronizedQueue<File> resultsQueue;

    /**
     * Constructor. Initializes the searcher thread.
     * 
     * @param pattern        Pattern to look for
     * @param extension      Wanted extension
     * @param directoryQueue A queue with directories to search in (as listed by the
     *                       scouter)
     * @param resultsQueue   A queue for files found (to be copied by a copier)
     */
    public Searcher(String pattern, String extension, SynchronizedQueue<File> directoryQueue,
            SynchronizedQueue<File> resultsQueue) {
        this.pattern = pattern;
        this.extension = extension;
        this.directoryQueue = directoryQueue;
        this.resultsQueue = resultsQueue;
    }

    /**
     * Runs the searcher thread. Thread will fetch a directory to search in from the
     * directory queue,
     * then search all files inside it (but will not recursively search
     * subdirectories!).
     * Files that contain the pattern and have the wanted extension are enqueued to
     * the results queue.
     * This method begins by registering to the results queue as a producer and when
     * finishes, it unregisters from it.
     */
    @Override
    public void run() {
        try {
            resultsQueue.registerProducer();
            File directory;
            while ((directory = directoryQueue.dequeue()) != null) {
                searchFiles(directory);
            }
        } finally {
            resultsQueue.unregisterProducer();
        }
    }

    private void searchFiles(File directory) {
        File[] files = directory.listFiles(File::isFile);
       
        if (files != null) {
            for (File file : files) {
                if (isFileMatch(file)) 
                    resultsQueue.enqueue(file);
            }
        }
    }

    private boolean isFileMatch(File file) {
        String name = file.getName();
        String baseName = name.substring(0, name.length() - extension.length());
        return  baseName.contains(pattern) && name.endsWith(extension);
    }
}