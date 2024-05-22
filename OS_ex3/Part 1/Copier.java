import java.io.File;
import java.io.IOException;

/**
 * A copier thread. Reads files to copy from a queue and copies them to the
 * given destination.
 */
public class Copier implements Runnable {

    public static final int COPY_BUFFER_SIZE = 4096;

    private final SynchronizedQueue<File> resultsQueue;
    private final File destination;

    /**
     * Constructor. Initializes the worker with a destination directory and a queue
     * of files to copy.
     * 
     * @param destination  The destination directory
     * @param resultsQueue The queue of files found, to be copied
     */
    public Copier(File destination, SynchronizedQueue<File> resultsQueue) {
        this.resultsQueue = resultsQueue;
        this.destination = destination;
    }

    /**
     * Runs the copier thread. The thread will fetch files from the queue and copy
     * them, one after each other, to the destination directory.
     * When the queue has no more files, the thread finishes.
     */
    @Override
    public void run() {
        resultsQueue.registerProducer();
        File file;
        while ((file = resultsQueue.dequeue()) != null) {
            try {
                copyFile(file, destination);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resultsQueue.unregisterProducer();
    }

    /**
     * Copies a file to a given destination.
     * 
     * @param file        The file to copy
     * @param destination The destination directory
     * @throws Exception If an error occurs during the copy process
     */
    public void copyFile(File file, File destination) throws Exception {
        // Check if the destination directory exists, create it if necessary
        if (!destination.exists()) {
            boolean created = destination.mkdirs();
            if (!created) {
                throw new IOException("Failed to create destination directory: " + destination);
            }
        }

        File destFile = new File(destination, file.getName());
        java.io.FileInputStream fis = new java.io.FileInputStream(file);
        java.io.FileOutputStream fos = new java.io.FileOutputStream(destFile);
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        int read;
        while ((read = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fis.close();
        fos.close();
    }
}
