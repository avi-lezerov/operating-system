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
        if (args.length != 6) {
            System.out.println(
                    "Usage: java DiskSearcher <filename-pattern> <file-extension> <root directory> <destination directory> <# of searchers> <# of copiers>");
            System.exit(1);
        }
        Boolean validArgs[] = validateArgs(args);
        if (!validArgs[0]) {
            printError(validArgs);
            System.exit(1);
        }

        String pattern = args[0];
        String extension = args[1];
        File rootDirectory = new File(args[2]);
        File destinationDirectory = new File(args[3]);
        int numSearchers = Integer.parseInt(args[4]);
        int numCopiers = Integer.parseInt(args[5]);

        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<>(RESULTS_QUEUE_CAPACITY);

        Thread scouter = new Thread(new Scouter(directoryQueue, rootDirectory));
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

    /**
     * Validates the arguments passed to the program.
     * 
     * @param args the arguments passed to the program
     * @return an array of booleans indicating the validity of the arguments
     */
    public static Boolean[] validateArgs(String[] args) {
        Boolean validArgs[] = new Boolean[7];
        validArgs[1] = args.length == 6;
        validArgs[0] = true;
        try {
            File destination = new File(args[3]);
            File root = new File(args[2]);
            validArgs[2] = root.exists() && root.isDirectory();
            validArgs[3] = ((!destination.exists() && destination.mkdir()) || destination.isDirectory());
        } catch (NumberFormatException e) {
            validArgs[0] = false;
        }

        try {
            validArgs[4] = Integer.parseInt(args[4]) > 0;
        } catch (NumberFormatException e) {
            validArgs[4] = false;
            validArgs[0] = false;
        }

        try {
            validArgs[5] = Integer.parseInt(args[5]) > 0;
        } catch (NumberFormatException e) {
            validArgs[5] = false;
            validArgs[0] = false;
        }
        return validArgs;
    }

    /**
     * Prints error messages based on the validity of the arguments passed to the
     * program.
     * 
     * @param validArgs an array of booleans indicating the validity of the
     *                  arguments
     */
    public static void printError(Boolean[] validArgs) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "Usage: java DiskSearcher <filename-pattern> <file-extension> <root directory> <destination directory> <# of searchers> <# of copiers>\n");
        if (!validArgs[1]) {
            sb.append("Invalid number of arguments\n");
        }
        if (!validArgs[2]) {
            sb.append("Root directory does not exist\n");
        }
        if (!validArgs[3]) {
            sb.append("Destination directory does not exist\n");
        }
        if (!validArgs[4]) {
            sb.append("Number of searchers must be a positive integer\n");
        }
        if (!validArgs[5]) {
            sb.append("Number of copiers must be a positive integer\n");
        }
        System.err.println(sb.toString());
    }
}
