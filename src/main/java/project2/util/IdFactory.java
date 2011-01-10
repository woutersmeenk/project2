package project2.util;

public final class IdFactory {
    private static long currentID = 0;

    private IdFactory() {

    }

    public static long generateID() {
        currentID++;
        return currentID;
    }

}
