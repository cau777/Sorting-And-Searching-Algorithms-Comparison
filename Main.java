package phonebook;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static PhoneInfo[] directory = new PhoneInfo[0];
    private static String[] find = new String[0];
    private static long timeLimit;

    public static void main(String[] args) {
        prepare();
        runLinearSearch();
        runJumpSearch();
        runBinarySearch();
        runHashtableSearch();
    }

    private static void runLinearSearch() {
        long start = System.currentTimeMillis();
        System.out.println("Start searching (linear search)...");

        int found = 0;
        for (String s : find) {
            int index = SearchingAlgorithms.linearSearch(directory, s);

            found += ~(index >> 31) & 1;
        }

        long end = System.currentTimeMillis();
        Duration elapsedTime = Duration.ofMillis(end - start);
        System.out.printf("Found %d / %d entries. %s%n", found, find.length, formatTime(elapsedTime));
        System.out.println();

        // The linear sorting time is used as a time limit for the bubble sort algorithm
        timeLimit = (end - start) * 10;
    }

    private static void runJumpSearch() {
        System.out.println("Start searching (bubble sort + jump search)...");

        AlgorithmRunnable sortingOperation = new AlgorithmRunnable() {
            long timeTaken;
            boolean finished;

            @Override
            public boolean isFinished() {
                return finished;
            }

            @Override
            public long getTimeTaken() {
                return timeTaken;
            }

            @Override
            public void run() {
                long start = System.currentTimeMillis();

                try {
                    SortingAlgorithms.bubbleSort(directory);
                } catch (InterruptedException ignored) {
                } finally {
                    timeTaken = System.currentTimeMillis() - start;
                }
            }
        };

        Thread thread = new Thread(sortingOperation);
        // Create a new Thread to stop sorting if it takes too long
        new Thread(() -> {
            try {
                Thread.sleep(timeLimit);
            } catch (InterruptedException ignored) {
            }
            if (!thread.isInterrupted()) thread.interrupt();
        }).start();
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }

        int found = 0;
        long searchStart = System.currentTimeMillis();
        if (sortingOperation.isFinished()) {
            // If finished, run jump search
            for (String toFind : find) {
                int index = SearchingAlgorithms.jumpSearch(directory, toFind);

                // Adds 1 if the index is not -1 (found)
                found += ~(index >> 31) & 1;
            }
        } else {
            // Else run linear search
            for (String toFind : find) {
                int index = SearchingAlgorithms.linearSearch(directory, toFind);

                found += ~(index >> 31) & 1;
            }
        }


        long searchTimeTaken = System.currentTimeMillis() - searchStart;
        Duration sortingTime = Duration.ofMillis(sortingOperation.getTimeTaken());
        Duration searchingTime = Duration.ofMillis(searchTimeTaken);
        Duration elapsedTime = Duration.ofMillis(searchTimeTaken + sortingOperation.getTimeTaken());

        System.out.printf("Found %d / %d entries. %s%n", found, find.length, formatTime(elapsedTime));
        System.out.println("Sorting time: " + formatTime(sortingTime) + (sortingOperation.isFinished() ? "" : " - STOPPED, moved to linear search"));
        System.out.println("Searching time: " + formatTime(searchingTime));
        System.out.println();
    }

    // Simple interface to run bubble sort in a separate thread
    private interface AlgorithmRunnable extends Runnable {
        boolean isFinished();

        long getTimeTaken();
    }

    private static void runBinarySearch() {
        System.out.println("Start searching (quick sort + binary search)...");

        long sortingStart = System.currentTimeMillis();
        SortingAlgorithms.quickSort(directory);
        long sortingTimeTaken = System.currentTimeMillis() - sortingStart;

        int found = 0;
        long searchStart = System.currentTimeMillis();
        for (String toFind : find) {
            int index = SearchingAlgorithms.binarySearch(directory, toFind);

            // Adds 1 if the index is not -1 (found)
            found += ~(index >> 31) & 1;
        }

        long searchingTimeTaken = System.currentTimeMillis() - searchStart;
        Duration sortingTime = Duration.ofMillis(sortingTimeTaken);
        Duration searchingTime = Duration.ofMillis(searchingTimeTaken);
        Duration elapsedTime = Duration.ofMillis(searchingTimeTaken + sortingTimeTaken);

        System.out.printf("Found %d / %d entries. %s%n", found, find.length, formatTime(elapsedTime));
        System.out.println("Sorting time: " + formatTime(sortingTime));
        System.out.println("Searching time: " + formatTime(searchingTime));
        System.out.println();
    }

    private static void runHashtableSearch() {
        System.out.println("Start searching (hash table)...");

        long creatingStart = System.currentTimeMillis();
        Hashtable<String, Integer> hashtable = new Hashtable<>(directory.length);

        for (PhoneInfo info : directory) {
            hashtable.put(info.owner, info.phoneNumber);
        }

        long creatingTimeTaken = System.currentTimeMillis() - creatingStart;
        long searchingStart = System.currentTimeMillis();

        int found = 0;
        for (String toFind : find) {
            if (hashtable.get(toFind) != null) found++;
        }

        long searchTimeTaken = System.currentTimeMillis() - searchingStart;

        Duration creatingTime = Duration.ofMillis(creatingTimeTaken);
        Duration searchingTime = Duration.ofMillis(searchTimeTaken);
        Duration elapsedTime = Duration.ofMillis(creatingTimeTaken + searchTimeTaken);

        System.out.printf("Found %d / %d entries. %s%n", found, find.length, formatTime(elapsedTime));
        System.out.println("Creating time: " + formatTime(creatingTime));
        System.out.println("Searching time: " + formatTime(searchingTime));
    }

    private static String formatTime(Duration elapsedTime) {
        return String.format("Time taken: %d min. %d sec. %d ms.", elapsedTime.toMinutesPart(), elapsedTime.toSecondsPart(),
                elapsedTime.toMillisPart());
    }

    private static void prepare() {
        try {
            directory = readDirectory("files/directory.txt");
            find = readFind("files/find.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static PhoneInfo[] readDirectory(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        List<PhoneInfo> lines = new ArrayList<>();

        while (scanner.hasNextLine()) {
            int phoneNumber = scanner.nextInt();
            String owner = scanner.nextLine().strip();
            lines.add(new PhoneInfo(phoneNumber, owner));
        }

        return lines.toArray(new PhoneInfo[0]);
    }

    private static String[] readFind(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        List<String> lines = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lines.add(line);
        }

        return lines.toArray(new String[0]);
    }
}
