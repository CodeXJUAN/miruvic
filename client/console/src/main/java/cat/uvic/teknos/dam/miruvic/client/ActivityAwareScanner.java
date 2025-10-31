package cat.uvic.teknos.dam.miruvic.client;

import java.util.Scanner;

public class ActivityAwareScanner {
    private final Scanner scanner;
    private final Runnable updateActivity;

    public ActivityAwareScanner(Scanner scanner, Runnable updateActivity) {
        this.scanner = scanner;
        this.updateActivity = updateActivity;
    }

    public String nextLine() {
        updateActivity.run();
        return scanner.nextLine();
    }

    public int nextInt() {
        updateActivity.run();
        return scanner.nextInt();
    }

    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    public boolean hasNext() {
        return scanner.hasNext();
    }
}