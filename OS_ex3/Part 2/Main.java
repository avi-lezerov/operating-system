import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        List<String> lines = getLinesFromFile();
        System.out.println("Number of lines found: " + lines.size());
        System.out.println("Starting to process");

        long startTimeWithoutThreads = System.currentTimeMillis();
        workWithoutThreads(lines);
        long elapsedTimeWithoutThreads = (System.currentTimeMillis() - startTimeWithoutThreads);
        System.out.println("Execution time: " + elapsedTimeWithoutThreads);


        long startTimeWithThreads = System.currentTimeMillis();
        workWithThreads(lines);
        long elapsedTimeWithThreads = (System.currentTimeMillis() - startTimeWithThreads);
        System.out.println("Execution time: " + elapsedTimeWithThreads);

    }

    private static void workWithThreads(List<String> lines) {
        int x = Runtime.getRuntime().availableProcessors();
        List<List<String>> partitions = getLinesLiseSpit(x, lines);
        

        ExecutorService executor = Executors.newFixedThreadPool(x);
        for (List<String> partition : partitions) {
            executor.submit(new Worker(partition));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void workWithoutThreads(List<String> lines) {
        Worker worker = new Worker(lines);
        worker.run();
    }

    private static List<String> getLinesFromFile() {
        try {
            return Files.readAllLines(Paths.get("C:\\Temp\\Shakespeare.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<List<String>> getLinesLiseSpit(int x, List<String> lines){
        List<List<String>> partitions = new ArrayList<>(x);
        int partitionSize = lines.size() / x;
        for (int i = 0; i < x; i++) {
            int startIndex = i * partitionSize;
            int endIndex = (i == x - 1) ? lines.size() : (i + 1) * partitionSize;
            partitions.add(lines.subList(startIndex, endIndex));
        }
        return partitions;
    }
}
