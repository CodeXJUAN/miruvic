package cat.uvic.teknos.dam.miruvic.server;

import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import cat.uvic.teknos.dam.miruvic.server.utils.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private final int port;
    private final RequestRouter router;
    private final ExecutorService executorService;
    private final ScheduledExecutorService monitorService;
    private final ConcurrentHashMap<String, ClientHandler> activeClients;
    private final Lock clientLock;
    private volatile boolean running;

    public Server(int port, RequestRouter router) {
        this.port = port;
        this.router = router;
        this.executorService = Executors.newFixedThreadPool(10);
        this.monitorService = Executors.newScheduledThreadPool(1);
        this.activeClients = new ConcurrentHashMap<>();
        this.clientLock = new ReentrantLock();
        this.running = false;
    }

    public void start() {
        running = true;

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     MIRUVIC SERVER STARTING...        ║");
        System.out.println("╚════════════════════════════════════════╝");

        startClientMonitorDaemon();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("✓ Server listening on port " + port);
            System.out.println("✓ Waiting for client connections...");
            System.out.println("✓ Press Ctrl+C to stop the server\n");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("→ New client connected: " +
                            clientSocket.getInetAddress().getHostAddress() +
                            ":" + clientSocket.getPort());
                    executorService.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("✗ Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("✗ FATAL: Could not start server on port " + port);
            System.err.println("  Reason: " + e.getMessage());
            System.err.println("  Tip: Make sure the port is not already in use");
        } finally {
            shutdown();
        }
    }

    private void startClientMonitorDaemon() {
        monitorService.scheduleAtFixedRate(() -> {
            clientLock.lock();
            try {
                System.out.println("📊 Connected clients: " + activeClients.size());
            } finally {
                clientLock.unlock();
            }
        }, 1, 1, TimeUnit.MINUTES);
        System.out.println("✓ Client monitor daemon started.");
    }

    private void handleClient(Socket clientSocket) {
        String clientId = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        ClientHandler handler = new ClientHandler(clientSocket, router, this);

        clientLock.lock();
        try {
            activeClients.put(clientId, handler);
        } finally {
            clientLock.unlock();
        }

        try {
            handler.handle();
        } finally {
            removeClient(clientId);
            System.out.println("  ✓ [" + clientId + "] Connection closed\n");
        }
    }

    public void removeClient(String clientId) {
        clientLock.lock();
        try {
            ClientHandler handler = activeClients.remove(clientId);
            if (handler != null) {
                System.out.println("✓ Client " + clientId + " removed from active clients");
            }
        } finally {
            clientLock.unlock();
        }
    }

    public void shutdown() {
        running = false;
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     SHUTTING DOWN SERVER...           ║");
        System.out.println("╚════════════════════════════════════════╝");

        monitorService.shutdown();
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!monitorService.awaitTermination(60, TimeUnit.SECONDS)) {
                monitorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            monitorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("✓ Server shutdown complete");
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return running;
    }
}