package phonebook;

public class SortingAlgorithms {

    public static void bubbleSort(PhoneInfo[] array) throws InterruptedException {


        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                if (array[j].owner.compareTo(array[j + 1].owner) > 0) {
                    swap(array, j, j + 1);
                }
            }
        }
    }

    public static void quickSort(PhoneInfo[] array) {quickSort(array, 0, array.length);}

    public static void quickSort(PhoneInfo[] array, int start, int end) {
        if (end - start <= 1) {
            return;
        }

        int partitionIndex = start;
        int pivotIndex = end - 1;
        String pivot = array[pivotIndex].owner;

        for (int i = start; i < end - 1; i++) {
            if (array[i].owner.compareTo(pivot) < 0) {
                swap(array, i, partitionIndex);
                partitionIndex++;
            }
        }

        swap(array, partitionIndex, pivotIndex);
        quickSort(array, start, partitionIndex);
        quickSort(array, partitionIndex + 1, end);
    }

    private static void swap(PhoneInfo[] array, int from, int to) {
        PhoneInfo temp = array[from];
        array[from] = array[to];
        array[to] = temp;
    }
}
