package phonebook;

public class SearchingAlgorithms {
    public static int linearSearch(PhoneInfo[] array, String ownerValue) {
        for (int i = 0; i < array.length; i++) {
            String element = array[i].owner;
            if (element.equals(ownerValue)) {
                return i;
            }
        }
        return -1;
    }

    public static int backwardLinearSearch(PhoneInfo[] array, int startIndex, int length, String ownerValue) {
        for (int i = startIndex; i > startIndex - length; i--) {
            if (array[i].owner.equals(ownerValue)) {
                return i;
            }
        }
        return -1;
    }

    public static int jumpSearch(PhoneInfo[] array, String ownerValue) {
        if (array.length == 0) return -1;
        if (array[0].owner.equals(ownerValue)) return 0;

        int jumpLength = (int) Math.sqrt(array.length);

        for (int right = jumpLength; right < array.length; right += jumpLength) {
            if (array[right].owner.compareTo(ownerValue) >= 0) {
                return backwardLinearSearch(array, right, jumpLength, ownerValue);
            }
        }

        if (array[array.length - 1].owner.compareTo(ownerValue) >= 0) {
            int rest = (array.length - 1) % jumpLength;
            return backwardLinearSearch(array, array.length - 1, rest, ownerValue);
        }

        return -1;
    }

    public static int binarySearch(PhoneInfo[] directory, String ownerValue) {
        int start = 0;
        int end = directory.length;

        while (true) {
            int length = end - start;
            if (length <= 3) {
                for (int i = start; i < end; i++) {
                    if (ownerValue.equals(directory[i].owner)) return i;
                }
                return -1;
            }

            int middle = (end + start - 1) >>> 1;
            int comparison = ownerValue.compareTo(directory[middle].owner);

            if (comparison == 0) return middle;

            if (comparison > 0) start = middle + 1;
            else end = middle;
        }
    }
}
