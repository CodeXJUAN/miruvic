package cat.uvic.teknos.dam.miruvic.client;

import java.util.Scanner;

/**
 * Wrapper del Scanner que actualiza la actividad del cliente cada vez que se lee input.
 */
public class ActivityAwareScanner {
    private final Scanner scanner;
    private final Runnable updateActivity;

    public ActivityAwareScanner(Scanner scanner, Runnable updateActivity) {
        this.scanner = scanner;
        this.updateActivity = updateActivity;
    }

    public String nextLine() {
        updateActivity.run(); // Actualizar actividad ANTES de leer
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