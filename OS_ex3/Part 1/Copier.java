import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
        File file;
        while ((file = resultsQueue.dequeue()) != null) {
            try {
                copyFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Copies a file to a given destination.
     * 
     * @param file        The file to copy
     * @param destination The destination directory
     * @throws Exception If an error occurs during the copy process
     */
    private void copyFile(File file) throws Exception {
        File destFile = fileDestination(file);
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(destFile);

        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        int read;
        while ((read = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        fis.close();
    }

    private File fileDestination(File file) throws Exception {
        String name = file.getName().substring(0, file.getName().lastIndexOf('.')) ;
        String format = file.getName().substring(file.getName().lastIndexOf('.'));
        
        File destFile = new File(destination, name + format);
        for (int i = 1; i < 100 && !destFile.createNewFile(); i++) {
            name = name + "(" + i + ")";
            destFile = new File(destination, name + format);

        }
        return destFile;
    }

}
