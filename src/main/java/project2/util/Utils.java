package project2.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Utils {

    private Utils() {
    }

    public static <T> List<T> removeDuplicatesWithOrder(final List<T> list) {
        final Set<T> set = new HashSet<T>();
        final List<T> newList = new ArrayList<T>();

        for (final T element : list) {
            if (set.add(element)) {
                newList.add(element);
            }
        }

        return newList;
    }
}
