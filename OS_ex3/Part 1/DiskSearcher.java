import java.io.File;

/**
 * Main application class. This application searches for all files under some
 * given path that contain a given textual pattern.
 * All files found are copied to some specific directory.
 */
public class DiskSearcher extends java.lang.Object {

    public static final int DIRECTORY_QUEUE_CAPACITY = 50;

    public static final int RESULTS_QUEUE_CAPACITY = 50;

    /**
     * Default constructor for DiskSearcher class
     */
    public DiskSearcher() {

    }

    /**
     * Main method. Reads arguments from command line and starts the search.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // if (!validateArgs(args)) {
        //     System.out.println("Usage: java DiskSearcher <filename-pattern> <file-extension> <root directory> <destination directory> <# of searchers> <# of copiers>");
        //     System.exit(1);
        // }
    
        // String pattern = args[0];
        // String extension = args[1];
        // File rootDirectory = new File(args[2]);
        // File destinationDirectory = new File(args[3]);
        // int numSearchers = Integer.parseInt(args[4]);
        // int numCopiers = Integer.parseInt(args[5]);

        String pattern = "test";
        String extension = "txt";
        File rootDirectory = new File("C:\\Users\\avrah\\Documents\\GitHub\\operating-system\\OS_ex3");
        File destinationDirectory = new File("C:\\Users\\avrah\\Documents\\GitHub\\operating-system\\OS_ex3\\Part 1\\test");
        int numSearchers = 1;
        int numCopiers = 1;
    
    
        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<>(RESULTS_QUEUE_CAPACITY);
    
        Thread scouter = new Thread(new Scouter(rootDirectory, directoryQueue));
        scouter.start();
    
        Thread[] searchers = new Thread[numSearchers];
        for (int i = 0; i < numSearchers; i++) {
            searchers[i] = new Thread(new Searcher(pattern, extension, directoryQueue, resultsQueue));
            searchers[i].start();
        }
    
        Thread[] copiers = new Thread[numCopiers];
        for (int i = 0; i < numCopiers; i++) {
            copiers[i] = new Thread(new Copier(destinationDirectory, resultsQueue));
            copiers[i].start();
        }
    
        try {
            scouter.join();
            for (Thread searcher : searchers) {
                searcher.join();
            }
            for (Thread copier : copiers) {
                copier.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static boolean validateArgs(String[] args) {
        if (args.length != 6 || !new File(args[2]).isDirectory() || !new File(args[3]).isDirectory())
            return false;

        try {
            if (Integer.parseInt(args[4]) <= 0 || Integer.parseInt(args[5]) <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
